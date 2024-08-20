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

