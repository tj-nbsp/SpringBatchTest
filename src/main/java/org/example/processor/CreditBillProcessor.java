package org.example.processor;

import org.example.pojo.CreditBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CreditBillProcessor implements ItemProcessor<CreditBill, CreditBill> {

    private static final Logger logger = LoggerFactory.getLogger(CreditBillProcessor.class);

    @Override
    public CreditBill process(CreditBill item) throws Exception {
        logger.info("{}", item.toString());
        return item;
    }
}
