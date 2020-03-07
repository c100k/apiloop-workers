/**
 *
 */
package io.apiloop.workers.base.persistence;

import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.Worker;

/**
 *
 */
public interface DatabaseSaver extends Worker<BusinessObject> {
    
    DatabaseSaver setEntity(BusinessObject businessObject);
    
}
