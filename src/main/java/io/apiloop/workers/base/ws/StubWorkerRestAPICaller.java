/**
 *
 */
package io.apiloop.workers.base.ws;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Stub a REST API for development or testing purposes
 */
@Accessors(chain = true)
public class StubWorkerRestAPICaller implements WorkerRestAPICaller {

    @Getter @Setter
    private Integer expectedStatus;

    @Getter @Setter
    private String expectedResponse;
    
    @Override
    public CompletionStage<WebServiceResponse> get(WebServiceRequest request) {
        return buildResponse();
    }

    @Override
    public CompletionStage<WebServiceResponse> post(WebServiceRequest request) {
        return buildResponse();
    }

    @Override
    public CompletionStage<WebServiceResponse> delete(WebServiceRequest request) {
        return buildResponse();
    }

    private CompletableFuture<WebServiceResponse> buildResponse() {
        return CompletableFuture.completedFuture(new WebServiceResponse()
            .setStatus(expectedStatus)
            .setBody(expectedResponse)
        );
    }

}
