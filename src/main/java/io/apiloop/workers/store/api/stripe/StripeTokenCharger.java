/**
 *
 */
package io.apiloop.workers.store.api.stripe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.ws.APICaller;
import io.apiloop.workers.base.ws.WebServiceRequest;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.base.ws.WorkerRestAPICaller;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "b23fa17b-a1d8-49c4-9c44-c8031fd3d104",
    name = @I18nDescriptor(
        en = "Charge a credit card with Stripe",
        fr = "Charger une carte de crédit avec Stripe"
    ),
    description = @I18nDescriptor(
        en = "Use Stripe to integrate credit card payment to your service",
        fr = "Utilisez Stripe pour intégrer le paiement par carte bleue à votre service"
    ),
    externalServiceUrl = "https://www.stripe.com"
)
public class StripeTokenCharger implements APICaller, BusinessObjectHandler {
    
    @Getter
    private final WorkerRestAPICaller restAPICaller;
    
    @Getter @Setter
    private Consumer<String> onError;
    
    @Getter @Setter
    private BusinessObject businessObject;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Api Key", fr = "Clé API"),
        description = @I18nDescriptor(
            en = "Create an account on http://www.stripe.com and paste your secret API Key here",
            fr = "Créez un compte sur http://www.stripe.com et collez votre Clé API secrète ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String apiKey;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Description", fr = "Description"),
        description = @I18nDescriptor(
            en = "Optional description that will be displayed in your Stripe backoffice",
            fr = "Description optionnelle qui sera affichée dans votre backoffice Stripe"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = false
    )
    private String description;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Stripe.js token", fr = "Jeton Stripe.js"),
        description = @I18nDescriptor(
            en = "This value is sent by your frontend using Stripe.js in the context of the workflow (usually {{context.id}})",
            fr = "Cette valeur est envoyée par votre frontend utilisant Stripe.js dans le contexte du workflow (usuellement {{context.id}})"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String source;
    
    @Inject
    public StripeTokenCharger(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }
    
    @Override
    public StripeTokenCharger mapParameters(ObjectNode parameters) {
        apiKey = parameters.get("apiKey").asText();
        description = parameters.get("description").asText();
        source = parameters.get("source").asText();
        return this;
    }
    
    @Override
    public StripeTokenCharger assertWellConfigured() {
        Assert.notNull(businessObject);
        return this;
    }
    
    @Override
    public CompletionStage<WebServiceResponse> execute() {
        WebServiceRequest request = new WebServiceRequest()
            .setUrl("https://api.stripe.com/v1/charges")
            .addHeader("Authorization", "Bearer " + apiKey)
            .addFormUrlEncodedParameter("amount", businessObject.getPricing().getAtiPrice())
            .addFormUrlEncodedParameter("currency", businessObject.getPricing().getCurrency())
            .addFormUrlEncodedParameter("description", description)
            .addFormUrlEncodedParameter("source", source);
    
        return restAPICaller.post(request).thenApply(this::processResponse);
    }
    
    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
        
        if (response.getStatus() == 200) {
            businessObject.getPricing().addSale(responseAsJson.get("id").asText());
        } else {
            String errorMessage = responseAsJson.get("error").get("message").asText();
            onError.accept(errorMessage);
        }
        
        return response;
    }
    
}
