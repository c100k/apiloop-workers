/**
 * 
 */
package io.apiloop.test.workers.store.object;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.base.crypt.BCryptChecker;
import io.apiloop.workers.store.object.ObjectFieldsBCryptHasher;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class ObjectFieldsBCryptHasherTest {

    private ObjectFieldsBCryptHasher worker;
    private ObjectNode node;
    private List<String> fields;

    @Before
    public void setUp() {
        worker = new ObjectFieldsBCryptHasher();
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }

    @Test
    public void withNullNode() {
        // Given
        fields = singletonList("field1");

        // When
        worker.setBusinessObject(new BusinessObjectImpl().setData(node)).setFields(fields).go();

        // Then
    }

    @Test
    public void withNullFields() {
        // Given
        node = new ObjectMapper().createObjectNode();

        // When
        worker.setBusinessObject(new BusinessObjectImpl().setData(node)).setFields(fields).go();

        // Then
        assertThat(node).isEqualTo(worker.getBusinessObject().getData());
    }

    @Test
    public void shouldHashOnlyDesiredFields() {
        // Given
        node = new ObjectMapper().createObjectNode()
            .put("name", "Dexter")
            .put("age", 32)
            .put("password", "the dark passenger")
            .put("doorCode", "456");
        fields = asList("password", "doorCode");

        // When
        worker.setBusinessObject(new BusinessObjectImpl().setData(node)).setFields(fields).go();

        // Then
        assertThat(node.get("name").asText()).isEqualTo("Dexter");
        assertThat(node.get("age").asInt()).isEqualTo(32);
        assertThatFieldHashed(node, "password", "the dark passenger");
        assertThatFieldHashed(node, "doorCode", "456");
    }

    private void assertThatFieldHashed(ObjectNode node, String field, String initialValue) {
        assertThat(node.get(field).asText()).startsWith("$");
        assertThat(new BCryptChecker()
            .value(initialValue)
            .hash(node.get(field).asText())
            .go()
            .valuesMatch()
        ).isTrue();
    }
    
}
