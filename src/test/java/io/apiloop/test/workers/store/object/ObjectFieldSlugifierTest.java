/**
 * 
 */
package io.apiloop.test.workers.store.object;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.test.workers.FixturesUtils;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.store.object.ObjectFieldSlugifier;
import org.junit.Before;
import org.junit.Test;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class ObjectFieldSlugifierTest {
    
    private ObjectFieldSlugifier worker;
    private ObjectNode node;
    
    @Before
    public void setUp() {
        worker = new ObjectFieldSlugifier();
        node = new ObjectMapper().createObjectNode()
            .put("name", "My super blog article n° 1 !")
            .put("likes", 35);
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
            .setSourceField("name")
            .setDestinationField("slug");
        
        // When
        worker.go();
        
        // Then
    }
    
    @Test
    public void withBadSourceField() {
        for (String value : FixturesUtils.mandatoryStringBadValues()) {
            // Given
            worker
                .setBusinessObject(new BusinessObjectImpl().setData(node))
                .setSourceField(value)
                .setDestinationField("slug");
            
            // When
            worker.go();
    
            // Then
        }
    }
    
    @Test
    public void withBadDestinationField() {
        for (String value : FixturesUtils.mandatoryStringBadValues()) {
            // Given
            worker
                .setBusinessObject(new BusinessObjectImpl().setData(node))
                .setSourceField("name")
                .setDestinationField(value);
            
            // When
            worker.go();
            
            // Then
        }
    }
    
    @Test
    public void withInexistingSourceField() {
        // Given
        worker
            .setBusinessObject(new BusinessObjectImpl().setData(node))
            .setSourceField("field")
            .setDestinationField("slug");
        
        // When
        worker.go();
        
        // Then
        assertThat(node.get("slug")).isNull();
    }
    
    @Test
    public void withNonTextualSourceField() {
        // Given
        worker
            .setBusinessObject(new BusinessObjectImpl().setData(node))
            .setSourceField("likes")
            .setDestinationField("slug");
        
        // When
        worker.go();
        
        // Then
        assertThat(node.get("slug")).isNull();
    }
    
    @Test
    public void withValidData() {
        // Given
        worker
            .setBusinessObject(new BusinessObjectImpl().setData(node))
            .setSourceField("name")
            .setDestinationField("slug");
        
        // When
        worker.go();
        
        // Then
        assertThat(node.get("slug").asText()).isEqualTo("my-super-blog-article-n-1");
    }
    
}
