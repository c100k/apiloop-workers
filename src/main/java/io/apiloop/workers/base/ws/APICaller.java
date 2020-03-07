/**
 * 
 */
package io.apiloop.workers.base.ws;

import io.apiloop.workers.base.Worker;
import org.springframework.util.Assert;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 * Worker calling an external API
 */
public interface APICaller extends Worker<CompletionStage<WebServiceResponse>> {

    WorkerRestAPICaller getRestAPICaller();

    Consumer<String> getOnError();

    APICaller setOnError(Consumer<String> onError);
    
    @Override
    default APICaller assertWellConfigured() {
        Worker.super.assertWellConfigured();
        Assert.notNull(getOnError(), "onErrorMandatory");
        return this;
    }

}
