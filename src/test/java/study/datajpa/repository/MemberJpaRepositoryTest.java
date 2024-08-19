package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Member;


@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;


    @Test
    void save() {
    }


    @Test
    void find() {

        Member member = memberJpaRepository.find(1L);

        assertThat(member.getId()).isEqualTo(member.getId());
    }
}