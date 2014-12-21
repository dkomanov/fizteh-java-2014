package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that a field is a part of JSON object structure and should be serialized.
 * @author Phoenix
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONField {
    /**
     * Name of JSON field.<br/>
     * If you do not specify this parameter or set it an empty string,
     * the default name of the field in java code will be used.
     * @see java.lang.reflect.Field#getName()
     */
    String name() default "";
}
