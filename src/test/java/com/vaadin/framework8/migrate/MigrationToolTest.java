package com.vaadin.framework8.migrate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mavi
 */
public class MigrationToolTest {
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
        project.getJavaFile("NewDesign.java").assertModified();
        project.getTemplate("NewDesign.html").assertModified();
    }

    /**
     * Test for https://github.com/vaadin/framework8-migration-tool/issues/35
     * @throws Exception
     */
    @Test
    public void migrationShouldNotOverwriteUnmodifiedFiles() throws Exception {
        project.migrate();
        project.getJavaFile("FileWithNoVaadinImport.java").assertNotModified();
        project.getJavaFile("FileWithNonMigratedVaadinImport.java").assertNotModified();
    }

    @Test
    public void testImportsAreMigrated() throws Exception {
        project.migrate();
        final TestJavaFile myLabel = project.getJavaFile("MyLabel.java");
        myLabel.assertModified();
        myLabel.assertContents("package com.vaadin.random.files;\n" +
                "import com.vaadin.v7.ui.Label;\n" +
                "public class MyLabel extends Label {}\n");
    }
}
