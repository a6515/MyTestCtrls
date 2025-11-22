/**
 * 前端自定义控件 - 按钮 (CSDK 优化版)
 * 功能：利用致远官方 CSDK 接口精准获取上下文数据
 */
(function(factory) {
    var nameSpace = 'field_456654';
    if (!window[nameSpace]) {
        var Builder = factory();
        window[nameSpace] = { instance: {} };
        window[nameSpace].init = function(options) {
            window[nameSpace].instance[options.privateId] = new Builder(options);
        };
    }
})(function() {
    function App(options) {
        var self = this;
        self.initParams(options);
        self.initDom();
        self.events();
        self.isLoading = false;
    }

    App.prototype = {
        initParams: function(options) {
            var self = this;
            self.adaptation = options.adaptation;
            self.adaptation.formMessage = options.formMessage;
            self.privateId = options.privateId;
            self.messageObj = options.getData;
            self.preUrl = options.url_prefix;
        },

        initDom: function() {
            var self = this;
            dynamicLoading.css(self.preUrl + 'css/formQueryBtn.css');
            self.appendChildDom();
        },

        events: function() {
            var self = this;
            self.adaptation.ObserverEvent.listen('Event' + self.privateId, function() {
                self.messageObj = self.adaptation.childrenGetData(self.privateId);
                self.appendChildDom();
            });
        },

        appendChildDom: function() {
            var self = this;
            // ... (保持原来的 DOM 结构不变)
            var domStructure = '<section class="customButton_box_content">' +
                '<div class="customButton_class_box my-fancy-btn ' + self.privateId + '" title="' + self.messageObj.display.escapeHTML() + '">' +
                self.messageObj.display.escapeHTML() +
                '</div>' +
                '</section>';

            document.querySelector('#' + self.privateId).innerHTML = domStructure;
            self.buttonElement = document.querySelector('.' + self.privateId);
            var clickHandler = self.jumpFun.bind(self);

            if (self.buttonElement) {
                self.buttonElement.removeEventListener('click', clickHandler);
                self.buttonElement.addEventListener('click', clickHandler);
            }
            if (self.messageObj.auth === 'hide') {
                document.querySelector('#' + self.privateId).innerHTML = '***';
            }
        },

        // =================================================================
        // 核心逻辑优化部分
        // =================================================================
        jumpFun: async function() {
            var self = this;

            // 1. 获取表名 (依然从 adaptation 拿)
            var tableName = "";
            if (self.adaptation && self.adaptation.formMessage) {
                tableName = self.adaptation.formMessage.tableName;
            }
            if (!tableName) { alert("未获取到表名 tableName"); return; }

            // 2. 使用 CSDK 获取核心元数据 (MasterId, AffairId)
            // 这比正则匹配 URL 稳定得多
            var metaData = {};
            if (window.csdk && window.csdk.core && window.csdk.core.getMetaData) {
                metaData = window.csdk.core.getMetaData() || {};
            } else {
                console.warn("未找到 csdk.core.getMetaData 接口，尝试降级获取");
            }

            // 获取数据ID (优先用 contentDataId，这就是真正的 MasterId)
            // 如果是无流程表单浏览，URL里的 moduleId 实际上就是 contentDataId
            var masterId = metaData.contentDataId;
            // 获取 AffairId (仅在流程表单中存在)
            var affairId = metaData.affairId || "";
            // FormId (表单定义ID): 对应 contentTemplateId  <--- 这里就是 formId
            var formId = metaData.contentTemplateId || "";

            // 打印调试日志，方便确认
            console.log(">>> [按钮点击] 数据获取结果：");
            console.log("    Table Name: " + tableName);
            console.log("    Master ID : " + masterId);
            console.log("    Form ID    : " + formId); // 打印出来看看
            console.log("    Affair ID : " + affairId + (affairId ? " (流程表单)" : " (无流程??/新建流程状态)"));

            if (!masterId) { alert("未找到表单数据ID (MasterId)"); return; }

            // 3. 发起请求
            try {
                var ctxPath = window.top._ctxPath || "/seeyon";
                var requestUrl = ctxPath + "/abc/simpleTest.do?tableName=" + tableName + "&masterId=" + masterId;

                if (affairId) {
                    requestUrl += "&affairId=" + affairId;
                }
                if (formId) {
                    requestUrl += "&formId=" + formId;
                }

                const response = await fetch(requestUrl, {
                    method: 'GET',
                    headers: { 'Accept': 'application/json' }
                });

                if (!response.ok) throw new Error("HTTP Error " + response.status);
                const result = await response.json();

                if (result.success) {
                    var showMsg = "【查询结果】\n";
                    showMsg += affairId ? "(来源：流程表单)\n" : "(来源：无流程表)\n";
                    showMsg += "-----------------\n";

                    var data = result.data;
                    console.log("致远三方接口查询结果：", data);
                    if (Array.isArray(data)) data = data[0];

                    for (var key in data) {
                        if (data.hasOwnProperty(key)) {
                            showMsg += key + ": " + data[key] + "\n";
                        }
                    }
                    alert(showMsg);
                } else {
                    alert("查询失败: " + result.message);
                }

            } catch (e) {
                console.error(e);
                alert("异常: " + e.message);
            }
        }
    };

    var dynamicLoading = {
        css: function(path) {
            if (!path) return;
            var head = document.getElementsByTagName('head')[0];
            var link = document.createElement('link');
            link.href = path; link.rel = 'stylesheet'; link.type = 'text/css';
            head.appendChild(link);
        },
        js: function(path) { }
    }
    return App;
});