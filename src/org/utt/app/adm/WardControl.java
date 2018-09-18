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
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.utt.app.dao.DBmanager;
import org.utt.app.util.ArrayListTransferHandler;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

public class WardControl extends WebPanel{
	int width,height;
	String [][] ward_indb=null ;
	String [][] ward_userold=null ;
	String [][] ward_usernew=null ;
	String [][] ward_userdiff=null ;
	String [][] report_indb=null ;
	String [][] report_userold=null ;
	String [][] report_usernew=null ;
	String [][] report_userdiff=null ;
	String userStatus="where status='1' ",username="",user_control="",op="",user_status="1";
	DefaultListModel from,move,fromReport,moveReport;
	Vector<String> columnNames;
	Vector<Vector<String>> data;
	String wardcode="";
	
	WebTable table;
	WebButton ButtonRefresh,btnDelete,btnSave,btnNew,btnClear,btnDel,btnRefresh,btnDelete_1;
	WebScrollPane scrollPane;
	WebPanel midPanel,ButtomPanel,DiagRoomPanel,ReportPanel,ButtonReportPanel;
	WebLabel Label_Code,Label_DoctorName,Label_Status;
	WebTextField TextField_ID,TextField_Code,TextField_DoctorName;
	JList dragFrom,moveTo,dragReportFrom,moveReportTo;
	WebRadioButton userStatus1,userStatus2,ButtonStatus1,ButtonStatus2;
	JScrollPane spFrom,spTo,spReportFrom,spReportTo;
	
	ButtonGroup bg = new ButtonGroup();
	ButtonGroup bgStatus = new ButtonGroup();
	
	ArrayListTransferHandler arrayListHandler;
	
