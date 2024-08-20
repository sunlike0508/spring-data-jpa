package study.datajpa.repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;


@SpringBootTest
@Transactional
@Rollback(value = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;


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


    @Test
    void findByUserNameAndAgeGreaterThan() {

        Member member1 = new Member("member11", 10);
        Member member2 = new Member("member11", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("member11", 9);

        assertThat(members).hasSize(2);
    }


    @Test
    @DisplayName("named query")
    void findByUserName() {

        Member member1 = new Member("member11", 10);
        Member member2 = new Member("member11", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsername("member11");

        assertThat(members).hasSize(2);
    }


    @Test
    @DisplayName("직접 jpql")
    void findByUserName2() {

        Member member1 = new Member("member11", 10);
        Member member2 = new Member("member11", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsernameAndAge("member11", 10);

        assertThat(members).hasSize(1);
    }


    @Test
    void test() {

        Member member1 = new Member("member11", 10);
        Member member2 = new Member("member12", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> members = memberRepository.findUserNames();

        assertThat(members).hasSize(2);

        members.forEach(m -> System.out.println(m));
    }


    @Test
    void test2() {

        Team team = new Team("teatA");

        teamRepository.save(team);

        Member member1 = new Member("member1", 10);

        member1.setTeam(team);

        memberRepository.save(member1);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        memberDtos.forEach(memberDto -> System.out.println(memberDto));
    }


    @Test
    void test3() {

        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(List.of("member1", "member2"));

        assertThat(members).hasSize(2);

        members.forEach(m -> System.out.println(m));
    }


    @Test
    void findByPage() {

        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        Member member3 = new Member("member3", 10);
        Member member4 = new Member("member4", 10);
        Member member5 = new Member("member5", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> membersPage = memberRepository.findByAge(10, pageRequest);
        //Slice<Member> membersPage = memberRepository.findByAge(10, pageRequest);

        List<Member> content = membersPage.getContent();

        assertThat(content).hasSize(3);
        assertThat(membersPage.getTotalElements()).isEqualTo(5);
        assertThat(membersPage.getNumber()).isZero(); // 현재 페이지 숫자
        assertThat(membersPage.getTotalPages()).isEqualTo(2); // page 개수
        assertThat(membersPage.isFirst()).isTrue(); // 첫장인가?
        assertThat(membersPage.hasNext()).isTrue(); // 다음장 있나?

        //        Page<MemberDto> memberDtoPage =
        //                membersPage.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }


    @Test
    void test5() {

        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        Member member3 = new Member("member3", 10);
        Member member4 = new Member("member4", 10);
        Member member5 = new Member("member5", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        int bulk = memberRepository.bulkAgePlus(10);

        assertThat(bulk).isEqualTo(5);

        List<Member> members = memberRepository.findAll();

        members.forEach(member -> System.out.println(member.getAge()));
    }


    @Test
    void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("team = " + member.getTeam().getClass());
            System.out.println("teamName = " + member.getTeam().getName());
        }
    }


    @Test
    void findMemberFetchJoin() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //List<Member> members = memberRepository.findMemberFetchJoin();
        //List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraph();
        //List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("team = " + member.getTeam().getClass());
            System.out.println("teamName = " + member.getTeam().getName());
        }
    }


    @Test
    void queryHint() {
        Member member1 = new Member("member1", 10);

        memberRepository.save(member1);

        em.flush();
        em.clear();

        //Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");

        findMember.setUsername("member2");

        em.flush();
    }


    @Test
    void findMemberLockByUsername() {
        Member member1 = new Member("member1", 10);

        memberRepository.save(member1);

        em.flush();
        em.clear();

        //Member findMember = memberRepository.findById(member1.getId()).get();
        List<Member> findMember = memberRepository.findMemberLockByUsername("member1");

    }


    @Test
    void findAllMemberCustom() {

        Member member1 = new Member("member11", 10);
        Member member2 = new Member("member11", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findAllMemberCustom();

        assertThat(members).hasSize(2);
    }


    @Test
    void projections() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();


        List<UsernameOnly> usernameOnlyList = memberRepository.findProjectionByUsername("member1");

        for(UsernameOnly usernameOnly : usernameOnlyList) {
            System.out.println(usernameOnly.getUsername());
        }
    }
}