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
public class StringTest {
    
    private ObjectNode node;
    private JsonPredicateChecker worker;
    private String onBadSyntaxErrorMessage;
    
    @Before
    public void setUp() {
        node = new ObjectMapper().createObjectNode()
            .put("firstname", "Sheldon")
            .put("lastname", "Cooper");
        node.set("address", new ObjectMapper().createObjectNode()
            .put("city", "Pasadena")
            .put("country", "USA")
            .set("state", new ObjectMapper().createObjectNode()
                .put("code", "CA")
                .put("name", "California")
            )
        );
        
        worker = new JsonPredicateChecker()
            .setNode(node)
            .setOnBadSyntaxError(message -> onBadSyntaxErrorMessage = message);
    }
    
    @Test
    public void matchingFirstLevelField() {
        // Given
        worker.setExpression("firstname == \"Sheldon\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.getContext().has("firstname")).isTrue();
        assertThat(worker.getContext().has("lastname")).isTrue();
        assertThat(worker.getContext().has("address.city")).isTrue();
        assertThat(worker.getContext().has("address.country")).isTrue();
        assertThat(worker.getContext().has("address.state.code")).isTrue();
        assertThat(worker.getContext().has("address.state.name")).isTrue();
        assertThat(worker.getEvaluation()).isInstanceOf(Boolean.class);
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void notMatchingFirstLevelField() {
        // Given
        worker.setExpression("firstname == \"Leonard\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isFalse();
    }
    
    @Test
    public void matchingMultipleFields() {
        // Given
        worker.setExpression("firstname == \"Sheldon\" && lastname == \"Cooper\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void notMatchingMultipleFields() {
        // Given
        worker.setExpression("firstname == \"Sheldon\" && lastname == \"Coo\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isFalse();
    }
    
    @Test
    public void matchingThirdLevelField() {
        // Given
        worker.setExpression("address.state.code == \"CA\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isTrue();
    }
    
    @Test
    public void notMatchingThirdLevelField() {
        // Given
        worker.setExpression("address.state.code == \"CAL\"");
        
        // When
        worker.go();
        
        // Then
        assertThat(worker.isTrue()).isFalse();
    }
    
}
