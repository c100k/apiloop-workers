/**
 * 
 */
package io.apiloop.test.workers.store.object;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.store.object.ObjectFieldsCapitalizer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class ObjectFieldsCapitalizerTest {
    
    private ObjectFieldsCapitalizer worker;
    
    @Before
    public void setUp() {
        worker = new ObjectFieldsCapitalizer();
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }
    
    @Test
    public void withBadNode() {
        // Given
        worker
            .setBusinessObject(new BusinessObjectImpl().setData(null))
            .setFields(Arrays.asList("field1"));
        
        // When
        worker.go();
        
        // Then
    }
    
    @Test
    public void withBadFields() {
        // Given
        worker
            .setBusinessObject(new BusinessObjectImpl().setData(new ObjectMapper().createObjectNode()))
            .setFields(null);
        
        // When
        worker.go();
        
        // Then
    }
    
    @Test
    public void withFields() {
        // Given
        Map<String, String> fieldsToHash = new HashMap<>();
        fieldsToHash.put("firstname", "sheldon");
        fieldsToHash.put("lastname", "cooper");
        ObjectNode node = new ObjectMapper().createObjectNode().put("age", 28);
        for (Entry<String, String> entry : fieldsToHash.entrySet()) {
            node.put(entry.getKey(), entry.getValue());
        }
        worker
            .setBusinessObject(new BusinessObjectImpl().setData(node))
            .setFields(new ArrayList<>(fieldsToHash.keySet()));
        
        // When
        worker.go();
        
        // Then
        assertThat(node.get("age").asInt()).isEqualTo(28);
        // Just to make sure there is not any case sensitive/insensitive thing
        assertThat(node.get("firstname").asText()).isNotEqualTo("sheldon");
        assertThat(node.get("firstname").asText()).isEqualTo("Sheldon");
        assertThat(node.get("firstname").asText()).isNotEqualTo("cooper");
        assertThat(node.get("lastname").asText()).isEqualTo("Cooper");
    }
    
}
