package study.datajpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
// springboot는 필요없다. spring date jpa를 사용하기 위한 설정
public class SpringDataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }


    @Bean
    public AuditorAware<String> auditorAware() {
        // 원래는 쿠키나 세션 같이 개인정보 id 가져와서 하는 부분
        return () -> Optional.of(UUID.randomUUID().toString());
    }

}
