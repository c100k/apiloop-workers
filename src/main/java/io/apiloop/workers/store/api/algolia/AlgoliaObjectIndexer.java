/**
 *
 */
package io.apiloop.workers.store.api.algolia;

import com.algolia.search.exceptions.AlgoliaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.ws.APICaller;
import io.apiloop.workers.base.ws.WebServiceResponse;
import io.apiloop.workers.base.ws.WorkerRestAPICaller;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "3098ef33-6899-443f-a953-9e08e01baa4c",
    name = @I18nDescriptor(
        en = "Index an object in Algolia",
        fr = "Indexer un objet dans Algolia"
    ),
    description = @I18nDescriptor(
        en = "You can index an object to make it available for search for you customers",
        fr = "Vous pouvez indexer un objet pour le rendre disponible à la recherche pour vos clients"
    ),
    externalServiceUrl = "https://www.algolia.com"
)
public class AlgoliaObjectIndexer implements APICaller, BusinessObjectHandler {

    @Getter
    private final WorkerRestAPICaller restAPICaller;
    private final AlgoliaIndexer algoliaIndexer;

    @Getter @Setter
    private Consumer<String> onError;

    @Getter @Setter
    private BusinessObject businessObject;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Application ID", fr = "ID de l'application"),
        description = @I18nDescriptor(
            en = "Create an account on https://www.algolia.com and paste your application ID here",
            fr = "Créez un compte sur https://www.algolia.com et collez votre Id de l'application ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String applicationId;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Api Key", fr = "Clé API"),
        description = @I18nDescriptor(
            en = "Create an account on https://www.algolia.com and paste your Api Key here",
            fr = "Créez un compte sur https://www.algolia.com et collez votre Clé API ici"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String apiKey;

    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Index name", fr = "Nom de l'index"),
        description = @I18nDescriptor(
            en = "Name of the index where to index the object",
            fr = "Nom de l'index dans lequel indexer l'objet"
        ),
        type = "StringType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private String indexName;

    @Inject
    public AlgoliaObjectIndexer(WorkerRestAPICaller restAPICaller, AlgoliaIndexer algoliaIndexer) {
        this.restAPICaller = restAPICaller;
        this.algoliaIndexer = algoliaIndexer;
    }
    
    @Override
    public AlgoliaObjectIndexer mapParameters(ObjectNode parameters) {
        applicationId = parameters.get("applicationId").asText();
        apiKey = parameters.get("apiKey").asText();
        indexName = parameters.get("indexName").asText();
        return this;
    }

    @Override
    public CompletionStage<WebServiceResponse> execute() {
        try {
            return algoliaIndexer.index(applicationId, apiKey, indexName, businessObject)
                .thenApply(asyncTaskIndexing -> new WebServiceResponse()
                    .setStatus(200)
                    .setStatusText("OK")
                    .setBody(new ObjectMapper().createObjectNode()
                        .put("indexName", asyncTaskIndexing.getIndexName())
                        .put("objectID", asyncTaskIndexing.getObjectID())
                        .put("taskID", asyncTaskIndexing.getTaskID())
                        .toString()
                    )
                )
                .exceptionally(e -> {
                    if (e.getMessage().contains("Illegal character in authority") || e.getMessage().contains("Failed to query host")) {
                        onError.accept("Invalid application ID");
                    } else {
                        onError.accept(e.getCause().getLocalizedMessage());
                    }
                    return null;
                });
        } catch (AlgoliaException e) {
            onError.accept(e.getLocalizedMessage());
            return CompletableFuture.completedFuture(new WebServiceResponse().setStatus(400));
        }
    }

}
