/**
 * 
 */
package io.apiloop.workers.base.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.Worker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Optional;

/**
 * Read the value of a field in the node
 */
@Accessors(chain = true)
public class JsonFieldReader implements Worker<JsonFieldReader> {
    
    /**
     * Node where the field is
     */
    @Getter @Setter
    private ObjectNode node;
    
    /**
     * Name of the field
     */
    @Getter @Setter
    private String field;
    
    /**
     * Value of the field
     */
    @Getter
    private Optional<JsonNode> value;
    
    @Override
    public JsonFieldReader mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public JsonFieldReader execute() {
        if (Strings.isNullOrEmpty(field)) {
            value = Optional.empty();
            return this;
        }

        Boolean nonNull = node != null && !node.isNull() && node.hasNonNull(field);
        Boolean fieldIsReadable = nonNull &&
            (
                (node.get(field).isTextual() && !Strings.isNullOrEmpty(node.get(field).asText())) ||
                !node.get(field).isTextual()
            );
        
        if (!fieldIsReadable) {
            value = Optional.empty();
        } else {
            value = Optional.of(node.get(field));
        }
        
        return this;
    }
    
}
