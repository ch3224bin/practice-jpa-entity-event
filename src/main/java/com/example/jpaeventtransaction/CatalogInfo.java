package com.example.jpaeventtransaction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CatalogInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kind;
    private String name;
    private String title;

    public CatalogInfo(String kind, String name, String title) {
        this.kind = kind;
        this.name = name;
        this.title = title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
