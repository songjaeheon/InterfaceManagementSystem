package com.ims.eims;

import com.ims.eims.config.AppConfig;
import com.ims.eims.config.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, WebConfig.class})
@WebAppConfiguration
public class EimsApplicationTests {

    @Test
    public void contextLoads() {
        // "If this test passes, it means our manual wiring is perfect! Josh Long would be proud."
    }
}
