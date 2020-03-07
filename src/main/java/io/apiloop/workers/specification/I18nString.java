/**
 *
 */
package io.apiloop.workers.specification;

import io.apiloop.workers.specification.annotations.I18nDescriptor;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@Getter
@ToString
public class I18nString {

    private final String en;
    private final String fr;

    public I18nString(I18nDescriptor descriptor) {
        this.en = descriptor.en();
        this.fr = descriptor.fr();
    }

}
