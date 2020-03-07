/**
 *
 */
package io.apiloop.workers.base.ws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import io.apiloop.workers.base.Worker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.NotImplementedException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;

/**
 * Build a XML Document from a XML string
 */
@Accessors(chain = true)
public class XmlDocumentBuilder implements Worker<Document> {

    @Getter @Setter
    private String content;
    
    @Override
    public XmlDocumentBuilder mapParameters(ObjectNode parameters) {
        throw new NotImplementedException("Not applicable for this worker");
    }

    @Override
    public Document execute() {
        if (Strings.isNullOrEmpty(content)) {
            return null;
        }
        try {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(new InputSource(new StringReader(content)));
        } catch (IOException | JDOMException e) {
            throw new IllegalStateException(e);
        }
    }

}
