## JPA Entity Event Listener 사용시 ConcurrentModificationException 발생건 확인

### 1. 문제 상황

저의 경우는 테스트코드에서 @PostPersist, @PostUpdate를 발생시키기 위해 saveAndFlush를 사용하였습니다.    
그런데 테스트코드를 실행하면 아래와 같은 에러가 발생합니다.

Test 클래스에는 @Transactional 어노테이션을 붙였습니다.

```
java.util.ConcurrentModificationException
	at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1013)
	at java.base/java.util.ArrayList$Itr.next(ArrayList.java:967)
	at java.base/java.util.Collections$UnmodifiableCollection$1.next(Collections.java:1054)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:610)
	at org.hibernate.engine.spi.ActionQueue.lambda$executeActions$1(ActionQueue.java:483)
	at java.base/java.util.LinkedHashMap.forEach(LinkedHashMap.java:721)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:480)
	at org.hibernate.event.internal.AbstractFlushingEventListener.performExecutions(AbstractFlushingEventListener.java:329)
	at org.hibernate.event.internal.DefaultFlushEventListener.onFlush(DefaultFlushEventListener.java:39)
	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:107)
	at org.hibernate.internal.SessionImpl.doFlush(SessionImpl.java:1425)
	at org.hibernate.internal.SessionImpl.flush(SessionImpl.java:1411)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at org.springframework.orm.jpa.SharedEntityManagerCreator$SharedEntityManagerInvocationHandler.invoke(SharedEntityManagerCreator.java:311)
	at jdk.proxy2/jdk.proxy2.$Proxy100.flush(Unknown Source)
	at org.springframework.data.jpa.repository.support.SimpleJpaRepository.flush(SimpleJpaRepository.java:658)
	at org.springframework.data.jpa.repository.support.SimpleJpaRepository.saveAndFlush(SimpleJpaRepository.java:625)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at org.springframework.data.repository.core.support.RepositoryMethodInvoker$RepositoryFragmentMethodInvoker.lambda$new$0(RepositoryMethodInvoker.java:288)
	at org.springframework.data.repository.core.support.RepositoryMethodInvoker.doInvoke(RepositoryMethodInvoker.java:136)
	at org.springframework.data.repository.core.support.RepositoryMethodInvoker.invoke(RepositoryMethodInvoker.java:120)
	at org.springframework.data.repository.core.support.RepositoryComposition$RepositoryFragments.invoke(RepositoryComposition.java:516)
	at org.springframework.data.repository.core.support.RepositoryComposition.invoke(RepositoryComposition.java:285)
	at org.springframework.data.repository.core.support.RepositoryFactorySupport$ImplementationMethodExecutionInterceptor.invoke(RepositoryFactorySupport.java:628)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.doInvoke(QueryExecutorMethodInterceptor.java:168)
	at org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.invoke(QueryExecutorMethodInterceptor.java:143)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor.invoke(DefaultMethodInvokingMethodInterceptor.java:77)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:123)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:391)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:119)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:137)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor$CrudMethodMetadataPopulatingMethodInterceptor.invoke(CrudMethodMetadataPostProcessor.java:163)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:218)
	at jdk.proxy2/jdk.proxy2.$Proxy113.saveAndFlush(Unknown Source)
	at com.example.jpaeventtransaction.TestTransaction.test(TestTransaction.java:37)
	...
```

### 2. 원인 추측

테스트코드에서 saveAndFlush를 호출하여 @PostPersist, @PostUpdate 이벤트가 발생했고,  
각 이벤트의 handler 역시 catalogInfoRepository를 통해 영속성 관련 작업을 수행하게 되었습니다.

그러나 먼저 실행된 saveAndFlush에서 flush가 수행되면서,  
각 handler에서 수행한 영속성 작업이 미쳐 처리되지 못한 것 같습니다.

이것은 정확한 원인은 아니고, 제 추측입니다.

### 3. 해결방법

추측 기반으로 handler의 트랜잭션을 분리하면 괜찮을까 생각해 보았고 테스트 해보았습니다.

각 handler에서는 `@Transactional(propagation = Propagation.REQUIRES_NEW)`로 트랜잭션을 분리하였습니다.

테스트 결과 위 오류가 발생하지 않는 것을 확인하였습니다.

### 4. 결론

Entity Event를 받아 처리하는 곳은 Entity 수정이 발생하는 곳과 트랜잭션이 분리되어야 합니다.  
Entity Event를 받아 처리한다는 것 자체가 의존성을 분리시키겠다는 의도이기도 하므로, 트랜잭션이 분리되어도 상관없다고 생각이 듭니다.  
같은 트랙잭션 내에서 사용하는 경우는 어떤 경우가 있을지 깊게 생각은 안해봤습니다.

copilot 도움으로 글을 쓰는데, 문장이 좀 어색해지네요.
