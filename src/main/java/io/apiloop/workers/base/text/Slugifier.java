/**
 * 
 */
package io.apiloop.workers.base.text;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.slugify.Slugify;
import com.google.common.base.Strings;
import io.apiloop.workers.base.Worker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

/**
 * Slugify the given string
 */
@Accessors(chain = true)
public class Slugifier implements Worker<Slugifier> {
    
    /**
     * Value to slugify
     */
    @Getter @Setter
    private String value;
    
    /**
     * Slug computed {@link #value} or empty string if {@link #value} is null or empty
     */
    @Getter
    private String slug;
    
    @Override
    public Slugifier mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public Slugifier execute() {
        if (Strings.isNullOrEmpty(value)) {
            slug = "";
        }
        
        try {
            Slugify slugify = new Slugify(true);
            slug = slugify.slugify(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        return this;
    }
    
}
