/**
 *
 */
package io.apiloop.workers.base;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@Accessors(chain = true)
public class BusinessObjectPricingImpl implements BusinessObjectPricing {
    
    @Getter @Setter
    private Integer atiPrice;
    
    @Getter @Setter
    private String currency;
    
    @Getter
    private AtomicInteger sales;
    
    @Getter
    private List<String> paymentReferences;
    
    @Override
    public BusinessObjectPricingImpl addSale(String paymentReference) {
        if (sales == null) {
            sales = new AtomicInteger(0);
        }
        sales.incrementAndGet();
        
        if (paymentReferences == null) {
            paymentReferences = new ArrayList<>();
        }
        paymentReferences.add(paymentReference);
        return this;
    }
    
}
