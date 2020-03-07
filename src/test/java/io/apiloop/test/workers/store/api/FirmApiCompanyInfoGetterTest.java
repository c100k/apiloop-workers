/**
 *
 */
package io.apiloop.test.workers.store.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.apiloop.workers.base.BusinessObjectImpl;
import io.apiloop.workers.base.ws.StubWorkerRestAPICaller;
import io.apiloop.workers.store.api.FirmApiCompanyInfoGetter;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.apiloop.test.workers.store.Commons.testParametersMapping;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 *
 */
public class FirmApiCompanyInfoGetterTest {

    private FirmApiCompanyInfoGetter worker;
    private StubWorkerRestAPICaller restAPICaller;
    private ObjectNode node;
    private String onErrorMessage;

    @Before
    public void setUp() {
        restAPICaller = new StubWorkerRestAPICaller();
        node = new ObjectMapper().createObjectNode();
        worker = new FirmApiCompanyInfoGetter(restAPICaller)
            .setBusinessObject(new BusinessObjectImpl().setData(node))
            .setOnError(message -> onErrorMessage = message)
            .setSiren("530085802")
            .setResponseFieldName("companyInfo");
    }
    
    @Test
    public void shouldMapParameters() {
        testParametersMapping(worker);
    }

    @Test
    public void withExistingCompany() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller.setExpectedResponse("{\"status\":\"success\",\"params\":{\"siren\":\"530085802\"},\"company\":{\"siren\":\"530085802\",\"names\":{\"best\":\"Facebook France\",\"denomination\":\"FACEBOOK FRANCE\",\"commercial_name\":null,\"sigle\":null,\"first_name\":null,\"last_name\":null},\"legal_form\":\"EURL\",\"address\":\"6 rue Ménars\",\"postal_code\":\"75002\",\"city\":\"Paris\",\"vat_number\":\"FR36530085802\",\"capital\":4950000,\"administration\":\" Gérant partant : Shields, Joanna, nomination du Gérant : Crehan, Shane Hugh, nomination du Commissaire aux comptes titulaire : ERNST \\u0026 YOUNG AUDIT, nomination du Commissaire aux comptes suppléant : AUDITEX \",\"activity\":\"Fournir au groupe Facebook des prestations de services en rapport avec la vente d'espaces publicitaires, le développement commercial, le marketing, la recherche et le développement technologiques, les relations publiques, le Lobbying, la communication, le support juridique et toutes autres prestations de services commerciales, administratives et/ou informatiques visant à développer les services et la marque Facebook en France\",\"radie\":false,\"last_legal_update\":\"2016-05-20\",\"established_on\":\"2012-08-23\",\"id\":1461163}}");

        // When
        worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(node.get("companyInfo").get("siren").asText()).isEqualTo("530085802");
    }

    @Test
    public void withUnknownCompany() throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        restAPICaller.setExpectedResponse("{\"status\":\"error\",\"message\":\"Entreprise avec le siren 000 non trouvée\"}");

        // When
        worker.go().toCompletableFuture().get(5, SECONDS);

        // Then
        assertThat(onErrorMessage).isEqualTo("Entreprise avec le siren 000 non trouvée");
    }

}
