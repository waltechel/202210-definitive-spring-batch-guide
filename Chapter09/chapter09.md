
# chapter09 ItemWriter

---

### JpaItemReader

`org.springframework.batch.item.writer.JpaItemWriter`를 보면 JPA의 `javax.persistence.EntityManager`를 감싼 간단한 래퍼에 불과하다는 것을 알 수 있다.

관계형 데이터베이스는 현대 엔터프라이즈 환경에서 좋든 싫든 사용되는데, 앞서 살펴본 것처럼 스프링 배치를 사용하면 데이터베이스에 잡의 실행 결과를 쉽게 저장할 수 있다.

많은 기업에서 몽고DB와 관련한 논쟁 중 하나는, 역사적으로 몽고DB가 ACID(Atomicity, Consistency, Isolation, Durability) 트랜잭션을 지원하지 않는 것이다.

---

### JmsItemWriter

JMS는 둘 이상의 엔드포인트간에 통신하는 메시지 지향적인 방식이다. 자바 애플리케이션은 지점간 통신 또는 발행 구독 모델을 사용해 해당 메시징 구현체와 인터페이스할 수 있는 다른 모든 기술과 통신할 수 있다.