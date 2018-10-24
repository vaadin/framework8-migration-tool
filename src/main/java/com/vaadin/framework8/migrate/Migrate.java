package com.vaadin.framework8.migrate;

import org.apache.commons.io.Charsets;

import java.io.File;
import java.nio.charset.Charset;

public class Migrate {

    private static final String VERSION = "-version=";
    private static final String CHARSET = "-charset=";

    public static void main(String[] args) throws Exception {
        String version = "8.5.2";
        Charset charset = Charsets.UTF_8;
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith(VERSION)) {
                    version = arg.substring(VERSION.length());
                } else if (arg.startsWith(CHARSET)) {
                    charset = Charset.forName(arg.substring(CHARSET.length()));
                }
            }
        }

        new MigrationTool(version, new File("."), charset).migrate();
    }
}
