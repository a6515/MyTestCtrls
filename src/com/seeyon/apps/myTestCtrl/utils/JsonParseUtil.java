package com.seeyon.apps.myTestCtrl.utils;

import com.seeyon.ctp.common.log.CtpLogFactory;
import org.apache.commons.logging.Log;
import org.json.JSONObject;
public class JsonParseUtil {
    private static final Log LOGGER = CtpLogFactory.getLog(JsonParseUtil.class);


    public static boolean isPass(String result) {
        try {
            // 2. 解析最外层的JSON响应体
            JSONObject rootJson = new JSONObject(result);
            // 3. 【重要】先从最外层获取 "response" 对象
            if (rootJson.has("response")) {
                JSONObject responseJson = rootJson.getJSONObject("response"); // 获取 response 对象
                // 4. 接下来的逻辑和之前一样，在 responseJson 内部检查 "head"
                if (responseJson.has("head")) {
                    JSONObject head = responseJson.getJSONObject("head");
                    String resultCode = head.getString("resultcode");
                    if ("SUC0000".equals(resultCode)) {
                        LOGGER.info("接口业务处理成功！resultcode 为 SUC0000。");
                        // 成功逻辑
                    } else {
                        String errorMsg = head.getString("resultmsg");
                        String fullError = "招商接口业务处理失败！错误码: " + resultCode + ", 错误信息: " + errorMsg;
                        LOGGER.error(fullError);
                        return false;

                    }
                } else {
                    // response 内部没有 head
                    String fullError = "招商接口返回格式异常，'response'内缺少'head'字段。响应内容: " + result;
                    LOGGER.error(fullError);
                    return false;
                }
            } else {
                // 5. 如果连 "response" 字段都没有，说明返回了完全错误的格式
                String fullError = "招商接口返回异常格式，缺少'response'字段。响应内容: " + result;
                LOGGER.error(fullError);
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("报错" + e);
            return false;
        }


        return true;
    }
}
