/**
 * 
 */
package io.apiloop.test.workers.base.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.test.workers.FixturesUtils;
import io.apiloop.workers.base.json.JsonFieldSetter;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class JsonFieldSetterTest {
    
    private JsonFieldSetter worker;
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
        
        worker = new JsonFieldSetter().setNode(node);
    }
    
    @Test
    public void withBadNode() {
        // Given
        
        // When
        worker.setNode(null).setField("name").setValue("Default Name").go();
        
        // Then
    }
    
    @Test
    public void withBadField() {
        // Given
        
        // When
        for (String value : FixturesUtils.mandatoryStringBadValues()) {
            worker.setField(value).setValue("Default value").go();
        }
        
        // Then
    }
    
    @Test
    public void withString() {
        // Given
        String newValue = "Default value";
        
        // When
        for (String field : asList("nullString", "emptyString", "existingString")) {
            worker.setField(field).setValue(newValue).go();
            assertThat(node.get(field).isTextual()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullString").asText()).isEqualTo(newValue);
        assertThat(node.get("emptyString").asText()).isEqualTo(newValue);
        assertThat(node.get("existingString").asText()).isEqualTo(newValue);
    }

    @Test
    public void withStringBeginningWithNumber() {
        // Given
        String newValue = "123 Hahaha";
        
        // When
        worker.setField("nullString").setValue(newValue).go();
        
        // Then
        assertThat(node.get("nullString").asText()).isEqualTo(newValue);
    }
    
    @Test
    public void withInteger() {
        // Given
        Integer newValue = 45;
        
        // When
        for (String field : asList("nullInteger", "existingInteger")) {
            worker.setField(field).setValue(newValue).go();
            assertThat(node.get(field).isInt()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullInteger").asInt()).isEqualTo(newValue);
        assertThat(node.get("existingInteger").asInt()).isEqualTo(newValue);
    }
    
    @Test
    public void withLong() {
        // Given
        Long newValue = 32L;
        
        // When
        for (String field : asList("nullLong", "existingLong")) {
            worker.setField(field).setValue(newValue).go();
        }
        
        // Then
        assertThat(node.get("nullLong").asLong()).isEqualTo(newValue);
        assertThat(node.get("existingLong").asLong()).isEqualTo(newValue);
    }
    
    @Test
    public void withDouble() {
        // Given
        Double newValue = 98.7;
        
        // When
        for (String field : asList("nullDouble", "existingDouble")) {
            worker.setField(field).setValue(newValue).go();
            assertThat(node.get(field).isDouble()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullDouble").asDouble()).isEqualTo(newValue);
        assertThat(node.get("existingDouble").asDouble()).isEqualTo(newValue);
    }
    
    @Test
    public void withFloat() {
        // Given
        Float newValue = 37.4F;
        
        // When
        for (String field : asList("nullFloat", "existingFloat")) {
            worker.setField(field).setValue(newValue).go();
            assertThat(node.get(field).isFloat()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullFloat").floatValue()).isEqualTo(newValue);
        assertThat(node.get("existingFloat").floatValue()).isEqualTo(newValue);
    }
    
    @Test
    public void withBoolean() {
        // Given
        Boolean newValue = false;
        
        // When
        for (String field : asList("nullBoolean", "existingTrueBoolean", "existingFalseBoolean")) {
            worker.setField(field).setValue(newValue).go();
            assertThat(node.get(field).isBoolean()).isTrue();
        }
        
        // Then
        assertThat(node.get("nullBoolean").asBoolean()).isEqualTo(newValue);
        assertThat(node.get("existingTrueBoolean").asBoolean()).isEqualTo(newValue);
        assertThat(node.get("existingFalseBoolean").asBoolean()).isEqualTo(newValue);
    }
    
    @Test
    public void withNode() {
        // Given
        ObjectNode newValue = new ObjectMapper().createObjectNode().put("f1", "value");
        
        // When
        for (String field : asList("nullNode", "existingNode")) {
            worker.setField(field).setValue(newValue).go();
        }
        
        // Then
        assertThat(node.get("nullNode")).isEqualTo(newValue);
        assertThat(node.get("existingNode")).isEqualTo(newValue);
    }
    
}
