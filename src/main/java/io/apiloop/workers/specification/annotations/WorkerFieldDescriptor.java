/**
 *
 */
package io.apiloop.workers.specification.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WorkerFieldDescriptor {

    I18nDescriptor name();
    I18nDescriptor description();
    String type();
    String htmlFormFieldType();
    boolean mandatory();

}
