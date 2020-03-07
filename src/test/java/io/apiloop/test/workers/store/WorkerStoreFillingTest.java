/**
 *
 */
package io.apiloop.test.workers.store;

import io.apiloop.workers.store.WorkerStore;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class WorkerStoreFillingTest {

    @Test
    public void shouldFillStore() {
        // Given
        WorkerStore store = new WorkerStore();

        // When
        store.fill();

        // Then
        assertThat(store.getForApplication(null)).hasSize(0);
        assertThat(store.getForApplication(UUID.randomUUID())).hasSize(14);
        assertThat(store.getForApplication(UUID.fromString("5ac6d8ff-9807-4907-abf9-69d9f71cdff3"))).hasSize(15);
        assertThat(store.getAll()).hasSize(15);
    }

}
