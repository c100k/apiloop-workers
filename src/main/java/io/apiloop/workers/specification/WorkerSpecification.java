/**
 *
 */
package io.apiloop.workers.specification;

import com.google.common.base.Strings;
import io.apiloop.workers.base.Worker;
import io.apiloop.workers.specification.annotations.WorkerDescriptor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.util.Arrays.asList;

/**
 *
 */
@Getter
@ToString
public class WorkerSpecification<T extends Worker> {

    public static final String LOGO_PATH = "https://s3-eu-west-1.amazonaws.com/apiloop-static/56476bd0-e41a-48fc-8cf2-054ab8c01201/workers/";
    
    private final String id;
    private final String canonicalName;
    private final I18nString name;
    private final I18nString description;
    private final String externalServiceUrl;
    private final String logoUrl;
    private final List<WorkerFieldSpecification> parameters;
    private final List<String> allowedApplications;

    public WorkerSpecification(Class<T> clazz, WorkerDescriptor descriptor) {
        this.id = descriptor.id();
        this.canonicalName = clazz.getCanonicalName();
        this.name = new I18nString(descriptor.name());
        this.description = new I18nString(descriptor.description());
        this.externalServiceUrl = Strings.isNullOrEmpty(descriptor.externalServiceUrl()) ? null : descriptor.externalServiceUrl() + "?ref=apiloop";
        this.logoUrl = LOGO_PATH + UPPER_CAMEL.to(LOWER_HYPHEN, clazz.getSimpleName()) + ".png";
        this.parameters = new ArrayList<>();
        this.allowedApplications = asList(descriptor.allowedApplications());
    }

    public WorkerSpecification addParameter(WorkerFieldSpecification field) {
        parameters.add(field);
        return this;
    }

}
