/**
 *
 */
package io.apiloop.test.workers.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import org.assertj.core.api.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public final class Commons {
    
    private Commons() {
        
    }
    
    public static void testParametersMapping(Worker worker) {
        // Given
        String stringValue = "Some value";
        ObjectNode nodeValue = new ObjectMapper().createObjectNode().put("field", "value");
        List<String> stringListValue = asList("one", "two", "three");
        Object objectStringValue = "Some object";
        Boolean booleanValue = true;
        UUID uuidValue = randomUUID();
        ObjectNode parameters = new ObjectMapper().createObjectNode();
        List<Field> parameterizableFields = new ArrayList<>();
        for (Field field : worker.getClass().getDeclaredFields()) {
            WorkerFieldDescriptor fieldSpecificationDescriptor = field.getAnnotation(WorkerFieldDescriptor.class);
            if (fieldSpecificationDescriptor != null) {
                parameterizableFields.add(field);
                if (String.class.equals(field.getType())) {
                    parameters.put(field.getName(), stringValue);
                } else if (ObjectNode.class.equals(field.getType())) {
                    parameters.set(field.getName(), nodeValue);
                } else if (List.class.equals(field.getType())) {
                    parameters.putArray(field.getName());
                    stringListValue.forEach(item -> ((ArrayNode) parameters.get(field.getName())).add(item));
                } else if (Boolean.class.equals(field.getType())) {
                    parameters.put(field.getName(), booleanValue);
                } else if (UUID.class.equals(field.getType())) {
                    parameters.put(field.getName(), uuidValue.toString());
                } else {
                    parameters.put(field.getName(), (String) objectStringValue);
                }
            }
        }
        
        // When
        worker.mapParameters(parameters);
        
        // Then
        assertThat(parameterizableFields).are(new Condition<>(field -> {
            try {
                Object value = null;
                for (Method method : worker.getClass().getMethods()) {
                    if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                        if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                            value =  method.invoke(worker);
                        }
                    }
                }
                if (value != null) {
                    if (String.class.equals(field.getType())) {
                        return stringValue.equals(value.toString());
                    } else if (ObjectNode.class.equals(field.getType())) {
                        return nodeValue.equals(value);
                    } else if (List.class.equals(field.getType())) {
                        return stringListValue.equals(value);
                    } else if (Boolean.class.equals(field.getType())) {
                        return booleanValue.equals(value);
                    } else if (UUID.class.equals(field.getType())) {
                        return uuidValue.equals(value);
                    } else {
                        return objectStringValue.toString().equals(value.toString());
                    }
                } else {
                    return false;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                return false;
            }
        }, "to be '%s' || '%s' || '%s' || '%s' || '%s' || '%s' as value regarding its type", stringValue, nodeValue, stringListValue, booleanValue, uuidValue, objectStringValue));
    }
    
}
