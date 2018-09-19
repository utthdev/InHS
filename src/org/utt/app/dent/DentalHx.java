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

public class DentalHx extends WebPanel {

	Dimension screen;
	JDatePicker picker1,picker2;
	
	WebPanel LeftSection,MiddleSection,left1,left2,mid1,mid2,jPanel1,jPanel2;
	WebButton buttonReport,ButtonSearch,btnNewButton,ButtonCount;
	WebTable tableR;
	Vector<Vector<String>> data,dataReport;
	Vector<String> columnNamesReport;
	WebScrollPane scrollPaneR;
	
	String day1="",month1="",dateSearch1="",dateSearch2="",day2="",month2="";
	WebLabel labelReport,labelTime;

	public DentalHx() {
		super();

		setLayout(new BorderLayout(0, 0));
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screen.width, screen.height-160));
		
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
		//MiddleSection.setPreferredSize(new Dimension(screen.width-200, screen.height-160));
		
		add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid1 = new WebPanel();
		mid1.setPreferredSize(new Dimension(screen.width, 50));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		mid1.add(jPanel1);
		mid1.add(jPanel2);
		
		ButtonCount = new WebButton("Show");
		ButtonCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDataReport();
			}
		});
		ButtonCount.setBounds(530, 4, 89, 23);
		mid1.add(ButtonCount);
		
		 
		
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
		labelTime.setBounds(76, 28, 500, 25);
		mid2.add(labelTime);
		
 
		//
		columnNamesReport = new Vector<String>();
  		columnNamesReport.add(" visitdate");
  		columnNamesReport.add(" vn");
		columnNamesReport.add(" hn");
		columnNamesReport.add(" cid");
		columnNamesReport.add(" tse");
		columnNamesReport.add(" tpt");
		columnNamesReport.add(" edu");
		columnNamesReport.add(" edle");
		columnNamesReport.add(" u0");
		columnNamesReport.add(" u1");
		columnNamesReport.add(" u2");
		columnNamesReport.add(" u3");
		columnNamesReport.add(" u4");
		columnNamesReport.add(" u5");
		columnNamesReport.add(" u6");
		columnNamesReport.add(" u7");
		columnNamesReport.add(" u8");
		columnNamesReport.add(" u9");
		columnNamesReport.add(" u10");
		columnNamesReport.add(" u11");
		columnNamesReport.add(" u12");
		columnNamesReport.add(" u13");
		columnNamesReport.add(" u14");
		columnNamesReport.add(" u15");
		columnNamesReport.add(" l0");
		columnNamesReport.add(" l1");
		columnNamesReport.add(" l2");
		columnNamesReport.add(" l3");
		columnNamesReport.add(" l4");
		columnNamesReport.add(" l5");
		columnNamesReport.add(" l6");
		columnNamesReport.add(" l7");
		columnNamesReport.add(" l8");
		columnNamesReport.add(" l9");
		columnNamesReport.add(" l10");
		columnNamesReport.add(" l11");
		columnNamesReport.add(" l12");
		columnNamesReport.add(" l13");
		columnNamesReport.add(" l14");
		columnNamesReport.add(" l15");
		columnNamesReport.add(" perio");
		columnNamesReport.add(" occpetope");
		columnNamesReport.add(" occpetopr");
		columnNamesReport.add(" occprtopr");
		columnNamesReport.add(" fluneed");
		columnNamesReport.add(" sneed");
		columnNamesReport.add(" prosneed");
		columnNamesReport.add(" d_update");
		columnNamesReport.add(" provider");
		
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
		columnModelR.getColumn(0).setPreferredWidth(80);
		columnModelR.getColumn(1).setPreferredWidth(50 );
		columnModelR.getColumn(2).setPreferredWidth(50);
		columnModelR.getColumn(3).setPreferredWidth(120 );
		for (int n = 4; n < 48; n++) {	
			columnModelR.getColumn(n+1).setPreferredWidth(15);
			//columnModelR.getColumn(n+1).setMinWidth(0);
			//columnModelR.getColumn(n+1).setMaxWidth(0);
		}
		columnModelR.getColumn(47).setPreferredWidth(150 );
		columnModelR.getColumn(48).setPreferredWidth(50 );
		
		((DefaultTableModel)tableR.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataOPDReport(){
		Vector<Vector<String>> dataReport = new Vector<Vector<String>>();
		String b=Setup.DateInDBMSSQL(dateSearch1.trim());
		String f=Setup.DateInDBMSSQL(dateSearch2.trim());
		
		labelTime.setText("ช่วงเวลา   วันที่   "+Setup.ShowThaiDate1(b)+" ถึง  วันที่  "+Setup.ShowThaiDate1(f) +" :: งานทันตกรรมเคลื่อนที่นับรวม ");
		
		
		String sql="select * from dhis where  visitdate between '"+b+"' and '"+f+"'" ;
		Connection conn1 =  new DBmanager().getConnMySql();
		PreparedStatement stmt;
		try {
			stmt = conn1.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()){
				final Vector<String> vstring = new Vector<String>();
				for(int i=0;i<49;i++){
					vstring.add(" "+rs.getString(i+1));
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
}


