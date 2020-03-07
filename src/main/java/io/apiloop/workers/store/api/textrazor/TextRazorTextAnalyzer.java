/**
 *
 */
package io.apiloop.workers.store.api.textrazor;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "4d752e05-81cd-446c-9b33-34e876f57694",
    name = @I18nDescriptor(
        en = "Analyze a text with TextRazor",
        fr = "Analyser un texte avec TextRazor"
    ),
    description = @I18nDescriptor(
        en = "A natural language analysis is performed on the text to give you information about what it says",
        fr = "Une analyse de langage naturel est réalisée sur le texte pour vous donner des informations sur ce qu'il dit"
    ),
    externalServiceUrl = "https://www.textrazor.com"
)
public class TextRazorTextAnalyzer implements APICaller, BusinessObjectHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextRazorTextAnalyzer.class);

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
            en = "Create an account on https://www.textrazor.com and paste your API Key here",
            fr = "Créez un compte sur https://www.textrazor.com et collez votre Clé API ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String apiKey;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Text to analyze", fr = "Texte à analyser"),
        description = @I18nDescriptor(en = "The text you want to submit for an analysis", fr = "Le texte que vous souhaitez soumettre pour une analyse"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String text;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Destination", fr = "Destination"),
        description = @I18nDescriptor(en = "Name of the field to put the analysis in", fr = "Nom du champ dans lequel mettre l'analyse"),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String responseFieldName;

    @Inject
    public TextRazorTextAnalyzer(WorkerRestAPICaller restAPICaller) {
        this.restAPICaller = restAPICaller;
    }
    
    @Override
    public TextRazorTextAnalyzer mapParameters(ObjectNode parameters) {
        apiKey = parameters.get("apiKey").asText();
        text = parameters.get("text").asText();
        responseFieldName = parameters.get("responseFieldName").asText();
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        WebServiceRequest request = new WebServiceRequest()
            .setUrl("https://api.textrazor.com")
            .addHeader("Accept-encoding", "gzip")
            .addHeader("x-textrazor-key", apiKey)
            .addFormUrlEncodedParameter("extractors", "entities,topics,words,phrases,dependency-trees,relations,entailments,senses")
            .addFormUrlEncodedParameter("text", text);

        return restAPICaller.post(request).thenApply(this::processResponse);
    }

    private WebServiceResponse processResponse(WebServiceResponse response) {
        JsonNode responseAsJson = response.asJson();
        LOGGER.debug("Text : {} / Response : {}", text, responseAsJson);

        if (response.getStatus().equals(200)) {
            renameUnauthorizedFields(responseAsJson);
            businessObject.getData().set(responseFieldName, responseAsJson);
        } else {
            onError.accept(responseAsJson.get("error").asText());
        }

        return response;
    }

    private void renameUnauthorizedFields(JsonNode response) {
        // language field is used by Mongo to index documents
        ((ObjectNode) response.get("response"))
            .put("__language", response.get("response").get("language").asText())
            .remove("language");
    }

}
