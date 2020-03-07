/**
 *
 */
package io.apiloop.workers.store.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "51ba7bb5-9ab2-4441-a1ae-ef77105561f0",
    name = @I18nDescriptor(
        en = "Get a French company information",
        fr = "Récupérer les informations d'une entreprise française"
    ),
    description = @I18nDescriptor(
        en = "Calls FirmApi with the SIREN to retrieve the information about the company",
        fr = "Appelle FirmApi avec le SIREN pour récupérer les informations sur l'entreprise"
    ),
    externalServiceUrl = "https://firmapi.com",
    allowedApplications = {"5ac6d8ff-9807-4907-abf9-69d9f71cdff3"}
)
public class FirmApiCompanyInfoGetter implements APICaller, BusinessObjectHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirmApiCompanyInfoGetter.class);

    @Getter
    private final WorkerRestAPICaller restAPICaller;

    @Getter @Setter
    private Consumer<String> onError;

    @Getter @Setter
    private BusinessObject businessObject;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Siren", fr = "Siren"),
        description = @I18nDescriptor(en = "Siren of the company", fr = "Siren de l'entreprise"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String siren;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Destination", fr = "Destination"),
        description = @I18nDescriptor(en = "Name of the field to put the company information in", fr = "Nom du champ dans lequel mettre les informations de l'entreprise"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String responseFieldName;

    @Inject
    public FirmApiCompanyInfoGetter(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }

    @Override
    public FirmApiCompanyInfoGetter mapParameters(ObjectNode parameters) {
        siren = parameters.get("siren").asText();
        responseFieldName = parameters.get("responseFieldName").asText();
        return this;
    }

    @Override
    public FirmApiCompanyInfoGetter assertWellConfigured() {
        APICaller.super.assertWellConfigured();
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        if (Strings.isNullOrEmpty(siren)) {
            return CompletableFuture.completedFuture(new WebServiceResponse());
        }

        WebServiceRequest request = new WebServiceRequest().setUrl("https://firmapi.com/api/v1/companies/" + siren);

        return restAPICaller.get(request).thenApply(this::processResponse);
    }

    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
        LOGGER.debug("Siren : {} / Response field name : {} / Response : {}", siren, responseFieldName, responseAsJson);

        Optional<JsonNode> status = new JsonFieldReader()
            .setNode((ObjectNode) responseAsJson)
            .setField("status")
            .go()
            .getValue();

        if (!status.isPresent()) {
            throw new IllegalStateException("Response received without status : " + response);
        }

        switch (status.get().asText()) {
            case "success":
                processSuccess(responseAsJson);
                break;
            case "error":
                processError(responseAsJson);
                break;
            default: throw new IllegalStateException("Response received with unknown status : " + response);
        }

        return response;
    }

    private void processSuccess(JsonNode responseAsJson) {
        businessObject.getData().set(responseFieldName, responseAsJson.get("company"));
    }

    private void processError(JsonNode responseAsJson) {
        onError.accept(responseAsJson.get("message").asText());
    }

}
