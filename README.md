# Migration Tool for Converting Vaadin Framework 7 Projects to Vaadin Framework 8

## Building the Project

Running `mvn clean install` produces a runnable JAR in the local Maven folder. The project is currently not available in Maven central.

## Using the Tool
To convert a Vaadin 7 project, execute the JAR in the project folder, e.g.
in "myproject" run

`java -jar $HOME/.m2/repository/com/vaadin/framework8-migration-tool/8.0-SNAPSHOT/framework8-migration-tool-8.0-SNAPSHOT.jar`

## What Is Migrated?

The tool changes
* Class imports from `com.vaadin.ui` to `com.vaadin.v7.ui` for all components which have been moved to the compatibility package in Vaadin Framework 8.
* Declarative (HTML) files to use `<vaadin7-text-field>` instead of `<vaadin-text-field>` for all components which have been moved to the compatibility package in Vaadin Framework 8.

The tool does not, and you need to
* Update the dependencies in the project from version 7.x to 8.x
* Make sure that you are using Java 8
* Update your `vaadin.version` property to some Vaadin Framework 8 version (e.g. 8.0.0.beta1).
* Use `vaadin-prereleases` repository if you are using some beta (not a snapshot): 
<pre>
&lt;repository&gt;
        &lt;id&gt;vaadin-prereleases&lt;/id&gt;
        &lt;url&gt;https://maven.vaadin.com/vaadin-prereleases&lt;/url&gt;
&lt;/repository&gt;


&lt;pluginRepositories&gt;
    &lt;pluginRepository&gt;
            &lt;id&gt;vaadin-prereleases&lt;/id&gt;
            &lt;url&gt;https://maven.vaadin.com/vaadin-prereleases&lt;/url&gt;
    &lt;/pluginRepository&gt;
&lt;/pluginRepositories&gt;

</pre>
* Change project dependencies from `vaadin-server` to `vaadin-compatibility-server`
* Change project dependencies from `vaadin-client-compiled` to `vaadin-compatibility-client-compiled` if you are using `com.vaadin.DefaultWidgetSet`
* Change project widget set from `com.vaadin.DefaultWidgetSet` to `com.vaadin.v7.Vaadin7WidgetSet` if you are using `DefaultWidgetset`. This is typically declared with a @Widgetset annotation in your UI or in the web.xml file.
* Recompile your widget set if you are not using `com.vaadin.DefaultWidgetSet`


