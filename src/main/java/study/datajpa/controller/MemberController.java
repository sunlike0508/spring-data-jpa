package study.datajpa.controller;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;


    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(1L).get();

        return member.getUsername();
    }


    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {

        return member.getUsername();
    }


    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }


    @PostConstruct
    public void init() {
        //        for(int i = 0; i < 100; i++) {
        //            memberRepository.save(new Member("shin" + i, i));
        //        }

    }
}
