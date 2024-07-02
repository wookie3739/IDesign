package dummydata;

import com.my.interrior.IDesignApplication;
import com.my.interrior.client.shop.vignette.entity.VignetteEntity;
import com.my.interrior.client.shop.vignette.repository.VignetteRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 데이터 넣기 싫으면 @Transactional 붙이세요.
 */

@SpringBootTest(classes = IDesignApplication.class)
@Slf4j
class dummydata {

    @Autowired
    VignetteRepository vignetteRepository;

    @Test
    @DisplayName("소품 SHOP 많이 넣기")
    void 소품SHOP더미넣기() {

        List<VignetteEntity> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 100; i++) {
            int price = (random.nextInt(1000) + 1) * 1000;

            VignetteEntity vignetteEntity = VignetteEntity
                    .builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .price(price)
                    .build();

            list.add(vignetteEntity);
        }

        vignetteRepository.saveAll(list);
    }
}