	 public WardControl(int w,int h) {
	        width=w;
	        height=h;
	 
	        user_control="0";
			setPreferredSize(new Dimension(width, height-175));
			setLayout(new BorderLayout(0, 0));
			
			arrayListHandler = new ArrayListTransferHandler();
			
			WebPanel leftPanel = new WebPanel();
			leftPanel.setBackground(new Color(206,203,208));
			add(leftPanel, BorderLayout.WEST);
			leftPanel.setPreferredSize(new Dimension((width*2)/10, height-175));
			leftPanel.setLayout(new BorderLayout(0, 0));
			
			WebPanel topPanel = new WebPanel();
			leftPanel.add(topPanel, BorderLayout.NORTH);
			topPanel.setLayout(null);
			topPanel.setPreferredSize(new Dimension((width*2)/10, 30));
			
			WebLabel Label_Info = new WebLabel(" ข้อมูล  ward");
			Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 12));
			Label_Info.setBounds(30,5,100,20);
			topPanel.add(Label_Info);
			
			userStatus1 = new WebRadioButton("ALL");
			userStatus1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					userStatus="";
					user_status="2";
				}
			});
			bg.add(userStatus1);
			userStatus1.setBounds(150, 5, 50, 25);
			topPanel.add(userStatus1);
			
			userStatus2 = new WebRadioButton("Active");
			userStatus2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					userStatus="where status='1' ";
					user_status="1";
				}
			});
			userStatus2.setSelected(true);
			bg.add(userStatus2);
			userStatus2.setEnabled(true);
			userStatus2.setBounds(200, 5, 70, 25);
			topPanel.add(userStatus2);
			
			columnNames = new Vector<String>();
	 		columnNames.add("ที่");
	 		columnNames.add(" ");
			columnNames.add(" Code ");
			columnNames.add(" ");
			columnNames.add(" Ward Name ");
			for (int n = 5; n < 7; n++) {	
				columnNames.add(" ");
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
					if (!isRowSelected(row)){
						c.setBackground(getBackground());
						int modelRow = convertRowIndexToModel(row);
						String type = (String)getModel().getValueAt(modelRow, 4);
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
			        ClearTextField();
			        TextField_ID.setEditable(false);
			        TextField_ID.setText(table.getValueAt(row,1).toString().trim());
			        TextField_Code.setEditable(false);
			        TextField_Code.setText(table.getValueAt(row,2).toString().trim());
			        TextField_DoctorName.setText(table.getValueAt(row,4).toString().trim());
			        wardcode=table.getValueAt(row,2).toString().trim();
			        //TextField_Type.setText(table.getValueAt(row,6).toString().trim());
			        //TextField_Clinic_code.setText(table.getValueAt(row,7).toString().trim());
			        //TextField_Report_code.setText(table.getValueAt(row,9).toString().trim());
			        //TextField_Certifyno.setText(table.getValueAt(row,10).toString().trim());
			        if(table.getValueAt(row,6).toString().trim().equals("1")){
			        	ButtonStatus1.setSelected(true);
			        }else{
			        	ButtonStatus2.setSelected(true);
			        }
			        username=table.getValueAt(row,2).toString().trim();
			        
			        setAppCodeIPD(table.getValueAt(row,2).toString().trim());
			        setReportCodeIPD(table.getValueAt(row,2).toString().trim());
			        
			         
			       
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
			
			midPanel = new WebPanel();
			add(midPanel, BorderLayout.CENTER);
			midPanel.setLayout(null);
			
			WebLabel Label_Info_1 = new WebLabel("ข้อมูลผู้ใช้งาน");
			Label_Info_1.setHorizontalAlignment(SwingConstants.CENTER);
			Label_Info_1.setFont(new Font("Tahoma", Font.BOLD, 13));
			Label_Info_1.setBounds(10, 11, 182, 25);
			midPanel.add(Label_Info_1);
			
			WebLabel Label_ID = new WebLabel("ID");
			Label_ID.setFont(new Font("Tahoma", Font.PLAIN, 13));
			Label_ID.setHorizontalAlignment(SwingConstants.LEFT);
			Label_ID.setBounds(20, 45, 80, 20);
			midPanel.add(Label_ID);
			
			Label_Code = new WebLabel("Code");
			Label_Code.setFont(new Font("Tahoma", Font.PLAIN, 13));
			Label_Code.setHorizontalAlignment(SwingConstants.LEFT);
			Label_Code.setBounds(20, 75, 80, 20);
			midPanel.add(Label_Code);
			
			Label_DoctorName = new WebLabel("Ward Name");
			Label_DoctorName.setFont(new Font("Tahoma", Font.PLAIN, 13));
			Label_DoctorName.setHorizontalAlignment(SwingConstants.LEFT);
			Label_DoctorName.setBounds(20, 105, 80, 20);
			midPanel.add(Label_DoctorName);
			
			 
			
			Label_Status = new WebLabel("Status");
			Label_Status.setFont(new Font("Tahoma", Font.PLAIN, 13));
			Label_Status.setHorizontalAlignment(SwingConstants.LEFT);
			Label_Status.setBounds(20, 345, 80, 20);
			midPanel.add(Label_Status);
			
			TextField_ID = new WebTextField();
			TextField_ID.setEditable(false);
			TextField_ID.setBounds(110, 45, 100, 20);
			midPanel.add(TextField_ID);
			
			TextField_Code = new WebTextField();
			TextField_Code.setEditable(false);
			TextField_Code.setBounds(110, 75, 100, 20);
			midPanel.add(TextField_Code);
			
			TextField_DoctorName = new WebTextField();
			TextField_DoctorName.setBounds(110, 105, 200, 20);
			midPanel.add(TextField_DoctorName);
			
			 
			ButtonStatus1 = new WebRadioButton("Enable");
			ButtonStatus1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					userStatus="where status='1' ";
				}
			});
			bgStatus.add(ButtonStatus1);
			ButtonStatus1.setBounds(106, 345, 64, 23);
			midPanel.add(ButtonStatus1);
			
			ButtonStatus2 = new WebRadioButton("ALL");
			ButtonStatus2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					userStatus="";
				}
			});
			bgStatus.add(ButtonStatus2);
			ButtonStatus2.setBounds(174, 345, 64, 23);
			midPanel.add(ButtonStatus2);
			
			DiagRoomPanel = new WebPanel();
			DiagRoomPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Doctor List", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			DiagRoomPanel.setBounds(357, 17, 461, 200);
			midPanel.add(DiagRoomPanel);
			DiagRoomPanel.setLayout(null);
			
			setInit();
			setInitReport();
			 
			from = new DefaultListModel();
			move = new DefaultListModel();
		
			spFrom = new JScrollPane();
			spFrom.setBounds(6, 16, 220, 144);
			spFrom.setPreferredSize(new Dimension(220, 200));
			DiagRoomPanel.add(spFrom);
			
			dragFrom = new JList(from);
			dragFrom.setTransferHandler(new FromTransferHandler());
			dragFrom.setPrototypeCellValue("List Item WWWWWW");
			dragFrom.setDragEnabled(true);
			dragFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			spFrom.setViewportView(dragFrom);
			
			spTo = new JScrollPane();
			spTo.setBounds(235, 16, 220, 144);
			spTo.setPreferredSize(new Dimension(220, 200));
			DiagRoomPanel.add(spTo);
			
			 
			moveTo = new WebList(move);
			moveTo.setTransferHandler(new ToTransferHandler(TransferHandler.MOVE));
		    moveTo.setDropMode(DropMode.INSERT);
			spTo.setViewportView(moveTo);
			
			ButtomPanel = new WebPanel();
			ButtomPanel.setBounds(6, 160, 449, 33);
			DiagRoomPanel.add(ButtomPanel);
			
			btnDelete = new WebButton("Delete");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					move = (DefaultListModel) moveTo.getModel();
					int selectedIndex = moveTo.getSelectedIndex();
					if (selectedIndex != -1) {
					    move.remove(selectedIndex);
					}
				}
			});
			ButtomPanel.add(btnDelete);
			
			btnSave = new WebButton("Save");
			btnSave.setBounds(110, 375, 150, 23);
			midPanel.add(btnSave);
			
			btnNew = new WebButton("New");
			btnNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TextField_ID.setEditable(true);
					TextField_Code.setEditable(true);
					ClearTextField();
					user_control="1";
				}
			});
			btnNew.setBounds(20, 376, 60, 23);
			btnNew.setEnabled(false);
			midPanel.add(btnNew);
			
			btnClear = new WebButton("Clear");
			btnClear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					user_control="0";
					TextField_ID.setEditable(false);
					TextField_Code.setEditable(false);
					ClearTextField();
				}
			});
			btnClear.setBounds(20, 410, 60, 23);
			midPanel.add(btnClear);
			
			btnDel = new WebButton("Del");
			btnDel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/**
					String username=TextField_Username.getText().trim();
					String sql_deluser = "update INACCOUNT set status = '3'where username='"+username+"'";
					try {
					
						Connection conn_user_del = new DBmanager().getConnMySql();
						PreparedStatement stmt_user_del = conn_user_del.prepareStatement(sql_deluser);
						int rs_update_user =  stmt_user_del.executeUpdate();
						stmt_user_del.close();
						conn_user_del.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					*/
					TextField_ID.setEditable(false);
					TextField_Code.setEditable(false);
					user_control="0";
					ClearTextField();
				}
			});
			btnDel.setBounds(20, 444, 60, 23);
			btnDel.setEnabled(false);
			midPanel.add(btnDel);
			
			btnRefresh = new WebButton("Refresh");
			btnRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					user_control="0";
					TextField_ID.setEditable(false);
					TextField_Code.setEditable(false);
					getData();
					ClearTextField();
				}
			});
			btnRefresh.setBounds(136, 409, 91, 23);
			midPanel.add(btnRefresh);
			
			ReportPanel = new WebPanel();
			ReportPanel.setBorder(new TitledBorder(null, "Report", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			ReportPanel.setBounds(357, 228, 461, 200);
			midPanel.add(ReportPanel);
			ReportPanel.setLayout(null);
			
			fromReport = new DefaultListModel();
			moveReport = new DefaultListModel();
			
		
			spReportFrom = new JScrollPane();
			spReportFrom.setPreferredSize(new Dimension(220, 200));
			spReportFrom.setBounds(6, 16, 220, 144);
			ReportPanel.add(spReportFrom);
			
			/*
			dragReportFrom = new JList(fromReport);
			dragReportFrom.setTransferHandler(new FromReportTransferHandler());
			dragReportFrom.setPrototypeCellValue("List Item WWWWWW");
			dragReportFrom.setDragEnabled(true);
			dragReportFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			spReportFrom.setViewportView(dragReportFrom);
			*/
			dragReportFrom = new JList(fromReport);
			dragReportFrom.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			dragReportFrom.setTransferHandler(arrayListHandler);
			dragReportFrom.setDragEnabled(true);
			spReportFrom.setViewportView(dragReportFrom);
			
			spReportTo = new JScrollPane();
			spReportTo.setBounds(235, 16, 220, 144);
			spReportTo.setPreferredSize(new Dimension(220, 200));
			ReportPanel.add(spReportTo);
			
			/*
			moveReportTo = new WebList(moveReport);
			moveReportTo.setTransferHandler(new ToReportTransferHandler(TransferHandler.MOVE));
		    moveReportTo.setDropMode(DropMode.INSERT);
		    //moveReportTo.setDragEnabled(true);
			spReportTo.setViewportView(moveReportTo);
			*/
			moveReportTo = new JList(moveReport);
			moveReportTo.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			moveReportTo.setTransferHandler(arrayListHandler);
			moveReportTo.setDragEnabled(true);
			spReportTo.setViewportView(moveReportTo);
			
			ButtonReportPanel = new WebPanel();
			ButtonReportPanel.setBounds(6, 160, 449, 33);
			ReportPanel.add(ButtonReportPanel);
			
			btnDelete_1 = new WebButton("Delete");
			btnDelete_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					moveReport = (DefaultListModel) moveReportTo.getModel();
					int selectedIndex = moveReportTo.getSelectedIndex();
					if (selectedIndex != -1) {
					    moveReport.remove(selectedIndex);
					}
				}
			});
			ButtonReportPanel.add(btnDelete_1);
			/////
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveUser();
				}
			});
	 }
	 public void getData(){
			table.setModel(fetchData());
			TableColumnModel columnModel = table.getColumnModel();
			columnModel.getColumn(0).setPreferredWidth(40);
			columnModel.getColumn(1).setPreferredWidth(0);
			columnModel.getColumn(1).setMinWidth(0);
			columnModel.getColumn(1).setMaxWidth(0);
			columnModel.getColumn(2).setPreferredWidth(80 );
			columnModel.getColumn(3).setPreferredWidth(0);
			columnModel.getColumn(3).setMinWidth(0);
			columnModel.getColumn(3).setMaxWidth(0);
			columnModel.getColumn(4).setPreferredWidth((width*2)/10-120);
			
			for (int n = 4; n < 6; n++) {	
				columnModel.getColumn(n+1).setPreferredWidth(0);
				columnModel.getColumn(n+1).setMinWidth(0);
				columnModel.getColumn(n+1).setMaxWidth(0);
			}
			((DefaultTableModel)table.getModel()).fireTableDataChanged();
		}
		public  DefaultTableModel fetchData(){
			Connection conn;
			PreparedStatement stmt,stmt1;
			ResultSet rs,rs1;
			String query = "select id,ward_code,department_id,ward_name,remark,status from ward_ipd "+userStatus+" order by id" ;
			
			
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
		            vstring.add("  "+rs.getString(4));
		            vstring.add("  "+rs.getString(5));
		            vstring.add("  "+rs.getString(6));
		            
		             
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
			TextField_Code.setText("");
			TextField_DoctorName.setText("");
			bgStatus.clearSelection();
			move.clear();
			from.clear();
			moveReport.clear();
			fromReport.clear();
			user_status="1";
			wardcode="";
		}
		public void setInit(){
			int p=0,p1=0;
			Connection conn3;
			PreparedStatement stmt32;
			ResultSet rs33,rs34;
			//String query31="select code,thainame from sysconfig where ctrlcode=?";
			String query31="select distinct doctor_id,doctor_name from doctor where status='1'";
			try {
			
				conn3 = new DBmanager().getConnMySql();
				 
				//ward
				stmt32 = conn3.prepareStatement(query31);
				//stmt32.setString(1, "20024"); 
				rs33 = stmt32.executeQuery();
				while (rs33.next()) {
					p1++;
				}
				rs33.close();
				ward_indb = new String [p1][2];
				rs34 = stmt32.executeQuery();
				int pp1=0;
				while (rs34.next()) {
					 
					if(rs34.getString(1)==null || rs34.getString(1).equals("")){
						ward_indb[pp1][0]="";
					}else{
						ward_indb[pp1][0]=rs34.getString(1);
					}
					if(rs34.getString(2)==null || rs34.getString(2).equals("")){
						ward_indb[pp1][1]="";
					}else{
						//ward_indb[pp1][1]=rs34.getString(2).substring(1).trim();
						ward_indb[pp1][1]=rs34.getString(2).trim();
					}			 
					pp1++;
				}
				rs34.close();
				stmt32.close();
				
				conn3.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		public void setInitReport(){
			int p=0,p1=0;
			Connection conn3;
			PreparedStatement stmt32;
			ResultSet rs33,rs34;
			//String query31="select code,thainame from sysconfig where ctrlcode=?";
			String query31="select distinct form_code,form_name from formipd_master where status='1' order by form_code desc";
			try {
			
				conn3 = new DBmanager().getConnMySql();
				 
				//ward
				stmt32 = conn3.prepareStatement(query31);
				//stmt32.setString(1, "20024"); 
				rs33 = stmt32.executeQuery();
				while (rs33.next()) {
					p1++;
				}
				rs33.close();
				report_indb = new String [p1][2];
				rs34 = stmt32.executeQuery();
				int pp1=0;
				while (rs34.next()) {
					 
					if(rs34.getString(1)==null || rs34.getString(1).equals("")){
						report_indb[pp1][0]="";
					}else{
						report_indb[pp1][0]=rs34.getString(1);
					}
					if(rs34.getString(2)==null || rs34.getString(2).equals("")){
						report_indb[pp1][1]="";
					}else{
						//ward_indb[pp1][1]=rs34.getString(2).substring(1).trim();
						report_indb[pp1][1]=rs34.getString(2).trim();
					}			 
					pp1++;
				}
				rs34.close();
				stmt32.close();
				
				conn3.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		class FromTransferHandler extends TransferHandler {
			public int getSourceActions(JComponent comp) {
	           return COPY_OR_MOVE;
	       }

	       private int index = 0;

	       public Transferable createTransferable(JComponent comp) {
	           index = dragFrom.getSelectedIndex();
	           if (index < 0 || index >= from.getSize()) {
	               return null;
	           }

	           return new StringSelection((String)dragFrom.getSelectedValue());
	       }
	       
	       public void exportDone(JComponent comp, Transferable trans, int action) {
	           if (action != MOVE) {
	               return;
	           }

	           from.removeElementAt(index);
	       }
		}
		class FromReportTransferHandler extends TransferHandler {
			public int getSourceActions(JComponent comp) {
	           return COPY_OR_MOVE;
	       }

	       private int index = 0;

	       public Transferable createTransferable(JComponent comp) {
	           index = dragReportFrom.getSelectedIndex();
	           if (index < 0 || index >= fromReport.getSize()) {
	               return null;
	           }

	           return new StringSelection((String)dragReportFrom.getSelectedValue());
	       }
	       
	       public void exportDone(JComponent comp, Transferable trans, int action) {
	           if (action != MOVE) {
	               return;
	           }

	           fromReport.removeElementAt(index);
	       }
		}
		class ToTransferHandler extends TransferHandler {
			int action;
	       
	       public ToTransferHandler(int action) {
	           this.action = action;
	       }
	       
	       public boolean canImport(TransferHandler.TransferSupport support) {
	           // for the demo, we'll only support drops (not clipboard paste)
	           if (!support.isDrop()) {
	               return false;
	           }

	           // we only import Strings
	           if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	               return false;
	           }

	           boolean actionSupported = (action & support.getSourceDropActions()) == action;
	           if (actionSupported) {
	               support.setDropAction(action);
	               return true;
	           }

	           return false;
	       }
	       public boolean importData(TransferHandler.TransferSupport support) {
	           // if we can't handle the import, say so
	           if (!canImport(support)) {
	               return false;
	           }

	           // fetch the drop location
	           JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();

	           int index = dl.getIndex();

	           // fetch the data and bail if this fails
	           String data;
	           try {
	               data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
	           } catch (UnsupportedFlavorException e) {
	               return false;
	           } catch (java.io.IOException e) {
	               return false;
	           }

	           JList list = (JList)support.getComponent();
	           DefaultListModel model = (DefaultListModel)list.getModel();
	           model.insertElementAt(data, index);

	           Rectangle rect = list.getCellBounds(index, index);
	           list.scrollRectToVisible(rect);
	           list.setSelectedIndex(index);
	           list.requestFocusInWindow();

	           return true;
	       } 
	       
		}
		 class ToReportTransferHandler extends TransferHandler {
		   		int action;
		          
		          public ToReportTransferHandler(int action) {
		              this.action = action;
		          }
		          
		          public boolean canImport(TransferHandler.TransferSupport support) {
		              // for the demo, we'll only support drops (not clipboard paste)
		              if (!support.isDrop()) {
		                  return false;
		              }

		              // we only import Strings
		              if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
		                  return false;
		              }

		              boolean actionSupported = (action & support.getSourceDropActions()) == action;
		              if (actionSupported) {
		                  support.setDropAction(action);
		                  return true;
		              }

		              return false;
		          }
		       public boolean importData(TransferHandler.TransferSupport support) {
		           // if we can't handle the import, say so
		           if (!canImport(support)) {
		               return false;
		           }

		           // fetch the drop location
		           JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();

		           int index = dl.getIndex();

		           // fetch the data and bail if this fails
		           String data;
		           try {
		               data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
		           } catch (UnsupportedFlavorException e) {
		               return false;
		           } catch (java.io.IOException e) {
		               return false;
		           }

		           JList list = (JList)support.getComponent();
		           DefaultListModel model = (DefaultListModel)list.getModel();
		           model.insertElementAt(data, index);

		           Rectangle rect = list.getCellBounds(index, index);
		           list.scrollRectToVisible(rect);
		           list.setSelectedIndex(index);
		           list.requestFocusInWindow();

		           return true;
		       } 
		 }

		public void saveUser(){
			int check_username=0;
			String appcode="",report="";
			ListModel model = moveTo.getModel();
			for(int i=0;i<model.getSize();i++){
				//System.out.println(model.getElementAt(i)); 
				for(int j=0;j<ward_indb.length;j++){
					if(model.getElementAt(i).toString().trim().equals(ward_indb[j][1].trim())){
						appcode+=ward_indb[j][0]+","; 
					}
				}	  
			}
			ListModel modelReport = moveReportTo.getModel();
			for(int i=0;i<modelReport.getSize();i++){
				//System.out.println(model.getElementAt(i)); 
				for(int j=0;j<report_indb.length;j++){
					if(modelReport.getElementAt(i).toString().trim().equals(report_indb[j][1].trim())){
						report+=report_indb[j][0]+","; 
					}
				}	  
			}
			
			Connection conn1;
			PreparedStatement  stmt3,stmt_user_check,stmt4;
			ResultSet rs_user_check;
			try {
				
				conn1 =new DBmanager().getConnMySql();
				if(user_control.equals("1")){
					/*
					if(!Setup.checkEmptyTextField(TextField_Username)){
						String username=TextField_Username.getText().trim();
						String query_user="select username  from inaccount where username=?";
						stmt_user_check = conn1.prepareStatement(query_user);
						stmt_user_check.setString(1,username);
						rs_user_check = stmt_user_check.executeQuery();
						while (rs_user_check.next()) {
							if(rs_user_check.getString(1) !=null){
								JOptionPane.showMessageDialog(this,"Username duplicate !!","Username Error",JOptionPane.ERROR_MESSAGE);
								check_username=1;
							}
						}
						rs_user_check.close();
						stmt_user_check.close();
						if(check_username==0){
							String sql_insertuser = " insert into inaccount (username,password,name,position,type,certifyno,clinic_code,report_code,app_code,status) "
									+ "values (?,?,?"
											+ ",?,?,?,?"
													+ ",?,'"+appcode+"','1') ";
							PreparedStatement stmt_insert_user = conn1.prepareStatement(sql_insertuser);
							String password_new=TextField_Password.getText().trim();
							String name_new=TextField_Name.getText().trim();
							String position_new=TextField_Position.getText().trim();
							String type_new=TextField_Type.getText().trim();
							String certifyno_new=TextField_Certifyno.getText().trim();
							String clinic_code_new=TextField_Clinic_code.getText().trim();
							String report_code_new=TextField_Report_code.getText().trim();
							
							stmt_insert_user.setString(1, username);
							stmt_insert_user.setString(2, password_new);
							stmt_insert_user.setString(3, name_new);
							stmt_insert_user.setString(4, position_new);
							stmt_insert_user.setString(5, type_new);
							stmt_insert_user.setString(6, certifyno_new);
							stmt_insert_user.setString(7, clinic_code_new);
							stmt_insert_user.setString(8, report_code_new);

							int rs_insert_user =  stmt_insert_user.executeUpdate( );
							//System.out.println("save update user.."+rs_update_user);
							stmt_insert_user.close();
						}else{
							check_username=0;
						}
						
					}else{
						JOptionPane.showMessageDialog(this,"Please check UserName ","Username Error",JOptionPane.ERROR_MESSAGE);
					}
					user_control="0";
			        TextField_Username.setEditable(false);
			        */
				}
				else if(user_control.equals("0")){
					/*
					String password_new=TextField_Password.getText().trim();
					String name_new=TextField_Name.getText().trim();
					String position_new=TextField_Position.getText().trim();
					String type_new=TextField_Type.getText().trim();
					String certifyno_new=TextField_Certifyno.getText().trim();
					String clinic_code_new=TextField_Clinic_code.getText().trim();
					String report_code_new=TextField_Report_code.getText().trim();
				*/
					String sql_update="update ward_ipd set doctor=?, report=?  where ward_code=? ";			 
					stmt3 = conn1.prepareStatement(sql_update);
					//stmt3.setString(1, password_new);
					//stmt3.setString(2, name_new);
					//stmt3.setString(3, position_new);
					//stmt3.setString(4, type_new);
					//stmt3.setString(5, certifyno_new);
					//stmt3.setString(6, clinic_code_new);
					//stmt3.setString(7, report_code_new);
					stmt3.setString(1, appcode);
					stmt3.setString(2, report);
					stmt3.setString(3, wardcode);	
					stmt3.executeUpdate();
					stmt3.close();

					
				}	 
				conn1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ClearTextField();
			getData();
		 
		}
		public void setAppCodeIPD(String username){
			move.clear();
			from.clear();
			int p=0,p1=0;		 
			String un=username;
			Connection conn3;
			PreparedStatement stmt31,stmt32;
			ResultSet rs31,rs32,rs33,rs34;
			String query31="select doctor from ward_ipd where ward_code=?";
			try {
				
				conn3 = new DBmanager().getConnMySql();
				stmt31 = conn3.prepareStatement(query31);
				stmt31.setString(1,un); 
				rs31 = stmt31.executeQuery();
				String[] parts=null;
				String doctor="";
				while (rs31.next()) {
					 
					if(rs31.getString(1)==null || rs31.getString(1).trim().equals("")){
						doctor=",";
					}else{
						doctor+=rs31.getString(1).trim()+",";
						 
					}
					 
					parts = doctor.split("\\,");
					 
				}
				rs31.close();
				ward_usernew = new String [parts.length][2];
				
				for(int i=0;i<ward_indb.length;i++){
					for(int j=0;j<parts.length;j++){
						if(ward_indb[i][0].equals(parts[j])){
							ward_usernew[j][0]=parts[j];
							ward_usernew[j][1]= ward_indb[i][1];
						}
					}				 
				}
				ward_userold = ward_indb;
				Collection totalList = new ArrayList() {{
					for(int j=0;j<ward_userold.length;j++){
						add(ward_userold[j][0]);
					}
				}};
				Collection newList = new ArrayList() {{
					for(int j=0;j<ward_usernew.length;j++){
						add(ward_usernew[j][0]);
					}
				}};
				
				totalList.removeAll(newList);
				
				
				Object[] totalA= new String [totalList.size()];
				totalA=totalList.toArray();
				ward_userdiff = new String [totalA.length][2];
				
				for(int i=0;i<ward_indb.length;i++){
					for(int j=0;j<totalA.length;j++){
						if(ward_indb[i][0].equals(totalA[j])){
							ward_userdiff[j][0]=totalA[j].toString().trim();
							ward_userdiff[j][1]= ward_indb[i][1];
						}
					}				 
				}
				
				stmt31.close();			
				conn3.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<ward_userdiff.length;i++){
				from.add(0,ward_userdiff[i][1]);
			}
			for(int i=0;i<ward_usernew.length;i++){
				move.add(0,ward_usernew[i][1]);
			}
		}
		public void setReportCodeIPD(String username){

			moveReport.clear();
			fromReport.clear();
			int p=0,p1=0;		 
			String un=username;
			Connection conn3;
			PreparedStatement stmt31,stmt32;
			ResultSet rs31,rs32,rs33,rs34;
			String query31="select report from ward_ipd where ward_code=?";
			try {
				
				conn3 = new DBmanager().getConnMySql();
				stmt31 = conn3.prepareStatement(query31);
				stmt31.setString(1,un); 
				rs31 = stmt31.executeQuery();
				String[] parts=null;
				String formcode="";
				while (rs31.next()) {
					 
					if(rs31.getString(1)==null || rs31.getString(1).trim().equals("")){
						formcode=",";
					}else{
						formcode+=rs31.getString(1).trim()+",";
						 
					}
					 
					parts = formcode.split("\\,");
					 
				}
				rs31.close();
				report_usernew = new String [parts.length][2];
				
				for(int i=0;i<report_indb.length;i++){
					for(int j=0;j<parts.length;j++){
						if(report_indb[i][0].equals(parts[j])){
							report_usernew[j][0]=parts[j];
							report_usernew[j][1]= report_indb[i][1];
						}
					}				 
				}
				report_userold = report_indb;
				Collection totalList = new ArrayList() {{
					for(int j=0;j<report_userold.length;j++){
						add(report_userold[j][0]);
					}
				}};
				Collection newList = new ArrayList() {{
					for(int j=0;j<report_usernew.length;j++){
						add(report_usernew[j][0]);
					}
				}};
				
				totalList.removeAll(newList);
				
				
				Object[] totalA= new String [totalList.size()];
				totalA=totalList.toArray();
				report_userdiff = new String [totalA.length][2];
				
				for(int i=0;i<report_indb.length;i++){
					for(int j=0;j<totalA.length;j++){
						if(report_indb[i][0].equals(totalA[j])){
							report_userdiff[j][0]=totalA[j].toString().trim();
							report_userdiff[j][1]= report_indb[i][1];
						}
					}				 
				}
				
				stmt31.close();			
				conn3.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<report_userdiff.length;i++){
				fromReport.add(0,report_userdiff[i][1]);
				//fromReport.addElement("0 (list 1)");
			}
			for(int i=0;i<report_usernew.length;i++){
				moveReport.add(0,report_usernew[i][1]);
			}
		}


}
