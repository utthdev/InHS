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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.utt.app.dent.DentalFrame;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Prop;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

public class DentalFormPanel extends WebPanel implements Observer,ActionListener{
	ObjectData oUserInfo;
	 
    int width,height;
	WebTable tableOPDform,tableExam1,tableExam2;
	Vector<String> columnNamesExam1,columnNamesExam2;
	WebSplitPane split;
	WebPanel LeftSection,MainSection,RightSection,right1,right2,Panel43,LeftNorthSection,LeftMidSection;
	Vector<String> columnNamesOPDform;
	WebPanel MiddleSection,mid2,mid1;
	JScrollPane scrollPaneOPDform,scrollPaneMid2,scrollPane43 ;
	WebButton ButtonPrintExam1,Button431,Button432,ButtonSaveExam1,ButtonSaveExam3,ButtonEdit,ButtonIn,ButtonOut,ButtonTEdit;
	int exam_status=0;
	GregorianCalendar day ;
	WebTextField TextField_EdulevelB,TextField_occ1,TextField_occ2,TextField_occ3,TextField_provider;
	WebTextField TextField_ptcc,TextField_ptdrug,TextField_ptp,TextField_ptbp;
	JRadioButton ptTypeBButton[]=null,FNButton[]=null,SNButton[]=null,ProsNButton[]=null;
	JRadioButton ptEduBButton[]=null, ptEdulevelBButton[]=null;
	ButtonGroup groupTypeB,groupEduB,groupEdulevelB,groupFN,groupSN,groupProsN;
	
	String servplace="1",dentaltype="5",schooltype="",classlevel="",gum="999999",provider="000",need_fluoride="0",need_scaling="0";
	int pteeth=0,pcaries=0,pfilling=0,pextract=0;
	int dteeth=0,dcaries=0,dfilling=0,dextract=0;
	int need_sealant=0,need_pfilling=0,need_pextract=0,need_dfilling=0,need_dextract=0,nprosthesis=0;
	int perm_perm=0,perm_pros=0,pros_pros=0;
	String [] u = new String[16];
	String [] l = new String[16];
	
	JRadioButton Pthxno,Pthxyes,Pthxno1,Pthxyes1;
	ButtonGroup Pthx = new ButtonGroup();
	ButtonGroup Ptallergy = new ButtonGroup();
	WebButton ButtonDQ;
	WebLabel Label_Info_in,Label_Info_out;
	
	public DentalFormPanel(ObjectData oUserInfo, int w, int h) {
		super();
		this.oUserInfo=oUserInfo;
		width=w;
	    height=h;
		setLayout(new BorderLayout(0, 0));

		setPreferredSize(new Dimension(width-(width*2)/10, height-175));
		split = new WebSplitPane(split.HORIZONTAL_SPLIT);
		split.setDividerLocation(200);
		day = new GregorianCalendar();
	 
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(200, height-175));
		split.setLeftComponent(LeftSection);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		LeftNorthSection = new WebPanel();
		LeftNorthSection.setPreferredSize(new Dimension(200, 80));
		LeftSection.add(LeftNorthSection, BorderLayout.NORTH);
		LeftNorthSection.setLayout(new BorderLayout(0, 0));
		LeftNorthSection.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		 
		
		LeftMidSection = new WebPanel();
		LeftMidSection.setPreferredSize(new Dimension(200, 30));
		LeftNorthSection.add(LeftMidSection, BorderLayout.CENTER);
		LeftMidSection.setLayout(null);
		
		WebLabel label_infotime = new WebLabel("บันทึกเวลาการรับริการ");
		label_infotime.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_infotime.setHorizontalAlignment(SwingConstants.CENTER);
		label_infotime.setBounds(10, 1, 200, 23);
		LeftMidSection.add(label_infotime);
		 
		 
		
		WebLabel label_info = new WebLabel("แบบฟอร์ม");
		label_info.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_info.setHorizontalAlignment(SwingConstants.CENTER);
		LeftNorthSection.add(label_info, BorderLayout.SOUTH);
		
