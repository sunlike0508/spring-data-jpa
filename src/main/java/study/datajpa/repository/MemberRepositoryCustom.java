package study.datajpa.repository;

import java.util.List;
import study.datajpa.entity.Member;

/**
 * 쿼리dsl 쓸떄 많이 쓴다.
 */
public interface MemberRepositoryCustom {

    List<Member> findAllMemberCustom();
}
