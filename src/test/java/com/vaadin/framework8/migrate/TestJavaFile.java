package com.vaadin.framework8.migrate;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mavi
 */
public class TestJavaFile extends TestFile {
    public TestJavaFile(File file) {
        super(file);
        assertTrue(file.getName().endsWith(".java"), file + " isn't a Java file");
    }
}
