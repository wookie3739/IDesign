package initdata;

import com.my.interrior.IDesignApplication;
import com.my.interrior.client.shop.vignette.entity.VignetteEntity;
import com.my.interrior.client.shop.vignette.repository.VignetteRepository;
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

    @Autowired
    VignetteRepository vignetteRepository;

    @Test
    @DisplayName("유저 정보 넣기")
    void 유저정보넣기() {

        log.info("켜지나요?");
        log.debug("ㅇ");


        if (userRepository.existsByUId("qqq")) {
            log.info("이미 init 유저 존재함");

            return;
        }

        UserEntity userEntity = UserEntity
                .builder()
                .UId("q")
                .UPw(passwordEncoder.encode("1"))
                .UName("이름1")
                .UMail("메일1")
                .UBirth("생일1")
                .UTel("번호1")
                .build();

        UserEntity savedEntity = userRepository.save(userEntity);

        assertThat(savedEntity).isNotNull();
    }

    @Test
    @DisplayName("소품 SHOP 넣기")
    void 소품SHOP넣기() {

        if (vignetteRepository.existsByNo(1)) {
            log.info("이미 init 존재함");

            return;
        }

        VignetteEntity vignetteEntity = VignetteEntity
                .builder()
                .title("제목")
                .content("내용")
                .price(10000)
                .build();

        VignetteEntity savedEntity = vignetteRepository.save(vignetteEntity);

        assertThat(savedEntity).isNotNull();
    }
}
