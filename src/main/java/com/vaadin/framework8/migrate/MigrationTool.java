package com.vaadin.framework8.migrate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author mavi
 */
public class MigrationTool {
    private HashSet<String> serverV7Classes;
    private Set<String> sharedV7Classes;
    private Set<String> serverV7UIClasses;
    private Set<String> clientV7Classes;
    private Map<String, String> specialRenames;
    private final String vaadin8Version;
    private final File projectRoot;

    public MigrationTool(String vaadin8Version, File projectRoot) {
        this.vaadin8Version = vaadin8Version;
        this.projectRoot = projectRoot;
    }

    public void migrate() throws Exception {
        String version = vaadin8Version;
        System.out.println("Scanning for compatibility classes for " + version
                + " version...");
        String compatServerFilename = VadinJarFinder
                .get("vaadin-compatibility-server", version);
        String compatSharedFilename = VadinJarFinder
                .get("vaadin-compatibility-shared", version);
        String compatClientFilename = VadinJarFinder
                .get("vaadin-compatibility-client", version);

        serverV7Classes = new HashSet<>();
        sharedV7Classes = new HashSet<>();
        clientV7Classes = new HashSet<>();

        findV7Classes(compatServerFilename, serverV7Classes);
        findV7Classes(compatSharedFilename, sharedV7Classes);
        findV7Classes(compatClientFilename, clientV7Classes);

        // This is used in interface and will break more than it fixes
        clientV7Classes.remove("com.vaadin.v7.client.ComponentConnector");

        serverV7UIClasses = serverV7Classes.stream().filter(
                cls -> cls.matches("^com\\.vaadin\\.v7\\.ui\\.[^\\.]*$"))
                .collect(Collectors.toSet());

        System.out.println("Found " + serverV7Classes.size() + "+"
                + sharedV7Classes.size() + " classes, including "
                + serverV7UIClasses.size() + " UI classes");

        specialRenames = new HashMap<>();
        specialRenames.put("com.vaadin.data.fieldgroup.PropertyId",
                "com.vaadin.annotations.PropertyId");
        specialRenames.put("com.vaadin.shared.ui.grid.Range",
                "com.vaadin.shared.Range");

        AtomicInteger javaCount = new AtomicInteger(0);
        AtomicInteger htmlCount = new AtomicInteger(0);
        migrateFiles(projectRoot, javaCount, htmlCount, version);

        System.out.println("Scanned " + javaCount.get() + " Java files");
        System.out.println("Scanned " + htmlCount.get() + " HTML files");
        System.out.println("Migration complete");
    }

    private static void findV7Classes(String jarFilename, Set<String> target)
            throws ZipException, IOException {
        File serverFile = new File(jarFilename);
        try (ZipFile jar = new ZipFile(serverFile)) {
            Enumeration<? extends ZipEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }

                getVaadin7Class(entry).ifPresent(target::add);
            }
        }

    }

    private void migrateFiles(File directory, AtomicInteger javaCount,
                                     AtomicInteger htmlCount, String version) throws IOException {
        assert directory.isDirectory();

        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                migrateFiles(f, javaCount, htmlCount, version);
            } else if (isJavaFile(f)) {
                javaCount.incrementAndGet();
                migrateJava(f);
            } else if (isDeclarativeFile(f)) {
                htmlCount.incrementAndGet();
                migrateDeclarative(f, version);
            }
        }
    }

    private static boolean isJavaFile(File f) {
        return f.getName().endsWith(".java");
    }

    private static boolean isDeclarativeFile(File f) {
        return f.getName().endsWith(".html");
    }

    private static Optional<String> getVaadin7Class(ZipEntry entry) {
        String name = entry.getName();
        if (name.startsWith("com/vaadin/v7") && name.endsWith(".class")) {
            name = name.replace('/', '.');
            name = name.replace('$', '.');
            name = name.replace(".class", "");
            return Optional.of(name);
        }

        return Optional.empty();
    }

    private void migrateJava(File f) throws IOException {
        String javaFile = IOUtils.toString(f.toURI(), StandardCharsets.UTF_8);
        String migratedFile = modifyJava(javaFile);
        if (!javaFile.equals(migratedFile)) {
            FileUtils.write(f, migratedFile, StandardCharsets.UTF_8);
        }
    }

    private void migrateDeclarative(File f, String version)
            throws IOException {
        String htmlFile = IOUtils.toString(f.toURI(), StandardCharsets.UTF_8);
        IOUtils.write(modifyDeclarative(htmlFile, version),
                new FileOutputStream(f));
    }

    private String modifyJava(String javaFile) {
        for (String v7Class : Stream
                .concat(Stream.concat(serverV7Classes.stream(),
                        sharedV7Classes.stream()), clientV7Classes.stream())
                .collect(Collectors.toList())) {

            String comvaadinClass = v7Class.replace("com.vaadin.v7.",
                    "com.vaadin.");
            javaFile = performReplacement(javaFile, comvaadinClass, v7Class);
        }

        for (Map.Entry<String, String> rename : specialRenames.entrySet()) {
            javaFile = performReplacement(javaFile, rename.getKey(),
                    rename.getValue());
        }

        return javaFile;
    }

    private static String performReplacement(String javaFile,
                                             String comvaadinClass, String v7Class) {
        javaFile = javaFile.replace("import " + comvaadinClass + ";",
                "import " + v7Class + ";");
        javaFile = javaFile.replace("extends " + comvaadinClass + " ",
                "extends " + v7Class + " ");
        javaFile = javaFile.replace("implements " + comvaadinClass + " ",
                "implements " + v7Class + " ");
        javaFile = javaFile.replace("throws " + comvaadinClass + " ",
                "throws " + v7Class + " ");
        return javaFile;
    }

    private String modifyDeclarative(String htmlFile, String version) {
        for (String v7Class : serverV7UIClasses) {
            String simpleClassName = v7Class
                    .substring(v7Class.lastIndexOf('.') + 1);
            String tagName = classNameToElementName(simpleClassName);

            String legacyStartTag = "<v-" + tagName + ">";
            String legacyStartTag2 = "<v-" + tagName + " ";
            String startTag = "<vaadin-" + tagName + ">";
            String startTag2 = "<vaadin-" + tagName + " ";
            String newStartTag = "<vaadin7-" + tagName + ">";
            String newStartTag2 = "<vaadin7-" + tagName + " ";
            String legacyEndTag = "</v-" + tagName + ">";
            String endTag = "</vaadin-" + tagName + ">";
            String newEndTag = "</vaadin7-" + tagName + ">";

            htmlFile = htmlFile.replace(legacyStartTag, newStartTag);
            htmlFile = htmlFile.replace(startTag, newStartTag);
            htmlFile = htmlFile.replace(legacyStartTag2, newStartTag2);
            htmlFile = htmlFile.replace(startTag2, newStartTag2);

            htmlFile = htmlFile.replace(legacyEndTag, newEndTag);
            htmlFile = htmlFile.replace(endTag, newEndTag);

            // Version
            htmlFile = htmlFile.replaceAll(
                    "<meta(.*)name=\"vaadin-version\"(.*)content=\"7.*\"(.*)>",
                    "<meta name=\"vaadin-version\" content=\"" + version
                            + "\">");
        }

        return htmlFile;
    }

    /**
     * From Design.java
     */
    private static String classNameToElementName(String className) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("-");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}
