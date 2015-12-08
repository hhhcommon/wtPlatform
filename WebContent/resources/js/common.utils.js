/**
 * 通用Javascript函数，注意这里不要出现任何非基本javascript的方法，如jquery的方法。
 */

/**
 * 用于调试，得到对象中的元素及其值
 * @param obj 检测对象
 * @returns {String} 对象中元素的信息
 */
function allFields(obj) {
  var i=0;
  var props = "";
  if (obj==null) props="[allPrposCount=0]\nnull";
  else {
    for(var p in obj) {
      i=i+1;
      if (typeof(p)!="function") {
        if ((obj[p]+"").indexOf("[")!==0) {
          props += i+":"+p+"="+obj[p]+";\n";
        }
      }
    }
    props = "[allPrposCount="+i+"]\n"+props;
  }
  return props;
}

/**
 * 判断对象是否为空，为{}或null返回true
 */
function isEmpty(obj) {
  if (!obj) return true;
  for (var name in obj) return false;
  return true;
};

/**
 * 判断对象是否未定义，或者为空，或者长度为0
 */
function isUndefinedNullEmpty(obj) {
	if (obj) {
	  if (typeof(obj)=="string") return (obj.length==0);
	  if (typeof(obj)=="object") for (var name in obj) return false;
	  return false;
	} else return true;
};

/**
 * 获得url中参数名为paramName的参数值
 * 如果url没有名称为paramName的参数返回null
 * 如果url中参数paramName的值为空，返回值也是空。(如:userName=&password=a或userName&password=a)取userName值为""
 * @returns {String} 在url中指定的paramName参数的值
 */
function getUrlParam(url, paramName) {
  if (!paramName&&!url) return null;
  var _url = url+"";
  if (_url.indexOf("?")==-1) return null;
  _url = "&"+_url.substring(_url.indexOf("?")+1);
  var pos=_url.lastIndexOf("&"+paramName+"=");
  if (pos==-1) return null;
  _url=_url.substring(pos+paramName.length+2);
  return _url.indexOf("&")==-1?_url:_url.substring(0, _url.indexOf("&"));
}

/**
 * 得到当前浏览器版本
 * @returns {String}
 */
function getBrowserVersion() {
  var browser = {};
  var userAgent = navigator.userAgent.toLowerCase();

  var s;
  (s = userAgent.match(/msie ([\d.]+)/)) ? browser.ie = s[1] :
  (s = userAgent.match(/net\sclr\s([\d.]+)/)) ? browser.ie = userAgent.match(/rv:([\d.]+)/)[1] :
  (s = userAgent.match(/firefox\/([\d.]+)/)) ? browser.firefox = s[1] :
  (s = userAgent.match(/chrome\/([\d.]+)/)) ? browser.chrome = s[1] :
  (s = userAgent.match(/opera.([\d.]+)/)) ? browser.opera = s[1] :
  (s = userAgent.match(/version\/([\d.]+).*safari/)) ? browser.safari = s[1] : 0;

  var version = browser.ie? 'msie '+browser.ie:
    browser.firefox?'firefox ' + browser.firefox:
    browser.chrome?'chrome ' + browser.chrome:
    browser.opera? 'opera ' + browser.opera:
    browser.safari?'safari ' + browser.safari:
    '未知';

  return version;
}

/**
 * 生成UUID，默认为36位
 */
var CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");  
function getUUID(len,radix) {
  var chars = CHARS, uuid = [];
  radix = radix||chars.length;
  if (len) {
    for (var i=0; i<len; i++) uuid[i] = chars[0 | Math.random()*radix];  
  } else {
    var r;
    uuid[8] = uuid[13] = uuid[18] = uuid[23] = "-";
    uuid[14] = "4";
    for (var i=0; i<36; i++) {
      if (!uuid[i]) {
        r = 0 | Math.random()*16;
        uuid[i] = chars[(i == 19)?(r&0x3)|0x8:r];
      }
    }
  }
  return uuid.join("");
}

/**
 * 把json串转换为对象
 * @param jsonStr json串
 * @returns javascript对象
 */
function str2JsonObj(jsonStr) {
  return eval("(" +jsonStr+ ")");
}

/**
 * 将form表单元素的值转化为对象。
 * 在ajax提交form时，可提交序列化后的对象。
 * 注意，若其中有file字段，将不能正确处理。
 * param form 需要序列化的form元素的id
 */
function formField2Object(formId) {
  var o = {};
  var _form = $("#"+formId).form();

  if (_form) {
    $.each(_form.serializeArray(),function(){
      if(o[this['name']]){
        o[this['name']]=o[this['name']]+","+this['value'];
      }else{
        o[this['name']]=this['value'];
      }
    });
  }
  return o;
}

function jqueryColor2HexColor(jqueryColor) {
  var rgb = jqueryColor.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
  if (!rgb) return "#FFFFFF";
  if (rgb.length!=4) return "#FFFFFF";
  rgb= "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
  return rgb;
  function hex(x) {
    return ("0" + parseInt(x).toString(16)).slice(-2);
  };
}

/**
 * 对Date的扩展，将 Date 转化为指定格式的String 
 * 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
 * 例子： 
 * (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
 * (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
 * @param date.Format
 * @returns 时间的字符串类型
 * @author: meizz
 */
Date.prototype.Format = function(fmt) {
  var o = {
   "M+" : this.getMonth()+1,                 //月份
   "d+" : this.getDate(),                    //日
   "h+" : this.getHours(),                   //小时
   "m+" : this.getMinutes(),                 //分
   "s+" : this.getSeconds(),                 //秒
   "q+" : Math.floor((this.getMonth()+3)/3), //季度
   "S"  : this.getMilliseconds()             //毫秒
  };
  if (/(y+)/.test(fmt)) fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
  for(var k in o) {
    if(new RegExp("("+ k +")").test(fmt)) {
      if (k=="S") {
      	fmt = fmt.replace("S", ("00"+o["S"]).substr(("00"+o["S"]).length-3));
      } else {
        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
      }
    }
  }
  return fmt;
}

//扩展方法
/**
 * 扩展String属性：得到中英混排文字符串长度
 */
String.prototype.cnLength = function () {
  return ((this.replace(/[^x00-xFF]/g, "**")).length);
};

/**
 * 删除数组中的元素
 */
Array.prototype.removeByIndex = function (i){
  if (i>=0 && i<this.length) this.splice(i,1);
};

/**
 * 在指定位置i插入对象item
 * @param i 插入的数组中的位置
 * @param item 元素对象
 */
Array.prototype.insertAt = function (index, item) {
  this.splice(index, 0, item);
};