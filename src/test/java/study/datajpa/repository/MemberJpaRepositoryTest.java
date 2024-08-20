package study.datajpa.repository;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;


@SpringBootTest
@Transactional
@Rollback(value = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;


    @Test
    void baseCRUD() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberJpaRepository.findAll();

        assertThat(all).hasSize(2);

        long count = memberJpaRepository.count();

        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deleteCount = memberJpaRepository.count();

        assertThat(deleteCount).isZero();
    }


    @Test
    void findByUserNameAndAgeGreaterThan() {

        Member member1 = new Member("member11", 10);
        Member member2 = new Member("member11", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> members = memberJpaRepository.findByUserNameAndAgeGreaterThan("member11", 10);

        assertThat(members).hasSize(2);
    }


    @Test
    void findByPage() {

        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        Member member3 = new Member("member3", 10);
        Member member4 = new Member("member4", 10);
        Member member5 = new Member("member5", 10);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5);

        List<Member> members = memberJpaRepository.findByPage(10, 1, 3);
        long totalCount = memberJpaRepository.totalCount(10);

        assertThat(members).hasSize(3);
        assertThat(totalCount).isEqualTo(5);
    }


    @Test
    void test5() {

        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        Member member3 = new Member("member3", 10);
        Member member4 = new Member("member4", 10);
        Member member5 = new Member("member5", 10);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5);

        int bulk = memberJpaRepository.bulkAgePlus(10);

        assertThat(bulk).isEqualTo(5);
    }
}