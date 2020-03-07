/**
 * 
 */
package io.apiloop.workers.base.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.Worker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Check if the given expression is true against the given node
 */
@Accessors(chain = true)
public class JsonPredicateChecker implements Worker<JsonPredicateChecker> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPredicateChecker.class);
    
    /**
     * Node where the field is
     */
    @Getter @Setter
    private ObjectNode node;
    
    /**
     * Expression to check
     */
    @Getter @Setter
    private String expression;

    /**
     * Function to execute if there is a bad syntax error
     */
    @Getter @Setter
    private Consumer<String> onBadSyntaxError;
    
    /**
     * Expression parser engine
     */
    @Getter
    private JexlEngine engine;
    
    /**
     * Expression parsed by the engine
     */
    @Getter
    private Expression parsedExpression;
    
    /**
     * Context for the engine containing the variables
     */
    @Getter
    private JexlContext context;
    
    /**
     * Result of the evaluation
     */
    @Getter
    private Object evaluation;
    
    /**
     * 
     */
    public JsonPredicateChecker() {
        super();
        engine = new JexlEngine();
        engine.setSilent(false);
        engine.setStrict(true);
    }

    @Override
    public JsonPredicateChecker mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public JsonPredicateChecker assertWellConfigured() {
        Assert.notNull(onBadSyntaxError);
        return this;
    }

    @Override
    public JsonPredicateChecker execute() {
        if (Strings.isNullOrEmpty(expression)) {
            return this;
        }

        try {
            parsedExpression = engine.createExpression(expression);
            
            context = new MapContext();
            populateContext(null, node);

            evaluation = parsedExpression.evaluate(context);
            if (!(evaluation instanceof Boolean)) {
                throw new IllegalStateException("Evaluation did not return a boolean");
            }
        } catch (JexlException.Variable e) {
            LOGGER.trace("Variable does not exist", e);
            evaluation = false;
        } catch (JexlException.Parsing e) {
            LOGGER.trace("Parsing error", e);
            onBadSyntaxError.accept("BadSyntax");
        } catch (JexlException e) {
            throw new IllegalStateException(e);
        }
        
        return this;
    }
    
    /**
     * Populate context recursively with {@link #node} fields
     * @param name
     * @param node
     */
    private void populateContext(String name, JsonNode node) {
        if (node.isNull()) {
            context.set(name, null);
        } else if (node.isArray()) {
            context.set(name, node);
        } else if (node.isObject()) {
            String prefix = Strings.isNullOrEmpty(name) ? "" : name + ".";
            Iterator<Entry<String, JsonNode>> iterator = node.fields();
            while (iterator.hasNext()) {
                Entry<String, JsonNode> subNode = iterator.next();
                populateContext(prefix + subNode.getKey(), subNode.getValue());
            }
        } else {
            context.set(name, node.asText());
        }
    }
    
    /**
     * Check if the expression is true
     * @return
     */
    public Boolean isTrue() {
        return (Boolean) evaluation;
    }
    
}
