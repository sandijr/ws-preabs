/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presensi;

import java.util.Date;

/**
 *
 * @author user
 */
public class PegAbsen {

    private String nik, nohp, nama;
    private Date tgllhr;
    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public Date getTgllhr() {
        return tgllhr;
    }

    public void setTgllhr(Date tgllhr) {
        this.tgllhr = tgllhr;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

}
