/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
(function(){
function load_script(xyUrl, callback){
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = xyUrl;
    //alert(xyUrl);
    //借鉴了jQuery的script跨域方法
    script.onload = script.onreadystatechange = function(){
        if((!this.readyState || this.readyState === "loaded" || this.readyState === "complete")){
            callback && callback();
            // Handle memory leak in IE
            script.onload = script.onreadystatechange = null;
            if ( head && script.parentNode ) {
                head.removeChild( script );
            }
        }
    };
    // Use insertBefore instead of appendChild  to circumvent an IE6 bug.
    head.insertBefore( script, head.firstChild );
}
function myTranslate(point,type,callback,i){
    var callbackName = 'cbk_' + Math.round(Math.random() * 10000);    //随机函数名
    var xyUrl = "http://api.map.baidu.com/ag/coord/convert?from="+ type + "&to=4&x=" + point.lng + "&y=" + point.lat + "&callback=myBMap.Convertor." + callbackName;
    //动态创建script标签
    load_script(xyUrl);
    myBMap.Convertor[callbackName] = function(xyResult){
        
        delete myBMap.Convertor[callbackName];    //调用完需要删除改函数
        //alert(xyResult);
        var point = new BMap.Point(xyResult.x, xyResult.y);
        callback && callback(point,i);
    };
}
window.myBMap = window.myBMap || {};
myBMap.Convertor = {};
myBMap.Convertor.myTranslate = myTranslate;
})();
