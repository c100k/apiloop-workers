/**
 * 
 */
package io.apiloop.test.workers.base.json.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.json.JsonPredicateChecker;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class IntegerTest {
    
    private ObjectNode node;
    private JsonPredicateChecker worker;
    private String onBadSyntaxErrorMessage;
    
    @Before
    public void setUp() {
        node = new ObjectMapper().createObjectNode()
            .put("age", 45);
        
        worker = new JsonPredicateChecker()
            .setNode(node)
            .setOnBadSyntaxError(message -> onBadSyntaxErrorMessage = message);
    }
    
    @Test
    public void matching() {
        // Given
        Map<String, Integer> values = new HashMap<>();
        values.put("==", 45);
        values.put(">", 40);
        values.put(">=", 45);
        values.put("<", 99);
        values.put("<=", 45);
        
        for (Entry<String, Integer> entry : values.entrySet()) {
            // Given
            worker.setExpression("age " + entry.getKey() + " " + entry.getValue());
            
            // When
            worker.go();
            
            // Then
            assertThat(worker.isTrue()).isTrue();
        }
    }
    
    @Test
    public void notMatching() {
        // Given
        Map<String, Integer> values = new HashMap<>();
        values.put("==", 87);
        values.put(">", 87);
        values.put(">=", 87);
        values.put("<", 23);
        values.put("<=", 23);
        
        for (Entry<String, Integer> entry : values.entrySet()) {
            // Given
            worker.setExpression("age " + entry.getKey() + " " + entry.getValue());
            
            // When
            worker.go();
            
            // Then
            assertThat(worker.isTrue()).isFalse();
        }
    }
    
}
