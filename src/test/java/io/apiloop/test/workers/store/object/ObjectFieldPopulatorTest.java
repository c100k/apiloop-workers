/**
 * 
 */
package io.apiloop.test.workers.store.object;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.test.workers.FixturesUtils;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.store.object.ObjectFieldPopulator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class ObjectFieldPopulatorTest {
    
    private ObjectFieldPopulator worker;
    private ObjectNode node;
    
    @Before
    public void setUp() {
        String existingString = "LOL";
        Integer existingInteger = 4;
        Long existingLong = 12L;
        Double existingDouble = 3.45;
        Float existingFloat = 7.53F;
        ObjectNode existingNode = new ObjectMapper().createObjectNode().put("f1", 34).put("f2", "wow");
        
        node = new ObjectMapper().createObjectNode()
            .putNull("nullString")
            .put("emptyString", "")
            .put("existingString", existingString)
            .putNull("nullInteger")
            .put("existingInteger", existingInteger)
            .putNull("nullLong")
            .put("existingLong", existingLong)
            .putNull("nullDouble")
            .put("existingDouble", existingDouble)
            .putNull("nullFloat")
            .put("existingFloat", existingFloat)
            .putNull("nullBoolean")
            .put("existingTrueBoolean", true)
            .put("existingFalseBoolean", false)
            .putNull("nullNode");
        node.set("existingNode", existingNode);
    
        worker = new ObjectFieldPopulator().setBusinessObject(new BusinessObjectImpl().setData(node));
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }
    
    @Test
    public void withBadNode() {
        // Given
        worker.setField("name").setValue("Default Name");
        
        // When
        worker.go();
        
        // Then
    }
    
    @Test
    public void withBadField() {
        for (String value : FixturesUtils.mandatoryStringBadValues()) {
            // Given
            worker.setField(value).setValue("Default value");
            
            // When
            worker.go();
            
            // Then
        }
    }
    
    @Test
    public void withString() {
        for (String field : Arrays.asList("nullString", "emptyString", "existingString")) {
            // Given
            worker.setField(field).setValue("Default value");
            
            // When
            worker.go();
            
            // Then
            assertThat(node.get(field).isTextual()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullString").asText()).isEqualTo("Default value");
        assertThat(node.get("emptyString").asText()).isEqualTo("Default value");
        assertThat(node.get("existingString").asText()).isEqualTo("Default value");
    }

    @Test
    public void withStringBeginningWithNumber() {
        // Given
        worker.setField("nullString").setValue("123 Hahaha");
        
        // When
        worker.go();
        
        // Then
        assertThat(node.get("nullString").asText()).isEqualTo("123 Hahaha");
    }
    
    @Test
    public void withInteger() {
        for (String field : Arrays.asList("nullInteger", "existingInteger")) {
            // Given
            worker.setField(field).setValue(45);
            
            // When
            worker.go();
            
            // Then
            assertThat(node.get(field).isInt()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullInteger").asInt()).isEqualTo(45);
        assertThat(node.get("existingInteger").asInt()).isEqualTo(45);
    }
    
    @Test
    public void withLong() {
        for (String field : Arrays.asList("nullLong", "existingLong")) {
            // Given
            worker.setField(field).setValue(32L);
            
            // When
            worker.go();
        }
        
        // Then
        assertThat(node.get("nullLong").asLong()).isEqualTo(32L);
        assertThat(node.get("existingLong").asLong()).isEqualTo(32L);
    }
    
    @Test
    public void withDouble() {
        for (String field : Arrays.asList("nullDouble", "existingDouble")) {
            // Given
            worker.setField(field).setValue(98.7);
            
            // When
            worker.go();
            
            // Then
            assertThat(node.get(field).isDouble()).isTrue();
        }
    
        // Then
        assertThat(node.get("nullDouble").asDouble()).isEqualTo(98.7);
        assertThat(node.get("existingDouble").asDouble()).isEqualTo(98.7);
    }
    
    @Test
    public void withFloat() {
        for (String field : Arrays.asList("nullFloat", "existingFloat")) {
            // Given
            worker.setField(field).setValue(37.4F);
            
            // When
            worker.go();
    
            // Then
            assertThat(node.get(field).isFloat()).isTrue();
        }
    
        // Then
        assertThat(node.get("nullFloat").floatValue()).isEqualTo(37.4F);
        assertThat(node.get("existingFloat").floatValue()).isEqualTo(37.4F);
    }
    
    @Test
    public void withBoolean() {
        for (String field : Arrays.asList("nullBoolean", "existingTrueBoolean", "existingFalseBoolean")) {
            // Given
            worker.setField(field).setValue(false);
            
            // When
            worker.go();
    
            // Then
            assertThat(node.get(field).isBoolean()).isTrue();
        }
    
        // Then
        assertThat(node.get("nullBoolean").asBoolean()).isEqualTo(false);
        assertThat(node.get("existingTrueBoolean").asBoolean()).isEqualTo(false);
        assertThat(node.get("existingFalseBoolean").asBoolean()).isEqualTo(false);
    }
    
    @Test
    public void withNode() {
        for (String field : Arrays.asList("nullNode", "existingNode")) {
            // Given
            worker.setField(field).setValue(new ObjectMapper().createObjectNode().put("f1", "value"));
            
            // When
            worker.go();
        }
    
        // Then
        assertThat(node.get("nullNode")).isEqualTo(new ObjectMapper().createObjectNode().put("f1", "value"));
        assertThat(node.get("existingNode")).isEqualTo(new ObjectMapper().createObjectNode().put("f1", "value"));
    }
    
}
