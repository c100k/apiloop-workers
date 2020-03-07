/**
 * 
 */
package io.apiloop.test.workers.base.text;

import io.apiloop.test.workers.FixturesUtils;
import io.apiloop.workers.base.text.Slugifier;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class SlugifierTest {
    
    private Slugifier worker;
    
    @Before
    public void setUp() {
        worker = new Slugifier();
    }
    
    @Test
    public void withBadValue() {
        for (String value : FixturesUtils.mandatoryStringBadValues()) {
            // Given
            worker.setValue(value);
            
            // When
            String slug = worker.go().getSlug();
            
            // Then
            assertThat(slug).isEqualTo("");
        }
    }
    
    @Test
    public void withNormalValue() {
        // Given
        worker.setValue("A text to slugify ! 999 ?!");
        
        // When
        String slug = worker.go().getSlug();
        
        // Then
        assertThat(slug).isEqualTo("a-text-to-slugify-999");
    }
    
}
