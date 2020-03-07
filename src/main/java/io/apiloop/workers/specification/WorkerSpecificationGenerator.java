/**
 *
 */
package io.apiloop.workers.specification;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import io.apiloop.workers.specification.annotations.WorkerFieldDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

/**
 * Generate the specification of a worker
 */
@Accessors(chain = true)
public class WorkerSpecificationGenerator<T extends Worker> implements Worker<WorkerSpecification> {

    @Getter @Setter
    private Class<T> clazz;

    @Override
    public WorkerSpecificationGenerator<T> mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public WorkerSpecificationGenerator<T> assertWellConfigured() {
        Assert.notNull(clazz);
        return this;
    }

    @Override
    public WorkerSpecification execute() {
        WorkerDescriptor specificationDescriptor = clazz.getAnnotation(WorkerDescriptor.class);

        if (specificationDescriptor == null) {
            throw new IllegalStateException(clazz.getCanonicalName() + " needs to define a " + WorkerDescriptor.class.getSimpleName());
        }

        WorkerSpecification<T> specification = new WorkerSpecification<>(clazz, specificationDescriptor);

        for (Field field : clazz.getDeclaredFields()) {
            WorkerFieldDescriptor fieldSpecificationDescriptor = field.getAnnotation(WorkerFieldDescriptor.class);
            if (fieldSpecificationDescriptor == null) {
                continue;
            }
            specification.addParameter(new WorkerFieldSpecification(field.getName(), fieldSpecificationDescriptor));
        }

        return specification;
    }

}
