package study.data_jpa.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    // bulkUpdate 이후에 영속성 context를 초기화 하기 위해.
    @Autowired
    EntityManager em;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThanTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTop3HelloByTest() {
        Member memberA = new Member("AAA", 10);
        Member memberB = new Member("BBB", 20);
        Member memberC = new Member("CCC", 30);
        Member memberD = new Member("DDD", 40);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
        assertThat(top3HelloBy.size()).isEqualTo(3);
    }

    @Test
    public void findByUsername() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> findMembers = memberRepository.findByUsername("memberA");
        Member findMember = findMembers.get(0);
        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    public void findUserTest() throws Exception {
        //given
        Member memberA = new Member("AAA", 10);
        Member memberB = new Member("BBB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        List<Member> result = memberRepository.findUser("AAA", 10);
        //then
        assertThat(result.get(0)).isEqualTo(memberA);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findUserNameListTest() throws Exception {
        //given
        Member memberA = new Member("AAA", 10);
        Member memberB = new Member("BBB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        List<String> usernameList = memberRepository.findUsernameList();
        //then
        assertThat(usernameList.size()).isEqualTo(2);
        assertThat(usernameList).containsExactly("AAA", "BBB");
    }

    @Test
    public void findMemberDtoTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        // Dirty Checking으로 감지.
        m1.changeTeam(teamA);
        //when
        // 이 순간에 em.flush를 하기 때문에 changeTeam이 반영된다.
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        //then
        for (MemberDto dto : memberDto) {
            System.out.println("dto.getUsername() = " + dto.getUsername());
            System.out.println("dto.getTeamName() = " + dto.getTeamName());
            assertThat(dto.getUsername()).isEqualTo("AAA");
            assertThat(dto.getTeamName()).isEqualTo("teamA");
        }
    }

    @Test
    public void findByNamesTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        //then
        for (Member member : byNames) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnTypeTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //when
        // List로 반환
        List<Member> results = memberRepository.findListByUsername("AAA");
        results.forEach(System.out::println);
        // 단건으로 반환
        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);
        // Optional로 반환
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");
        optional.ifPresent(member -> System.out.println("optional.get() = " + member));
        //then
    }

    // Junit4
//    @Test(expected = IncorrectResultSizeDataAccessException.class)
    @Test
    public void returnTypeFailTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("BBB", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        //when
        // List로 반환
        List<Member> difekj = memberRepository.findListByUsername("difekj");
        // List로 반환할 시에 데이터가 없으면 Null이 아니라 Empty Collection을 반환한다.
        System.out.println("difekj.size() = " + difekj.size());

        // 단건 반환
        /*
         ** 이 경우에는 순수 JPA에서는 No Result Exception이 발생하지만,
         * Spring Data JPA에서 내부적으로 try catch 처리 후 null 반환.
         */
        Member findMember = memberRepository.findMemberByUsername("akdfjeij");
        System.out.println("findMember = " + findMember); // null 반환.

        // Optional로 반환
        Optional<Member> optional = memberRepository.findOptionalByUsername("akdfjeij");
        System.out.println("optional.isEmpty() = " + optional.isEmpty());

        // Exception 발생
        /**
         * 순수 JPA에서는 NonUniqueResultException이 발생하지만,
         * Spring Data JPA에서는 Spring 예외로 추상화 한 IncorrectResultSizeDataAccessException 발생.
         */
        assertThrows(IncorrectResultSizeDataAccessException.class, () -> {
            memberRepository.findMemberByUsername("BBB");
        });
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10, offset = 0, limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // Entity -> DTO로 반환
        // 이대로 Page<Dto> 형식으로 controller로 반환해도 가능.
        Page<MemberDto> dtoMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        long totalElements = page.getTotalElements();
        List<Member> content = page.getContent();

        //then
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        assertThat(totalElements).isEqualTo(5);
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void pagingBySlice() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10, offset = 0, limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Direction.DESC, "username"));

        //when
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        List<Member> content = slice.getContent();

        //then
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.hasNext()).isTrue();
        assertThat(slice.isFirst()).isTrue();
    }

    @Test
    public void bulkUpdatetest() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 30));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCnt = memberRepository.bulkUpdate(20);
//        em.clear(); # @Modifying(clearAutomatically = true) 옵션으로 자동 적용.
        List<Member> member5 = memberRepository.findByUsername("member5");
        System.out.println("member5.get(0).getAge() = " + member5.get(0).getAge());

        //then
        assertThat(resultCnt).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();
        //when
        // 공통 메서드 오버라이드 EntityGraph 추가
