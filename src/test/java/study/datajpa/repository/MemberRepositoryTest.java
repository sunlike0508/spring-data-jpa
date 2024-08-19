package study.datajpa.repository;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;


    @Test
    void find() {

        System.out.println("repo : " + memberRepository.getClass());

        Member member = new Member("memberA");

        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        System.out.println(member.getClass());
        System.out.println(saveMember.getClass());
        System.out.println(findMember.getClass());

        assertThat(member).isEqualTo(saveMember);
        assertThat(findMember).isEqualTo(saveMember);
        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
    }


    @Test
    void baseCRUD() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();

        assertThat(all).hasSize(2);

        long count = memberRepository.count();

        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();

        assertThat(deleteCount).isZero();
    }
}