package com.seeyon.apps.myTestCtrl.controller;
import com.seeyon.apps.myTestCtrl.service.MyTestService;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.log.CtpLogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController extends BaseController {

    private static final Log LOGGER = CtpLogFactory.getLog(TestController.class);

    @Autowired
    private MyTestService myTestService;

    @GetMapping(path = "/abc/simpleTest.do", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> doSimpleTest(@RequestParam(value = "masterId", required = false) Long masterId,
                                            @RequestParam("tableName") String tableName,
                                            @RequestParam(value = "affairId", required = false) Long affairId) {
        System.out.println("========================================");
        System.out.println("【调试】数据库时间是: " + myTestService.showTime());
        System.out.println("========================================");

        Map<String, Object> response = new HashMap<>();
        Long targetId = masterId; // 默认使用前端传来的 masterId (即 moduleId)

        try {
            LOGGER.info(">>> 请求参数 | tableName: " + tableName + " | masterId: " + masterId + " | affairId: " + affairId);

            // ============================================================
            // 【核心逻辑判断】
            // 1. 如果 affairId 存在 -> 说明是流程表 -> 查 ctp_affair 获取 form_recordid
            // 2. 如果 affairId 为空 -> 说明是无流程 -> 直接用 masterId (moduleId)
            // ============================================================
            if (affairId != null) {
                System.out.println(">>> [模式识别] 检测到 affairId，判定为【流程表单】");
                LOGGER.info(">>> [模式识别] 检测到 affairId，判定为【流程表单】");
                Long realFormId = myTestService.findFormRecordIdByAffairId(affairId);

                if (realFormId != null) {
                    LOGGER.info(">>> [ID转换] affairId (" + affairId + ") -> form_recordid (" + realFormId + ")");
                    targetId = realFormId;
                } else {
                    // 防御性代码：万一查不到，还是用原来的试试，或者报错
                    LOGGER.warn(">>> [警告] 根据 affairId 未查到 form_recordid！将尝试使用原始 masterId");
                }
            } else {
                System.out.println(">>> [模式识别] affairId 为空，判定为【无流程表单】，直接使用 masterId");
                LOGGER.info(">>> [模式识别] affairId 为空，判定为【无流程表单】，直接使用 masterId: " + targetId);
            }

            if (targetId == null) {
                throw new Exception("无法确定有效的数据 ID");
            }

            // 执行查询
            Map<String, Object> rowData = myTestService.selectDynamicTable(tableName, targetId);

            if (rowData != null) {
                // 处理时间格式
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                    if (entry.getValue() instanceof LocalDateTime) {
                        entry.setValue(((LocalDateTime) entry.getValue()).format(fmt));
                    }
                }

                response.put("success", true);
                response.put("data", rowData);
            } else {
                response.put("success", false);
                response.put("message", "ID: " + targetId + " 在表 " + tableName + " 中未找到数据");
            }

        } catch (Exception e) {
            LOGGER.error("查询异常", e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}