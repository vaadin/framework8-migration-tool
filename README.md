# Migration Tool for Converting Vaadin Framework 7 Projects to Vaadin Framework 8

### This tool has been added to the _vaadin-maven-plugin_, and can be run with the target `vaadin:upgrade8`. Please refer to the [documentation](https://vaadin.com/docs/-/part/framework/migration/migrating-to-vaadin8.html) on how to migrate.

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
* Update any fully-qualified classnames used in the code for classes that have been moved to compatibility packages
* Update your `vaadin.version` property to some Vaadin Framework 8 version (e.g. 8.0.0).
* Change project dependencies from `vaadin-server` to `vaadin-compatibility-server`
* Change project dependencies from `vaadin-client-compiled` to `vaadin-compatibility-client-compiled` if you are using `com.vaadin.DefaultWidgetSet`
* Change project widget set from `com.vaadin.DefaultWidgetSet` to `com.vaadin.v7.Vaadin7WidgetSet` if you are using `DefaultWidgetset`. This is typically declared with a @Widgetset annotation in your UI or in the web.xml file.
* Recompile your widget set if you are not using `com.vaadin.DefaultWidgetSet`
