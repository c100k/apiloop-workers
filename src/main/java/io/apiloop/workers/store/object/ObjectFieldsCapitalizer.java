/**
 * 
 */
package io.apiloop.workers.store.object;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "ae9ed419-d038-4ebf-80b1-35af28958909",
    name = @I18nDescriptor(
        en = "Capitalize the first letter",
        fr = "Mettre en majuscule la première lettre"
    ),
    description = @I18nDescriptor(
        en = "Some users do not put an uppercase on their lastname, this worker lets you automate this",
        fr = "Certains utilisateurs ne mettent pas de majuscule à leur nom, ce worker vous permet d'automatiser cela"
    )
)
public class ObjectFieldsCapitalizer implements Worker<ObjectFieldsCapitalizer>, BusinessObjectHandler {

    @Getter @Setter
    private BusinessObject businessObject;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Fields", fr = "Champs"),
        description = @I18nDescriptor(en = "Field to capitalize", fr = "Champs dont la première lettre doit être mise en majuscule"),
        type = "EnumerationType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private List<String> fields;

    @Override
    public ObjectFieldsCapitalizer mapParameters(ObjectNode parameters) {
        fields = new ArrayList<>();
        if (parameters.get("fields").isArray()) {
            parameters.get("fields").elements().forEachRemaining(field -> fields.add(field.asText()));
        }
        return this;
    }
    
    @Override
    public ObjectFieldsCapitalizer execute() {
        if (businessObject.getData() == null || fields == null) {
            return this;
        }

        for (String field : fields) {
            if (businessObject.getData().has(field) && businessObject.getData().get(field).isTextual()) {
                businessObject.getData().put(field, WordUtils.capitalize(businessObject.getData().get(field).asText()));
            }
        }

        return this;
    }
    
}
