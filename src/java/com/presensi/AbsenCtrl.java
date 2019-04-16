/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presensi;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
//import com.askes.peserta.conf.Helper;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import io.jsonwebtoken.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author Student-03
 */
@Controller
@RequestMapping("/presensi")
public class AbsenCtrl {

//    @RequestMapping(value = "/inquery/nik/{strnik}", method = RequestMethod.GET)//+ 
//    @ResponseBody
//    public Response getPstDukcapilByNIK(@PathVariable String strnik) {
//        return Response.status(401).entity(strnik).build();
//    }
//    @RequestMapping(value = "/get/user", method = RequestMethod.POST)
//    @ResponseBody
//    @POST
//    @Consumes("application/json")
//    public Response getUser(InputStream inputStream) {
//        //
//        String strParam = "";
//        String line = null;
//        String strError = "";
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            while ((line = br.readLine()) != null) {
//                strParam = strParam + "" + line;
//            }
//            JSONParser parser = new JSONParser();
//            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(strParam);
//            //
//            String strUID = (String) json.get("USERID");
//            String strPWD = (String) json.get("PWD");
//            DatUser datUser = new DatUser();
//            datUser.setUid("Usernya : " + strUID);
//            datUser.setPwd("Passwordnya :" + strPWD);
//            List<DatUser> lstDU = new ArrayList<DatUser>();
//            lstDU.add(datUser);
//            return Response.status(200).entity(lstDU).build();
////            Datuser du = masterService.getDatUser(strUID, strPWD);
////            if (du.getUserid() == null) {
////                List<Datuser> lstDU = new ArrayList<>();
////                //return Response.status(401).entity("User/Password Salah/Tidak Ditemukan").build();
////                return Response.status(401).entity(lstDU).build();
////            } else {
////                List<Datuser> lstDU = new ArrayList<>();
////                lstDU.add(du);
////                return Response.status(200).entity(lstDU).build();
////            }
//        } catch (Exception e) {
//            strError = e.getMessage();
//        }
//        return Response.status(401).entity(strError).build();
//    }
    @RequestMapping(value = "/get/token", method = RequestMethod.POST)//untuk mendapatkan tokennya
    @ResponseBody
    @POST
    @Consumes("application/json")
    public Response getJWTToken(InputStream inputStream
    ) {
        String strParam = "";
        String line = null;
        String strError = "";
        String strUSERID = "";
        String strPWD = "";
        String strNoHP = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                strParam = strParam + "" + line;
            }
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(strParam);
            strUSERID = (String) json.get("username");
            strPWD = (String) json.get("credentials");
            strNoHP = (String) json.get("hp");
            if (!strUSERID.equalsIgnoreCase("$@nDy")
                    || !strPWD.equalsIgnoreCase("jUni@r")) {
                return Response.status(Status.FORBIDDEN).entity("Anda tidak berhak mengakses API").build();
            }
            //            
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            String issuer = strNoHP;//"Sandi Juniar";
            String subject = "Service Absensi";
            Long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            Date exp = new Date(nowMillis + (1000 * 300)); // 300 seconds
            //
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("Sandi");
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            //Let's set the JWT Claims
            JwtBuilder builder = Jwts.builder().setId(nowMillis.toString())
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey)
                    .setExpiration(exp);
//                return builder.compact();
            JSONObject obj = new JSONObject();
            obj.put("token", builder.compact());
            obj.put("expireDate", exp);
            obj.put("versi", Config.APP_VERSI);
            return Response.status(200).entity(obj).build();

        } catch (Exception e) {
            strError = e.getMessage();
        }
        return Response.status(401).entity(strError).build();
    }

    @RequestMapping(value = "/valpegabsen", method = RequestMethod.POST)//untuk memvalidasi token yg sudah tercreate
    @ResponseBody
    @POST
    @Consumes("application/json")
    public Response valPegAbsen(InputStream inputStream,
            @RequestHeader(value = "authorization") String token) {
        //Buka Service Koneksi

        String strParam = "";
        String line = null;
        String strError = "";
        try {
            Long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                strParam = strParam + "" + line;
            }
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(strParam);
            //
//            String strToken = (String) json.get("token");
            String strNik = (String) json.get("nrk");
            String strNohp = (String) json.get("hp");
            String strTgllhr = (String) json.get("tgllhr");

            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("Sandi"))
                    .parseClaimsJws(token).getBody();

