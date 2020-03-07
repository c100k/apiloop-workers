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
import static org.assertj.core.api.StrictAssertions.catchThrowable;

/**
 * 
 */
public class BooleanTest {
    
    private ObjectNode node;
    private JsonPredicateChecker worker;
    private String onBadSyntaxErrorMessage;
    
    @Before
    public void setUp() {
        node = new ObjectMapper().createObjectNode()
            .put("trueValue", true)
            .put("falseValue", false);
        
        worker = new JsonPredicateChecker()
            .setNode(node)
            .setOnBadSyntaxError(message -> onBadSyntaxErrorMessage = message);
    }
    
    @Test
    public void matchingTrue() {
        // Given
        worker.setExpression("trueValue == true");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void matchingTrueShortSyntax() {
        // Given
        worker.setExpression("trueValue");
        
        // When
        Throwable thrown = catchThrowable(() -> worker.go());
        
        // Then
        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("Evaluation did not return a boolean");
    }
    
    @Test
    public void matchingFalse() {
        // Given
        worker.setExpression("falseValue == false");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void matchingFalseShortSyntax() {
        // Given
        worker.setExpression("!falseValue");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void notMatching() {
        // Given
        worker.setExpression("trueValue == false");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isFalse();
    }
    
    @Test
    public void notMatchingShortSyntax() {
        // Given
        worker.setExpression("!trueValue");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isFalse();
    }
    
}
