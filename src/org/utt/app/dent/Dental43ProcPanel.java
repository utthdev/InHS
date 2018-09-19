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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.utt.app.InApp;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.DateLabelFormatter;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;

public class Dental43ProcPanel extends WebPanel {
 
	Dimension screen;
	JDatePicker picker1,picker2;
	
	WebPanel LeftSection,MiddleSection,left1,left2,mid1,mid2,jPanel1,jPanel2;
	WebButton buttonReport,ButtonSearch,ButtonImp,btnNewButton,ButtonCount,ButtonExport;
	WebTable tableR;
	Vector<Vector<String>> data,dataReport;
	Vector<String> columnNamesReport;
	WebScrollPane scrollPaneR;
	
	String day1="",month1="",dateSearch1="",dateSearch2="",day2="",month2="";
	WebLabel labelReport,labelTime;

	/**
	 * Create the panel.
	 */
	public Dental43ProcPanel( ) {
		 
		super();
 
		setLayout(new BorderLayout(0, 0));
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screen.width, screen.height-160));
		//getT();
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(250, screen.height-160));
		//add(LeftSection, BorderLayout.WEST);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		left1 = new WebPanel();
		left1.setPreferredSize(new Dimension(250, 80));
		LeftSection.add(left1, BorderLayout.NORTH);
		left1.setLayout(null);
		
		left2 = new WebPanel();
		LeftSection.add(left2, BorderLayout.CENTER);
		left2.setLayout(null);
		
		buttonReport = new WebButton("รายงานทันตกรรม");
		buttonReport.setBounds(20, 5, 113, 23);
		left2.add(buttonReport);
		
		btnNewButton = new WebButton("New button");
		btnNewButton.setBounds(138, 5, 91, 23);
		left2.add(btnNewButton);
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//insertData();
				//checkData();
			}
		});
		buttonReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//report1();
			}
		});
		
		
		jPanel1 = new WebPanel();
		UtilDateModel model1 = new UtilDateModel();
		model1.setSelected(true);
		Properties p1 = new Properties();
		p1.put("text.today", "");
		p1.put("text.month", "");
		p1.put("text.year", "");
		JDatePanelImpl datePanel1 = new JDatePanelImpl(model1,p1);
		picker1 = new JDatePickerImpl(datePanel1, new DateLabelFormatter());
		picker1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(picker1.getModel().getDay()<10){
					day1="0"+picker1.getModel().getDay();
				}else{
					day1=""+picker1.getModel().getDay();
				}
				if(picker1.getModel().getMonth()+1<10){
					month1="0"+(picker1.getModel().getMonth()+1);
				}else{
					month1=""+(picker1.getModel().getMonth()+1);
				}
				dateSearch1=picker1.getModel().getYear()+"-"+month1+"-"+day1;
				
			}
		});
		picker1.setShowYearButtons(true);
		picker1.setTextEditable(false);
		 
		jPanel1 = new WebPanel();
		jPanel1.setBounds(35, 1, 220, 30);
		jPanel1.add((JComponent)picker1);
		
		jPanel2 = new WebPanel();
		UtilDateModel model2 = new UtilDateModel();
		model2.setSelected(true);
		Properties p2 = new Properties();
		p2.put("text.today", "");
		p2.put("text.month", "");
		p2.put("text.year", "");
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2,p2);
		picker2 = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
		picker2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(picker2.getModel().getDay()<10){
					day2="0"+picker2.getModel().getDay();
				}else{
					day2=""+picker2.getModel().getDay();
				}
				if(picker2.getModel().getMonth()+1<10){
					month2="0"+(picker2.getModel().getMonth()+1);
				}else{
					month2=""+(picker2.getModel().getMonth()+1);
				}
				dateSearch2=picker2.getModel().getYear()+"-"+month2+"-"+day2;
				
			}
		});
		picker2.setShowYearButtons(true);
		picker2.setTextEditable(false);
		 
		jPanel2 = new WebPanel();
		jPanel2.setBounds(300, 1, 220, 30);
		jPanel2.add((JComponent)picker2);
		
		MiddleSection = new WebPanel();
		MiddleSection.setBorder(new TitledBorder(null, "Dental History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		MiddleSection.setPreferredSize(new Dimension(screen.width, screen.height-160));
		
		add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid1 = new WebPanel();
		mid1.setPreferredSize(new Dimension(screen.width, 50));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		mid1.add(jPanel1);
		mid1.add(jPanel2);
		
		ButtonImp = new WebButton("import data");
		ButtonImp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getD();
			}
		});
		ButtonImp.setBounds(630, 4, 89, 23);
		if(InApp.usertype.trim().equals("102") ){
			//mid1.add(ButtonImp);
		}
		 
		ButtonCount = new WebButton("Show");
		ButtonCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDataReport();
				//getD();
				//getDD();
			}
		});
		ButtonCount.setBounds(530, 4, 89, 23);
		mid1.add(ButtonCount);
		
		ButtonExport = new WebButton("Export to HDC");
		ButtonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDataToExport();
			}
		});
		ButtonExport.setBounds(630, 4, 120, 23);
		mid1.add(ButtonExport);

		WebLabel label = new WebLabel("เริ่ม");
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setBounds(10, 8, 30, 20);
		mid1.add(label);
		
		WebLabel label_1 = new WebLabel("สิ้นสุด");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_1.setBounds(260, 8, 40, 20);
		mid1.add(label_1);
		
		
		mid2 = new WebPanel();
		mid2.setPreferredSize(new Dimension(screen.width, screen.height-210));
		MiddleSection.add(mid2, BorderLayout.CENTER);
		mid2.setLayout(null);
		
		labelReport = new WebLabel("รายงานการลง  43  แฟ้ม  รายวัน");
		labelReport.setFont(new Font("Tahoma", Font.BOLD, 13));
		labelReport.setBounds(76, 8, 232, 25);
		mid2.add(labelReport);
		
		labelTime = new WebLabel("ช่วงเวลา");
		labelTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labelTime.setBounds(76, 28, 800, 25);
		mid2.add(labelTime);
		
 
		//
		columnNamesReport = new Vector<String>();
  		columnNamesReport.add(" hcode");
  		columnNamesReport.add(" pid");
		columnNamesReport.add(" seq");
		columnNamesReport.add(" date_serv");
		columnNamesReport.add(" clinic");
		columnNamesReport.add(" procedcode");
		columnNamesReport.add(" serviceprice");
		columnNamesReport.add(" provider");
		columnNamesReport.add(" d_update");
		columnNamesReport.add(" cid");
		
		
		dataReport = new Vector<Vector<String>>();
		
		DefaultTableModel modelR = new DefaultTableModel(dataReport, columnNamesReport){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		tableR = new WebTable(modelR){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);
					 
					String type = (String)getModel().getValueAt(modelRow, 1);
					//System.out.println("--"+type);	 
				}
				 
				return c;
			}
		};
		tableR.setRowHeight(25);		
		tableR.setFillsViewportHeight(true);
 
		scrollPaneR = new WebScrollPane(tableR);
		scrollPaneR.setBounds(5, 50, screen.width-50, screen.height-300);
		mid2.add(scrollPaneR);
		
		
	}
 
	public void getDataReport(){
		tableR.setModel(fetchDataOPDReport());
		TableColumnModel columnModelR = tableR.getColumnModel();
		columnModelR.getColumn(0).setPreferredWidth(30);
		columnModelR.getColumn(1).setPreferredWidth(50 );
		columnModelR.getColumn(2).setPreferredWidth(100);
		columnModelR.getColumn(3).setPreferredWidth(50 );
		for (int n = 4; n < 8; n++) {	
			columnModelR.getColumn(n+1).setPreferredWidth(50);
			//columnModelR.getColumn(n+1).setMinWidth(0);
			//columnModelR.getColumn(n+1).setMaxWidth(0);
		}
		
		
		((DefaultTableModel)tableR.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataOPDReport(){
		Vector<Vector<String>> dataReport = new Vector<Vector<String>>();
		System.out.println(dateSearch1.trim());
		String b=Setup.DateInDBMSSQL(dateSearch1.trim());
		String f=Setup.DateInDBMSSQL(dateSearch2.trim());
		
		labelTime.setText("ช่วงเวลา   วันที่   "+Setup.ShowThaiDate1(b)+" ถึง  วันที่  "+Setup.ShowThaiDate1(f) +" :: งานทันตกรรมเคลื่อนที่นับรวม ");
		
		
		String sql="select * from procedure_opd where  date_serv between '"+b+"' and '"+f+"'" ;
		Connection conn1 =  new DBmanager().getConnMySql();
		System.out.println(sql);
		PreparedStatement stmt;
		try {
			stmt = conn1.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()){
				final Vector<String> vstring = new Vector<String>();
				vstring.add(" 10673");
				for(int i=0;i<9;i++){
					if(i==2) {
						vstring.add(" "+Setup.DateServ43(rs.getString(i+1)));
					}else {
						if(i==3) {
							if(rs.getString(i+1).equals("")) {
								vstring.add("01101");
							}
						}
						else if(i==5) {
							if( rs.getString(i+1)==null) {
								vstring.add("0.00");
							}
						}
						else {
							vstring.add(" "+rs.getString(i+1));
						}
						 
					}
					 
					 
					//System.out.println(rs.getString(i+1));
				}
				 
				dataReport.add(vstring);
			}
			stmt.close();
			conn1.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
		return new DefaultTableModel(dataReport, columnNamesReport);
	}

	public void getD() {
		String b=Setup.DateInDBMSSQL(dateSearch1.trim());
		String f=Setup.DateInDBMSSQL(dateSearch2.trim());
		//String b="2018-01-01";
		//String f="2018-01-31";
		//System.out.println(b+"--"+f);
		
		String sql="select treatment,doctor,hn,visitdatetime ,toothno from dental_activity where typeplace='2' and  visitdatetime between '"+b+"' and '"+f+"'" ;
		Connection conn1 =  new DBmanager().getConnMySql();
		PreparedStatement stmt,stmt1,stmt2;
		int p=1;
		try {
			stmt = conn1.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int pp=1;
			while (rs.next()){	
				String dr="047";
				String cid="";
				String d_serv="";
				String d_now=Setup.GetDateNow43();
				String hn="";
				String seq="";
				
				
				if(rs.getString(2) ==null) {
					dr="047";
				}else if(rs.getString(2).equals("")){
					dr="047";
				}else {
					dr=rs.getString(2).trim();
				}////////////////////////////////////
				//System.out.println(p+"."+tx+"--"+rs.getString(1).trim()+"--"+dr);
				if(rs.getString(3) !=null) {
		 
					hn=rs.getString(3).trim();
					cid=getRef(hn);
					d_serv=Setup.DateServ43(rs.getString(4).trim().substring(0, 10));
					seq=hn+d_serv;
					 
					
					}
				///////////////////
					String tx="";
					String tx1=rs.getString(1).trim();
					 
					if(rs.getString(1) !=null && !(rs.getString(1).equals(""))) {
						if(tx1.substring(0, 1).equals("2") && tx1.length()==7) {
							tx=tx1;
							String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
							 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
									
							 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
							 int rs_insert = stmt6.executeUpdate();
							 stmt6.close();
							 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
							 
						}else if(tx1.substring(0, 1).equals("1")){
							String sql1="select icdcm_code from dentalcode where code='"+tx1+"'" ;
							stmt1 = conn1.prepareStatement(sql1);
							ResultSet rs1 = stmt1.executeQuery();
							
							while (rs1.next()){
								if(rs1.getString(1).trim().length()==7) {
									tx=rs1.getString(1).trim();
									String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
									 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
											
									 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
									 int rs_insert = stmt6.executeUpdate();
									 stmt6.close();
									 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
									 
									
								}else if(rs1.getString(1).trim().length()>7){
									if(tx1.equals("101351")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="2387030";
												
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="2377030";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
										 
									}
									else if(tx1.equals("101352")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="2387040";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="2377040";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
										 
									}
									else if(tx1.equals("102330-1")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="23871B1";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="23771B1";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									else if(tx1.equals("102331-3")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="23871B2";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="23771B2";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									else if(tx1.equals("102330-3")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="23871B1";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="23771B1";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									else if(tx1.equals("102330")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="23871B1";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="23771B1";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									else if(tx1.equals("102331")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="23871B2";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="23771B2";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									else if(tx1.equals("102332-3")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="23871B3";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="23771B3";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									else if(tx1.equals("104345-6")) {
										tx="2277310";
										String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
										 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','2277310','"+dr+"','"+d_now+"','"+cid+"')";
												
										 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
										 //int rs_insert = stmt6.executeUpdate();
										 stmt6.close();
										 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
										 
											String sql_insert_data1="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','2287310','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt61 = conn1.prepareStatement(sql_insert_data1);	
											 int rs_insert = stmt61.executeUpdate();
											 stmt61.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										
									}
									else if(tx1.equals("104345")) {
										tx="2277310";
										String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
										 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','2277310','"+dr+"','"+d_now+"','"+cid+"')";
												
										 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
										 //int rs_insert = stmt6.executeUpdate();
										 stmt6.close();
										 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
										 
											String sql_insert_data1="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','2287310','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt61 = conn1.prepareStatement(sql_insert_data1);	
											 int rs_insert = stmt61.executeUpdate();
											 stmt61.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										
									}
									else if(tx1.equals("107110")) {
										if(rs.getString(5)!=null) {
											String tno=rs.getString(5).trim();
											if(tno.substring(0, 1).equals("1") || tno.substring(0, 1).equals("2") || tno.substring(0, 1).equals("3") || tno.substring(0, 1).equals("4")) {
												tx="2382700";
											}
											else if(tno.substring(0, 1).equals("5") || tno.substring(0, 1).equals("6") || tno.substring(0, 1).equals("7") || tno.substring(0, 1).equals("8")) {
												tx="2372700";
											}
											String sql_insert_data="insert into procedure_opd (pid,seq,date_serv,clinic,procedcode,provider,d_update,cid) "
											 		+ "values ('"+hn+"','"+seq+"','"+d_serv+"','','"+tx+"','"+dr+"','"+d_now+"','"+cid+"')";
													
											 PreparedStatement stmt6 = conn1.prepareStatement(sql_insert_data);	
											 int rs_insert = stmt6.executeUpdate();
											 stmt6.close();
											 //System.out.println(hn+"--"+seq+"--"+d_serv+"--"+tx+"--"+dr+"--"+d_now+"=="+cid);
											 
										}
									}
									
									System.out.println(pp+"."+tx1+"--"+rs1.getString(1).trim()+"--"+tx);
									pp++;
								}
								 
							}
							stmt1.close();
						}
						
					}
					 
					 
			p++;
			}
			stmt.close();
			conn1.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 System.out.println("end..............");
	}
	public String getRef(String hn) {
		String c="";
		String query="select ref from patient_ref where hn='"+hn+"' and reftype='01'";
		Connection conn=new DBmanager().getConnMSSql();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			int p=1;
			while (rs.next()) {
				if(rs.getString(1) !=null) {
					c=rs.getString(1).trim();
				}
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//stmt.setString(2, "1100");
		 
		return c;
	}
	public void getT() {
		String sql="select code,icdcm_code  from dentalcode " ;
		Connection conn1 =  new DBmanager().getConnMySql();
		PreparedStatement stmt,stmt1,stmt2;
		int p=1;
		try {
			stmt = conn1.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()){	
				if(rs.getString(2).trim().length()>7) {
					System.out.println(p+"."+rs.getString(1).trim()+"--"+rs.getString(2).trim());
				}
				 p++;
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getDataToExport(){
		String b=Setup.DateInDBMSSQL(dateSearch1.trim());
		String f=Setup.DateInDBMSSQL(dateSearch2.trim());
		String sql="select * from procedure_opd where  date_serv between '"+b+"' and '"+f+"'" ;
		Connection conn1 =  new DBmanager().getConnMySql();
		PreparedStatement stmt;
		try {
			stmt = conn1.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			FileWriter writer=null;
			 
			JFileChooser chooser = new JFileChooser();
	        int result = chooser.showSaveDialog(null);
	        if (result == JFileChooser.APPROVE_OPTION) {
	        	File file = new File(chooser.getSelectedFile()+".txt");
	        	writer = new FileWriter(file, true);
	        }
	        writer.write("HOSPCODE|PID|SEQ|DATE_SERV|CLINIC|PROCEDCODE|SERVICEPRICE|PROVIDER|D_UPDATE|CID \n");
			
			while (rs.next()){
				 writer.write("10673");

				for(int i=1;i<10;i++){
					if(rs.getString(i) !=null) {
						if(i==3) {
							writer.write("|"+Setup.DateServ43(rs.getString(i)));
						}
						else if(i==4) {
							if(rs.getString(i).equals("")) {
								writer.write("|01105");
							}
							 
						}
						else {
							writer.write("|"+rs.getString(i));
						}
						 
					}else {
						if(i==6) {
							writer.write("|0.00" );
						}
						 
					}
					 
					//System.out.println(rs.getString(i+1));
				}
				writer.write("\n");
				
			}
			writer.close();
			stmt.close();
			conn1.close();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

