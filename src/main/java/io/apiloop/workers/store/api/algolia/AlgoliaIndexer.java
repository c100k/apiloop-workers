/**
 *
 */
package io.apiloop.workers.store.api.algolia;

import com.algolia.search.exceptions.AlgoliaException;
import com.algolia.search.objects.tasks.async.AsyncTaskIndexing;
import io.apiloop.workers.base.BusinessObject;

import java.util.concurrent.CompletionStage;

/**
 *
 */
public interface AlgoliaIndexer {

    CompletionStage<AsyncTaskIndexing> index(String applicationId, String apiKey, String indexName, BusinessObject businessObject) throws AlgoliaException;

}
