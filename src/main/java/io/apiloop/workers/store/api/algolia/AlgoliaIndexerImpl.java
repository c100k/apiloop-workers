/**
 *
 */
package io.apiloop.workers.store.api.algolia;

import com.algolia.search.AsyncHttpAPIClientBuilder;
import com.algolia.search.exceptions.AlgoliaException;
import com.algolia.search.objects.tasks.async.AsyncTaskIndexing;
import io.apiloop.workers.base.BusinessObject;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletionStage;

/**
 *
 */
@Accessors(chain = true)
public class AlgoliaIndexerImpl implements AlgoliaIndexer {

    public CompletionStage<AsyncTaskIndexing> index(String applicationId, String apiKey, String indexName, BusinessObject businessObject) throws AlgoliaException {
        return new AsyncHttpAPIClientBuilder(applicationId, apiKey).build()
            .initIndex(indexName, BusinessObject.class)
            .addObject(businessObject.getUuid().toString(), businessObject);
    }

}
