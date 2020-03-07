/**
 * 
 */
package io.apiloop.workers.store.object;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.base.crypt.BCryptHasher;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "cf617410-4574-4bcd-829f-23ad0ada5aa3",
    name = @I18nDescriptor(
        en = "Crypt data",
        fr = "Crypter des informations"
    ),
    description = @I18nDescriptor(
        en = "Crypt information to make them unreadable by a human",
        fr = "Crypte des informations afin de les rendre illisibles par un humain"
    )
)
public class ObjectFieldsBCryptHasher implements Worker<ObjectFieldsBCryptHasher>, BusinessObjectHandler {

    @Getter @Setter
    private BusinessObject businessObject;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Fields", fr = "Champs"),
        description = @I18nDescriptor(en = "Fields to crypt", fr = "Champs à crypter"),
        type = "EnumerationType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private List<String> fields;
    
    @Override
    public ObjectFieldsBCryptHasher mapParameters(ObjectNode parameters) {
        fields = new ArrayList<>();
        if (parameters.get("fields").isArray()) {
            parameters.get("fields").elements().forEachRemaining(field -> fields.add(field.asText()));
        }
        return this;
    }

    @Override
    public ObjectFieldsBCryptHasher execute() {
        if (businessObject.getData() == null || fields == null) {
            return this;
        }

        for (String field : fields) {
            if (businessObject.getData().has(field) && businessObject.getData().get(field).isTextual()) {
                businessObject.getData().put(field, new BCryptHasher()
                    .value(businessObject.getData().get(field).asText())
                    .go()
                    .hash()
                );
            }
        }

        return this;
    }
    
}
