package com.yupi.project.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Test
    public void invokeCount() {
        boolean b = userInterfaceInfoService.invokeCount(4l, 1l);
        Assertions.assertTrue(b);
    }
}