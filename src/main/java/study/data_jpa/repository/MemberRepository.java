package study.data_jpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import study.data_jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
}
