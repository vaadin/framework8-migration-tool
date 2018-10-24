package com.vaadin.framework8.migrate;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * References a test project with sources placed in a temp directory. The structure is exactly as the
 * <code>test-projects/random-files</code> project co-located in the migration tool sources, but
 * the project is copied to a temp directory so you can tamper with the sources easily.
 * @author mavi
 */
public class TestProject implements Closeable {
    /**
     * A temporary directory. The directory contains the {@code pom.xml} file and the {@code src/} folder. This is the identical copy
     * of the sample project located in the {@code test-projects/random-files/} folder.
     */
    public final File dir;

    public final File pomXml;

    private TestProject(File dir) {
        this.dir = Objects.requireNonNull(dir);
        assertTrue(dir.exists(), dir + " doesn't exist");
        assertTrue(dir.isDirectory(), dir + " isn't a directory");
        pomXml = new File(dir, "pom.xml");
    }

    /**
     * Copies the test project to a temporary directory, and makes sure all files access time is of yesterday, so that all modifications
     * will be clearly identifiable.
     * @return the test project, not null.
     * @throws IOException
     */
    public static TestProject fromTemplate() throws IOException {
        final File dir = new File("test-projects/random-files").getAbsoluteFile();
        assertTrue(dir.exists(), dir + " doesn't exist");
        assertTrue(dir.isDirectory(), dir + " isn't a directory");
        final File tempFile = File.createTempFile("testproject", "dir");
        final File tempDir = new File(tempFile.getAbsolutePath() + "-dir");
        FileUtils.copyDirectory(dir, tempDir);
        Files.delete(tempFile.toPath());
        Files.walk(tempDir.toPath()).forEach(path -> {
            final File file = path.toFile();
            if (file.isFile()) {
                if (!file.setLastModified(System.currentTimeMillis() - 2 * ONE_DAY)) {
                    throw new RuntimeException("Failed to set lastModified of " + file + " to 2 days ago");
                }
            }
        });
        return new TestProject(tempDir);
    }

    /**
     * Creates a new empty test project. Use {@link #withFile(String, String, Charset)} and {@link #withJavaFile(String, String, Charset)} to populate
     * it with files.
     * @return the test project, not null.
     * @throws IOException
     */
    public static TestProject empty() throws IOException {
        final File tempFile = File.createTempFile("testproject", "dir");
        final File tempDir = new File(tempFile.getAbsolutePath() + "-dir");
        Files.createDirectories(tempDir.toPath());
        Files.delete(tempFile.toPath());
        return new TestProject(tempDir);
    }

    public TestFile getFile(String name) {
        return getFile(name, Charsets.UTF_8);
    }

    public TestFile getFile(String name, Charset charset) {
        return new TestFile(new File(dir, name), charset);
    }

    /**
     * Returns the java file located in {@code src/main/java/com/vaadin/random/files}. UTF-8.
     * @param name the java file name, not null. The file must exist.
     * @return the test file reference.
     */
    public TestJavaFile getJavaFile(String name) {
        return getJavaFile(name, Charsets.UTF_8);
    }

    /**
     * Returns the java file located in {@code src/main/java/com/vaadin/random/files}.
     * @param name the java file name, not null. The file must exist.
     * @return the test file reference.
     */
    public TestJavaFile getJavaFile(String name, Charset charset) {
        return getFile("src/main/java/com/vaadin/random/files/" + name, charset).java();
    }

    /**
     * Returns the Vaadin Designer html template located in {@code src/main/resources/com/vaadin/random/files}. Uses UTF_8 charset.
     * @param name the template file name, not null. The template file must exist.
     * @return the test file reference.
     */
    public TestFile getTemplate(String name) {
        return getFile("src/main/resources/com/vaadin/random/files/" + name, Charsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteDirectory(dir);
    }

    /**
     * Runs the migration to Vaadin 8.5.2.
     * @throws Exception
     */
    public void migrate() throws Exception {
        migrate("8.5.2", Charsets.UTF_8);
    }

    /**
     * Runs the migration.
     * @throws Exception
     */
    public void migrate(String vaadinVersion, Charset charset) throws Exception {
        new MigrationTool(vaadinVersion, dir, charset).migrate();
    }

    static final long ONE_DAY = 1L * 24 * 60 * 60 * 1000;

    /**
     * Creates a new Java file in {@code src/main/java/com/vaadin/random/files/} inside of the project.
     * @param name the java file name, not null.
     * @param contents the contents, not null.
     * @param encoding the file encoding, not null.
     * @throws IOException
     */
    public void withJavaFile(String name, String contents, Charset encoding) throws IOException {
        withFile("src/main/java/com/vaadin/random/files/" + name, contents, encoding);
    }

    /**
     * Creates a new file in inside of the project.
     * @param name the java file name, not null.
     * @param contents the contents, not null.
     * @param encoding the file encoding, not null.
     * @throws IOException
     */
    public void withFile(String name, String contents, Charset encoding) throws IOException {
        final File file = new File(dir, name);
        Files.createDirectories(file.getParentFile().toPath());
        FileUtils.write(file, contents, encoding);
        if (!file.setLastModified(System.currentTimeMillis() - 2 * ONE_DAY)) {
            throw new RuntimeException("Failed to set lastModified of " + file + " to 2 days ago");
        }
    }

    public void withTemplate(String name, String contents) throws IOException {
        withTemplate(name, contents, Charsets.UTF_8);
    }

    public void withTemplate(String name, String contents, Charset encoding) throws IOException {
        withFile("src/main/resources/com/vaadin/random/files/" + name, contents, encoding);
    }
}
