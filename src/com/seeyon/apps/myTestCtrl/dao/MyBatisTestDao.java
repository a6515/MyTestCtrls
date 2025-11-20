package com.seeyon.apps.myTestCtrl.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.Map;
@Mapper
public interface MyBatisTestDao {

    /**
     * 万能动态查询：查指定表的指定行
     * @param tableName 表名 (例如 formmain_0015)
     * @param id 主键ID
     * @return 包含该行所有字段的 Map
     */
    @Select("SELECT * FROM ${tableName} WHERE id = #{id}")
    Map<String, Object> selectDynamicTable(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 【关键】根据 affairId 查 form_recordid
     * 这对应流程表单的真实业务数据 ID
     */
    @Select("SELECT form_recordid FROM ctp_affair WHERE id = #{affairId}")
    Long findFormRecordIdByAffairId(@Param("affairId") Long affairId);

    @Select("SELECT now()")
    Date showTime();
}