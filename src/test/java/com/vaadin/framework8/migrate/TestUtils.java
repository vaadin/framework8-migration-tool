package com.vaadin.framework8.migrate;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * @author mavi
 */
public class TestUtils {
    public static void setDefaultCharset(Charset charset) {
        try {
            final Field field = Charset.class.getDeclaredField("defaultCharset");
            field.setAccessible(true);
            field.set(null, charset);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
