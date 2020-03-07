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

/**
 * Set the value of a field in the node 
 */
@Accessors(chain = true)
public class JsonFieldSetter implements Worker<ObjectNode> {
    
    @Getter @Setter
    private ObjectNode node;

    @Getter @Setter
    private String field;

    @Getter @Setter
    private Object value;
    
    @Override
    public JsonFieldSetter mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public ObjectNode execute() {
        if (node == null || Strings.isNullOrEmpty(field)) {
            return node;
        }
    
        if (value == null) {
            node.putNull(field);
        } else {
            setAccordingToType();
        }
        
        return node;
    }
    
    /**
     * This code is very ugly but Jackson does not handle
     * this transparently when we want to set an Object
     */
    private void setAccordingToType() {
        if (value instanceof String) {
            node.put(field, (String) value);
        } else if (value instanceof Boolean) {
            node.put(field, (Boolean) value);
        } else if (value instanceof Integer) {
            node.put(field, (Integer) value);
        } else if (value instanceof Long) {
            node.put(field, (Long) value);
        } else if (value instanceof Double) {
            node.put(field, (Double) value);
        } else if (value instanceof Float) {
            node.put(field, (Float) value);
        } else if (value instanceof JsonNode) {
            node.set(field, (JsonNode) value);
        }
    }
    
}
