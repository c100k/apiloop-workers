/**
 * 
 */
package io.apiloop.workers.store.object;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObject;
import io.apiloop.workers.base.BusinessObjectHandler;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.base.persistence.DatabaseSaver;
import io.apiloop.workers.specification.annotations.I18nDescriptor;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

/**
 *
 */
@Accessors(chain = true)
@WorkerDescriptor(
    id = "3769ef76-c4d4-4745-b6da-071e27b19d41",
    name = @I18nDescriptor(
        en = "Save a resource",
        fr = "Sauvegarder une ressource"
    ),
    description = @I18nDescriptor(
        en = "Save a resource in the database",
        fr = "Enregistre une ressource dans la base de données"
    )
)
public class ObjectSaver implements Worker<BusinessObject>, BusinessObjectHandler {
    
    private final DatabaseSaver databaseSaver;
    
    @Getter @Setter
    private BusinessObject businessObject;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Meta entity Id", fr = "Id de la meta entité"),
        description = @I18nDescriptor(en = "UUID of the meta entity the object is based on", fr = "UUID de la meta entité sur laquelle est basé l'objet"),
        type = "UUIDType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private UUID metaEntityId;
    
    @Getter @Setter
    @WorkerFieldDescriptor(
        name = @I18nDescriptor(en = "Data", fr = "Données"),
        description = @I18nDescriptor(en = "Data of the resource", fr = "Données de la ressource"),
        type = "ObjectNodeType",
        htmlFormFieldType = "text",
        mandatory = true
    )
    private ObjectNode data;
    
    @Inject
    public ObjectSaver(DatabaseSaver databaseSaver) {
        this.databaseSaver = databaseSaver;
    }
    
    @Override
    public ObjectSaver mapParameters(ObjectNode parameters) {
        metaEntityId = UUID.fromString(parameters.get("metaEntityId").asText());
        data = (ObjectNode) parameters.get("data");
        return this;
    }
    
    @Override
    public ObjectSaver assertWellConfigured() {
        Assert.notNull(businessObject, "objectMandatory");
        return this;
    }
    
    @Override
    public BusinessObject execute() {
        return databaseSaver.setEntity(new BusinessObjectImpl()
            .setUuid(randomUUID())
            .setEnabled(true)
            .setCreatedAt(now())
            .setUpdatedAt(now())
            .setMetaEntityId(metaEntityId)
            .setRootOwnerId(businessObject.getRootOwnerId())
            .setOwnerId(businessObject.getOwnerId())
            .setData(data)
        ).go();
    }
    
}
