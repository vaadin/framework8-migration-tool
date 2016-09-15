# Migration Tool for Converting Vaadin Framework 7 Projects to Vaadin Framework 8

## Building the Project

Running `mvn clean install` produces a runnable JAR in the local Maven folder. The project is currently not available in Maven central.

## Using the Tool
To convert a Vaadin 7 project, execute the JAR in the project folder, e.g.
in "myproject" run

`java -jar $HOME/.m2/repository/com/vaadin/vaadin8-migration-tool/8.0-SNAPSHOT/vaadin8-migration-tool-8.0-SNAPSHOT.jar`

## What Is Migrated?

The tool changes
* Class imports from `com.vaadin.ui` to `com.vaadin.v7.ui` for all components which have been moved to the compatibility package in Vaadin Framework 8.
* Declarative (HTML) files to use `<vaadin7-text-field>` instead of `<vaadin-text-field>` for all components which have been moved to the compatibility package in Vaadin Framework 8.

The tool does not, and you need to
* Update the dependencies in the project from version 7.x to 8.x
* Change project dependencies from `vaadin-server` to `vaadin-compatibility-server`
* Change project dependencies from `vaadin-client-compiled` to `vaadin-compatibility-client-compiled` if you are using `DefaultWidgetSet`
* Change project widget set from `DefaultWidgetSet` to `Vaadin7WidgetSet` if you are using `DefaultWidgetset`
* Recompile your widget set if you are not using `DefaultWidgetSet`


