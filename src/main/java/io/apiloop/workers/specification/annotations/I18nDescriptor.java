/**
 *
 */
package io.apiloop.workers.specification.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nDescriptor {

    String en();
    String fr();

}
