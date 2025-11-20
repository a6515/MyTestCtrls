/**
 * 前端自定义控件 - 按钮 (修正版)
 * 功能：获取表单数据并与后端通信 (支持缓存+数据库双模查询)
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
            var domStructure = '<section class="customButton_box_content">' +
                // 注意这里加了 "my-fancy-btn" 类名
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
            // 权限控制
            if (self.messageObj.auth === 'hide') {
                document.querySelector('#' + self.privateId).innerHTML = '***';
            }
        },

        jumpFun: async function() {
            var self = this;

            // ==========================================================
            // 1. 获取 tableName (不变)
            // ==========================================================
            var tableName = "";
            if (self.adaptation && self.adaptation.formMessage) {
                tableName = self.adaptation.formMessage.tableName;
            }

            // ==========================================================
            // 2. 获取 masterId (不变)
            // ==========================================================
            var masterId = "";
            var matchModule = window.location.href.match(/moduleId=([^&]+)/);
            if (matchModule) {
                masterId = matchModule[1];
            } else if (self.adaptation && self.adaptation.formMessage) {
                masterId = self.adaptation.formMessage.contentDataId;
            }

            // ==========================================================
            // 3. 【核心修复】获取 affairId (要去 window.top 找)
            // ==========================================================
            var affairId = "";

            // 定义一个包含所有可能地址的大字符串
            var allUrls = window.location.href;

            try {
                // 尝试把浏览器最顶层(地址栏)的 URL 也拼进来找
                // 加上 try-catch 是为了防止极少数情况下的跨域报错
                if (window.top && window.top.location) {
                    allUrls += "|||" + window.top.location.href;
                    console.log("顶层窗口URL:", window.top.location.href);
                    console.log("拼接起来的url为:", allUrls)
                }
            } catch (e) {
                console.log("无法获取顶层窗口URL，忽略跨域限制");
            }

            console.log("正在以下范围查找 affairId:", allUrls);

            // 在所有地址里正则匹配 affairId
            // 这里的正则匹配逻辑不变，只是匹配源变成了 allUrls
            var matchAffair = allUrls.match(/affairId=([^&]+)/);
            if (matchAffair) {
                affairId = matchAffair[1];
            }

            // ==========================================================
            // 4. 发起请求 (不变)
            // ==========================================================
            if (!tableName) { alert("未找到表名 tableName"); return; }
            if (!masterId) { alert("未找到 masterId"); return; }

            try {
                var ctxPath = window.top._ctxPath || "/seeyon";
                var requestUrl = ctxPath + "/abc/simpleTest.do?tableName=" + tableName + "&masterId=" + masterId;

                if (affairId) {
                    requestUrl += "&affairId=" + affairId;
                    console.log(">>> 捕获到流程表单，AffairId:", affairId);
                } else {
                    console.log(">>> 未捕获到 AffairId，按无流程表单处理");
                }

                const response = await fetch(requestUrl, {
                    method: 'GET',
                    headers: { 'Accept': 'application/json' }
                });

                if (!response.ok) throw new Error("HTTP Error " + response.status);
                const result = await response.json();

                if (result.success) {
                    var showMsg = "【查询结果】\n";
                    if(affairId) showMsg += "(来源：流程表单)\n";
                    else showMsg += "(来源：无流程表)\n";
                    showMsg += "-----------------\n";

                    // 简单处理数据展示
                    var data = result.data;
                    if (Array.isArray(data)) data = data[0]; // 如果是数组取第一个

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