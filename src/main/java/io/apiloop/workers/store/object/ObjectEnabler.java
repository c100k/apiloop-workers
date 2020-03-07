/**
 * 
 */
package io.apiloop.workers.store.object;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "de95de06-0f95-4364-bf91-0b8e8e0907da",
    name = @I18nDescriptor(
        en = "Enable a resource",
        fr = "Activer une ressource"
    ),
    description = @I18nDescriptor(
        en = "Useful to enable a user that was disabled by error for example",
        fr = "Utile pour activer un utilisateur qui a été désactivé par erreur par exemple"
    )
)
public class ObjectEnabler implements Worker<Boolean>, BusinessObjectHandler {

    @Getter @Setter
    private BusinessObject businessObject;

    @Override
    public ObjectEnabler mapParameters(ObjectNode parameters) {
        return this;
    }
    
    @Override
    public ObjectEnabler assertWellConfigured() {
        Assert.notNull(businessObject, "objectMandatory");
        return this;
    }
    
    @Override
    public Boolean execute() {
        if (businessObject.getEnabled() == null || !businessObject.getEnabled()) {
            businessObject.setEnabled(true);
            return true;
        } else {
            return false;
        }
    }
    
}
