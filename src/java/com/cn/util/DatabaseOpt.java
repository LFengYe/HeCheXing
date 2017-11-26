/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.tomcat.jdbc.pool.DataSource;

/**
 *
 * @author LFeng
 */
public class DatabaseOpt{

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DatabaseOpt.class);

    public Connection getConnectGps(String gpsUrl) {
        try {
            Properties prop = new Properties();
            prop.load(DatabaseOpt.class.getClassLoader().getResourceAsStream("./config.properties"));
            Class.forName(prop.getProperty("driverName"));
            Connection connect = DriverManager.getConnection(gpsUrl, prop.getProperty("username"), prop.getProperty("password"));
            return connect;
        } catch (ClassNotFoundException ex) {
            logger.error("找不类名错误", ex);
        } catch (IOException ex) {
            logger.error("IO错误", ex);
        } catch (SQLException ex) {
            logger.error("SQL错误", ex);
        } finally {
        }
        return null;
    }
    
    /**
     * 连接数据库
     *
     * @return
     */
    public Connection getConnect() {

        //Tomcat jdbc pool连接池
        Context ctx;
        try {
            ctx = new InitialContext();
            Context envctx = (Context) ctx.lookup("java:comp/env");
            DataSource ds = (DataSource) envctx.lookup("jdbc/TestDB");
            Future<Connection> futrue = ds.getConnectionAsync();
            while (!futrue.isDone()) {
                System.out.println("Connection is not yet available. Do some background work");
                try {
                    Thread.sleep(100); //simulate work       
                } catch (InterruptedException x) {
                    Thread.currentThread().interrupted();
                }
            }
            Connection conn = futrue.get();
            return conn;
        } catch (NamingException e) {
            logger.error("NamingException", e);
        } catch (SQLException e) {
            logger.error("SQL错误", e);
        } catch (InterruptedException ex) {
            logger.error("InterruptedException", ex);
        } catch (ExecutionException ex) {
            logger.error("ExecutionException", ex);
        } finally {

        }
        return null;
    }
}
