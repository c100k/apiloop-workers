/**
 *
 */
package io.apiloop.workers.base.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Save an object in memory
 */
@Accessors(chain = true)
public class InMemoryDatabaseSaver implements DatabaseSaver {

    @Getter @Setter
    private BusinessObject entity;

    @Getter
    private List<BusinessObject> items = new ArrayList<>();

    @Override
    public InMemoryDatabaseSaver mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public BusinessObject execute() {
        items.add(entity);
        return entity;
    }

}
