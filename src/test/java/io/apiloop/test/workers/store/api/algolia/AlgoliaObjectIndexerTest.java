/**
 *
 */
package io.apiloop.test.workers.store.api.algolia;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.store.api.algolia.AlgoliaIndexerInMemory;
import io.apiloop.workers.store.api.algolia.AlgoliaObjectIndexer;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 *
 */
public class AlgoliaObjectIndexerTest {

    private AlgoliaObjectIndexer worker;
    private AlgoliaIndexerInMemory algoliaIndexer;
    private String errorMessage;

    @Before
    public void setUp() {
        StubWorkerRestAPICaller restAPICaller = new StubWorkerRestAPICaller();
        algoliaIndexer = new AlgoliaIndexerInMemory();
        worker = new AlgoliaObjectIndexer(restAPICaller, algoliaIndexer)
            .setOnError(message -> errorMessage = message)
            .setBusinessObject(new BusinessObjectImpl()
                .setUuid(randomUUID())
                .setData(new ObjectMapper().createObjectNode()
                    .put("firstName", "Dexter")
                    .put("lastName", "Morgan")
                )
            )
            .setApplicationId("myApplicationID")
            .setApiKey("myApiKey")
            .setIndexName("my-first-index");
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }

    @Test
    public void withValidResponse() throws InterruptedException, ExecutionException, TimeoutException {
        // Given

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.asJson().get("indexName").asText()).isEqualTo(worker.getIndexName());
        assertThat(response.asJson().get("objectID").asText()).isEqualTo(worker.getBusinessObject().getUuid().toString());
        assertThat(response.asJson().get("taskID").asLong()).isGreaterThan(0);
    }

    @Test
    public void withBadApplicationID() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        algoliaIndexer.expectedIllegalArgumentExceptionMessage("Failed to query host");

        // When
        worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(errorMessage).isEqualTo("Invalid application ID");
    }

    @Test
    public void withBadFormatApplicationID() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        algoliaIndexer.expectedIllegalArgumentExceptionMessage("Illegal character in authority");

        // When
        worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(errorMessage).isEqualTo("Invalid application ID");
    }
    
}
