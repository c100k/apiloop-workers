/**
 * 
 */
package io.apiloop.test.workers.base.json.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.json.JsonPredicateChecker;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class ArrayTest {
    
    private ObjectNode node;
    private JsonPredicateChecker worker;
    private String onBadSyntaxErrorMessage;
    
    @Before
    public void setUp() {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode()
            .add("item 1")
            .add("item 2")
            .add("item 3");

        node = new ObjectMapper().createObjectNode();
        node.set("filledArray", arrayNode);
        node.putArray("nullArray");

        worker = new JsonPredicateChecker()
            .setNode(node)
            .setOnBadSyntaxError(message -> onBadSyntaxErrorMessage = message);
    }

    @Test
    public void matchingArrayItem() {
        // Given
        worker.setExpression("filledArray.get(2).asText().equals(\"item 3\")");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.getContext().has("filledArray")).isTrue();
        assertThat(worker.getEvaluation()).isInstanceOf(Boolean.class);
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void matchingEmptyArray() {
        // Given
        worker.setExpression("nullArray.size() == 0");
            
        // When
        worker.go();
        
        // Then
        assertThat(worker.getContext().has("nullArray")).isTrue();
        assertThat(worker.getEvaluation()).isInstanceOf(Boolean.class);
        assertThat(worker.isTrue()).isTrue();
    }
    
}
