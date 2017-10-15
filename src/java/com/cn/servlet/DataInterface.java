/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.cn.util.Units;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Customer;
import com.cn.bean.CustomerDevice;
import com.cn.bean.NewestPosition;
import com.cn.bean.SMSResult;
import com.cn.bean.Vehicles;
import com.cn.controller.CommonController;
import com.cn.controller.GPSController;
import com.cn.controller.SMSController;
import com.cn.handler.SMSResultHandler;
import com.cn.util.DatabaseOpt;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class DataInterface extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DataInterface.class);

    private Properties prop;

    @Override
    public void init() throws ServletException {
        try {
            prop = new Properties();
            prop.load(DataInterface.class.getClassLoader().getResourceAsStream("./config.properties"));
        } catch (IOException ex) {
            logger.error("加载配置文件出错", ex);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param params
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String params)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String subUri = uri.substring(uri.lastIndexOf("/") + 1,
                uri.lastIndexOf("."));
        String json = null;
        logger.info(subUri + ",params:" + params);
        try {
            HttpSession session = request.getSession();
            //System.out.println(session.getAttribute("user"));
            if (!"UserLogin".equals(subUri) && !"UserReg".equals(subUri) && !"SendSMSMessage".equals(subUri)
                    && session.getAttribute("user") == null) {
                session.invalidate();
                json = Units.objectToJson(-99, "未登陆", null);
                PrintWriter out = response.getWriter();
                try {
                    response.setContentType("text/html;charset=UTF-8");
                    response.setHeader("Cache-Control", "no-store");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                    out.print(json);
                } finally {
                    out.close();
                }
                return;
            }
            Customer curCustomer = (Customer) session.getAttribute("user");
            JSONObject paramsJson = JSONObject.parseObject(params.replace(" ", "%20"));
            //String serverUrl = prop.getProperty("GPSServerUrl") + "gpsapi.ashx";
            switch (subUri) {
                //<editor-fold desc="UserLogin">
                case "UserLogin": {
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    String whereCase = "LoginUserName = '" + paramsJson.getString("account") + "'";
                    List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", whereCase, 11, 1, "LoginUserName", 0, opt.getConnect());
                    if (list != null && list.size() > 0) {
                        Customer customer = (Customer) list.get(0);
                        if (customer.getLoginPassword().compareTo(paramsJson.getString("password")) == 0) {
                            session.setAttribute("user", customer);
                            list = controller.dataBaseQuery("table", "com.cn.bean.", "CustomerDevice", "*", whereCase, 11, 1, "LoginUserName", 0, opt.getConnect());
                            json = Units.objectToJson(0, "登录成功!", list);
                        } else {
                            json = Units.objectToJson(-1, "用户名或密码错误!", null);
                        }
                    } else {
                        json = Units.objectToJson(-1, "用户名不存在!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="UserReg">
                case "UserReg": {
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    //System.out.println("验证码:" + session.getAttribute("regCode").toString());
                    if (session.getAttribute("regCode") != null && paramsJson.getString("code").compareTo(session.getAttribute("regCode").toString()) == 0) {
                        if (session.getAttribute("mobile") != null && paramsJson.getString("loginUserName").compareTo(session.getAttribute("mobile").toString()) == 0) {
                            int result = controller.dataBaseOperate("[" + params + "]", "com.cn.bean.", "Customer", "add", opt.getConnect()).get(0);
                            if (result == 0) {
                                json = Units.objectToJson(0, "用户注册成功!", null);
                            } else if (result == 2627) {
                                json = Units.objectToJson(-1, "改手机号已经注册!", null);
                            } else {
                                json = Units.objectToJson(-1, "用户注册出错!", null);
                            }
                            session.invalidate();
                        } else {
                            json = Units.objectToJson(-1, "请使用获取验证码的手机注册!", null);
                        }
                    } else {
                        json = Units.objectToJson(-1, "验证码输入错误或已过期!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="SendSMSMessage">
                case "SendSMSMessage": {
                    String mobile = paramsJson.getString("phone");
                    String regCode = Units.createOnlyNumPhoneValidateCode(4);
                    String content = String.format("验证码%s【和车行】", regCode);

                    SMSController controller = new SMSController();
                    String result = controller.sendSMSMessage(mobile, content);
                    System.out.println(result);
                    ByteArrayInputStream stream = new ByteArrayInputStream(result.getBytes("UTF-8"));
                    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                    SMSResultHandler handler = new SMSResultHandler();
                    parser.parse(stream, handler);
                    SMSResult mSResult = handler.getResult();
                    if (mSResult.getStatus().compareTo("Success") == 0) {
                        session.setAttribute("mobile", mobile);
                        session.setAttribute("regCode", regCode);
                        json = Units.objectToJson(0, "验证码发送成功, 5分钟之内有效!", null);
                    } else {
                        json = Units.objectToJson(-1, mSResult.getMessage(), null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="VerifyDeviceID">
                case "VerifyDeviceID": {
                    GPSController gpsController = new GPSController();
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    String deviceID = "0" + paramsJson.getString("deviceID");
                    String gpsUrl = String.format("jdbc:sqlserver://%s:%s;DatabaseName=CxGpsBaseInfo", curCustomer.getCustomerGPSDatabaseIP(), curCustomer.getCustomerGPSDatabasePort());
                    List<Vehicles> vehiclesList = gpsController.getGpsServerDeviceList(gpsUrl, deviceID);
                    if (vehiclesList != null && vehiclesList.size() > 0) {
                        String whereCase = "DeviceID = '" + deviceID + "'";
                        List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "CustomerDevice", "*", whereCase, Integer.MAX_VALUE, 1, "LoginUserName", 0, opt.getConnect());
                        if (list != null && list.size() > 0) {
                            boolean isSelfBind = false;
                            CustomerDevice ownerCustomer = null;
                            Iterator iterator = list.iterator();
                            while (iterator.hasNext()) {
                                CustomerDevice device = (CustomerDevice) iterator.next();
                                //绑定时间最早的用户为车主
                                if (ownerCustomer == null) {
                                    ownerCustomer = device;
                                } else {
                                    if (Units.getInterval(ownerCustomer.getBandTime(), device.getBandTime()) > 0) {
                                        ownerCustomer = device;
                                    }
                                }
                                //如果该设备绑定列表中有当前登录用户名, 则用户已绑定该设备
                                if (device.getLoginUserName().compareTo(curCustomer.getLoginUserName()) == 0) {
                                    isSelfBind = true;
                                    break;
                                }
                            }
                            if (isSelfBind) {
                                json = Units.objectToJson(1, "您已绑定该设备!", null);
                            } else {
                                json = Units.objectToJson(2, "已有其他用户绑定该设备!", ownerCustomer);
                            }
                        } else {
                            json = Units.objectToJson(0, "您可以绑定该设备!", null);
                        }
                    } else {
                        json = Units.objectToJson(-1, "设备ID不存在!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="SendVerifySMSMessage">
                case "SendVerifySMSMessage": {
                    /*
                    CustomerDevice ownerCustomer = JSONObject.parseObject(paramsJson.getString("owner"), CustomerDevice.class);
                    String verifyCode = Units.createOnlyNumPhoneValidateCode(4);
                    String content = String.format(
                            "用户%s(手机号为: %s)请求绑定您的设备(设备编号为: %s), 验证码%s【和车行】"
                            , curCustomer.getCustomerName()
                            , curCustomer.getLoginUserName()
                            , ownerCustomer.getDeviceID()
                            , verifyCode);

                    SMSController controller = new SMSController();
                    String result = controller.sendSMSMessageWithUTF(ownerCustomer.getLoginUserName(), content);
                    
                    if (result == null) {
                        json = Units.objectToJson(-1, "验证码发送出错!", null);
                        break;
                    }
                    ByteArrayInputStream stream = new ByteArrayInputStream(result.getBytes("UTF-8"));
                    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                    SMSResultHandler handler = new SMSResultHandler();
                    parser.parse(stream, handler);
                    SMSResult mSResult = handler.getResult();
                    if (mSResult.getStatus().compareTo("Success") == 0) {
                        session.setAttribute("mobile", ownerCustomer.getLoginUserName());
                        session.setAttribute("verifyCode", verifyCode);
                        json = Units.objectToJson(0, "验证码发送成功, 5分钟之内有效!", null);
                    } else {
                        json = Units.objectToJson(-1, mSResult.getMessage(), null);
                    }
                    */
                    CustomerDevice ownerCustomer = JSONObject.parseObject(paramsJson.getString("owner"), CustomerDevice.class);
                    String verifyCode = Units.createOnlyNumPhoneValidateCode(4);
                    json = Units.sendBandDeviceSMSCode(
                            ownerCustomer.getLoginUserName(),
                            curCustomer.getCustomerName(),
                            curCustomer.getLoginUserName(),
                            ownerCustomer.getDeviceID(),
                            verifyCode);
                    if (JSONObject.parseObject(json).getIntValue("status") == 0) {
                        session.setAttribute("verifyCode", verifyCode);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="VerifyCode">
                case "VerifyCode": {
                    if (session.getAttribute("verifyCode") != null && paramsJson.getString("code").compareTo(session.getAttribute("verifyCode").toString()) == 0) {
                        json = Units.objectToJson(0, "验证成功!", null);
                    } else {
                        json = Units.objectToJson(-1, "验证码输入错误或已过期!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="AddCustomerDevice">
                case "AddCustomerDevice": {
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    paramsJson.put("loginUserName", curCustomer.getLoginUserName());
                    paramsJson.put("bandTime", Units.getNowTime());
                    int result = controller.dataBaseOperate("[" + paramsJson.toJSONString() + "]", "com.cn.bean.", "CustomerDevice", "add", opt.getConnect()).get(0);
                    if (result == 0) {
                        json = Units.objectToJson(0, "绑定成功!", curCustomer.getCustomerName());
                    } else if (result == 2627) {
                        json = Units.objectToJson(-1, "设备已绑定!", null);
                    } else {
                        json = Units.objectToJson(-1, "设备绑定出错!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="GetCustomerDevice">
                case "GetCustomerDevice": {
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    String whereCase = "LoginUserName = '" + curCustomer.getLoginUserName() + "'";
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "CustomerDevice", "*", whereCase, pageSize, pageIndex, "LoginUserName", 0, opt.getConnect());
                    if (list != null && list.size() > 0) {
                        json = Units.objectToJson(0, "", list);
                    } else {
                        json = Units.objectToJson(-1, "没有更多数据!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="LoadUserVehicles">
                case "LoadUserVehicles": {
                    String sendBody = "method=LoadUserVehicles&username=" + paramsJson.getString("username") + "&pwd=" + paramsJson.getString("password");
                    json = Units.requestWithNoHeaderKey(curCustomer.getCustomerGPSServerUrl() + "gpsapi.ashx", sendBody);
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="GetDevicePosition">
                case "GetDevicePosition": {
                    GPSController gpsController = new GPSController();
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    String deviceID = paramsJson.getString("systemNo");
                    String gpsUrl = String.format("jdbc:sqlserver://%s:%s;DatabaseName=CxGpsBaseInfo", curCustomer.getCustomerGPSDatabaseIP(), curCustomer.getCustomerGPSDatabasePort());
                    List<NewestPosition> newestPositions = gpsController.getNewestLocation(gpsUrl, deviceID);
                    if (newestPositions != null && newestPositions.size() > 0) {
                        
                        String whereCase = "LoginUserName = '" + curCustomer.getLoginUserName() + "' and DeviceID = '" + paramsJson.getString("systemNo") + "'";
                        List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "CustomerDevice", "*", whereCase, 11, 1, "LoginUserName", 0, opt.getConnect());
                        if (list != null && list.size() > 0) {
                            CustomerDevice device = (CustomerDevice) list.get(0);
                            
                            JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(newestPositions.get(0)));
                            object.put("carNo", device.getCarNo());
                            json = Units.objectToJson(0, "", object);
                        } else {
                            json = Units.objectToJson(-1, "数据出错!", null);
                        }
                    } else {
                        json = Units.objectToJson(-1, "没有定位信息!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="LoadVehiclePostion">
                case "LoadVehiclePostion": {
                    String sendBody = "method=LoadVehiclePostion&systemno=" + paramsJson.getString("systemNo");
                    json = Units.requestWithNoHeaderKey(curCustomer.getCustomerGPSServerUrl() + "gpsapi.ashx", sendBody);
                    CommonController controller = new CommonController();
                    DatabaseOpt opt = new DatabaseOpt();
                    String whereCase = "LoginUserName = '" + paramsJson.getString("account") + "' and DeviceID = '" + paramsJson.getString("systemNo") + "'";
                    List<Object> list = controller.dataBaseQuery("table", "com.cn.bean.", "CustomerDevice", "*", whereCase, 11, 1, "LoginUserName", 0, opt.getConnect());
                    if (list != null && list.size() > 0) {
                        CustomerDevice device = (CustomerDevice) list.get(0);
                        JSONObject object = JSONObject.parseObject(json);
                        object.put("carNo", device.getCarNo());
                        json = Units.objectToJson(0, "", object);
                    } else {
                        json = Units.objectToJson(-1, "设备绑定出错!", null);
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="LoadHistoryData">
                case "LoadHistoryData": {
                    String sendBody = "method=LoadHistoryData&Sys=" + paramsJson.getString("systemNo") + "&btime=" + paramsJson.getString("start") + "&etime=" + paramsJson.getString("end");
                    //String sendBody = "method=LoadHistoryData&Sys=" + paramsJson.getString("systemNo") + "&btime=2017-01-0120%12:12&etime=2017-01-0220%11:11";
                    json = Units.requestWithNoHeaderKey(curCustomer.getCustomerGPSServerUrl() + "gpsapi.ashx", sendBody);
                    json = Units.objectToJson(0, "", json);
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="convertLatLng">
                case "convertLatLng": {
                    JSONArray lnglatJson = JSONArray.parseArray(paramsJson.getString("points"));
                    String[] lnglat = new String[lnglatJson.size()];
                    for (int i = 0; i < lnglatJson.size(); i++) {
                        JSONObject object = lnglatJson.getJSONObject(i);
                        lnglat[i] = object.getString("lng") + "," + object.getString("lat");
                    }
                    json = Units.objectToJson(0, "", Units.getBaiduLnglatConvert(lnglat, 1));
                    break;
                }
                //</editor-fold>
            }
        } catch (Exception e) {
            logger.info(subUri);
            logger.error("错误信息:" + e.getMessage(), e);
            json = Units.objectToJson(-1, "输入参数错误!", e.toString());
        }

        PrintWriter out = response.getWriter();

        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            out.print(json);
//            System.out.println(json);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String params = request.getParameter("params");
        processRequest(request, response, params);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String params = getRequestPostStr(request);
        processRequest(request, response, params);
    }

    /**
     * 描述:获取 post 请求内容
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
