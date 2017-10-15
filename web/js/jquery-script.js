/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function ajaxRequest(url, requestData, callback) {
    $.ajax({
        type: "post",
        url: url,
        data: JSON.stringify(requestData),
        dataType: "json",
        success: function (data) {
            if (data.status === -99) {
                alert('未登录或登录已超时!请你重新登录!');
                location.href = "login.html";
            } else {
                callback(data);
            }
        },
        error: function() {
            //alert('请求出错!');
        }
    });
}
