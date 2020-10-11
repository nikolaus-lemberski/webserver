package com.lemberski.webserver;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={AppConfig.class})
@TestPropertySource("classpath:application-test.properties")
public abstract class ContextAwareBaseTest {
}
