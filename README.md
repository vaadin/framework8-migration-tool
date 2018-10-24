# Migration Tool for Converting Vaadin Framework 7 Projects to Vaadin Framework 8

### This tool has been added to the _vaadin-maven-plugin_, and can be run with the target `vaadin:upgrade8`. Please refer to the [documentation](https://vaadin.com/docs/-/part/framework/migration/migrating-to-vaadin8.html) on how to migrate.

## Building the Project

Running `./mvnw -C clean package` produces a runnable JAR both in the `target/` directory, and in the local Maven folder. The project is currently not available in Maven central.

## Using the Tool
To convert a Vaadin 7 project, just follow these steps. First, simply build the migration tool as following:

```bash
git clone https://github.com/vaadin/framework8-migration-tool
cd framework8-migration-tool
./mvnw -C clean package
```

then:

* copy `target/*.jar` to your project as `upgrade8.jar`,
* run `java -jar upgrade8.jar 8.5.2` to migrate your project
* after that's done, remove `upgrade8.jar`

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

## Modifying the Tool

Often you attempt to migrate your Vaadin app in two steps:

* First you migrate the app on your machine only, to see the outcome of the migration. You don't want to commit the outcome of the migration to the master repo though,
  simply because the project sources are often left uncompilable, or the addons are incompatible etc etc. Having project in this state would cause your co-workers to stop working
  until you figure out how to port the whole app to Vaadin 8 in its entirety. And we don't want to do that. So you keep changes local only, or in a separate git branch.
* After you're satisfied with the migration (the app+widgetset compiles and everything seems to generally work), then you try to merge with the master repo. Uh-oh -
  your colleagues meanwhile modified lots of sources. The changes are incompatible with the changes the migration tool did, so you get lots of git conflicts,
  which are next to impossible to fix by hand.
* So you run the migration tool once again on the fresh master, only to find that your local post-migration changes are gone, and you need to start anew. Not cool.

Therefore, what I tend to do is to introduce a customary migration steps into the migration tool, so that I can perform a full migration automatically
on the fresh master, and the outcome is still able to compile and run. That way, I can tell my colleagues to stop committing (stop-the-world), do the migration in reasonable
time (1 hour) and push to master. This way, I avoid git merge conflicts, and my coworkers are blocked only for a brief period of time (the stop-the-world window is small).

In order to achieve this, I need to modify the migration tool sources to introduce my own customary migration steps.
What I do is:

* I open this repo in the IDE of my choosing, and simply edit the migration tool sources.
* Then, I run the main `Migrate` class from my IDE, setting the current working directory to the project being migrated.
* Then I try to compile and run the project. If it doesn't run, I add more custom rules and repeat the whole process.

The point is that I don't even need to build a runnable jar file of the Migration tool - you simply run the migration tool straight off the sources, directly from your IDE.

> I tend to prepare the new widgetset upfront, so that I have it stand by and ready on the Migration Day. Then, I just migrate the java sources,
bump the version of the widgetset and the migration is over very fast.
