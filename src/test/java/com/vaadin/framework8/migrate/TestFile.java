package com.vaadin.framework8.migrate;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.getFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mavi
 */
public class TestFile {
    public final File file;

    public TestFile(File file) {
        this.file = Objects.requireNonNull(file);
        assertTrue(file.isAbsolute());
        assertTrue(file.exists(), file + " doesn't exist");
        assertTrue(file.isFile(), file + " isn't a file");
    }

    /**
     * Checks whether the file has been modified by the migration tool (that is, the last modification time is now).
     * @return true if this file has been modified, false if not.
     */
    public boolean isModified() {
        final long lm = file.lastModified();
        assertFalse(lm <= 0, "last-modified of " + file + " is " + lm);
        return lm >= System.currentTimeMillis() - TestProject.ONE_DAY;
    }

    public TestJavaFile java() {
        return new TestJavaFile(file);
    }

    public String getContents() throws IOException {
        return FileUtils.readFileToString(file, Charsets.UTF_8);
    }

    public void assertModified() throws IOException {
        assertTrue(isModified(), "The file " + file + " has NOT been modified by the migration tool. Contents:\n" + getContents());
    }

    public void assertNotModified() throws IOException {
        assertFalse(isModified(), "The file " + file + " has been modified by the migration tool. Contents:\n" + getContents());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + file + '}';
    }

    public void assertContents(String expectedContents) throws IOException {
        assertEquals(expectedContents, getContents(), "The file " + file + " content differs");
    }
}
