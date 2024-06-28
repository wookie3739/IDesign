package dummydata;

import com.my.interrior.IDesignApplication;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = IDesignApplication.class)
@Transactional
@Slf4j
class dummydata {
}
