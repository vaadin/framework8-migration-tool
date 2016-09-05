package com.vaadin.framework8.migrate;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.aether.artifact.Artifact;

public class VadinJarFinder {

    public static String get(String moduleName, String version)
            throws IOException {
        String m2 = System.getenv("M2_HOME");
        if (m2 == null) {
            m2 = System.getenv("HOME") + "/.m2";
        }

        final String m2repositoryFolder = m2 + "/repository";

        return getFromLocalMaven(m2repositoryFolder, moduleName, version)
                .orElseGet(() -> download(m2repositoryFolder, moduleName,
                        version));
    }

    private static Optional<String> getFromLocalMaven(String m2repositoryFolder,
            String moduleName, String version) {
        File m2File = new File(
                m2repositoryFolder + "/" + getMavenPath(moduleName, version));
        if (m2File.exists()) {
            System.out.println("Using " + moduleName + " " + version
                    + " from .m2 cache (" + m2File.getAbsolutePath() + ")");
            return Optional.of(m2File.getAbsolutePath());
        } else {
            return Optional.empty();
        }

    }

    private static String download(String m2repositoryFolder, String moduleName,
            String version) {
        String filenameWithVersion = moduleName + "-" + version + ".jar";

        File target = new File(m2repositoryFolder + "/" + filenameWithVersion);
        if (target.exists()) {
            System.out.println("Using " + target.getAbsolutePath());
            return target.getAbsolutePath();
        }

        System.out.println("Downloading " + moduleName + " " + version
                + " from Maven to " + m2repositoryFolder);
        // The file will automatically go into the correct Maven folder
        // hierarchy
        MavenResolver resolver = new MavenResolver(m2repositoryFolder);
        Stream<Artifact> resolved = resolver.resolve("com.vaadin", moduleName,
                version);
        Optional<Artifact> first = resolved.findFirst();
        if (!first.isPresent() || !first.get().getFile().exists()) {
            throw new RuntimeException("Unable to download " + moduleName + " "
                    + version + " from Maven");
        }
        String path = first.get().getFile().getAbsolutePath();
        System.out.println("Using " + path);
        return path;
    }

    private static String getMavenPath(String moduleName, String version) {
        String filenameWithVersion = moduleName + "-" + version + ".jar";
        return "com/vaadin/" + moduleName + "/" + version + "/"
                + filenameWithVersion;
    }

}
