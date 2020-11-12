jQuery.validator.addMethod("nameCheck", function (value, element) {
    return this.optional(element) || /^[a-z][a-z0-9_]{2,19}$/i.test(value);
}, "请输入合法名称，英文字母开头+英文、数字或下划线的组合（3-20位）");

jQuery.validator.addMethod("ipCheck", function (value, element) {
    return this.optional(element) || (/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(value) && (RegExp.$1 < 256 && RegExp.$1 > 0 && RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256));
}, "请输入合法IP地址");

jQuery.validator.addMethod("portCheck", function (value, element) {
    return this.optional(element) || /^(\d+)$/.test(value) && (RegExp.$1 < 65536) && RegExp.$1 > 0;
}, "请输入合法端口");

jQuery.validator.addMethod("passwordCheck", function (value, element) {
    return this.optional(element) || /[^\u4e00-\u9fa5]{5,20}$/.test(value);
}, "请输入非中文字符或输入长度在5-20位之间");

jQuery.validator.addMethod("paramCheck", function (value, element) {
    return this.optional(element) || /[^\u4e00-\u9fa5]{6,20}$/.test(value);
}, "请输入非中文字符或输入长度在6-20位之间");

jQuery.validator.addMethod("paramNoLimitCheck", function (value, element) {
    return this.optional(element) || /[^\u4e00-\u9fa5]$/.test(value);
}, "请输入非中文字符");

jQuery.validator.addMethod("numCheck", function (value, element) {
    return this.optional(element) || /^\d+$/.test(value);
}, "请输入合法数字");

jQuery.validator.addMethod("tranCheck", function (value, element) {
    return this.optional(element) || /^[a-z]{3}00[a-z0-9]{5}$/.test(value);
}, "请输入合法交易码(由3位系统名+00+5位数字、小写字母组成)");

jQuery.validator.addMethod("timeTaskingCheck", function (value, element) {
    return this.optional(element) || /^[0-9]+$/.test(value);
}, "请输入合法编号(由0-9之间的数字组成)");

jQuery.validator.addMethod("priorityCheck", function (value, element) {
    return this.optional(element) || /^[1-5]$/.test(value);
}, "数字输入不合法 ,请输入【1-5】之间的数字！");

jQuery.validator.addMethod("compCheck", function (value, element) {
    return this.optional(element) || /^[a-z][/.a-z0-9]*$/i.test(value);
}, "请输入合法类名");

jQuery.validator.addMethod("percCheck", function (value, element) {
    return this.optional(element) || /^[1-9][0-9]{1}%$|100%|^[0-9]%$/.test(value);
}, "请输入合法百分比");

jQuery.validator.addMethod("direCheck", function (value, element) {
    return this.optional(element) || /^[\u4e00-\u9fa5\/0-9a-z_-]*$/i.test(value);
}, "请输入合法目录名");

jQuery.validator.addMethod("demoCheck", function (value, element) {
    return this.optional(element) || /^.{0,99}$/i.test(value);
}, "输入的字符太长，最大为100");

jQuery.validator.addMethod("dire2Check", function (value, element) {
    return this.optional(element) || /^\/[\u4e00-\u9fa5\/0-9a-z_-]*$/i.test(value);
}, "请输入合法目录名，以“/”开头");

jQuery.validator.addMethod("nodeNameCheck", function (value, element) {
    return this.optional(element) || /^[a-z][a-z0-9]{0,16}[0-9]{3}$/i.test(value);
}, "输入不合法，英文字母开头+后三位数字（4-20位）");

jQuery.validator.addMethod("clsnameCheck", function (value, element) {
    return this.optional(element) || /^[a-z][a-z0-9_]{2,29}$/i.test(value);
}, "请输入合法名称，英文字母开头+英文、数字或下划线的组合（3-30位）");

jQuery.validator.addMethod("clientAddressCheck", function (value, element) {
    return this.optional(element) || (/^(\d+)\.(\d+)\.(\d+)\.(\d+):(\d+)$/.test(value) && (RegExp.$1 < 256 && RegExp.$1 > 0 && RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256) && (RegExp.$5 < 65536) && (RegExp.$5 > 0));
}, "请输入合法IP地址:端口");

