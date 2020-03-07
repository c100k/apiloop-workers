/**
 * 
 */
package io.apiloop.test.workers.base.crypt;

import io.apiloop.workers.base.crypt.BCryptChecker;
import io.apiloop.workers.base.crypt.BCryptHasher;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * 
 */
public class BCryptHasherAndCheckerTest {

    private BCryptHasher worker;

    @Before
    public void setUp() {
        worker = new BCryptHasher();
    }

    @Test
    public void shouldMatchClearValue() {
        // Given
        String clearValue = "my password";

        // When
        String hash = worker.value(clearValue).go().hash();
        Boolean valuesMatch = new BCryptChecker().value(clearValue).hash(hash).go().valuesMatch();

        // Then
        assertThat(valuesMatch).isTrue();
    }
    
}
