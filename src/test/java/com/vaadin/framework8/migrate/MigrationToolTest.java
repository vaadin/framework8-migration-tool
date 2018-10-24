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
    public void migrationShouldNotOverwriteUnmodifiedTemplates() throws Exception {
        project.withTemplate("Foo.html", "</>");
        project.migrate();
        project.getTemplate("Foo.html").assertNotModified();
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

    @Test
    public void testTemplatesAreMigrated() throws Exception {
        project.withTemplate("Foo.html", "<vaadin-vertical-layout></vaadin-vertical-layout>");
        project.migrate();
        final TestFile template = project.getTemplate("Foo.html");
        template.assertModified();
        template.assertContents("<vaadin7-vertical-layout></vaadin7-vertical-layout>");
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

    @Test
    public void saveDeclarativeFilesInUTF8() throws Exception {
        TestUtils.setDefaultCharset(Charsets.ISO_8859_1);
        project.withTemplate("Foo.html", "<vaadin-vertical-layout><!-- Geschäftspartner --></vaadin-vertical-layout>");
        project.migrate();
        final TestFile template = project.getTemplate("Foo.html");
        template.assertModified();
        template.assertContents("<vaadin7-vertical-layout><!-- Geschäftspartner --></vaadin7-vertical-layout>");
    }

    /**
     * https://github.com/vaadin/framework8-migration-tool/issues/40
     * @throws Exception
     */
    @Test
    public void testStarImportsAreMigrated() throws Exception {
        project.withJavaFile("MyLabel.java", "package com.vaadin.random.files;\n" +
                "import com.vaadin.ui.*;\n" +
                "import com.vaadin.data.*;\n" +
                "import com.vaadin.data.validator.*;\n" +
                "import com.vaadin.data.util.*;\n" +
                "import com.vaadin.data.util.converter.*;\n" +
                "import com.vaadin.data.util.filter.*;\n" +
                "import com.vaadin.data.util.sqlcontainer.*;\n" +
                "public class MyLabel extends Label {}\n", Charsets.UTF_8);
        project.migrate();
        final TestJavaFile myLabel = project.getJavaFile("MyLabel.java");
        myLabel.assertModified();
        myLabel.assertContents("package com.vaadin.random.files;\n" +
                "import com.vaadin.v7.ui.*;\n" +
                "import com.vaadin.v7.data.*;\n" +
                "import com.vaadin.v7.data.validator.*;\n" +
                "import com.vaadin.v7.data.util.*;\n" +
                "import com.vaadin.v7.data.util.converter.*;\n" +
                "import com.vaadin.v7.data.util.filter.*;\n" +
                "import com.vaadin.v7.data.util.sqlcontainer.*;\n" +
                "public class MyLabel extends Label {}\n");
    }
}
