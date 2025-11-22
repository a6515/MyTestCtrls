package com.seeyon.apps.myTestCtrl.utils;

import com.seeyon.cap4.form.bean.FormDataMasterBean;
import com.seeyon.cap4.form.bean.FormDataSubBean;
import com.seeyon.cap4.form.service.CAP4FormManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.log.CtpLogFactory;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//注意，此工具类只适用于第三方工具类，如contrlller
@Component("ctrlFormModificationService")
public class ctrlFormModificationService {
    /**
     * 通用查询方法：根据 formId 和 recordId 获取表单全量数据
     * @param formId 表单定义ID (ContentTemplateId)
     * @param recordId 表单数据主键 (ModuleId / ContentDataId)
     * @return 包含主表和明细表数据的 Map
     */
    public Map<String, Object> queryFormData(Long formId, Long recordId) throws Exception {
        CAP4FormManager cap4FormManager = (CAP4FormManager) AppContext.getBean("cap4FormManager");

        // 1. 使用 Manager 查询数据对象 (这是最核心的一步，替代了 select * from table)
        // findDataById(long var1, long var3, String[] var5)
        FormDataMasterBean masterBean = cap4FormManager.findDataById(recordId, formId, null);

        if (masterBean == null) {
            return null;
        }

        // 2. 组装返回数据 (将 Bean 转换为 Map 方便前端显示)
        Map<String, Object> resultMap = new HashMap<>();

        // A. 获取主表数据 (getRowData返回的是纯净的业务数据Map)
        //
        resultMap.putAll(masterBean.getRowData());

        // B. 获取所有明细表数据 (自动遍历所有子表)
        //
        Map<String, List<FormDataSubBean>> subTables = masterBean.getSubTables();
        if (subTables != null) {
            for (Map.Entry<String, List<FormDataSubBean>> entry : subTables.entrySet()) {
                String subTableName = entry.getKey();
                List<FormDataSubBean> subRows = entry.getValue();

                List<Map<String, Object>> subDataList = new ArrayList<>();
                for (FormDataSubBean row : subRows) {
                    // 将每一行明细数据转为 Map
                    Map<String, Object> rowData = row.getRowData();
                    rowData.put("id", row.getId()); // 补充行ID
                    subDataList.add(rowData);
                }
                // 将明细表数据放入结果，key 为表名 (例如 formson_0018)
                resultMap.put(subTableName, subDataList);
            }
        }

        return resultMap;
    }

    private static final Log LOGGER = CtpLogFactory.getLog(ctrlFormModificationService.class);

    public void updateExternal(Long formId, Long recordId) throws Exception {
        CAP4FormManager cap4FormManager = (CAP4FormManager) AppContext.getBean("cap4FormManager");

        // 1. 【查】获取主对象
        FormDataMasterBean masterBean = cap4FormManager.findDataById(recordId, formId, null); //

        if (masterBean != null) {
            // --- 主表操作 ---
            // Object mainVal = masterBean.getFieldValue("field0001");

            // ==========================================
            // 2. 【查明细表】并横向打印
            // ==========================================
            // 获取明细表所有行 (假设表名是 formson_0001)
            List<FormDataSubBean> detailRows = masterBean.getSubData("formson_0001"); //

            if (detailRows != null) {
                System.out.println("====== 明细表数据开始 ======");
                // 打印表头（可选）
                System.out.println("行ID \t\t | 列1(field0010) \t | 列2(field0011) \t | 列3(field0012)");

                // 遍历每一行
                for (FormDataSubBean row : detailRows) {
                    // 这一行的所有列数据取出来
                    Object col1 = row.getFieldValue("field0010"); //
                    Object col2 = row.getFieldValue("field0011");
                    Object col3 = row.getFieldValue("field0012");

                    // 拼接成一行字符串打印 (使用 \t 制表符或者空格隔开)
                    String oneRowText = "ID:" + row.getId() + " \t | " + col1 + " \t\t | " + col2 + " \t\t | " + col3;

                    // 这一句保证了它是横向排列的：一次打印一行
                    System.out.println(oneRowText);
                }
                System.out.println("====== 明细表数据结束 ======");
            }

            // 3. 【存】
            cap4FormManager.saveOrUpdateFormData(masterBean, formId, true); //
        }
    }
}