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
import java.awt.Color;
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
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

public class CompControl extends WebPanel{
	int width,height;
	String comp_control="",compStatus="where status='1' ",comp_status="1";
	
	Vector<Vector<String>> data;
	WebButton ButtonRefresh;
	Vector<String> columnNames;
	WebScrollPane scrollPane;
	WebTable table;
	WebRadioButton compStatus1,compStatus2, ButtonStatus1, ButtonStatus2;
	WebPanel rightPanel,right1Panel,right2Panel;
	WebLabel Label_ID,Label_Ip,Label_Name,Label_Version,Label_Status;
	WebTextField TextField_ID,TextField_IPuser,TextField_Computername,TextField_Version;
	WebButton ButtonNew,ButtonSave,ButtonDel,ButtonClear,ButtonRefresh_1;
	ButtonGroup bg = new ButtonGroup();
	
	public CompControl(int w,int h) {
		width=w;
        height=h;
        comp_control="0";
        
        setPreferredSize(new Dimension(width, height-175));
		setLayout(new BorderLayout(0, 0));
        
        WebPanel leftPanel = new WebPanel();
		leftPanel.setBackground(new Color(206,203,208));
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setPreferredSize(new Dimension((width*2)/10, height-175));
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		WebPanel topPanel = new WebPanel();
		leftPanel.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(null);
		topPanel.setPreferredSize(new Dimension((width*2)/10, 30));
		
		WebLabel Label_Info = new WebLabel(" ข้อมูลเครื่องคอมพิวเตอร์");
		Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Label_Info.setBounds(10,5,150,20);
		topPanel.add(Label_Info);
		
		compStatus1 = new WebRadioButton("ALL");
		compStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				compStatus="";
			}
		});
		bg.add(compStatus1);
		compStatus1.setBounds(150, 5, 50, 25);
		topPanel.add(compStatus1);
		
		compStatus2 = new WebRadioButton("Active");
		compStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compStatus="where status='1' ";
			}
		});
		compStatus2.setSelected(true);
		bg.add(compStatus2);
		compStatus2.setEnabled(true);
		compStatus2.setBounds(200, 5, 70, 25);
		topPanel.add(compStatus2);
		
		columnNames = new Vector<String>();
  		columnNames.add("ที่");
  		columnNames.add("");
		columnNames.add(" Computer Name ");;
		columnNames.add(" IP ");
		columnNames.add("");
		columnNames.add("");
		
		data = new Vector<Vector<String>>();
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
					int modelRow = convertRowIndexToModel(row);
					String type = (String)getModel().getValueAt(modelRow, 1);
					String status=type.trim();					  
					if ("1".equals(status)){
						c.setBackground(new Color(240, 248, 255));						
					}
				}
				return c;
			}
		};
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        String id=table.getValueAt(row,1).toString().trim();
		        String compname=table.getValueAt(row,2).toString().trim();
		        String ipuser=table.getValueAt(row,3).toString().trim();
		        String version=table.getValueAt(row,4).toString().trim();
		        //String status=table.getValueAt(row,5).toString().trim();
		        TextField_ID.setText(id);
				TextField_IPuser.setText(ipuser);
				TextField_Computername.setText(compname);
				TextField_Version.setText(version);
				 if(table.getValueAt(row,5).toString().trim().equals("1")){
			        	ButtonStatus1.setSelected(true);
			        }else{
			        	ButtonStatus2.setSelected(true);
			        }
				 
    
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
			}
		});
		ButtonRefresh.setBounds((((width*2)/10)/2-45), 2, 90, 25);
		bottomPanel.add(ButtonRefresh);
		
		rightPanel = new WebPanel();
		add(rightPanel, BorderLayout.CENTER);
		rightPanel.setLayout(null);
		
		right1Panel = new WebPanel();
		right1Panel.setBorder(new TitledBorder(null, "\u0E23\u0E32\u0E22\u0E25\u0E30\u0E40\u0E2D\u0E35\u0E22\u0E14\u0E40\u0E04\u0E23\u0E37\u0E48\u0E2D\u0E07\u0E04\u0E2D\u0E21\u0E1E\u0E34\u0E27\u0E40\u0E15\u0E2D\u0E23\u0E4C", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		right1Panel.setBounds(5, 5, 317, 200);
		rightPanel.add(right1Panel);
		right1Panel.setLayout(null);
		
		Label_ID = new WebLabel("ID");
		Label_ID.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_ID.setBounds(10, 25, 46, 14);
		right1Panel.add(Label_ID);
		
		Label_Ip = new WebLabel("IP");
		Label_Ip.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Ip.setBounds(10, 50, 46, 14);
		right1Panel.add(Label_Ip);
		
		Label_Name = new WebLabel("Name");
		Label_Name.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Name.setBounds(10, 75, 46, 14);
		right1Panel.add(Label_Name);
		
		Label_Version = new WebLabel("Version");
		Label_Version.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Version.setBounds(10, 100, 46, 14);
		right1Panel.add(Label_Version);
		
		Label_Status = new WebLabel("Status");
		Label_Status.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Status.setBounds(10, 125, 46, 14);
		right1Panel.add(Label_Status);
		
		TextField_ID = new WebTextField();
		TextField_ID.setEditable(false);
		TextField_ID.setBounds(66, 25, 86, 20);
		right1Panel.add(TextField_ID);
		TextField_ID.setColumns(10);
		
		TextField_IPuser = new WebTextField();
		TextField_IPuser.setBounds(66, 50, 86, 20);
		right1Panel.add(TextField_IPuser);
		TextField_IPuser.setColumns(10);
		
		TextField_Computername = new WebTextField();
		TextField_Computername.setBounds(66, 75, 120, 20);
		right1Panel.add(TextField_Computername);
		TextField_Computername.setColumns(10);
		
		TextField_Version = new WebTextField();
		TextField_Version.setBounds(66, 100, 86, 20);
		right1Panel.add(TextField_Version);
		TextField_Version.setColumns(10);
		
		ButtonStatus1 = new WebRadioButton("Enable");
		bg.add(ButtonStatus1);
		ButtonStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comp_status="1";
			}
		});
		ButtonStatus1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonStatus1.setBounds(66, 125, 70, 23);
		right1Panel.add(ButtonStatus1);
		
		ButtonStatus2 = new WebRadioButton("Disable");
		bg.add(ButtonStatus2);
		ButtonStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comp_status="2";
			}
		});
		ButtonStatus2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonStatus2.setBounds(147, 125, 70, 23);
		right1Panel.add(ButtonStatus2);
		
		right2Panel = new WebPanel();
		right2Panel.setBorder(new LineBorder(new Color(176, 196, 222), 1, true));
		right2Panel.setBounds(5, 210, 317, 115);
		rightPanel.add(right2Panel);
		right2Panel.setLayout(null);
		
		ButtonNew = new WebButton("New");
		ButtonNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextField_IPuser.setEditable(true);
				ClearTextField();
				comp_control="1";
			}
		});
		ButtonNew.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonNew.setBounds(10, 29, 89, 23);
		right2Panel.add(ButtonNew);
		
		ButtonSave = new WebButton("Save");
		ButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveComp();
				TextField_IPuser.setEditable(false);
				comp_control="0";
				ClearTextField();
				getData();
			}
		});
		ButtonSave.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonSave.setBounds(109, 29, 89, 23);
		right2Panel.add(ButtonSave);
		
		ButtonDel = new WebButton("Del");
		ButtonDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ipuser=TextField_IPuser.getText().trim();
				String sql_deluser = "update incomputer set status = '3'where ipuser=?";
				try {
			
					Connection conn_user_del =new DBmanager().getConnMySql();
					PreparedStatement stmt_user_del = conn_user_del.prepareStatement(sql_deluser);
					stmt_user_del.setString(1,ipuser);
					int rs_update_user =  stmt_user_del.executeUpdate();
					//System.out.println("save update user.."+rs_update_user);
					stmt_user_del.close();
					conn_user_del.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				TextField_IPuser.setEditable(false);
				comp_control="0";
				ClearTextField();
			}
		});
		ButtonDel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonDel.setBounds(40, 63, 89, 23);
		right2Panel.add(ButtonDel);
		
		ButtonClear = new WebButton("Clear");
		ButtonClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextField_IPuser.setEditable(false);
				comp_control="0";
				ClearTextField();
			}
		});
		ButtonClear.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonClear.setBounds(208, 30, 89, 23);
		right2Panel.add(ButtonClear);
		
		ButtonRefresh_1 = new WebButton("Refresh");
		ButtonRefresh_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextField_IPuser.setEditable(false);
				comp_control="0";
				ClearTextField();
				getData();
			}
		});
		ButtonRefresh_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonRefresh_1.setBounds(156, 63, 89, 23);
		right2Panel.add(ButtonRefresh_1);
	}
	public void getData(){
		table.setModel(fetchData());
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(0);
		columnModel.getColumn(1).setMinWidth(0);
		columnModel.getColumn(1).setMaxWidth(0);
		columnModel.getColumn(2).setPreferredWidth((width*2)/10-160);
		columnModel.getColumn(3).setPreferredWidth( 120);
		columnModel.getColumn(4).setPreferredWidth(0);
		columnModel.getColumn(4).setMinWidth(0);
		columnModel.getColumn(4).setMaxWidth(0);
		columnModel.getColumn(5).setPreferredWidth(0);
		columnModel.getColumn(5).setMinWidth(0);
		columnModel.getColumn(5).setMaxWidth(0);
 
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchData(){
		Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		String query = "select id,ipuser,computername,version,status from incomputer "+compStatus+" order by id" ;
		
		
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		try {
		
			conn = new DBmanager().getConnMySql();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			int p=1;
			while (rs.next()) {
				final Vector<String> vstring = new Vector<String>();
				vstring.add(" "+Integer.toString(p) );
	            vstring.add(String.valueOf(rs.getInt(1)));
	            vstring.add("  "+rs.getString(3));
	            vstring.add("  "+rs.getString(2));
	            vstring.add(rs.getString(4));
	            vstring.add(rs.getString(5));
	             
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
		
		TextField_ID.setText("");
		TextField_Computername.setText("");
		TextField_IPuser.setText("");
		TextField_Version.setText("");
		bg.clearSelection();
		comp_status="1";
	}
	public void saveComp(){
		int check_ipuser=0;
		Connection conn_user_check;
		PreparedStatement stmt_user_check;
        ResultSet rs_user_check;
        
        try {
        	
			conn_user_check = new DBmanager().getConnMySql();
			if(comp_control.equals("1")){
				if(!Setup.checkEmptyTextField(TextField_IPuser)){
					String ipuser=TextField_IPuser.getText().trim();
					String query_user_check="select ipuser  from incomputer where ipuser=?";
					stmt_user_check = conn_user_check.prepareStatement(query_user_check);
					stmt_user_check.setString(1,ipuser); 
					rs_user_check = stmt_user_check.executeQuery();
					while (rs_user_check.next()) {
						if(rs_user_check.getString(1) !=null){
							WebOptionPane.showMessageDialog(this,"IP Address duplicate !!","IP Address Error",WebOptionPane.ERROR_MESSAGE);
							check_ipuser=1;
						}
					}
					rs_user_check.close();
					stmt_user_check.close();
					////end ip in
					if(check_ipuser==0){
						String sql_insertuser = " insert into incomputer (ipuser,computername,version,status,datemodify) "
								+ "values (?,?,?,'1','"+Setup.GetDateNow()+"')";
						PreparedStatement stmt_insert_user = conn_user_check.prepareStatement(sql_insertuser);
						stmt_insert_user.setString(1,ipuser); 
						stmt_insert_user.setString(2,TextField_Computername.getText().trim());
						stmt_insert_user.setString(3,TextField_Version.getText().trim()); 
						int rs_insert_user =  stmt_insert_user.executeUpdate();
						//System.out.println("save update user.."+rs_update_user);
						stmt_insert_user.close();
					}else{
						check_ipuser=0;
					}
				}else{
					WebOptionPane.showMessageDialog(this,"Please check IP Address ","IP Address Error",WebOptionPane.ERROR_MESSAGE);
				}
				comp_control="0";
		        TextField_IPuser.setEditable(false);
			}
			else if(comp_control.equals("0")){
				String ipuser=TextField_IPuser.getText().trim();
				String sql_updateuser = "update incomputer set  computername=?,version=?,status=?,datemodify=? where ipuser=?";
				PreparedStatement stmt_user_check1 = conn_user_check.prepareStatement(sql_updateuser);
				stmt_user_check1.setString(1,TextField_Computername.getText().trim());
				stmt_user_check1.setString(2,TextField_Version.getText().trim());
				stmt_user_check1.setString(3, comp_status);
				stmt_user_check1.setString(4, Setup.GetDateNow());
				stmt_user_check1.setString(5, ipuser);
				int rs_update_user =  stmt_user_check1.executeUpdate();
				 
				stmt_user_check1.close();
			}
			conn_user_check.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
