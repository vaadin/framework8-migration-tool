package com.vaadin.framework8.migrate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mavi
 */
public class MigrateTest {
    private TestProject project;

    @BeforeEach
    public void setupTestProject() throws Exception {
        project = TestProject.create();
    }

    @AfterEach
    public void tearDown() throws Exception {
        project.close();
    }

    @Test
    public void smokeTest() throws Exception {
        project.migrate();
        assertTrue(project.isModified("src/main/java/com/vaadin/random/files/NewDesign.java"));
        assertTrue(project.isModified("src/main/resources/com/vaadin/random/files/NewDesign.html"));
    }

    /**
     * Test for https://github.com/vaadin/framework8-migration-tool/issues/35
     * @throws Exception
     */
    @Test
    public void migrationShouldNotOverwriteUnmodifiedFiles() throws Exception {
        project.assertNotModified("src/main/java/com/vaadin/random/files/FileWithNoVaadinImport.java");
    }
}
