/*******************************************************************************
 * Copyright 2018  Uttaradit Hospital
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.utt.app.util;

import com.alee.laf.label.WebLabel;
import com.alee.laf.text.WebTextField;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import okhttp3.OkHttpClient;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.utt.app.InApp;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.ui.IFrame;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static net.sf.dynamicreports.report.builder.DynamicReports.template;

public class Setup {
	public static final double CM_PER_INCH = 0.393700787d;
	public static final double INCH_PER_CM = 2.545d;
	public static final double INCH_PER_MM = 25.45d;
	/**
	 * Converts the given pixels to cm's based on the supplied DPI
	 *
	 * @param pixels
	 * @param dpi
	 * @return
	 */
	public static double pixelsToCms(double pixels, double dpi) {
	    return inchesToCms(pixels / dpi);
	}
	/**
	 * Converts the given cm's to pixels based on the supplied DPI
	 *
	 * @param cms
	 * @param dpi
	 * @return
	 */
	public static double cmsToPixel(double cms, double dpi) {
	    return cmToInches(cms) * dpi;
	}
	/**
	 * Converts the given cm's to inches
	 *
	 * @param cms
	 * @return
	 */
	public static double cmToInches(double cms) {
	    return cms * CM_PER_INCH;
	}
	/**
	 * Converts the given inches to cm's
	 *
	 * @param inch
	 * @return
	 */
	public static double inchesToCms(double inch) {
	    return inch * INCH_PER_CM;
	}
    public static boolean checkEmptyTextField(WebTextField t){
        String id ="";
        id = t.getText();
        if(id.equals(""))
            return true;
        else return false;
    }
    public static void setLocale(){
        String[] parts = Prop.getProperty("app.locale").split("_");
        Locale.setDefault(new Locale(parts[0],parts[1]));
    }
    public static Color getColor() {
        return new Color(229,229,229);
    }
    public static WebTextField getTextField(int fwidth) {
        WebTextField tf = new WebTextField();
        tf.setFont(new Font("Tahoma", Font.PLAIN, fwidth));
        tf.setText("");
        return tf;
    }
    public static WebLabel getLabel(String name,int fwidth,int type ,int align) {
        WebLabel label=new WebLabel(name,align);
        label.setFont(new Font("Tahoma", type, fwidth));
        return label;
    }
    public static String DateInDBMSSQL(String date){
        String dateInDBMSSQL="";
        String Date_from=date;
        String Month_from=date;
        String Year_from=date;
        String year =Year_from.substring(0, 4);
        String month=Month_from.substring(5, 7);
        String day=Date_from.substring(8);
        int db_year=Integer.parseInt(year)-543 ;
        dateInDBMSSQL=db_year+"-"+month+"-"+day;
        return dateInDBMSSQL;
    }
    public static String AgeInAll(String birthday){
        String age="";
        LocalDate birthdate = new LocalDate(birthday);          //Birth date
        LocalDate now = new LocalDate();                    //Today's date
        Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
        int age_year=period.getYears();
        int age_month=period.getMonths();
        int age_day=period.getDays();
        return age=age_year+" ปี "+age_month+" เดือน "+age_day+" วัน";
    }
    public static String AgeInYear(String birthday){
        String age="";
        LocalDate birthdate = new LocalDate (birthday);          //Birth date
        LocalDate now = new LocalDate();                    //Today's date
        Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
        int age_year=period.getYears();
        return age=age_year+"";
    }
    public static String DateInDBMSSQLno(String date){
        String dateInDBMSSQL="";
        String Date_from=date;
        String Month_from=date;
        String Year_from=date;
        String year =Year_from.substring(0, 4);
        String month=Month_from.substring(4, 6);
        String day=Date_from.substring(6);
        int db_year=Integer.parseInt(year)-543 ;
        dateInDBMSSQL=db_year+"-"+month+"-"+day;

        return dateInDBMSSQL;
    }
    public static String ShowThaiDate(String date){
        String DateThai="",day="";
        String Date_from=date;
        String Month_from=date;
        String Year_from=date;
        String year =Year_from.substring(0, 4);
        String month=Month_from.substring(5, 7);
        String day1=Date_from.substring(8).trim();
        if(day1.substring(0,1).equals("0")){
            day=day1.substring(1);
        }else{
            day=day1;
        }
        DateThai=day+" "+getMonthThaiName2(month)+" พ.ศ. "+year;

        return DateThai;
    }
    public static String getMonthThaiName2(String month){

        String currentMonth=month;
        String currentMonthThaiName="";
        if(currentMonth.equals("01")){
            currentMonthThaiName="มกราคม";
        }
        else if(currentMonth.equals("02")){
            currentMonthThaiName="กุมภาพันธ์";
        }
        else if(currentMonth.equals("03")){
            currentMonthThaiName="มีนาคม";
        }
        else if(currentMonth.equals("04")){
            currentMonthThaiName="เมษายน";
        }
        else if(currentMonth.equals("05")){
            currentMonthThaiName="พฤษภาคม";
        }
        else if(currentMonth.equals("06")){
            currentMonthThaiName="มิถุนายน";
        }
        else if(currentMonth.equals("07")){
            currentMonthThaiName="กรกฎาคม";
        }
        else if(currentMonth.equals("08")){
            currentMonthThaiName="สิงหาคม";
        }
        else if(currentMonth.equals("09")){
            currentMonthThaiName="กันยายน";
        }
        else if(currentMonth.equals("10")){
            currentMonthThaiName="ตุลาคม";
        }
        else if(currentMonth.equals("11")){
            currentMonthThaiName="พฤศจิกายน";
        }
        else if(currentMonth.equals("12")){
            currentMonthThaiName="ธันวาคม";
        }
        return currentMonthThaiName;
    }

    public static JasperReportBuilder PrintOPD(String form_code,String h_print) {
        String confFile="report/"+form_code.trim()+".jrxml";
        int h_page=0;
        if(h_print.equals("1")){
            h_page=Integer.parseInt(Prop.getProperty("print.hpage1"));
        }else if(h_print.equals("2")){
            h_page=Integer.parseInt(Prop.getProperty("print.hpage2"));
        }
        InputStream is;
        JasperReportBuilder report = DynamicReports.report();
        report.setTemplate(template()).setPageFormat(590, h_page, PageOrientation.PORTRAIT);
        is = Setup.class.getClassLoader().getResourceAsStream(confFile );
        try {
            report.setTemplateDesign(is);
            report.setDataSource(new JREmptyDataSource());
        } catch (DRException e) {
            e.printStackTrace();
        }

        return report;
    }
    public static String getName(String hn){
        String name_to="",initname="";
        Connection conn;
        PreparedStatement stmt,stmt2;
        ResultSet rs,rs2;
        String query_db= SetupSQL.getName1(hn) ;
        String query_name=SetupSQL.getName2(hn) ;
        conn=new DBmanager().getConnMSSql();
        try {
            stmt = conn.prepareStatement(query_db);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String sex_pt="",mar_pt="";
                Date birthday=rs.getDate(1);
                String age_pt=Setup.AgeInAll(birthday.toString()).trim();
                if(rs.getString(4)==null || rs.getString(4).equals("1") || rs.getString(4).equals("2") || rs.getString(4).equals("3") || rs.getString(4).equals("127") || rs.getString(4).equals("128")){
                    sex_pt=rs.getString(2).trim();
                    mar_pt=rs.getString(3).trim();
                    int age_y_int= Integer.parseInt(Setup.AgeInYear(birthday.toString()).trim());
                    if(age_y_int<15){
                        if(sex_pt.equals("1")){
                            initname="ด.ช.";
                        }else if(sex_pt.equals("2")){
                            initname="ด.ญ.";
                        }
                    }else if(age_y_int>=15){
                        if(sex_pt.equals("1")){
                            initname="นาย";
                        }else if(sex_pt.equals("2")){
                            if(mar_pt.equals("1")){
                                initname="นางสาว";
                            }else {
                                initname="นาง";
                            }
                        }
                    }
                }else{
                    for(int i = 0; i< IFrame.initname_indb.length; i++){
                        if(rs.getString(4).trim().equals(IFrame.initname_indb[i][0])){
                            initname=IFrame.initname_indb[i][1];
                            break;
                        }
                    }
                }
            }
            stmt.close();
            //
            stmt2 = conn.prepareStatement(query_name);
            rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                if(rs2.getString(1) !=null){
                    name_to=" "+initname+" "+rs2.getString(1).substring(1).trim()+" "+rs2.getString(2).substring(1).trim();
                }
            }
            stmt2.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name_to;
    }
    public static String getClinic(String vn_in,String fndate){
        String clinic="";
        int p=0;
        try{
            String sql_clinic="select  clinic from vnpres where vn=? and visitdate= '"+fndate+"'  order by clinic";
            Connection conn=new DBmanager().getConnMSSql();
            PreparedStatement stmt = conn.prepareStatement(sql_clinic);
            stmt.setString(1, vn_in);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if(rs.getString(1)!=null){
                    clinic+=rs.getString(1).trim()+"-";
                }
                p++;
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p+":"+clinic;
    }
    public static String getMonthShortThaiName(String month){

        String currentMonth=month;
        String currentMonthThaiName="";
        if(currentMonth.equals("01")){
            currentMonthThaiName="ม.ค.";
        }
        else if(currentMonth.equals("02")){
            currentMonthThaiName="ก.พ.";
        }
        else if(currentMonth.equals("03")){
            currentMonthThaiName="มี.ค.";
        }
        else if(currentMonth.equals("04")){
            currentMonthThaiName="เม.ย.";
        }
        else if(currentMonth.equals("05")){
            currentMonthThaiName="พ.ค.";
        }
        else if(currentMonth.equals("06")){
            currentMonthThaiName="มิ.ย.";
        }
        else if(currentMonth.equals("07")){
            currentMonthThaiName="ก.ค.";
        }
        else if(currentMonth.equals("08")){
            currentMonthThaiName="ส.ค.";
        }
        else if(currentMonth.equals("09")){
            currentMonthThaiName="ก.ย.";
        }
        else if(currentMonth.equals("10")){
            currentMonthThaiName="ต.ค.";
        }
        else if(currentMonth.equals("11")){
            currentMonthThaiName="พ.ย.";
        }
        else if(currentMonth.equals("12")){
            currentMonthThaiName="ธ.ค.";
        }
        return currentMonthThaiName;
    }
    public static String getINTmonth(String month) {
    	 String currentMonthThaiName=month;
         String currentMonth ="";
         if(currentMonthThaiName.equals("ม.ค.")){
             currentMonth="01";
         }
         else if(currentMonthThaiName.equals("ก.พ.")){
             currentMonth="02";
         }
         else if(currentMonthThaiName.equals("มี.ค.")){
             currentMonth="03";
         }
         else if(currentMonthThaiName.equals("เม.ย.")){
             currentMonth="04";
         }
         else if(currentMonthThaiName.equals("พ.ค.")){
             currentMonth="05";
         }
         else if(currentMonthThaiName.equals("มิ.ย.")){
             currentMonth="06";
         }
         else if(currentMonthThaiName.equals("ก.ค.")){
             currentMonth="07";
         }
         else if(currentMonthThaiName.equals("ส.ค.")){
             currentMonth="08";
         }
         else if(currentMonthThaiName.equals("ก.ย.")){
             currentMonth="09";
         }
         else if(currentMonthThaiName.equals("ต.ค.")){
             currentMonth="10";
         }
         else if(currentMonthThaiName.equals("พ.ย.")){
             currentMonth="11";
         }
         else if(currentMonthThaiName.equals("ธ.ค.")){
             currentMonth="12";
         }
         return currentMonth;
    }
    public static void SetUnderline(WebLabel label){
        Font font = label.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        label.setFont(font.deriveFont(attributes));
    }
    public static String LOSInDayNow(String admDatein){
		String age="";
		LocalDate admDate = new LocalDate (admDatein);          //Birth date
        LocalDate now = new LocalDate();                    //Today's date
        Period period = new Period(admDate, now, PeriodType.yearMonthDay());
        int age_day=period.getDays();
		return age=age_day+"";
	}
    public static String DateInDBMSSQLRef43no543(String date){
		String dateInDBMSSQL="";
		String Date_from=date;
		String Month_from=date;
		String Year_from=date;
		String year =Year_from.substring(0, 4);
		String month=Month_from.substring(5, 7);
		String day=Date_from.substring(8,10);
		
		dateInDBMSSQL=year+month+day;
		
		return dateInDBMSSQL;
		
	}
    public static String DateInDBMSSQLRef(String date){
		String dateInDBMSSQL="";
		String Date_from=date;
		String Month_from=date;
		String Year_from=date;
		String year =Year_from.substring(0, 4);
		String month=Month_from.substring(5, 7);
		String day=Date_from.substring(8);
		
		int db_year=Integer.parseInt(year)+543 ;
		dateInDBMSSQL=db_year+month+day;
		
		return dateInDBMSSQL;
		
	}
    public static String ConvertDateTimePrint(Calendar time){
		int date = time.get(time.DAY_OF_MONTH );
		int month = time.get(time.MONTH)+1;
		int  year = time.get(time.YEAR) + 543;
		String currentDate = date + " " + getMonthThaiName(time) + " " + year;
		
		SimpleDateFormat TimeFormat_now = new SimpleDateFormat("HH:mm:ss");
		Calendar cal_time= Calendar.getInstance();
		String time_now = TimeFormat_now.format(cal_time.getTime());
		
		return currentDate+" เวลา "+time_now;
	}
    public static String getMonthThaiName(Calendar time){
		 
		String currentMonth=getMonth(time);
		String currentMonthThaiName="";
		    if(currentMonth.equals("1")){
		    	currentMonthThaiName="มกราคม";
		    }
		    else if(currentMonth.equals("2")){
		    	currentMonthThaiName="กุมภาพันธ์";
		    }
		    else if(currentMonth.equals("3")){
		    	currentMonthThaiName="มีนาคม";
		    }
		    else if(currentMonth.equals("4")){
		    	currentMonthThaiName="เมษายน";
		    }
		    else if(currentMonth.equals("5")){
		    	currentMonthThaiName="พฤษภาคม";
		    }
		    else if(currentMonth.equals("6")){
		    	currentMonthThaiName="มิถุนายน";
		    }
		    else if(currentMonth.equals("7")){
		    	currentMonthThaiName="กรกฎาคม";
		    }
		    else if(currentMonth.equals("8")){
		    	currentMonthThaiName="สิงหาคม";
		    }
		    else if(currentMonth.equals("9")){
		    	currentMonthThaiName="กันยายน";
		    }
		    else if(currentMonth.equals("10")){
		    	currentMonthThaiName="ตุลาคม";
		    }
		    else if(currentMonth.equals("11")){
		    	currentMonthThaiName="พฤศจิกายน";
		    }
		    else if(currentMonth.equals("12")){
		    	currentMonthThaiName="ธันวาคม";
		    }
		return currentMonthThaiName;
	}
    public static String getMonth(Calendar time){
		int month=time.get(time.MONTH)+1;
		String currentMonth=month+"";
		return currentMonth;
	}
    public static String GetDateTimeNow(){
		DateTime now = new DateTime();
		String datetime_in=now.toLocalDateTime().toString();
		return datetime_in;
	}
    public static void getlog(String hn,String ip,String filescanname,String type) {
    	Connection con;
		PreparedStatement stmt;
		ResultSet rs ;
		String sql_addlog="";
    	if(type.equals("p")) {
    		sql_addlog = "insert into  inlog ( visitdatetime,hn,ip,fn,operation) values ('"+Setup.GetDateTimeNow()+"','"+hn+"','"+InApp.ip+"','"+filescanname+"','p')";
    		
    	}else {
    		sql_addlog = "insert into  inlog ( visitdatetime,hn,ip,fn,operation) values ('"+Setup.GetDateTimeNow()+"','"+hn+"','"+InApp.ip+"','"+filescanname+"','r')";
    		
    	}
    	try {
			con = new DBmanager().getConnMySql();
			stmt = con.prepareStatement(sql_addlog);
			int rs_save = stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    public static String ConverttoScanDate(String _visitdate){
		String date =_visitdate.substring(_visitdate.length()-2);
		String month =_visitdate.substring(_visitdate.length()-5,_visitdate.length()-3);
		int  year = Integer.parseInt(_visitdate.substring(0,4))+543;
			String visitDate = ""+year+month+date;
		return visitDate;
	}
    public static String GetDateNow43(){
		DateTime now = new DateTime();
		String date_in=now.toLocalDateTime().toString();
		String y=date_in.substring(0, 4);
		String m=date_in.substring(5, 7);
		String d=date_in.substring(8, 10);
		String h=date_in.substring(11, 13);
		String mm=date_in.substring(14, 16);
		String s=date_in.substring(17, 19);
		date_in=y+m+d+h+mm+s;
		return date_in;
	}
    public static String ShowThaiDate1(String date){
		String DateThai="",day1="";
		String Date_from=date;
		String Month_from=date;
		String Year_from=date;
		String year =Year_from.substring(0, 4);
		String month=Month_from.substring(5, 7);
		String day=Date_from.substring(8).trim();
		if(day.substring(0,1).equals("0")){
			day1=day.substring(1);
		}
		else{
			day1=day;
		}
		int Thai_year=Integer.parseInt(year)+543 ;
		DateThai=day1+" "+getMonthThaiName2(month)+" พ.ศ. "+Thai_year;
		
		return DateThai;
		
	}
    public static String ShowThaiDateShort1(String date){
		String DateThai="",day1="";
		String Date_from=date;
		String Month_from=date;
		String Year_from=date;
		String year =Year_from.substring(0, 4);
		String month=Month_from.substring(5, 7);
		String day=Date_from.substring(8).trim();
		if(day.substring(0,1).equals("0")){
			day1=day.substring(1);
		}
		else{
			day1=day;
		}
		int Thai_year=Integer.parseInt(year)+543 ;
		DateThai=day1+" "+getMonthShortThaiName(month)+" "+Thai_year;
		
		return DateThai;
		
	}
    public static String GetDateNow(){
		DateTime now = new DateTime();
		String date_in=now.toLocalDate().toString();
		return date_in;
	}
    public static String GetDateMo(int m){
		DateTime now = new DateTime();
		String date_in=now.minusMonths(m).toLocalDate().toString();
		
		return date_in;
	}
    public static String DateServ43(String date){
		String date_serv="";
		String Date_from=date;
		String Month_from=date;
		String Year_from=date;
		String year =Year_from.substring(0, 4);
		String month=Month_from.substring(5, 7);
		String day=Date_from.substring(8);
		
		date_serv=year+month+day;
		
		return date_serv;
		
	}
    
    public static OkHttpClient getUnsafeOkHttpClient() {
	    try {
	        // Create a trust manager that does not validate certificate chains
	        final TrustManager[] trustAllCerts = new TrustManager[]{
	                new X509TrustManager() {
	                    @Override
	                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
	                                                   String authType) throws CertificateException {
	                    }

	                    @Override
	                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
	                                                   String authType) throws CertificateException {
	                    }

	                    @Override
	                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                        return new X509Certificate[0];
	                    }
	                }
	        };

	        // Install the all-trusting trust manager
	        final SSLContext sslContext = SSLContext.getInstance("SSL");
	        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
	        // Create an ssl socket factory with our all-trusting manager
	        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

	        return new OkHttpClient.Builder()
	                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
	                .hostnameVerifier(new HostnameVerifier() {
	                    @Override
	                    public boolean verify(String hostname, SSLSession session) {
	                        return true;
	                    }
	                }).build();

	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
    public static String getNHSO(String cid,String visitdate) {
    	String inscl="";
    	Connection conn;
        PreparedStatement stmt;
        ResultSet rs;
        String query="select maininscl,maininscl_main from patientnhso where cid='"+cid+"' and visitdate='"+visitdate+"'";
        try {
            conn=new DBmanager().getConnMySql();			
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
            	inscl=rs.getString(2).trim()+":"+rs.getString(1).trim();
            }
            stmt.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    	return inscl;
    }

}
