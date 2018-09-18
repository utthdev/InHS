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
package org.utt.app.opd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;

import com.alee.laf.panel.WebPanel;

public class OPDFormPanel  extends WebPanel implements Observer,ActionListener{
	ObjectData oUserInfo;
    int width,height;
    String [][] shortname=null ;
    String [][] engname=null ;
    String [][] thainame=null ;
    public OPDFormPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        initDrugName();
        
        
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public void actionPerformed(ActionEvent e) {
    	
    }
    public void initDrugName() {
		int p=0,p1=0;
		 
		//get unit name
		Connection conn3;
		PreparedStatement stmt31,stmt32,stmt33,stmt34;
		
		conn3 = new DBmanager().getConnMSSql();
		String query31="select stockcode,englishname from stock_master where englishname is not null ";
		String query32="select stockcode,shortname from stock_master where shortname is not null ";
		try {
			stmt31 = conn3.prepareStatement(query31);
			ResultSet rs31 = stmt31.executeQuery();
			 
			while (rs31.next()) {
				p++;
			}
			stmt31.close();
			engname=new String [p][2];
			stmt32 = conn3.prepareStatement(query31);			 
			ResultSet rs32 = stmt32.executeQuery();
			int pp=0; 
			while (rs32.next()) {
				 engname[pp][0]=rs32.getString(1);
				 engname[pp][1]=rs32.getString(2).substring(1);
				 pp++;
			}
			stmt32.close();
			
			stmt33 = conn3.prepareStatement(query32);
			ResultSet rs33 = stmt33.executeQuery();
			 
			while (rs33.next()) {
				p1++;
			}
			stmt33.close();
			shortname=new String [p1][2];
			stmt34 = conn3.prepareStatement(query32);			 
			ResultSet rs34 = stmt34.executeQuery();
			int pp1=0; 
			while (rs34.next()) {
				 shortname[pp1][0]=rs34.getString(1);
				 shortname[pp1][1]=rs34.getString(2);
				 pp1++;
			}
			stmt34.close();
			conn3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    public String getGFR(String hn) {
		String lab="",date="",result="";
		String query="select top 1 resultvalue,entrydatetime from labresult where hn='"+hn+"' and labcode='C73' and resultdatetime is not null order by entrydatetime desc";
		Connection conn = new DBmanager().getConnMSSql();
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();			 
			while (rs.next()) {
				if(rs.getString(1)!=null) {
					lab=rs.getString(1).trim();
					date=rs.getString(2).trim().substring(0, 10);
				}				 
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(date.equals("")) {
			result="GFR ล่าสุด  วันที่เจาะ ...............";
		}else {
			result="GFR ล่าสุด  วันที่เจาะ "+Setup.ShowThaiDateShort1(date)+"="+lab;
		}
		 
		return result;
	}

}
