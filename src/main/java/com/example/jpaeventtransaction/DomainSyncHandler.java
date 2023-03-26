package com.example.jpaeventtransaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DomainSyncHandler implements BusinessServiceUpdateHandler, BusinessServicePersistHandler {
    private final CatalogInfoRepository catalogInfoRepository;

    public DomainSyncHandler(CatalogInfoRepository catalogInfoRepository) {
        this.catalogInfoRepository = catalogInfoRepository;
    }

    @Override
    public void update(BusinessService businessService) {
        CatalogInfo domain = catalogInfoRepository.findByKindAndName("Domain", businessService.getGroupCode());
        domain.updateTitle(businessService.getGroupName());
    }

    @Override
    public void persist(BusinessService businessService) {
        catalogInfoRepository.save(new CatalogInfo("Domain", businessService.getGroupCode(), businessService.getGroupName()));
    }
}
