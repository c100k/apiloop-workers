/**
 *
 */
package io.apiloop.workers.store.api.algolia;

import com.algolia.search.exceptions.AlgoliaException;
import com.algolia.search.objects.tasks.async.AsyncTaskIndexing;
import com.google.common.base.Strings;
import io.apiloop.workers.base.BusinessObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Accessors(fluent = true)
public class AlgoliaIndexerInMemory implements AlgoliaIndexer {

    private List<BusinessObject> objects = new ArrayList<>();

    @Getter @Setter
    private String expectedIllegalArgumentExceptionMessage;

    public CompletableFuture<AsyncTaskIndexing> index(String applicationId, String apiKey, String indexName, BusinessObject businessObject) throws AlgoliaException {
        if (!Strings.isNullOrEmpty(expectedIllegalArgumentExceptionMessage)) {
            CompletableFuture<AsyncTaskIndexing> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException(expectedIllegalArgumentExceptionMessage));
            return future;
        }
        objects.add(businessObject);
        AsyncTaskIndexing indexing = new AsyncTaskIndexing();
        indexing.setIndex(indexName).setObjectID(businessObject.getUuid().toString()).setTaskID(274474884L);
        return CompletableFuture.completedFuture(indexing);
    }

}
