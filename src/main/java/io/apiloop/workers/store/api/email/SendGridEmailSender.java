/**
 *
 */
package io.apiloop.workers.store.api.email;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "2084359b-3300-4eee-89b5-02ef3fb8a917",
    name = @I18nDescriptor(
        en = "Send an email via SendGrid",
        fr = "Envoyer un email via SendGrid"
    ),
    description = @I18nDescriptor(
        en = "Contact your customers directly by email thanks to SendGrid API",
        fr = "Contactez vos client directement par email grâce à l'API SendGrid"
    ),
    externalServiceUrl = "https://www.sendgrid.com"
)
public class SendGridEmailSender implements APICaller, Loopable {

    @Getter
    private final WorkerRestAPICaller restAPICaller;

    @Getter @Setter
    private Consumer<String> onError;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Api Key", fr = "Clé API"),
        description = @I18nDescriptor(
            en = "Create an account on http://www.sendgrid.com and paste your API Key here",
            fr = "Créez un compte sur http://www.sendgrid.com et collez votre Clé API ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String apiKey;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Sender email", fr = "Email de l'émetteur"),
        description = @I18nDescriptor(en = "Example : no-reply@apiloop.io", fr = "Exemple : no-reply@apiloop.io"),
        type = "StringType",
        htmlFormFieldType = "email",
        mandatory = true
    )
    private String from;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Recipient email", fr = "Email du ou des destinataires"),
        description = @I18nDescriptor(en = "Example : me@apiloop.io or {{attributeContainingResourceEmail}}", fr = "Exemple : me@apiloop.io ou {{attributeContenantEmailDeLaRessource}}"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String to;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Email subject", fr = "Objet de l'email"),
        description = @I18nDescriptor(en = "Example : Hello !", fr = "Exemple : Salut !"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String subject;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Email content", fr = "Contenu de l'email"),
        description = @I18nDescriptor(en = "Example : Hello ! How are you ?", fr = "Exemple : Salut ! Comment ça va ?"),
        type = "StringType",
        htmlFormFieldType = "textarea",
        mandatory = true
    )
    private String content;

    @Inject
    public SendGridEmailSender(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }
    
    @Override
    public SendGridEmailSender mapParameters(ObjectNode parameters) {
        apiKey = parameters.get("apiKey").asText();
        from = parameters.get("from").asText();
        to = parameters.get("to").asText();
        subject = parameters.get("subject").asText();
        content = parameters.get("content").asText().replaceAll("\\n", "<br>");
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        WebServiceRequest request = new WebServiceRequest()
            .setUrl("https://api.sendgrid.com/v3/mail/send")
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .setPayloadAsJson(new ObjectMapper().valueToTree(new SendGridEmailSenderRequest(to, from, subject, content)));

        return restAPICaller.post(request).thenApply(this::processResponse);
    }

    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
    
        if (response.getStatus() >= 400) {
            String errorMessage = null;
        
            Optional<JsonNode> error = new JsonFieldReader()
                .setNode((ObjectNode) responseAsJson)
                .setField("errors")
                .go()
                .getValue();
        
            if (error.isPresent() && error.get().isArray()) {
                errorMessage = error.get().get(0).get("message").asText();
            }
        
            onError.accept(errorMessage);
        }

        return response;
    }

    @Override
    public String getLoopableField() {
        return "to";
    }

}
