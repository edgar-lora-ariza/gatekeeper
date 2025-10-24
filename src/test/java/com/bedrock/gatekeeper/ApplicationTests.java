package com.bedrock.gatekeeper;

import com.bedrock.gatekeeper.config.TestH2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest()
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestH2Config.class})
class ApplicationTests {

  @Test
  void contextLoads() {
    //No implementation
  }

}
