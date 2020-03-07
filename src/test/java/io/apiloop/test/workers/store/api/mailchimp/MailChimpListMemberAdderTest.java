/**
 *
 */
package io.apiloop.test.workers.store.api.mailchimp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.store.api.mailchimp.MailChimpListMemberAdder;
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
public class MailChimpListMemberAdderTest {

    private MailChimpListMemberAdder worker;
    private StubWorkerRestAPICaller restAPICaller;
    private String errorMessage;

    @Before
    public void setUp() {
        restAPICaller = new StubWorkerRestAPICaller();
        worker = new MailChimpListMemberAdder(restAPICaller)
            .setOnError(message -> errorMessage = message)
            .setApiKey("API KEY")
            .setListID("List ID")
            .setEmail("hello@apiloop.io")
            .setMergeFields(new ObjectMapper().createObjectNode());
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }

    @Test
    public void withValidResponse() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(200)
            .setExpectedResponse("{\n" +
                "\t\"id\": \"8134bcb6444d5e609616058530d798f1\",\n" +
                "\t\"email_address\": \"hello@apiloop.io\",\n" +
                "\t\"unique_email_id\": \"950bee6632\",\n" +
                "\t\"email_type\": \"html\",\n" +
                "\t\"status\": \"subscribed\",\n" +
                "\t\"merge_fields\": {\n" +
                "\t\t\"FNAME\": \"\",\n" +
                "\t\t\"LNAME\": \"\",\n" +
                "\t\t\"UUID\": \"\",\n" +
                "\t\t\"LASTUPDATE\": \"\",\n" +
                "\t\t\"ENABLED\": \"\",\n" +
                "\t\t\"ACTIVITY\": \"\"\n" +
                "\t},\n" +
                "\t\"stats\": {\n" +
                "\t\t\"avg_open_rate\": 0,\n" +
                "\t\t\"avg_click_rate\": 0\n" +
                "\t},\n" +
                "\t\"ip_signup\": \"\",\n" +
                "\t\"timestamp_signup\": \"\",\n" +
                "\t\"ip_opt\": \"178.255.96.170\",\n" +
                "\t\"timestamp_opt\": \"2016-08-03T12:13:56+00:00\",\n" +
                "\t\"member_rating\": 2,\n" +
                "\t\"last_changed\": \"2016-08-03T12:13:56+00:00\",\n" +
                "\t\"language\": \"\",\n" +
                "\t\"vip\": false,\n" +
                "\t\"email_client\": \"\",\n" +
                "\t\"location\": {\n" +
                "\t\t\"latitude\": 0,\n" +
                "\t\t\"longitude\": 0,\n" +
                "\t\t\"gmtoff\": 0,\n" +
                "\t\t\"dstoff\": 0,\n" +
                "\t\t\"country_code\": \"\",\n" +
                "\t\t\"timezone\": \"\"\n" +
                "\t},\n" +
                "\t\"list_id\": \"a2196339d1\",\n" +
                "\t\"_links\": []" +
                "}");

        // When
        WebServiceResponse response = worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(response.asJson().get("unique_email_id").asText()).isNotEmpty();
    }

    @Test
    public void withSimpleError() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(400)
            .setExpectedResponse("{\"type\":\"http://developer.mailchimp.com/documentation/mailchimp/guides/error-glossary/\",\"title\":\"Member Exists\",\"status\":400,\"detail\":\" is already a list member. Use PUT to insert or update list members.\",\"instance\":\"\"}");

        // When
        worker.go().toCompletableFuture().get(5L, SECONDS);

        // Then
        assertThat(errorMessage).isEqualTo(" is already a list member. Use PUT to insert or update list members.");
    }

    @Test
    public void withMultipleErrors() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(400)
            .setExpectedResponse("{\n" +
                "  \"type\": \"http://developer.mailchimp.com/documentation/mailchimp/guides/error-glossary/\",\n" +
                "  \"title\": \"Invalid Resource\",\n" +
                "  \"status\": 400,\n" +
                "  \"detail\": \"The resource submitted could not be validated. For field-specific details, see the 'errors' array.\",\n" +
                "  \"instance\": \"\",\n" +
                "  \"errors\": [\n" +
                "    {\n" +
                "      \"field\": \"status\",\n" +
                "      \"message\": \"Data presented is not one of the accepted values: subscribed, unsubscribed, cleaned, pending.\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        // When
        worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(errorMessage).isEqualTo("Data presented is not one of the accepted values: subscribed, unsubscribed, cleaned, pending.");
    }
    
}
