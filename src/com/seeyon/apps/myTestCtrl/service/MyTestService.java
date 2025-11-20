package com.seeyon.apps.myTestCtrl.service; // 建议包名也叫 service

import org.apache.ibatis.annotations.Param;


import java.util.Date;
import java.util.Map;

public interface MyTestService {

    Map<String, Object> selectDynamicTable(@Param("tableName") String tableName, @Param("id") Long id);

    Long findFormRecordIdByAffairId(@Param("affairId") Long affairId);
    Date showTime();

}