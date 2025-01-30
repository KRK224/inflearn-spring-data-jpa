package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import study.data_jpa.entity.Member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
