package study.datajpa.entity;


import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;


    @PrePersist
    public void perPersist() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }


    @PreUpdate
    public void perUpdate() {
        updateDate = LocalDateTime.now();
    }
}
