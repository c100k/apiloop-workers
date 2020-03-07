/**
 *
 */
package io.apiloop.test.workers.specification;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.Worker;
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
    id = "worker-id",
    name = @I18nDescriptor(en = "An annotated worker", fr = "Un worker annoté"),
    description = @I18nDescriptor(en = "This worker does not do anything", fr = "Ce worker ne fait rien"),
    externalServiceUrl = "https://www.annotatedworker.com"
)
public class AnnotatedWorker implements Worker<AnnotatedWorker> {

    @Getter @Setter
    private String injectedField;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Editable string", fr = "Chaîne de caractères éditable"),
        description = @I18nDescriptor(en = "A string editable by the user", fr = "Une chaîne éditable par l'utilisateur"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String editableString;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Editable boolean", fr = "Booléen éditable"),
        description = @I18nDescriptor(en = "A boolean editable by the user", fr = "Une booléen éditable par l'utilisateur"),
        type = "BooleanType",
        htmlFormFieldType = "radio",
        mandatory = true
    )
    private Boolean editableBoolean;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Editable integer", fr = "Booléen éditable"),
        description = @I18nDescriptor(en = "A integer editable by the user", fr = "Une entier éditable par l'utilisateur"),
        type = "integerType",
        htmlFormFieldType = "number-integer",
        mandatory = false
    )
    private Integer editableInteger;
    
    @Override
    public Worker<AnnotatedWorker> mapParameters(ObjectNode parameters) {
        editableString = parameters.get("editableString").asText();
        editableBoolean = parameters.get("editableBoolean").asBoolean();
        editableInteger = parameters.get("editableInteger").asInt();
        return this;
    }

    @Override
    public AnnotatedWorker execute() {
        return this;
    }
    
}
