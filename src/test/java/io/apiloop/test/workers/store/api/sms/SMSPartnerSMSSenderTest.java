/**
 *
 */
package io.apiloop.test.workers.store.api.sms;

import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.store.api.sms.SMSPartnerSMSSender;
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
public class SMSPartnerSMSSenderTest {

    private SMSPartnerSMSSender worker;
    private StubWorkerRestAPICaller restAPICaller;
    private String errorMessage;

    @Before
    public void setUp() {
        restAPICaller = new StubWorkerRestAPICaller();
        worker = new SMSPartnerSMSSender(restAPICaller)
            .setOnError(message -> errorMessage = message)
            .setApiKey("API KEY")
            .setPhoneNumber("+33600000000")
            .setMessage("This is a test")
            .setSender("DEMOSMS");
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }

    @Test
    public void withValidResponse() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller.setExpectedResponse("{\n" +
            "   \"success\":true,\n" +
            "   \"code\":200,\n" +
            "   \"message_id\":307\n" +
            "}"
        );

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.asJson().get("success").asBoolean()).isTrue();
    }

    @Test
    public void withSimpleMessageError() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller.setExpectedResponse("{\"success\":false,\"code\":10,\"message\":\"Clef API incorrecte\"}");

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.asJson().get("success").asBoolean()).isFalse();
        assertThat(errorMessage).isEqualTo("Clef API incorrecte");
    }

    @Test
    public void withComplexMessageError() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller.setExpectedResponse("{\"success\":false,\"code\":11,\"error\":{\"message\":\"NOCREDITS\"},\"credits\":{\"creditSms\":0,\"creditSmsLowCost\":0,\"toSend\":0,\"solde\":0}}");

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.asJson().get("success").asBoolean()).isFalse();
        assertThat(errorMessage).isEqualTo("NOCREDITS");
    }

    @Test
    public void withBadRequestError() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller.setExpectedResponse("{\n" +
            "    \"success\": false,\n" +
            "    \"code\":9,\n" +
            "    \"error\": [{\n" +
            "        \"elementId\": \"children[message].data\",\n" +
            "        \"message\": \"Le message est requis\"\n" +
            "    }, {\n" +
            "        \"elementId\": \"children[phoneNumbers].data\",\n" +
            "        \"message\": \"Ce numero de telephone n'est pas valide (922264)\"\n" +
            "    }, {\n" +
            "        \"elementId\": \"children[sender].data\",\n" +
            "        \"message\": \"L'emetteur ne peut pas etre plus long que 11 caracteres\"\n" +
            "    }, {\n" +
            "        \"elementId\": \"children[scheduledDeliveryDate].data\",\n" +
            "        \"message\": \"La date (21/11/2014 \\u00e0 :) est anterieure a la date actuelle.\"\n" +
            "    }, {\n" +
            "        \"elementId\": \"children[minute].data\",\n" +
            "        \"message\": \"La minute est requise\"\n" +
            "    }, {\n" +
            "        \"elementId\": \"children[time].data\",\n" +
            "        \"message\": \"L'heure est requise\"\n" +
            "    }]\n" +
            "}");

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.asJson().get("success").asBoolean()).isFalse();
        assertThat(errorMessage).isEqualTo("Le message est requis");
    }
    
}
