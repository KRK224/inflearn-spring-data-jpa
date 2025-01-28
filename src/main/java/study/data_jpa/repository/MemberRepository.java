package study.data_jpa.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // Query Method 1) Method Name Query
    // Top~ scrolling, By~ search condition
    List<Member> findTop3HelloBy();

    // Query Method 2) Named Query using @Query
    @Query(name = "Member.findByUsername") // 기본적으로 NamedQuery를 Entity.Method명으로 찾기 때문에 생략 가능.
    List<Member> findByUsername(@Param("username") String username);

    // Query Method 3) using @Query
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // Spring Data JPA에서는 다양한 반환 타입을 제공한다.
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);
}
