/**
 *
 */
package io.apiloop.test.workers.store.api.email;

import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.store.api.email.SendGridEmailSender;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class SendGridEmailSenderTest {
    
    private SendGridEmailSender worker;
    private StubWorkerRestAPICaller restAPICaller;
    private String errorMessage;
    
    @Before
    public void setUp() {
        restAPICaller = new StubWorkerRestAPICaller();
        worker = new SendGridEmailSender(restAPICaller)
            .setOnError(message -> errorMessage = message)
            .setApiKey("API KEY")
            .setTo("to@apiloop.io")
            .setFrom("from@apiloop.io")
            .setSubject("This is an email from me")
            .setContent("Hello, here is the content of the email");
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }
    
    @Test
    public void withBadRequest() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(400)
            .setExpectedResponse("{\"errors\":[{\"message\":\"Bad Request\",\"field\":null,\"help\":null}]}");
        
        // When
        worker.go().toCompletableFuture().get(5, SECONDS);
        
        // Then
        assertThat(errorMessage).isEqualTo("Bad Request");
    }
    
    @Test
    public void withBadApiKey() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(401)
            .setExpectedResponse("{\"errors\":[{\"message\":\"The provided authorization grant is invalid, expired, or revoked\",\"field\":null,\"help\":null}]}");
        
        // When
        worker.go().toCompletableFuture().get(5, SECONDS);
        
        // Then
        assertThat(errorMessage).isEqualTo("The provided authorization grant is invalid, expired, or revoked");
    }
    
}
