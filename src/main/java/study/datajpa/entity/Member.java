package study.datajpa.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NamedQuery;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(name = "findByUsername", query = "select m from Member m where m.username = :username")
public class Member extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teal_id")
    private Team team;


    public Member(String username) {
        this.username = username;
    }


    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }


    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        changeTeam(team);

    }


    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
