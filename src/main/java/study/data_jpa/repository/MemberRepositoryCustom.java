package study.data_jpa.repository;

import java.util.List;
import study.data_jpa.entity.Member;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
