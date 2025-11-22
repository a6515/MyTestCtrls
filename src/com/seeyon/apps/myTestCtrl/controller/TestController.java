package com.seeyon.apps.myTestCtrl.controller;

import com.seeyon.apps.myTestCtrl.utils.ctrlFormModificationService;
import com.seeyon.ctp.common.affair.manager.AffairManager;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController extends BaseController {

    private static final Log LOGGER = CtpLogFactory.getLog(TestController.class);

    // 注入我们封装好的 CAP4 操作服务
    @Autowired
    private ctrlFormModificationService formService;

    // 注入致远标准的事项管理器 (用于 ID 转换)
    @Autowired
    private AffairManager affairManager;

    @GetMapping(path = "/abc/simpleTest.do", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> doSimpleTest(
            @RequestParam(value = "masterId", required = false) Long masterId,
            @RequestParam(value = "formId", required = false) Long formId, // 前端现在传这个参数了
            @RequestParam(value = "affairId", required = false) Long affairId) {

        Map<String, Object> response = new HashMap<>();
        Long targetRecordId = masterId; // 默认为 masterId

        try {
            System.out.println(">>> 致远三方接口操作表单方法开始");
            System.out.println(">>> 请求参数 | formId: " + formId + " | masterId: " + masterId + " | affairId: " + affairId);

            LOGGER.info(">>> 请求参数 | formId: " + formId + " | masterId: " + masterId + " | affairId: " + affairId);

            if (formId == null) {
                System.out.println("缺少必要参数 formId，无法加载表单定义");
                throw new Exception("缺少必要参数 formId，无法加载表单定义");
            }

            // ============================================================
            // 【核心逻辑优化】使用标准 API 进行 ID 转换
            // ============================================================
            if (affairId != null) {
                System.out.println(">>> [三方接口模式识别] 检测到 affairId，正在通过 AffairManager 获取真实数据ID...");
                LOGGER.info(">>> [三方接口模式识别] 检测到 affairId，正在通过 AffairManager 获取真实数据ID...");

                // 使用 Manager 根据 ID 查出实体对象
                CtpAffair affair = affairManager.get(affairId);

                // 从实体对象中获取 formRecordId
                if (affair != null) {
                    // getFormRecordId() 是 CtpAffair 类的方法，不是 Manager 的
                    targetRecordId = affair.getFormRecordid();
                } else {
                    System.out.println(">>> [警告] 未找到对应的 Affair 对象，将尝试使用原始 masterId");
                    LOGGER.warn(">>> [警告] 未找到对应的 Affair 对象，将尝试使用原始 masterId");
                }
            } else {
                System.out.println(">>> [三方接口模式识别] 无流程表单，直接使用 masterId: " + targetRecordId);
                LOGGER.info(">>> [三方接口模式识别] 无流程表单，直接使用 masterId: " + targetRecordId);
            }

            if (targetRecordId == null) {
                System.out.println(">>> [警告] 无法确定有效的主表数据 ID (RecordId)");
                throw new Exception("无法确定有效的主表数据 ID (RecordId)");
            }

            // ============================================================
            // 【数据查询】调用 CAP4FormManager 封装服务，不再查数据库
            // ============================================================
            Map<String, Object> rowData = formService.queryFormData(formId, targetRecordId);

            if (rowData != null && !rowData.isEmpty()) {
                response.put("success", true);
                response.put("data", rowData);
            } else {
                response.put("success", false);
                response.put("message", "未找到表单数据 (formId=" + formId + ", recordId=" + targetRecordId + ")");
            }

        } catch (Exception e) {
            LOGGER.error("查询异常", e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}