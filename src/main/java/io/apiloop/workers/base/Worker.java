/**
 * 
 */
package io.apiloop.workers.base;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Basic worker that performs an action
 * @param <R> type returned by the execution of the worker
 */
public interface Worker<R extends Object> {

    /**
     * Map parameters with the worker ones
     * @param parameters
     * @return
     */
    Worker<R> mapParameters(ObjectNode parameters);
    
    /**
     * Assert that the worker is ready to be executed (check of properties for example)
     * If something is not valid, it throws an {@link IllegalArgumentException}
     * @return itself
     */
    default Worker<R> assertWellConfigured() {
        return this;
    }
    
    /**
     * Execute the main work of the worker
     * @return itself
     */
    R execute();
    
    /**
     * Execute all the actions in the right order : {@link #assertWellConfigured()} and {@link #execute()}
     * @return itself
     */
    default R go() {
        return assertWellConfigured().execute();
    }
    
}
