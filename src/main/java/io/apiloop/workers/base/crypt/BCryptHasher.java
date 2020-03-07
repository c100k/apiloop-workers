/**
 * 
 */
package io.apiloop.workers.base.crypt;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.Worker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Hash the value using the BCrypt algorithm
 */
@Accessors(fluent = true)
public class BCryptHasher implements Worker<BCryptHasher> {
    
    /**
     * Value to hash
     */
    @Getter @Setter
    private String value;
    
    /**
     * Generated salt used to hash
     */
    @Getter
    private String salt;
    
    /**
     * Hashed value
     */
    @Getter
    private String hash;
    
    @Override
    public BCryptHasher mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public BCryptHasher execute() {
        salt = BCrypt.gensalt();
        hash = BCrypt.hashpw(value, salt);
        return this;
    }
    
}
