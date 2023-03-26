package com.example.jpaeventtransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogInfoRepository extends JpaRepository<CatalogInfo, Long> {
    CatalogInfo findByKindAndName(String kind, String name);
}
