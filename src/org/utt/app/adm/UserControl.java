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

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

import org.utt.app.InApp;
import org.utt.app.adm.WardControl.FromReportTransferHandler;
import org.utt.app.adm.WardControl.FromTransferHandler;
import org.utt.app.adm.WardControl.ToReportTransferHandler;
import org.utt.app.adm.WardControl.ToTransferHandler;
import org.utt.app.dao.DBmanager;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
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

public class UserControl extends WebPanel {
    int width,height;

    String userStatus="where status='1' ",user_status="1";
    Vector<String> columnNames;
    DefaultListModel from,move,fromReport,moveReport;

    WebRadioButton userStatus1,userStatus2,ButtonStatus1,ButtonStatus2;
    ButtonGroup bg = new ButtonGroup();
    ButtonGroup bgStatus = new ButtonGroup();
    WebButton ButtonRefresh,btnDelete,btnSave,btnDelete_1,btnSave1;
    WebScrollPane scrollPane;
    WebTable table;
    WebPanel midPanel,DiagRoomPanel,ButtomPanel,ReportPanel,ButtonReportPanel;
    WebLabel Label_Username,Label_Password,Label_Name,Label_Position,Label_Type,Label_Clinic_code,Label_Report_code,Label_Certifyno,Label_Status;
    WebTextField TextField_ID,TextField_Username,TextField_Password,TextField_Name,TextField_Position,TextField_Type,TextField_Clinic_code,TextField_Report_code,TextField_Certifyno;
    JScrollPane spFrom,spTo,spReportFrom,spReportTo;
    JList dragFrom,moveTo,dragReportFrom,moveReportTo;
    Vector<Vector<String>> data;
    String user_control;
    
    String [][] clinic_indb=null ;
    String [][] clinic_userold=null ;
	String [][] clinic_usernew=null ;
	String [][] clinic_userdiff=null ;
	
	String [][] report_indb=null ;
	String [][] report_userold=null ;
	String [][] report_usernew=null ;
	String [][] report_userdiff=null ;

