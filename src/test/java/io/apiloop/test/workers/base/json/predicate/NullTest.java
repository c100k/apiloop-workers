/**
 * 
 */
package io.apiloop.test.workers.base.json.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.json.JsonPredicateChecker;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class NullTest {
    
    private ObjectNode node;
    private JsonPredicateChecker worker;
    private String onBadSyntaxErrorMessage;
    
    @Before
    public void setUp() {
        node = new ObjectMapper().createObjectNode()
            .putNull("nullField")
            .put("notNullField", "something");
        
        worker = new JsonPredicateChecker()
            .setNode(node)
            .setOnBadSyntaxError(message -> onBadSyntaxErrorMessage = message);
    }
    
    @Test
    public void matchingNull() {
        // Given
        worker.setExpression("nullField == null");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }

    @Test
    public void matchingNotNull() {
        // Given
        worker.setExpression("notNullField != null");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }
    
}
