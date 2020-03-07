/**
 *
 */
package io.apiloop.workers.base.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.Worker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

/**
 * Build a Json Document from a Json string
 */
@Accessors(chain = true)
public class JsonDocumentBuilder implements Worker<JsonNode> {

    @Getter @Setter
    private String content;

    @Override
    public JsonDocumentBuilder mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }
    
    @Override
    public JsonNode execute() {
        if (Strings.isNullOrEmpty(content)) {
            return null;
        }
        try {
            return new ObjectMapper().readTree(content);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
