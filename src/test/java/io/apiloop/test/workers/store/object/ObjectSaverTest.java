/**
 * 
 */
package io.apiloop.test.workers.store.object;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.base.persistence.InMemoryDatabaseSaver;
import io.apiloop.workers.store.object.ObjectSaver;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.catchThrowable;

/**
 * 
 */
public class ObjectSaverTest {
    
    private ObjectSaver worker;
    private InMemoryDatabaseSaver entitySaver;
    private BusinessObject object;
    
    @Before
    public void setUp() {
        entitySaver = new InMemoryDatabaseSaver();
        object = new BusinessObjectImpl().setRootOwnerId(randomUUID()).setOwnerId(randomUUID());
        worker = new ObjectSaver(entitySaver).setBusinessObject(object);
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
    public void shouldSaveTheNewResource() {
        // Given
        UUID metaEntityId = randomUUID();
        ObjectNode data = new ObjectMapper().createObjectNode().put("field1", "value1").put("field2", "value2");
        worker.setBusinessObject(object).setMetaEntityId(metaEntityId).setData(data);
        
        // When
        BusinessObject savedResource = worker.go();
    
        // Then
        assertThat(entitySaver.getItems()).hasSize(1);
        assertThat(savedResource.getUuid()).isNotNull();
        assertThat(savedResource.getMetaEntityId()).isEqualTo(metaEntityId);
        assertThat(savedResource.getRootOwnerId()).isEqualTo(object.getRootOwnerId());
        assertThat(savedResource.getOwnerId()).isEqualTo(object.getOwnerId());
        assertThat(savedResource.getData()).isEqualTo(data);
        assertThat(savedResource.getEnabled()).isTrue();
        assertThat(savedResource.getCreatedAt()).isNotNull();
        assertThat(savedResource.getUpdatedAt()).isNotNull();
    }
    
}
