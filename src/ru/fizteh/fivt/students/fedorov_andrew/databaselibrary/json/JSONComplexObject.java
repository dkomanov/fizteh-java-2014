package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that objects of the annotated type contain multiple JSON fields.
 * @author Phoenix
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface JSONComplexObject {
    /**
     * If true, this object will not be converted to JSON directly. Its single field will be taken instead.
     */
    boolean singleField() default false;
}
