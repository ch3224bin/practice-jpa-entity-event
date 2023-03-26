package com.example.jpaeventtransaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SystemSyncHandler implements BusinessServiceUpdateHandler, BusinessServicePersistHandler {

    private final CatalogInfoRepository catalogInfoRepository;

    public SystemSyncHandler(CatalogInfoRepository catalogInfoRepository) {
        this.catalogInfoRepository = catalogInfoRepository;
    }

    @Override
    public void update(BusinessService businessService) {
        CatalogInfo system = catalogInfoRepository.findByKindAndName("System", businessService.getCode());
        system.updateTitle(businessService.getName());
    }

    @Override
    public void persist(BusinessService businessService) {
        catalogInfoRepository.save(new CatalogInfo("System", businessService.getCode(), businessService.getName()));
    }
}
