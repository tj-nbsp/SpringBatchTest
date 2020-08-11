package org.example.support;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * junit with spring boot 测试基类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class SpringTestCase {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}