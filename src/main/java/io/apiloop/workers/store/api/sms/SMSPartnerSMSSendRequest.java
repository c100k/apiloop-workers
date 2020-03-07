/**
 *
 */
package io.apiloop.workers.store.api.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 */
@Getter @Setter
@Accessors(chain = true)
public class SMSPartnerSMSSendRequest {

    public enum Formula {
        PREMIUM(1),
        LOW_COST(2);

        private Integer value;

        Formula(Integer value) {
            this.value = value;
        }

        @JsonValue
        public Integer getValue() {
            return value;
        }
    }

    private String apiKey;

    @JsonProperty("phoneNumbers")
    private String phoneNumber;

    private String message;

    @JsonProperty("gamme")
    private Formula formula;

    private String sender;

}
