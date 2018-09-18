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
package org.utt.app.adm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

public class IPDReport  extends WebPanel{
	int width,height;
	WebTable table;
	Vector<Vector<String>> data;
	Vector<String> columnNames;
	WebScrollPane scrollPane;
	WebRadioButton userStatus1,userStatus2,ButtonStatus1,ButtonStatus2,ButtonTOP1,ButtonTOP2;
	ButtonGroup bg = new ButtonGroup();
	
	ButtonGroup bgtop = new ButtonGroup();
	ButtonGroup bgstatus = new ButtonGroup();
	WebTextField TextField_code,TextField_name;
	
	WebButton ButtonRefresh,ButtonDownload,ButtonUpload,ButtonPreview,ButtonNew,ButtonEdit,ButtonDel,ButtonClear,ButtonSave;
	
	WebPanel leftPanel ,midPanel;
	
	String formcode="",clinic="";


	String typeprint="";
	
	public IPDReport(int w,int h) {
		width=w;
	    height=h;
	    leftPanel = new WebPanel();
		leftPanel.setBackground(Setup.getColor());
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setPreferredSize(new Dimension(450, height-175));
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		WebPanel topPanel = new WebPanel();
		leftPanel.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(null);
		topPanel.setPreferredSize(new Dimension(450, 30));
		
		WebLabel Label_Info = new WebLabel(" รายชื่อแบบฟอร์ม");
		//Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Label_Info.setBounds(160,5,100,20);
		topPanel.add(Label_Info);
		
		userStatus1 = new WebRadioButton("ALL");
		userStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		bg.add(userStatus1);
		userStatus1.setBounds(300, 5, 50, 25);
		topPanel.add(userStatus1);
		
		userStatus2 = new WebRadioButton("Active");
		userStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		userStatus2.setSelected(true);
		bg.add(userStatus2);
		userStatus2.setEnabled(true);
		userStatus2.setBounds(360, 5, 70, 25);
		topPanel.add(userStatus2);
		
		columnNames = new Vector<String>();
  		columnNames.add("ที่");
  		columnNames.add(" ");
		columnNames.add(" ");
		for (int n = 4; n < 5; n++) {	
			columnNames.add("  ชื่อ  ");
		}
		
		data = new Vector<Vector<String>>();
		DefaultTableModel model = new DefaultTableModel(data, columnNames){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		table = new WebTable(model){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				
				return c;
			}
		};
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        ClearTextField();

		       // if(table.getValueAt(row,1).toString().trim().equals("0016")){
		       // 	ButtonStatus1.setSelected(true);
		       // }else{
		       // 	ButtonStatus2.setSelected(true);
		       // }
		        if(table.getValueAt(row,2).toString().trim().equals("0016")){
		        	ButtonTOP2.setSelected(true);
		        	typeprint="2";
		        }else{
		        	ButtonTOP1.setSelected(true);
		        }

		        //username=table.getValueAt(row,2).toString().trim();
		        //op=table.getValueAt(row,6).toString().trim();
		        /*
		        if(op.equals("6")){
		        	setAppCodeIPD(table.getValueAt(row,2).toString().trim());
		        }else{
		        	setAppCode(table.getValueAt(row,2).toString().trim());
		        }
		         */
		        formcode=table.getValueAt(row,2).toString().trim();
		        TextField_code.setText(table.getValueAt(row,2).toString().trim());
		        TextField_name.setText(table.getValueAt(row,3).toString().trim());

		       
			}
		});
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		table.setRowHeight(25);		
		table.setFillsViewportHeight(true);
 
		scrollPane = new WebScrollPane(table);
		getData();
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		
		WebPanel bottomPanel = new WebPanel();
		bottomPanel.setPreferredSize(new Dimension((width*2)/10, 30));
		leftPanel.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(null);
		
		ButtonRefresh = new WebButton("Refresh");
		ButtonRefresh.setForeground(UIManager.getColor("Button.darkShadow"));

		ButtonRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getData();
				//hack();
			}
		});
		ButtonRefresh.setBounds((450/2-45), 2, 90, 25);
		bottomPanel.add(ButtonRefresh);
		
		//right
		midPanel = new WebPanel();
		add(midPanel, BorderLayout.CENTER);
		midPanel.setLayout(null);
		
		WebLabel Label_Info_1 = new WebLabel("ข้อมูลแบบฟอร์ม");
		Label_Info_1.setHorizontalAlignment(SwingConstants.CENTER);
		Label_Info_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		Label_Info_1.setBounds(10, 11, 182, 25);
		midPanel.add(Label_Info_1);
		
		WebLabel Label_code = new WebLabel("CODE");
		Label_code.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_code.setHorizontalAlignment(SwingConstants.LEFT);
		Label_code.setBounds(20, 45, 80, 20);
		midPanel.add(Label_code);
		

		
		WebLabel Label_name = new WebLabel("Name");
		Label_name.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_name.setHorizontalAlignment(SwingConstants.LEFT);
		Label_name.setBounds(20, 75, 80, 20);
		midPanel.add(Label_name);

		
		WebLabel Label_top = new WebLabel("Type of Print");
		Label_top.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_top.setHorizontalAlignment(SwingConstants.LEFT);
		Label_top.setBounds(20, 225, 80, 20);
		midPanel.add(Label_top);
		
		WebLabel Label_status = new WebLabel("Status");
		Label_status.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_status.setHorizontalAlignment(SwingConstants.LEFT);
		Label_status.setBounds(20, 255, 80, 20);
		midPanel.add(Label_status);
		
		
		////
		TextField_code = new WebTextField();
		TextField_code.setEditable(false);
		TextField_code.setBounds(130, 45, 100, 25);
		midPanel.add(TextField_code);
		

		
		TextField_name = new WebTextField();
		TextField_name.setEditable(false);
		TextField_name.setBounds(130, 75, 350, 25);
		midPanel.add(TextField_name);
		

		
		ButtonTOP1 = new WebRadioButton("A4");
		ButtonTOP1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		bgtop.add(ButtonTOP1);
		ButtonTOP1.setBounds(130, 225, 100, 25);
		midPanel.add(ButtonTOP1);
		
		ButtonTOP2 = new WebRadioButton("A5");
		ButtonTOP2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		bgtop.add(ButtonTOP2);
		ButtonTOP2.setBounds(230, 225, 100, 23);
		midPanel.add(ButtonTOP2);
		
		ButtonStatus1 = new WebRadioButton("Enable");
		ButtonStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		bgstatus.add(ButtonStatus1);
		ButtonStatus1.setBounds(130, 255, 100, 25);
		midPanel.add(ButtonStatus1);
		
		ButtonStatus2 = new WebRadioButton("Disable");
		ButtonStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		bgstatus.add(ButtonStatus2);
		ButtonStatus2.setBounds(230, 255, 100, 23);
		midPanel.add(ButtonStatus2);
		
		////
		WebLabel Label_admin= new WebLabel("Administration Report File");
		Label_admin.setFont(new Font("Tahoma", Font.BOLD, 14));
		Label_admin.setHorizontalAlignment(SwingConstants.LEFT);
		Label_admin.setBounds(20, 290, 200, 30);
		midPanel.add(Label_admin);
		
		ButtonDownload = new WebButton("DownLoad File");
		ButtonDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//getFile(formcode);
			}
		});
		ButtonDownload.setBounds(20,330, 150, 25);
		midPanel.add(ButtonDownload);
		
		ButtonPreview = new WebButton("Print Preview");
		ButtonPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 //printReport(formcode,typeprint);
			}
		});
		ButtonPreview.setBounds(20,415, 150, 25);
		midPanel.add(ButtonPreview);
		

		
		ButtonUpload = new WebButton("Send File");
		ButtonUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//sendFile(formcode);
			}
		});
		ButtonUpload.setBounds(20,375, 150, 25);
		midPanel.add(ButtonUpload);

		//edit
		ButtonNew = new WebButton("New Report");
		ButtonNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		ButtonNew.setBounds(300,330, 150, 25);
		midPanel.add(ButtonNew);
		
		ButtonEdit = new WebButton("Edit Report");
		ButtonEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 //edit(formcode,clinic);
				 ClearTextField();
			}
		});
		ButtonEdit.setBounds(300,375, 150, 25);
		midPanel.add(ButtonEdit);
		
		ButtonClear = new WebButton("CLEAR");
		ButtonClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClearTextField();
			}
		});
		ButtonClear.setBounds(470,330, 100, 25);
		midPanel.add(ButtonClear);
		
		ButtonDel = new WebButton("DELETE");
		ButtonDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		ButtonDel.setBounds(470,375, 100, 25);
		midPanel.add(ButtonDel);

		ButtonSave = new WebButton("Save");
		ButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
			}
		});
		ButtonSave.setBounds(600,330, 100, 70);
		midPanel.add(ButtonSave);
	}
	public void getData(){
		table.setModel(fetchData());
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(0);
		columnModel.getColumn(1).setMinWidth(0);
		columnModel.getColumn(1).setMaxWidth(0);
		columnModel.getColumn(2).setPreferredWidth(60 );
		columnModel.getColumn(3).setPreferredWidth(290);
	
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchData(){
		Connection conn;
		PreparedStatement stmt,stmt1;
		ResultSet rs,rs1;
		String query = "select id,form_code,form_name from formipd_master where status='1' order by form_code" ;
		
		
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		try {
			
			conn = new DBmanager().getConnMySql();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			int p=1;
			while (rs.next()) {
				final Vector<String> vstring = new Vector<String>();
				vstring.add(" "+Integer.toString(p) );
	            vstring.add("  "+rs.getString(1));
	            vstring.add("  "+rs.getString(2));
	            vstring.add("  "+rs.getString(3));

	          
	            
	             
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
	public void ClearTextField(){
		formcode="";
		typeprint="";
		TextField_code.setText("");

		TextField_name.setText("");

		bgstatus.clearSelection();
		bgtop.clearSelection();

	}

}
