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
package org.utt.app.ui;

import com.alee.laf.desktoppane.WebInternalFrame;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.util.Setup;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IFrame extends WebInternalFrame {
    public  static String [][] initname_indb=null ;
    public  static String [][] rightname=null ;
    public  static String [] hn_z515=null ;
    public  static String [][] clinicname=null ;
    public  static String [][] clinicno=null ;
    public  static String [][] doseqtycode=null ;
    public  static String [][] dosecode=null ;
    public  static String [][] dosetype=null ;
    public  static String [][] auxlabel1=null ;
    public  static String [][] doseunitcode=null;
    public  static String [][] formdrug=null ;

    public IFrame(){
        setFrameIcon (new ImageIcon(getClass().getClassLoader().getResource("images/bullet_black.png")));
        setResizable(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
        setVisible(false);
         
        initname_indb();
        rightname();
        init_z515();
        initclinic();
        initDose();
        initFormDrug();

    }
    public void initname_indb(){
        int p=0;
        PreparedStatement stmt31;
        ResultSet rs31,rs32;
        String query="select code,englishname from sysconfig where ctrlcode=?";
        Connection conn = new DBmanager().getConnMSSql();
        try {
            stmt31 = conn.prepareStatement(query);
            stmt31.setString(1, "10121");
            rs31 = stmt31.executeQuery();
            while (rs31.next()) {
                p++;
            }
            rs31.close();
            initname_indb = new String [p][2];
            rs32 = stmt31.executeQuery();
            int pp=0;
            while (rs32.next()) {
                initname_indb[pp][0]=rs32.getString(1);
                initname_indb[pp][1]=rs32.getString(2).substring(1);
                pp++;
            }
            rs32.close();
            stmt31.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void rightname(){
        int p=0;
        PreparedStatement stmt31;
        ResultSet rs31,rs32;
        String query="select code,englishname from sysconfig where ctrlcode=?";
        Connection conn = new DBmanager().getConnMSSql();
        try {
            stmt31 = conn.prepareStatement(query);
            stmt31.setString(1, "20019");
            rs31 = stmt31.executeQuery();
            while (rs31.next()) {
                p++;
            }
            rs31.close();
            rightname = new String [p][2];
            rs32 = stmt31.executeQuery();
            int pp=0;
            while (rs32.next()) {
                rightname[pp][0]=rs32.getString(1);
                rightname[pp][1]=rs32.getString(2).substring(1);
                pp++;
            }
            rs32.close();
            stmt31.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void init_z515(){
        int p=0;
        Connection conn1,conn2;
        PreparedStatement stmt1,stmt,stmt2;
        ResultSet rs1,rs,rs2;
        try {
            conn1 = new DBmanager().getConnMySql();
            String q1= SetupSQL.init_z515;
            stmt = conn1.prepareStatement(q1);
            rs = stmt.executeQuery();
            while (rs.next()) {
                p++;
            }
            rs.close();
            hn_z515 = new String [p];
            stmt1 = conn1.prepareStatement(q1);
            rs1 = stmt1.executeQuery();
            int pp=0;
            while (rs1.next()) {
                String hn="";
                if(rs1.getString(1) !=null){
                    hn=rs1.getString(1);
                }
                if(hn.equals("")){

                }else{
                    //add
                    hn_z515[pp]=hn;
                }
                pp++;
            }
            stmt1.close();
            stmt.close();
            conn1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void initclinic(){
		String queryclinic=SetupSQL.initclinic;
		try {

			Connection conn = new DBmanager().getConnMySql();
			PreparedStatement stmt1 = conn.prepareStatement(queryclinic);
			ResultSet rs1 = stmt1.executeQuery();
		    int p1=0;
			while (rs1.next()) {	
				p1++;
			}
			clinicname = new String [p1][2];
			clinicno = new String [p1][2];
			ResultSet rs11 = stmt1.executeQuery();
			int pp1=0;
			while (rs11.next()) {
				clinicname[pp1][0]=rs11.getString(1).trim();
				clinicname[pp1][1]=rs11.getString(3).trim();
				clinicno[pp1][0]=rs11.getString(1).trim();
				clinicno[pp1][1]=rs11.getString(2).trim();
				pp1++;
			}
			rs11.close();
			stmt1.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    public void initFormDrug(){
		String queryformdrug=SetupSQL.formdrug;
		try {

			Connection conn = new DBmanager().getConnMySql();
			PreparedStatement stmt1 = conn.prepareStatement(queryformdrug);
			ResultSet rs1 = stmt1.executeQuery();
		    int p1=0;
			while (rs1.next()) {	
				p1++;
			}
			formdrug = new String [p1][2];
			ResultSet rs11 = stmt1.executeQuery();
			int pp1=0;
			while (rs11.next()) {
				formdrug[pp1][0]=rs11.getString(1).trim()+rs11.getString(2).trim();
				formdrug[pp1][1]=rs11.getString(3).trim();
				pp1++;
			}
			rs11.close();
			stmt1.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void initDose(){
		int p=0,p1=0,p2=0,p3=0,p4=0;
		PreparedStatement stmt31,stmt32,stmt33,stmt34,stmt35;
		ResultSet rs31,rs32,rs33,rs34,rs35,rs36,rs37,rs38,rs39,rs40;
		String query30=SetupSQL.initDose;
		try {
			Connection conn = new DBmanager().getConnMSSql();
			stmt31 = conn.prepareStatement(query30);
			stmt31.setString(1, "20030"); 
			rs31 = stmt31.executeQuery();
			while (rs31.next()) {
				if(rs31.getString(1) !=null){
					p++;
				}
				 
			}
			rs31.close();
			auxlabel1 = new String [p][2];
			rs32 = stmt31.executeQuery();
			int pp=0;
			while (rs32.next()) {
				String a="",b="";
				if(rs32.getString(1) !=null){
					a=rs32.getString(1).trim();
				}
				if(rs32.getString(2) !=null){
					b=rs32.getString(2).trim().substring(1);
				}
				auxlabel1[pp][0]=a;
				auxlabel1[pp][1]=b;
				pp++;
			}
			rs32.close();
			stmt31.close();
			//20031
			String query31=SetupSQL.initDoseeng;
			stmt32 = conn.prepareStatement(query31);
			stmt32.setString(1, "20031"); 
			rs33 = stmt32.executeQuery();
			while (rs33.next()) {
				if(rs33.getString(1) !=null){
					p1++;
				}
			}
			rs33.close();
			dosetype = new String [p1][2];
			rs34 = stmt32.executeQuery();
			int pp1=0;
			while (rs34.next()) {
				String a="",b="";
				if(rs34.getString(1) !=null){
					a=rs34.getString(1).trim();
				}
				if(rs34.getString(2) !=null){
					b=rs34.getString(2).trim().substring(1);
				}
				dosetype[pp1][0]=a;
				dosetype[pp1][1]=b;
				pp1++;
			}
			rs34.close();
			stmt32.close();
			
			//20032
			String query32=SetupSQL.initDoseeng;
			stmt33 = conn.prepareStatement(query32);
			stmt33.setString(1, "20032"); 
			rs35 = stmt33.executeQuery();
			while (rs35.next()) {
				if(rs35.getString(1) !=null){
					p2++;
				}
			}
			rs35.close();
			dosecode = new String [p2][2];
			rs36 = stmt33.executeQuery();
			int pp2=0;
			while (rs36.next()) {
				String a="",b="";
				if(rs36.getString(1) !=null){
					a=rs36.getString(1).trim();
				}
				if(rs36.getString(2) !=null){
					b=rs36.getString(2).trim().substring(1);
				}
				dosecode[pp2][0]=a;
				dosecode[pp2][1]=b;
				pp2++;
			}
			rs36.close();
			stmt33.close();
			
			//20034
			String query34=SetupSQL.initDoseeng;
			stmt34 = conn.prepareStatement(query34);
			stmt34.setString(1, "20034"); 
			rs37 = stmt34.executeQuery();
			while (rs37.next()) {
				if(rs37.getString(1) !=null){
					p3++;
				}
			}
			rs37.close();
			doseunitcode = new String [p3][2];
			rs38 = stmt34.executeQuery();
			int pp3=0;
			while (rs38.next()) {
				String a="",b="";
				if(rs38.getString(1) !=null){
					a=rs38.getString(1).trim();
				}
				if(rs38.getString(2) !=null){
					b=rs38.getString(2).trim().substring(1);
				}
				doseunitcode[pp3][0]=a;
				doseunitcode[pp3][1]=b;
				pp3++;
			}
			rs38.close();
			stmt34.close();
			//20033
			String query35=SetupSQL.initDoseeng;
			stmt35 = conn.prepareStatement(query35);
			stmt35.setString(1, "20033"); 
			rs39 = stmt35.executeQuery();
			while (rs39.next()) {
				if(rs39.getString(1) !=null){
					p4++;
				}
			}
			rs39.close();
			doseqtycode = new String [p4][2];
			rs40 = stmt35.executeQuery();
			int pp4=0;
			while (rs40.next()) {
				String a="",b="";
				if(rs40.getString(1) !=null){
					a=rs40.getString(1).trim();
				}
				if(rs40.getString(2) !=null){
					b=rs40.getString(2).trim().substring(1);
				}
				doseqtycode[pp4][0]=a;
				doseqtycode[pp4][1]=b;
				pp4++;
			}
			rs40.close();
			stmt35.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String [][] getDoseCode(){
		
		return dosecode;
	}
	public static String [][] getQtyCode(){
		
		return doseqtycode;
	}
	public static String [][] getDoseType(){
		
		return dosetype;
	}
	public static String [][] getAuxLabel1(){
		
		return auxlabel1;
	}
	public static String GetAuxLabel1Name(String id){
		String auxlabel1name_to="";
		for(int i=0;i<auxlabel1.length;i++){
			if(id.trim().equals(auxlabel1[i][0].trim())){
				auxlabel1name_to=auxlabel1[i][1].trim();
			}
			//System.out.println(i+1+". "+depart[i][0]);
		}		
		return auxlabel1name_to;
	}
	public static String [][] getDoseUnitCode(){
		
		return doseunitcode;
	}

}
