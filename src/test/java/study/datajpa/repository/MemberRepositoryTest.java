package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Member;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;


    @Test
    void find() {

        //Member member = new Member("memberA");

        Member savedMember = memberRepository.findById(1L).get();

        assertThat(savedMember.getId()).isEqualTo(savedMember.getId());
    }
}