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
 * Check that a clear value is equal to a previously hashed value using {@link BCryptHasher}
 */
@Accessors(fluent = true)
public class BCryptChecker implements Worker<BCryptChecker> {
    
    /**
     * Value to check (i.e. : the clear value submitted in a sign in form)
     */
    @Getter @Setter
    private String value;
    
    /**
     * Value previsously hashed with {@link BCryptHasher} (i.e. : the value stored in the database)
     */
    @Getter @Setter
    private String hash;
    
    /**
     * Whether the values match or not
     */
    @Getter
    private Boolean valuesMatch;
    
    @Override
    public BCryptChecker mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public BCryptChecker execute() {
        valuesMatch = BCrypt.checkpw(value, hash);
        return this;
    }
    
}