//            System.out.println("ID: " + claims.getId());
//            System.out.println("Subject: " + claims.getSubject());
//            System.out.println("Issuer: " + claims.getIssuer());
//            System.out.println("Expiration: " + claims.getExpiration());
//            System.out.println(claims.getExpiration());
//            System.out.println(now);
            System.out.println("isuer: " + claims.getIssuer());
            System.out.println("hp: " + strNohp);
            if (!claims.getIssuer().equalsIgnoreCase(strNohp)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Token tidak bisa digunakan"
            }

            if (claims.getExpiration().before(now)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Expired Token"
            }

            AbsenDao absenDao = new AbsenDaoImpl();
            PegAbsen pegAbsen = absenDao.getPegAbsen(strNik);

            if (pegAbsen.getNik() == null) {
                return Response.status(Status.UNAUTHORIZED).entity("Nomor Pegawai Belum terdaftar").build();
            } else {
                if (pegAbsen.getNohp() == null
                        || !pegAbsen.getNohp().equalsIgnoreCase(strNohp)) {
                    return Response.status(Status.UNAUTHORIZED).entity("No.HP salah").build();
                } else if (pegAbsen.getTgllhr() == null
                        || !Helper.convertDatetoStringDDMMYYYY(pegAbsen.getTgllhr()).equalsIgnoreCase(strTgllhr)) {
                    return Response.status(Status.UNAUTHORIZED).entity("Tgl. Lahir salah").build();

                }
            }
            return Response.status(200).entity(pegAbsen.getNama()).build();
//            return Response.status(200).entity(Helper.convertDatetoStringDDMMYYYYtimestamp(claims.getExpiration())).build();

        } catch (Exception e) {
            strError = e.getMessage();
        }
        return Response.status(401).entity(null).build();
    }

    @RequestMapping(value = "/valpegabsen2", method = RequestMethod.POST)//untuk memvalidasi token yg sudah tercreate
    @ResponseBody
    @POST
    @Consumes("application/json")
    public Response valPegAbsen2(InputStream inputStream,
            @RequestHeader(value = "authorization") String token) {
        //Buka Service Koneksi

        String strParam = "";
        String line = null;
        String strError = "";
        try {
            Long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                strParam = strParam + "" + line;
            }
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(strParam);
            //
//            String strToken = (String) json.get("token");
            String strNik = (String) json.get("nrk");
            String strNohp = (String) json.get("hp");

            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("Sandi"))
                    .parseClaimsJws(token).getBody();

//            System.out.println("ID: " + claims.getId());
//            System.out.println("Subject: " + claims.getSubject());
//            System.out.println("Issuer: " + claims.getIssuer());
//            System.out.println("Expiration: " + claims.getExpiration());
//            System.out.println(claims.getExpiration());
//            System.out.println(now);
            System.out.println("isuer: " + claims.getIssuer());
            System.out.println("hp: " + strNohp);
            if (!claims.getIssuer().equalsIgnoreCase(strNohp)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Token tidak bisa digunakan"
            }

            if (claims.getExpiration().before(now)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Expired Token"
            }

            AbsenDao absenDao = new AbsenDaoImpl();
            PegAbsen pegAbsen = absenDao.getPegAbsen(strNik);

            JSONObject obj = new JSONObject(); 
            obj.put("nama", "");
            obj.put("tgllhr", "");
            if (pegAbsen.getNik() == null) {
                obj.put("nama", "Nomor Pegawai Belum terdaftar");
                return Response.status(Status.UNAUTHORIZED).entity(obj).build();
            } else {
                if (pegAbsen.getNohp() == null
                        || !pegAbsen.getNohp().equalsIgnoreCase(strNohp)) {
                    obj.put("nama", "No.HP salah");
                    return Response.status(Status.UNAUTHORIZED).entity(obj).build();
                }
            }
            obj.put("nama", pegAbsen.getNama());
            obj.put("tgllhr", Helper.convertDatetoStringDDMMYYYY(pegAbsen.getTgllhr()));
            return Response.status(200).entity(obj).build();
