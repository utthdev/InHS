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
package org.utt.app.ipd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.UIManager;

import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.util.ComboItem;
import org.utt.app.util.I18n;
import org.utt.app.util.PrintIPDLabLabel;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.splitpane.WebSplitPane;

public class NurseFormIPD extends WebPanel implements Observer,ActionListener{
	ObjectData oUserInfo;
    int width,height;
    String formcode="",doctorname="";
    String an1="",an2="",age1="",age2="",hn1="",hn2="",name1="",name2="";
    String name="",an="",hn="",age="",bed="",memo="",cid="";
    String wardcode,department_id,ward_name_short,wardname,department,departmentName,doctor,formward;
    
    WebButton ButtonPrint;
    WebPanel LeftSection,bottomPanelForm;
    WebComboBox cbDR,cbRow1,cbRow2;
    WebButton ButtonRefreshForm;
    WebSplitPane split;
    
    public NurseFormIPD(ObjectData oUserInfo,int w,int h) {
    	width=w;
        height=h;
        this.oUserInfo=oUserInfo;
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(width-(width*2)/10, height-160));
		cbRow1 =new WebComboBox(); 
		cbRow1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		cbRow2 =new WebComboBox();
		cbRow2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		split = new WebSplitPane(split.HORIZONTAL_SPLIT);
		split.setDividerLocation(200);
		
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(200, height-160));
		split.setLeftComponent(LeftSection);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		bottomPanelForm = new WebPanel();
		bottomPanelForm.setBackground(Setup.getColor());
        LeftSection.add(bottomPanelForm, BorderLayout.SOUTH);
        bottomPanelForm.setLayout(null);
        bottomPanelForm.setPreferredSize(new Dimension((width*2)/10, 20));
        
		ButtonRefreshForm = new WebButton(I18n.lang("label.refresh"));
        ButtonRefreshForm.setBackground(Setup.getColor());
        ButtonRefreshForm.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefreshForm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	getDataForm();
            }
        });
        ButtonRefreshForm.setBounds(55, 1, 90, 20);
        bottomPanelForm.add(ButtonRefreshForm);
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData)oObservable); // cast
    }
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == ButtonPrint){			 
			PrintLabLabel();
			 
		}
    }
    public void setComboBox() {
    	wardcode= InApp.reportcode.trim();
    	getValueWard();
		setInit();
		cbDR.setSelectedIndex(0);
		getDataForm();
    }
    public void PrintLabLabel() {
    	PrintIPDLabLabel pr1= new PrintIPDLabLabel();	
		pr1.setName1(name1);
		pr1.setHN1(hn1);
		pr1.setAN1(an1);
		pr1.setAge1(age1);
		pr1.setDate(oUserInfo.GetPtVisitdate());
		pr1.setWardNameShort(ward_name_short);
		pr1.setPosition("1");
		
		pr1.setName2(name2);
		pr1.setHN2(hn2);
		pr1.setAN2(an2);
		pr1.setAge2(age2);
		
		pr1.showPrinter();
    }
    public void getValueWard(){
    	Connection conn=new DBmanager().getConnMySql();
    	PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(SetupSQL.getWard(wardcode));
			ResultSet rs = stmt.executeQuery();
	    	while (rs.next()) {	        	 
	        	wardname=rs.getString(1).trim();
		    	department=rs.getString(2).trim();
		    	department_id=rs.getString(3).trim();
		    	ward_name_short=rs.getString(4).trim();
		    	doctor=rs.getString(5).trim();
		    	formward=rs.getString(6).trim();
	        }
	        stmt.close();
	    	conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	 
    }
    public void setInit(){
    	cbRow1.removeAllItems();
    	cbRow2.removeAllItems();
    	cbDR.removeAllItems();
    	
    	String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and ward='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length>1){
			sql_user1="and ward in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
		}
		int p=0;
		Connection conn3=new DBmanager().getConnMSSql();
		PreparedStatement stmtRow;
		try {
			stmtRow = conn3.prepareStatement(SetupSQL.initWard(sql_user));
			ResultSet rsRow = stmtRow.executeQuery();
			while (rsRow.next()) {
				String sex_pt="",mar_pt="";
				String initname="",right="",age_pt="";
				String query_db=SetupSQL.getDOB(rsRow.getString(2).trim());
				PreparedStatement stmt = conn3.prepareStatement(query_db);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if(rs.getString(1) !=null){
						Date birthday=rs.getDate(1);
						age_pt=Setup.AgeInAll(birthday.toString()).trim();
					}
				}
				stmt.close();
				String name_=SetupSQL.getName(rsRow.getString(2).trim());
				cbRow1.addItem(new ComboItem("AN: "+rsRow.getString(1).trim()+"   "+name_,rsRow.getString(1).trim()+":"+name_+"*"+rsRow.getString(2).trim()+"#"+age_pt));
				cbRow2.addItem(new ComboItem("AN: "+rsRow.getString(1).trim()+"   "+name_,rsRow.getString(1).trim()+":"+name_+"*"+rsRow.getString(2).trim()+"#"+age_pt));
			}
			stmtRow.close();
			conn3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getDoctor();
    }
    public void getDataForm(){
    	
    }
    public void getDoctor() {
    	
    }
}
