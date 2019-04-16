/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presensi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public interface AbsenDao {
     public PegAbsen getPegAbsen(String nik);
     public List<AbsenIndo> getHistAbsen(String nik, String bln, String thn);
     public void saveAbsenIndo(AbsenIndo absenIndo);
}
