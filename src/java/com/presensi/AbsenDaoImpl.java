/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presensi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class AbsenDaoImpl implements AbsenDao {

    @Override
    public PegAbsen getPegAbsen(String nik) {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        PegAbsen ret = new PegAbsen();
        try {
            String query = " SELECT NRK, NAMA, HP, TALHIR "
                    + " FROM " + Config.JDBC_DB2_UID + ".PEGABSEN WHERE NRK=? ";
            con = Helper.BuatKoneksi();
            ps = con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, nik);
            rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    ret.setNik(rs.getString("NRK"));
                    ret.setNohp(rs.getString("HP"));
                    ret.setNama(rs.getString("NAMA"));
                    ret.setTgllhr(rs.getDate("TALHIR"));
                }
            }
        } catch (SQLException e) {
        } finally {
            Helper.safeClose(rs, ps, con);
        }
        return ret;
    }

    @Override
    public void saveAbsenIndo(AbsenIndo p) {

        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Helper.BuatKoneksi();
            String sql = " INSERT INTO " + Config.JDBC_DB2_UID + ".ABSEN_INDO (NRK, TGL_ABSEN, MANUAL,  AWALUPD, LATITUDE, LONGITUDE, LOCSHARE ) "
                    + " VALUES (?,CURRENT DATE,'2',CURRENT TIME,?,?,?)";
            //+ " VALUES (?,GETDATE(),'2',GETDATE(),?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNrk());
            //ps.setDate(2, new java.sql.Date(p.getTGL_ABSEN().getTime()));
            //ps.setString(2, "2");
            //ps.setDate(4, new java.sql.Date(p.getSHIFTUPD().getTime()));
            // ps.setDate(5, new java.sql.Date(p.getAWALUPD().getTime()));
            //ps.setDate(6, new java.sql.Date(p.getAHIRUPD().getTime()));
            //ps.setString(7, p.getKETERANGAN());
            //ps.setString(8, p.getURUT());
            ps.setDouble(2, Double.valueOf(p.getLatitude()));
            ps.setDouble(3, Double.valueOf(p.getLongitude()));
            ps.setString(4, p.getLocshare());
            ps.executeUpdate();
        } catch (Exception err) {
            err.getMessage().toString();
        } finally {
            Helper.safeClose(null, ps, con);
        }
    }

    @Override
    public List<AbsenIndo> getHistAbsen(String nrk, String bln, String thn) {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<AbsenIndo> ret = new ArrayList<AbsenIndo>();
        try {
            String query = " SELECT NRK, TGL_ABSEN, AWALUPD, LATITUDE, LONGITUDE, LOCSHARE "
                    + " FROM " + Config.JDBC_DB2_UID + ".ABSEN_INDO "
                    + " WHERE NRK=? "
                    + " AND MONTH(TGL_ABSEN) = ? "
                    + " AND YEAR(TGL_ABSEN) = ?"
                    + " AND MANUAL = 2 "
                    + " ORDER BY AWALUPD ASC ";
            con = Helper.BuatKoneksi();
            ps = con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, nrk);
            ps.setString(2, bln);
            ps.setString(3, thn);
            rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    AbsenIndo absen = new AbsenIndo();
                    absen.setNrk(rs.getString("NRK"));
                    absen.setLatitude(rs.getString("LATITUDE"));
                    absen.setLongitude(rs.getString("LONGITUDE"));
                    absen.setLocshare(rs.getString("LOCSHARE"));
                    absen.setTglabsen(Helper.convertDatetoStringDDMMYYYY(rs.getDate("TGL_ABSEN")));
                    absen.setAwalupd(Helper.convertDatetoStringHHmmss(rs.getTime("AWALUPD")));
                    ret.add(absen);
                }
            }
        } catch (SQLException e) {
        } finally {
            Helper.safeClose(rs, ps, con);
        }
        return ret;
    }

}
