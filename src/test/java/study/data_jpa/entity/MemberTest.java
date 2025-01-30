package study.data_jpa.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> selectMFromMemberM = em.createQuery("select m from Member m join fetch m.team t", Member.class)
                .getResultList();

        for (Member member : selectMFromMemberM) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given
        Member member = new Member("member1", 10);
        em.persist(member); // @PrePersist

        //when
        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); // @PreUpdate
        em.clear();

        Member findMember = em.find(Member.class, member.getId());

        //then
        System.out.println("findMember.created = " + findMember.getCreatedDate());
//        System.out.println("findMember.updated = " + findMember.getUpdatedDate());
        System.out.println("findMember.lastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember.createdBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.lastModifiedBy() = " + findMember.getLastModifiedBy());
    }
}