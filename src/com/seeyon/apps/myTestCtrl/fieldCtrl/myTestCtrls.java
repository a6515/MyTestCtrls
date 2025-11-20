package com.seeyon.apps.myTestCtrl.fieldCtrl;
import com.seeyon.cap4.form.bean.fieldCtrl.FormFieldCustomCtrl;
import com.seeyon.ctp.common.log.CtpLogFactory;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class myTestCtrls extends FormFieldCustomCtrl {
    private static final Log LOGGER = CtpLogFactory.getLog(myTestCtrls.class);

    public String getKey() {
        return "456654";
    }

    public String getFieldLength() {
        return "255";
    }

    public void init() {
        setPluginId("myTestCtrls");
        setIcon("cap-icon-querystatistics");
    }

    public String getPCInjectionInfo() {
        LOGGER.info("正在打入独立控件资源66");
        return "{path:'apps_res/cap/customCtrlResources/def/',jsUri:'js/myTestCtrls.js',initMethod:'init',nameSpace:'field_" + getKey() + "'}";
    }

    public String getMBInjectionInfo() {
        return null;
    }

    public String getText() {
        return "myTest777";
    }

    public boolean canBathUpdate() {
        return false;
    }

    public List<String[]> getListShowDefaultVal(Integer externalType) {
        return null;
    }

    public String[] getDefaultVal(String defaultValue) {
        return new String[0];
    }

    public boolean canInjectionWord() {
        return false;
    }


}
