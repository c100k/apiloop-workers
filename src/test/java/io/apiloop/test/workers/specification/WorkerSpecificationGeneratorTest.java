/**
 *
 */
package io.apiloop.test.workers.specification;

import io.apiloop.workers.specification.WorkerFieldSpecification;
import io.apiloop.workers.specification.WorkerSpecification;
import io.apiloop.workers.specification.WorkerSpecificationGenerator;
import org.junit.Before;
import org.junit.Test;

import static io.apiloop.workers.specification.WorkerSpecification.LOGO_PATH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class WorkerSpecificationGeneratorTest {

    private WorkerSpecificationGenerator<AnnotatedWorker> worker;

    @Before
    public void setUp() {
        worker = new WorkerSpecificationGenerator<AnnotatedWorker>().setClazz(AnnotatedWorker.class);
    }

    @Test
    public void shouldGetAllFields() {
        // Given

        // When
        WorkerSpecification specification = worker.go();

        // Then
        assertThat(specification.getId()).isEqualTo("worker-id");
        assertThat(specification.getName().getEn()).isNotEmpty();
        assertThat(specification.getName().getFr()).isNotEmpty();
        assertThat(specification.getDescription().getEn()).isNotEmpty();
        assertThat(specification.getDescription().getFr()).isNotEmpty();
        assertThat(specification.getExternalServiceUrl()).isEqualTo("https://www.annotatedworker.com?ref=apiloop");
        assertThat(specification.getLogoUrl()).isEqualTo(LOGO_PATH + "annotated-worker.png");
        assertThat(specification.getParameters()).hasSize(3);
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(0)).getCode()).isEqualTo("editableString");
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(0)).getName().getEn()).isNotEmpty();
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(0)).getDescription().getEn()).isNotEmpty();
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(0)).getType()).isEqualTo("StringType");
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(0)).getHtmlFormFieldType()).isEqualTo("text");
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(0)).getMandatory()).isEqualTo(true);
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(1)).getCode()).isEqualTo("editableBoolean");
        assertThat(((WorkerFieldSpecification) specification.getParameters().get(2)).getCode()).isEqualTo("editableInteger");
    }

}
