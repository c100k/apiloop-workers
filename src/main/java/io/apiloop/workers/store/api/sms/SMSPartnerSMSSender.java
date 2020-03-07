/**
 *
 */
package io.apiloop.workers.store.api.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.Loopable;
import io.apiloop.workers.base.json.JsonFieldReader;
import io.apiloop.workers.base.ws.APICaller;
import io.apiloop.workers.base.ws.WebServiceRequest;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.base.ws.WorkerRestAPICaller;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import io.apiloop.workers.store.api.sms.SMSPartnerSMSSendRequest.Formula;
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
    id = "438235e0-a63d-43f6-8c28-7606f8ad11cd",
    name = @I18nDescriptor(
        en = "Send an SMS via SMS Partner",
        fr = "Envoyer un SMS via SMS Partner"
    ),
    description = @I18nDescriptor(
        en = "Contact your customers directly by SMS thanks to SMS Partner API",
        fr = "Contactez vos client directement par SMS grâce à l'API SMS Partner"
    ),
    externalServiceUrl = "http://www.smspartner.fr"
)
public class SMSPartnerSMSSender implements APICaller, Loopable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSPartnerSMSSender.class);

    @Getter
    private final WorkerRestAPICaller restAPICaller;

    @Getter @Setter
    private Consumer<String> onError;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Api Key", fr = "Clé API"),
        description = @I18nDescriptor(
            en = "Create an account on http://www.smspartner.fr and paste your API Key here",
            fr = "Créez un compte sur http://www.smspartner.fr et collez votre Clé API ici"
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
    public SMSPartnerSMSSender(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }
    
    @Override
    public SMSPartnerSMSSender mapParameters(ObjectNode parameters) {
        apiKey = parameters.get("apiKey").asText();
        sender = parameters.get("sender").asText();
        phoneNumber = parameters.get("phoneNumber").asText();
        message = parameters.get("message").asText();
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        SMSPartnerSMSSendRequest serviceRequest = new SMSPartnerSMSSendRequest()
            .setApiKey(apiKey)
            .setPhoneNumber(phoneNumber)
            .setMessage(message)
            .setFormula(Formula.PREMIUM)
            .setSender(sender);

        WebServiceRequest request = new WebServiceRequest()
            .setUrl("https://api.smspartner.fr/v1/send")
            .setPayloadAsJson(new ObjectMapper().valueToTree(serviceRequest));

        return restAPICaller.post(request).thenApply(this::processResponse);
    }

    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
        LOGGER.debug("Phone number : {} / Message : {} / Response : {}", phoneNumber, message, responseAsJson);

        if (!responseAsJson.get("success").asBoolean()) {
            String errorMessage = null;

            Optional<JsonNode> error = new JsonFieldReader()
                .setNode((ObjectNode) responseAsJson)
                .setField("error")
                .go()
                .getValue();

            if (error.isPresent()) {
                if (error.get().isArray()) {
                    errorMessage = error.get().get(0).get("message").asText();
                } else {
                    errorMessage = error.get().get("message").asText();
                }
            } else {
                Optional<JsonNode> responseMessage = new JsonFieldReader()
                    .setNode((ObjectNode) responseAsJson)
                    .setField("message")
                    .go()
                    .getValue();

                if (responseMessage.isPresent()) {
                    errorMessage = responseMessage.get().asText();
                }
            }

            if (Strings.isNullOrEmpty(errorMessage)) {
                errorMessage = "unknown_error";
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
