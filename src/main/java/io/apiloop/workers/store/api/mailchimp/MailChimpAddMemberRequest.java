/**
 *
 */
package io.apiloop.workers.store.api.mailchimp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 */
@Getter @Setter
@Accessors(chain = true)
public class MailChimpAddMemberRequest {

    public enum Status {
        SUBSCRIBED("subscribed"),
        UNSUBSCRIBED("unsubscribed"),
        CLEANED("cleaned"),
        PENDING("pending");

        private String value;

        Status(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    private Status status;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("merge_fields")
    private ObjectNode mergeFields;

}
