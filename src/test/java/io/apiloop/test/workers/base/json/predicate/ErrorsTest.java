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
public class ErrorsTest {

    private JsonPredicateChecker worker;
    private String onBadSyntaxErrorMessage;
    
    @Before
    public void setUp() {
        ObjectNode node = new ObjectMapper().createObjectNode()
            .put("firstname", "Sheldon");
        
        worker = new JsonPredicateChecker()
            .setNode(node)
            .setOnBadSyntaxError(message -> onBadSyntaxErrorMessage = message);
    }
    
    @Test
    public void notExistingField() {
        // Given
        worker.setExpression("hdjskghkfdghfdkjg == \"123\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isFalse();
    }
    
    @Test
    public void badSyntax() {
        // Given
        worker.setExpression("hfdjfhdfhjf?-");
        
        // When
        worker.go();
        
        // Then
        assertThat(onBadSyntaxErrorMessage).isEqualTo("BadSyntax");
    }
    
}
