/**
 *
 */
package io.apiloop.workers.base;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public interface BusinessObjectPricing {
    
    Integer getAtiPrice();
    
    BusinessObjectPricing setAtiPrice(Integer atiPrice);
    
    String getCurrency();
    
    BusinessObjectPricing setCurrency(String currency);
    
    AtomicInteger getSales();
    
    List<String> getPaymentReferences();
    
    BusinessObjectPricing addSale(String paymentReference);
    
}
