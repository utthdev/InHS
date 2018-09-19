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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

public class DentalCommTxPanel extends WebPanel implements Observer,ActionListener{
	ObjectData oUserInfo;
	 
    int width,height;
    int exam_status=0;
    WebPanel LeftSection,MainSection,RightSection,right1,right2,Panel43;
    WebPanel MiddleSection,mid2,mid1,txPanel;
    JScrollPane scrollPaneMid2,scrollPaneTx;
    WebButton ButtonTreatment,ButtonDelTx,ButtonSID,ButtonClearTx;
    WebLabel l_provider,l_toothno,l_diag,l_tx,l_date,l_id;
    WebTextField TextField_doctor,TextField_toothno,TextField_diag,TextField_tx,TextField_date,TextField_id;
    
	Vector<String> columnNamesTx;
	WebTable tableTx;
	ArrayList<String> intx = new ArrayList<String>();
	ArrayList<String> indr = new ArrayList<String>();
    
    public DentalCommTxPanel(ObjectData oUserInfo, int w, int h) {
    	super();
		this.oUserInfo=oUserInfo;
		width=w;
	    height=h;
		setLayout(new BorderLayout(0, 0));

		setPreferredSize(new Dimension(width-(width*2)/10, height-175));
		
		columnNamesTx = new Vector<String>();
		columnNamesTx.add("");
		columnNamesTx.add("  วันที่ให้การรักษา");
		columnNamesTx.add("  ตำแหน่ง");
		columnNamesTx.add("  การวินิจฉัย");
		columnNamesTx.add("  การรักษา");
		columnNamesTx.add("  ผู้ให้การรักษา");
		
		getTx();
		

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

		//setupPanel();
		
		add(MainSection, BorderLayout.CENTER);
    	
    }
    public void update(Observable oObservable, Object oObject) {
		
		oUserInfo = ((ObjectData)oObservable); // cast
		 
	}
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == ButtonTreatment){
    		String doctor="",toothno="",diag="",treatment="",date="";
			if(TextField_doctor.getText().trim().equals("")){
    			doctor="047";
    		}else{
    			String dr=TextField_doctor.getText().trim();
    			doctor=dr.substring(0, dr.indexOf(":"));
    		}
			if(TextField_diag.getText().trim().equals("")){
    			diag="";
    		}else{
    			 
    			diag=TextField_diag.getText().trim();
    		}
			if(TextField_toothno.getText().trim().equals("")){
    			toothno="01";
    		}else{
    			 
    			toothno=TextField_toothno.getText().trim();
    		}
			if(TextField_tx.getText().trim().equals("")){
    			treatment="";
    		}else{
    			String tx=TextField_tx.getText().trim();
    			treatment=tx.substring(0,tx.indexOf(":"));
    			//treatment=TextField_tx.getText().trim();
    		}
			TextField_date.setText(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
    		date=TextField_date.getText().trim();
    		
    		System.out.println("--"+treatment+"--"+date);
    		//update data
    		Connection con6;
    		PreparedStatement stmt6,stmt2,stmt3;
    		ResultSet rs6,rs2;
    		int data_status=0;
    		try {
					con6 = new DBmanager().getConnMySql();
					 String sql_check_in_database="select hn from dental_activity where hn='"+oUserInfo.GetPtHN()+"' and toothno='"+toothno+"' and treatment='"+treatment+"' and diag='"+diag+"' and doctor='"+doctor+"' and visitdatetime='"+date+"'";
					 stmt2 = con6.prepareStatement(sql_check_in_database);			
					 rs2 = stmt2.executeQuery();
					 System.out.println("HN..--"+oUserInfo.GetPtHN()+" "+date+" "+toothno+" "+diag+" "+treatment+" "+doctor);
					 while( rs2.next() ){
						 //System.out.println("--"+rs2.getString(1));
						 if(rs2.getString(1) !=null){
							 //data_status=1;
							// String sql_update_data="update dental_activity set  toothno='"+toothno+"',treatment='"+treatment+"',diag='"+diag+"',doctor='"+doctor+"',visitdatetime='"+date+"' where hn='"+hn+"' ";
					    	 	
							// int rs_update =  stmt3.executeUpdate(sql_update_data);
					    	 
					    	 String sql_del_data="delete from dental_activity where hn='"+oUserInfo.GetPtHN()+"' and toothno='"+toothno+"' and treatment='"+treatment+"' and diag='"+diag+"' and doctor='"+doctor+"' and visitdatetime='"+date+"'";
					    	 stmt3 = con6.prepareStatement(sql_del_data);
					    	 int rs_update =  stmt3.executeUpdate();
							 stmt3.close();
							 
						 }
					 }
					 if(data_status==0){
						 String sql_insert_data="insert into dental_activity (visitdatetime,toothno,treatment,diag,doctor,amt,vn,typeplace,hn) values ('"+date+"','"+toothno+"','"+treatment+"','"+diag+"','"+doctor+"','','','2','"+oUserInfo.GetPtHN()+"')";
						 stmt6 = con6.prepareStatement(sql_insert_data);	
						 int rs_insert = stmt6.executeUpdate();
						 stmt6.close();
					 }
					 stmt2.close();
					 data_status=0;
	    			
	    		
	    			con6.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		
    		TextField_tx.setText("");
    		TextField_doctor.setText("");
    		TextField_diag.setText("");
    		TextField_toothno.setText("");
    		TextField_date.setText(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
    		tableTx.setModel(fetchDataTx());
			TableColumnModel columnModelTx = tableTx.getColumnModel();
			columnModelTx.getColumn(0).setPreferredWidth(30);
			columnModelTx.getColumn(1).setPreferredWidth(100);
			columnModelTx.getColumn(2).setPreferredWidth(80);
			columnModelTx.getColumn(3).setPreferredWidth(80);
			columnModelTx.getColumn(4).setPreferredWidth(80);
			columnModelTx.getColumn(5).setPreferredWidth(50);
			 
			((DefaultTableModel)tableTx.getModel()).fireTableDataChanged(); 
			
    	}
    	else if(e.getSource() == ButtonDelTx){
			//System.out.println(TextField_toothno.getText().trim()+"-"+TextField_tx.getText().trim()+"-"+TextField_diag.getText().trim()+"-"+TextField_doctor.getText().trim()+"-"+TextField_date.getText().trim());
			Connection con6;
    		PreparedStatement stmt6;   		
    		try {
				con6 = new DBmanager().getConnMySql();
				String sql_del_tx="delete from dental_activity where hn='"+oUserInfo.GetPtHN()+"' and toothno='"+TextField_toothno.getText().trim()+"' and treatment='"+TextField_tx.getText().trim()+"' and diag='"+TextField_diag.getText().trim()+"' and doctor='"+TextField_doctor.getText().trim()+"' and visitdatetime='"+TextField_date.getText().trim()+"'";
				 stmt6 = con6.prepareStatement(sql_del_tx);	
				 int rs_insert = stmt6.executeUpdate();
				 stmt6.close();
				 
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
    		TextField_tx.setText("");
    		TextField_doctor.setText("");
    		TextField_diag.setText("");
    		TextField_toothno.setText("");
    		TextField_date.setText(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
    		tableTx.setModel(fetchDataTx());
			TableColumnModel columnModelTx = tableTx.getColumnModel();
			columnModelTx.getColumn(0).setPreferredWidth(30);
			columnModelTx.getColumn(1).setPreferredWidth(100);
			columnModelTx.getColumn(2).setPreferredWidth(80);
			columnModelTx.getColumn(3).setPreferredWidth(80);
			columnModelTx.getColumn(4).setPreferredWidth(80);
			columnModelTx.getColumn(5).setPreferredWidth(50);
			 
			((DefaultTableModel)tableTx.getModel()).fireTableDataChanged(); 
		}
		else if(e.getSource() == ButtonClearTx){
			oUserInfo.setPtHN("");
			oUserInfo.setPtLabel("");
			oUserInfo.setPtCID("");
			TextField_tx.setText("");
			TextField_id.setText("");
    		TextField_doctor.setText("");
    		TextField_diag.setText("");
    		TextField_toothno.setText("");
    		TextField_date.setText(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
    		getDataClearTx();
		}
    }
    public void setupPanel() {
    	if(oUserInfo.GetPtHN().length() !=7){
			JOptionPane.showMessageDialog(this,"Please select Patient","Patient Error",JOptionPane.ERROR_MESSAGE);

		}else{
			 clearPanel() ;
			mid2.removeAll();
			txPanel = new WebPanel();
			txPanel.setLayout(null);
			
			l_date=new WebLabel("วันที่ให้บริการ ",WebLabel.RIGHT);
			l_date.setBounds(50,40,80,20);			
			txPanel.add(l_date);
			
			TextField_date= new WebTextField();
			TextField_date.setBounds(140,40,80,20);
			TextField_date.setEditable(false);
			TextField_date.setText(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
			TextField_date.addActionListener(this);
			txPanel.add(TextField_date);
			 
			l_id=new WebLabel("เลขที่บัตรประชาชน ",WebLabel.RIGHT);
			l_id.setBounds(230,40,100,20);			
			//txPanel.add(l_id);
			
			TextField_id= new WebTextField();
			TextField_id.setBounds(340,40,150,20);
			TextField_id.setText("");
			TextField_id.addActionListener(this);
			//txPanel.add(TextField_id);
			
			ButtonSID = new WebButton("Search");		 
			ButtonSID.setBounds(520,40,80,20);		 
			ButtonSID.addActionListener(this);
			if(oUserInfo.GetPtVN().equals("")){
				//txPanel.add(ButtonSID);
			}
			
			l_toothno=new WebLabel("ตำแหน่งที่รักษา  ",WebLabel.LEFT);
			l_toothno.setBounds(60,80,100,20);			
			txPanel.add(l_toothno);
			
			TextField_toothno= new WebTextField();
			TextField_toothno.setBounds(70,100,50,20);
			TextField_toothno.setText("");
			TextField_toothno.addActionListener(this);
			txPanel.add(TextField_toothno);
			
			l_diag=new WebLabel("การวินิจฉัย  ",WebLabel.LEFT);
			l_diag.setBounds(170,80,150,20);			
			txPanel.add(l_diag);
			
			TextField_diag= new WebTextField();
			TextField_diag.setBounds(150,100,100,20);
			TextField_diag.setText("");
			TextField_diag.addActionListener(this);
			txPanel.add(TextField_diag);
			
			l_tx=new WebLabel("การรักษา  ",WebLabel.LEFT);
			l_tx.setBounds(330,80,100,20);			
			txPanel.add(l_tx);
			
			//TextField_tx= new WebTextField();
			//TextField_tx.setBounds(300,100,100,20);
			//TextField_tx.setText("");
			//TextField_tx.addActionListener(this);
			//txPanel.add(TextField_tx);
			
			TextField_tx = new WebTextField();
	        setupAutoComplete(TextField_tx, intx);
	        TextField_tx.setColumns(30);
	        TextField_tx.setBounds(300, 100, 250, 20);
	        txPanel.add(TextField_tx);
	        
			l_provider=new WebLabel("ผู้ตรวจ  ",WebLabel.LEFT);
			l_provider.setBounds(580,80,80,20);
				
			txPanel.add(l_provider);
			
			TextField_doctor = new WebTextField();
			setupAutoComplete(TextField_doctor, indr);
		    TextField_doctor.setColumns(30);
		    TextField_doctor.setBounds(580, 100, 200, 20);
			//TextField_doctor.setBounds(580,100,100,20);
			//TextField_doctor.setText("");
			//TextField_doctor.addActionListener(this);
			txPanel.add(TextField_doctor);
			
			//database
			Vector<Vector<String>> dataTx = new Vector<Vector<String>>();
				
			DefaultTableModel modelTx = new DefaultTableModel(dataTx, columnNamesTx)
		    {
				public Class getColumnClass(int column)
				{
					return getValueAt(0, column).getClass();
				}
			};
			tableTx = new WebTable(modelTx)
		    {
				public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
				{
					Component c = super.prepareRenderer(renderer, row, column);

		 
					return c;
				}
		    };
		    TableColumnModel columnModelTx = tableTx.getColumnModel();
			columnModelTx.getColumn(0).setPreferredWidth(30);
			columnModelTx.getColumn(1).setPreferredWidth(100);
			columnModelTx.getColumn(2).setPreferredWidth(80);
			columnModelTx.getColumn(3).setPreferredWidth(80);
			columnModelTx.getColumn(4).setPreferredWidth(80);
			columnModelTx.getColumn(5).setPreferredWidth(50);
			 
			tableTx.setPreferredScrollableViewportSize(new Dimension(600, 200));
			tableTx.setFillsViewportHeight(true);
			tableTx.setRowHeight(30);			 
			tableTx.addMouseListener(new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 int row_tableTx = tableTx.rowAtPoint(e.getPoint());
				        int col_tabletx = tableTx.columnAtPoint(e.getPoint());
				        String date=tableTx.getValueAt(row_tableTx,1).toString().trim();
				        String toothno=tableTx.getValueAt(row_tableTx,2).toString().trim();
				        String diag=tableTx.getValueAt(row_tableTx,3).toString().trim();
				        String treatment=tableTx.getValueAt(row_tableTx,4).toString().trim();
				        String doctor=tableTx.getValueAt(row_tableTx,5).toString().trim();
				        
				        TextField_date.setText(date);
				        TextField_toothno.setText(toothno);
				        TextField_diag.setText(diag);
				        TextField_tx.setText(treatment);
				        TextField_doctor.setText(doctor);
				 }
				
			});
			scrollPaneTx = new JScrollPane(tableTx);
			scrollPaneTx.setBounds(50, 150, 600, 200);
			txPanel.add(scrollPaneTx);
			
			getDataTx();
			
			mid2.add(txPanel, BorderLayout.CENTER);
			mid2.revalidate();
			mid2.repaint();
			
			right1.removeAll();
			/*
			ButtonPrintExam1 = new JButton("Print");		 
			ButtonPrintExam1.setBounds(5,40,80,20);		 
			ButtonPrintExam1.addActionListener(this);
			if(vn.equals("")){
				right1.add(ButtonPrintExam1);
			}
			*/
			ButtonTreatment = new WebButton("Save");		 
			ButtonTreatment.setBounds(5,40,80,20);		 
			ButtonTreatment.addActionListener(this);
			if(oUserInfo.GetPtVN().equals("")){
				right1.add(ButtonTreatment);
			}
			ButtonClearTx = new WebButton("Clear");		 
			ButtonClearTx.setBounds(5,70,80,20);		 
			ButtonClearTx.addActionListener(this);
			if(oUserInfo.GetPtVN().equals("")){
				right1.add(ButtonClearTx);
			}
			ButtonDelTx = new WebButton("Delete");		 
			ButtonDelTx.setBounds(5,100,80,20);		 
			ButtonDelTx.addActionListener(this);
			if(oUserInfo.GetPtVN().equals("")){
				right1.add(ButtonDelTx);
			}
			
			right1.revalidate();
			right1.repaint();
			MiddleSection.revalidate();
			MiddleSection.repaint();
			RightSection.revalidate();
			RightSection.repaint();
		}
    }
    public void clearPanel() {
		mid2.removeAll();
		mid2.revalidate();
		mid2.repaint();	
		MiddleSection.revalidate();
		MiddleSection.repaint();
		//right
		right1.removeAll();
		right1.revalidate();
		right1.repaint();
		RightSection.revalidate();
		RightSection.repaint();
	}
    public void getDataTx(){
		tableTx.setModel(fetchDataTx());
		TableColumnModel columnModelTx = tableTx.getColumnModel();
		columnModelTx.getColumn(0).setPreferredWidth(30);
		columnModelTx.getColumn(1).setPreferredWidth(100);
		columnModelTx.getColumn(2).setPreferredWidth(80);
		columnModelTx.getColumn(3).setPreferredWidth(80);
		columnModelTx.getColumn(4).setPreferredWidth(80);
		columnModelTx.getColumn(5).setPreferredWidth(50);
		((DefaultTableModel)tableTx.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataTx(){

		Vector<Vector<String>> dataDataTx = new Vector<Vector<String>>();
		String sql_datatx = "select  visitdatetime,toothno,diag,treatment,doctor,typeplace from dental_activity where hn='"+oUserInfo.GetPtHN()+"' and hn !='' order by visitdatetime";
		
		//String sql_formopd = "select form_code,form_name,page  from formopd where type='1' and clinic= '"+clinic+"'  order by orderpage";
		Connection conn;
		PreparedStatement stmt;
		try {
			conn = new DBmanager().getConnMySql(); 
	        stmt = conn.prepareStatement(sql_datatx);	         
	        ResultSet rs = stmt.executeQuery();
	        int p=1;
	        
	        while (rs.next()) {
	        	final Vector<String> vstringDataTx = new Vector<String>();
	        	vstringDataTx.add(" "+Integer.toString(p));
	        	vstringDataTx.add(rs.getString(1).trim().substring(0, 10));	           
	        	vstringDataTx.add(rs.getString(2).trim());
	        	vstringDataTx.add(rs.getString(3).trim());
	        	vstringDataTx.add(rs.getString(4).trim());
	        	vstringDataTx.add(rs.getString(5).trim());
	        	 
		           
	        	dataDataTx.add(vstringDataTx); 
	            p++;
	        }
	        stmt.close();
	    	conn.close();
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		return new DefaultTableModel(dataDataTx, columnNamesTx);
	}
	public void getDataClearTx(){
		tableTx.setModel(fetchDataClearTx());
		TableColumnModel columnModelTx = tableTx.getColumnModel();
		columnModelTx.getColumn(0).setPreferredWidth(30);
		columnModelTx.getColumn(1).setPreferredWidth(100);
		columnModelTx.getColumn(2).setPreferredWidth(80);
		columnModelTx.getColumn(3).setPreferredWidth(80);
		columnModelTx.getColumn(4).setPreferredWidth(80);
		columnModelTx.getColumn(5).setPreferredWidth(50);
		((DefaultTableModel)tableTx.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataClearTx(){

		Vector<Vector<String>> dataDataTx = new Vector<Vector<String>>();
	
		return new DefaultTableModel(dataDataTx, columnNamesTx);
	}
	public void getTx() {
		String query="select code,name from dentalcode  order by code";
		String query_dr="select provider,name,lname from provider where providertype in ('02','06') order by provider desc";
		Connection conn = new DBmanager().getConnMySql();
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	String txcode="",txname="";
	        	if(rs.getString(1)!=null) {
	        		txcode=rs.getString(1).trim();
	        	}
	        	if(rs.getString(2)!=null) {
	        		txname=rs.getString(2).trim();
	        		
	        	}
	        	if(txcode !="" && txname !="") {
	        		//if(StringUtils.isNumeric(txcode)) {
	        			//System.out.println(drcode+":"+drname);
	        			//indr.add(drcode+":"+drname);
	        			intx.add(txcode+":"+txname);
	        		//}
	        		 
	        	}
	        	  
	        }
	        stmt.close();
	        //
	        PreparedStatement stmt1 = conn.prepareStatement(query_dr);
			ResultSet rs1 = stmt1.executeQuery();
	        while (rs1.next()) {
	        	String drcode="",drname="";
	        	if(rs1.getString(1)!=null) {
	        		drcode=rs1.getString(1).trim();
	        	}
	        	if(rs1.getString(2)!=null) {
	        		drname=rs1.getString(2).trim()+"  "+rs1.getString(3).trim();
	        		
	        	}
	        	if(drcode !="" && drname !="") {
	        		if(StringUtils.isNumeric(drcode)) {
	        			//System.out.println(drcode+":"+drname);
	        			//indr.add(drcode+":"+drname);
	        			indr.add(drcode+":"+drname);
	        		}
	        		 
	        	}
	        	  
	        }
	        stmt1.close();
	        
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
		
	}
	private  boolean isAdjusting(JComboBox cbInput) {
        if (cbInput.getClientProperty("is_adjusting") instanceof Boolean) {
            return (Boolean) cbInput.getClientProperty("is_adjusting");
        }
        return false;
    }

    private  void setAdjusting(JComboBox cbInput, boolean adjusting) {
        cbInput.putClientProperty("is_adjusting", adjusting);
    }
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public  void setupAutoComplete(final JTextField txtInput, final ArrayList<String> items) {
        final DefaultComboBoxModel model = new DefaultComboBoxModel();
        final JComboBox cbInput = new JComboBox(model) {
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };
        setAdjusting(cbInput, false);
        for (String item : items) {
            model.addElement(item);
        }
        cbInput.setSelectedItem(null);
        cbInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdjusting(cbInput)) {
                    if (cbInput.getSelectedItem() != null) {
                        txtInput.setText(cbInput.getSelectedItem().toString());
                    }
                }
            }
        });

        txtInput.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                setAdjusting(cbInput, true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (cbInput.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.setSource(cbInput);
                    cbInput.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtInput.setText(cbInput.getSelectedItem().toString());
                        cbInput.setPopupVisible(false);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cbInput.setPopupVisible(false);
                }
                setAdjusting(cbInput, false);
            }
        });
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateList();
            }

            public void removeUpdate(DocumentEvent e) {
                updateList();
            }

            public void changedUpdate(DocumentEvent e) {
                updateList();
            }

            private void updateList() {
                setAdjusting(cbInput, true);
                model.removeAllElements();
                String input = txtInput.getText();
                if (!input.isEmpty()) {
                    for (String item : items) {
                        if (item.toLowerCase().startsWith(input.toLowerCase())) {
                            model.addElement(item);
                        }
                    }
                }
                cbInput.setPopupVisible(model.getSize() > 0);
                setAdjusting(cbInput, false);
            }
        });
        txtInput.setLayout(new BorderLayout());
        txtInput.add(cbInput, BorderLayout.SOUTH);
    }

}