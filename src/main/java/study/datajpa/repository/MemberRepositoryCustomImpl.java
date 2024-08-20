package study.datajpa.repository;


import java.util.List;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final EntityManager em;


    @Override
    public List<Member> findAllMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();

    }
}
