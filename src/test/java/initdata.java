import com.my.interrior.IDesignApplication;
import com.my.interrior.client.user.UserEntity;
import com.my.interrior.client.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = IDesignApplication.class)
public class initdata {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저 정보 넣기")
    void 유저정보넣기() {
        UserEntity userEntity = UserEntity
                .builder()
                .UId("qqq")
                .UPw(passwordEncoder.encode("1"))
                .UName("이름1")
                .UMail("메일1")
                .UBirth("생일1")
                .UTel("번호1")
                .build();

        UserEntity savedEntity = userRepository.save(userEntity);

        assertThat(savedEntity).isNotNull();
    }
}
