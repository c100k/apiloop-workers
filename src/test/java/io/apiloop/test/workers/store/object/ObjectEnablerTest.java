/**
 * 
 */
package io.apiloop.test.workers.store.object;

import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.store.object.ObjectEnabler;
import org.junit.Before;
import org.junit.Test;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.catchThrowable;

/**
 * 
 */
public class ObjectEnablerTest {
    
    private ObjectEnabler worker;
    private BusinessObject object;
    
    @Before
    public void setUp() {
        worker = new ObjectEnabler();
        object = new BusinessObjectImpl().setEnabled(true);
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }
    
    @Test
    public void withBadObject() {
        // Given
        worker.setBusinessObject(null);
    
        // When
        Throwable thrown = catchThrowable(() -> worker.go());
    
        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("objectMandatory");
    }
    
    @Test
    public void withEnabledNull() {
        // Given
        worker.setBusinessObject(object.setEnabled(null));
    
        // When
        Boolean changed = worker.go();
    
        // Then
        assertThat(changed).isTrue();
        assertThat(object.getEnabled()).isTrue();
    }
    
    @Test
    public void withEnabledFalse() {
        // Given
        worker.setBusinessObject(object.setEnabled(false));
    
        // When
        Boolean changed = worker.go();
    
        // Then
        assertThat(changed).isTrue();
        assertThat(object.getEnabled()).isTrue();
    }
    
    @Test
    public void withEnabledTrue() {
        // Given
        worker.setBusinessObject(object.setEnabled(true));
    
        // When
        Boolean changed = worker.go();
    
        // Then
        assertThat(changed).isFalse();
        assertThat(object.getEnabled()).isTrue();
    }
    
}
