package study.data_jpa.repository;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // Query Method 1) Method Name Query
    // Top~ scrolling, By~ search condition
    List<Member> findTop3HelloBy();

    // Query Method 2) Named Query using @Query
    @Query(name = "Member.findByUsername")
    // 기본적으로 NamedQuery를 Entity.Method명으로 찾기 때문에 생략 가능.
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

    // Paging
    // 반환 타입이 Page인 경우, count 쿼리까지 자동으로 실행한다.
    // count 쿼리에 대해서 성능 최적화를 countQuery를 통해 할 수 있다.

    /**
     * @param age
     * @param pageable
     * @return
     */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // Bulk Update
    // Modifying 어노테이션을 활용해야 executeUpdate()를 실행한다.
    @Modifying(clearAutomatically = true) // clearAutomatically = true 옵션을 통해 업데이트 후에 entity manager를 초기화한다.
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkUpdate(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    // 공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // '메서드 이름으로 쿼리' 사용 가능
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @EntityGraph(value = "Member.all")
    @Query("select m from Member m")
    List<Member> findNamedEntityGraph();

    // QueryHints
    @QueryHints(value = {@QueryHint(name = "org.hibernate.readOnly", value = "true")})
    Member findReadOnlyByUsername(String username);
}
