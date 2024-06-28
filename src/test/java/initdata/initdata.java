package initdata;

import com.my.interrior.IDesignApplication;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 데이터 넣기 싫으면 @Transactional 붙이세요.
 */

@SpringBootTest(classes = IDesignApplication.class)
@Slf4j
class initdata {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저 정보 넣기")
    void 유저정보넣기() {

        if (userRepository.existsByUId("qqq")) {
            log.info("이미 init 유저 존재함");

            return;
        }

        UserEntity userEntity = UserEntity
                .builder()
                .UId("qqq")
                .UPw(passwordEncoder.encode("1"))
                .UName("이름")
                .UMail("메일")
                .UBirth("생일")
                .UTel("번호")
                .build();

        UserEntity savedEntity = userRepository.save(userEntity);

        assertThat(savedEntity).isNotNull();
    }
}
