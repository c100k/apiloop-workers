/**
 *
 */
package io.apiloop.workers.store.api.mailchimp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.json.JsonFieldReader;
import io.apiloop.workers.base.ws.APICaller;
import io.apiloop.workers.base.ws.WebServiceRequest;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.base.ws.WorkerRestAPICaller;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import io.apiloop.workers.store.api.mailchimp.MailChimpAddMemberRequest.Status;
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
    id = "e66c35f3-e88a-44d2-8b07-23b2ae91bc56",
    name = @I18nDescriptor(
        en = "Add a member to a MailChimp list",
        fr = "Ajouter un membre à une liste MailChimp"
    ),
    description = @I18nDescriptor(
        en = "Add your users after registration automatically to a specific list",
        fr = "Ajoutez vos utilisateurs après inscription automatiquement à une liste spécifique"
    ),
    externalServiceUrl = "http://mailchimp.com"
)
public class MailChimpListMemberAdder implements APICaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailChimpListMemberAdder.class);

    @Getter
    private final WorkerRestAPICaller restAPICaller;

    @Getter @Setter
    private Consumer<String> onError;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Api Key", fr = "Clé API"),
        description = @I18nDescriptor(
            en = "Create an account on http://mailchimp.com and paste your API Key here",
            fr = "Créez un compte sur http://mailchimp.com et collez votre Clé API ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String apiKey;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "List ID", fr = "ID de la liste"),
        description = @I18nDescriptor(
            en = "Unique identifier of your list in MailChimp",
            fr = "Identifiant unique de votre liste dans MailChimp"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String listID;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Email address", fr = "Adresse email"),
        description = @I18nDescriptor(
            en = "Email address of the user to add to the list",
            fr = "Adresse email de l'utilisateur à ajouter à la liste"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String email;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Merge fields", fr = "Champs supplémentaires"),
        description = @I18nDescriptor(
            en = "Data of the user to map with merge tags in MailChimp like the firstname, lastname, age...",
            fr = "Données de l'utilisateur à mapper avec les merge tags dans MailChimp comme le prénom, le nom, l'âge..."
        ),
        type = "MapType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private ObjectNode mergeFields;

    @Inject
    public MailChimpListMemberAdder(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }
    
    @Override
    public MailChimpListMemberAdder mapParameters(ObjectNode parameters) {
        apiKey = parameters.get("apiKey").asText();
        listID = parameters.get("listID").asText();
        email = parameters.get("email").asText();
        mergeFields = ((ObjectNode) parameters.get("mergeFields"));
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        MailChimpAddMemberRequest serviceRequest = new MailChimpAddMemberRequest()
            .setStatus(Status.SUBSCRIBED)
            .setEmailAddress(email)
            .setMergeFields(mergeFields);

        WebServiceRequest request = new WebServiceRequest()
            .setUrl("https://us8.api.mailchimp.com/3.0/lists/" + listID + "/members")
            .addHeader("Authorization", "apikey " + apiKey)
            .setPayloadAsJson(new ObjectMapper().valueToTree(serviceRequest));

        return restAPICaller.post(request).thenApply(this::processResponse);
    }

    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
        LOGGER.debug("List : {} / Email : {} / Merge fields : {}, Response : {}", listID, email, mergeFields, responseAsJson);

        if (response.getStatus() != 200) {
            String errorMessage = null;

            Optional<JsonNode> error = new JsonFieldReader()
                .setNode((ObjectNode) responseAsJson)
                .setField("errors")
                .go()
                .getValue();

            if (error.isPresent()) {
                if (error.get().isArray()) {
                    errorMessage = error.get().get(0).get("message").asText();
                }
            } else {
                errorMessage = responseAsJson.get("detail").asText();
            }

            onError.accept(errorMessage);
        }

        return response;
    }

}
