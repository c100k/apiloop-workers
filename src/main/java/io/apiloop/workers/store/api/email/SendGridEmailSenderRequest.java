/**
 *
 */
package io.apiloop.workers.store.api.email;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter @Setter
@Accessors(chain = true)
public class SendGridEmailSenderRequest {
    
    private final List<Personalization> personalizations = new ArrayList<>();
    private final Actor from;
    private final String subject;
    private final List<Content> content = new ArrayList<>();
    
    public SendGridEmailSenderRequest(String to, String from, String subject, String content) {
        this.personalizations.add(new Personalization().add(new Actor(to, null)));
        this.from = new Actor(from, null);
        this.subject = subject;
        this.content.add(new Content("text/html", content));
    }
    
    @Getter @Setter
    @Accessors(chain = true)
    private class Actor {
        
        private final String email;
        private final String name;
        
        Actor(String email, String name) {
            this.email = email;
            this.name = name;
        }
        
    }
    
    @Getter @Setter
    @Accessors(chain = true)
    private class Content {
        
        private final String type;
        private final String value;
    
        Content(String type, String value) {
            this.type = type;
            this.value = value;
        }
        
    }
    
    @Getter @Setter
    @Accessors(chain = true)
    private class Personalization {
        
        private final List<Actor> to = new ArrayList<>();
    
        Personalization add(Actor actor) {
            to.add(actor);
            return this;
        }
        
    }
    
}
