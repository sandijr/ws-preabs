/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presensi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author user
 */
public class Helper {

    public static Connection BuatKoneksi() throws SQLException {
        Connection con = null;
        try {
            Class.forName(Config.JDBC_DB2_DRIVER);
        } catch (java.lang.ClassNotFoundException e) {
        }
        try {
            con = DriverManager.getConnection(Config.JDBC_DB2_URL, Config.JDBC_DB2_UID, Config.JDBC_DB2_PWD);
        } catch (SQLException ex) {
        }
        return con;
    }

    public static void safeClose(ResultSet rs, Statement statement, Connection con) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            // ignore this
        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            // ignore this
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            // ignore this
        }
    }

    public static String convertDatetoStringDDMMYYYY(Date date) {
        String reportDate = "1900-01-01";
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            reportDate = df.format(date);
        } catch (Exception e) {
            return null;
        }
        return reportDate;
    }
    public static String convertDatetoStringDDMMYYYYHHmmss(Date date) {
        String reportDate = "1900-01-01";
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            reportDate = df.format(date);
        } catch (Exception e) {
            return null;
        }
        return reportDate;
    }
    public static String convertDatetoStringHHmmss(Date date) {
        String reportDate = "1900-01-01";
        try {
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            reportDate = df.format(date);
        } catch (Exception e) {
            return null;
        }
        return reportDate;
    }
    public static Date convertStringDDMMYYYYtoDate(String tgl) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(tgl);
            ////System.out.println(tgl);
        } catch (ParseException e) {
            //System.out.println("error B");
            return testDate;
        }
        ////System.out.println("testDate "+testDate);
        return testDate;
    }
    public static String getTimeddmmyyyyHHmmssSSS() {
        String ret = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        ret = sdf.format(date);
        return ret;
    }
}