//            return Response.status(200).entity(Helper.convertDatetoStringDDMMYYYYtimestamp(claims.getExpiration())).build();

        } catch (Exception e) {
            strError = e.getMessage();
        }
        return Response.status(401).entity(null).build();
    }

    @RequestMapping(value = "/saveabsen", method = RequestMethod.POST)//untuk memvalidasi token yg sudah tercreate
    @ResponseBody
    @POST
    @Consumes("application/json")
    public Response saveAbsen(InputStream inputStream,
            @RequestHeader(value = "authorization") String token) {
        //Buka Service Koneksi

        String strParam = "";
        String line = null;
        String strError = "";
        try {
            Long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                strParam = strParam + "" + line;
            }
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(strParam);
            //
            String strNohp = (String) json.get("hp");
            String strNrk = (String) json.get("nrk");
//            String strTglabsen = (String) json.get("tglabsen");
//            String strManual = (String) json.get("manual");
//            String strShifupd = (String) json.get("shifupd");
//            String strAwalupd = (String) json.get("awalupd");
//            String strAkhirupd = (String) json.get("akhirupd");
//            String strKeterangan = (String) json.get("keterangan");
//            String strUrut = (String) json.get("urut");
            String strLatitude = (String) json.get("latitude");
            String strlLongitude = (String) json.get("longitude");
            String strLocshare = (String) json.get("locshare");

            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("Sandi"))
                    .parseClaimsJws(token).getBody();

            if (!claims.getIssuer().equalsIgnoreCase(strNohp)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Token tidak bisa digunakan"
            }

            if (claims.getExpiration().before(now)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Expired Token"
            }

            AbsenDao absenDao = new AbsenDaoImpl();
            AbsenIndo absenIndo = new AbsenIndo();
//            absenIndo.setAHIRUPD(Helper.convertStringDDMMYYYYtoDate(strAkhirupd));
//            absenIndo.setAWALUPD(Helper.convertStringDDMMYYYYtoDate(strAwalupd));
//            absenIndo.setKETERANGAN(strKeterangan);
            absenIndo.setLatitude(strLatitude);
            absenIndo.setLongitude(strlLongitude);
            absenIndo.setLocshare(strLocshare);
//            absenIndo.setMANUAL(strManual);
            absenIndo.setNrk(strNrk);
//            absenIndo.setSHIFTUPD(Helper.convertStringDDMMYYYYtoDate(strShifupd));
//            absenIndo.setTGL_ABSEN(Helper.convertStringDDMMYYYYtoDate(strTglabsen));
//            absenIndo.setURUT(strUrut);
            absenDao.saveAbsenIndo(absenIndo);
            return Response.status(200).entity(Helper.getTimeddmmyyyyHHmmssSSS()).build();

        } catch (Exception e) {
            strError = e.getMessage();
        }
        return Response.status(401).entity(strError).build();
    }

    @RequestMapping(value = "/histabsen", method = RequestMethod.POST)//untuk memvalidasi token yg sudah tercreate
    @ResponseBody
    @POST
    @Consumes("application/json")
    public Response histabsen(InputStream inputStream,
            @RequestHeader(value = "authorization") String token) {
        //Buka Service Koneksi

        String strParam = "";
        String line = null;
        String strError = "";
        try {
            Long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                strParam = strParam + "" + line;
            }
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(strParam);
            //
//            String strToken = (String) json.get("token");
            String strNik = (String) json.get("nrk");
            String strNohp = (String) json.get("hp");
            String strBln = (String) json.get("bln");
            String strThn = (String) json.get("thn");

            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("Sandi"))
                    .parseClaimsJws(token).getBody();

//            System.out.println("ID: " + claims.getId());
//            System.out.println("Subject: " + claims.getSubject());
//            System.out.println("Issuer: " + claims.getIssuer());
//            System.out.println("Expiration: " + claims.getExpiration());
//            System.out.println(claims.getExpiration());
//            System.out.println(now);
            // System.out.println("isuer: " + claims.getIssuer());
            // System.out.println("hp: " + strNohp);
            if (!claims.getIssuer().equalsIgnoreCase(strNohp)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Token tidak bisa digunakan"
            }

            if (claims.getExpiration().before(now)) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Expired Token"
            }

            AbsenDao absenDao = new AbsenDaoImpl();
            List<AbsenIndo> absenIndos = absenDao.getHistAbsen(strNik, strBln, strThn);
            if (absenIndos.size() == 0) {
                return Response.status(Status.UNAUTHORIZED).entity(null).build();//"Expired Token"
            }

            return Response.status(200).entity(absenIndos).build();
//            return Response.status(200).entity(Helper.convertDatetoStringDDMMYYYYtimestamp(claims.getExpiration())).build();

        } catch (Exception e) {
            strError = e.getMessage();
        }
        return Response.status(401).entity(null).build();
    }
}
