/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author LFeng
 */
public class Units {

    /**
     * 验证码序列
     */
    private static final char[] numCodeSequence = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final double EARTH_RADIUS = 6378137;//赤道半径(单位m)
    public static final String BAIDU_CONVERT_KEY = "UGTSrlHZTd3O95SiMiQkhLO2";
    public static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullNumberAsZero,
        SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullListAsEmpty};

    private static String session;

    /**
     * 将指定类型坐标转换成为百度坐标
     *
     * @param lnglat 待转换的经纬度列表, 字符串格式为: longitude,latitude
     * @param type 待转换的坐标类型, 类型的取值如下: 1：GPS设备获取的角度坐标，wgs84坐标;
     * 2：GPS获取的米制坐标、sogou地图所用坐标;
     * 3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局坐标; 4：3中列表地图坐标对应的米制坐标;
     * 5：百度地图采用的经纬度坐标; 6：百度地图采用的米制坐标; 7：mapbar地图坐标; 8：51地图坐标
     * @return 返回坐标为: bd09ll(百度经纬度坐标)
     */
    public static String getBaiduLnglatConvert(String[] lnglat, int type) {
        String httpUrl = "http://api.map.baidu.com/geoconv/v1/";
        String httpArg = "";
        String coords = "coords=";
        if (lnglat.length < 100) {
            for (String lnglat1 : lnglat) {
                coords += (lnglat1 + ";");
            }
            coords = coords.substring(0, coords.length() - 1);
            httpArg += coords;
            httpArg += "&form=" + type + "&to=5&ak=" + BAIDU_CONVERT_KEY + "&output=json";
            return requestWithNoHeaderKey(httpUrl, httpArg);
        } else {
            return "经纬度点大于100个";
        }
    }

    /**
     * 百度地址解析
     *
     * @param latlng
     * @param coordType
     * 坐标的类型，目前支持的坐标类型包括：bd09ll（百度经纬度坐标）、bd09mc（百度米制坐标）、gcj02ll（国测局经纬度坐标）、wgs84ll（
     * GPS经纬度）
     * @return
     */
    public static String getBaiduRenderReverse(String latlng, String coordType) {
        String httpUrl = "http://api.map.baidu.com/geocoder/v2/";
        String httpArg = "location=" + latlng;
        httpArg += "&ak=" + BAIDU_CONVERT_KEY + "&output=json";
        httpArg += "&pois=1&extensions_town=true&latest_admin=1&radius=500";
        return requestWithNoHeaderKey(httpUrl, httpArg);
    }
    
    /**
     * 根据身份证号获取身份信息
     *
     * @param idCardNO
     * @return JSON返回示例 : { "errNum": 0, "retMsg": "success", "retData": {
     * "sex": "M", //M-男，F-女，N-未知 "birthday": "1987-04-20", "address":
     * "湖北省孝感市汉川市" } }
     */
    public static String getIDCardInfo(String idCardNO) {
        /*身份证信息验证URL*/
        String httpUrl = "http://apis.baidu.com/apistore/idservice/id";
        String httpArg = "id=" + idCardNO;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * 根据城市名称获取天气信息
     *
     * @param cityName
     * @return * JSON返回示例 :{ errNum: 0, errMsg: "success", retData: { city:
     * "北京", //城市 pinyin: "beijing", //城市拼音 citycode: "101010100", //城市编码 date:
     * "15-02-11", //日期 time: "11:00", //发布时间 postCode: "100000", //邮编
     * longitude: 116.391, //经度 latitude: 39.904, //维度 altitude: "33", //海拔
     * weather: "晴", //天气情况 temp: "10", //气温 l_tmp: "-4", //最低气温 h_tmp: "10",
     * //最高气温 WD: "无持续风向",	//风向 WS: "微风(<10m/h)", //风力 sunrise: "07:12", //日出时间
     * sunset: "17:44" //日落时间 } }
     */
    public static String getWeatherInfo(String cityName) {

        /*天气信息获取*/
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
        String httpArg = "cityname=" + cityName;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * 根据汉语拼音获取天气信息
     *
     * @param cityPinyin
     * @return JSON返回示例 :{ errNum: 0, errMsg: "success", retData: { city: "北京",
     * //城市 pinyin: "beijing", //城市拼音 citycode: "101010100", //城市编码 date:
     * "15-02-11", //日期 time: "11:00", //发布时间 postCode: "100000", //邮编
     * longitude: 116.391, //经度 latitude: 39.904, //维度 altitude: "33", //海拔
     * weather: "晴", //天气情况 temp: "10", //气温 l_tmp: "-4", //最低气温 h_tmp: "10",
     * //最高气温 WD: "无持续风向",	//风向 WS: "微风(<10m/h)", //风力 sunrise: "07:12", //日出时间
     * sunset: "17:44" //日落时间 } }
     */
    public static String getWeacherInfoWithPinyin(String cityPinyin) {
        /*天气信息获取*/
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/weather";
        String httpArg = "cityname=" + cityPinyin;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * 返回城市信息
     *
     * @param cityName
     * @return JSON返回示例 : { errNum: 0, retMsg: "success", retData: { cityName:
     * "北京", provinceName: "北京", cityCode: "101010100", //天气预报城市代码 zipCode:
     * "100000", //邮编 telAreaCode: "010" //电话区号 } }
     */
    public static String getCityInfo(String cityName) {
        /*城市信息获取URL*/
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityinfo";
        String httpArg = "cityname=" + cityName;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "d39fd8a034a21aa3b7d45ebf97c8ffd9");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 发送请求头不包含apikey的http请求
     *
     * @param httpUrl
     * @param httpArg
     * @return
     */
    public static String requestWithNoHeaderKey(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;
        //System.out.println("httpUrl:" + httpUrl);
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            /*
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
             */
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {

                }
            }
        }
        return result;
    }

    public static String requestWithUTF8(String httpUrl, String httpArg) throws Exception {
        BufferedReader reader = null;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;
        System.out.println("httpUrl:" + URLEncoder.encode(httpUrl, "UTF-8"));
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            /*
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
             */
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {

                }
            }
        }
        return result;
    }

    /**
     * 发送post请求
     *
     * @param httpUrl
     * @param sendBody
     * @return
     */
    public static String requestWithPost(String httpUrl, String sendBody) {
        BufferedReader reader;
        String result;
        StringBuilder sbf = new StringBuilder();
        try {
            System.out.println(httpUrl);
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String cookieValue = connection.getHeaderField("set-cookie");
            if (cookieValue != null) {
                session = cookieValue.substring(0, cookieValue.indexOf(";"));
            } else {
                session = "";
            }
            //第二次运行的时候，把上次读取的session的值设置上
            connection.setRequestProperty("Cookie", "JSESSIONID=" + session);

            if (null == sendBody) {
                connection.setRequestProperty("Content-Length", "0");
            }
            connection.connect();

            if (null != sendBody) {
//                System.out.println(sendBody);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                out.write(sendBody);
                out.flush();
                out.close();
            } else {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.write("".getBytes("UTF-8"), 0, 0);
                out.flush();
                out.close();
            }

            if (connection.getResponseCode() == 200) {
                //System.out.println("success!");
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }

                //第一次运行的时候，记录下来session的值
                String session_value = connection.getHeaderField("Set-Cookie");
                String[] sessionId = session_value.split(";");
                session = sessionId[0];
                System.out.println(sessionId[0]);
                System.out.println("Session Value = " + session_value);

                //reader.close();
                result = sbf.toString();
                return result;
            } else {
                System.out.println(connection.getResponseCode() + ",message:" + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将列表转换成为json格式数据
     *
     * @param result
     * @param recordCount
     * @return
     */
    public static String listToJson(ArrayList<?> result, int recordCount) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (null != result && result.size() > 0) {
            json.append("\"").append("status").append("\"").append(":");
            json.append(0).append(",");
            json.append("\"").append("message").append("\"").append(":");
            json.append("\"").append("\"").append(",");
            json.append("\"").append("count").append("\"").append(":");
            json.append("\"").append(recordCount).append("\"").append(",");
            json.append("\"").append("data").append("\"").append(":");
            json.append(JSONObject.toJSONString(result, features));
        } else {
            json.append("\"").append("status").append("\"").append(":");
            json.append(-1).append(",");
            json.append("\"").append("message").append("\"").append(":");
            json.append("\"").append("the record is null").append("\"").append(",");
            json.append("\"").append("count").append("\"").append(":");
            json.append("\"").append(0).append("\"").append(",");
            json.append("\"").append("data").append("\"").append(":");
            json.append("\"").append("\"");
        }
        json.append("}");
        return json.toString();
    }

    /**
     * 将对象转成json格式数据
     *
     * @param status
     * @param message
     * @param object
     * @return
     */
    public static String objectToJson(int status, String message, Object object) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"").append("status").append("\"").append(":");
        json.append(status).append(",");
        json.append("\"").append("message").append("\"").append(":");
        json.append("\"").append(message).append("\"").append(",");
        json.append("\"").append("data").append("\"").append(":");
        json.append(JSONObject.toJSONString(object, features));
        json.append("}");
        return json.toString();
    }

    /**
     * json字符串转换成json格式数据
     *
     * @param status
     * @param message
     * @param jsonStr
     * @return
     */
    public static String jsonStrToJson(int status, String message, String jsonStr) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"").append("status").append("\"").append(":");
        json.append(status).append(",");
        json.append("\"").append("message").append("\"").append(":");
        json.append("\"").append(message).append("\"").append(",");
        json.append("\"").append("data").append("\"").append(":");
        json.append(jsonStr);
        json.append("}");
        return json.toString();
    }

    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     *
     * @param lon1 第一点的经度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的经度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     */
    public static double GetDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 转化为弧度(rad)
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 判断给定的日期是不是今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        Date nowDate = new Date();
        return (date.getTime() >= dayBegin(nowDate).getTime())
                && (date.getTime() <= dayEnd(nowDate).getTime());
    }

    /**
     * 获取当前系统时间字符串
     *
     * @return 返回yyyy-MM-dd HH:mm:ss格式时间字符串
     */
    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        return format.format(nowDate);
    }

    /**
     * 获取当前系统时间没有分隔符的字符串
     *
     * @return 返回yyyyMMddHHmmss格式时间字符串
     */
    public static String getNowTimeNoSeparator() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date nowDate = new Date();
        return format.format(nowDate);
    }

    /**
     * 获取系统当前日期字符串
     *
     * @return
     */
    public static String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date();
        return format.format(nowDate);
    }

    /**
     * 返回指定时间与当前时间之间的时间间隔
     *
     * @param date
     * @return 若指定时间早于当前时间则返回正值; 若指定时间晚于当前时间则返回负值.
     */
    public static long getIntervalTimeWithNow(Date date) {
        Date nowDate = new Date();
        return nowDate.getTime() - date.getTime();
    }

    /**
     * 返回两个时间直接的间隔(单位毫秒)
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long getInterval(String firstDate, String secondDate) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date first = format.parse(firstDate);
        Date second = format.parse(secondDate);
        return first.getTime() - second.getTime();
    }

    /**
     * 根据状态码及消息内容返回json个数数据
     *
     * @param statusCode
     * @param message
     * @return json格式数据
     */
    public static String createJsonWithResult(String statusCode, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"").append("status").append("\"").append(":");
        builder.append("\"").append("success").append("\"").append(",");
        builder.append("\"").append("message").append("\"").append(":");
        builder.append("\"").append(message).append("\"").append(",");
        builder.append("\"").append("result").append("\"").append(":");
        builder.append("\"").append(statusCode).append("\"");
        builder.append("}");

        return builder.toString();
    }

    /**
     * 指定字符串是否在指定的字符串数组中
     *
     * @param str
     * @param array
     * @return
     */
    public static boolean isStrInArray(String str, String[] array) {
        for (String string : array) {
            if (string.compareTo(str) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个字符串是否为空, 长度为0或者是null都为空
     *
     * @param string
     * @return
     */
    public static boolean strIsEmpty(String string) {
        if (string == null) {
            return true;
        }
        if (string.trim().length() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 指定文件的内容作为字符串返回
     *
     * @param path
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static String returnFileContext(String path, String fileName) throws FileNotFoundException {
        File file = new File(path + fileName);
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file, "utf-8");
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        return builder.toString();
    }

    /**
     * 根据给定的路径和文件名生产文件
     *
     * @param filePath
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createNewFile(String filePath, String fileName) throws IOException {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 在srcStr字符串中查找desStr字符串, 然后在desStr后面插入insertStr字符串
     *
     * @param srcStr
     * @param desStr
     * @param insertStr
     * @return
     */
    public static String insertStr(String srcStr, String desStr, String insertStr) {
        StringBuffer buffer = new StringBuffer(srcStr);
        int index = 0;
        while ((index = buffer.indexOf(desStr, index)) != -1) {
            index += desStr.length() - 1;
            buffer.insert(index, insertStr);
        }
        return buffer.toString();
    }

    /**
     * 查询给定结果集中是否包含指定列名
     *
     * @param res
     * @param columnName
     * @return
     */
    public static boolean isExistColumn(ResultSet res, String columnName) {
        try {
            if (res.findColumn(columnName) > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * ************************************容联云通讯短信发送平台*****************************************
     */
    /**
     * 生成只有数字的指定位数验证码(包含数字)
     *
     * @param codeCount
     * @return
     */
    public static String createOnlyNumPhoneValidateCode(int codeCount) {
        // randomCode记录随机产生的验证码
        StringBuilder randomCode = new StringBuilder();
        Random random = new Random();
        // 随机产生codeCount个字符的验证码
        for (int i = 0; i < codeCount; i++) {

            String strRand = String.valueOf(numCodeSequence[random.nextInt(numCodeSequence.length)]);
            // 将产生的codeCount个随机数组合在一起
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }

    /**
     * 生成指定位数验证码(包含字母和数字)
     *
     * @param codeCount
     * @return
     */
    public static String createPhoneValidateCode(int codeCount) {
        // randomCode记录随机产生的验证码
        StringBuilder randomCode = new StringBuilder();
        Random random = new Random();
        // 随机产生codeCount个字符的验证码
        for (int i = 0; i < codeCount; i++) {

            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            // 将产生的codeCount个随机数组合在一起
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }

    /**
     * 向指定的手机号发送验证码
     *
     * @param to 要发送的手机号，多个手机号用英文状态逗号隔开，一次最多支持100个手机号
     * @param verificationCode 要发送的验证码
     * @return
     */
    public static String sendSMSVerificationCode(String to, String verificationCode) {
        //初始化SDK
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();

        //*初始化服务器地址和端口
        restAPI.init(Constants.SMS_PLATFORM_URL_DISTRIBUTION, Constants.SMS_PLATFORM_PORT);

        //*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN
        restAPI.setAccount(Constants.SMS_PLATFORM_ACCOUNT_SID, Constants.SMS_PLATFORM_AUTH_TOKEN);

        //*初始化应用ID
        restAPI.setAppId(Constants.SMS_PLATFORM_APP_ID);

        //*调用发送模板短信的接口发送短信
        HashMap<String, Object> result = restAPI.sendTemplateSMS(to, Constants.SMS_PLATFORM_TEMPLATE_ID, new String[]{verificationCode, Constants.SMS_EXPIRED_MINUTE});

        String json;
        if ("000000".equals(result.get("statusCode"))) {
            json = createJsonWithResult(String.valueOf(result.get("statusCode")), "验证码已发送到您的手机，请于5分钟内输入手机验证码，以免失效！");
        } else {
            json = createJsonWithResult(String.valueOf(result.get("statusCode")), String.valueOf(result.get("statusMsg")));
        }
        return json;
    }

    /**
     * 发送设备绑定验证码
     *
     * @param phone
     * @param userName
     * @param userPhone
     * @param deviceId
     * @param verifyCode
     * @return
     */
    public static String sendBandDeviceSMSCode(String phone, String userName, String userPhone, String deviceId, String verifyCode) {
        //初始化SDK
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();

        //*初始化服务器地址和端口
        restAPI.init(Constants.SMS_PLATFORM_URL_DISTRIBUTION, Constants.SMS_PLATFORM_PORT);

        //*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN
        restAPI.setAccount(Constants.SMS_PLATFORM_ACCOUNT_SID, Constants.SMS_PLATFORM_AUTH_TOKEN);

        //*初始化应用ID
        restAPI.setAppId(Constants.SMS_PLATFORM_APP_ID);

        //*调用发送模板短信的接口发送短信
        HashMap<String, Object> result = restAPI.sendTemplateSMS(phone, Constants.SMS_PLATFORM_BAND_DEVICE_TEMPLATE_ID, new String[]{userName, userPhone, deviceId, verifyCode});

        String json;
        if ("000000".equals(result.get("statusCode"))) {
            //json = createJsonWithResult(String.valueOf(result.get("statusCode")), "验证码已发送到您的手机，请于30分钟内输入手机验证码，以免失效！");
            json = objectToJson(0, "验证码已发送到您的手机，请于30分钟内输入手机验证码，以免失效！", null);
        } else {
            //json = createJsonWithResult(String.valueOf(result.get("statusCode")), String.valueOf(result.get("statusMsg")));
            json = objectToJson(-1, String.valueOf(result.get("statusMsg")), null);
        }
        return json;
    }
}
