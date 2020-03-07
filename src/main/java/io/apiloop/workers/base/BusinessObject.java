/**
 * 
 */
package io.apiloop.workers.base;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Object representing a business related object
 */
public interface BusinessObject {
    
    UUID getUuid();
    BusinessObject setUuid(UUID uuid);
    
    UUID getMetaEntityId();
    BusinessObject setMetaEntityId(UUID metaEntityId);
    
    UUID getRootOwnerId();
    BusinessObject setRootOwnerId(UUID rootOwnerId);
    
    UUID getOwnerId();
    BusinessObject setOwnerId(UUID ownerId);

    ObjectNode getData();
    BusinessObject setData(ObjectNode data);
    
    Boolean getEnabled();
    BusinessObject setEnabled(Boolean enabled);
    
    BusinessObjectPricing getPricing();
    BusinessObject setPricing(BusinessObjectPricing pricing);
    
    LocalDateTime getCreatedAt();
    BusinessObject setCreatedAt(LocalDateTime createdAt);
    
    LocalDateTime getUpdatedAt();
    BusinessObject setUpdatedAt(LocalDateTime updatedAt);
    
}
