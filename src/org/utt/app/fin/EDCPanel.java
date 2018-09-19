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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Setup;
import org.utt.app.util.TextTransfer;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class EDCPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebPanel Section,Right;
    WebTable table;
    WebScrollPane scrollPane;
    Vector<String> columnNames;
    String price="000000000000";
    String cid_in="0000000000000";
    WebLabel Label_edc,Label_price,Label_cid,Label_type,Label_appcode,Label_appcode1;
    WebTextField CIDTextField;
    WebRadioButton P1,P2,P3,P4;
    ButtonGroup PG = new ButtonGroup();
    WebButton ButtonEDC;
    
    String test="";
    
    int type_send=11;
    
    byte bufferStart[]= {0x02};
    byte bufferLength[];
    byte bufferRes[]= {0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
    
    byte bufferData3[] = {0x1C};
    byte bufferStop[]= {0x03};
    
    InputStream in=null;
	 OutputStream out=null;
	 SerialPort serialPort=null;
	 SerialWriterNew sn=null;
	 TextTransfer textTransfer;
    
    public EDCPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        textTransfer = new TextTransfer();
        
        Section = new WebPanel();
        Section.setPreferredSize(new Dimension(width-(width*2)/10-300, height-160));
		add(Section, BorderLayout.CENTER);
		Section.setLayout(new BorderLayout(0, 0));
		
		setAR();
		
		Right = new WebPanel();
        Right.setPreferredSize(new Dimension(300, height-160));
		add(Right, BorderLayout.EAST);
		Right.setLayout(new BorderLayout(0, 0));
		
		columnNames = new Vector<String>();		
		columnNames.add("   ที่");
		columnNames.add("   VN");
		columnNames.add("   Ref No.");
		columnNames.add("   ห้องตรวจที่");
		columnNames.add("   Charge Code");
		columnNames.add("   AR Code");
		columnNames.add("   ราคา");
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
		
		
		WebPanel edcPanel =new WebPanel();
		edcPanel.setPreferredSize(new Dimension(300, 400));
		Right.add(edcPanel, BorderLayout.NORTH);
		edcPanel.setLayout(null);
		
		WebButton ButtonPrint = new WebButton("แสดงค่าใช้จ่าย");
		//ButtonPrint.setEnabled(false);
        ButtonPrint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	//price="000000000000";
            	getData(oUserInfo.GetPtVN(),oUserInfo.GetPtVisitdate());
            	price=table.getValueAt(table.getRowCount()-1,6).toString().trim();
            	System.out.println(">>>"+price+">>"+oUserInfo.GetPtCID());
            	Label_price.setText(" ค่าใช้จ่ายที่เรียกเก็บ  "+price);
            	CIDTextField.setText(oUserInfo.GetPtCID());
            	ButtonEDC.setEnabled(true);
            	ButtonPrint.setEnabled(false);
            	
            	
            }
        });
        //ButtonPrint.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/printer.png")));
        ButtonPrint.setBounds(10, 10, 100, 25);
        edcPanel.add(ButtonPrint);
        
        WebButton ButtonConn = new WebButton("เชื่อมต่อเครื่อง EDC");
        ButtonConn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	  
            }
        });
        //ButtonPrint.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/printer.png")));
        ButtonConn.setBounds(10, 200, 100, 25);
        //edcPanel.add(ButtonConn);
        
        Label_price = new WebLabel(" ค่าใช้จ่ายที่เรียกเก็บ  ");
		Label_price.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_price.setHorizontalAlignment(SwingConstants.LEFT);
		Label_price.setBounds(10,40,250,20);
		edcPanel.add(Label_price);
		
		Label_cid = new WebLabel(" CID ");
		Label_cid.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_cid.setHorizontalAlignment(SwingConstants.LEFT);
		Label_cid.setBounds(10,65,50,20);
		edcPanel.add(Label_cid);
		
		CIDTextField = new WebTextField();
		edcPanel.add(CIDTextField);
        CIDTextField.setBounds(70, 65, 120, 20);
        
        Label_type = new WebLabel(" ชนิดของการเรียกเก็บ ");
		Label_type.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_type.setHorizontalAlignment(SwingConstants.LEFT);
		Label_type.setBounds(10,90,150,20);
		edcPanel.add(Label_type);
		
		P1 = new WebRadioButton("ผู้ป่วยนอกทั่วไป สิทธิตนเองและครอบครัว");
		P1.setBackground(Setup.getColor());
		P1.setSelected(true);
		P1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 type_send=11;
			}
		});
		PG.add(P1);
		P1.setBounds(20, 115, 250, 25);
		edcPanel.add(P1);
		
		P2 = new WebRadioButton("ผู้ป่วยนอกทั่วไป สิทธิบุตร 0 - 7 ปี");
		P2.setBackground(Setup.getColor());
		P2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 type_send=12;
			}
		});
		PG.add(P2);
		P2.setBounds(20, 145, 250, 25);
		edcPanel.add(P2);
		
		P3 = new WebRadioButton("ผู้ป่วยนอกทั่วไป สิทธิคู่สมรสต่างชาติ");
		P3.setBackground(Setup.getColor());
		P3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 type_send=13;
			}
		});
		PG.add(P3);
		P3.setBounds(20, 170, 250, 25);
		edcPanel.add(P3);
		
		P4 = new WebRadioButton("ผู้ป่วยนอกทั่วไป ไม่สามารถใช้บัตรได้");
		P4.setBackground(Setup.getColor());
		P4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 type_send=14;
			}
		});
		PG.add(P4);
		P4.setBounds(20, 200, 250, 25);
		edcPanel.add(P4);
        
        Label_edc = new WebLabel(" ");
		Label_edc.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_edc.setHorizontalAlignment(SwingConstants.LEFT);
		Label_edc.setBounds(120,250,60,20);
		edcPanel.add(Label_edc);
		
		WebButton ButtonCopyAppCode = new WebButton("Copy AppCode");
        ButtonCopyAppCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	String coprapp=Label_appcode1.getText().trim();
            	System.out.println(">>"+coprapp);
            	textTransfer.setClipboardContents(coprapp);
            	System.out.println("Clipboard contains:" + textTransfer.getClipboardContents());
            	//StringSelection stringSelection = new StringSelection(coprapp);
            	 
            	//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            	//clipboard.setContents(stringSelection, this);
            }
        });
        ButtonCopyAppCode.setBounds(140, 250, 100, 25);
        edcPanel.add(ButtonCopyAppCode);
			
		ButtonEDC = new WebButton("EDC");
		ButtonEDC.setBounds(10,250, 100, 25);
		ButtonEDC.setEnabled(false);
        ButtonEDC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	  String last2=price.substring(price.indexOf(".")+1).trim();
            	  if(last2.length()==1 || last2.length()==2) {
            		  if(last2.length()==1) {
                		  last2=last2+"0";
                	  }
            		  String send_price=price.substring(0,price.indexOf("."))+last2 ;
                	             	  
                	  int total=12;
                	  int need=total-send_price.length();
                	  String add="";
                	  if(need==1) {
                		  add="0";
                	  }
                	  else if(need==2) {
                		  add="00";
                	  }
                	  else if(need==3) {
                		  add="000";
                	  }
                	  else if(need==4) {
                		  add="0000";
                	  }
                	  else if(need==5) {
                		  add="00000";
                	  }
                	  else if(need==6) {
                		  add="000000";
                	  }
                	  else if(need==7) {
                		  add="0000000";
                	  }
                	  else if(need==8) {
                		  add="00000000";
                	  }
                	  else if(need==9) {
                		  add="000000000";
                	  }
                	  else if(need==10) {
                		  add="0000000000";
                	  }
                	 
                	  String send_adj=add+send_price;
                	  System.out.println(">>>"+price+"---"+send_price+"----"+send_adj+"type send"+type_send);
                	  if(serialPort ==null) {
                		  connectNew("COM3",send_adj,type_send);
                	  }
                	  
                	  sn=new SerialWriterNew(out,send_adj,type_send);
                	  (new Thread(sn)).start();
                	  System.out.println("sent !!!!++++++++");
                	  //sendEDC("COM3",send_adj,type_send);
                	  //sendEDC1(send_adj,type_send);
                	  ButtonPrint.setEnabled(true);
                	  ButtonEDC.setEnabled(false);
                	  
                	  price="000000000000";
                	  

                	  
            	  }            	   
            	   System.out.println(">>>>>>>>>>>>"+test);
            }
        });
        edcPanel.add(ButtonEDC);
        
        Label_appcode = new WebLabel(" APP Code ");
		Label_appcode.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_appcode.setHorizontalAlignment(SwingConstants.LEFT);
		Label_appcode.setBounds(20,300,150,20);
		edcPanel.add(Label_appcode);
		Label_appcode1 = new WebLabel("");
		Label_appcode1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_appcode1.setHorizontalAlignment(SwingConstants.LEFT);
		Label_appcode1.setBounds(200,300,200,20);
		edcPanel.add(Label_appcode1);
		
    	
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public void getData(String vn,String visitdate){
		table.setModel(fetchData(vn,visitdate));
		TableColumnModel columnModelOPDform = table.getColumnModel();
		columnModelOPDform.getColumn(0).setPreferredWidth(40);
		columnModelOPDform.getColumn(1).setPreferredWidth(40);
		columnModelOPDform.getColumn(2).setPreferredWidth(100);
		columnModelOPDform.getColumn(3).setPreferredWidth(60);
		columnModelOPDform.getColumn(4).setPreferredWidth(60);
		columnModelOPDform.getColumn(5).setPreferredWidth(60);
		columnModelOPDform.getColumn(6).setPreferredWidth(60);

		((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
    public  DefaultTableModel fetchData(String vn,String visitdate){
    	Vector<Vector<String>> data = new Vector<Vector<String>>();
    	Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		String sql_ = "select vnrcpt.vn,vnrcpt.refno,vnrcptdtl.suffix,vnrcptdtl.chargecode,vnrcptdtl.tfaractcode,vnrcptdtl.amt from vnrcpt,vnrcptdtl where vnrcpt.vn=vnrcptdtl.vn and vnrcpt.visitdate=vnrcptdtl.visitdate "
				+ " and vnrcpt.visitdate=? and vnrcpt.vn=? and cxldatetime is null";	
		conn = new DBmanager().getConnMSSql();
		try {
			stmt = conn.prepareStatement(sql_);
			stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
			stmt.setString(2, vn);
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
				if(rs.getString(5)!=null) {
					vstring.add(rs.getString(5).trim());
				}else {
					vstring.add("");
				}
				 
				vstring.add(rs.getString(6).trim());
				total+=Double.parseDouble(rs.getString(6).trim());				
				data.add(vstring);
                p++;

			}
			final Vector<String> vstring1 = new Vector<String>();
			vstring1.add("");
			vstring1.add("");
			vstring1.add("");
			vstring1.add("");		
			vstring1.add("");
			vstring1.add(" รวมทั้งหมด ");
			vstring1.add(""+total);			
			data.add(vstring1);						
		} catch (SQLException e) {
			e.printStackTrace();
		}	
    	return new DefaultTableModel(data, columnNames);
	}
    public void setAR() {
    	Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		String sql_="select code,thainame from sysconfig where ctrlcode='20023' "; 
		conn = new DBmanager().getConnMSSql();
		try {
			stmt = conn.prepareStatement(sql_);
			rs = stmt.executeQuery();
			int p=1;
			while (rs.next()) {
				String c="",n="";
				if(rs.getString(1) !=null) {
					c=rs.getString(1).trim();
				}
				if(rs.getString(2) !=null) {
					n=rs.getString(2).trim().substring(1);
				}
				if(n.equals("")) {
					
				}else{
					//System.out.println(p+"."+c+"-"+n);
				}
				p++;
				 
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
    }
    /////////


    public void sendEDC(String portName,String price,int tsend) {
    	CommPortIdentifier portIdentifier;
        
        try {
        	portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			if ( portIdentifier.isCurrentlyOwned() ){
	            System.out.println("Error: Port is currently in use");
	        }
	        else{
	            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
	        	 if ( commPort instanceof SerialPort ){
	        		 SerialPort serialPort = (SerialPort) commPort;
		                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);             
		                InputStream in = serialPort.getInputStream();
		                
		                OutputStream outputStream = serialPort.getOutputStream();
		                if(tsend==11){
		    				byte bufferLength[]= {0x00,0x35};
		    				byte bufferHeader[]= {0x31,0x30,0x31,0x31,0x30,0x30,0x30,0x1C};
		    				byte bufferData1[] = {0x34,0x30,0x00,0x12};
		    				byte bufferData2[]=getData(price);
		    				byte bufferLRC[]= {0x10};
		    				try {
		    					outputStream.write(bufferStart);
		    					outputStream.write(bufferLength);
		    					outputStream.write(bufferRes); 
		    					outputStream.write(bufferHeader);
		    					outputStream.write(bufferData1);
		    					outputStream.write(bufferData2);
		    					outputStream.write(bufferData3);
		    					outputStream.write(bufferStop); 
		    					outputStream.write(bufferLRC);			
		    				} catch (IOException e1) {
		    					e1.printStackTrace();
		    				}
		            	}
		            	else if(tsend==12){
		            		byte bufferLength[]= {0x00,0x53};
		    				byte bufferHeader[]= {0x31,0x30,0x31,0x32,0x30,0x30,0x30,0x1C};
		    				byte bufferData1[] = {0x34,0x30,0x00,0x12};
		    				byte bufferData2[]=getData(price);
		    				byte bufferData4[] = {0x37,0x31,0x00,0x13};
		    				byte bufferData5[]=getData(cid_in);
		    				byte bufferLRC[]= {0x4D};
		    				try {
		    					outputStream.write(bufferStart);
		    					outputStream.write(bufferLength);
		    					outputStream.write(bufferRes); 
		    					outputStream.write(bufferHeader);
		    					outputStream.write(bufferData1);
		    					outputStream.write(bufferData2);
		    					outputStream.write(bufferData3);
		    					outputStream.write(bufferData4);
		    					outputStream.write(bufferData5);
		    					outputStream.write(bufferData3);			
		    					outputStream.write(bufferStop); 
		    					outputStream.write(bufferLRC);			
		    				} catch (IOException e1) {
		    					e1.printStackTrace();
		    				}
		            	}
		            	else if(tsend==13){
		            		byte bufferLength[]= {0x00,0x53};
		    				byte bufferHeader[]= {0x31,0x30,0x31,0x33,0x30,0x30,0x30,0x1C};
		    				byte bufferData1[] = {0x34,0x30,0x00,0x12};
		    				byte bufferData2[]=getData(price);
		    				byte bufferData4[] = {0x37,0x31,0x00,0x13};
		    				byte bufferData5[]=getData(cid_in);
		    				byte bufferLRC[]= {0x4D};
		    				try {
		    					outputStream.write(bufferStart);
		    					outputStream.write(bufferLength);
		    					outputStream.write(bufferRes); 
		    					outputStream.write(bufferHeader);
		    					outputStream.write(bufferData1);
		    					outputStream.write(bufferData2);
		    					outputStream.write(bufferData3);
		    					outputStream.write(bufferData4);
		    					outputStream.write(bufferData5);
		    					outputStream.write(bufferData3);
		    					outputStream.write(bufferStop); 
		    					outputStream.write(bufferLRC);			
		    				} catch (IOException e1) {
		    					e1.printStackTrace();
		    				}
		            	}
		            	else if(tsend==14){
		            		byte bufferLength[]= {0x00,0x53};
		    				byte bufferHeader[]= {0x31,0x30,0x31,0x34,0x30,0x30,0x30,0x1C};
		    				byte bufferData1[] = {0x34,0x30,0x00,0x12};
		    				byte bufferData2[]=getData(price);
		    				byte bufferData4[] = {0x37,0x31,0x00,0x13};
		    				byte bufferData5[]=getData(cid_in);
		    				byte bufferLRC[]= {0x4D};
		    				try {
		    					outputStream.write(bufferStart);
		    					outputStream.write(bufferLength);
		    					outputStream.write(bufferRes); 
		    					outputStream.write(bufferHeader);
		    					outputStream.write(bufferData1);
		    					outputStream.write(bufferData2);
		    					outputStream.write(bufferData3);
		    					outputStream.write(bufferData4);
		    					outputStream.write(bufferData5);
		    					outputStream.write(bufferData3);
		    					outputStream.write(bufferStop); 
		    					outputStream.write(bufferLRC);			
		    				} catch (IOException e1) {
		    					e1.printStackTrace();
		    				}
		            	}
		    	
		              
		                
		                 
		                serialPort.removeEventListener();
	        	 }
	        	 else{
		                System.out.println("Error: Only serial ports are handled by this example.");
		         }
	        	 commPort .close();
	        }
			 
			System.out.println("sent !!!!");
		} catch (IOException | NoSuchPortException | UnsupportedCommOperationException | PortInUseException e) {
			e.printStackTrace();
		}
    }
    
    public byte [] getData(String price) {
		byte num0 = 0x30;
		byte num1 = 0x31;
		byte num2 = 0x32;
		byte num3 = 0x33;
		byte num4 = 0x34;
		byte num5 = 0x35;
		byte num6 = 0x36;
		byte num7 = 0x37;
		byte num8 = 0x38;
		byte num9 = 0x39;
		
		
		byte n1=30,n2=30,n3=30,n4=30,n5=30,n6=30,n7=30,n8=30,n9=30,n10=30,n11=30,n12=30;
		byte [] data ={n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12};
		 
		if(price.length()==12) {
			String ary [] =price.split("");

			for(int i=0;i<ary.length;i++) {
				if(ary[i].trim().equals("0")) {
					data[i]=num0;
				}else if(ary[i].trim().equals("1")){
					data[i]=num1;
				}else if(ary[i].trim().equals("2")){
					data[i]=num2;
				}else if(ary[i].trim().equals("3")){
					data[i]=num3;
				}else if(ary[i].trim().equals("4")){
					data[i]=num4;
				}else if(ary[i].trim().equals("5")){
					data[i]=num5;
				}else if(ary[i].trim().equals("6")){
					data[i]=num6;
				}else if(ary[i].trim().equals("7")){
					data[i]=num7;
				}else if(ary[i].trim().equals("8")){
					data[i]=num8;
				}else if(ary[i].trim().equals("9")){
					data[i]=num9;
				}
			}
			 
			
		}

		return data;
	}
    public void showAppCode(String cid,String visitdate) {
    	String appcode="";
    	Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		String sql_ ="select distinct appcode from ecddata where cid='"+cid+"' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"'";
		conn = new DBmanager().getConnMySql();
		try {
			stmt = conn.prepareStatement(sql_);
			rs = stmt.executeQuery();
			while (rs.next()) {
				appcode=rs.getString(1).trim();
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Label_appcode1.setText(appcode);
    }
    
    public int connectNew( String portName,String price_send,int typesend ){
    	int result=0;
    	CommPortIdentifier portIdentifier;   	 
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			 if ( portIdentifier.isCurrentlyOwned() )
		        {
		            System.out.println("Error: Port is currently in use");
		        }
		        else
		        {
		            CommPort commPort;
					try {
						commPort = portIdentifier.open(this.getClass().getName(),2000);
						 if ( commPort instanceof SerialPort )
				            {
				                serialPort = (SerialPort) commPort;
				                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
				                
				                in = serialPort.getInputStream();
				                out = serialPort.getOutputStream();
				                (new Thread(new SerialReaderNew(in))).start();
				                //(new Thread(sn)).start();

				            }
				            else
				            {
				                System.out.println("Error: Only serial ports are handled by this example.");
				            }
					} catch (PortInUseException | UnsupportedCommOperationException | IOException e) {

						e.printStackTrace();
					}
		            
		            
		        }  
			  
		} catch (NoSuchPortException e1) {
			e1.printStackTrace();
		}
        
    	
    	
    	return result;
    }
    /** */
    public class SerialReaderNew implements Runnable 
    {
        InputStream in;
        
        public SerialReaderNew ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                	String stringis=new String(buffer,0,len);
                    System.out.println(stringis);
                    int a=stringis.trim().indexOf("APPROVED");
   	             	int b=stringis.trim().indexOf("APPROVED 65");
   	             	int c=stringis.trim().indexOf("xxxxxx");
   	             if(a>0) {
	            	 String aa=stringis.trim().substring(0,a).trim();
	            	 String aaa=stringis.trim().substring(aa.length()-12,aa.length()-2).trim();
	            	 String ab=stringis.trim().substring(b+10,b+16).trim();
	            	 String c1=stringis.trim().substring(c-3,c+1).trim();
	            	 String c2=stringis.trim().substring(c+6,c+10).trim();
	            	 String d=stringis.trim().substring(b+13,b+21).trim();
	            	 String e=stringis.trim().substring(b+23,b+38).trim();
	            	 
	            	 System.out.println(aa+"-"+aaa+"-"+ab+"-"+stringis.trim().substring(stringis.trim().length()-16).trim()+"-"+c1+"-->"+c2);
	            	 System.out.println(aa+"***"+aaa+"----"+ab+"*****"+c1+"---"+c2+"-d-"+d+"-e-"+e);
	            	 
	            	  
	            	 
	            	 Connection con;
			          PreparedStatement stmt,stmt1;
			          String sql_="insert into ecddata (visitdate,vn,hn,cid,appcode,d_update) "
			          		+ "  values ('"+Setup.GetDateNow()+"','"+oUserInfo.GetPtVN()+"','"+oUserInfo.GetPtHN()+"','"+oUserInfo.GetPtCID()+"','"+aaa+"','"+Setup.GetDateTimeNow()+"')";
			          String sql_dev="insert into ecddev (visitdate,vn,hn,cid,message,dateupdate) "
				          		+ "  values ('"+Setup.GetDateNow()+"','"+oUserInfo.GetPtVN()+"','"+oUserInfo.GetPtHN()+"','"+oUserInfo.GetPtCID()+"','"+stringis.trim()+"','"+Setup.GetDateTimeNow()+"')";
				        
			          con = new DBmanager().getConnMySql();
			          try {
						stmt = con.prepareStatement(sql_);
						int rs_save = stmt.executeUpdate();
				        stmt.close();
				        //
				        stmt1 = con.prepareStatement(sql_dev);
						int rs_save1 = stmt1.executeUpdate();
				        stmt1.close();
				        //
				        con.close();
				  
			          } catch (SQLException e1) {
						e1.printStackTrace();
			          }
			          showAppCode(oUserInfo.GetPtCID(),oUserInfo.GetPtVisitdate());
	    
	             }
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public  class SerialWriterNew implements Runnable 
    {
        OutputStream outputStream;
        String price_;
        int tsend;
        
        public SerialWriterNew ( OutputStream outputStream ,String price,int type)
        {
            this.outputStream = outputStream;
            price_=price;
            tsend=type;
        }     
        public void run (){
        	
        	if(tsend==11) {
        	   byte bufferStart[]= {0x02};
  			   byte bufferLength[]= {0x00,0x35};
  			   byte bufferRes[]= {0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
  			   byte bufferHeader[]= {0x31,0x30,0x31,0x31,0x30,0x30,0x30,0x1C};
  			   byte bufferData1[] = {0x34,0x30,0x00,0x12};
  			   byte bufferData2[]=getData(price_);		
  			   byte bufferData3[] = {0x1C};
  			   byte bufferStop[]= {0x03};
  			   byte bufferLRC[]= {0x10};
  			  try {
					outputStream.write(bufferStart);
					 outputStream.write(bufferLength); 
					   outputStream.write(bufferRes); 
					   outputStream.write(bufferHeader);
					   
					   outputStream.write(bufferData1);
					   outputStream.write(bufferData2);
					   outputStream.write(bufferData3);
					   
					   outputStream.write(bufferStop); 
					   outputStream.write(bufferLRC); 
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	else if(tsend==12 ||tsend==14){
        		
        		byte bufferStart[]= {0x02};
        		 
        		byte bufferLength[]= {0x00,0x53};
        		byte bufferRes[]= {0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
				byte bufferHeader[]= {0x31,0x30,0x31,0x34,0x30,0x30,0x30,0x1C};
				byte bufferData1[] = {0x34,0x30,0x00,0x12};
				byte bufferData2[]=getData(price);
				byte bufferData3[] = {0x1C};
				byte bufferData4[] = {0x37,0x33,0x00,0x13};
				byte bufferData5[]={0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
				byte bufferStop[]= {0x03};
				byte bufferLRC[]= {0x4D};
				try {
					outputStream.write(bufferStart);
					outputStream.write(bufferLength);
					outputStream.write(bufferRes); 
					outputStream.write(bufferHeader);
					outputStream.write(bufferData1);
					outputStream.write(bufferData2);
					outputStream.write(bufferData3);
					outputStream.write(bufferData4);
					outputStream.write(bufferData5);
					outputStream.write(bufferData3);
					outputStream.write(bufferStop); 
					outputStream.write(bufferLRC);			
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        	else if(tsend==13){
        		
        		byte bufferStart[]= {0x02};
        		 
        		byte bufferLength[]= {0x00,0x53};
        		byte bufferRes[]= {0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
				byte bufferHeader[]= {0x31,0x30,0x31,0x34,0x30,0x30,0x30,0x1C};
				byte bufferData1[] = {0x34,0x30,0x00,0x12};
				byte bufferData2[]=getData(price);
				byte bufferData3[] = {0x1C};
				byte bufferData4[] = {0x37,0x33,0x00,0x13};
				byte bufferData5[]={0x42,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
				byte bufferStop[]= {0x03};
				byte bufferLRC[]= {0x4D};
				try {
					outputStream.write(bufferStart);
					outputStream.write(bufferLength);
					outputStream.write(bufferRes); 
					outputStream.write(bufferHeader);
					outputStream.write(bufferData1);
					outputStream.write(bufferData2);
					outputStream.write(bufferData3);
					outputStream.write(bufferData4);
					outputStream.write(bufferData5);
					outputStream.write(bufferData3);
					outputStream.write(bufferStop); 
					outputStream.write(bufferLRC);			
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        			   
        }
    } 
    public byte [] getDataCID(String cid) {
		byte num0 = 0x30;
		byte num1 = 0x31;
		byte num2 = 0x32;
		byte num3 = 0x33;
		byte num4 = 0x34;
		byte num5 = 0x35;
		byte num6 = 0x36;
		byte num7 = 0x37;
		byte num8 = 0x38;
		byte num9 = 0x39;
			
		byte n1=30,n2=30,n3=30,n4=30,n5=30,n6=30,n7=30,n8=30,n9=30,n10=30,n11=30,n12=30,n13=30;
		byte [] data ={n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13};
		 
		if(cid.length()==13) {
			String ary [] =cid.split("");
			for(int i=0;i<ary.length;i++) {
				if(ary[i].trim().equals("0")) {
					data[i]=num0;
				}else if(ary[i].trim().equals("1")){
					data[i]=num1;
				}else if(ary[i].trim().equals("2")){
					data[i]=num2;
				}else if(ary[i].trim().equals("3")){
					data[i]=num3;
				}else if(ary[i].trim().equals("4")){
					data[i]=num4;
				}else if(ary[i].trim().equals("5")){
					data[i]=num5;
				}else if(ary[i].trim().equals("6")){
					data[i]=num6;
				}else if(ary[i].trim().equals("7")){
					data[i]=num7;
				}else if(ary[i].trim().equals("8")){
					data[i]=num8;
				}else if(ary[i].trim().equals("9")){
					data[i]=num9;
				}
			}			
		}
		return data;
	}

}
