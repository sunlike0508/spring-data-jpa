# Spring Data JPA

## 파라미터 바인딩

1) 위치기반
2) 이름기반

근데 위치기반 사용 X, 가독성 별로. 그냥 이름 기반 써라.

## 반환 타입

Optional<T> 단건 조회할때, 복수개가 조회가 된다면 exception 터짐

근데 이때 exception은 spring data jpa가 springframwork 예외로 전달함.

## 페이징

### 기능

org.springframework.data.domain.Sort : 정렬기능

org.springframework.data.domain.Pageable : 페이징 기능(내부에 정렬 기능 포함)

org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징

org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1 조회)

* slice는 모바일 더보기 기능에 많이 쓰임
* page는 1이 아닌 0부터 시작

### 단점

보통 데이터가 많을수록 totalCount 쿼리가 성능이 떨어지는 경우가 많다.

그리고 예를 들어 left outer join 같은 경우 데이터 개수가 어쨌든 totalCount 개수는 일정하다. (left outer join을 잘 생각해보자)

그러면 굳이 count 쿼리에 조인을 할 필요가 없다. 해결책은 아래

```java

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // page
    Page<Member> findByAge(int age, Pageable pageable);

    // slice
    Slice<Member> findByAge(int age, Pageable pageable);

    // left outer join 일 경우 count 쿼리는 메인 엔티티만 조회
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
}

```

## 벌크성 수정 쿼리

```java

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}

```

업데이트 쿼리는 문제가 있다. 업데이트 쿼리는 영속성 컨텍스트를 거치지 않고 바로 db에 쿼리를 날린다.

순수 JPA에서는 update 쿼리 이후 clear를 사용한다. (만약 비영속 상태 객체 저장시에는 flush, clear 이후에 insert 한다.)

Spring Data JPA 에서는 다음과 같은 옵션을 제공한다.

```java

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}

```

### EntityGraph

```java

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);
}

```

@NamedEntityGraph 도 있는데... 뭐 잘 안쓴다. @NamedQuery 같이 entity class 위에 올라와 았는 것.

**어쨌든, JPA의 동작 원리를 잘 이해하는 것이 중요하다**

### JPA Hint & Lock

#### hint

정확히는 SQL Hint가 아니다.

```java

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
}

```

근데 잘 안쓴다. 뭐 진짜 읽기 조회한다고 readonly 옵션 넣어봐야 최적화가 그닥...? 성능 테스트를 해봐야한다.

거의 대부분 복잡한 쿼리를 튜닝하는 것이 좋다.

1) 쿼리 튜닝
2) 캐시 사용
3) readonly 옵션

이렇게 순서를 가져가면 될듯?

#### Lock

```java

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Member> findMemberLockByUsername(String username);
}

```

아래와 같이 쿼리가 나간다.

```sql
    select m1_0.member_id,
           m1_0.age,
           m1_0.teal_id,
           m1_0.username
    from member m1_0
    where m1_0.username = ? for update
```

실시간 서비스에서는 lock을 거의 걸지 않는다. 트래픽이 몰리는데 lock을 걸면 ... 장애 발생 가능성이 크다.

## 확장 기능

스프링 데이터 JPA repo는 인터페이스만 정의하고 구현제는 스프링이 자동으로 셍성

사용자 정의 인터페이스로 내가 정의해서 JPA 인터페이스에 extend하여 사용 가능

근데 비즈니스 로직에 따라 화면, api repo 등 기능에 따라 잘 연결하거나 분리하자.

### Auditing

* JPA : JpaBaseEntity 참조

@PrePersist, @PostPersist, @PreUpdate, @PostUpdate ->> 순수 JPA로 사용

* Spring Data JPA

```java

@EnableJpaAuditing // 이거 필수 추가
@SpringBootApplication
public class SpringDataJpaApplication {

    @Bean
    public AuditorAware<String> auditorAware() {
        // 원래는 쿠키나 세션 같이 개인정보 id 가져와서 하는 부분
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}


@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @CreatedBy
    @Column(updatable = false)
    private String createBy;

    @LastModifiedBy
    private String updateBy;
}

```

* 전체 적용
  `@EntityListeners(AuditingEntityListener.class)` 를 생략하고 스프링 데이터 JPA 가 제공하는 이벤 트를 엔티티 전체에 적용하려면 orm.xml에 다음과 같이 등록하면
  된다.

### 도메인 클래스 컨버터 (web)

음.. 실용성은 글쎄?

### 페이징과 정렬

```yml 

# 글로벌 설정
spring:
  data:
    web:
      pageable:
        default-page-size: 10
        max-page-size: 2000
```

### 새로운 엔티티 구별 전략

* `save()` 메서드
    * 새로운 엔티티면 저장( `persist` ) 새로운 엔티티가 아니면 병합( `merge` )

* 새로운 엔티티를 판단하는 기본 전략
    * 식별자가 객체일 때 `null` 로 판단
    * 식별자가 자바 기본타입일때 `0` 으로판단
    * `Persistable` 인터페이스를 구현해서 판단 로직 변경 가능

```java

package org.springframework.data.domain;

public interface Persistable<ID> {

    ID getId();

    boolean isNew();
}

```

참고: JPA 식별자 생성 전략이 `@GenerateValue` 면 `save()` 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다.

그런데 JPA 식별자 생성 전략이 `@Id` 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 `save()`를 호출한다.

따라서 이 경우 `merge()`가 호출된다.

`merge()`는 우선 DB를 호출해서 값 을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다.

따라서 `Persistable`를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.

참고로 등록시간( `@CreatedDate` )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다.

(@CreatedDate에 값이 없으면 새로운 엔티티로 판단)

```java
package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id
    private final String id;
    @CreatedDate
    private LocalDateTime createdDate;


    public Item(String id) {
        this.id = id;
    }


    @Override
    public String getId() {
        return id;
    }


    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
```

### Projection

Specification, Query By Example이 있는데 이건 좀 실무에서 적용하기 빡세다. 간단할때만 사용하자. 아니 그냥 querydsl 쓰자...

Projection 또한 간단한 조회할 때만 사용하는 것으로 결론...

* 정리

    * 프로젝션 대상이 root 엔티티면 유용하다.

    * 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!

    * 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.

    * 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자

### 네이티브

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);


    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName "
            + "FROM member m left join team t ON m.team_id = t.team_id", countQuery = "SELECT count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}

```

#### 제약

* Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)

* JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가

* 동적 쿼리 불가

**결국은 spring date jpa 기본 기능이외에는 쿼리dsl 쓴다.**
