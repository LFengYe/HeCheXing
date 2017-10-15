/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.NewestPosition;
import com.cn.bean.Vehicles;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class GPSController {

    private static final Logger logger = Logger.getLogger(CommonController.class);

    /**
     * 获取GPS服务器该设备编号的设备信息
     *
     * @param gpsDatabaseUrl GPS数据库连接url
     * @param deviceID
     * @return
     * @throws Exception
     */
    public ArrayList<Vehicles> getGpsServerDeviceList(String gpsDatabaseUrl, String deviceID) throws Exception {
        DatabaseOpt opt = new DatabaseOpt();
        String sql = "select * from Vehicles where SystemNo = ?";
        CallableStatement statement = null;
        Connection conn = null;
        try {
            conn = opt.getConnectGps(gpsDatabaseUrl);
            statement = conn.prepareCall(sql);
            statement.setString(1, deviceID);
            ResultSet set = statement.executeQuery();

            Class objClass = Class.forName("com.cn.bean.Vehicles");
            Method[] methods = objClass.getMethods();
            ArrayList<Vehicles> result = new ArrayList<>();
            while (set.next()) {
                Vehicles object = new Vehicles();
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.startsWith("set") && !Modifier.isStatic(method.getModifiers())) {
                        // 根据方法名字得到数据表格中字段的名字
                        String columnName = methodName.substring(3, methodName.length());
                        if (Units.isExistColumn(set, columnName)) {
                            // 得到方法的参数类型
                            Class[] parmts = method.getParameterTypes();
                            if (parmts[0] == int.class) {
                                method.invoke(object, set.getInt(columnName));
                            } else if (parmts[0] == boolean.class) {
                                method.invoke(object, set.getBoolean(columnName));
                            } else if (parmts[0] == float.class) {
                                method.invoke(object, set.getFloat(columnName));
                            } else if (parmts[0] == double.class) {
                                method.invoke(object, set.getDouble(columnName));
                            } else {
                                method.invoke(object, set.getString(columnName));
                            }
                        }
                    }
                }
                result.add(object);
            }
            return result;
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("类名错误", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("数据库关闭连接错误", ex);
            }
        }
        return null;
    }

    /**
     * 获取指定设备的最新位置信息
     * @param gpsDatabaseUrl
     * @param deviceID
     * @return
     * @throws Exception 
     */
    public ArrayList<NewestPosition> getNewestLocation(String gpsDatabaseUrl, String deviceID) throws Exception {
        DatabaseOpt opt = new DatabaseOpt();
        String sql = "select * from NewestPosition where SystemNo = ?";
        CallableStatement statement = null;
        Connection conn = null;
        try {
            conn = opt.getConnectGps(gpsDatabaseUrl);
            statement = conn.prepareCall(sql);
            statement.setString(1, deviceID);
            ResultSet set = statement.executeQuery();

            Class objClass = Class.forName("com.cn.bean.NewestPosition");
            Method[] methods = objClass.getMethods();
            ArrayList<NewestPosition> result = new ArrayList<>();
            while (set.next()) {
                NewestPosition object = new NewestPosition();
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.startsWith("set") && !Modifier.isStatic(method.getModifiers())) {
                        // 根据方法名字得到数据表格中字段的名字
                        String columnName = methodName.substring(3, methodName.length());
                        if (Units.isExistColumn(set, columnName)) {
                            // 得到方法的参数类型
                            Class[] parmts = method.getParameterTypes();
                            if (parmts[0] == int.class) {
                                method.invoke(object, set.getInt(columnName));
                            } else if (parmts[0] == boolean.class) {
                                method.invoke(object, set.getBoolean(columnName));
                            } else if (parmts[0] == float.class) {
                                method.invoke(object, set.getFloat(columnName));
                            } else if (parmts[0] == double.class) {
                                method.invoke(object, set.getDouble(columnName));
                            } else {
                                method.invoke(object, set.getString(columnName));
                            }
                        }
                    }
                }
                result.add(object);
            }
            return result;
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("类名错误", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("数据库关闭连接错误", ex);
            }
        }
        return null;
    }
}
