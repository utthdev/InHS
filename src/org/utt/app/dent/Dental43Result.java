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
package org.utt.app.dent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.joda.time.DateTime;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;

public class Dental43Result extends WebPanel implements Observer{
	ObjectData oUserInfo;
	int width,height;
	WebPanel MainSection,MiddleSection,mid2,mid1;
	 JScrollPane scrollPaneMid2;
	 
	 
	public Dental43Result( ObjectData oUserInfo,int w, int h) {
		super();
		this.oUserInfo=oUserInfo;
		width=w;
	    height=h;
		setLayout(new BorderLayout(0, 0));

		setPreferredSize(new Dimension(width-(width*2)/10, height-175));
		
		MainSection = new WebPanel();
		MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-300, height-175));
		MainSection.setLayout(new BorderLayout(0, 0));
		
		MiddleSection = new WebPanel();
		MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10)-300, height-175));
		MainSection.add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid2 = new WebPanel();
		mid2.setPreferredSize(new Dimension(width-((width*2)/10)-400, 800));
		mid2.setLayout(new BorderLayout(0, 0));	
		scrollPaneMid2 = new JScrollPane(mid2);
		MiddleSection.add(scrollPaneMid2,BorderLayout.CENTER);
		
		setup();

		add(MainSection, BorderLayout.CENTER);
		
	}
	public void update(Observable oObservable, Object oObject) {
		
		oUserInfo = ((ObjectData)oObservable); // cast
	}
	public void setup() {
		if(oUserInfo.GetPtVisitdate().equals("")) {
			
		}else {
			String year43=Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim().substring(0, 4);
			String date_stat=year43+"-10-01";
			String date_inti="";
			 
		 /////////////////////////////////////////////////////////////change point time to check 
			
			DateTime dt21 = new DateTime(date_stat);
			DateTime dt20 = new DateTime(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim());
			//System.out.println("setting.."+dt20);
			if(dt20.isBefore(dt21)) {

				date_inti=(Integer.parseInt(year43)-1)+"-10-01";	
				//System.out.println("setting..thist"+dt21+"---"+date_inti);
				
			}else {
				 
				date_inti=year43+"-10-01";	
				//System.out.println("setting..last"+dt21+"---"+date_inti);
			}
			mid2.removeAll();
			String [] Info43_1={"hospcode","pid","seq","date_serv","denttype","servplace","pteeth","pcaries","pfilling","pextract","dteeth","dcaries","dfilling","dextract"};
			String [] Info43_2={"need_fluoride","need_scaling","need_sealant","need_pfilling","need_dfilling","need_pextract","need_dextract","nprosthesis","permanent_permanent","permanent_prosthesis","prosthesis_prosthesis","gum","schooltype","class","provider","d_update"};
			int l_Info43_1=Info43_1.length;
			int l_Info43_2=Info43_2.length;
		 
			 WebLabel[] Label43_1=new WebLabel[l_Info43_1];
			 for(int r=0;r<l_Info43_1;r++){
				 int y=5;
				// Label43_1[r] = new JLabel(Info43_1[r],JLabel.LEFT);		 
				// Label43_1[r].setBounds(10,y+(r*30),100,20);
				// mid2.add(Label43_1[r]);
			 }
			 
			 WebLabel[] Label43_2=new WebLabel[l_Info43_2];
			 for(int r=0;r<l_Info43_2;r++){
				 int y=5;
				// Label43_2[r] = new JLabel(Info43_2[r],JLabel.LEFT);
				// Label43_2[r].setBounds(350,y+(r*30),150,20);
				// mid2.add(Label43_2[r]);
			 }
		 		String sql_43 = "select *  from dental43 where  pid= '"+oUserInfo.GetPtHN()+"' and date_serv between '"+date_inti+"' and '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
				Connection conn43;
				PreparedStatement stmt43,stmt431;
				try {
						conn43 = new DBmanager().getConnMySql();	 
						stmt43 = conn43.prepareStatement(sql_43);	
						stmt431 = conn43.prepareStatement(sql_43);	 
						ResultSet rs43 = stmt43.executeQuery();
						ResultSet rs431 = stmt431.executeQuery();
			        
						while (rs43.next()) {
							 
								for(int j=0;j<l_Info43_1;j++){
								int y=5;
								 Label43_1[j] = new WebLabel(Info43_1[j]+" = "+rs43.getString(j+1),WebLabel.LEFT);		 
								 Label43_1[j].setBounds(10,y+(j*30),150,20);
								 mid2.add(Label43_1[j]);
								//System.out.println("label 1 "+rs43.getString(j+1));
								}						 
						}
						while (rs431.next()) {
							 
							for(int j=0;j<l_Info43_2;j++){
								int y=5;
								Label43_2[j] = new WebLabel(Info43_2[j]+" = "+rs431.getString(j+15),WebLabel.LEFT);		 
							 	Label43_2[j].setBounds(350,y+(j*30),200,20);
							 	mid2.add(Label43_2[j]);
							//System.out.println("label 2 "+rs431.getString(j+15));
							}						 
					}
						stmt43.close();
						stmt431.close();
						conn43.close();
					} catch (SQLException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    
			mid2.revalidate();
			mid2.repaint();
			MiddleSection.revalidate();
			MiddleSection.repaint();
		}
		 
	}

}