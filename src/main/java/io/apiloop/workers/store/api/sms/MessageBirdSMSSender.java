/**
 *
 */
package io.apiloop.workers.store.api.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.Loopable;
import io.apiloop.workers.base.json.JsonFieldReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "16887e25-1ce6-46fa-b2fa-3fc35b002d5b",
    name = @I18nDescriptor(
        en = "Send an SMS via MessageBird",
        fr = "Envoyer un SMS via MessageBird"
    ),
    description = @I18nDescriptor(
        en = "Contact your customers directly by SMS thanks to MessageBird API",
        fr = "Contactez vos client directement par SMS grâce à l'API MessageBird"
    ),
    externalServiceUrl = "https://www.messagebird.com"
)
public class MessageBirdSMSSender implements APICaller, Loopable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBirdSMSSender.class);

    @Getter
    private final WorkerRestAPICaller restAPICaller;

    @Getter @Setter
    private Consumer<String> onError;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Api Key", fr = "Clé API"),
        description = @I18nDescriptor(
            en = "Create an account on http://www.messagebird.com and paste your API Key here",
            fr = "Créez un compte sur http://www.messagebird.com et collez votre Clé API ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String apiKey;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Sender", fr = "Émetteur"),
        description = @I18nDescriptor(
            en = "If left empty, the SMS are sent with a carrier shortcode like 36608. Between 3 and 11 characters without special characters nor spaces",
            fr = "Si laissé vide, vos SMS sont envoyés avec un shortcode opérateur comme 36608. Entre 3 et 11 caractères sans caractères spéciaux et espaces"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String sender;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Recipient", fr = "Destinataire"),
        description = @I18nDescriptor(en = "Phone number of the recipient", fr = "Numéro de téléphone du destinataire"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String phoneNumber;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Message", fr = "Message"),
        description = @I18nDescriptor(en = "160-character message", fr = "Message de 160 caractères"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String message;

    @Inject
    public MessageBirdSMSSender(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }
    
    @Override
    public MessageBirdSMSSender mapParameters(ObjectNode parameters) {
        apiKey = parameters.get("apiKey").asText();
        sender = parameters.get("sender").asText();
        phoneNumber = parameters.get("phoneNumber").asText();
        message = parameters.get("message").asText();
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        WebServiceRequest request = new WebServiceRequest()
            .setUrl("https://rest.messagebird.com/messages")
            .addHeader("Authorization", "AccessKey " + apiKey)
            .addFormUrlEncodedParameter("originator", sender)
            .addFormUrlEncodedParameter("recipients", phoneNumber)
            .addFormUrlEncodedParameter("body", message);

        return restAPICaller.post(request).thenApply(this::processResponse);
    }

    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
        LOGGER.debug("Phone number : {} / Message : {} / Response : {}", phoneNumber, message, responseAsJson);

        if (response.getStatus() != 201) {
            String errorMessage = null;

            Optional<JsonNode> error = new JsonFieldReader()
                .setNode((ObjectNode) responseAsJson)
                .setField("errors")
                .go()
                .getValue();

            if (error.isPresent() && error.get().isArray()) {
                errorMessage = error.get().get(0).get("description").asText();
            }

            onError.accept(errorMessage);
        }

        return response;
    }

    @Override
    public String getLoopableField() {
        return "phoneNumber";
    }

}
