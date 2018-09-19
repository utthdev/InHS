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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.utt.app.InApp;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;


public class DentalConf extends WebPanel {
	Dimension screen;
	WebPanel LeftSection,MiddleSection;
	WebTable table;
	Vector<String> columnNames;
	WebScrollPane scrollPane;
	WebPanel left1,mid1,mid2;
	WebButton ButtonRefresh,ButtonSave,ButtonEdit;
	private WebLabel lblCode,lblName,lblIcdcm,lblReportType1,lblReporttype,lblReporttype3;
	private WebTextField TextFieldCode,TextFieldName,TextFieldICDCM,TextFieldReport1,TextFieldReport2,TextFieldReport3,TextFieldCat,TextFieldSubCat;

	public DentalConf() {
		super();
		setLayout(new BorderLayout(0, 0));
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screen.width-(screen.width*2)/10, screen.height-160));
		
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(200, screen.height-160));
		add(LeftSection, BorderLayout.WEST);
		LeftSection.setLayout(new BorderLayout(0, 0));
		add(LeftSection, BorderLayout.WEST);
		
		columnNames = new Vector<String>();		
		columnNames.add("");
		columnNames.add("  code");
		columnNames.add("");
		columnNames.add("");
		columnNames.add("");
		columnNames.add("");
		columnNames.add("");
		columnNames.add("");
		columnNames.add("");
		
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		DefaultTableModel model = new DefaultTableModel(data, columnNames){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		table = new WebTable(model){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
				}
				return c;
			}
		};
		
		left1 = new WebPanel();
		left1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		left1.setPreferredSize(new Dimension(200, 450));
		LeftSection.add(left1, BorderLayout.NORTH);
		left1.setLayout(new BorderLayout(0, 0));
		
		WebLabel label = new WebLabel("Code");
		left1.add(label, BorderLayout.NORTH);
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
	 	
		table = new WebTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row_table = table.rowAtPoint(e.getPoint());
		        int col_table = table.columnAtPoint(e.getPoint());
		         
		        TextFieldCode.setText(table.getValueAt(row_table,1).toString().trim());
		        TextFieldName.setText(table.getValueAt(row_table,2).toString().trim());
		        TextFieldICDCM.setText(table.getValueAt(row_table,3).toString().trim());
		        TextFieldReport1.setText(table.getValueAt(row_table,4).toString().trim());
		        TextFieldReport2.setText(table.getValueAt(row_table,5).toString().trim());
		        TextFieldReport3.setText(table.getValueAt(row_table,6).toString().trim());
		        TextFieldCat.setText(table.getValueAt(row_table,7).toString().trim());
		        TextFieldSubCat.setText(table.getValueAt(row_table,8).toString().trim());
			}
		});
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		table.setRowHeight(25);		
		table.setFillsViewportHeight(true);
		
		scrollPane = new WebScrollPane(table);
		left1.add(scrollPane, BorderLayout.CENTER);
		getData();
		ButtonRefresh = new WebButton("Refresh");
		ButtonRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getData();
				clearData();
			}
		});
		left1.add(ButtonRefresh, BorderLayout.SOUTH);
		 	
		MiddleSection = new WebPanel();
		add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setPreferredSize(new Dimension(600, screen.height-160));
		MiddleSection.setLayout(new BorderLayout(0, 0));
		mid1 = new WebPanel();
		mid1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mid1.setPreferredSize(new Dimension(600, 30));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		

		
		ButtonSave = new WebButton("Save");
		ButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveData();
				clearData();
				getData();
				TextFieldICDCM.setEditable(false);
				TextFieldReport1.setEditable(false);
				TextFieldReport2.setEditable(false);
				TextFieldReport3.setEditable(false);
			}
		});
		ButtonSave.setBounds(150, 4, 89, 23);
		mid1.add(ButtonSave);
		
		ButtonEdit = new WebButton("Edit");
		ButtonEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextFieldICDCM.setEditable(true);
				TextFieldReport1.setEditable(true);
				TextFieldReport2.setEditable(true);
				TextFieldReport3.setEditable(true);
			}
		});
		ButtonEdit.setBounds(50, 4, 89, 23);
		mid1.add(ButtonEdit);
		
		WebButton ButtonClear = new WebButton("Clear");
		ButtonClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearData();
		 
			}
		});
		ButtonClear.setBounds(250, 4, 89, 23);
		mid1.add(ButtonClear);
		
		mid2 = new WebPanel();
		mid2.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(176, 224, 230), null, new Color(250, 235, 215), null));
		mid2.setPreferredSize(new Dimension(600, 600));
		MiddleSection.add(mid2, BorderLayout.CENTER);
		mid2.setLayout(null);
		
		lblCode = new WebLabel("Code");
		lblCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCode.setBounds(10, 10, 46, 25);
		mid2.add(lblCode);
		
		lblName = new WebLabel("Name");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblName.setBounds(10, 40, 46, 25);
		mid2.add(lblName);
		
		lblIcdcm = new WebLabel("ICDCM");
		lblIcdcm.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblIcdcm.setBounds(10, 70, 46, 25);
		mid2.add(lblIcdcm);
		
		lblReportType1 = new WebLabel("Report Tpye 1");
		lblReportType1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblReportType1.setBounds(10, 100, 89, 25);
		mid2.add(lblReportType1);
		
		lblReporttype = new WebLabel("ReportType2");
		lblReporttype.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblReporttype.setBounds(10, 130, 89, 25);
		mid2.add(lblReporttype);
		
		lblReporttype3 = new WebLabel("ReportType3");
		lblReporttype3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblReporttype3.setBounds(10, 160, 89, 25);
		mid2.add(lblReporttype3);
		
		TextFieldCode = new WebTextField();
		TextFieldCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TextFieldCode.setEditable(false);
		TextFieldCode.setBounds(143, 10, 142, 25);
		mid2.add(TextFieldCode);
		TextFieldCode.setColumns(10);
		
		TextFieldName = new WebTextField();
		TextFieldName.setEditable(false);
		TextFieldName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TextFieldName.setBounds(143, 40, 567, 25);
		mid2.add(TextFieldName);
		TextFieldName.setColumns(10);
		
		TextFieldICDCM = new WebTextField();
		TextFieldICDCM.setEditable(false);
		TextFieldICDCM.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TextFieldICDCM.setBounds(143, 70, 202, 25);
		mid2.add(TextFieldICDCM);
		TextFieldICDCM.setColumns(10);
		
		TextFieldReport1 = new WebTextField();
		TextFieldReport1.setEditable(false);
		TextFieldReport1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TextFieldReport1.setBounds(143, 100, 86, 25);
		mid2.add(TextFieldReport1);
		TextFieldReport1.setColumns(10);
		
		TextFieldReport2 = new WebTextField();
		TextFieldReport2.setEditable(false);
		TextFieldReport2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TextFieldReport2.setBounds(143, 130, 86, 25);
		mid2.add(TextFieldReport2);
		TextFieldReport2.setColumns(10);
		
		TextFieldReport3 = new WebTextField();
		TextFieldReport3.setEditable(false);
		TextFieldReport3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TextFieldReport3.setBounds(143, 160, 86, 25);
		mid2.add(TextFieldReport3);
		TextFieldReport3.setColumns(10); 
		
		WebLabel lblCat = new WebLabel("Category");
		lblCat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCat.setBounds(10, 190, 90, 25);
		mid2.add(lblCat);
		
		WebLabel lblSubCat = new WebLabel("Sub Category");
		lblSubCat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSubCat.setBounds(10, 220, 90, 25);
		mid2.add(lblSubCat);
		
		TextFieldCat = new WebTextField();
		TextFieldCat.setEditable(false);
		TextFieldCat.setBounds(143, 190, 500, 25);
		mid2.add(TextFieldCat);
		TextFieldCat.setColumns(10);
		
		TextFieldSubCat = new WebTextField();
		TextFieldSubCat.setEditable(false);
		TextFieldSubCat.setBounds(143, 220, 500, 25);
		mid2.add(TextFieldSubCat);
		TextFieldSubCat.setColumns(10);
	}
	public void getData(){
		 
		table.setModel(fetchData());
		TableColumnModel columnModel = table.getColumnModel();		
		columnModel.getColumn(0).setPreferredWidth(50);
		columnModel.getColumn(1).setPreferredWidth(140);
		columnModel.getColumn(2).setPreferredWidth(0);
		columnModel.getColumn(2).setMinWidth(0);
		columnModel.getColumn(2).setMaxWidth(0);
		columnModel.getColumn(3).setPreferredWidth(0);
		columnModel.getColumn(3).setMinWidth(0);
		columnModel.getColumn(3).setMaxWidth(0);
		columnModel.getColumn(4).setPreferredWidth(0);
		columnModel.getColumn(4).setMinWidth(0);
		columnModel.getColumn(4).setMaxWidth(0);
		columnModel.getColumn(5).setPreferredWidth(0);
		columnModel.getColumn(5).setMinWidth(0);
		columnModel.getColumn(5).setMaxWidth(0);
		columnModel.getColumn(6).setPreferredWidth(0);
		columnModel.getColumn(6).setMinWidth(0);
		columnModel.getColumn(6).setMaxWidth(0);
		columnModel.getColumn(7).setPreferredWidth(0);
		columnModel.getColumn(7).setMinWidth(0);
		columnModel.getColumn(7).setMaxWidth(0);
		columnModel.getColumn(8).setPreferredWidth(0);
		columnModel.getColumn(8).setMinWidth(0);
		columnModel.getColumn(8).setMaxWidth(0);


		((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchData(){
		 
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Connection conn,conn6 ;
		PreparedStatement stmt,stmt6 ;
	 	String sql = "select code,name,icdcm_code,report1,report2,report3,categoryname,subcategoryname  from dentalcode where status='1'order by code";
		int p=1;
		try {
			conn =  new DBmanager().getConnMySql();
	        stmt = conn.prepareStatement(sql);
	        ResultSet rs = stmt.executeQuery();
	     
	        while (rs.next()) {
	        	String name="",icdcm_code="",report1="",report2="",report3="",cat="",subcat="";
	        	if(rs.getString(2) ==null){
					name="";
				}else{
					name=rs.getString(2).trim();
				}
	        	if(rs.getString(3) ==null){
					icdcm_code="";
				}else{
					icdcm_code=rs.getString(3).trim();
				}
	        	if(rs.getString(4) ==null){
					report1="";
				}else{
					report1=rs.getString(4).trim();
				}
	        	if(rs.getString(5) ==null){
					report2="";
				}else{
					report2=rs.getString(5).trim();
				}
	        	if(rs.getString(6) ==null){
					report3="";
				}else{
					report3=rs.getString(6).trim();
				}
	        	if(rs.getString(7) ==null){
					cat="";
				}else{
					cat=rs.getString(7).trim();
				}
	        	if(rs.getString(8) ==null){
					subcat="";
				}else{
					subcat=rs.getString(8).trim();
				}
	         
	        	final Vector<String> vstring = new Vector<String>();
	        	 
	        	vstring.add(Integer.toString(p));
	        	vstring.add(rs.getString(1).trim());
	        	vstring.add(name);
	        	vstring.add(icdcm_code);
	        	vstring.add(report1);
	        	vstring.add(report2);
	        	vstring.add(report3);
	        	vstring.add(cat);
	        	vstring.add(subcat);
	        	data.add(vstring);
		       
	        	p++;
	        }
	        stmt.close();
	    	conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DefaultTableModel(data, columnNames);
	}
	public void clearData(){
		TextFieldCode.setText("");
		TextFieldName.setText("");
		TextFieldICDCM.setText("");
		TextFieldReport1.setText("");
		TextFieldReport2.setText("");
		TextFieldReport3.setText("");
		TextFieldCat.setText("");
		TextFieldSubCat.setText("");
	}
	public void saveData(){
		String code=TextFieldCode.getText(),icdcm="",report1="",report2="",report3="";
		if(code.equals("")){
			
		}else{
			code=TextFieldCode.getText();
			icdcm=TextFieldICDCM.getText();
			report1=TextFieldReport1.getText();
			report2=TextFieldReport2.getText();
			report3=TextFieldReport3.getText();
			//System.out.println(code+" "+icdcm+" "+report1+" "+report2);
			
			try {
				
				Connection conn = new DBmanager().getConnMySql();
				String sql_update="update dentalcode set icdcm_code=?,report1=?,report2=?,report3=?  where code=? ";
				PreparedStatement stmt3 = conn.prepareStatement(sql_update);
				stmt3.setString(1, icdcm);
				stmt3.setString(2, report1);	
				stmt3.setString(3, report2);
				stmt3.setString(4, report3);
				stmt3.setString(5, code);
				stmt3.executeUpdate();
				stmt3.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
