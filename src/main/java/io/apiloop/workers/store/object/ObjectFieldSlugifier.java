/**
 * 
 */
package io.apiloop.workers.store.object;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.base.json.JsonFieldReader;
import io.apiloop.workers.base.text.Slugifier;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "29606e6a-82ce-4a6a-87f7-12c97a65165c",
    name = @I18nDescriptor(
        en = "Make an attribute URL-friendly",
        fr = "Rendre un attribut URL-friendly"
    ),
    description = @I18nDescriptor(
        en = "When you write a blog post 'My super blog post' you want it to have a clean URL like 'my-super-blog-post'",
        fr = "Quand vous écrivez un article de blog 'Mon super article de blog', vous voulez qu'il ait une URL propre comme 'mon-super-article-de-blog'"
    )
)
public class ObjectFieldSlugifier implements Worker<ObjectFieldSlugifier>, BusinessObjectHandler {

    @Getter @Setter
    private BusinessObject businessObject;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Source", fr = "Source"),
        description = @I18nDescriptor(en = "Field containing the string to slugify", fr = "Champ contenant la valeur à sluguifier"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String sourceField;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Destination", fr = "Destination"),
        description = @I18nDescriptor(en = "Field where to put the computed slug", fr = "Champ où mettre le slug calculé"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String destinationField;
    
    @Override
    public ObjectFieldSlugifier mapParameters(ObjectNode parameters) {
        sourceField = parameters.get("sourceField").asText();
        destinationField = parameters.get("destinationField").asText();
        return this;
    }

    @Override
    public ObjectFieldSlugifier execute() {
        if (businessObject.getData() == null || Strings.isNullOrEmpty(sourceField) || Strings.isNullOrEmpty(destinationField)) {
            return this;
        }

        Optional<JsonNode> sourceValue = new JsonFieldReader()
            .setNode(businessObject.getData())
            .setField(sourceField)
            .go()
            .getValue();
        
        if (sourceValue.isPresent() && sourceValue.get().isTextual()) {
            businessObject.getData().put(destinationField, new Slugifier()
                .setValue(sourceValue.get().asText())
                .go()
                .getSlug()
            );
        }
        
        return this;
    }
    
}
