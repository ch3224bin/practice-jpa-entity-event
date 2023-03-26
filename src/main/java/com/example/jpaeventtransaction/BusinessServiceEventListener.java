package com.example.jpaeventtransaction;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;

public class BusinessServiceEventListener {

    @PostPersist
    public void postPersist(BusinessService businessService) {
        SpringContext.getBeansOfType(BusinessServicePersistHandler.class)
                .forEach(handler -> handler.persist(businessService));
    }

    @PostUpdate
    public void postUpdate(BusinessService businessService) {
        SpringContext.getBeansOfType(BusinessServiceUpdateHandler.class)
                .forEach(handler -> handler.update(businessService));
    }

    @Component
    static class SpringContext implements ApplicationContextAware {

        private static ApplicationContext applicationContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        public static <T> Collection<T> getBeansOfType(Class<T> type) {
            return applicationContext.getBeansOfType(type).values();
        }
    }
}
