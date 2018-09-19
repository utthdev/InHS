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
package org.utt.app.fin;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;

public class EDCReport  extends WebPanel implements Observer{
	ObjectData oUserInfo;
	int width,height;
    WebPanel Section,top;
    WebTable table;
    WebScrollPane scrollPane;
    Vector<String> columnNames;
	
	 public EDCReport (ObjectData oUserInfo, int w, int h) {
		 this.oUserInfo=oUserInfo;
		 width=w;
	        height=h;
	        setLayout(new BorderLayout(0, 0));
	        setPreferredSize(new Dimension(width, height));
	        setBounds(0, 0, width, height);
	        
	        Section = new WebPanel();
	        Section.setPreferredSize(new Dimension(width-(width*2)/10-300, height-190));
			add(Section, BorderLayout.CENTER);
			Section.setLayout(new BorderLayout(0, 0));
			top = new WebPanel();
	        top.setPreferredSize(new Dimension(width-(width*2)/10-300, 30));
			add(top, BorderLayout.NORTH);
			top.setLayout(null);
			WebButton ButtonPrint = new WebButton("	Show");
			//ButtonPrint.setEnabled(false);
	        ButtonPrint.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent arg0) {
	            	//price="000000000000";
	            	getData(oUserInfo.GetPtVisitdate());
            	
	            }
	        });
	        ButtonPrint.setBounds(10, 1, 100, 25);
	        top.add(ButtonPrint);
	        WebButton ButtonExport = new WebButton("Export");
			ButtonExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getDataToExport(oUserInfo.GetPtVisitdate());
				}
			});
			ButtonExport.setBounds(130, 1, 89, 25);
			top.add(ButtonExport);
			
			columnNames = new Vector<String>();		
			
			columnNames.add("   ที่");
			columnNames.add("   วันที่");
			columnNames.add("   VN");
			columnNames.add("   HN");
			columnNames.add("   CID");
			columnNames.add("   APPCODE");
			Vector<Vector<String>> data = new Vector<Vector<String>>();
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
				public void mouseClicked(MouseEvent e) {
					int row_table = table.rowAtPoint(e.getPoint());
			        int col_table = table.columnAtPoint(e.getPoint());		         
			        System.out.println(row_table+"-"+col_table);
				}
			});
			table.setFont(new Font("Tahoma", Font.PLAIN, 13));
			table.setRowHeight(25);		
			table.setFillsViewportHeight(true);
	 
			scrollPane = new WebScrollPane(table);
			Section.add(scrollPane, BorderLayout.CENTER);
			

	 }
	 public void update(Observable oObservable, Object oObject) {
	        oUserInfo = ((ObjectData) oObservable); // cast
	    }
	 public void getData(String visitdate){
			table.setModel(fetchData(visitdate));
			TableColumnModel columnModelOPDform = table.getColumnModel();
			columnModelOPDform.getColumn(0).setPreferredWidth(40);
			columnModelOPDform.getColumn(1).setPreferredWidth(40);
			columnModelOPDform.getColumn(2).setPreferredWidth(100);
			columnModelOPDform.getColumn(3).setPreferredWidth(60);
			columnModelOPDform.getColumn(4).setPreferredWidth(60);
			columnModelOPDform.getColumn(5).setPreferredWidth(60);

			((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchData(String visitdate){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
    	Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		String sql_ = "select distinct visitdate,vn,hn,cid,appcode from ecddata where visitdate='"+ Setup.DateInDBMSSQL(visitdate)+"' ";

		conn = new DBmanager().getConnMySql();
		try {
			stmt = conn.prepareStatement(sql_);
			rs = stmt.executeQuery();
			int p=1;
			double total=0.0;
			while (rs.next()) {
				final Vector<String> vstring = new Vector<String>();
				vstring.add(" "+Integer.toString(p) );
				vstring.add(rs.getString(1).trim());
				vstring.add(rs.getString(2).trim());
				vstring.add(rs.getString(3).trim());
				vstring.add(rs.getString(4).trim());
				vstring.add(rs.getString(5).trim());
			
				data.add(vstring);
                p++;

			}
					
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return new DefaultTableModel(data, columnNames);
		
	}
	public void getDataToExport(String visitdate){

		String sql="select visitdate,vn,hn,cid,appcode from ecddata where  visitdate='"+ Setup.DateInDBMSSQL(visitdate)+"'" ;
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
	        writer.write("VISITDATE|VN|HN|CID|APPCODE\n");
			
			while (rs.next()){
				 writer.write(rs.getString(1)+"|") ;
				 writer.write(rs.getString(2)+"|");
				 writer.write(rs.getString(3)+"|");
				 writer.write(rs.getString(4)+"|");
				 writer.write(rs.getString(5));
	
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