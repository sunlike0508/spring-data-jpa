package study.datajpa.repository;


import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

@Repository
public class TeamJpaRepository {

    @PersistenceContext
    EntityManager entityManager;


    public Team save(Team team) {
        entityManager.persist(team);
        return team;
    }


    public void delete(Team team) {
        entityManager.remove(team);
    }


    public List<Team> findAll() {
        return entityManager.createQuery("select t from Team t", Team.class).getResultList();
    }


    public Optional<Team> findById(Long id) {
        Team team = entityManager.find(Team.class, id);

        return Optional.ofNullable(team);
    }


    public long count() {
        return entityManager.createQuery("select count(t) from Team t", Long.class).getSingleResult();
    }
}
