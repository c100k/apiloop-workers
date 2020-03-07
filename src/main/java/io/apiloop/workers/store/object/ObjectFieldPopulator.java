/**
 * 
 */
package io.apiloop.workers.store.object;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.base.json.JsonFieldSetter;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "3bca4aaa-3f85-4656-97c8-910e6d5362b3",
    name = @I18nDescriptor(
        en = "Populate an attribute with a default value",
        fr = "Renseigner un attribut avec une valeur par défaut"
    ),
    description = @I18nDescriptor(
        en = "Sometimes you set default values when a user has not filled them",
        fr = "Parfois vous renseignez des valeurs par défaut quand un utilisateur ne les a pas saisies"
    )
)
public class ObjectFieldPopulator implements Worker<ObjectFieldPopulator>, BusinessObjectHandler {

    @Getter @Setter
    private BusinessObject businessObject;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Field", fr = "Champ"),
        description = @I18nDescriptor(en = "Name of the field to populate", fr = "Nom du champ à remplir"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String field;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Value", fr = "Valeur"),
        description = @I18nDescriptor(en = "Value to populate the field with", fr = "Valeur avec laquelle remplir le champ"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private Object value;
    
    @Override
    public ObjectFieldPopulator mapParameters(ObjectNode parameters) {
        field = parameters.get("field").asText();
        value = parameters.get("value").asText();
        return this;
    }

    @Override
    public ObjectFieldPopulator execute() {
        if (businessObject.getData() == null || Strings.isNullOrEmpty(field)) {
            return this;
        }
    
        new JsonFieldSetter().setNode(businessObject.getData()).setField(field).setValue(value).go();
        
        return this;
    }
    
}
