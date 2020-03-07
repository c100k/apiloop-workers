/**
 *
 */
package io.apiloop.workers.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apiloop.workers.specification.WorkerSpecification;
import io.apiloop.workers.specification.WorkerSpecificationGenerator;
import io.apiloop.workers.store.api.FirmApiCompanyInfoGetter;
import io.apiloop.workers.store.api.algolia.AlgoliaObjectIndexer;
import io.apiloop.workers.store.api.email.SendGridEmailSender;
import io.apiloop.workers.store.api.mailchimp.MailChimpListMemberAdder;
import io.apiloop.workers.store.api.sms.MessageBirdSMSSender;
import io.apiloop.workers.store.api.sms.SMSPartnerSMSSender;
import io.apiloop.workers.store.api.stripe.StripeTokenCharger;
import io.apiloop.workers.store.api.textrazor.TextRazorTextAnalyzer;
import io.apiloop.workers.store.object.ObjectDisabler;
import io.apiloop.workers.store.object.ObjectEnabler;
import io.apiloop.workers.store.object.ObjectFieldPopulator;
import io.apiloop.workers.store.object.ObjectFieldSlugifier;
import io.apiloop.workers.store.object.ObjectFieldsBCryptHasher;
import io.apiloop.workers.store.object.ObjectFieldsCapitalizer;
import io.apiloop.workers.store.object.ObjectSaver;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Store of workers
 */
@Accessors(chain = true)
public class WorkerStore {

    private final List<WorkerSpecification> specifications;

    public WorkerStore() {
        this.specifications = new ArrayList<>();
    }

    public WorkerStore fill() {
        // API
        specifications.add(new WorkerSpecificationGenerator<AlgoliaObjectIndexer>().setClazz(AlgoliaObjectIndexer.class).go());
        specifications.add(new WorkerSpecificationGenerator<FirmApiCompanyInfoGetter>().setClazz(FirmApiCompanyInfoGetter.class).go());
        specifications.add(new WorkerSpecificationGenerator<MailChimpListMemberAdder>().setClazz(MailChimpListMemberAdder.class).go());
        specifications.add(new WorkerSpecificationGenerator<MessageBirdSMSSender>().setClazz(MessageBirdSMSSender.class).go());
        specifications.add(new WorkerSpecificationGenerator<SendGridEmailSender>().setClazz(SendGridEmailSender.class).go());
        specifications.add(new WorkerSpecificationGenerator<SMSPartnerSMSSender>().setClazz(SMSPartnerSMSSender.class).go());
        specifications.add(new WorkerSpecificationGenerator<StripeTokenCharger>().setClazz(StripeTokenCharger.class).go());
        specifications.add(new WorkerSpecificationGenerator<TextRazorTextAnalyzer>().setClazz(TextRazorTextAnalyzer.class).go());
        // Object
        specifications.add(new WorkerSpecificationGenerator<ObjectDisabler>().setClazz(ObjectDisabler.class).go());
        specifications.add(new WorkerSpecificationGenerator<ObjectFieldPopulator>().setClazz(ObjectFieldPopulator.class).go());
        specifications.add(new WorkerSpecificationGenerator<ObjectFieldsBCryptHasher>().setClazz(ObjectFieldsBCryptHasher.class).go());
        specifications.add(new WorkerSpecificationGenerator<ObjectFieldsCapitalizer>().setClazz(ObjectFieldsCapitalizer.class).go());
        specifications.add(new WorkerSpecificationGenerator<ObjectFieldSlugifier>().setClazz(ObjectFieldSlugifier.class).go());
        specifications.add(new WorkerSpecificationGenerator<ObjectEnabler>().setClazz(ObjectEnabler.class).go());
        specifications.add(new WorkerSpecificationGenerator<ObjectSaver>().setClazz(ObjectSaver.class).go());
        return this;
    }

    public List<WorkerSpecification> getForApplication(UUID applicationID) {
        if (applicationID == null) {
            return new ArrayList<>();
        }
        return specifications.stream()
            .filter(specification -> specification.getAllowedApplications() == null ||
                    specification.getAllowedApplications().isEmpty() ||
                    specification.getAllowedApplications().contains(applicationID.toString())
            )
            .collect(Collectors.toList());
    }

    public JsonNode getForApplicationAsJson(UUID applicationID) {
        return new ObjectMapper().valueToTree(getForApplication(applicationID));
    }
    
    public List<WorkerSpecification> getAll() {
        return specifications;
    }

}
