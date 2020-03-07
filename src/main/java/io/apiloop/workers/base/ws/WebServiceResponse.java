/**
 *
 */
package io.apiloop.workers.base.ws;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jdom2.Document;

/**
 *
 */
@Accessors(chain = true)
public class WebServiceResponse {

    @Getter @Setter
    private Integer status;

    @Getter @Setter
    private String statusText;

    @Getter @Setter
    private String body;

    public Document asXml() {
        return new XmlDocumentBuilder().setContent(body).go();
    }

    public JsonNode asJson() {
        return new JsonDocumentBuilder().setContent(body).go();
    }

}
