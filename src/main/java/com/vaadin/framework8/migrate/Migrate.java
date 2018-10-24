package com.vaadin.framework8.migrate;

import java.io.File;

public class Migrate {

    private static final String VERSION = "-version=";

    public static void main(String[] args) throws Exception {
        String version = "8.5.2";
        if (args.length > 0) {
            if (args[0].startsWith(VERSION)) {
                version = args[0].substring(VERSION.length());
            }
        }

        new MigrationTool(version, new File(".")).migrate();
    }
}
