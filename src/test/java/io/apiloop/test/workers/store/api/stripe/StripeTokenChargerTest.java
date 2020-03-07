/**
 *
 */
package io.apiloop.test.workers.store.api.stripe;

import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.base.BusinessObjectPricingImpl;
import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.store.api.stripe.StripeTokenCharger;
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
public class StripeTokenChargerTest {
    
    private StripeTokenCharger worker;
    private StubWorkerRestAPICaller restAPICaller;
    private BusinessObjectImpl businessObject;
    private String onErrorMessage;
    
    @Before
    public void setUp() {
        restAPICaller = new StubWorkerRestAPICaller();
        businessObject = new BusinessObjectImpl().setPricing(new BusinessObjectPricingImpl());
        worker = new StripeTokenCharger(restAPICaller)
            .setOnError(message -> onErrorMessage = message)
            .setBusinessObject(businessObject)
            .setApiKey("My API Key")
            .setDescription("My first charge")
            .setSource("st_123456");
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }
    
    @Test
    public void withValidCharge() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        String chargeId = "ch_196bd62eZvKYlo2CJeWhdTyv";
        restAPICaller
            .setExpectedStatus(200)
            .setExpectedResponse("{\n" +
            "  \"id\": \"" + chargeId + "\",\n" +
            "  \"object\": \"charge\",\n" +
            "  \"amount\": 7140,\n" +
            "  \"amount_refunded\": 0,\n" +
            "  \"application_fee\": null,\n" +
            "  \"balance_transaction\": \"txn_18tm9B2eZvKYlo2C5vs7CPrL\",\n" +
            "  \"captured\": true,\n" +
            "  \"created\": 1476951024,\n" +
            "  \"currency\": \"usd\",\n" +
            "  \"customer\": \"cus_9M4MMU05hXUm75\",\n" +
            "  \"description\": \"Purchase of 10000 api credits.\",\n" +
            "  \"destination\": null,\n" +
            "  \"dispute\": null,\n" +
            "  \"failure_code\": null,\n" +
            "  \"failure_message\": null,\n" +
            "  \"fraud_details\": {\n" +
            "  },\n" +
            "  \"invoice\": null,\n" +
            "  \"livemode\": false,\n" +
            "  \"metadata\": {\n" +
            "  },\n" +
            "  \"order\": null,\n" +
            "  \"outcome\": {\n" +
            "    \"network_status\": \"approved_by_network\",\n" +
            "    \"reason\": null,\n" +
            "    \"risk_level\": \"normal\",\n" +
            "    \"seller_message\": \"Payment complete.\",\n" +
            "    \"type\": \"authorized\"\n" +
            "  },\n" +
            "  \"paid\": true,\n" +
            "  \"receipt_email\": null,\n" +
            "  \"receipt_number\": null,\n" +
            "  \"refunded\": false,\n" +
            "  \"refunds\": {\n" +
            "    \"object\": \"list\",\n" +
            "    \"data\": [\n" +
            "\n" +
            "    ],\n" +
            "    \"has_more\": false,\n" +
            "    \"total_count\": 0,\n" +
            "    \"url\": \"/v1/charges/ch_196bd62eZvKYlo2CJeWhdTyv/refunds\"\n" +
            "  },\n" +
            "  \"shipping\": null,\n" +
            "  \"source\": {\n" +
            "    \"id\": \"card_193MDI2eZvKYlo2Cik4c97Vi\",\n" +
            "    \"object\": \"card\",\n" +
            "    \"address_city\": null,\n" +
            "    \"address_country\": null,\n" +
            "    \"address_line1\": null,\n" +
            "    \"address_line1_check\": null,\n" +
            "    \"address_line2\": null,\n" +
            "    \"address_state\": null,\n" +
            "    \"address_zip\": null,\n" +
            "    \"address_zip_check\": null,\n" +
            "    \"brand\": \"MasterCard\",\n" +
            "    \"country\": \"US\",\n" +
            "    \"customer\": \"cus_9M4MMU05hXUm75\",\n" +
            "    \"cvc_check\": null,\n" +
            "    \"dynamic_last4\": null,\n" +
            "    \"exp_month\": 1,\n" +
            "    \"exp_year\": 2020,\n" +
            "    \"funding\": \"credit\",\n" +
            "    \"last4\": \"4444\",\n" +
            "    \"metadata\": {\n" +
            "    },\n" +
            "    \"name\": \"Tester\",\n" +
            "    \"tokenization_method\": null\n" +
            "  },\n" +
            "  \"source_transfer\": null,\n" +
            "  \"statement_descriptor\": null,\n" +
            "  \"status\": \"succeeded\"\n" +
            "}");
        
        // When
        worker.go().toCompletableFuture().get(5, SECONDS);
        
        // Then
        assertThat(businessObject.getPricing().getSales().intValue()).isEqualTo(1);
        assertThat(businessObject.getPricing().getPaymentReferences()).hasSize(1);
        assertThat(businessObject.getPricing().getPaymentReferences().contains(chargeId));
    }
    
    @Test
    public void withError() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller
            .setExpectedStatus(403)
            .setExpectedResponse("{\n" +
                "  \"error\": {\n" +
                "    \"type\": \"invalid_request_error\",\n" +
                "    \"message\": \"Invalid API Key provided: sk_test_*****************H4ol\"\n" +
                "  }\n" +
                "}");
        
        // When
        worker.go().toCompletableFuture().get(5, SECONDS);
        
        // Then
        assertThat(onErrorMessage).isEqualTo("Invalid API Key provided: sk_test_*****************H4ol");
        assertThat(businessObject.getPricing().getSales()).isNull();
        assertThat(businessObject.getPricing().getPaymentReferences()).isNull();
    }
    
}
