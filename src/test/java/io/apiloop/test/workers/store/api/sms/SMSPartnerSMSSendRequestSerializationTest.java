/**
 *
 */
package io.apiloop.test.workers.store.api.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apiloop.workers.store.api.sms.SMSPartnerSMSSendRequest;
import io.apiloop.workers.store.api.sms.SMSPartnerSMSSendRequest.Formula;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 *
 */
public class SMSPartnerSMSSendRequestSerializationTest {

    private SMSPartnerSMSSendRequest request;

    @Before
    public void setUp() {
        request = new SMSPartnerSMSSendRequest()
            .setApiKey("API KEY")
            .setPhoneNumber("+33600000000")
            .setMessage("This is my message")
            .setFormula(Formula.LOW_COST)
            .setSender("Sender");
    }

    @Test
    public void shouldSerializeEnums() {
        // Given

        // When
        JsonNode node = new ObjectMapper().valueToTree(request);

        // Then
        assertThat(node.get("apiKey").asText()).isEqualTo(request.getApiKey());
        assertThat(node.get("phoneNumbers").asText()).isEqualTo(request.getPhoneNumber());
        assertThat(node.get("message").asText()).isEqualTo(request.getMessage());
        assertThat(node.get("gamme").asInt()).isEqualTo(request.getFormula().getValue());
        assertThat(node.get("sender").asText()).isEqualTo(request.getSender());
    }

}
