/**
 *
 */
package io.apiloop.workers.base.ws;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Accessors(chain = true)
public class WebServiceRequest {

    @Getter @Setter
    private String url;

    @Getter @Setter
    private Map<String, Object> headers;

    @Getter @Setter
    private Map<String, Object> queryStringParameters;

    @Getter @Setter
    private String payloadAsString;

    @Getter @Setter
    private JsonNode payloadAsJson;

    @Getter @Setter
    private Map<String, Object> payloadAsFormUrlEncodedParameters;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    public WebServiceRequest addHeader(String name, Object value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        return this;
    }

    public WebServiceRequest addQueryStringParameter(String name, Object value) {
        if (queryStringParameters == null) {
            queryStringParameters = new HashMap<>();
        }
        queryStringParameters.put(name, value);
        return this;
    }

    public WebServiceRequest addFormUrlEncodedParameter(String name, Object value) {
        if (payloadAsFormUrlEncodedParameters == null) {
            payloadAsFormUrlEncodedParameters = new HashMap<>();
        }
        payloadAsFormUrlEncodedParameters.put(name, value);
        return this;
    }

}
