package com.vaadin.framework8.migrate;

import org.apache.commons.io.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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
    public static TestProject create() throws IOException {
        final File dir = new File("test-projects/random-files").getAbsoluteFile();
        assertTrue(dir.exists(), dir + " doesn't exist");
        assertTrue(dir.isDirectory(), dir + " isn't a directory");
        final File tempFile = File.createTempFile("testproject", "dir");
        final File tempDir = new File(tempFile.getAbsolutePath() + "-dir");
        FileUtils.copyDirectory(dir, tempDir);
        tempFile.delete();
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

    public TestFile getFile(String name) {
        return new TestFile(new File(dir, name));
    }

    public TestJavaFile getJavaFile(String name) {
        return new TestJavaFile(new File(new File(dir, "src/main/java/com/vaadin/random/files"), name));
    }

    public TestFile getTemplate(String name) {
        return new TestFile(new File(new File(dir, "src/main/resources/com/vaadin/random/files"), name));
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteDirectory(dir);
    }

    public void migrate() throws Exception {
        new MigrationTool("8.5.2", dir).migrate();
    }

    static final long ONE_DAY = 1L * 24 * 60 * 60 * 1000;
}
