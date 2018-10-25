package com.vaadin.framework8.migrate;

import org.apache.commons.io.Charsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author mavi
 */
public class MigrationToolTest {
    private TestProject project;

    @BeforeEach
    public void setupTestProject() throws Exception {
        project = TestProject.empty();
    }

    @AfterEach
    public void tearDown() throws Exception {
        project.close();
    }

    @Test
    public void smokeTest() throws Exception {
        project.close();
        project = TestProject.fromTemplate();
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
        project.close();
        project = TestProject.fromTemplate();
        project.migrate();
        project.getJavaFile("FileWithNoVaadinImport.java").assertNotModified();
        project.getJavaFile("FileWithNonMigratedVaadinImport.java").assertNotModified();
    }

    @Test
    public void testImportsAreMigrated() throws Exception {
        project.withJavaFile("MyLabel.java", "package com.vaadin.random.files;\n" +
                "import com.vaadin.ui.Label;\n" +
                "public class MyLabel extends Label {}\n", Charsets.UTF_8);
        project.migrate();
        final TestJavaFile myLabel = project.getJavaFile("MyLabel.java");
        myLabel.assertModified();
        myLabel.assertContents("package com.vaadin.random.files;\n" +
                "import com.vaadin.v7.ui.Label;\n" +
                "public class MyLabel extends Label {}\n");
    }

    /**
     * Test for https://github.com/vaadin/framework8-migration-tool/issues/24
     */
    @Test
    public void allowSpecifyingEncoding() throws Exception {
        project.withJavaFile("MyLabel.java", "package com.vaadin.random.files;\n" +
                "import com.vaadin.ui.Label;\n" +
                "public class MyLabel extends Label {}\n// Geschäftspartner", Charsets.ISO_8859_1);
        project.migrate("8.5.2", Charsets.ISO_8859_1);
        final TestJavaFile myLabel = project.getJavaFile("MyLabel.java", Charsets.ISO_8859_1);
        myLabel.assertContents("package com.vaadin.random.files;\n" +
                "import com.vaadin.v7.ui.Label;\n" +
                "public class MyLabel extends Label {}\n// Geschäftspartner");
    }
}
