package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        return memberRepository.findById(id).get().getUsername();
    }

    // 도메인 클래스 컨버터 사용
    // 잘 사용하지 않고 만약 쓴다면 조회용으로만 사용할 것.
    @GetMapping("/members2/{id}")
    public String findMember(@NonNull  @PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
//        return memberRepository.findAll(pageable).map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        return memberRepository.findAll(pageable).map(MemberDto::new); // new MeberDto(Member member)
    }

//    @PostConstruct
//    public void init () {
//        for(int i = 0; i< 100; i++) {
//            memberRepository.save(new Member("user" + i, i));
//        }
//    }
}
