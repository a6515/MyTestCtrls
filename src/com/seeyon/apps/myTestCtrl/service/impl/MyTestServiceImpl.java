package com.seeyon.apps.myTestCtrl.service.impl;
import com.seeyon.apps.myTestCtrl.service.MyTestService;
import com.seeyon.apps.myTestCtrl.dao.MyBatisTestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // 核心注解

import java.util.Date;
import java.util.Map;

/**
 * 加上 @Service 注解，Spring 容器扫描到它时，
 * 会自动把它注册为 Bean，默认 ID 通常是 "myTestServiceImpl"
 */
@Service
public class MyTestServiceImpl implements MyTestService {

    @Autowired
    private MyBatisTestDao myBatisTestDao;

    @Override
    public Map<String, Object> selectDynamicTable(String tableName, Long id) {
        return myBatisTestDao.selectDynamicTable(tableName, id);
    }

    @Override
    public Long findFormRecordIdByAffairId(Long affairId) {
        return myBatisTestDao.findFormRecordIdByAffairId(affairId);
    }

    @Override
    public Date showTime() {
        return myBatisTestDao.showTime();
    }
}