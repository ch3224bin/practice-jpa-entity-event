package com.example.jpaeventtransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TestTransaction {

    @Autowired
    BusinessServiceRepository businessServiceRepository;

    @Autowired
    CatalogInfoRepository catalogInfoRepository;

    @Test
    void test() {
        // given : 비즈니스서비스 필드값 생성
        BusinessService businessService = new BusinessService("heimdall", "헤임달", "heimdall.console", "헤임달콘솔");

        // when : 비즈니스서비스 생성 시 EntityListener가 호출되어 CatalogInfo가 생성된다.
        businessServiceRepository.save(businessService);

        // then : Domain, System 생성 확인
        assertionDomainEquals(businessService);
        assertionSystemEquals(businessService);

        // given : 비즈니스서비스 필드 변경
        businessService.updateName("달임헤");
        businessService.updateGroupName("달임헤그룹");

        // when : 비즈니스서비스 변경 시 EntityListener가 호출되어 CatalogInfo가 변경된다.
        businessServiceRepository.save(businessService);

        // then : Domain, System 변경 확인
        assertionDomainEquals(businessService);
        assertionSystemEquals(businessService);
    }

    private void assertionSystemEquals(BusinessService businessService) {
        CatalogInfo system = catalogInfoRepository.findByKindAndName("System", businessService.getCode());
        assertThat(system).isNotNull();
        assertThat(system.getName()).isEqualTo(businessService.getCode());
        assertThat(system.getTitle()).isEqualTo(businessService.getName());
    }

    private void assertionDomainEquals(BusinessService businessService) {
        CatalogInfo domain = catalogInfoRepository.findByKindAndName("Domain", businessService.getGroupCode());
        assertThat(domain).isNotNull();
        assertThat(domain.getName()).isEqualTo(businessService.getGroupCode());
        assertThat(domain.getTitle()).isEqualTo(businessService.getGroupName());
    }
}
