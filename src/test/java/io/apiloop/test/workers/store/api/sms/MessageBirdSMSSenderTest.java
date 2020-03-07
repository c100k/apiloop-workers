/**
 *
 */
package io.apiloop.test.workers.store.api.sms;

import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.store.api.sms.MessageBirdSMSSender;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 *
 */
public class MessageBirdSMSSenderTest {

    private MessageBirdSMSSender worker;
    private StubWorkerRestAPICaller restAPICaller;
    private String errorMessage;

    @Before
    public void setUp() {
        restAPICaller = new StubWorkerRestAPICaller();
        worker = new MessageBirdSMSSender(restAPICaller)
            .setOnError(message -> errorMessage = message)
            .setApiKey("API KEY")
            .setPhoneNumber("33600000000")
            .setMessage("This is a test")
            .setSender("Apiloop Test");
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }

    @Test
    public void withValidResponse() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(201)
            .setExpectedResponse("{\"id\":\"faf0f714557a0a83764f807b94875505\",\"href\":\"https://rest.messagebird.com/messages/faf0f714557a0a83764f807b94875505\",\"direction\":\"mt\",\"type\":\"sms\",\"originator\":\"Apiloop\",\"body\":\"This is a message\",\"reference\":null,\"validity\":null,\"gateway\":2,\"typeDetails\":{},\"datacoding\":\"plain\",\"mclass\":1,\"scheduledDatetime\":null,\"createdDatetime\":\"2016-08-02T14:03:35+00:00\",\"recipients\":{\"totalCount\":1,\"totalSentCount\":1,\"totalDeliveredCount\":0,\"totalDeliveryFailedCount\":0,\"items\":[{\"recipient\":31612345678,\"status\":\"sent\",\"statusDatetime\":\"2016-08-02T14:03:35+00:00\"}]}}");

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.asJson().get("createdDatetime").asText()).isNotEmpty();
    }

    @Test
    public void withError() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(402)
            .setExpectedResponse("{\"errors\":[{\"code\":9,\"description\":\"no (correct) recipients found\",\"parameter\":\"recipients\"}]}");

        // When
        worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(errorMessage).isEqualTo("no (correct) recipients found");
    }
    
}