    public UserControl(int w,int h) {
        width=w;
        height=h;
        user_control="0";
        WebPanel leftPanel = new WebPanel();
        leftPanel.setBackground(new Color(206,203,208));
        add(leftPanel, BorderLayout.WEST);
        leftPanel.setPreferredSize(new Dimension(400, height));
        leftPanel.setLayout(new BorderLayout(0, 0));

        WebPanel topPanel = new WebPanel();
        leftPanel.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(null);
        topPanel.setPreferredSize(new Dimension(400, 30));

        WebLabel Label_Info = new WebLabel(" ข้อมูลผู้ใช้งาน");
        Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 12));
        Label_Info.setBounds(30,5,400,20);
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
        columnNames.add(" UserName ");
        columnNames.add(" ");
        columnNames.add(" ชื่อ ");
        for (int n = 5; n < 12; n++) {
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
                    String status=(String)getModel().getValueAt(convertRowIndexToModel(row), 6);
                    if ("1".equals(status)){
                        c.setBackground(new Color(206,203,208));
                    }
                }
                return c;
            }
        };
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                ClearTextField();
                TextField_ID.setEditable(false);
                TextField_ID.setText(table.getValueAt(row,1).toString().trim());
                TextField_Username.setEditable(false);
                TextField_Username.setText(table.getValueAt(row,2).toString().trim());
                TextField_Password.setText(table.getValueAt(row,3).toString().trim());
                TextField_Name.setText(table.getValueAt(row,4).toString().trim());
                TextField_Position.setText(table.getValueAt(row,5).toString().trim());
                TextField_Type.setText(table.getValueAt(row,6).toString().trim());
                TextField_Clinic_code.setText(table.getValueAt(row,7).toString().trim());
                TextField_Report_code.setText(table.getValueAt(row,9).toString().trim());
                TextField_Certifyno.setText(table.getValueAt(row,10).toString().trim());
                if(table.getValueAt(row,11).toString().trim().equals("1")){
                    ButtonStatus1.setSelected(true);
                }else{
                    ButtonStatus2.setSelected(true);
                }
                if(TextField_Type.getText().trim().equals("8") || TextField_Type.getText().trim().equals("83") ||TextField_Type.getText().trim().equals("10") || TextField_Type.getText().trim().equals("102")) {
                	
                	 
                	
                	DiagRoomPanel = new WebPanel();
            		DiagRoomPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "clinic List", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
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
            		
            		setAppCodeOPD(table.getValueAt(row,2).toString().trim());
            		 
            		
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
        			//
        			
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
        			
        			dragReportFrom = new JList(fromReport);
        			dragReportFrom.setTransferHandler(new FromReportTransferHandler());
        			dragReportFrom.setPrototypeCellValue("List Item WWWWWW");
        			dragReportFrom.setDragEnabled(true);
        			dragReportFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        			spReportFrom.setViewportView(dragReportFrom);
        			
        			spReportTo = new JScrollPane();
        			spReportTo.setBounds(235, 16, 220, 144);
        			spReportTo.setPreferredSize(new Dimension(220, 200));
        			ReportPanel.add(spReportTo);
        			
        			moveReportTo = new WebList(moveReport);
        			moveReportTo.setTransferHandler(new ToReportTransferHandler(TransferHandler.MOVE));
        		    moveReportTo.setDropMode(DropMode.INSERT);
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
        			
        			btnSave = new WebButton("Save");
        			btnSave.setBounds(110, 375, 150, 23);
        			midPanel.add(btnSave);
        			
        			setReportCodeOPD(table.getValueAt(row,2).toString().trim());
        			
        			btnSave.addActionListener(new ActionListener() {
        				public void actionPerformed(ActionEvent e) {
        					saveUser(table.getValueAt(row,2).toString().trim());
        				}
        			});
            		
                }
                
                /*
                username=table.getValueAt(row,2).toString().trim();
                op=table.getValueAt(row,6).toString().trim();
                if(op.equals("6")){
                    setAppCodeIPD(table.getValueAt(row,2).toString().trim());
                }else{
                    setAppCode(table.getValueAt(row,2).toString().trim());
                }
                */


            }
        });
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        scrollPane = new WebScrollPane(table);
        getData();
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        WebPanel bottomPanel = new WebPanel();
        bottomPanel.setPreferredSize(new Dimension(300, 30));
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(null);

        ButtonRefresh = new WebButton("Refresh");
        ButtonRefresh.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                getData();

            }
        });
        ButtonRefresh.setBounds(((400)/2-45), 2, 90, 25);
        bottomPanel.add(ButtonRefresh);

        //
        midPanel = new WebPanel();
        midPanel.setPreferredSize(new Dimension(width-400, height));
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

        Label_Username = new WebLabel("Username");
        Label_Username.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Username.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Username.setBounds(20, 75, 80, 20);
        midPanel.add(Label_Username);

        Label_Password = new WebLabel("Password");
        Label_Password.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Password.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Password.setBounds(20, 105, 80, 20);
        midPanel.add(Label_Password);

        Label_Name = new WebLabel("Name");
        Label_Name.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Name.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Name.setBounds(20, 135, 80, 20);
        midPanel.add(Label_Name);

        Label_Position = new WebLabel("Position");
        Label_Position.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Position.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Position.setBounds(20, 165, 80, 20);
        midPanel.add(Label_Position);

        Label_Type = new WebLabel("Type");
        Label_Type.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Type.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Type.setBounds(20, 195, 80, 20);
        midPanel.add(Label_Type);

        Label_Clinic_code = new WebLabel("Clinic_code");
        Label_Clinic_code.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Clinic_code.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Clinic_code.setBounds(20, 225, 80, 20);
        midPanel.add(Label_Clinic_code);

        Label_Report_code = new WebLabel("Report_code");
        Label_Report_code.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Report_code.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Report_code.setBounds(20, 255, 80, 20);
        midPanel.add(Label_Report_code);

        Label_Certifyno = new WebLabel("Cert No.");
        Label_Certifyno.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Certifyno.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Certifyno.setBounds(20, 285, 80, 20);
        midPanel.add(Label_Certifyno);

        Label_Status = new WebLabel("Status");
        Label_Status.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Status.setHorizontalAlignment(SwingConstants.LEFT);
        Label_Status.setBounds(20, 345, 80, 20);
        midPanel.add(Label_Status);

        TextField_ID = new WebTextField();
        TextField_ID.setEditable(false);
        TextField_ID.setBounds(110, 45, 100, 20);
        midPanel.add(TextField_ID);

        TextField_Username = new WebTextField();
        TextField_Username.setEditable(false);
        TextField_Username.setBounds(110, 75, 100, 20);
        midPanel.add(TextField_Username);

        TextField_Password = new WebTextField();
        TextField_Password.setBounds(110, 105, 100, 20);
        midPanel.add(TextField_Password);

        TextField_Name = new WebTextField();
        TextField_Name.setBounds(110, 135, 200, 25);
        midPanel.add(TextField_Name);

        TextField_Position = new WebTextField();
        TextField_Position.setBounds(110, 165, 150, 25);
        midPanel.add(TextField_Position);

        TextField_Type = new WebTextField();
        TextField_Type.setBounds(110, 195, 150, 20);
        midPanel.add(TextField_Type);

        TextField_Clinic_code = new WebTextField();
        TextField_Clinic_code.setBounds(110, 225, 150, 20);
        midPanel.add(TextField_Clinic_code);

        TextField_Report_code = new WebTextField();
        TextField_Report_code.setBounds(110, 255, 150, 20);
        midPanel.add(TextField_Report_code);

        TextField_Certifyno = new WebTextField();
        TextField_Certifyno.setBounds(110, 285, 150, 20);
        midPanel.add(TextField_Certifyno);

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
        
        btnSave1 = new WebButton("Save user");
		btnSave1.setBounds(110, 405, 150, 60);
		midPanel.add(btnSave1);
		
		btnSave1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveUser1(TextField_Username.getText().trim());
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
        columnModel.getColumn(2).setPreferredWidth(100 );
        columnModel.getColumn(3).setPreferredWidth(0);
        columnModel.getColumn(3).setMinWidth(0);
        columnModel.getColumn(3).setMaxWidth(0);
        columnModel.getColumn(4).setPreferredWidth(400-140);

        for (int n = 4; n < 11; n++) {
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
        String query = "select id,username,password,name,position,type,clinic_code,app_code,report_code,certifyno,status from inaccount "+userStatus+" order by id" ;

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
                vstring.add("  "+rs.getString(7));
                vstring.add("  "+rs.getString(8));
                vstring.add("  "+rs.getString(9));
                vstring.add("  "+rs.getString(10));
                vstring.add("  "+rs.getString(11));

                data.add(vstring);
                p++;
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(data, columnNames);
    }
    public void ClearTextField(){
        TextField_ID.setText("");
        TextField_Username.setText("");
        TextField_Password.setText("");
        TextField_Name.setText("");
        TextField_Certifyno.setText("");
        TextField_Position.setText("");
        TextField_Type.setText("");
        TextField_Clinic_code.setText("");
        TextField_Report_code.setText("");
        bgStatus.clearSelection();
        //move.clear();
        //from.clear();
        //moveReport.clear();
		//fromReport.clear();
        user_status="1";
        if(DiagRoomPanel !=null) {
        	midPanel.remove(btnSave);
        	midPanel.remove(DiagRoomPanel);
            midPanel.revalidate();
            midPanel.repaint();
        }
        if(ReportPanel !=null) {
        	midPanel.remove(btnSave);
        	midPanel.remove(ReportPanel);
            midPanel.revalidate();
            midPanel.repaint();
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
    public void setInit(){
    	int p=0,p1=0;
		Connection conn3;
		PreparedStatement stmt32;
		ResultSet rs33,rs34;
		//String query31="select code,thainame from sysconfig where ctrlcode=?";
		String query31="select distinct app_code,clinic_name from clinic_name ";
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
			clinic_indb = new String [p1][2];
			rs34 = stmt32.executeQuery();
			int pp1=0;
			while (rs34.next()) {
				 
				if(rs34.getString(1)==null || rs34.getString(1).equals("")){
					clinic_indb[pp1][0]="";
				}else{
					clinic_indb[pp1][0]=rs34.getString(1);
				}
				if(rs34.getString(2)==null || rs34.getString(2).equals("")){
					clinic_indb[pp1][1]="";
				}else{
					//ward_indb[pp1][1]=rs34.getString(2).substring(1).trim();
					clinic_indb[pp1][1]=rs34.getString(2).trim();
				}			 
				pp1++;
			}
			rs34.close();
			stmt32.close();
			
			conn3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
 	   
    }
    public void setInitReport(){
		int p=0,p1=0;
		Connection conn3;
		PreparedStatement stmt32;
		ResultSet rs33,rs34;
		//String query31="select code,thainame from sysconfig where ctrlcode=?";
		String query31="select distinct form_code,form_name from formopd where type='1' order by form_code desc";
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
			e.printStackTrace();
		}
		
	}
    public void setAppCodeOPD(String username){
		move.clear();
		from.clear();
		int p=0,p1=0;		 
		String un=username;
		Connection conn3;
		PreparedStatement stmt31,stmt32;
		ResultSet rs31,rs32,rs33,rs34;
		String query31="select app_code from inaccount where username=?";
		try {
			
			conn3 = new DBmanager().getConnMySql();
			stmt31 = conn3.prepareStatement(query31);
			stmt31.setString(1,un); 
			rs31 = stmt31.executeQuery();
			String[] parts=null;
			String clinic="";
			while (rs31.next()) {
				 
				if(rs31.getString(1)==null || rs31.getString(1).trim().equals("")){
					clinic=",";
				}else{
					clinic+=rs31.getString(1).trim()+",";
					 
				}
				 
				parts = clinic.split("\\,");
				 
			}
			rs31.close();
			clinic_usernew = new String [parts.length][2];
			
			for(int i=0;i<clinic_indb.length;i++){
				for(int j=0;j<parts.length;j++){
					if(clinic_indb[i][0].equals(parts[j])){
						clinic_usernew[j][0]=parts[j];
						clinic_usernew[j][1]= clinic_indb[i][1];
					}
				}				 
			}
			clinic_userold = clinic_indb;
			Collection totalList = new ArrayList() {{
				for(int j=0;j<clinic_userold.length;j++){
					add(clinic_userold[j][0]);
				}
			}};
			Collection newList = new ArrayList() {{
				for(int j=0;j<clinic_usernew.length;j++){
					add(clinic_usernew[j][0]);
				}
			}};
			
			totalList.removeAll(newList);
			
			
			Object[] totalA= new String [totalList.size()];
			totalA=totalList.toArray();
			clinic_userdiff = new String [totalA.length][2];
			
			for(int i=0;i<clinic_indb.length;i++){
				for(int j=0;j<totalA.length;j++){
					if(clinic_indb[i][0].equals(totalA[j])){
						clinic_userdiff[j][0]=totalA[j].toString().trim();
						clinic_userdiff[j][1]= clinic_indb[i][1];
					}
				}				 
			}
			
			stmt31.close();			
			conn3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i=0;i<clinic_userdiff.length;i++){
			from.add(0,clinic_userdiff[i][1]);
		}
		for(int i=0;i<clinic_usernew.length;i++){
			move.add(0,clinic_usernew[i][1]);
		}
	}
    public void setReportCodeOPD(String username){
		moveReport.clear();
		fromReport.clear();
		int p=0,p1=0;		 
		String un=username;
		Connection conn3;
		PreparedStatement stmt31,stmt32;
		ResultSet rs31,rs32,rs33,rs34;
		String query31="select report_code from inaccount where username=?";
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
		}
		for(int i=0;i<report_usernew.length;i++){
			moveReport.add(0,report_usernew[i][1]);
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
    
    public void saveUser(String username){
    	int check_username=0;
		String appcode="",report="";
		ListModel model = moveTo.getModel();
		for(int i=0;i<model.getSize();i++){
			System.out.println(model.getElementAt(i)); 
			
			for(int j=0;j<clinic_indb.length;j++){
				if(model.getElementAt(i) !=null) {
					if(model.getElementAt(i).toString().trim().equals(clinic_indb[j][1].trim())){
						appcode+=clinic_indb[j][0]+","; 
					}
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
		
			}
			else if(user_control.equals("0")){

				String sql_update="update inaccount set app_code=?, report_code=?   where username=? ";			 
				stmt3 = conn1.prepareStatement(sql_update);
	
				stmt3.setString(1, appcode);
				stmt3.setString(2, report);
				stmt3.setString(3, username);	
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
    public void saveUser1(String username){
    	Connection conn1;
    	PreparedStatement  stmt3;
    	String sql_update="update inaccount set certifyno=?,password=?,name=?  where username=? ";			 
		try {
			conn1 =new DBmanager().getConnMySql();
			stmt3 = conn1.prepareStatement(sql_update);
			stmt3.setString(1, TextField_Certifyno.getText().trim());
			stmt3.setString(2, TextField_Password.getText().trim());
			stmt3.setString(3, TextField_Name.getText().trim());
			 
			stmt3.setString(4, username);	
			stmt3.executeUpdate();
			stmt3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ClearTextField();
		getData();
		 
    }
    
}