		columnNamesOPDform = new Vector<String>();		
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		
		Vector<Vector<String>> dataOPDform = new Vector<Vector<String>>();
		DefaultTableModel modelOPDform = new DefaultTableModel(dataOPDform, columnNamesOPDform){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		tableOPDform = new WebTable(modelOPDform){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
				}
				return c;
			}
		};
		
	 	
		tableOPDform = new WebTable();
		tableOPDform.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String clinic_name="",clinic_scan="";
				int row_tableOPDform = tableOPDform.rowAtPoint(e.getPoint());
		        int col_tableOPDform = tableOPDform.columnAtPoint(e.getPoint());
		        String form_code=tableOPDform.getValueAt(row_tableOPDform,1).toString().trim();
		        String typeofprint=tableOPDform.getValueAt(row_tableOPDform,5).toString().trim();
		        String form_clinic_name=tableOPDform.getValueAt(row_tableOPDform,4).toString().trim();
		        String page=tableOPDform.getValueAt(row_tableOPDform,3).toString().trim();
		        String clinic_pt=oUserInfo.GetPtCliniccode();
		        
		        for(int i=0;i<DentalFrame.clinicname.length;i++){
					if(clinic_pt.equals(DentalFrame.clinicname[i][0])){
						clinic_name=DentalFrame.clinicname[i][1];
					}
					//System.out.println(i+1+". "+clinic_name[i][0]);
				}
				for(int i=0;i<DentalFrame.clinicno.length;i++){
					if(clinic_pt.equals(DentalFrame.clinicno[i][0])){
						clinic_scan=DentalFrame.clinicno[i][1];
					}
					//System.out.println(i+1+". "+clinic_name[i][0]);
				}
				if(form_code.equals("00107")) {
					formPanel(form_code,"","");
				}
				else {
					formPanel(form_code,page,form_clinic_name);
				}
				

				 
			}
		});
		tableOPDform.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tableOPDform.setRowHeight(25);		
		tableOPDform.setFillsViewportHeight(true);
 
		scrollPaneOPDform = new JScrollPane(tableOPDform);
		getDataForm();
		LeftSection.add(scrollPaneOPDform, BorderLayout.CENTER);
		
		MainSection = new WebPanel();
		MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-300, height-175));
		MainSection.setLayout(new BorderLayout(0, 0));
		split.setRightComponent(MainSection);
		
		MiddleSection = new WebPanel();
		MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10)-300, height-175));
		MainSection.add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid2 = new WebPanel();
		mid2.setPreferredSize(new Dimension(width-((width*2)/10)-400, 800));
		mid2.setLayout(new BorderLayout(0, 0));	
		scrollPaneMid2 = new JScrollPane(mid2);
		MiddleSection.add(scrollPaneMid2,BorderLayout.CENTER);

		mid1 = new WebPanel();
		mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		Label_Info_in = new WebLabel(" ");
		ImageIcon img_in = new ImageIcon(getClass().getClassLoader().getResource("images/home.png"));
		Label_Info_in.setIcon(img_in);
		Label_Info_in.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info_in.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info_in.setBounds(20, 18, 100, 15);
		mid1.add(Label_Info_in);
		
		Label_Info_out = new WebLabel(" ");
		ImageIcon img_out = new ImageIcon(getClass().getClassLoader().getResource("images/delivery.png"));
		Label_Info_out.setIcon(img_out);
		Label_Info_out.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info_out.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info_out.setBounds(120, 18, 200, 15);
		mid1.add(Label_Info_out);
		 
		ButtonIn = new WebButton("In");
		ButtonIn.setEnabled(true);
		
		ButtonIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {				 
				ButtonOut.setEnabled(true);
				Connection con;
				PreparedStatement stmt,stmt1;
				ResultSet rs1;
				String id="";
				String sql_hn="select id from visit where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' ";
				String sql_in = "insert into txlog (visitdate,hn,vn,txdatetime,txtype,servroom,txip,dupdate,status) values ('"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"','"+oUserInfo.GetPtHN()+"','"+oUserInfo.GetPtVN()+"','"+Setup.GetDateTimeNow()+"','1','"+oUserInfo.GetPtCliniccode().trim()+"','"+InApp.ip+"','"+Setup.GetDateTimeNow()+"','1') ";
				try {
					con = new DBmanager().getConnMySql();
					//System.out.println(id+"******************"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"----"+oUserInfo.GetPtHN()+"---****--"+oUserInfo.GetPtCliniccode()+"-----"+Setup.GetDateTimeNow());
					stmt = con.prepareStatement(sql_in);
					int rs_save =stmt.executeUpdate();
					stmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				 
				Label_Info_in.setText(getTxDatetimeIn(oUserInfo.GetPtHN()));
				Label_Info_out.setText(getTxDatetimeOut(oUserInfo.GetPtHN()));
				
				ButtonIn.setEnabled(false);
			}
			
		});
		ButtonIn.setBounds(10, 25, 50, 23);
		 
		LeftMidSection.add(ButtonIn);
		
		ButtonOut = new WebButton("Out");
		ButtonOut.setEnabled(false);
		ButtonOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ButtonIn.setEnabled(false);
				Connection con;
				PreparedStatement stmt,stmt1;
				ResultSet rs1;
				String id="";
				String sql_hn="select id from visit where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQLno(oUserInfo.GetPtVisitdate())+"' ";
				String sql_out = "insert into txlog (visitdate,hn,vn,txdatetime,txtype,servroom,txip,dupdate,status) values ('"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"','"+oUserInfo.GetPtHN()+"','"+oUserInfo.GetPtVN()+"','"+Setup.GetDateTimeNow()+"','2','"+oUserInfo.GetPtCliniccode().trim()+"','"+InApp.ip+"','"+Setup.GetDateTimeNow()+"','1') ";
				try {
					con = new DBmanager().getConnMySql();
					stmt = con.prepareStatement(sql_out);
					int rs_save =stmt.executeUpdate();
					stmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Label_Info_in.setText(getTxDatetimeIn(oUserInfo.GetPtHN()));
				Label_Info_out.setText(getTxDatetimeOut(oUserInfo.GetPtHN()));
				ButtonOut.setEnabled(false);	

			}
		});
		ButtonOut.setBounds(60, 25, 50, 23);
		LeftMidSection.add(ButtonOut);
		
		ButtonTEdit = new WebButton("Edit");
		ButtonTEdit.setEnabled(false);
		ButtonTEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ButtonIn.setEnabled(true);
				
				ButtonOut.setEnabled(true);	
				ButtonTEdit.setEnabled(false);
			}
		});
		ButtonTEdit.setBounds(120, 25, 50, 23);

		//LeftMidSection.add(ButtonTEdit);
		
		RightSection = new WebPanel();
		RightSection.setPreferredSize(new Dimension(100, height-160));
		MiddleSection.add(RightSection, BorderLayout.EAST);
		RightSection.setLayout(null);
		
		right1 = new WebPanel();
		right1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		right1.setBounds(5, 5, 90, 200);
		RightSection.add(right1);
		right1.setLayout(null);
		
		right2 = new WebPanel();
		right2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		right2.setBounds(5, 210, 90, 200);
		RightSection.add(right2);
		right2.setLayout(null);

		
		add(split, BorderLayout.CENTER);
	}
	public void update(Observable oObservable, Object oObject) {
		
		oUserInfo = ((ObjectData)oObservable); // cast
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == Button431){
			String [] input43 = new String [30];
			input43[0]="10673";
			input43[1]=oUserInfo.GetPtHN().trim();
			input43[2]="";input43[3]="";input43[4]="5";input43[5]="1";input43[6]="0";input43[7]="0";
			input43[8]="0";input43[9]="0";input43[10]="0";input43[11]="0";input43[12]="0";input43[13]="0";input43[14]="2";
			input43[15]="2";input43[16]="0";input43[17]="0";input43[18]="0";input43[19]="0";input43[20]="0";input43[21]="4";
			input43[22]="0";input43[23]="0";input43[24]="0";input43[25]="999999";input43[26]="";input43[27]="";input43[28]="000";
			input43[29]=Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate());
			
			String [] input43c = new String [30];
			input43c[0]="HOSPCODE";
			input43c[1]="PID";
			input43c[2]="SEQ";input43c[3]="DATE_SERV";input43c[4]="DENTTYPE";input43c[5]="SERVPLACE";input43c[6]="PTEETH";input43c[7]="PCARIES";
			input43c[8]="PFILLING";input43c[9]="PEXTRACT";input43c[10]="DTEETH";input43c[11]="DCARIES";input43c[12]="DFILLING";input43c[13]="DEXTRACT";input43c[14]="NEED_FLUORIDE";
			input43c[15]="NEED_SCALING";input43c[16]="NEED_SEALANT";input43c[17]="NEED_PFILLING";input43c[18]="NEED_DFILLING";input43c[19]="NEED_PEXTRACT";input43c[20]="NEED_DEXTRACT";input43c[21]="NPROSTHESIS";
			input43c[22]="PERM_PERM";input43c[23]="PERM_PROS";input43c[24]="PROS_PROS";input43c[25]="GUM";input43c[26]="SCHOOLTYPE";input43c[27]="CLASS";input43c[28]="PROVIDER";
			input43c[29]="D_UPDATE";
			
			
			int status43_insert=0;
			Connection con43;
			PreparedStatement stmt43,stmt431,stmt432,stmt44;
			ResultSet rs43,rs44 ;
			String sql_getHN43="select pid from dental43 where  pid='"+oUserInfo.GetPtHN().trim()+"' and date_serv='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
			try {
				con43 = new DBmanager().getConnMySql();				 
				String sql_getdhis="select * from dhis where  hn='"+oUserInfo.GetPtHN().trim()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
				stmt44 = con43.prepareStatement(sql_getdhis);
				rs44 = stmt44.executeQuery();
				while( rs44.next() ){
					input43[1]=rs44.getString(3).trim();
					
					String date=rs44.getString(1).trim();;
					String d= date.substring(8);
					String m= date.substring(5, 7);
					String y= date.substring(0, 4);
					
					input43[2]=rs44.getString(3).trim()+y+m+d;
					input43[3]=y+"-"+m+"-"+d;
					if(rs44.getString(6) !=null){
						input43[4]=rs44.getString(6).trim();
					}
					if(rs44.getString(5) !=null){
						input43[5]=rs44.getString(5).trim();
					}
					
					int pt=0,pc=0,pf=0,pe=0,npf=0,npe=0;
					int dt=0,dc=0,df=0,de=0,ndf=0,nde=0;
					int nsealant=0;
					for(int k=9;k<41;k++){
						if(rs44.getString(k) !=null){
						if(rs44.getString(k).trim().equals("0")){
							pt++;
						}
						else if(rs44.getString(k).trim().equals("6")){
							pc++;
							 
						}
						else if(rs44.getString(k).trim().equals("1") || rs44.getString(k).trim().equals("2")){
							npf++;
							 
						}
						else if(rs44.getString(k).trim().equals("3") || rs44.getString(k).trim().equals("5")){
							pf++;
							 
						}
						else if(rs44.getString(k).trim().equals("4") || rs44.getString(k).trim().equals("10")){
							pe++;
						}
						else if(rs44.getString(k).trim().equals("7")){
							npe++;
							 
						}
						
						else if(rs44.getString(k).trim().equals("A") || rs44.getString(k).trim().equals("a")){
							dt++;
						}
						else if(rs44.getString(k).trim().equals("G") || rs44.getString(k).trim().equals("g")){
							dc++;
							 
						}
						else if(rs44.getString(k).trim().equals("B") || rs44.getString(k).trim().equals("C") || rs44.getString(k).trim().equals("b") || rs44.getString(k).trim().equals("c")){
							ndf++;
							 
						}
						else if(rs44.getString(k).trim().equals("D") || rs44.getString(k).trim().equals("F") || rs44.getString(k).trim().equals("d") || rs44.getString(k).trim().equals("f")){
							df++;
							 
						}
						else if(rs44.getString(k).trim().equals("E") || rs44.getString(k).trim().equals("e")){
							de++;
						}
						else if(rs44.getString(k).trim().equals("H") || rs44.getString(k).trim().equals("h")){
							nde++;
							 
						}
						}
						if(k==9 || k==10 || k==11 || k==22 || k==23 || k==24 || k==25 || k==26 || k==27 || k==39 || k==40 || k==41){
							if(rs44.getString(k).trim().equals("0")){
								nsealant++;
							}
						}
					}
					 
					
					input43[6]=String.valueOf(pt+pc+pf+npf+npe);
					input43[7]=String.valueOf(pc+npf);
					input43[8]=String.valueOf(pf);
					input43[9]=String.valueOf(pe);
					
					input43[10]=String.valueOf(dt+dc+df+ndf+nde);			
					input43[11]=String.valueOf(dc+ndf);
					input43[12]=String.valueOf(df);
					input43[13]=String.valueOf(de);
					
					
					//need fluoride
					if(rs44.getString(45) !=null){
						input43[14]=rs44.getString(45).trim();
					}
					if(rs44.getString(46) !=null){
						input43[15]=rs44.getString(46).trim();	
					}
					
					String sealant="0";
					if(checkAge()==3){
						sealant=String.valueOf(nsealant);
					}
					 
					input43[16]=sealant;
					 
					input43[17]=String.valueOf(npf);
					input43[18]=String.valueOf(ndf);				
					input43[19]=String.valueOf(npe);
					input43[20]=String.valueOf(nde);
					//npros
					if(rs44.getString(47) !=null){
						input43[21]=rs44.getString(47).trim();
					}
					//occlusion
					if(rs44.getString(42) !=null){
						input43[22]=rs44.getString(42).trim();	
					}
					if(rs44.getString(43) !=null){
						input43[23]=rs44.getString(43).trim();		
					}
					if(rs44.getString(44) !=null){
						input43[24]=rs44.getString(44).trim();
					}
					//gum
					if(rs44.getString(41) !=null){
						input43[25]=rs44.getString(41).trim();
					}
					String st="",cl="";
					if(rs44.getString(7) !=null){
					if(rs44.getString(7).trim().equals("1")){
						st=rs44.getString(7).trim();
						if(rs44.getString(8) !=null){
						cl=rs44.getString(8).trim();
						}
					}
					else if(rs44.getString(7).trim().equals("2")){
						st=rs44.getString(7).trim();
						if(rs44.getString(8) !=null){
						cl=rs44.getString(8).trim();
						}
					}
					else{
						st="";
						cl="";
					}
					}
					//schooltype
					input43[26]=st;
					//class
					input43[27]=cl;
					if(rs44.getString(49) !=null){
					input43[28]=rs44.getString(49).trim();
					}
				}
				
				rs44.close();
				stmt44.close();
				
				stmt43 = con43.prepareStatement(sql_getHN43);
				//System.out.println(stmt6 );
				rs43 = stmt43.executeQuery();
				
				while( rs43.next() ){
					//System.out.println("***"+rs43.getString(1));
					//String visitdate43=rs43.getString(1).trim();
					String hn43=rs43.getString(1).trim();
					//if(visitdate43.equals(oUserInfo.GetPtVisitdate()) && hn43.equals(oUserInfo.GetPtHN().trim())){
					if(rs43.getString(1) !=null){
						//status43_insert=1;
						 
						 for(int h=0;h<input43c.length;h++){
							//System.out.println("Value input "+h+"-"+input43[h]);
							//String sql_update43 = "update dental43 set "+input43c[h]+"='"+input43[h]+"' where pid='"+oUserInfo.GetPtHN().trim()+"' ";
								
							//int rs_save431 = ((java.sql.Statement) stmt431).executeUpdate(sql_update43);
							String sql_del43 = "delete from dental43  where pid='"+oUserInfo.GetPtHN().trim()+"' and date_serv='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' ";
							stmt431 = con43.prepareStatement(sql_del43);
							int rs_save431 =stmt431.executeUpdate();
							stmt431.close();
						}
						 
						 
					}
					 
				}
				if(status43_insert==0){

					String sql_insert43 = "insert into  dental43 ( hospcode,pid,seq,date_serv,denttype,servplace,pteeth"
							+ ",pcaries,pfilling,pextract,dteeth,dcaries,dfilling,dextract,need_fluoride,need_scaling"
							+ ",need_sealant,need_pfilling,need_dfilling,need_pextract,need_dextract,nprosthesis,perm_perm"
							+ ",perm_pros,pros_pros,gum,schooltype,class,provider,d_update ) values "
							+ "('"+input43[0]+"','"+input43[1]+"','"+input43[2]+"','"+input43[3].trim()+"','"+input43[4]+"','"+input43[5]+"',"+input43[6]+","+input43[7]+","
							+ input43[8]+","+input43[9]+","+input43[10]+","+input43[11]+","+input43[12]+","+input43[13]+",'"+input43[14]+"','"+input43[15]+"',"
							+ input43[16]+","+input43[17]+","+input43[18]+","+input43[19]+","+input43[20]+",'"+input43[21]+"',"+input43[22]+","+input43[23]+","
							+ input43[24]+",'"+input43[25]+"','"+input43[26]+"','"+input43[27]+"','"+input43[28]+"','"+Setup.GetDateNow()+"')";
								
					stmt432 = con43.prepareStatement(sql_insert43);
					int rs_save432 = stmt432.executeUpdate();
					stmt432.close();
				}
				
				stmt43.close();
				con43.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(int h=0;h<input43.length;h++){
				//System.out.println("Value input "+h+"-"+input43[h]);
			}
			status43_insert=0;
		}
		else if(e.getSource() == Button432){
			String [] input43 = new String [30];
			input43[0]="10673";
			input43[1]=oUserInfo.GetPtHN().trim();
			input43[2]="";input43[3]="";input43[4]="5";input43[5]="2";input43[6]="0";input43[7]="0";
			input43[8]="0";input43[9]="0";input43[10]="0";input43[11]="0";input43[12]="0";input43[13]="0";input43[14]="2";
			input43[15]="2";input43[16]="0";input43[17]="0";input43[18]="0";input43[19]="0";input43[20]="0";input43[21]="4";
			input43[22]="0";input43[23]="0";input43[24]="0";input43[25]="999999";input43[26]="";input43[27]="";input43[28]="000";
			input43[29]=Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate());
			
			String [] input43c = new String [30];
			input43c[0]="HOSPCODE";
			input43c[1]="PID";
			input43c[2]="SEQ";input43c[3]="DATE_SERV";input43c[4]="DENTTYPE";input43c[5]="SERVPLACE";input43c[6]="PTEETH";input43c[7]="PCARIES";
			input43c[8]="PFILLING";input43c[9]="PEXTRACT";input43c[10]="DTEETH";input43c[11]="DCARIES";input43c[12]="DFILLING";input43c[13]="DEXTRACT";input43c[14]="NEED_FLUORIDE";
			input43c[15]="NEED_SCALING";input43c[16]="NEED_SEALANT";input43c[17]="NEED_PFILLING";input43c[18]="NEED_DFILLING";input43c[19]="NEED_PEXTRACT";input43c[20]="NEED_DEXTRACT";input43c[21]="NPROSTHESIS";
			input43c[22]="PERM_PERM";input43c[23]="PERM_PROS";input43c[24]="PROS_PROS";input43c[25]="GUM";input43c[26]="SCHOOLTYPE";input43c[27]="CLASS";input43c[28]="PROVIDER";
			input43c[29]="D_UPDATE";
			
			
			int status43_insert=0;
			Connection con43;
			PreparedStatement stmt43,stmt431,stmt432,stmt44;
			ResultSet rs43,rs44 ;
			String sql_getHN43="select pid from dental43 where  pid='"+oUserInfo.GetPtHN().trim()+"'  and date_serv='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
			//String sql_getHN43="select pid from dental43 where date_serv='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' and pid='"+oUserInfo.GetPtHN().trim()+"'";
			
			try {
				con43 = new DBmanager().getConnMySql();			 
				String sql_getdhis="select * from dhis where hn='"+oUserInfo.GetPtHN().trim()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
				//String sql_getdhis="select * from dhis where visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' and hn='"+oUserInfo.GetPtHN().trim()+"'";
				stmt44 = con43.prepareStatement(sql_getdhis);
				rs44 = stmt44.executeQuery();
				while( rs44.next() ){
					input43[1]=rs44.getString(3).trim();
					
					String date=rs44.getString(1).trim();;
					String d= date.substring(8);
					String m= date.substring(5, 7);
					String y= date.substring(0, 4);
					
					input43[2]=rs44.getString(3).trim()+y+m+d;
					input43[3]=y+"-"+m+"-"+d;
					if(rs44.getString(6) !=null){
						input43[4]=rs44.getString(6).trim();
					}
					if(rs44.getString(5) !=null){
						input43[5]=rs44.getString(5).trim();
					}
					
					int pt=0,pc=0,pf=0,pe=0,npf=0,npe=0;
					int dt=0,dc=0,df=0,de=0,ndf=0,nde=0;
					int nsealant=0;
					for(int k=9;k<41;k++){
						if(rs44.getString(k) !=null){
						if(rs44.getString(k).trim().equals("0")){
							pt++;
						}
						else if(rs44.getString(k).trim().equals("6")){
							pc++;
						}
						else if(rs44.getString(k).trim().equals("1") || rs44.getString(k).trim().equals("2")){
							npf++;
						}
						else if(rs44.getString(k).trim().equals("3") || rs44.getString(k).trim().equals("5")){
							pf++;
						}
						else if(rs44.getString(k).trim().equals("4") || rs44.getString(k).trim().equals("10")){
							pe++;
						}
						else if(rs44.getString(k).trim().equals("7")){
							npe++;
						}
						
						else if(rs44.getString(k).trim().equals("A") || rs44.getString(k).trim().equals("a")){
							dt++;
						}
						else if(rs44.getString(k).trim().equals("G") || rs44.getString(k).trim().equals("g")){
							dc++;
						}
						else if(rs44.getString(k).trim().equals("B") || rs44.getString(k).trim().equals("C") || rs44.getString(k).trim().equals("b") || rs44.getString(k).trim().equals("c")){
							ndf++;
						}
						else if(rs44.getString(k).trim().equals("D") || rs44.getString(k).trim().equals("F") || rs44.getString(k).trim().equals("d") || rs44.getString(k).trim().equals("f")){
							df++;
						}
						else if(rs44.getString(k).trim().equals("E") || rs44.getString(k).trim().equals("e")){
							de++;
						}
						else if(rs44.getString(k).trim().equals("H") || rs44.getString(k).trim().equals("h")){
							nde++;
						}
						}
						if(k==9 || k==10 || k==11 || k==22 || k==23 || k==24 || k==25 || k==26 || k==27 || k==39 || k==40 || k==41){
							if(rs44.getString(k).trim().equals("0")){
								nsealant++;
							}
						}
					}
					 
					
					input43[6]=String.valueOf(pt+pc+pf+npf+npe);
					input43[7]=String.valueOf(pc+npf);
					input43[8]=String.valueOf(pf);
					input43[9]=String.valueOf(pe);
					
					input43[10]=String.valueOf(dt+dc+df+ndf+nde);			
					input43[11]=String.valueOf(dc+ndf);
					input43[12]=String.valueOf(df);
					input43[13]=String.valueOf(de);
					
					
					//need fluoride
					if(rs44.getString(45) !=null){
						input43[14]=rs44.getString(45).trim();
					}
					if(rs44.getString(46) !=null){
						input43[15]=rs44.getString(46).trim();	
					}
					
					String sealant="0";
					if(checkAge()==3){
						sealant=String.valueOf(nsealant);
					}
					 
					input43[16]=sealant;
					 
					input43[17]=String.valueOf(npf);
					input43[18]=String.valueOf(ndf);				
					input43[19]=String.valueOf(npe);
					input43[20]=String.valueOf(nde);
					//npros
					if(rs44.getString(47) !=null){
						input43[21]=rs44.getString(47).trim();
					}
					//occlusion
					if(rs44.getString(42) !=null){
						input43[22]=rs44.getString(42).trim();	
					}
					if(rs44.getString(43) !=null){
						input43[23]=rs44.getString(43).trim();		
					}
					if(rs44.getString(44) !=null){
						input43[24]=rs44.getString(44).trim();
					}
					//gum
					if(rs44.getString(41) !=null){
						input43[25]=rs44.getString(41).trim();
					}
					String st="",cl="";
					if(rs44.getString(7) !=null){
					if(rs44.getString(7).trim().equals("1")){
						st=rs44.getString(7).trim();
						if(rs44.getString(8) !=null){
						cl=rs44.getString(8).trim();
						}
					}
					else if(rs44.getString(7).trim().equals("2")){
						st=rs44.getString(7).trim();
						if(rs44.getString(8) !=null){
						cl=rs44.getString(8).trim();
						}
					}
					else{
						st="";
						cl="";
					}
					}
					//schooltype
					input43[26]=st;
					//class
					input43[27]=cl;
					if(rs44.getString(49) !=null){
					input43[28]=rs44.getString(49).trim();
					}
				}
				
				rs44.close();
				stmt44.close();
				
				stmt43 = con43.prepareStatement(sql_getHN43);
				//System.out.println(stmt6 );
				rs43 = stmt43.executeQuery();
				
				while( rs43.next() ){
					//String visitdate43=rs43.getString(1).trim();
					//String hn43=rs43.getString(2).trim();
					//if(visitdate43.equals(oUserInfo.GetPtVisitdate()) && hn43.equals(oUserInfo.GetPtHN().trim())){
					if(rs43.getString(1) !=null){
						//status43_insert=1;
						 
						 for(int h=0;h<input43c.length;h++){
							//System.out.println("Value input "+h+"-"+input43[h]);
							//String sql_update43 = "update dental43 set "+input43c[h]+"='"+input43[h]+"' where pid='"+oUserInfo.GetPtHN().trim()+"' ";
								
							//int rs_save431 = ((java.sql.Statement) stmt431).executeUpdate(sql_update43);
							String sql_del43 = "delete from dental43  where pid='"+oUserInfo.GetPtHN().trim()+"'  and date_serv='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
							stmt431 = con43.prepareStatement(sql_del43);
							int rs_save431 = stmt431.executeUpdate();
							stmt431.close();
						}
						 
						 
					}
					 
				}
				if(status43_insert==0){
					 
					String sql_insert43 = "insert into  dental43 ( hospcode,pid,seq,date_serv,denttype,servplace,pteeth"
							+ ",pcaries,pfilling,pextract,dteeth,dcaries,dfilling,dextract,need_fluoride,need_scaling"
							+ ",need_sealant,need_pfilling,need_dfilling,need_pextract,need_dextract,nprosthesis,perm_perm"
							+ ",perm_pros,pros_pros,gum,schooltype,class,provider,d_update ) values "
							+ "('"+input43[0]+"','"+input43[1]+"','"+input43[2]+"','"+input43[3].trim()+"','"+input43[4]+"','"+input43[5]+"',"+input43[6]+","+input43[7]+","
							+ input43[8]+","+input43[9]+","+input43[10]+","+input43[11]+","+input43[12]+","+input43[13]+",'"+input43[14]+"','"+input43[15]+"',"
							+ input43[16]+","+input43[17]+","+input43[18]+","+input43[19]+","+input43[20]+",'"+input43[21]+"',"+input43[22]+","+input43[23]+","
							+ input43[24]+",'"+input43[25]+"','"+input43[26]+"','"+input43[27]+"','"+input43[28]+"','"+Setup.GetDateNow()+"')";
								
					stmt432 = con43.prepareStatement(sql_insert43);
					int rs_save432 = stmt432.executeUpdate(sql_insert43);
					stmt432.close();
				}
				
				stmt43.close();
				con43.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(int h=0;h<input43.length;h++){
				//System.out.println("Value input "+h+"-"+input43[h]);
			}
			status43_insert=0;
		}
		else if(e.getSource() == ButtonSaveExam1){
			if(TextField_EdulevelB.getText().trim().equals("")){
    			classlevel="";
    		}else{
    			 
    			classlevel=TextField_EdulevelB.getText().trim();
    		}
			//update data
    		Connection con611;
    		PreparedStatement stmt611,stmt612,stmt613;
    		try {
				con611 = new DBmanager().getConnMySql();			
    			String sql_updatepart1 = "update dhis set   vn='"+oUserInfo.GetPtVN()+"', cid='"+oUserInfo.GetPtCID()+"', typeserv='"+servplace+"', typept='"+dentaltype+"', edu='"+schooltype+"', edulevel='"+classlevel+"', d_update='"+Setup.GetDateNow()+"' where hn='"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
    			stmt611 = con611.prepareStatement(sql_updatepart1);
    			int rs_update = stmt611.executeUpdate();
    			stmt611.close();
    			
    			if(!checkValueTable()){
        			WebOptionPane.showMessageDialog(this,"ค่าที่กรอกไม่ถูกต้อง !!","Value Error",WebOptionPane.ERROR_MESSAGE);
     
        		}else{
        			for(int m=0;m<16;m++){
            			u[(m)]=tableExam1.getValueAt(0,m).toString().trim();           			             			
            			String sql_updateU = "update dhis set  u"+m+"='"+u[m]+"' where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
            			stmt612 = con611.prepareStatement(sql_updateU);
            			int rs_updateU = stmt612.executeUpdate();
            			stmt612.close();
            			//System.out.println("u"+(m)+".."+u[(m)]);
            		}
            		for(int m=0;m<16;m++){
            			l[(m)]=tableExam1.getValueAt(1,m).toString().trim();           			
            			String sql_updateL = "update dhis set  l"+m+"='"+l[m]+"' where hn='"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' ";
            			stmt613 = con611.prepareStatement(sql_updateL);
            			int rs_updateU = stmt613.executeUpdate();
            			stmt613.close();
            		//	System.out.println("l"+(m)+".."+l[(m)]);
            		}
        		}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		clearDataPt43_1();   
    		///section2
    		if(TextField_occ1.getText().trim().equals("")){
    			perm_perm=0;
    		}else{
    			perm_perm= Integer.parseInt( TextField_occ1.getText().trim());
    		}
    		if(TextField_occ2.getText().trim().equals("")){
    			perm_pros=0;
    		}else{
    			perm_pros= Integer.parseInt( TextField_occ2.getText().trim());
    		}
    		if(TextField_occ3.getText().trim().equals("")){
    			pros_pros=0;
    		}else{
    			pros_pros= Integer.parseInt( TextField_occ3.getText().trim());
    		}
    		 
    		
    		if(TextField_provider.getText().trim().equals("")){
    			provider="047";
    		}else{
    			provider= TextField_provider.getText().trim();
    		}
    		
    		//perio
    		gum="999999";
    		if(!checkValueTableGum()){
    			WebOptionPane.showMessageDialog(this,"ค่าที่กรอกไม่ถูกต้อง !!","Value Error",WebOptionPane.ERROR_MESSAGE);
 
    		}else{
    			String ur="9",ua="9",ul="9",lr="9",la="9",ll="9";
    			ur=tableExam2.getValueAt(0,0).toString().trim();
    			ua=tableExam2.getValueAt(0,1).toString().trim();
    			ul=tableExam2.getValueAt(0,2).toString().trim();
    			lr=tableExam2.getValueAt(1,2).toString().trim();
    			la=tableExam2.getValueAt(1,1).toString().trim();
    			ll=tableExam2.getValueAt(1,0).toString().trim();
    			gum=ur+ua+ul+ll+la+lr;
    			
    			//update data
        		Connection con615;
        		PreparedStatement stmt615;
        		try {
					con615 = new DBmanager().getConnMySql();       			       			
        			String sql_updatepart2 = "update dhis set  perio='"+gum+"', occpetope='"+perm_perm+"', occpetopr='"+perm_pros+"', occprtopr='"+pros_pros+"', fluneed='"+need_fluoride+"', sneed='"+need_scaling+"', prosneed='"+nprosthesis+"', provider='"+provider+"' where hn='"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' ";
        			stmt615 = con615.prepareStatement(sql_updatepart2);
        			int rs_update = stmt615.executeUpdate();
        			stmt615.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    		clearDataPt43_2();   

		}
		else if(e.getSource() == ButtonSaveExam3){
			if(TextField_EdulevelB.getText().trim().equals("")){
    			classlevel="";
    		}else{
    			 
    			classlevel=TextField_EdulevelB.getText().trim();
    		}
			//update data
    		Connection con611;
    		PreparedStatement stmt611,stmt612,stmt613;
    		try {
				con611 = new DBmanager().getConnMySql();   			   			
    			String sql_updatepart1 = "update dhis set   vn='0000', cid='"+oUserInfo.GetPtCID()+"', typeserv='2', typept='"+dentaltype+"', edu='"+schooltype+"', edulevel='"+classlevel+"', d_update='"+Setup.GetDateNow()+"' where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
    			stmt611 = con611.prepareStatement(sql_updatepart1);
    			int rs_update = stmt611.executeUpdate();
    			stmt611.close();
    			
    			if(!checkValueTable()){
        			JOptionPane.showMessageDialog(this,"ค่าที่กรอกไม่ถูกต้อง !!","Value Error",JOptionPane.ERROR_MESSAGE);
     
        		}else{
        			for(int m=0;m<16;m++){
            			u[(m)]=tableExam1.getValueAt(0,m).toString().trim();            			            			
            			String sql_updateU = "update dhis set  u"+m+"='"+u[m]+"' where hn='"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' ";
            			stmt612 = con611.prepareStatement(sql_updateU);
            			int rs_updateU = stmt612.executeUpdate();
            			stmt612.close();
            			//System.out.println("u"+(m)+".."+u[(m)]);
            		}
            		for(int m=0;m<16;m++){
            			l[(m)]=tableExam1.getValueAt(1,m).toString().trim();
            			 
            			
            			String sql_updateL = "update dhis set  l"+m+"='"+l[m]+"' where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
            			stmt613 = con611.prepareStatement(sql_updateL);
            			int rs_updateU = stmt613.executeUpdate();
            			stmt613.close();
            		//	System.out.println("l"+(m)+".."+l[(m)]);
            		}
        		}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		clearDataPt43_1();
			if(TextField_occ1.getText().trim().equals("")){
    			perm_perm=0;
    		}else{
    			perm_perm= Integer.parseInt( TextField_occ1.getText().trim());
    		}
    		if(TextField_occ2.getText().trim().equals("")){
    			perm_pros=0;
    		}else{
    			perm_pros= Integer.parseInt( TextField_occ2.getText().trim());
    		}
    		if(TextField_occ3.getText().trim().equals("")){
    			pros_pros=0;
    		}else{
    			pros_pros= Integer.parseInt( TextField_occ3.getText().trim());
    		}
    		 
    		
    		if(TextField_provider.getText().trim().equals("")){
    			provider="047";
    		}else{
    			provider= TextField_provider.getText().trim();
    		}
    		
    		//perio
    		gum="999999";
    		if(!checkValueTableGum()){
    			JOptionPane.showMessageDialog(this,"ค่าที่กรอกไม่ถูกต้อง !!","Value Error",JOptionPane.ERROR_MESSAGE);
 
    		}else{
    			String ur="9",ua="9",ul="9",lr="9",la="9",ll="9";
    			ur=tableExam2.getValueAt(0,0).toString().trim();
    			ua=tableExam2.getValueAt(0,1).toString().trim();
    			ul=tableExam2.getValueAt(0,2).toString().trim();
    			lr=tableExam2.getValueAt(1,2).toString().trim();
    			la=tableExam2.getValueAt(1,1).toString().trim();
    			ll=tableExam2.getValueAt(1,0).toString().trim();
    			gum=ur+ua+ul+ll+la+lr;
    			
    			//update data
        		Connection con615;
        		PreparedStatement stmt615;
        		try {
					con615 = new DBmanager().getConnMySql();       			         			
        			String sql_updatepart2 = "update dhis set  perio='"+gum+"', occpetope='"+perm_perm+"', occpetopr='"+perm_pros+"', occprtopr='"+pros_pros+"', fluneed='"+need_fluoride+"', sneed='"+need_scaling+"', prosneed='"+nprosthesis+"', provider='"+provider+"' where hn='"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
        			stmt615 = con615.prepareStatement(sql_updatepart2);
        			int rs_update = stmt615.executeUpdate();
        			stmt615.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    		clearDataPt43_2();
		}
		else if(e.getSource()instanceof JRadioButton){
			if(e.getActionCommand().trim().equals("ผู้สูงอายุ")){
	    		
    			dentaltype="4";		
    		}
			else if(e.getActionCommand().trim().equals("กลุ่มอื่นๆ")){
	    		
    			dentaltype="5";
    		}
    		else if(e.getActionCommand().trim().equals("เด็กวัยเรียน")){
        		
    			dentaltype="3";
    		}
    		else if(e.getActionCommand().trim().equals("เด็กก่อนวัยเรียน")){
        		
    			dentaltype="2";
    		}
    		else if(e.getActionCommand().trim().equals("หญิงตั้งครรภ์")){
        		
    			dentaltype="1";
    		}
    		else if(e.getActionCommand().trim().equals("ศพด.")){   	    		
    			schooltype="1";		
    		}
    		else if(e.getActionCommand().trim().equals("ประถม-รัฐบาล")){   	    		
				schooltype="2";		
    		}
			else if(e.getActionCommand().trim().equals("ประถม-เทศบาล")){   	    		
				schooltype="3";		
    		}
			else if(e.getActionCommand().trim().equals("ประถม-ท้องถิ่น")){   	    		
				schooltype="4";		
    		}
			else if(e.getActionCommand().trim().equals("ประถม-เอกชน")){   	    		
				schooltype="5";		
    		}
			else if(e.getActionCommand().trim().equals("มัธยม-รัฐบาล")){   	    		
				schooltype="6";		
    		}
			else if(e.getActionCommand().trim().equals("มัธยม-เทศบาล")){   	    		
				schooltype="7";		
    		}
			else if(e.getActionCommand().trim().equals("มัธยม-ท้องถิ่น")){   	    		
				schooltype="8";		
    		}
			else if(e.getActionCommand().trim().equals("มัธยม-เอกชน")){   	    		
				schooltype="9";		
    		}
			else if(e.getActionCommand().trim().equals("ไม่เลือก")){   	    		
				schooltype="0";		
    		}
			else if(e.getActionCommand().trim().equals("ต้องใส่ฟันบนและล่าง")){   
				nprosthesis=1;
			}
			else if(e.getActionCommand().trim().equals("ต้องใส่ฟันบน")){   
				nprosthesis=2;
			}
			else if(e.getActionCommand().trim().equals("ต้องใส่ฟันล่าง")){   
				nprosthesis=3;
			}
			else if(e.getActionCommand().trim().equals("ไม่ต้องใส่ฟันเทียม")){   
				nprosthesis=4;
			}
			else if(e.getActionCommand().trim().equals("จำเป็นs")){   
				need_scaling="1";
			}
			else if(e.getActionCommand().trim().equals("ไม่จำเป็นs")){   
				need_scaling="2";
			}
			else if(e.getActionCommand().trim().equals("จำเป็นf")){   
				need_fluoride="1";
			}
			else if(e.getActionCommand().trim().equals("ไม่จำเป็นf")){   
				need_fluoride="2";
			}
		}
		 
	}
	public void getDataForm(){
		tableOPDform.setModel(fetchDataForm());
		TableColumnModel columnModelOPDform = tableOPDform.getColumnModel();
		columnModelOPDform.getColumn(0).setPreferredWidth(40);
		columnModelOPDform.getColumn(1).setPreferredWidth(0);
		columnModelOPDform.getColumn(1).setMinWidth(0);
		columnModelOPDform.getColumn(1).setMaxWidth(0);
		columnModelOPDform.getColumn(2).setPreferredWidth(360);
		for (int n = 2; n < 5; n++) {	
			columnModelOPDform.getColumn(n+1).setPreferredWidth(0);
			columnModelOPDform.getColumn(n+1).setMinWidth(0);
			columnModelOPDform.getColumn(n+1).setMaxWidth(0);
		}

		((DefaultTableModel)tableOPDform.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataForm(){
		/*
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and clinic='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length==0){
			sql_user="";
		}
		else if(parts.length>1){
			sql_user1="and clinic in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
		}
		*/
		String formopd="";
		Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		
		String query =  "select report_code from inaccount where  status='1'  and username='"+InApp.username+"'" ;
		
		Connection confw=new DBmanager().getConnMySql();
		PreparedStatement stmtfw;
		try {
			stmtfw = confw.prepareStatement(query);
			ResultSet rsfw = stmtfw.executeQuery();
			while (rsfw.next()) {
				formopd=rsfw.getString(1).trim();
			}
			stmtfw.close();
			confw.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		 
		String[] report=null;
		report = formopd.split("\\,");
		
		Vector<Vector<String>> dataForm = new Vector<Vector<String>>();
		
		for(int i=0;i<report.length;i++){
			String sql_formopd = "select form_code,form_name,page,form_name_print,typeofprint  from formopd where type='1' and form_code='"+report[i].trim()+"'  order by clinic,orderpage";
			//System.out.println(sql_formopd);
			try {
				conn=new DBmanager().getConnMySql();
				stmt = conn.prepareStatement(sql_formopd);
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					final Vector<String> vstringForm = new Vector<String>();
					vstringForm.add(" "+Integer.toString(i+1) );
					vstringForm.add(rs.getString(1).trim());	           
		        	vstringForm.add(rs.getString(2));
		        	if(rs.getString(3) ==null){
		        		vstringForm.add("88");
		        	}else{
		        	vstringForm.add(rs.getString(3).trim());
		        	}
		        	if(rs.getString(4) ==null){
		        		vstringForm.add("");
		        	}else{
		        	vstringForm.add(rs.getString(4).trim());
		        	}
		        	if(rs.getString(5) ==null){
		        		vstringForm.add("1");
		        	}else{
		        	vstringForm.add(rs.getString(5).trim());
		        	}
		        	//System.out.println(rs.getString(1).trim()+""+rs.getString(2).trim()+"--"+rs.getString(5).trim());
					dataForm.add(vstringForm);
				
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		 
		 
		return new DefaultTableModel(dataForm, columnNamesOPDform);
	}
	//form panel
	public void formPanel(String informcode,String page,String form_clinic_name){
		clearPanel();
		String formcode=informcode.trim();
		if(formcode.equals("00115") || formcode.equals("00117")){
			if(oUserInfo.GetPtHN().length() !=7){
				WebOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",WebOptionPane.ERROR_MESSAGE);

			}else{
				checkExam(oUserInfo.GetPtHN());
				//String runno=getQ(oUserInfo.GetPtCliniccode(),oUserInfo.GetPtVN(),oUserInfo.GetPtHN());
				/*
				Panel43 = new WebPanel();
				Panel43.setPreferredSize(new Dimension((width*2)/10, 800));
				Panel43.setLayout(null);
				
				WebLabel ptcc=new WebLabel("CC ",WebLabel.CENTER);
				ptcc.setBounds(5,5,100,25);
				ptcc.setFont(new Font("Tahoma", Font.PLAIN, 14));
				Panel43.add(ptcc);
				
				TextField_ptcc = new WebTextField();
				TextField_ptcc.setFont(new Font("Tahoma", Font.PLAIN, 14));
				TextField_ptcc.setBounds(100,5,600,25);
				TextField_ptcc.setText("");
				Panel43.add(TextField_ptcc);
				
				WebLabel ptbp=new WebLabel("BP ",WebLabel.CENTER);
				ptbp.setBounds(5,35,100,25);
				ptbp.setFont(new Font("Tahoma", Font.PLAIN, 14));
				Panel43.add(ptbp);
				
				TextField_ptbp = new WebTextField();
				TextField_ptbp.setFont(new Font("Tahoma", Font.PLAIN, 14));
				TextField_ptbp.setBounds(100,35,80,25);
				TextField_ptbp.setText("");
				Panel43.add(TextField_ptbp);
				
				WebLabel ptp=new WebLabel("P ",WebLabel.CENTER);
				ptp.setBounds(150,35,50,25);
				ptp.setFont(new Font("Tahoma", Font.PLAIN, 14));
				Panel43.add(ptp);
				
				TextField_ptp = new WebTextField();
				TextField_ptp.setFont(new Font("Tahoma", Font.PLAIN, 14));
				TextField_ptp.setBounds(200,35,80,25);
				TextField_ptp.setText("");
				Panel43.add(TextField_ptp);
				
				WebLabel pthx=new WebLabel("Hx ",WebLabel.CENTER);
				pthx.setBounds(5,65,100,25);
				pthx.setFont(new Font("Tahoma", Font.PLAIN, 14));
				Panel43.add(pthx);
				
				Pthxno = new JRadioButton("No");
				Pthxno.setBackground(Setup.getColor());
				Pthxno.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 
					}
				});
				Pthx.add(Pthxno);
				Pthxno.setBounds(100, 65, 40, 25);
				
				Panel43.add(Pthxno);
				
				Pthxyes = new JRadioButton("Yes");
				Pthxyes.setBackground(Setup.getColor());
				Pthxyes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
					}
				});
				Pthx.add(Pthxyes);
				Pthxyes.setBounds(145, 65, 50, 25);
				Panel43.add(Pthxyes);
				
			
				
				WebLabel ptdrug=new WebLabel("Drug used ",WebLabel.CENTER);
				ptdrug.setBounds(5,95,100,25);
				ptdrug.setFont(new Font("Tahoma", Font.PLAIN, 14));
				Panel43.add(ptdrug);
				
				TextField_ptdrug = new WebTextField();
				TextField_ptdrug.setFont(new Font("Tahoma", Font.PLAIN, 14));
				TextField_ptdrug.setBounds(100,95,600,25);
				TextField_ptdrug.setText("");
				Panel43.add(TextField_ptdrug);
				
				
				WebLabel ptallergy=new WebLabel("Allergy ",WebLabel.CENTER);
				ptallergy.setBounds(5,125,100,25);
				ptallergy.setFont(new Font("Tahoma", Font.PLAIN, 14));
				Panel43.add(ptallergy);
				
				Pthxno1 = new JRadioButton("No");
				Pthxno1.setBackground(Setup.getColor());
				Pthxno1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 
					}
				});
				Ptallergy.add(Pthxno1);
				Pthxno1.setBounds(100, 125, 40, 25);
				
				Panel43.add(Pthxno1);
				
				Pthxyes1 = new JRadioButton("Yes");
				Pthxyes1.setBackground(Setup.getColor());
				Pthxyes1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
					}
				});
				Ptallergy.add(Pthxyes1);
				Pthxyes1.setBounds(145, 125, 50, 25);
				Panel43.add(Pthxyes1);
				
				mid2.add(Panel43, BorderLayout.CENTER);
				*/
				
				mid2.revalidate();
				mid2.repaint();	
				MiddleSection.revalidate();
				MiddleSection.repaint();
				
				//right Section
				//Button Print
				ButtonPrintExam1 = new WebButton("Print");		 
				ButtonPrintExam1.setBounds(5,10,80,30);		 
				ButtonPrintExam1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						PrintOPD(formcode,"2",page,form_clinic_name);
					}
				});
				
				right1.add(ButtonPrintExam1);
				
				right1.revalidate();
				right1.repaint();
				
				MiddleSection.revalidate();
				MiddleSection.repaint();
				RightSection.revalidate();
				RightSection.repaint();
			}
		
		}
		else if(formcode.equals("01005")){
			PrintOPD(formcode,"1",page,form_clinic_name);
		}
		else if(formcode.equals("00116")){
			clearPanel();
			exam_status=0;
			//System.out.println(oUserInfo.GetPtHN());
			if(oUserInfo.GetPtHN().length() !=7){
				WebOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",WebOptionPane.ERROR_MESSAGE);

			}else{
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
				
				checkExam(oUserInfo.GetPtHN());
				
				Panel43 = new WebPanel();
				Panel43.setPreferredSize(new Dimension((width*2)/10, 800));
				Panel43.setLayout(null);
				
				WebLabel ptType,ptEdu,ptEdulevel,ptExam1,ptExam2,ptExam3,ptExam4,ptLabelExam1;
				String [] ptTypeB={"หญิงตั้งครรภ์","เด็กก่อนวัยเรียน","เด็กวัยเรียน","ผู้สูงอายุ","กลุ่มอื่นๆ"};
				String [] ptEduB={"ศพด.","ประถม-รัฐบาล","ประถม-เทศบาล","ประถม-ท้องถิ่น","ประถม-เอกชน","มัธยม-รัฐบาล","มัธยม-เทศบาล","มัธยม-ท้องถิ่น","มัธยม-เอกชน","ไม่เลือก"};
				
				ptType=new WebLabel("ประเภท ",WebLabel.LEFT);
				ptType.setBounds(5,1,150,20);
				ptType.setFont(new Font("Tahoma", Font.PLAIN, 13));
				Panel43.add(ptType);
				int l_ptTypeB=ptTypeB.length;
				ptTypeBButton=new JRadioButton [l_ptTypeB];
				groupTypeB = new ButtonGroup();
				for(int row=0;row<l_ptTypeB;row++){
					int x=30;
					ptTypeBButton[row] = new JRadioButton(ptTypeB[row]);
					ptTypeBButton[row].setBounds(x+(row*130),20,120,20);
					ptTypeBButton[row].setBackground(new Color(207,229,233));
					ptTypeBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptTypeBButton[row].setActionCommand( ptTypeBButton[row].getText() );
					ptTypeBButton[row].addActionListener(this); 
					Panel43.add(ptTypeBButton[row]);
					groupTypeB.add(ptTypeBButton[row]);
				}
				//
				ptEdu=new WebLabel("สถานศึกษา (กรณีเด็กวัยเรียน) ",WebLabel.LEFT);
				ptEdu.setBounds(5,45,200,20);
				ptEdu.setFont(new Font("Tahoma", Font.PLAIN, 13));
				Panel43.add(ptEdu);
				
				int l_ptEduB=ptEduB.length;
				ptEduBButton=new JRadioButton [l_ptEduB];
				groupEduB = new ButtonGroup();
				for(int row=0;row<10;row++){
					if(row<5){
					int x=30;
					 
					ptEduBButton[row] = new JRadioButton(ptEduB[row]);
					ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
					ptEduBButton[row].addActionListener(this);
					ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptEduBButton[row].setBounds(x+(row*130),70,120,20);
					ptEduBButton[row].setBackground(new Color(207,229,233));
					if(checkAge()==3){
						ptEduBButton[row].setEnabled(true);
					}else{
						ptEduBButton[row].setEnabled(false);
					}
					Panel43.add(ptEduBButton[row]);
					groupEduB.add(ptEduBButton[row]);
					}else if(row>4 && row<10){
						int x=30;
						 
						ptEduBButton[row] = new JRadioButton(ptEduB[row]);
						ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
						ptEduBButton[row].addActionListener(this);
						ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptEduBButton[row].setBounds(x+((row-5)*130),100,120,20);
						ptEduBButton[row].setBackground(new Color(207,229,233));
						if(checkAge()==3){
							ptEduBButton[row].setEnabled(true);
						}else{
							ptEduBButton[row].setEnabled(false);
						}
						Panel43.add(ptEduBButton[row]);
						groupEduB.add(ptEduBButton[row]);
					}
				}
				//
				ptEdulevel=new WebLabel("ระดับการศึกษา (กรณีเด็กวัยเรียน) ",WebLabel.LEFT);
				ptEdulevel.setFont(new Font("Tahoma", Font.PLAIN, 13));
				ptEdulevel.setBounds(5,140,250,20);				
				Panel43.add(ptEdulevel);
								 
				Border textfield_b = new SoftBevelBorder(BevelBorder.LOWERED);
				TextField_EdulevelB = new WebTextField();
				TextField_EdulevelB.setFont(new Font("Tahoma", Font.PLAIN, 13));
				TextField_EdulevelB.setBorder(textfield_b);
				TextField_EdulevelB.setBounds(200,140,50,20);
				TextField_EdulevelB.setText("");
				if(checkAge()==3){
					TextField_EdulevelB.setEnabled(true);
				}else{
					TextField_EdulevelB.setEnabled(false);
				}
				TextField_EdulevelB.addActionListener(this);
				Panel43.add(TextField_EdulevelB);
				//
				ptLabelExam1=new WebLabel("",WebLabel.LEFT);
				ptLabelExam1.setFont(new Font("Tahoma", Font.PLAIN, 13));
				ptLabelExam1.setBounds(210,180,200,20);
				Panel43.add(ptLabelExam1);
				//
				ptExam1=new WebLabel("1.การตรวจในช่องปาก ",WebLabel.LEFT);
				ptExam1.setFont(new Font("Tahoma", Font.PLAIN, 13));
				ptExam1.setBounds(5,180,200,20);
				Panel43.add(ptExam1);
				
				columnNamesExam1 = new Vector<String>();
				
				for(int c=0;c<16;c++){
					columnNamesExam1.add("");
				}
				
				Vector<Vector<String>> dataExam1 = new Vector<Vector<String>>();
				final Vector<String> dataExam = new Vector<String>();
				final Vector<String> dataExam2 = new Vector<String>();
				
				//String sql_432 = "select * from dhis where  hn= '"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
				
				String sql_432 = "select * from dhis where  hn= '"+oUserInfo.GetPtHN()+"' and visitdate between '"+date_inti+"' and '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' order by visitdate desc limit 1";
				//String sql_432 = "select * from dhis where  hn= '"+hn+"' order by visitdate desc limit 1";
				 

				Connection conn43;
				PreparedStatement stmt43,stmt433;
				try {
					conn43 = new DBmanager().getConnMySql(); 
					stmt43 = conn43.prepareStatement(sql_432);
					 
					ResultSet rs43 = stmt43.executeQuery();
				 
					while (rs43.next()) {
						if(rs43.getString(3) !=null) {
							ptLabelExam1.setText("วันที่พิมพ์  "+Setup.ShowThaiDateShort1(rs43.getString(1)));
						}
						if(rs43.getString(6) ==null){
							//ptTypeBButton[4].setSelected(true);
						}
						else if(rs43.getString(6).trim().equals("1")){
							ptTypeBButton[0].setSelected(true);
							
						}
						else if(rs43.getString(6).trim().equals("2")){
							ptTypeBButton[1].setSelected(true);
							
						}
						else if(rs43.getString(6).trim().equals("3")){
							ptTypeBButton[2].setSelected(true);
							
						}
						else if(rs43.getString(6).trim().equals("4")){
							ptTypeBButton[3].setSelected(true);
							
						}
						else if(rs43.getString(6).trim().equals("5")){
							ptTypeBButton[4].setSelected(true);
							
						}
						if(rs43.getString(7) ==null || rs43.getString(7).trim().equals("0")){
							 
						}
						else if(rs43.getString(7).trim().equals("1")){
							ptEduBButton[0].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("2")){
							ptEduBButton[1].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("3")){
							ptEduBButton[2].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("4")){
							ptEduBButton[3].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("5")){
							ptEduBButton[4].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("6")){
							ptEduBButton[5].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("7")){
							ptEduBButton[6].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("8")){
							ptEduBButton[7].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("9")){
							ptEduBButton[8].setSelected(true);
						}
				
						if(rs43.getString(8) ==null || rs43.getString(8).trim().equals("0")){
							TextField_EdulevelB.setText("");
						}
						else  if(rs43.getString(8) !=null){
							TextField_EdulevelB.setText(rs43.getString(8).trim());
						}
							
							
						for(int r=9;r<25;r++){
							//System.out.println(rs43.getString(r));
							if(rs43.getString(r) ==null){
								dataExam.add("");
							}							
							else if(rs43.getString(r) !=null){
								dataExam.add(rs43.getString(r).trim());
								//System.out.println(rs43.getString(r).trim());
							}							 
						}
						dataExam1.add(dataExam); 					 					 
					}
					stmt43.close(); 
					stmt433 = conn43.prepareStatement(sql_432);
					ResultSet rs433 = stmt433.executeQuery();
					while (rs433.next()) {						
						for(int r=25;r<41;r++){
							if(rs433.getString(r) ==null){
								dataExam2.add("");
							}
							else if(rs433.getString(r) !=null){
								dataExam2.add(rs433.getString(r).trim());
								//System.out.println(rs433.getString(r).trim());
							} 
						}
						dataExam1.add(dataExam2); 					 					 
					}
					stmt433.close();					 
					conn43.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
                
				DefaultTableModel modelExam1 = new DefaultTableModel(dataExam1, columnNamesExam1){
					public Class getColumnClass(int column){
						return getValueAt(0, column).getClass();
					}
				};
				tableExam1 = new WebTable(modelExam1){
					public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
						WebLabel c = (WebLabel) super.prepareRenderer(renderer, row, column);
						c.setBackground(Color.WHITE);
						c.setForeground(Color.BLACK);
						c.setHorizontalAlignment(WebLabel.CENTER);
				        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
				        return c;
					}
					public void changeSelection(int row, int column, boolean toggle, boolean extend){
					        super.changeSelection(row, column, toggle, extend);
					 
					        if (editCellAt(row, column)){
					            Component editor = getEditorComponent();
					            editor.requestFocusInWindow();
				
//					          ((JTextComponent)editor).selectAll();
					        }
					}
				};

				TableColumnModel columnModelExam1 = tableExam1.getColumnModel();
			    for(int cl=0;cl<16;cl++){
			    	columnModelExam1.getColumn(cl).setPreferredWidth(35);
				}
			    tableExam1.setPreferredScrollableViewportSize(new Dimension(560, 70));
				tableExam1.setFillsViewportHeight(true);
				tableExam1.setEnabled(true);
				tableExam1.setRowHeight(35);
				tableExam1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				tableExam1.setBounds(40,260,560,70);
				//tableExam1.addMouseListener(this);
				Panel43.add(tableExam1);
				//
				String [] tooth_dUP={"55","54","53","52","51","61","62","63","64","65"};
				String [] tooth_dLOW={"85","84","83","82","81","71","72","73","74","75"};
				int l_tooth_dUP=tooth_dUP.length;
				int l_tooth_dLOW=tooth_dLOW.length;
				
				 WebLabel[] ltooth_dUP=new WebLabel[l_tooth_dUP];
				 for(int row_tooth_dUP=0;row_tooth_dUP<l_tooth_dUP;row_tooth_dUP++){
					 int x_tooth_dUP=155;
					 ltooth_dUP[row_tooth_dUP] = new WebLabel(tooth_dUP[row_tooth_dUP],WebLabel.CENTER);		 
					 ltooth_dUP[row_tooth_dUP].setBounds(x_tooth_dUP+(row_tooth_dUP*35),225,20,20);
					 Panel43.add(ltooth_dUP[row_tooth_dUP]);
				 }
				  
				 WebLabel[] ltooth_dLOW=new WebLabel[l_tooth_dLOW];
				 for(int row_tooth_dLOW=0;row_tooth_dLOW<l_tooth_dLOW;row_tooth_dLOW++){
					 int x_tooth_dLOW=155;
					 ltooth_dLOW[row_tooth_dLOW] = new WebLabel(tooth_dLOW[row_tooth_dLOW],WebLabel.CENTER);		 
					 ltooth_dLOW[row_tooth_dLOW].setBounds(x_tooth_dLOW+(row_tooth_dLOW*35),350,20,20);
					 Panel43.add(ltooth_dLOW[row_tooth_dLOW]);
				 }
				 String [] tooth_pUP={"18","17","16","15","14","13","12","11","21","22","23","24","25","26","27","28"};
				 String [] tooth_pLOW={"48","47","46","45","44","43","42","41","31","32","33","34","35","36","37","38"};
				 int l_tooth_pUP=tooth_pUP.length;
				 int l_tooth_pLOW=tooth_pLOW.length;
				 
				 WebLabel[] ltooth_pUP=new WebLabel[l_tooth_pUP];
				 for(int row_tooth_pUP=0;row_tooth_pUP<l_tooth_pUP;row_tooth_pUP++){
					 int x_tooth_pUP=50;
					 ltooth_pUP[row_tooth_pUP] = new WebLabel(tooth_pUP[row_tooth_pUP],WebLabel.CENTER);		 
					 ltooth_pUP[row_tooth_pUP].setBounds(x_tooth_pUP+(row_tooth_pUP*35),240,20,20);
					 Panel43.add(ltooth_pUP[row_tooth_pUP]);
				 }
				 WebLabel[] ltooth_pLOW=new WebLabel[l_tooth_pLOW];
				 for(int row_tooth_pLOW=0;row_tooth_pLOW<l_tooth_pLOW;row_tooth_pLOW++){
					 int x_tooth_pLOW=50;
					 ltooth_pLOW[row_tooth_pLOW] = new WebLabel(tooth_pLOW[row_tooth_pLOW],WebLabel.CENTER);		 
					 ltooth_pLOW[row_tooth_pLOW].setBounds(x_tooth_pLOW+(row_tooth_pLOW*35),335,20,20);
					 Panel43.add(ltooth_pLOW[row_tooth_pLOW]);
					 
				 }
				//end sec1
				 
				 WebLabel ptExam201,ptExam301,ptExam401,ptExam31,ptExam32,ptExam33,ptExam41,ptExam42,ptExam43,provider;
					ptExam201=new WebLabel("2.สภาวะปริทันต์ ",WebLabel.LEFT);
					ptExam201.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam201.setBounds(15,380,200,20);
					Panel43.add(ptExam201);
					
					columnNamesExam2 = new Vector<String>();			
					columnNamesExam2.add("");
					columnNamesExam2.add("");
					columnNamesExam2.add("");
					Vector<Vector<String>> dataExam201 = new Vector<Vector<String>>();
					final Vector<String> dataExam301 = new Vector<String>();
					final Vector<String> dataExam401 = new Vector<String>();
					String [] pLabel_UP={"ฟันหลังบนขวา","ฟันหน้าบน","ฟันหลังบนซ้าย"};
					String [] pLabel_LOW={"ฟันหลังล่างขวา","ฟันหน้าล่าง","ฟันหลังล่างซ้าย"};
					int l_pLabel_UP=pLabel_UP.length;
					int l_pLabel_LOW=pLabel_LOW.length;
					WebLabel[] p_UP=new WebLabel[l_pLabel_UP];
					 for(int row_p_UP=0;row_p_UP<l_pLabel_UP;row_p_UP++){
						 int x_UP=50;
						 p_UP[row_p_UP] = new WebLabel(pLabel_UP[row_p_UP],WebLabel.CENTER);		 
						 p_UP[row_p_UP].setBounds(x_UP+(row_p_UP*140),410,100,20);
						 Panel43.add(p_UP[row_p_UP]);
					 }
					  
					 WebLabel[] p_LOW=new WebLabel[l_pLabel_LOW];
					 for(int row_p_LOW=0;row_p_LOW<l_pLabel_LOW;row_p_LOW++){
						 int x_tooth_dLOW=50;
						 p_LOW[row_p_LOW] = new WebLabel(pLabel_LOW[row_p_LOW],WebLabel.CENTER);		 
						 p_LOW[row_p_LOW].setBounds(x_tooth_dLOW+(row_p_LOW*140),520,100,20);
						 Panel43.add(p_LOW[row_p_LOW]);
					 }
					
					
					ptExam3=new WebLabel("3.การสบฟัน",WebLabel.LEFT);
					ptExam3.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam3.setBounds(15,550,180,20);
					Panel43.add(ptExam3);
					
					ptExam31=new WebLabel("1.จำนวนคู่สบฟันแท้กับฟันแท้ ",WebLabel.LEFT);
					ptExam31.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam31.setBounds(25,570,350,20);				
					Panel43.add(ptExam31);
					
					TextField_occ1 = new WebTextField();
					TextField_occ1.setBounds(400,570,50,20);
					TextField_occ1.setText("0");
					TextField_occ1.addActionListener(this);
					Panel43.add(TextField_occ1);
					
					ptExam32=new WebLabel("2.จำนวนคู่สบฟันแท้กับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",WebLabel.LEFT);
					ptExam32.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam32.setBounds(25,590,350,20);					
					Panel43.add(ptExam32);
					
					TextField_occ2 = new WebTextField();
					TextField_occ2.setBounds(400,590,50,20);
					TextField_occ2.addActionListener(this);
					TextField_occ2.setEnabled(false);
					TextField_occ2.setText("0");
					if(checkAge()==4){
						TextField_occ2.setEnabled(true);
						TextField_occ2.setText("");					 
					}
					Panel43.add(TextField_occ2);
					
					ptExam33=new WebLabel("3.จำนวนคู่สบฟันเทียมกับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",WebLabel.LEFT);
					ptExam33.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam33.setBounds(25,610,350,20);					
					Panel43.add(ptExam33);
					
					TextField_occ3 = new WebTextField();
					TextField_occ3.setBounds(400,610,50,20);
					TextField_occ3.addActionListener(this);
					TextField_occ3.setEnabled(false);
					TextField_occ3.setText("0");
					if(checkAge()==4){
						TextField_occ3.setEnabled(true);
						TextField_occ3.setText("");
					}
					Panel43.add(TextField_occ3);
					
					ptExam4=new WebLabel("4.บริการที่ควรได้รับ ",WebLabel.LEFT);
					ptExam4.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam4.setBounds(15,635,180,20);					
					Panel43.add(ptExam4);
					
					ptExam41=new WebLabel("1.การทาฟูลออไรด์",WebLabel.LEFT);
					ptExam41.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam41.setBounds(25,660,100,20);					
					Panel43.add(ptExam41);
					
					FNButton=new JRadioButton [2];
					groupFN = new ButtonGroup();
					for(int row=0;row<2;row++){
						int x=140;
						if(row==1){
							FNButton[row] = new JRadioButton("ไม่จำเป็นf");						  
						 }else{
							 FNButton[row] = new JRadioButton("จำเป็นf");
						 }				 
						FNButton[row].setActionCommand( FNButton[row].getText() );
						FNButton[row].addActionListener(this);				
						FNButton[row].setBounds(x+(row*100),660,100,20);
						FNButton[row].setBackground(new Color(207,229,233));					
						Panel43.add(FNButton[row]);
						groupFN.add(FNButton[row]);				 
					}
					
					ptExam42=new WebLabel("2.การขูดหินน้ำลาย   ",WebLabel.LEFT);
					ptExam42.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam42.setBounds(25,690,250,20);				
					Panel43.add(ptExam42);
					
					SNButton=new JRadioButton [2];
					groupSN = new ButtonGroup();
					for(int row=0;row<2;row++){
						int x=140;
						if(row==1){
							SNButton[row] = new JRadioButton("ไม่จำเป็นs");					  
						 }else{
							 SNButton[row] = new JRadioButton("จำเป็นs");
						 }			
						SNButton[row].setActionCommand( SNButton[row].getText() );
						SNButton[row].addActionListener(this);					 				
						SNButton[row].setBounds(x+(row*100),690,100,20);
						SNButton[row].setBackground(new Color(207,229,233));										
						Panel43.add(SNButton[row]);
						groupSN.add(SNButton[row]);				 
					}
					
					ptExam43=new WebLabel("3.การใส่ฟันเทียม ",WebLabel.LEFT);
					ptExam43.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam43.setBounds(25,720,250,20);					
					Panel43.add(ptExam43);
					
					String [] prosNeed={"ต้องใส่ฟันบนและล่าง","ต้องใส่ฟันบน","ต้องใส่ฟันล่าง","ไม่ต้องใส่ฟันเทียม"};
					int l_prosNeed = prosNeed.length;			
					ProsNButton=new JRadioButton [l_prosNeed];
					groupProsN = new ButtonGroup();
					for(int row=0;row<l_prosNeed;row++){
						int x=140;
						if(row==3){
							ProsNButton[row] = new JRadioButton(prosNeed[row]);						  
						 }else{
							ProsNButton[row] = new JRadioButton(prosNeed[row]);
						 }					 
						ProsNButton[row].setActionCommand( ProsNButton[row].getText() );
						ProsNButton[row].addActionListener(this);					 				
						ProsNButton[row].setBounds(x+(row*120),720,120,20);
						ProsNButton[row].setBackground(new Color(207,229,233));										
						Panel43.add(ProsNButton[row]);
						groupProsN.add(ProsNButton[row]);				 
					}
					provider=new WebLabel("ผู้ตรวจ  ",WebLabel.LEFT);
					provider.setFont(new Font("Tahoma", Font.PLAIN, 13));
					provider.setBounds(25,750,300,20);					
					Panel43.add(provider);
					
					TextField_provider = new WebTextField();
					TextField_provider.setBounds(150,750,50,20);
					TextField_provider.setText("");
					TextField_provider.addActionListener(this);
					Panel43.add(TextField_provider);
					//String sql_43 = "select perio,occpetope,occpetopr,occprtopr,fluneed,sneed,prosneed,provider  from dhis where  hn= '"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
					
					String sql_43 = "select perio,occpetope,occpetopr,occprtopr,fluneed,sneed,prosneed,provider  from dhis where  hn= '"+oUserInfo.GetPtHN()+"'  and visitdate between '"+date_inti+"' and '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' order by visitdate desc limit 1";
					Connection conn431;
					PreparedStatement stmt431;
					try {
						conn431 = new DBmanager().getConnMySql();	 
						stmt431 = conn431.prepareStatement(sql_43);	
						ResultSet rs43 = stmt431.executeQuery();
						while (rs43.next()) {
							if(rs43.getString(1) ==null){
								String [] dex4={"","",""};
								for(int r=0;r<dex4.length;r++){
									dataExam301.add(dex4[r]);
								}			           
					            dataExam201.add(dataExam301); 
					            String [] dex5={"","",""};
								for(int r=0;r<dex5.length;r++){
									dataExam401.add(dex5[r]);
								}
					            dataExam201.add(dataExam401); 
							}
							else if(rs43.getString(1) !=null ){
								if( rs43.getString(1).equals("")) {
									String [] dex4={"","",""};
									for(int r=0;r<dex4.length;r++){
										dataExam301.add(dex4[r]);
									}			           
						            dataExam201.add(dataExam301); 
						            String [] dex5={"","",""};
									for(int r=0;r<dex5.length;r++){
										dataExam401.add(dex5[r]);
									}
						            dataExam201.add(dataExam401); 
								}else {
									String perio_text=rs43.getString(1).trim(); 	
									char[] perioArray = perio_text.toCharArray();
									for(int r=0;r<3;r++){
										dataExam301.add(String.valueOf(perioArray[r]));
									}
									dataExam201.add(dataExam301);
									for(int r=3;r<6;r++){
										dataExam401.add(String.valueOf(perioArray[r]));
									}
									dataExam201.add(dataExam401);
								}
								 
							}
							if(rs43.getString(2) ==null){							
							}
							else if(rs43.getString(2) !=null){
								TextField_occ1.setText(rs43.getString(2).trim());
							}						
							if(rs43.getString(3) ==null){							
							}
							else if(rs43.getString(3) !=null){
								TextField_occ2.setText(rs43.getString(3).trim());
							}
							if(rs43.getString(4) ==null){							
							}
							else if(rs43.getString(4) !=null){
								TextField_occ3.setText(rs43.getString(4).trim());
							}
							if(rs43.getString(5) ==null){						
							}
							else if(rs43.getString(5) !=null){
								if(rs43.getString(5).trim().equals("1")){
									FNButton[0].setSelected(true);
								}
								else if(rs43.getString(5).trim().equals("2")){
									FNButton[1].setSelected(true);
								}						 
							}						
							if(rs43.getString(6) ==null){							
							}
							else if(rs43.getString(6) !=null){
								if(rs43.getString(6).trim().equals("1")){
									SNButton[0].setSelected(true);
								}
								else if(rs43.getString(6).trim().equals("2")){
									SNButton[1].setSelected(true);
								}						 
							}						
							if(rs43.getString(7) ==null){						 
							}
							else if(rs43.getString(7) !=null){
								if(rs43.getString(7).trim().equals("1")){
									ProsNButton[0].setSelected(true);
								}
								else if(rs43.getString(7).trim().equals("2")){
									ProsNButton[1].setSelected(true);
								}
								else if(rs43.getString(7).trim().equals("3")){
									ProsNButton[2].setSelected(true);
								}
								else if(rs43.getString(7).trim().equals("4")){
									ProsNButton[3].setSelected(true);
								}							 
							}						
							if(rs43.getString(8) ==null){
								TextField_provider.setText("047");
							}
							else if(rs43.getString(8) !=null){
								TextField_provider.setText(rs43.getString(8).trim());
							}						 					 
						}		 
						stmt431.close(); 
						conn431.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					DefaultTableModel modelExam201 = new DefaultTableModel(dataExam201, columnNamesExam2){
						public Class getColumnClass(int column){
							return getValueAt(0, column).getClass();
						}
					};
					
					tableExam2 = new WebTable(modelExam201){
						public Component prepareRenderer(TableCellRenderer renderer,int row, int column){
							WebLabel c = (WebLabel) super.prepareRenderer(renderer, row, column);
							c.setBackground(Color.WHITE);
							c.setForeground(Color.BLACK);
							c.setHorizontalAlignment(WebLabel.CENTER);
					        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
	
					        return c;
						}
						public void changeSelection(int row, int column, boolean toggle, boolean extend){
					        super.changeSelection(row, column, toggle, extend);
					 
					        if (editCellAt(row, column)){
					            Component editor = getEditorComponent();
					            editor.requestFocusInWindow();
//					          ((JTextComponent)editor).selectAll();
					        }
						}

					};
					TableColumnModel columnModelExam2 = tableExam2.getColumnModel();
					for(int cl2=0;cl2<3;cl2++){
				    	columnModelExam2.getColumn(cl2).setPreferredWidth(35);
					}
				    tableExam2.setPreferredScrollableViewportSize(new Dimension(560, 70));
					tableExam2.setFillsViewportHeight(true);
					tableExam2.setEnabled(true);
					tableExam2.setRowHeight(35);
					tableExam2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					tableExam2.setBounds(40,440,420,70);
					//tableExam2.addMouseListener(this);
					mid2.add(tableExam2);
					
				
				scrollPane43 = new JScrollPane(Panel43);
				//scrollPane43.setPreferredSize(new Dimension(500,500));
				//scrollPane43.setViewportView(Panel43);
				mid2.add(Panel43, BorderLayout.CENTER);
				
				mid2.revalidate();
				mid2.repaint();	
				MiddleSection.revalidate();
				MiddleSection.repaint();
				//right
				right1.removeAll();
				ButtonSaveExam1 = new WebButton("Save");		 
				ButtonSaveExam1.setBounds(5,10,80,30);
				ButtonSaveExam1.addActionListener(this);
				ButtonPrintExam1 = new WebButton("Print");		 
				ButtonPrintExam1.setBounds(5,50,80,30);
				ButtonPrintExam1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						PrintOPD(formcode,"2",page,form_clinic_name);
						
					}
				});
				
				Button431 = new WebButton("To 43");		 
				Button431.setBounds(5,90,80,30);
				Button431.addActionListener(this);
				
				ButtonEdit = new WebButton("Edit");		 
				ButtonEdit.setBounds(5,130,80,30);			
				ButtonEdit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ButtonSaveExam1.setEnabled(true);
						Button431.setEnabled(true);
						
					}
				});
				
				if(oUserInfo.GetPtVN().equals("")){
					 
				}else{
					right1.add(ButtonSaveExam1);
					right1.add(ButtonPrintExam1);
					right1.add(Button431); 
					right1.add(ButtonEdit);
				}
				if(exam_status==0) {
					ButtonSaveExam1.setEnabled(false);
					ButtonPrintExam1.setEnabled(false);
					Button431.setEnabled(false);
				}
				right1.revalidate();
				right1.repaint();
				RightSection.revalidate();
				RightSection.repaint();
			}
		}
		else if(formcode.equals("00113")){
			exam_status=0;
			if(oUserInfo.GetPtHN().length() !=7){
				JOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",JOptionPane.ERROR_MESSAGE);

			}else{
				checkExam(oUserInfo.GetPtHN());
				//right Section
				
				if(oUserInfo.GetPtHN().length() !=7){
					JOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",JOptionPane.ERROR_MESSAGE);

				}else{
					mid2.removeAll();
					checkExam(oUserInfo.GetPtHN());
					
					Panel43 = new WebPanel();
					Panel43.setPreferredSize(new Dimension((width*2)/10, 800));
					Panel43.setLayout(null);
					
					JLabel ptType,ptEdu,ptEdulevel,ptExam1,ptExam2,ptExam3,ptExam4;
					String [] ptTypeB={"หญิงตั้งครรภ์","เด็กก่อนวัยเรียน","เด็กวัยเรียน","ผู้สูงอายุ","กลุ่มอื่นๆ"};
					String [] ptEduB={"ศพด.","ประถม-รัฐบาล","ประถม-เทศบาล","ประถม-ท้องถิ่น","ประถม-เอกชน","มัธยม-รัฐบาล","มัธยม-เทศบาล","มัธยม-ท้องถิ่น","มัธยม-เอกชน","ไม่เลือก"};
					String [] ptEdulevelB={"ศพด.1-3","ประถม1-6","มัธยม1-6"};
					
					ptType=new JLabel("ประเภท ",JLabel.LEFT);
					ptType.setBounds(5,1,150,20);
					ptType.setFont(new Font("Tahoma", Font.PLAIN, 13));
					Panel43.add(ptType);
					int l_ptTypeB=ptTypeB.length;
					ptTypeBButton=new JRadioButton [l_ptTypeB];
					groupTypeB = new ButtonGroup();
					for(int row=0;row<l_ptTypeB;row++){
						int x=30;
						ptTypeBButton[row] = new JRadioButton(ptTypeB[row]);
						ptTypeBButton[row].setBounds(x+(row*130),20,120,20);
						ptTypeBButton[row].setBackground(new Color(207,229,233));
						ptTypeBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptTypeBButton[row].setActionCommand( ptTypeBButton[row].getText() );
						ptTypeBButton[row].addActionListener(this); 
						Panel43.add(ptTypeBButton[row]);
						groupTypeB.add(ptTypeBButton[row]);
					}
					//
					ptEdu=new JLabel("สถานศึกษา (กรณีเด็กวัยเรียน) ",JLabel.LEFT);
					ptEdu.setBounds(5,45,200,20);
					ptEdu.setFont(new Font("Tahoma", Font.PLAIN, 13));
					Panel43.add(ptEdu);
					
					int l_ptEduB=ptEduB.length;
					ptEduBButton=new JRadioButton [l_ptEduB];
					groupEduB = new ButtonGroup();
					for(int row=0;row<10;row++){
						if(row<5){
						int x=30;
						 
						ptEduBButton[row] = new JRadioButton(ptEduB[row]);
						ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
						ptEduBButton[row].addActionListener(this);
						ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptEduBButton[row].setBounds(x+(row*130),70,120,20);
						ptEduBButton[row].setBackground(new Color(207,229,233));
						if(checkAge()==3){
							ptEduBButton[row].setEnabled(true);
						}else{
							ptEduBButton[row].setEnabled(false);
						}
						Panel43.add(ptEduBButton[row]);
						groupEduB.add(ptEduBButton[row]);
						}else if(row>4 && row<10){
							int x=30;
							 
							ptEduBButton[row] = new JRadioButton(ptEduB[row]);
							ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
							ptEduBButton[row].addActionListener(this);
							ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
							ptEduBButton[row].setBounds(x+((row-5)*130),100,120,20);
							ptEduBButton[row].setBackground(new Color(207,229,233));
							if(checkAge()==3){
								ptEduBButton[row].setEnabled(true);
							}else{
								ptEduBButton[row].setEnabled(false);
							}
							Panel43.add(ptEduBButton[row]);
							groupEduB.add(ptEduBButton[row]);
						}
					}
					//
					ptEdulevel=new JLabel("ระดับการศึกษา (กรณีเด็กวัยเรียน) ",JLabel.LEFT);
					ptEdulevel.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptEdulevel.setBounds(5,140,250,20);				
					Panel43.add(ptEdulevel);
									 
					Border textfield_b = new SoftBevelBorder(BevelBorder.LOWERED);
					TextField_EdulevelB = new WebTextField();
					TextField_EdulevelB.setFont(new Font("Tahoma", Font.PLAIN, 13));
					TextField_EdulevelB.setBorder(textfield_b);
					TextField_EdulevelB.setBounds(200,140,50,20);
					TextField_EdulevelB.setText("");
					TextField_EdulevelB.addActionListener(this);
					Panel43.add(TextField_EdulevelB);
					//
					ptExam1=new JLabel("1.การตรวจในช่องปาก ",JLabel.LEFT);
					ptExam1.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam1.setBounds(5,180,200,20);
					Panel43.add(ptExam1);
					
					columnNamesExam1 = new Vector<String>();
					
					for(int c=0;c<16;c++){
						columnNamesExam1.add("");
					}
					
					Vector<Vector<String>> dataExam1 = new Vector<Vector<String>>();
					final Vector<String> dataExam = new Vector<String>();
					final Vector<String> dataExam2 = new Vector<String>();
					
					String sql_432 = "select * from dhis where  hn= '"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
					Connection conn43;
					PreparedStatement stmt43,stmt433;
					try {
						conn43 = new DBmanager().getConnMySql(); 
						stmt43 = conn43.prepareStatement(sql_432);
						 
						ResultSet rs43 = stmt43.executeQuery();
					 
						while (rs43.next()) {
							if(rs43.getString(6) ==null){
								//ptTypeBButton[4].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("1")){
								ptTypeBButton[0].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("2")){
								ptTypeBButton[1].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("3")){
								ptTypeBButton[2].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("4")){
								ptTypeBButton[3].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("5")){
								ptTypeBButton[4].setSelected(true);
							}
							if(rs43.getString(7) ==null || rs43.getString(7).trim().equals("0")){
								 
							}
							else if(rs43.getString(7).trim().equals("1")){
								ptEduBButton[0].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("2")){
								ptEduBButton[1].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("3")){
								ptEduBButton[2].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("4")){
								ptEduBButton[3].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("5")){
								ptEduBButton[4].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("6")){
								ptEduBButton[5].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("7")){
								ptEduBButton[6].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("8")){
								ptEduBButton[7].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("9")){
								ptEduBButton[8].setSelected(true);
							}
					
							if(rs43.getString(8) ==null || rs43.getString(8).trim().equals("0")){
								TextField_EdulevelB.setText("");
							}
							else  if(rs43.getString(8) !=null){
								TextField_EdulevelB.setText(rs43.getString(8).trim());
							}
								
								
							for(int r=9;r<25;r++){
								//System.out.println(rs43.getString(r));
								if(rs43.getString(r) ==null){
									dataExam.add("");
								}							
								else if(rs43.getString(r) !=null){
									dataExam.add(rs43.getString(r).trim());
									//System.out.println(rs43.getString(r).trim());
								}							 
							}
							dataExam1.add(dataExam); 					 					 
						}
						stmt43.close(); 
						stmt433 = conn43.prepareStatement(sql_432);
						ResultSet rs433 = stmt433.executeQuery();
						while (rs433.next()) {						
							for(int r=25;r<41;r++){
								if(rs433.getString(r) ==null){
									dataExam.add("");
								}
								else if(rs433.getString(r) !=null){
									dataExam2.add(rs433.getString(r).trim());
									//System.out.println(rs433.getString(r).trim());
								} 
							}
							dataExam1.add(dataExam2); 					 					 
						}
						stmt433.close();					 
						conn43.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DefaultTableModel modelExam1 = new DefaultTableModel(dataExam1, columnNamesExam1){
						public Class getColumnClass(int column){
							return getValueAt(0, column).getClass();
						}
					};
					tableExam1 = new WebTable(modelExam1){
						public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
							JLabel c = (JLabel) super.prepareRenderer(renderer, row, column);
							c.setBackground(Color.WHITE);
							c.setForeground(Color.BLACK);
							c.setHorizontalAlignment(JLabel.CENTER);
					        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
					        return c;
						}
					};
					TableColumnModel columnModelExam1 = tableExam1.getColumnModel();
				    for(int cl=0;cl<16;cl++){
				    	columnModelExam1.getColumn(cl).setPreferredWidth(35);
					}
				    tableExam1.setPreferredScrollableViewportSize(new Dimension(560, 70));
					tableExam1.setFillsViewportHeight(true);
					tableExam1.setEnabled(true);
					tableExam1.setRowHeight(35);
					tableExam1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					tableExam1.setBounds(40,260,560,70);
					//tableExam1.addMouseListener(this);
					Panel43.add(tableExam1);
					//
					String [] tooth_dUP={"55","54","53","52","51","61","62","63","64","65"};
					String [] tooth_dLOW={"85","84","83","82","81","71","72","73","74","75"};
					int l_tooth_dUP=tooth_dUP.length;
					int l_tooth_dLOW=tooth_dLOW.length;
					
					 JLabel[] ltooth_dUP=new JLabel[l_tooth_dUP];
					 for(int row_tooth_dUP=0;row_tooth_dUP<l_tooth_dUP;row_tooth_dUP++){
						 int x_tooth_dUP=155;
						 ltooth_dUP[row_tooth_dUP] = new JLabel(tooth_dUP[row_tooth_dUP],JLabel.CENTER);		 
						 ltooth_dUP[row_tooth_dUP].setBounds(x_tooth_dUP+(row_tooth_dUP*35),225,20,20);
						 Panel43.add(ltooth_dUP[row_tooth_dUP]);
					 }
					  
					 JLabel[] ltooth_dLOW=new JLabel[l_tooth_dLOW];
					 for(int row_tooth_dLOW=0;row_tooth_dLOW<l_tooth_dLOW;row_tooth_dLOW++){
						 int x_tooth_dLOW=155;
						 ltooth_dLOW[row_tooth_dLOW] = new JLabel(tooth_dLOW[row_tooth_dLOW],JLabel.CENTER);		 
						 ltooth_dLOW[row_tooth_dLOW].setBounds(x_tooth_dLOW+(row_tooth_dLOW*35),350,20,20);
						 Panel43.add(ltooth_dLOW[row_tooth_dLOW]);
					 }
					 String [] tooth_pUP={"18","17","16","15","14","13","12","11","21","22","23","24","25","26","27","28"};
					 String [] tooth_pLOW={"48","47","46","45","44","43","42","41","31","32","33","34","35","36","37","38"};
					 int l_tooth_pUP=tooth_pUP.length;
					 int l_tooth_pLOW=tooth_pLOW.length;
					 
					 JLabel[] ltooth_pUP=new JLabel[l_tooth_pUP];
					 for(int row_tooth_pUP=0;row_tooth_pUP<l_tooth_pUP;row_tooth_pUP++){
						 int x_tooth_pUP=50;
						 ltooth_pUP[row_tooth_pUP] = new JLabel(tooth_pUP[row_tooth_pUP],JLabel.CENTER);		 
						 ltooth_pUP[row_tooth_pUP].setBounds(x_tooth_pUP+(row_tooth_pUP*35),240,20,20);
						 Panel43.add(ltooth_pUP[row_tooth_pUP]);
					 }
					 JLabel[] ltooth_pLOW=new JLabel[l_tooth_pLOW];
					 for(int row_tooth_pLOW=0;row_tooth_pLOW<l_tooth_pLOW;row_tooth_pLOW++){
						 int x_tooth_pLOW=50;
						 ltooth_pLOW[row_tooth_pLOW] = new JLabel(tooth_pLOW[row_tooth_pLOW],JLabel.CENTER);		 
						 ltooth_pLOW[row_tooth_pLOW].setBounds(x_tooth_pLOW+(row_tooth_pLOW*35),335,20,20);
						 Panel43.add(ltooth_pLOW[row_tooth_pLOW]);
						 
					 }
					//end sec1
					 
					 JLabel ptExam201,ptExam301,ptExam401,ptExam31,ptExam32,ptExam33,ptExam41,ptExam42,ptExam43,provider;
						ptExam201=new JLabel("2.สภาวะปริทันต์ ",JLabel.LEFT);
						ptExam201.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam201.setBounds(15,380,200,20);
						Panel43.add(ptExam201);
						
						columnNamesExam2 = new Vector<String>();			
						columnNamesExam2.add("");
						columnNamesExam2.add("");
						columnNamesExam2.add("");
						Vector<Vector<String>> dataExam201 = new Vector<Vector<String>>();
						final Vector<String> dataExam301 = new Vector<String>();
						final Vector<String> dataExam401 = new Vector<String>();
						String [] pLabel_UP={"ฟันหลังบนขวา","ฟันหน้าบน","ฟันหลังบนซ้าย"};
						String [] pLabel_LOW={"ฟันหลังล่างขวา","ฟันหน้าล่าง","ฟันหลังล่างซ้าย"};
						int l_pLabel_UP=pLabel_UP.length;
						int l_pLabel_LOW=pLabel_LOW.length;
						JLabel[] p_UP=new JLabel[l_pLabel_UP];
						 for(int row_p_UP=0;row_p_UP<l_pLabel_UP;row_p_UP++){
							 int x_UP=50;
							 p_UP[row_p_UP] = new JLabel(pLabel_UP[row_p_UP],JLabel.CENTER);		 
							 p_UP[row_p_UP].setBounds(x_UP+(row_p_UP*140),410,100,20);
							 Panel43.add(p_UP[row_p_UP]);
						 }
						  
						 JLabel[] p_LOW=new JLabel[l_pLabel_LOW];
						 for(int row_p_LOW=0;row_p_LOW<l_pLabel_LOW;row_p_LOW++){
							 int x_tooth_dLOW=50;
							 p_LOW[row_p_LOW] = new JLabel(pLabel_LOW[row_p_LOW],JLabel.CENTER);		 
							 p_LOW[row_p_LOW].setBounds(x_tooth_dLOW+(row_p_LOW*140),520,100,20);
							 Panel43.add(p_LOW[row_p_LOW]);
						 }
						
						
						ptExam3=new JLabel("3.การสบฟัน",JLabel.LEFT);
						ptExam3.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam3.setBounds(15,550,180,20);
						Panel43.add(ptExam3);
						
						ptExam31=new JLabel("1.จำนวนคู่สบฟันแท้กับฟันแท้ ",JLabel.LEFT);
						ptExam31.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam31.setBounds(25,570,350,20);				
						Panel43.add(ptExam31);
						
						TextField_occ1 = new WebTextField();
						TextField_occ1.setBounds(400,570,50,20);
						TextField_occ1.setText("0");
						TextField_occ1.addActionListener(this);
						Panel43.add(TextField_occ1);
						
						ptExam32=new JLabel("2.จำนวนคู่สบฟันแท้กับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",JLabel.LEFT);
						ptExam32.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam32.setBounds(25,590,350,20);					
						Panel43.add(ptExam32);
						
						TextField_occ2 = new WebTextField();
						TextField_occ2.setBounds(400,590,50,20);
						TextField_occ2.addActionListener(this);
						TextField_occ2.setEnabled(false);
						TextField_occ2.setText("0");
						if(checkAge()==4){
							TextField_occ2.setEnabled(true);
							TextField_occ2.setText("");					 
						}
						Panel43.add(TextField_occ2);
						
						ptExam33=new JLabel("3.จำนวนคู่สบฟันเทียมกับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",JLabel.LEFT);
						ptExam33.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam33.setBounds(25,610,350,20);					
						Panel43.add(ptExam33);
						
						TextField_occ3 = new WebTextField();
						TextField_occ3.setBounds(400,610,50,20);
						TextField_occ3.addActionListener(this);
						TextField_occ3.setEnabled(false);
						TextField_occ3.setText("0");
						if(checkAge()==4){
							TextField_occ3.setEnabled(true);
							TextField_occ3.setText("");
						}
						Panel43.add(TextField_occ3);
						
						ptExam4=new JLabel("4.บริการที่ควรได้รับ ",JLabel.LEFT);
						ptExam4.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam4.setBounds(15,635,180,20);					
						Panel43.add(ptExam4);
						
						ptExam41=new JLabel("1.การทาฟูลออไรด์",JLabel.LEFT);
						ptExam41.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam41.setBounds(25,660,100,20);					
						Panel43.add(ptExam41);
						
						FNButton=new JRadioButton [2];
						groupFN = new ButtonGroup();
						for(int row=0;row<2;row++){
							int x=140;
							if(row==1){
								FNButton[row] = new JRadioButton("ไม่จำเป็นf");						  
							 }else{
								 FNButton[row] = new JRadioButton("จำเป็นf");
							 }				 
							FNButton[row].setActionCommand( FNButton[row].getText() );
							FNButton[row].addActionListener(this);				
							FNButton[row].setBounds(x+(row*100),660,100,20);
							FNButton[row].setBackground(new Color(207,229,233));					
							Panel43.add(FNButton[row]);
							groupFN.add(FNButton[row]);				 
						}
						
						ptExam42=new JLabel("2.การขูดหินน้ำลาย   ",JLabel.LEFT);
						ptExam42.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam42.setBounds(25,690,250,20);				
						Panel43.add(ptExam42);
						
						SNButton=new JRadioButton [2];
						groupSN = new ButtonGroup();
						for(int row=0;row<2;row++){
							int x=140;
							if(row==1){
								SNButton[row] = new JRadioButton("ไม่จำเป็นs");					  
							 }else{
								 SNButton[row] = new JRadioButton("จำเป็นs");
							 }			
							SNButton[row].setActionCommand( SNButton[row].getText() );
							SNButton[row].addActionListener(this);					 				
							SNButton[row].setBounds(x+(row*100),690,100,20);
							SNButton[row].setBackground(new Color(207,229,233));										
							Panel43.add(SNButton[row]);
							groupSN.add(SNButton[row]);				 
						}
						
						ptExam43=new JLabel("3.การใส่ฟันเทียม ",JLabel.LEFT);
						ptExam43.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam43.setBounds(25,720,250,20);					
						Panel43.add(ptExam43);
						
						String [] prosNeed={"ต้องใส่ฟันบนและล่าง","ต้องใส่ฟันบน","ต้องใส่ฟันล่าง","ไม่ต้องใส่ฟันเทียม"};
						int l_prosNeed = prosNeed.length;			
						ProsNButton=new JRadioButton [l_prosNeed];
						groupProsN = new ButtonGroup();
						for(int row=0;row<l_prosNeed;row++){
							int x=140;
							if(row==3){
								ProsNButton[row] = new JRadioButton(prosNeed[row]);						  
							 }else{
								ProsNButton[row] = new JRadioButton(prosNeed[row]);
							 }					 
							ProsNButton[row].setActionCommand( ProsNButton[row].getText() );
							ProsNButton[row].addActionListener(this);					 				
							ProsNButton[row].setBounds(x+(row*120),720,120,20);
							ProsNButton[row].setBackground(new Color(207,229,233));										
							Panel43.add(ProsNButton[row]);
							groupProsN.add(ProsNButton[row]);				 
						}
						provider=new JLabel("ผู้ตรวจ  ",JLabel.LEFT);
						provider.setFont(new Font("Tahoma", Font.PLAIN, 13));
						provider.setBounds(25,750,300,20);					
						Panel43.add(provider);
						
						TextField_provider = new WebTextField();
						TextField_provider.setBounds(150,750,50,20);
						TextField_provider.setText("");
						TextField_provider.addActionListener(this);
						Panel43.add(TextField_provider);
						
						String sql_43 = "select perio,occpetope,occpetopr,occprtopr,fluneed,sneed,prosneed,provider  from dhis where  hn= '"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
						Connection conn431;
						PreparedStatement stmt431;
						try {
							conn431 = new DBmanager().getConnMySql();	 
							stmt431 = conn431.prepareStatement(sql_43);	
							ResultSet rs43 = stmt431.executeQuery();
							while (rs43.next()) {
								
								if(rs43.getString(1) ==null){
									String [] dex4={"","",""};
									for(int r=0;r<dex4.length;r++){
										dataExam301.add(dex4[r]);
									}			           
						            dataExam201.add(dataExam301); 
						            String [] dex5={"","",""};
									for(int r=0;r<dex5.length;r++){
										dataExam401.add(dex5[r]);
									}
						            dataExam201.add(dataExam401); 
								}
								else if(rs43.getString(1) !=null){
									if(rs43.getString(1).equals("")) {
										String [] dex4={"","",""};
										for(int r=0;r<dex4.length;r++){
											dataExam301.add(dex4[r]);
										}			           
							            dataExam201.add(dataExam301); 
							            String [] dex5={"","",""};
										for(int r=0;r<dex5.length;r++){
											dataExam401.add(dex5[r]);
										}
							            dataExam201.add(dataExam401); 
									}else {
										String perio_text=rs43.getString(1).trim(); 	
										char[] perioArray = perio_text.toCharArray();
										for(int r=0;r<3;r++){
											dataExam301.add(String.valueOf(perioArray[r]));
										}
										dataExam201.add(dataExam301);
										for(int r=3;r<6;r++){
											dataExam401.add(String.valueOf(perioArray[r]));
										}
										dataExam201.add(dataExam401);
									}
									 
								}
								if(rs43.getString(2) ==null){							
								}
								else if(rs43.getString(2) !=null){
									TextField_occ1.setText(rs43.getString(2).trim());
								}						
								if(rs43.getString(3) ==null){							
								}
								else if(rs43.getString(3) !=null){
									TextField_occ2.setText(rs43.getString(3).trim());
								}
								if(rs43.getString(4) ==null){							
								}
								else if(rs43.getString(4) !=null){
									TextField_occ3.setText(rs43.getString(4).trim());
								}
								if(rs43.getString(5) ==null){						
								}
								else if(rs43.getString(5) !=null){
									if(rs43.getString(5).trim().equals("1")){
										FNButton[0].setSelected(true);
									}
									else if(rs43.getString(5).trim().equals("2")){
										FNButton[1].setSelected(true);
									}						 
								}						
								if(rs43.getString(6) ==null){							
								}
								else if(rs43.getString(6) !=null){
									if(rs43.getString(6).trim().equals("1")){
										SNButton[0].setSelected(true);
									}
									else if(rs43.getString(6).trim().equals("2")){
										SNButton[1].setSelected(true);
									}						 
								}						
								if(rs43.getString(7) ==null){						 
								}
								else if(rs43.getString(7) !=null){
									if(rs43.getString(7).trim().equals("1")){
										ProsNButton[0].setSelected(true);
									}
									else if(rs43.getString(7).trim().equals("2")){
										ProsNButton[1].setSelected(true);
									}
									else if(rs43.getString(7).trim().equals("3")){
										ProsNButton[2].setSelected(true);
									}
									else if(rs43.getString(7).trim().equals("4")){
										ProsNButton[3].setSelected(true);
									}							 
								}						
								if(rs43.getString(8) ==null){
									TextField_provider.setText("047");
								}
								else if(rs43.getString(8) !=null){
									TextField_provider.setText(rs43.getString(8).trim());
								}						 					 
							}		 
							stmt431.close(); 
							conn431.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						DefaultTableModel modelExam201 = new DefaultTableModel(dataExam201, columnNamesExam2){
							public Class getColumnClass(int column){
								return getValueAt(0, column).getClass();
							}
						};
						
						tableExam2 = new WebTable(modelExam201){
							public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
								JLabel c = (JLabel) super.prepareRenderer(renderer, row, column);
								c.setBackground(Color.WHITE);
								c.setForeground(Color.BLACK);
								c.setHorizontalAlignment(JLabel.CENTER);
						        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
						        return c;
							}
						};
						TableColumnModel columnModelExam2 = tableExam2.getColumnModel();
						for(int cl2=0;cl2<3;cl2++){
					    	columnModelExam2.getColumn(cl2).setPreferredWidth(35);
						}
					    tableExam2.setPreferredScrollableViewportSize(new Dimension(560, 70));
						tableExam2.setFillsViewportHeight(true);
						tableExam2.setEnabled(true);
						tableExam2.setRowHeight(35);
						tableExam2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
						tableExam2.setBounds(40,440,420,70);
						//tableExam2.addMouseListener(this);
						mid2.add(tableExam2);
						
					
					scrollPane43 = new JScrollPane(Panel43);
					//scrollPane43.setPreferredSize(new Dimension(500,500));
					//scrollPane43.setViewportView(Panel43);
					mid2.add(scrollPane43, BorderLayout.CENTER);
					
					mid2.revalidate();
					mid2.repaint();	
					MiddleSection.revalidate();
					MiddleSection.repaint();
					//right
					right1.removeAll();
					ButtonSaveExam3 = new WebButton("Save");		 
					ButtonSaveExam3.setBounds(5,10,80,30);
					ButtonSaveExam3.addActionListener(this);
					ButtonPrintExam1 = new WebButton("Print");		 
					ButtonPrintExam1.setBounds(5,50,80,30);
					ButtonPrintExam1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							PrintOPD(formcode,"2",page,form_clinic_name);
							
						}
					});
					
					Button432 = new WebButton("To 43");		 
					Button432.setBounds(5,90,80,30);
					Button432.addActionListener(this);
					if(oUserInfo.GetPtVN().equals("")){
						right1.add(ButtonSaveExam3);
						right1.add(ButtonPrintExam1);
						right1.add(Button432); 
					}else{
						 
					}
					right1.revalidate();
					right1.repaint();
					RightSection.revalidate();
					RightSection.repaint();
				}
			
			}
		}
		else if(formcode.equals("00112")){
			if(oUserInfo.GetPtHN().length() !=7){
				JOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",JOptionPane.ERROR_MESSAGE);

			}else{
				checkExam(oUserInfo.GetPtHN());
				//right Section
				right1.removeAll();
				
				
				if(oUserInfo.GetPtHN().length() !=7){
					JOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",JOptionPane.ERROR_MESSAGE);

				}else{
					mid2.removeAll();
					checkExam(oUserInfo.GetPtHN());
					
					Panel43 = new WebPanel();
					Panel43.setPreferredSize(new Dimension((width*2)/10, 800));
					Panel43.setLayout(null);
					
					JLabel ptType,ptEdu,ptEdulevel,ptExam1,ptExam2,ptExam3,ptExam4;
					String [] ptTypeB={"หญิงตั้งครรภ์","เด็กก่อนวัยเรียน","เด็กวัยเรียน","ผู้สูงอายุ","กลุ่มอื่นๆ"};
					String [] ptEduB={"ศพด.","ประถม-รัฐบาล","ประถม-เทศบาล","ประถม-ท้องถิ่น","ประถม-เอกชน","มัธยม-รัฐบาล","มัธยม-เทศบาล","มัธยม-ท้องถิ่น","มัธยม-เอกชน","ไม่เลือก"};
					String [] ptEdulevelB={"ศพด.1-3","ประถม1-6","มัธยม1-6"};
					
					ptType=new JLabel("ประเภท ",JLabel.LEFT);
					ptType.setBounds(5,1,150,20);
					ptType.setFont(new Font("Tahoma", Font.PLAIN, 13));
					Panel43.add(ptType);
					int l_ptTypeB=ptTypeB.length;
					ptTypeBButton=new JRadioButton [l_ptTypeB];
					groupTypeB = new ButtonGroup();
					for(int row=0;row<l_ptTypeB;row++){
						int x=30;
						ptTypeBButton[row] = new JRadioButton(ptTypeB[row]);
						ptTypeBButton[row].setBounds(x+(row*130),20,120,20);
						ptTypeBButton[row].setBackground(new Color(207,229,233));
						ptTypeBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptTypeBButton[row].setActionCommand( ptTypeBButton[row].getText() );
						ptTypeBButton[row].addActionListener(this); 
						Panel43.add(ptTypeBButton[row]);
						groupTypeB.add(ptTypeBButton[row]);
					}
					//
					ptEdu=new JLabel("สถานศึกษา (กรณีเด็กวัยเรียน) ",JLabel.LEFT);
					ptEdu.setBounds(5,45,200,20);
					ptEdu.setFont(new Font("Tahoma", Font.PLAIN, 13));
					Panel43.add(ptEdu);
					
					int l_ptEduB=ptEduB.length;
					ptEduBButton=new JRadioButton [l_ptEduB];
					groupEduB = new ButtonGroup();
					for(int row=0;row<10;row++){
						if(row<5){
						int x=30;
						 
						ptEduBButton[row] = new JRadioButton(ptEduB[row]);
						ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
						ptEduBButton[row].addActionListener(this);
						ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptEduBButton[row].setBounds(x+(row*130),70,120,20);
						ptEduBButton[row].setBackground(new Color(207,229,233));
						if(checkAge()==3){
							ptEduBButton[row].setEnabled(true);
						}else{
							ptEduBButton[row].setEnabled(false);
						}
						Panel43.add(ptEduBButton[row]);
						groupEduB.add(ptEduBButton[row]);
						}else if(row>4 && row<10){
							int x=30;
							 
							ptEduBButton[row] = new JRadioButton(ptEduB[row]);
							ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
							ptEduBButton[row].addActionListener(this);
							ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
							ptEduBButton[row].setBounds(x+((row-5)*130),100,120,20);
							ptEduBButton[row].setBackground(new Color(207,229,233));
							if(checkAge()==3){
								ptEduBButton[row].setEnabled(true);
							}else{
								ptEduBButton[row].setEnabled(false);
							}
							Panel43.add(ptEduBButton[row]);
							groupEduB.add(ptEduBButton[row]);
						}
					}
					//
					ptEdulevel=new JLabel("ระดับการศึกษา (กรณีเด็กวัยเรียน) ",JLabel.LEFT);
					ptEdulevel.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptEdulevel.setBounds(5,140,250,20);				
					Panel43.add(ptEdulevel);
									 
					Border textfield_b = new SoftBevelBorder(BevelBorder.LOWERED);
					TextField_EdulevelB = new WebTextField();
					TextField_EdulevelB.setFont(new Font("Tahoma", Font.PLAIN, 13));
					TextField_EdulevelB.setBorder(textfield_b);
					TextField_EdulevelB.setBounds(200,140,50,20);
					TextField_EdulevelB.setText("");
					TextField_EdulevelB.addActionListener(this);
					Panel43.add(TextField_EdulevelB);
					//
					ptExam1=new JLabel("1.การตรวจในช่องปาก ",JLabel.LEFT);
					ptExam1.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam1.setBounds(5,180,200,20);
					Panel43.add(ptExam1);
					
					columnNamesExam1 = new Vector<String>();
					
					for(int c=0;c<16;c++){
						columnNamesExam1.add("");
					}
					
					Vector<Vector<String>> dataExam1 = new Vector<Vector<String>>();
					final Vector<String> dataExam = new Vector<String>();
					final Vector<String> dataExam2 = new Vector<String>();
					
					String sql_432 = "select * from dhis where  hn= '"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
					Connection conn43;
					PreparedStatement stmt43,stmt433;
					try {
						conn43 = new DBmanager().getConnMySql(); 
						stmt43 = conn43.prepareStatement(sql_432);
						 
						ResultSet rs43 = stmt43.executeQuery();
					 
						while (rs43.next()) {
							if(rs43.getString(6) ==null){
								//ptTypeBButton[4].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("1")){
								ptTypeBButton[0].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("2")){
								ptTypeBButton[1].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("3")){
								ptTypeBButton[2].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("4")){
								ptTypeBButton[3].setSelected(true);
							}
							else if(rs43.getString(6).trim().equals("5")){
								ptTypeBButton[4].setSelected(true);
							}
							if(rs43.getString(7) ==null || rs43.getString(7).trim().equals("0")){
								 
							}
							else if(rs43.getString(7).trim().equals("1")){
								ptEduBButton[0].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("2")){
								ptEduBButton[1].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("3")){
								ptEduBButton[2].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("4")){
								ptEduBButton[3].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("5")){
								ptEduBButton[4].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("6")){
								ptEduBButton[5].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("7")){
								ptEduBButton[6].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("8")){
								ptEduBButton[7].setSelected(true);
							}
							else if(rs43.getString(7).trim().equals("9")){
								ptEduBButton[8].setSelected(true);
							}
					
							if(rs43.getString(8) ==null || rs43.getString(8).trim().equals("0")){
								TextField_EdulevelB.setText("");
							}
							else  if(rs43.getString(8) !=null){
								TextField_EdulevelB.setText(rs43.getString(8).trim());
							}
								
								
							for(int r=9;r<25;r++){
								//System.out.println(rs43.getString(r));
								if(rs43.getString(r) ==null){
									dataExam.add("");
								}							
								else if(rs43.getString(r) !=null){
									dataExam.add(rs43.getString(r).trim());
									//System.out.println(rs43.getString(r).trim());
								}							 
							}
							dataExam1.add(dataExam); 					 					 
						}
						stmt43.close(); 
						stmt433 = conn43.prepareStatement(sql_432);
						ResultSet rs433 = stmt433.executeQuery();
						while (rs433.next()) {						
							for(int r=25;r<41;r++){
								if(rs433.getString(r) ==null){
									dataExam.add("");
								}
								else if(rs433.getString(r) !=null){
									dataExam2.add(rs433.getString(r).trim());
									//System.out.println(rs433.getString(r).trim());
								} 
							}
							dataExam1.add(dataExam2); 					 					 
						}
						stmt433.close();					 
						conn43.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DefaultTableModel modelExam1 = new DefaultTableModel(dataExam1, columnNamesExam1){
						public Class getColumnClass(int column){
							return getValueAt(0, column).getClass();
						}
					};
					tableExam1 = new WebTable(modelExam1){
						public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
							JLabel c = (JLabel) super.prepareRenderer(renderer, row, column);
							c.setBackground(Color.WHITE);
							c.setForeground(Color.BLACK);
							c.setHorizontalAlignment(JLabel.CENTER);
					        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
					        return c;
						}
					};
					TableColumnModel columnModelExam1 = tableExam1.getColumnModel();
				    for(int cl=0;cl<16;cl++){
				    	columnModelExam1.getColumn(cl).setPreferredWidth(35);
					}
				    tableExam1.setPreferredScrollableViewportSize(new Dimension(560, 70));
					tableExam1.setFillsViewportHeight(true);
					tableExam1.setEnabled(true);
					tableExam1.setRowHeight(35);
					tableExam1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					tableExam1.setBounds(40,260,560,70);
					//tableExam1.addMouseListener(this);
					Panel43.add(tableExam1);
					//
					String [] tooth_dUP={"55","54","53","52","51","61","62","63","64","65"};
					String [] tooth_dLOW={"85","84","83","82","81","71","72","73","74","75"};
					int l_tooth_dUP=tooth_dUP.length;
					int l_tooth_dLOW=tooth_dLOW.length;
					
					 JLabel[] ltooth_dUP=new JLabel[l_tooth_dUP];
					 for(int row_tooth_dUP=0;row_tooth_dUP<l_tooth_dUP;row_tooth_dUP++){
						 int x_tooth_dUP=155;
						 ltooth_dUP[row_tooth_dUP] = new JLabel(tooth_dUP[row_tooth_dUP],JLabel.CENTER);		 
						 ltooth_dUP[row_tooth_dUP].setBounds(x_tooth_dUP+(row_tooth_dUP*35),225,20,20);
						 Panel43.add(ltooth_dUP[row_tooth_dUP]);
					 }
					  
					 JLabel[] ltooth_dLOW=new JLabel[l_tooth_dLOW];
					 for(int row_tooth_dLOW=0;row_tooth_dLOW<l_tooth_dLOW;row_tooth_dLOW++){
						 int x_tooth_dLOW=155;
						 ltooth_dLOW[row_tooth_dLOW] = new JLabel(tooth_dLOW[row_tooth_dLOW],JLabel.CENTER);		 
						 ltooth_dLOW[row_tooth_dLOW].setBounds(x_tooth_dLOW+(row_tooth_dLOW*35),350,20,20);
						 Panel43.add(ltooth_dLOW[row_tooth_dLOW]);
					 }
					 String [] tooth_pUP={"18","17","16","15","14","13","12","11","21","22","23","24","25","26","27","28"};
					 String [] tooth_pLOW={"48","47","46","45","44","43","42","41","31","32","33","34","35","36","37","38"};
					 int l_tooth_pUP=tooth_pUP.length;
					 int l_tooth_pLOW=tooth_pLOW.length;
					 
					 JLabel[] ltooth_pUP=new JLabel[l_tooth_pUP];
					 for(int row_tooth_pUP=0;row_tooth_pUP<l_tooth_pUP;row_tooth_pUP++){
						 int x_tooth_pUP=50;
						 ltooth_pUP[row_tooth_pUP] = new JLabel(tooth_pUP[row_tooth_pUP],JLabel.CENTER);		 
						 ltooth_pUP[row_tooth_pUP].setBounds(x_tooth_pUP+(row_tooth_pUP*35),240,20,20);
						 Panel43.add(ltooth_pUP[row_tooth_pUP]);
					 }
					 JLabel[] ltooth_pLOW=new JLabel[l_tooth_pLOW];
					 for(int row_tooth_pLOW=0;row_tooth_pLOW<l_tooth_pLOW;row_tooth_pLOW++){
						 int x_tooth_pLOW=50;
						 ltooth_pLOW[row_tooth_pLOW] = new JLabel(tooth_pLOW[row_tooth_pLOW],JLabel.CENTER);		 
						 ltooth_pLOW[row_tooth_pLOW].setBounds(x_tooth_pLOW+(row_tooth_pLOW*35),335,20,20);
						 Panel43.add(ltooth_pLOW[row_tooth_pLOW]);
						 
					 }
					//end sec1
					 
					 JLabel ptExam201,ptExam301,ptExam401,ptExam31,ptExam32,ptExam33,ptExam41,ptExam42,ptExam43,provider;
						ptExam201=new JLabel("2.สภาวะปริทันต์ ",JLabel.LEFT);
						ptExam201.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam201.setBounds(15,380,200,20);
						Panel43.add(ptExam201);
						
						columnNamesExam2 = new Vector<String>();			
						columnNamesExam2.add("");
						columnNamesExam2.add("");
						columnNamesExam2.add("");
						Vector<Vector<String>> dataExam201 = new Vector<Vector<String>>();
						final Vector<String> dataExam301 = new Vector<String>();
						final Vector<String> dataExam401 = new Vector<String>();
						String [] pLabel_UP={"ฟันหลังบนขวา","ฟันหน้าบน","ฟันหลังบนซ้าย"};
						String [] pLabel_LOW={"ฟันหลังล่างขวา","ฟันหน้าล่าง","ฟันหลังล่างซ้าย"};
						int l_pLabel_UP=pLabel_UP.length;
						int l_pLabel_LOW=pLabel_LOW.length;
						JLabel[] p_UP=new JLabel[l_pLabel_UP];
						 for(int row_p_UP=0;row_p_UP<l_pLabel_UP;row_p_UP++){
							 int x_UP=50;
							 p_UP[row_p_UP] = new JLabel(pLabel_UP[row_p_UP],JLabel.CENTER);		 
							 p_UP[row_p_UP].setBounds(x_UP+(row_p_UP*140),410,100,20);
							 Panel43.add(p_UP[row_p_UP]);
						 }
						  
						 JLabel[] p_LOW=new JLabel[l_pLabel_LOW];
						 for(int row_p_LOW=0;row_p_LOW<l_pLabel_LOW;row_p_LOW++){
							 int x_tooth_dLOW=50;
							 p_LOW[row_p_LOW] = new JLabel(pLabel_LOW[row_p_LOW],JLabel.CENTER);		 
							 p_LOW[row_p_LOW].setBounds(x_tooth_dLOW+(row_p_LOW*140),520,100,20);
							 Panel43.add(p_LOW[row_p_LOW]);
						 }
						
						
						ptExam3=new JLabel("3.การสบฟัน",JLabel.LEFT);
						ptExam3.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam3.setBounds(15,550,180,20);
						Panel43.add(ptExam3);
						
						ptExam31=new JLabel("1.จำนวนคู่สบฟันแท้กับฟันแท้ ",JLabel.LEFT);
						ptExam31.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam31.setBounds(25,570,350,20);				
						Panel43.add(ptExam31);
						
						TextField_occ1 = new WebTextField();
						TextField_occ1.setBounds(400,570,50,20);
						TextField_occ1.setText("0");
						TextField_occ1.addActionListener(this);
						Panel43.add(TextField_occ1);
						
						ptExam32=new JLabel("2.จำนวนคู่สบฟันแท้กับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",JLabel.LEFT);
						ptExam32.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam32.setBounds(25,590,350,20);					
						Panel43.add(ptExam32);
						
						TextField_occ2 = new WebTextField();
						TextField_occ2.setBounds(400,590,50,20);
						TextField_occ2.addActionListener(this);
						TextField_occ2.setEnabled(false);
						TextField_occ2.setText("0");
						if(checkAge()==4){
							TextField_occ2.setEnabled(true);
							TextField_occ2.setText("");					 
						}
						Panel43.add(TextField_occ2);
						
						ptExam33=new JLabel("3.จำนวนคู่สบฟันเทียมกับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",JLabel.LEFT);
						ptExam33.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam33.setBounds(25,610,350,20);					
						Panel43.add(ptExam33);
						
						TextField_occ3 = new WebTextField();
						TextField_occ3.setBounds(400,610,50,20);
						TextField_occ3.addActionListener(this);
						TextField_occ3.setEnabled(false);
						TextField_occ3.setText("0");
						if(checkAge()==4){
							TextField_occ3.setEnabled(true);
							TextField_occ3.setText("");
						}
						Panel43.add(TextField_occ3);
						
						ptExam4=new JLabel("4.บริการที่ควรได้รับ ",JLabel.LEFT);
						ptExam4.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam4.setBounds(15,635,180,20);					
						Panel43.add(ptExam4);
						
						ptExam41=new JLabel("1.การทาฟูลออไรด์",JLabel.LEFT);
						ptExam41.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam41.setBounds(25,660,100,20);					
						Panel43.add(ptExam41);
						
						FNButton=new JRadioButton [2];
						groupFN = new ButtonGroup();
						for(int row=0;row<2;row++){
							int x=140;
							if(row==1){
								FNButton[row] = new JRadioButton("ไม่จำเป็นf");						  
							 }else{
								 FNButton[row] = new JRadioButton("จำเป็นf");
							 }				 
							FNButton[row].setActionCommand( FNButton[row].getText() );
							FNButton[row].addActionListener(this);				
							FNButton[row].setBounds(x+(row*100),660,100,20);
							FNButton[row].setBackground(new Color(207,229,233));					
							Panel43.add(FNButton[row]);
							groupFN.add(FNButton[row]);				 
						}
						
						ptExam42=new JLabel("2.การขูดหินน้ำลาย   ",JLabel.LEFT);
						ptExam42.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam42.setBounds(25,690,250,20);				
						Panel43.add(ptExam42);
						
						SNButton=new JRadioButton [2];
						groupSN = new ButtonGroup();
						for(int row=0;row<2;row++){
							int x=140;
							if(row==1){
								SNButton[row] = new JRadioButton("ไม่จำเป็นs");					  
							 }else{
								 SNButton[row] = new JRadioButton("จำเป็นs");
							 }			
							SNButton[row].setActionCommand( SNButton[row].getText() );
							SNButton[row].addActionListener(this);					 				
							SNButton[row].setBounds(x+(row*100),690,100,20);
							SNButton[row].setBackground(new Color(207,229,233));										
							Panel43.add(SNButton[row]);
							groupSN.add(SNButton[row]);				 
						}
						
						ptExam43=new JLabel("3.การใส่ฟันเทียม ",JLabel.LEFT);
						ptExam43.setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptExam43.setBounds(25,720,250,20);					
						Panel43.add(ptExam43);
						
						String [] prosNeed={"ต้องใส่ฟันบนและล่าง","ต้องใส่ฟันบน","ต้องใส่ฟันล่าง","ไม่ต้องใส่ฟันเทียม"};
						int l_prosNeed = prosNeed.length;			
						ProsNButton=new JRadioButton [l_prosNeed];
						groupProsN = new ButtonGroup();
						for(int row=0;row<l_prosNeed;row++){
							int x=140;
							if(row==3){
								ProsNButton[row] = new JRadioButton(prosNeed[row]);						  
							 }else{
								ProsNButton[row] = new JRadioButton(prosNeed[row]);
							 }					 
							ProsNButton[row].setActionCommand( ProsNButton[row].getText() );
							ProsNButton[row].addActionListener(this);					 				
							ProsNButton[row].setBounds(x+(row*120),720,120,20);
							ProsNButton[row].setBackground(new Color(207,229,233));										
							Panel43.add(ProsNButton[row]);
							groupProsN.add(ProsNButton[row]);				 
						}
						provider=new JLabel("ผู้ตรวจ  ",JLabel.LEFT);
						provider.setFont(new Font("Tahoma", Font.PLAIN, 13));
						provider.setBounds(25,750,300,20);					
						Panel43.add(provider);
						
						TextField_provider = new WebTextField();
						TextField_provider.setBounds(150,750,50,20);
						TextField_provider.setText("");
						TextField_provider.addActionListener(this);
						Panel43.add(TextField_provider);
						
						String sql_43 = "select perio,occpetope,occpetopr,occprtopr,fluneed,sneed,prosneed,provider  from dhis where  hn= '"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
						Connection conn431;
						PreparedStatement stmt431;
						try {
							conn431 = new DBmanager().getConnMySql();	 
							stmt431 = conn431.prepareStatement(sql_43);	
							ResultSet rs43 = stmt431.executeQuery();
							while (rs43.next()) {
								if(rs43.getString(1) ==null){
									String [] dex4={"","",""};
									for(int r=0;r<dex4.length;r++){
										dataExam301.add(dex4[r]);
									}			           
						            dataExam201.add(dataExam301); 
						            String [] dex5={"","",""};
									for(int r=0;r<dex5.length;r++){
										dataExam401.add(dex5[r]);
									}
						            dataExam201.add(dataExam401); 
								}
								else if(rs43.getString(1) !=null ){
									if( rs43.getString(1).equals("")) {
										String [] dex4={"","",""};
										for(int r=0;r<dex4.length;r++){
											dataExam301.add(dex4[r]);
										}			           
							            dataExam201.add(dataExam301); 
							            String [] dex5={"","",""};
										for(int r=0;r<dex5.length;r++){
											dataExam401.add(dex5[r]);
										}
							            dataExam201.add(dataExam401); 
									}else {
										String perio_text=rs43.getString(1).trim(); 	
										char[] perioArray = perio_text.toCharArray();
										for(int r=0;r<3;r++){
											dataExam301.add(String.valueOf(perioArray[r]));
										}
										dataExam201.add(dataExam301);
										for(int r=3;r<6;r++){
											dataExam401.add(String.valueOf(perioArray[r]));
										}
										dataExam201.add(dataExam401);
									}
									 
								}
							
								if(rs43.getString(2) ==null){							
								}
								else if(rs43.getString(2) !=null){
									TextField_occ1.setText(rs43.getString(2).trim());
								}						
								if(rs43.getString(3) ==null){							
								}
								else if(rs43.getString(3) !=null){
									TextField_occ2.setText(rs43.getString(3).trim());
								}
								if(rs43.getString(4) ==null){							
								}
								else if(rs43.getString(4) !=null){
									TextField_occ3.setText(rs43.getString(4).trim());
								}
								if(rs43.getString(5) ==null){						
								}
								else if(rs43.getString(5) !=null){
									if(rs43.getString(5).trim().equals("1")){
										FNButton[0].setSelected(true);
									}
									else if(rs43.getString(5).trim().equals("2")){
										FNButton[1].setSelected(true);
									}						 
								}						
								if(rs43.getString(6) ==null){							
								}
								else if(rs43.getString(6) !=null){
									if(rs43.getString(6).trim().equals("1")){
										SNButton[0].setSelected(true);
									}
									else if(rs43.getString(6).trim().equals("2")){
										SNButton[1].setSelected(true);
									}						 
								}						
								if(rs43.getString(7) ==null){						 
								}
								else if(rs43.getString(7) !=null){
									if(rs43.getString(7).trim().equals("1")){
										ProsNButton[0].setSelected(true);
									}
									else if(rs43.getString(7).trim().equals("2")){
										ProsNButton[1].setSelected(true);
									}
									else if(rs43.getString(7).trim().equals("3")){
										ProsNButton[2].setSelected(true);
									}
									else if(rs43.getString(7).trim().equals("4")){
										ProsNButton[3].setSelected(true);
									}							 
								}						
								if(rs43.getString(8) ==null){
									TextField_provider.setText("047");
								}
								else if(rs43.getString(8) !=null){
									TextField_provider.setText(rs43.getString(8).trim());
								}						 					 
							}		 
							stmt431.close(); 
							conn431.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						DefaultTableModel modelExam201 = new DefaultTableModel(dataExam201, columnNamesExam2){
							public Class getColumnClass(int column){
								return getValueAt(0, column).getClass();
							}
						};
						
						tableExam2 = new WebTable(modelExam201){
							public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
								JLabel c = (JLabel) super.prepareRenderer(renderer, row, column);
								c.setBackground(Color.WHITE);
								c.setForeground(Color.BLACK);
								c.setHorizontalAlignment(JLabel.CENTER);
						        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
						        return c;
							}
						};
						TableColumnModel columnModelExam2 = tableExam2.getColumnModel();
						for(int cl2=0;cl2<3;cl2++){
					    	columnModelExam2.getColumn(cl2).setPreferredWidth(35);
						}
					    tableExam2.setPreferredScrollableViewportSize(new Dimension(560, 70));
						tableExam2.setFillsViewportHeight(true);
						tableExam2.setEnabled(true);
						tableExam2.setRowHeight(35);
						tableExam2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
						tableExam2.setBounds(40,440,420,70);
						//tableExam2.addMouseListener(this);
						mid2.add(tableExam2);
						
					
					scrollPane43 = new JScrollPane(Panel43);
					//scrollPane43.setPreferredSize(new Dimension(500,500));
					//scrollPane43.setViewportView(Panel43);
					mid2.add(scrollPane43, BorderLayout.CENTER);
					
					mid2.revalidate();
					mid2.repaint();	
					MiddleSection.revalidate();
					MiddleSection.repaint();
					//right
					right1.removeAll();
					ButtonSaveExam3 = new WebButton("Save");		 
					ButtonSaveExam3.setBounds(5,10,80,30);
					ButtonSaveExam3.addActionListener(this);
					ButtonPrintExam1 = new WebButton("Print");		 
					ButtonPrintExam1.setBounds(5,50,80,30);
					ButtonPrintExam1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							PrintOPD(formcode,"2",page,form_clinic_name);
							
						}
					});
					
					
					Button432 = new WebButton("To 43");		 
					Button432.setBounds(5,90,80,30);
					Button432.addActionListener(this);
					if(oUserInfo.GetPtVN().equals("")){
						right1.add(ButtonSaveExam3);
						right1.add(ButtonPrintExam1);
						right1.add(Button432); 
					}else{
						 
					}
					right1.revalidate();
					right1.repaint();
					RightSection.revalidate();
					RightSection.repaint();
				}
				
			}
		}
		else if(formcode.equals("00120")){
			clearPanel();
			exam_status=0;
			//System.out.println(oUserInfo.GetPtHN());
			if(oUserInfo.GetPtHN().length() !=7){
				WebOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",WebOptionPane.ERROR_MESSAGE);

			}else{
				checkExam(oUserInfo.GetPtHN());
				
				Panel43 = new WebPanel();
				Panel43.setPreferredSize(new Dimension((width*2)/10, 800));
				Panel43.setLayout(null);
				
				WebLabel ptType,ptEdu,ptEdulevel,ptExam1,ptExam2,ptExam3,ptExam4;
				String [] ptTypeB={"หญิงตั้งครรภ์","เด็กก่อนวัยเรียน","เด็กวัยเรียน","ผู้สูงอายุ","กลุ่มอื่นๆ"};
				String [] ptEduB={"ศพด.","ประถม-รัฐบาล","ประถม-เทศบาล","ประถม-ท้องถิ่น","ประถม-เอกชน","มัธยม-รัฐบาล","มัธยม-เทศบาล","มัธยม-ท้องถิ่น","มัธยม-เอกชน","ไม่เลือก"};
				
				ptType=new WebLabel("ประเภท ",WebLabel.LEFT);
				ptType.setBounds(5,1,150,20);
				ptType.setFont(new Font("Tahoma", Font.PLAIN, 13));
				Panel43.add(ptType);
				int l_ptTypeB=ptTypeB.length;
				ptTypeBButton=new JRadioButton [l_ptTypeB];
				groupTypeB = new ButtonGroup();
				for(int row=0;row<l_ptTypeB;row++){
					int x=30;
					ptTypeBButton[row] = new JRadioButton(ptTypeB[row]);
					ptTypeBButton[row].setBounds(x+(row*130),20,120,20);
					ptTypeBButton[row].setBackground(new Color(207,229,233));
					ptTypeBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptTypeBButton[row].setActionCommand( ptTypeBButton[row].getText() );
					ptTypeBButton[row].addActionListener(this); 
					Panel43.add(ptTypeBButton[row]);
					groupTypeB.add(ptTypeBButton[row]);
				}
				//
				ptEdu=new WebLabel("สถานศึกษา (กรณีเด็กวัยเรียน) ",WebLabel.LEFT);
				ptEdu.setBounds(5,45,200,20);
				ptEdu.setFont(new Font("Tahoma", Font.PLAIN, 13));
				Panel43.add(ptEdu);
				
				int l_ptEduB=ptEduB.length;
				ptEduBButton=new JRadioButton [l_ptEduB];
				groupEduB = new ButtonGroup();
				for(int row=0;row<10;row++){
					if(row<5){
					int x=30;
					 
					ptEduBButton[row] = new JRadioButton(ptEduB[row]);
					ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
					ptEduBButton[row].addActionListener(this);
					ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptEduBButton[row].setBounds(x+(row*130),70,120,20);
					ptEduBButton[row].setBackground(new Color(207,229,233));
					if(checkAge()==3){
						ptEduBButton[row].setEnabled(true);
					}else{
						ptEduBButton[row].setEnabled(false);
					}
					Panel43.add(ptEduBButton[row]);
					groupEduB.add(ptEduBButton[row]);
					}else if(row>4 && row<10){
						int x=30;
						 
						ptEduBButton[row] = new JRadioButton(ptEduB[row]);
						ptEduBButton[row].setActionCommand( ptEduBButton[row].getText() );
						ptEduBButton[row].addActionListener(this);
						ptEduBButton[row].setFont(new Font("Tahoma", Font.PLAIN, 13));
						ptEduBButton[row].setBounds(x+((row-5)*130),100,120,20);
						ptEduBButton[row].setBackground(new Color(207,229,233));
						if(checkAge()==3){
							ptEduBButton[row].setEnabled(true);
						}else{
							ptEduBButton[row].setEnabled(false);
						}
						Panel43.add(ptEduBButton[row]);
						groupEduB.add(ptEduBButton[row]);
					}
				}
				//
				ptEdulevel=new WebLabel("ระดับการศึกษา (กรณีเด็กวัยเรียน) ",WebLabel.LEFT);
				ptEdulevel.setFont(new Font("Tahoma", Font.PLAIN, 13));
				ptEdulevel.setBounds(5,140,250,20);				
				Panel43.add(ptEdulevel);
								 
				Border textfield_b = new SoftBevelBorder(BevelBorder.LOWERED);
				TextField_EdulevelB = new WebTextField();
				TextField_EdulevelB.setFont(new Font("Tahoma", Font.PLAIN, 13));
				TextField_EdulevelB.setBorder(textfield_b);
				TextField_EdulevelB.setBounds(200,140,50,20);
				TextField_EdulevelB.setText("");
				if(checkAge()==3){
					TextField_EdulevelB.setEnabled(true);
				}else{
					TextField_EdulevelB.setEnabled(false);
				}
				TextField_EdulevelB.addActionListener(this);
				Panel43.add(TextField_EdulevelB);
				//
				ptExam1=new WebLabel("1.การตรวจในช่องปาก ",WebLabel.LEFT);
				ptExam1.setFont(new Font("Tahoma", Font.PLAIN, 13));
				ptExam1.setBounds(5,180,200,20);
				Panel43.add(ptExam1);
				
				columnNamesExam1 = new Vector<String>();
				
				for(int c=0;c<16;c++){
					columnNamesExam1.add("");
				}
				
				Vector<Vector<String>> dataExam1 = new Vector<Vector<String>>();
				final Vector<String> dataExam = new Vector<String>();
				final Vector<String> dataExam2 = new Vector<String>();
				
				String sql_432 = "select * from dhis where  hn= '"+oUserInfo.GetPtHN()+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
				//String sql_432 = "select * from dhis where  hn= '"+hn+"' order by visitdate desc limit 1";

				Connection conn43;
				PreparedStatement stmt43,stmt433;
				try {
					conn43 = new DBmanager().getConnMySql(); 
					stmt43 = conn43.prepareStatement(sql_432);
					 
					ResultSet rs43 = stmt43.executeQuery();
				 
					while (rs43.next()) {
						if(rs43.getString(6) ==null){
							//ptTypeBButton[4].setSelected(true);
						}
						else if(rs43.getString(6).trim().equals("1")){
							ptTypeBButton[0].setSelected(true);
						}
						else if(rs43.getString(6).trim().equals("2")){
							ptTypeBButton[1].setSelected(true);
						}
						else if(rs43.getString(6).trim().equals("3")){
							ptTypeBButton[2].setSelected(true);
						}
						else if(rs43.getString(6).trim().equals("4")){
							ptTypeBButton[3].setSelected(true);
						}
						else if(rs43.getString(6).trim().equals("5")){
							ptTypeBButton[4].setSelected(true);
						}
						if(rs43.getString(7) ==null || rs43.getString(7).trim().equals("0")){
							 
						}
						else if(rs43.getString(7).trim().equals("1")){
							ptEduBButton[0].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("2")){
							ptEduBButton[1].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("3")){
							ptEduBButton[2].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("4")){
							ptEduBButton[3].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("5")){
							ptEduBButton[4].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("6")){
							ptEduBButton[5].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("7")){
							ptEduBButton[6].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("8")){
							ptEduBButton[7].setSelected(true);
						}
						else if(rs43.getString(7).trim().equals("9")){
							ptEduBButton[8].setSelected(true);
						}
				
						if(rs43.getString(8) ==null || rs43.getString(8).trim().equals("0")){
							TextField_EdulevelB.setText("");
						}
						else  if(rs43.getString(8) !=null){
							TextField_EdulevelB.setText(rs43.getString(8).trim());
						}
							
							
						for(int r=9;r<25;r++){
							//System.out.println(rs43.getString(r));
							if(rs43.getString(r) ==null){
								dataExam.add("");
							}							
							else if(rs43.getString(r) !=null){
								dataExam.add(rs43.getString(r).trim());
								//System.out.println(rs43.getString(r).trim());
							}							 
						}
						dataExam1.add(dataExam); 					 					 
					}
					stmt43.close(); 
					stmt433 = conn43.prepareStatement(sql_432);
					ResultSet rs433 = stmt433.executeQuery();
					while (rs433.next()) {						
						for(int r=25;r<41;r++){
							if(rs433.getString(r) ==null){
								dataExam2.add("");
							}
							else if(rs433.getString(r) !=null){
								dataExam2.add(rs433.getString(r).trim());
								//System.out.println(rs433.getString(r).trim());
							} 
						}
						dataExam1.add(dataExam2); 					 					 
					}
					stmt433.close();					 
					conn43.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DefaultTableModel modelExam1 = new DefaultTableModel(dataExam1, columnNamesExam1){
					public Class getColumnClass(int column){
						return getValueAt(0, column).getClass();
					}
				};
				tableExam1 = new WebTable(modelExam1){
					public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
						WebLabel c = (WebLabel) super.prepareRenderer(renderer, row, column);
						c.setBackground(Color.WHITE);
						c.setForeground(Color.BLACK);
						c.setHorizontalAlignment(WebLabel.CENTER);
				        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
				        return c;
					}
				};
				TableColumnModel columnModelExam1 = tableExam1.getColumnModel();
			    for(int cl=0;cl<16;cl++){
			    	columnModelExam1.getColumn(cl).setPreferredWidth(35);
				}
			    tableExam1.setPreferredScrollableViewportSize(new Dimension(560, 70));
				tableExam1.setFillsViewportHeight(true);
				tableExam1.setEnabled(true);
				tableExam1.setRowHeight(35);
				tableExam1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				tableExam1.setBounds(40,260,560,70);
				//tableExam1.addMouseListener(this);
				Panel43.add(tableExam1);
				//
				String [] tooth_dUP={"55","54","53","52","51","61","62","63","64","65"};
				String [] tooth_dLOW={"85","84","83","82","81","71","72","73","74","75"};
				int l_tooth_dUP=tooth_dUP.length;
				int l_tooth_dLOW=tooth_dLOW.length;
				
				 WebLabel[] ltooth_dUP=new WebLabel[l_tooth_dUP];
				 for(int row_tooth_dUP=0;row_tooth_dUP<l_tooth_dUP;row_tooth_dUP++){
					 int x_tooth_dUP=155;
					 ltooth_dUP[row_tooth_dUP] = new WebLabel(tooth_dUP[row_tooth_dUP],WebLabel.CENTER);		 
					 ltooth_dUP[row_tooth_dUP].setBounds(x_tooth_dUP+(row_tooth_dUP*35),225,20,20);
					 Panel43.add(ltooth_dUP[row_tooth_dUP]);
				 }
				  
				 WebLabel[] ltooth_dLOW=new WebLabel[l_tooth_dLOW];
				 for(int row_tooth_dLOW=0;row_tooth_dLOW<l_tooth_dLOW;row_tooth_dLOW++){
					 int x_tooth_dLOW=155;
					 ltooth_dLOW[row_tooth_dLOW] = new WebLabel(tooth_dLOW[row_tooth_dLOW],WebLabel.CENTER);		 
					 ltooth_dLOW[row_tooth_dLOW].setBounds(x_tooth_dLOW+(row_tooth_dLOW*35),350,20,20);
					 Panel43.add(ltooth_dLOW[row_tooth_dLOW]);
				 }
				 String [] tooth_pUP={"18","17","16","15","14","13","12","11","21","22","23","24","25","26","27","28"};
				 String [] tooth_pLOW={"48","47","46","45","44","43","42","41","31","32","33","34","35","36","37","38"};
				 int l_tooth_pUP=tooth_pUP.length;
				 int l_tooth_pLOW=tooth_pLOW.length;
				 
				 WebLabel[] ltooth_pUP=new WebLabel[l_tooth_pUP];
				 for(int row_tooth_pUP=0;row_tooth_pUP<l_tooth_pUP;row_tooth_pUP++){
					 int x_tooth_pUP=50;
					 ltooth_pUP[row_tooth_pUP] = new WebLabel(tooth_pUP[row_tooth_pUP],WebLabel.CENTER);		 
					 ltooth_pUP[row_tooth_pUP].setBounds(x_tooth_pUP+(row_tooth_pUP*35),240,20,20);
					 Panel43.add(ltooth_pUP[row_tooth_pUP]);
				 }
				 WebLabel[] ltooth_pLOW=new WebLabel[l_tooth_pLOW];
				 for(int row_tooth_pLOW=0;row_tooth_pLOW<l_tooth_pLOW;row_tooth_pLOW++){
					 int x_tooth_pLOW=50;
					 ltooth_pLOW[row_tooth_pLOW] = new WebLabel(tooth_pLOW[row_tooth_pLOW],WebLabel.CENTER);		 
					 ltooth_pLOW[row_tooth_pLOW].setBounds(x_tooth_pLOW+(row_tooth_pLOW*35),335,20,20);
					 Panel43.add(ltooth_pLOW[row_tooth_pLOW]);
					 
				 }
				//end sec1
				 
				 WebLabel ptExam201,ptExam301,ptExam401,ptExam31,ptExam32,ptExam33,ptExam41,ptExam42,ptExam43,provider;
					ptExam201=new WebLabel("2.สภาวะปริทันต์ ",WebLabel.LEFT);
					ptExam201.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam201.setBounds(15,380,200,20);
					Panel43.add(ptExam201);
					
					columnNamesExam2 = new Vector<String>();			
					columnNamesExam2.add("");
					columnNamesExam2.add("");
					columnNamesExam2.add("");
					Vector<Vector<String>> dataExam201 = new Vector<Vector<String>>();
					final Vector<String> dataExam301 = new Vector<String>();
					final Vector<String> dataExam401 = new Vector<String>();
					String [] pLabel_UP={"ฟันหลังบนขวา","ฟันหน้าบน","ฟันหลังบนซ้าย"};
					String [] pLabel_LOW={"ฟันหลังล่างขวา","ฟันหน้าล่าง","ฟันหลังล่างซ้าย"};
					int l_pLabel_UP=pLabel_UP.length;
					int l_pLabel_LOW=pLabel_LOW.length;
					WebLabel[] p_UP=new WebLabel[l_pLabel_UP];
					 for(int row_p_UP=0;row_p_UP<l_pLabel_UP;row_p_UP++){
						 int x_UP=50;
						 p_UP[row_p_UP] = new WebLabel(pLabel_UP[row_p_UP],WebLabel.CENTER);		 
						 p_UP[row_p_UP].setBounds(x_UP+(row_p_UP*140),410,100,20);
						 Panel43.add(p_UP[row_p_UP]);
					 }
					  
					 WebLabel[] p_LOW=new WebLabel[l_pLabel_LOW];
					 for(int row_p_LOW=0;row_p_LOW<l_pLabel_LOW;row_p_LOW++){
						 int x_tooth_dLOW=50;
						 p_LOW[row_p_LOW] = new WebLabel(pLabel_LOW[row_p_LOW],WebLabel.CENTER);		 
						 p_LOW[row_p_LOW].setBounds(x_tooth_dLOW+(row_p_LOW*140),520,100,20);
						 Panel43.add(p_LOW[row_p_LOW]);
					 }
					
					
					ptExam3=new WebLabel("3.การสบฟัน",WebLabel.LEFT);
					ptExam3.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam3.setBounds(15,550,180,20);
					Panel43.add(ptExam3);
					
					ptExam31=new WebLabel("1.จำนวนคู่สบฟันแท้กับฟันแท้ ",WebLabel.LEFT);
					ptExam31.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam31.setBounds(25,570,350,20);				
					Panel43.add(ptExam31);
					
					TextField_occ1 = new WebTextField();
					TextField_occ1.setBounds(400,570,50,20);
					TextField_occ1.setText("0");
					TextField_occ1.addActionListener(this);
					Panel43.add(TextField_occ1);
					
					ptExam32=new WebLabel("2.จำนวนคู่สบฟันแท้กับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",WebLabel.LEFT);
					ptExam32.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam32.setBounds(25,590,350,20);					
					Panel43.add(ptExam32);
					
					TextField_occ2 = new WebTextField();
					TextField_occ2.setBounds(400,590,50,20);
					TextField_occ2.addActionListener(this);
					TextField_occ2.setEnabled(false);
					TextField_occ2.setText("0");
					if(checkAge()==4){
						TextField_occ2.setEnabled(true);
						TextField_occ2.setText("");					 
					}
					Panel43.add(TextField_occ2);
					
					ptExam33=new WebLabel("3.จำนวนคู่สบฟันเทียมกับฟันเทียม (เฉพาะกลุ่มที่มีอายุ >= 60 ปี)",WebLabel.LEFT);
					ptExam33.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam33.setBounds(25,610,350,20);					
					Panel43.add(ptExam33);
					
					TextField_occ3 = new WebTextField();
					TextField_occ3.setBounds(400,610,50,20);
					TextField_occ3.addActionListener(this);
					TextField_occ3.setEnabled(false);
					TextField_occ3.setText("0");
					if(checkAge()==4){
						TextField_occ3.setEnabled(true);
						TextField_occ3.setText("");
					}
					Panel43.add(TextField_occ3);
					
					ptExam4=new WebLabel("4.บริการที่ควรได้รับ ",WebLabel.LEFT);
					ptExam4.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam4.setBounds(15,635,180,20);					
					Panel43.add(ptExam4);
					
					ptExam41=new WebLabel("1.การทาฟูลออไรด์",WebLabel.LEFT);
					ptExam41.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam41.setBounds(25,660,100,20);					
					Panel43.add(ptExam41);
					
					FNButton=new JRadioButton [2];
					groupFN = new ButtonGroup();
					for(int row=0;row<2;row++){
						int x=140;
						if(row==1){
							FNButton[row] = new JRadioButton("ไม่จำเป็นf");						  
						 }else{
							 FNButton[row] = new JRadioButton("จำเป็นf");
						 }				 
						FNButton[row].setActionCommand( FNButton[row].getText() );
						FNButton[row].addActionListener(this);				
						FNButton[row].setBounds(x+(row*100),660,100,20);
						FNButton[row].setBackground(new Color(207,229,233));					
						Panel43.add(FNButton[row]);
						groupFN.add(FNButton[row]);				 
					}
					
					ptExam42=new WebLabel("2.การขูดหินน้ำลาย   ",WebLabel.LEFT);
					ptExam42.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam42.setBounds(25,690,250,20);				
					Panel43.add(ptExam42);
					
					SNButton=new JRadioButton [2];
					groupSN = new ButtonGroup();
					for(int row=0;row<2;row++){
						int x=140;
						if(row==1){
							SNButton[row] = new JRadioButton("ไม่จำเป็นs");					  
						 }else{
							 SNButton[row] = new JRadioButton("จำเป็นs");
						 }			
						SNButton[row].setActionCommand( SNButton[row].getText() );
						SNButton[row].addActionListener(this);					 				
						SNButton[row].setBounds(x+(row*100),690,100,20);
						SNButton[row].setBackground(new Color(207,229,233));										
						Panel43.add(SNButton[row]);
						groupSN.add(SNButton[row]);				 
					}
					
					ptExam43=new WebLabel("3.การใส่ฟันเทียม ",WebLabel.LEFT);
					ptExam43.setFont(new Font("Tahoma", Font.PLAIN, 13));
					ptExam43.setBounds(25,720,250,20);					
					Panel43.add(ptExam43);
					
					String [] prosNeed={"ต้องใส่ฟันบนและล่าง","ต้องใส่ฟันบน","ต้องใส่ฟันล่าง","ไม่ต้องใส่ฟันเทียม"};
					int l_prosNeed = prosNeed.length;			
					ProsNButton=new JRadioButton [l_prosNeed];
					groupProsN = new ButtonGroup();
					for(int row=0;row<l_prosNeed;row++){
						int x=140;
						if(row==3){
							ProsNButton[row] = new JRadioButton(prosNeed[row]);						  
						 }else{
							ProsNButton[row] = new JRadioButton(prosNeed[row]);
						 }					 
						ProsNButton[row].setActionCommand( ProsNButton[row].getText() );
						ProsNButton[row].addActionListener(this);					 				
						ProsNButton[row].setBounds(x+(row*120),720,120,20);
						ProsNButton[row].setBackground(new Color(207,229,233));										
						Panel43.add(ProsNButton[row]);
						groupProsN.add(ProsNButton[row]);				 
					}
					provider=new WebLabel("ผู้ตรวจ  ",WebLabel.LEFT);
					provider.setFont(new Font("Tahoma", Font.PLAIN, 13));
					provider.setBounds(25,750,300,20);					
					Panel43.add(provider);
					
					TextField_provider = new WebTextField();
					TextField_provider.setBounds(150,750,50,20);
					TextField_provider.setText("");
					TextField_provider.addActionListener(this);
					Panel43.add(TextField_provider);
					
					String sql_43 = "select perio,occpetope,occpetopr,occprtopr,fluneed,sneed,prosneed,provider  from dhis where  hn= '"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
					Connection conn431;
					PreparedStatement stmt431;
					try {
						conn431 = new DBmanager().getConnMySql();	 
						stmt431 = conn431.prepareStatement(sql_43);	
						ResultSet rs43 = stmt431.executeQuery();
						while (rs43.next()) {
							if(rs43.getString(1) ==null){
								String [] dex4={"","",""};
								for(int r=0;r<dex4.length;r++){
									dataExam301.add(dex4[r]);
								}			           
					            dataExam201.add(dataExam301); 
					            String [] dex5={"","",""};
								for(int r=0;r<dex5.length;r++){
									dataExam401.add(dex5[r]);
								}
					            dataExam201.add(dataExam401); 
							}
							else if(rs43.getString(1) !=null ){
								if( rs43.getString(1).equals("")) {
									String [] dex4={"","",""};
									for(int r=0;r<dex4.length;r++){
										dataExam301.add(dex4[r]);
									}			           
						            dataExam201.add(dataExam301); 
						            String [] dex5={"","",""};
									for(int r=0;r<dex5.length;r++){
										dataExam401.add(dex5[r]);
									}
						            dataExam201.add(dataExam401); 
								}else {
									String perio_text=rs43.getString(1).trim(); 	
									char[] perioArray = perio_text.toCharArray();
									for(int r=0;r<3;r++){
										dataExam301.add(String.valueOf(perioArray[r]));
									}
									dataExam201.add(dataExam301);
									for(int r=3;r<6;r++){
										dataExam401.add(String.valueOf(perioArray[r]));
									}
									dataExam201.add(dataExam401);
								}
								 
							}
							if(rs43.getString(2) ==null){							
							}
							else if(rs43.getString(2) !=null){
								TextField_occ1.setText(rs43.getString(2).trim());
							}						
							if(rs43.getString(3) ==null){							
							}
							else if(rs43.getString(3) !=null){
								TextField_occ2.setText(rs43.getString(3).trim());
							}
							if(rs43.getString(4) ==null){							
							}
							else if(rs43.getString(4) !=null){
								TextField_occ3.setText(rs43.getString(4).trim());
							}
							if(rs43.getString(5) ==null){						
							}
							else if(rs43.getString(5) !=null){
								if(rs43.getString(5).trim().equals("1")){
									FNButton[0].setSelected(true);
								}
								else if(rs43.getString(5).trim().equals("2")){
									FNButton[1].setSelected(true);
								}						 
							}						
							if(rs43.getString(6) ==null){							
							}
							else if(rs43.getString(6) !=null){
								if(rs43.getString(6).trim().equals("1")){
									SNButton[0].setSelected(true);
								}
								else if(rs43.getString(6).trim().equals("2")){
									SNButton[1].setSelected(true);
								}						 
							}						
							if(rs43.getString(7) ==null){						 
							}
							else if(rs43.getString(7) !=null){
								if(rs43.getString(7).trim().equals("1")){
									ProsNButton[0].setSelected(true);
								}
								else if(rs43.getString(7).trim().equals("2")){
									ProsNButton[1].setSelected(true);
								}
								else if(rs43.getString(7).trim().equals("3")){
									ProsNButton[2].setSelected(true);
								}
								else if(rs43.getString(7).trim().equals("4")){
									ProsNButton[3].setSelected(true);
								}							 
							}						
							if(rs43.getString(8) ==null){
								TextField_provider.setText("047");
							}
							else if(rs43.getString(8) !=null){
								TextField_provider.setText(rs43.getString(8).trim());
							}						 					 
						}		 
						stmt431.close(); 
						conn431.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					DefaultTableModel modelExam201 = new DefaultTableModel(dataExam201, columnNamesExam2){
						public Class getColumnClass(int column){
							return getValueAt(0, column).getClass();
						}
					};
					
					tableExam2 = new WebTable(modelExam201){
						public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
							WebLabel c = (WebLabel) super.prepareRenderer(renderer, row, column);
							c.setBackground(Color.WHITE);
							c.setForeground(Color.BLACK);
							c.setHorizontalAlignment(WebLabel.CENTER);
					        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
					        return c;
						}
					};
					TableColumnModel columnModelExam2 = tableExam2.getColumnModel();
					for(int cl2=0;cl2<3;cl2++){
				    	columnModelExam2.getColumn(cl2).setPreferredWidth(35);
					}
				    tableExam2.setPreferredScrollableViewportSize(new Dimension(560, 70));
					tableExam2.setFillsViewportHeight(true);
					tableExam2.setEnabled(true);
					tableExam2.setRowHeight(35);
					tableExam2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					tableExam2.setBounds(40,440,420,70);
					//tableExam2.addMouseListener(this);
					mid2.add(tableExam2);
					
				
				scrollPane43 = new JScrollPane(Panel43);
				//scrollPane43.setPreferredSize(new Dimension(500,500));
				//scrollPane43.setViewportView(Panel43);
				mid2.add(Panel43, BorderLayout.CENTER);
				
				mid2.revalidate();
				mid2.repaint();	
				MiddleSection.revalidate();
				MiddleSection.repaint();
				//right
				right1.removeAll();
				ButtonSaveExam1 = new WebButton("Save");		 
				ButtonSaveExam1.setBounds(5,10,80,30);
				ButtonSaveExam1.addActionListener(this);
				ButtonPrintExam1 = new WebButton("Print");		 
				ButtonPrintExam1.setBounds(5,50,80,30);
				ButtonPrintExam1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						PrintOPD(formcode,"2",page,form_clinic_name);
						
					}
				});
				
				Button431 = new WebButton("To 43");		 
				Button431.setBounds(5,90,80,30);
				Button431.addActionListener(this);
				
				//if(oUserInfo.GetPtVN().equals("")){
				//	System.out.println("<<<<<");
					 
				//}else{
					right1.add(ButtonSaveExam1);
					right1.add(ButtonPrintExam1);
					right1.add(Button431); 
				//}
				right1.revalidate();
				right1.repaint();
				RightSection.revalidate();
				RightSection.repaint();
			}
		}
		else if(formcode.equals("00107")) {
			clearPanel();
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
		 		String sql_43 = "select *  from dental43 where  pid= '"+oUserInfo.GetPtHN()+"' and date_serv='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
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
			

			//right Section
			right1.removeAll();
			
			
			right1.revalidate();
			right1.repaint();
			MiddleSection.revalidate();
			MiddleSection.repaint();
			RightSection.revalidate();
			RightSection.repaint();
		}
	}
	public void checkExam(String hn_check_in){
		String hn_check=hn_check_in;
		//int status_insert=0;
		int suc=0;
		String year43=Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim().substring(0, 4);
		String date_stat=year43+"-10-01";
		String date_inti="";
		 
	 /////////////////////////////////////////////////////////////change point time to check 
		
		DateTime dt21 = new DateTime(date_stat);
		DateTime dt20 = new DateTime(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim());
		//System.out.println("setting.."+dt2);
		if(dt20.isBefore(dt21)) {
			//System.out.println("setting..thist"+dt21);
			
			date_inti=(Integer.parseInt(year43)-1)+"-10-01";	
			
		}else {
			//System.out.println("setting..last"+dt21);
			date_inti=year43+"-10-01";	
		}
		DateTime dt2 = new DateTime(date_inti);
		Connection con6;
		PreparedStatement stmt6;
		ResultSet rs6 ;
		String sql_getHN="select  visitdate from dhis where hn=? order by visitdate desc LIMIT 1 ";
		try {
			con6 = new DBmanager().getConnMySql();
			stmt6 = con6.prepareStatement(sql_getHN);
			stmt6.setString(1, hn_check.trim()); 
			rs6 = stmt6.executeQuery();
			String old_visitdate="";
			 
			while( rs6.next() ){
				if(rs6.getString(1) !=null){
					old_visitdate=rs6.getString(1);
					//in
					DateTime dt1 = new DateTime(old_visitdate);
					 
					if(dt1.isBefore(dt2)){
						exam_status=1;
						// status_insert=1;
						 //System.out.println("1.."+dt2);
						//JOptionPane.showMessageDialog(this,"ลงข้อมูลแล้นวันที่ "+dt1,"Exam Note",JOptionPane.ERROR_MESSAGE);

					}
					//System.out.println("2.."+dt1);	
				} 
				suc=1;  	 
			}
			stmt6.close();
			con6.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(suc==0) {
			exam_status=1;
		}
		if(exam_status==1) {
			WebOptionPane.showMessageDialog(this,"กรุณาพิมพ์แบบตรวจ สุขภาพช่องปาก","Exam Note",WebOptionPane.ERROR_MESSAGE);

		}
		//System.out.println("2.."+exam_status+"--"+hn_check);
		
	}
	public void PrintOPD(String form_code,String typeofprint,String page,String form_clinic_name) {
		String h_print=typeofprint.trim();
    	String filename=Setup.ConverttoScanDate(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()))+"1100"+page+"0001"+oUserInfo.GetPtHN().trim();
    	int h_page=0;
		if(h_print.equals("1")){
    		h_page=430;
    	}else if(h_print.equals("2")){
    		h_page=840;
    	}
		String confFile="report/"+form_code.trim()+".jrxml";

		try {
			JasperReportBuilder report = DynamicReports.report(); 
			report.setTemplate(template()).setPageFormat(590, h_page, PageOrientation.PORTRAIT);
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(confFile );
			
			report.setTemplateDesign(in);
			if( form_code.equals("00115") ||  form_code.equals("00117")  ){
				try {
					report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
					report.setParameter("line1","โรงพยาบาลอุตรดิตถ์  ");
					report.setParameter("line2",form_clinic_name );
					report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
					report.setParameter("line4","วันที่ "+Setup.ConvertDateTimePrint(day)+" น. "+" "+oUserInfo.GetRightCode());
					report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
					report.setParameter("barcode",filename);
					report.setParameter("line6",oUserInfo.GetMemo1());
					report.setParameter("line7",form_code.trim()+".jrxml");
					/**
					if(checkconsent()==2) {
						report.setParameter("line11","ข้าพเจ้า.............................................................................................."
								+ "..\nยินยอมให้..........................................................................................."
								+ "\nได้รับการรักษาทางทันตกรรม..............................................................");
						
					}else {
						report.setParameter("line11","..........................................................................................................."
								+ "\n..........................................................................................................."
								+ "\n...........................................................................................................");
						
					}
	 				*/
					report.setDataSource(new JREmptyDataSource());
					report.print(true);
					//report.show(false);
				} catch (DRException e) {
					e.printStackTrace();
					}
				
				exam_status=0;
			}
			else if( form_code.equals("00116") || form_code.equals("00113") || form_code.equals("00112")){
				try {

					report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
					report.setParameter("line1","โรงพยาบาลอุตรดิตถ์  ");
					report.setParameter("line2",form_clinic_name );
					report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
					report.setParameter("line4","วันที่ "+Setup.ConvertDateTimePrint(day)+" น. "+" "+oUserInfo.GetRightCode());
					report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
					report.setParameter("barcode",filename);
					report.setParameter("line6",oUserInfo.GetMemo1());
					report.setParameter("line7",form_code.trim()+".jrxml");					
					report.setDataSource(new JREmptyDataSource());
					report.print(true);
					//report.show(false);
				} catch (DRException e) {
					e.printStackTrace();
				}
				
				if(exam_status==1) {
					Connection con6;
					PreparedStatement stmt6;
					ResultSet rs6 ;
					String sql_addHN = "insert into  dhis ( visitdate,hn,d_update) values ('"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"','"+oUserInfo.GetPtHN()+"','"+Setup.GetDateNow()+"')";
					try {
						con6 = new DBmanager().getConnMySql();
						stmt6 = con6.prepareStatement(sql_addHN);
						int rs_save = stmt6.executeUpdate();
						stmt6.close();
						con6.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				 
				exam_status=0;
		 
			}
			else if( form_code.trim().equals("01005") ){
				String runno="";
				//runno=getRunno(oUserInfo.GetPtVN(),oUserInfo.GetPtCliniccode());
				report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+form_clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+Setup.ConvertDateTimePrint(day)+" น. "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",form_code.trim()+".jrxml");
				report.setParameter("line10","");
				report.setParameter("line9","");
				report.setParameter("line11"," HN:"+oUserInfo.GetPtHN());
				report.setParameter("line12",oUserInfo.GetPtVN());
				report.setParameter("line13",oUserInfo.GetPtCliniccode());
				report.setParameter("barcode",filename);
				report.setDataSource(new JREmptyDataSource());
				report.print(true);
				//report.show(false);
			}
			else if( form_code.equals("00120")){
				try {

					report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
					report.setParameter("line1","โรงพยาบาลอุตรดิตถ์  ");
					report.setParameter("line2",form_clinic_name );
					report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
					report.setParameter("line4","วันที่ "+Setup.ConvertDateTimePrint(day)+" น. "+" "+oUserInfo.GetRightCode());
					report.setParameter("line5","แผนก/กลุ่มงาน............................................................โทรศัพท์.....................................");
					report.setParameter("barcode",filename);
					report.setParameter("line6","");
					report.setParameter("line7",form_code.trim()+".jrxml");					
					report.setDataSource(new JREmptyDataSource());
					report.print(true);
					//report.show(false);
				} catch (DRException e) {
					e.printStackTrace();
				}
				
				if(exam_status==1) {
					Connection con6;
					PreparedStatement stmt6;
					ResultSet rs6 ;
					String sql_addHN = "insert into  dhis ( visitdate,hn,d_update) values ('"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"','"+oUserInfo.GetPtHN()+"','"+Setup.GetDateNow()+"')";
					try {
						con6 = new DBmanager().getConnMySql();
						stmt6 = con6.prepareStatement(sql_addHN);
						int rs_save = stmt6.executeUpdate();
						stmt6.close();
						con6.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				 
				exam_status=0;
		 
			}
			
		} catch (DRException e1) {
			e1.printStackTrace();
		}
		
		 
	}
	public int checkAge(){
		 int result=1;
		 int age_check=0;
		 if(oUserInfo.GetPtAge().equals("")){
			 age_check = 0;
		 }else{
			 String cut_age=oUserInfo.GetPtAge().substring(0, oUserInfo.GetPtAge().indexOf("ปี")).trim();
			 age_check = Integer.parseInt(cut_age); 
		 }
		 if(age_check>=60){
				result=4 ;
			}
			else if(age_check<=20){
				result=3;
			}
		 return result;
		 
	}
	public int checkconsent(){
		 int result=1;
		 int age_check=0;
		 if(oUserInfo.GetPtAge().equals("")){
			 age_check = 0;
		 }else{
			 String cut_age=oUserInfo.GetPtAge().substring(0, oUserInfo.GetPtAge().indexOf("ปี")).trim();
			 age_check = Integer.parseInt(cut_age); 
		 }
		
			if(age_check<=15){
				result=2;
			}
		 return result;
		 
	}
	public boolean checkValueTable(){
		
		String [] data_input1={"0","1","2","3","4","5","6","7","8","9","10","a","b","c","d","e","f","g","h","A","B","C","D","E","F","G","H"};
		String [] data_input2={"0","1","2","3","4","5","6","7","8","9","10"};
		
		int rowCount_check = tableExam1.getRowCount();
	    int colCount_check = tableExam1.getColumnCount();
	    for (int i = 0; i < rowCount_check; i++) {
	    	for (int j = 0; j < colCount_check; j++){
	    		
	    		if(Arrays.asList(data_input1).contains(tableExam1.getValueAt(i, j).toString())){
	    			
	    		}else{
	    			return false;
 	    		}  	    		
	    	}    	    	
	    }
	    
	    
	    	for (int j = 0; j < 3; j++){
	    		if(Arrays.asList(data_input2).contains(tableExam1.getValueAt(0, j).toString())){  			
	    		}else{
	    			return false;
 	    		}  	    		 	     	    	
	    }
	     	for (int j = 0; j < 3; j++){
	    		if(Arrays.asList(data_input2).contains(tableExam1.getValueAt(1, j).toString())){  			
	    		}else{
	    			return false;
 	    		}  	    		 	     	    	
	    }
	     	for (int j = 13; j < 16; j++){
	    		if(Arrays.asList(data_input2).contains(tableExam1.getValueAt(0, j).toString())){  			
	    		}else{
	    			return false;
 	    		}  	    		 	     	    	
	    }
	     	for (int j = 13; j < 16; j++){
	    		if(Arrays.asList(data_input2).contains(tableExam1.getValueAt(1, j).toString())){  			
	    		}else{
	    			return false;
 	    		}  	    		 	     	    	
	    }
			
		return true;
		
	}
	public boolean checkValueTableGum(){
		String [] data_guminput={"1","2","3","4","5","9"};
		int rowCount_check = tableExam2.getRowCount();
	    int colCount_check = tableExam2.getColumnCount();
	    for (int i = 0; i < rowCount_check; i++) {
	    	for (int j = 0; j < colCount_check; j++){
	    		
	    		if(Arrays.asList(data_guminput).contains(tableExam2.getValueAt(i, j).toString())){
	    			
	    		}else{
	    			return false;
		    		}  	    		
	    	}    	    	
	    }
		return true;
	}
	public void clearDataPt43_1(){
		groupTypeB.clearSelection();
		groupEduB.clearSelection();
		TextField_EdulevelB.setText("");
		tableExam1.setModel(fetchClearData43_1());
		TableColumnModel columnModelExam1 = tableExam1.getColumnModel();
		 
		((DefaultTableModel)tableExam1.getModel()).fireTableDataChanged();
		 
	}
	public void clearDataPt43_2(){
		TextField_occ1.setText("");
		TextField_occ2.setText("");
		TextField_occ3.setText("");
		TextField_provider.setText("");
		groupFN.clearSelection();
		groupSN.clearSelection();
		groupProsN.clearSelection();
		tableExam2.setModel(fetchClearData43_2());
		TableColumnModel columnModelExam2 = tableExam2.getColumnModel();
		 
		((DefaultTableModel)tableExam2.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchClearData43_1(){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		return new DefaultTableModel(data, columnNamesExam1);
	}
	public  DefaultTableModel fetchClearData43_2(){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		return new DefaultTableModel(data, columnNamesExam2);
	}
	public String getQ(String clinic,String vn,String hn) {
		int extra=0;
		String q="";
		int q_all=0,id=0;
		System.out.println(vn+"<<<"+clinic+"<<"+hn);
		String sql_q="select visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitno='"+vn+"' and visit.hn='"+hn+"' and visit_clinic.clinic='"+clinic+"'";
		Connection conn=new DBmanager().getConnMySql();
		try {
			PreparedStatement stmt4 = conn.prepareStatement(sql_q);
			ResultSet rs4 = stmt4.executeQuery();
			while (rs4.next()) {	
				if(rs4.getString(1) !=null) {
					q=rs4.getString(1).trim();
					extra=1;
				}
				 		 
			}
			stmt4.close();
			 
			if(extra==1) {
				
			}
			else if(extra==0){
				if(q.equals("")) {
					System.out.println(vn+"<<<"+clinic+"<<"+hn+"---"+extra);
					String get_id="select id from visit where visitdate='"+Setup.GetDateNow()+"' and visitno='"+vn+"' and hn='"+hn+"'";
					PreparedStatement stmt41 = conn.prepareStatement(get_id);
					ResultSet rs41 = stmt41.executeQuery();
					while (rs41.next()) {	
						if(rs41.getString(1) !=null) {
							System.out.println(vn+"<<<"+clinic+"<<"+hn+"---"+extra+"*****"+rs41.getString(1));
							String insert_clinic = "insert into visit_clinic (id,clinic,userupdate,dateupdate,ipupdate) values ('"+rs41.getInt(1)+"','"+clinic+"','2','"+Setup.GetDateTimeNow()+"','172.17.71.11')";
							PreparedStatement stmt62 = conn.prepareStatement(insert_clinic);
							int rs_save =  stmt62.executeUpdate();
							stmt62.close();
							
							/*
							String check="select id from visit_clinic where id='"+rs41.getInt(1)+"' and clinic='"+clinic+"'";
							PreparedStatement stmt411 = conn.prepareStatement(check);
							ResultSet rs411 = stmt411.executeQuery();
							while (rs411.next()) {	
								if(rs411.getString(1) !=null) {								 
									System.out.println("+++++++++++++"+vn+"<<<"+clinic+"<<"+hn+"---"+extra+"*****"+rs41.getString(1));
								}else {
									System.out.println("-------*******---"+vn+"<<<"+clinic+"<<"+hn+"---"+extra+"*****"+rs41.getString(1));
									
									String insert_clinic = "insert into visit_clinic (id,clinic,userupdate,dateupdate,ipupdate) values ('"+rs41.getInt(1)+"','"+clinic+"','2','"+Setup.GetDateTimeNow()+"','172.17.71.11')";
									PreparedStatement stmt62 = conn.prepareStatement(insert_clinic);
									//int rs_save =  stmt62.executeUpdate();
									stmt62.close();
								}
							}
							stmt411.close();
							*/
							 
						}					 		 
					}
					stmt41.close();
				}else {

				}
		 
			}
			/////////////////////////////
			if(q.equals("")) {
				 
				String[] parts=null;
	            parts = InApp.userappcode.split("\\,");
	            String sql_user1="",sql_user="";
	            if(parts.length==1){
	                for(int i=0;i<parts.length;i++){
	                    sql_user1=" and visit_clinic.clinic='"+parts[i]+"'";
	                }
	                sql_user=sql_user1;
	            }
	            else if(parts.length==0){
	                sql_user="";
	            }
	            else if(parts.length>1){
	                sql_user1="and visit_clinic.clinic in (";
	                for(int i=0;i<parts.length;i++){
	                    sql_user1+="'"+parts[i]+"',";
	                }
	                sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
	            }
				String sql="select visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitdate='"+Setup.GetDateNow()+"' "+sql_user+"  order by visit_clinic.queue desc LIMIT 1";
				PreparedStatement stmt41 = conn.prepareStatement(sql);
				ResultSet rs41 = stmt41.executeQuery();
				while (rs41.next()) {
					if(rs41.getString(1) !=null) {
						q_all=rs41.getInt(1);
					}
					 			 
				}
				stmt41.close();
				if(q_all==0) {
					int id1=0;
					System.out.println("Queue..All..****"+q_all+"--"+id1+"---"+clinic);
					String sql42="select id from visit where visitno='"+vn+"'and hn='"+hn+"' and visitdate='"+Setup.GetDateNow()+"'  LIMIT 1";
					PreparedStatement stmt42 = conn.prepareStatement(sql42);
					ResultSet rs42 = stmt42.executeQuery();
					while (rs42.next()) {
						if(rs42.getString(1) !=null) {
							id1=rs42.getInt(1);
						}					 			 
					}
					stmt41.close();
					String sql_update1 = "update visit_clinic set  queue='1' where id='"+id1+"' and clinic='"+clinic+"' ";
					PreparedStatement stmt611 = conn.prepareStatement(sql_update1);
					int rs_update1 = stmt611.executeUpdate();
					 
					stmt611.close();
					   q="1";
					
				}else {
					 
					int id1=0;
					 
					String sql42="select id from visit where visitno='"+vn+"'and hn='"+hn+"' and visitdate='"+Setup.GetDateNow()+"'  LIMIT 1";
					PreparedStatement stmt42 = conn.prepareStatement(sql42);
					ResultSet rs42 = stmt42.executeQuery();
					while (rs42.next()) {
						if(rs42.getString(1) !=null) {
							id1=rs42.getInt(1);
						}					 			 
					}
					stmt41.close();
					System.out.println("Queue..All.."+q_all+"--"+id1+"---"+clinic);
					///
					String sql_update1 = "update visit_clinic set  queue='"+(q_all+1)+"' where id='"+id1+"' and clinic='"+clinic+"' ";
					PreparedStatement stmt611 = conn.prepareStatement(sql_update1);
					int rs_update1 = stmt611.executeUpdate();
					q_all=q_all+1;
					stmt611.close();
					q=Integer.toString(q_all);
				}
				 
				
				
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Queue.."+q);
		return q;
	}
	public String getTxDatetimeIn(String hn) {
		String to="";
		Connection conn=new DBmanager().getConnMySql();
		String sql="select txdatetime from txlog where txtype='1' and hn='"+hn+"' and status='1' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' order by txdatetime desc limit 1";
		 
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if(rs.getString(1) !=null) {
					String to1=rs.getString(1).trim().substring(10);
					to=" "+to1.trim().substring(0,8)+" น.";
				}					 			 
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"-----"+to);
		return to;
	}
	public String getTxDatetimeOut(String hn) {
		String to="";
		Connection conn=new DBmanager().getConnMySql();
		String sql="select txdatetime from txlog where txtype='2' and hn='"+hn+"'  and status='1' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' order by txdatetime desc limit 1";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if(rs.getString(1) !=null) {
					String to1=rs.getString(1).trim().substring(10);
					to=" "+to1.trim().substring(0,8)+" น.";
				}					 			 
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

			 
		return to;
	}
	public void getInOutDateTime(String hn) {
		Label_Info_in.setText(getTxDatetimeIn(oUserInfo.GetPtHN()));
		Label_Info_out.setText(getTxDatetimeOut(oUserInfo.GetPtHN()));
	}
	public void clearPanel() {
		Pthx.clearSelection();
		Ptallergy.clearSelection();
		mid2.removeAll();
		mid2.revalidate();
		mid2.repaint();	
		//mid1.removeAll();
		//mid1.revalidate();
		//mid1.repaint();
		MiddleSection.revalidate();
		MiddleSection.repaint();
		//right
		right1.removeAll();
		right1.revalidate();
		right1.repaint();
		RightSection.revalidate();
		RightSection.repaint();
		ButtonIn.setEnabled(true);
		ButtonOut.setEnabled(false);
		ButtonTEdit.setEnabled(false);
		Label_Info_in.setText("");
		Label_Info_out.setText("");
	}

}
