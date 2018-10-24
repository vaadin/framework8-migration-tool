package com.vaadin.framework8.migrate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        final TestProject testProject = TestProject.create();
        System.out.println(testProject.pomXml);
        testProject.migrate();
    }
}
