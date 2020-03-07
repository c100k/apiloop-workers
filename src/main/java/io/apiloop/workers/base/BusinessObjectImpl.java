/**
 * 
 */
package io.apiloop.workers.base;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 
 */
@Accessors(chain = true)
public class BusinessObjectImpl implements BusinessObject {
    
    @Getter @Setter
    private UUID uuid;
    
    @Getter @Setter
    private UUID metaEntityId;
    
    @Getter @Setter
    private UUID rootOwnerId;
    
    @Getter @Setter
    private UUID ownerId;
    
    @Getter @Setter
    private Boolean enabled;

    @Getter @Setter
    private ObjectNode data;
    
    @Getter @Setter
    private BusinessObjectPricing pricing;
    
    @Getter @Setter
    private LocalDateTime createdAt;
    
    @Getter @Setter
    private LocalDateTime updatedAt;
    
}
