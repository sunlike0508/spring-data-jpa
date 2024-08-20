package study.datajpa.entity;


import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;


//@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @LastModifiedDate
    private LocalDateTime updateDate;


    @CreatedBy
    @Column(updatable = false)
    private String createBy;

    @LastModifiedBy
    private String updateBy;
}
