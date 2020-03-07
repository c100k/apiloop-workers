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
    id = "0ed47858-0056-4f5b-8e14-27c516c3af94",
    name = @I18nDescriptor(
        en = "Disable a resource",
        fr = "Désactiver une ressource"
    ),
    description = @I18nDescriptor(
        en = "Useful to disable a bad user or a post that does not respect your policies for example",
        fr = "Utile pour désactiver un mauvais utilisateur ou un article qui ne respecte pas vos conditions générales par exemple"
    )
)
public class ObjectDisabler implements Worker<Boolean>, BusinessObjectHandler {

    @Getter @Setter
    private BusinessObject businessObject;
    
    @Override
    public ObjectDisabler mapParameters(ObjectNode parameters) {
        return this;
    }
    
    @Override
    public ObjectDisabler assertWellConfigured() {
        Assert.notNull(businessObject, "objectMandatory");
        return this;
    }
    
    @Override
    public Boolean execute() {
        if (businessObject.getEnabled() == null || businessObject.getEnabled()) {
            businessObject.setEnabled(false);
            return true;
        } else {
            return false;
        }
    }
    
}
