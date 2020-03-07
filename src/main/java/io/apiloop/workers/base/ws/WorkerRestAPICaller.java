/**
 *
 */
package io.apiloop.workers.base.ws;

import java.util.concurrent.CompletionStage;

/**
 *
 */
public interface WorkerRestAPICaller {

    CompletionStage<WebServiceResponse> get(WebServiceRequest request);

    CompletionStage<WebServiceResponse> post(WebServiceRequest request);

    CompletionStage<WebServiceResponse> delete(WebServiceRequest request);

}