//        List<Member> members = memberRepository.findAll();

        // JPQL + EntityGraph
        List<Member> members = memberRepository.findMemberEntityGraph();

        //JPQL + NamedEntityGraph
//        List<Member> namedEntityGraph = memberRepository.findNamedEntityGraph();

        // 메서드 이름 쿼리 + EntityGraph
//        List<Member> member1 = memberRepository.findEntityGraphByUsername("member1");

        //then
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // N+1 문제 발생.
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트의 변경 사항을 DB에 동기화하는 작업, 영속성 컨텍스트를 초기화X
        em.clear(); // 영속성 컨텍스트 초기화 O
        //when
        // queryHints 로 readOnly 옵션을 주면, 비교를 위한 Snapshot 자체를 생성하지 않는다.
        // em.flush도 무시.
        // @Transactional(readOnly = true)와 동일
        Member member1 = memberRepository.findReadOnlyByUsername("member1");
        Long id = member1.getId();
        member1.setUsername("member2");
        // 업데이트 쿼리가 나가지 않는다.
        em.flush();
        em.clear();

        //then
        Member member = memberRepository.findById(id).get();
        System.out.println("member.getUsername() = " + member.getUsername());
        assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    public void findMemberCustomTest() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        //when
        List<Member> result = memberRepository.findMemberCustom();

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void queryByExample() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        // Probe 생성
        Member member = new Member("m1");
        // inner join 가능, 그러나 left join은 불가능.
        Team teamExample = new Team("teamA");
        member.setTeam(teamExample);

        // primitive 타입의 기본값은 0이기 때문에 자동으로 검색되는 것을
        // 막기 위해 ExampleMatcher 사용.
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> members = memberRepository.findAll(example);

        assertThat(members.get(0).getUsername()).isEqualTo("m1");

        //then
    }


    @Test
    public void projectionsTest() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        List<UserNameOnly> result = memberRepository.findProjectionByUsername("m1");
        //then
        for (UserNameOnly userNameOnly : result) {
            System.out.println("userNameOnly.getClass() = " + userNameOnly.getClass());
            System.out.println("userNameOnly = " + userNameOnly);
            System.out.println("userNameOnly.getUsername() = " + userNameOnly.getUsername());
            System.out.println("userNameOnly.getUsernameAndAge() = " + userNameOnly.getUsernameAndAge());
        }
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void projectDtoTest() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        // class로 Projection 진행 시에는 정확한 쿼리가 나간다. select m.username from Member m where m.username = ?
        // Proxy가 아닌 구체 클래스가 담긴다.
        List<UsernameOnlyDto> m1 = memberRepository.findProjectionDtoByUsername("m1");

        //then
        for (UsernameOnlyDto usernameOnlyDto : m1) {
            System.out.println("usernameOnlyDto.getUsername() = " + usernameOnlyDto.getUsername());
        }
        assertThat(m1.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void dynamicProjectionTest() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        List<UsernameOnlyDto> m1 = memberRepository.findDynamicProjectByUsername("m1", UsernameOnlyDto.class);

        //then
        for (UsernameOnlyDto usernameOnlyDto : m1) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto);
            System.out.println("usernameOnlyDto.getUsername() = " + usernameOnlyDto.getUsername());
        }

        assertThat(m1.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void nestedClosedProjectionTest() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        List<NestedClosedProjection> m1 = memberRepository.findNestedClosedProjectionByUsername("m1");
        //then
        for (NestedClosedProjection nestedClosedProjection : m1) {
            System.out.println("nestedClosedProjection.getUsername() = " + nestedClosedProjection.getUsername());
            System.out.println("nestedClosedProjection.getTeam().getName() = " + nestedClosedProjection.getTeam().getName());
        }
    }

    @Test
    public void nativeQueryTest() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        Member m1 = memberRepository.findByNativeQuery("m1");
        //then
        assertThat(m1.getUsername()).isEqualTo("m1");
        assertThat(m1.getAge()).isEqualTo(10);
    }

    @Test
    public void nativeQueryProjectionTest() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        //when
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<MemberProjection> byNativeProjection = memberRepository.findByNativeProjection(pageRequest);
        //then
        assertThat(byNativeProjection.getSize()).isEqualTo(3);
        assertThat(byNativeProjection.getNumber()).isEqualTo(0);
        assertThat(byNativeProjection.getNumberOfElements()).isEqualTo(2);
    }


}