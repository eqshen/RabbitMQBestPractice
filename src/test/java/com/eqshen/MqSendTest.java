package com.eqshen;

import com.eqshen.mq.TestProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Auther: eqshen
 * @Description
 * @Date: 2020/5/14 15:43
 */
@SpringBootTest
public class MqSendTest {
    @Autowired
    private TestProducer testProducer;
    @Test
    public void testSend(){
        long reqId = System.currentTimeMillis();
        testProducer.sendRequest(reqId,"发送测试");
    }
}
