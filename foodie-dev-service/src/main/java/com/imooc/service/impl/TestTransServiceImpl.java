package com.imooc.service.impl;

import com.imooc.service.StuService;
import com.imooc.service.TestTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestTransServiceImpl implements TestTransService {
    @Autowired
    private StuService stuService;

    @Override
    public void testPropafationTrans() {
        stuService.saveParent();
        stuService.saveChildren();
    }
}
