package com.vaadin.framework8.migrate;

import java.io.File;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mavi
 */
public class TestJavaFile extends TestFile {
    public TestJavaFile(File file, Charset charset) {
        super(file, charset);
        assertTrue(file.getName().endsWith(".java"), file + " isn't a Java file");
    }
}
