package com.example.jpaeventtransaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(BusinessServiceEventListener.class)
public class BusinessService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupCode;
    private String groupName;
    private String code;
    private String name;

    public BusinessService(String groupCode, String groupName, String code, String name) {
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.code = code;
        this.name = name;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
