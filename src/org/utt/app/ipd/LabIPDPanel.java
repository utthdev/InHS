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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;

public class LabIPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    String [][] labunitname=null ;
	String [][] labroomname=null ;
	WebPanel LeftSection,MainSection,mid2,mid1,MiddleSection,bottomPanelForm;
	WebSplitPane split;
    WebAccordion accordion;
    WebLabel Label_Info;
    Vector<String> columnNamesLab;
    WebTable tableLab;
    WebScrollPane scrollPaneLab;
    
    public LabIPDPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        getLabValueName();
        
        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(300, height-175));
		split.setLeftComponent(LeftSection);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		WebLabel label = Setup.getLabel(I18n.lang("label.lab"), 13, 4, SwingConstants.CENTER);
		LeftSection.add(label, BorderLayout.NORTH);
		
		bottomPanelForm = new WebPanel();
		bottomPanelForm.setBackground(Setup.getColor());
        LeftSection.add(bottomPanelForm, BorderLayout.SOUTH);
        bottomPanelForm.setLayout(null);
        bottomPanelForm.setPreferredSize(new Dimension((width*2)/10, 20));
        WebButton ButtonRefreshForm = new WebButton(I18n.lang("label.refresh"));
        ButtonRefreshForm.setBackground(Setup.getColor());
        ButtonRefreshForm.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefreshForm.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
           	 getReq();
           }
        });
        ButtonRefreshForm.setBounds(((width*2)/20-45), 1, 90, 20);
        bottomPanelForm.add(ButtonRefreshForm);
        
        MainSection = new WebPanel();
        MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-200, height));
        MainSection.setLayout(new BorderLayout(0, 0));
        split.setRightComponent(MainSection);

        MiddleSection = new WebPanel();
        MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10-450), height));

        MainSection.add(MiddleSection, BorderLayout.CENTER);
        MiddleSection.setLayout(new BorderLayout(0, 0));

        mid2 = new WebPanel();
        mid2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        mid2.setPreferredSize(new Dimension(width-((width*2)/10-480), height));
        MiddleSection.add(mid2, BorderLayout.CENTER);
        mid2.setLayout(new BorderLayout(0, 0));


        mid1 = new WebPanel();
        mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
        MiddleSection.add(mid1, BorderLayout.NORTH);
        mid1.setLayout(null);
        
        Label_Info = Setup.getLabel("", 13, 4, SwingConstants.LEFT);
        Label_Info.setBounds(20, 18, 300, 15);
        mid1.add(Label_Info);

        
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public void getReq() {
    	
    }
    public void getLabValueName(){
		int p=0;
		 
		//get unit name
		Connection conn3;
		PreparedStatement stmt31,stmt32,stmt33,stmt34;
		try {
			conn3 = new DBmanager().getConnMSSql();
			String query31="select code from sysconfig where ctrlcode='20071'";
			stmt31 = conn3.prepareStatement(query31);
			 
			ResultSet rs31 = stmt31.executeQuery();
			 
			while (rs31.next()) {
				p++;
			}
			stmt31.close();
			labunitname = new String [p][2];
			String query32="select code,englishname from sysconfig where ctrlcode='20071'";
			stmt32 = conn3.prepareStatement(query32);			 
			ResultSet rs32 = stmt32.executeQuery();
			int pp=0; 
			while (rs32.next()) {
				 labunitname[pp][0]=rs32.getString(1);
				 labunitname[pp][1]=rs32.getString(2).substring(1);
				 pp++;
			}
			stmt32.close();
			
			//get lab fac name 
			int p1=0;
			String query33="select code from sysconfig where ctrlcode='20045'";
			stmt33 = conn3.prepareStatement(query33);			 
			ResultSet rs33 = stmt33.executeQuery();
			 
			while (rs33.next()) {
				p1++;
			}
			stmt33.close();
			labroomname = new String [p1][2];
			
			String query34="select code,thainame from sysconfig where ctrlcode='20045'";
			stmt34 = conn3.prepareStatement(query34);				 			 
			ResultSet rs34 = stmt34.executeQuery();
			int pp1=0; 
			while (rs34.next()) {
				 labroomname[pp1][0]=rs34.getString(1);
				 if(rs34.getString(2) !=null){
				 labroomname[pp1][1]=rs34.getString(2).substring(1);
				 }else{
					 labroomname[pp1][1]="";
				 }
				 pp1++;
			}
			stmt34.close();
			
			conn3.close();
		}
		catch  (SQLException e) {
			e.printStackTrace();
		}
	}
    public void getDataLabClear(){
		tableLab.setModel(fetchDataLabClear());
		TableColumnModel columnModelLab = tableLab.getColumnModel();

		columnModelLab.getColumn(0).setPreferredWidth(40);
		columnModelLab.getColumn(1).setPreferredWidth(210);
		columnModelLab.getColumn(2).setPreferredWidth(150);
		columnModelLab.getColumn(3).setPreferredWidth(80);
		columnModelLab.getColumn(4).setPreferredWidth(150);
		((DefaultTableModel)tableLab.getModel()).fireTableDataChanged(); 
		
	}
    public  DefaultTableModel fetchDataLabClear( ){
		Vector<Vector<Object>> dataLab = new Vector<Vector<Object>>();
		return new DefaultTableModel(dataLab, columnNamesLab);
	}
    public void clearPanel() {
    	Label_Info.setText("");
		mid1.revalidate();
		mid1.repaint();	
		getDataLabClear();
		mid2.revalidate();
		mid2.repaint();	
		MainSection.revalidate();
		MainSection.repaint();
		if(accordion !=null) {
			LeftSection.remove(accordion);
			LeftSection.revalidate();
			LeftSection.repaint();
		}	 
	}
}