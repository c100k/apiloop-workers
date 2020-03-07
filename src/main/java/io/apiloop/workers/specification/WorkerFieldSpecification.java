/**
 *
 */
package io.apiloop.workers.specification;

import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@Getter
@ToString
public class WorkerFieldSpecification {

    private final String code;
    private final I18nString name;
    private final I18nString description;
    private final String type;
    private final String htmlFormFieldType;
    private final Boolean mandatory;

    public WorkerFieldSpecification(String code, WorkerFieldDescriptor descriptor) {
        this.code = code;
        this.name = new I18nString(descriptor.name());
        this.description = new I18nString(descriptor.description());
        this.type = descriptor.type();
        this.htmlFormFieldType = descriptor.htmlFormFieldType();
        this.mandatory = descriptor.mandatory();
    }

}
