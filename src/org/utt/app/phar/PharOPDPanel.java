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
package org.utt.app.phar;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.opd.OPDFrame;
import org.utt.app.util.DateLabelFormatter;
import org.utt.app.util.PDFPrintPageHP;
import org.utt.app.util.Prop;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.Setup;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class PharOPDPanel extends WebPanel implements Observer {
    ObjectData oUserInfo;
    int width,height;
    
    WebPanel leftPanel,mainPanel,topLeftPanel,topMainPanel,midMainPanel,mid2,mid1,midPanel,pt_info,jPanel,bottomPanel,LeftSection;
    WebLabel Label_Date,Label_Info;
    WebRadioButton PtStatus1,PtStatus2,PtStatus3,PtStatus4,PtStatus5,Pr1,Pr2;
    WebScrollPane scrollPane,scrollPaneImg,scrollPaneFn;
    WebTable table,tableFn;
    WebButton ButtonRefresh;
    WebLabel Label_VN,Label_HN,Label_CID,Label_NAME,Label_Memo,Label_Extra,Label_Extra1,Label_IMG;
    WebTextField TextField_VN,TextField_HN,TextField_CID;
    WebTextArea textArea;
    Vector<Vector<String>> data,dataFn;
    String ptStatus="";
	ButtonGroup PtGroup = new ButtonGroup();
	ButtonGroup PG = new ButtonGroup();
	JDatePickerImpl picker;
	String day="",month="",dateSearch="";
	Vector<String> columnNames,columnNamesFn;
	String fn="";
	int xprint=10;
	WebSplitPane split;
	WebLabel labelImage;
    
    public PharOPDPanel(ObjectData oUserInfo,int w,int h) {
        this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
        split.setDividerLocation((width*2)/10);
        split.setOneTouchExpandable(true);
        
        leftPanel = new WebPanel();
		leftPanel.setPreferredSize(new Dimension((width*2)/10, height-175));
		leftPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		//add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		 
        split.setLeftComponent(leftPanel);
		
		topLeftPanel = new WebPanel();
		topLeftPanel.setLayout(null);
		topLeftPanel.setPreferredSize(new Dimension((width*2)/10, 90));
		leftPanel.add(topLeftPanel, BorderLayout.NORTH);
		
		Label_Date= new WebLabel("",WebLabel.CENTER);
		
		WebLabel LabelListdata = new WebLabel("รายชื่อผู้รับบริการ",WebLabel.CENTER);
		LabelListdata.setFont(new Font("Tahoma", Font.PLAIN, 13));
		LabelListdata.setBounds(80,60,100,20);
		topLeftPanel.add(LabelListdata);
		
		PtStatus1 = new WebRadioButton("All");
		PtStatus1.setBackground(Setup.getColor());
		PtStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus="";
			}
		});
		PtGroup.add(PtStatus1);
		PtStatus1.setSelected(true);
		PtStatus1.setBounds(5, 35, 50, 25);
		topLeftPanel.add(PtStatus1);
		
		PtStatus2 = new WebRadioButton("UCS");
		PtStatus2.setBackground(Setup.getColor());
		PtStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus=" and vnpres.rightcode not in('2009','2010','2019') and  vnpres.rightcode not like '6%' ";
			}
		});
		PtGroup.add(PtStatus2);
		//PtStatus2.setSelected(true);
		PtStatus2.setBounds(55, 35, 50, 25);
		topLeftPanel.add(PtStatus2);
		
		PtStatus3 = new WebRadioButton("OFC");
		PtStatus3.setBackground(Setup.getColor());
		PtStatus3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus="  and vnpres.rightcode in ('2009','2010') ";
				 
			}
		});
		PtGroup.add(PtStatus3);
		 
		PtStatus3.setBounds(105, 35, 50, 25);
		topLeftPanel.add(PtStatus3);
		
		PtStatus4 = new WebRadioButton("LGO");
		PtStatus4.setBackground(Setup.getColor());
		PtStatus4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus="  and vnpres.rightcode = '2019' ";
			}
		});
		PtGroup.add(PtStatus4);
		//PtStatus4.setSelected(true);
		PtStatus4.setBounds(160, 35, 50, 25);
		topLeftPanel.add(PtStatus4);
		
		PtStatus5 = new WebRadioButton("SSS");
		PtStatus5.setBackground(Setup.getColor());
		PtStatus5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus=" and vnpres.rightcode like '6%' ";
			}
		});
		PtGroup.add(PtStatus5);
		PtStatus5.setBounds(210, 35, 50, 25);
		topLeftPanel.add(PtStatus5);
		
		mainPanel = new WebPanel();
		mainPanel.setPreferredSize(new Dimension(width-(width*2)/10, height-175));
		mainPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		 
		mainPanel.setLayout(new BorderLayout(0, 0));
		split.setRightComponent(mainPanel);
		
		topMainPanel = new WebPanel();
		topMainPanel.setLayout(new BorderLayout(0, 0));
		topMainPanel.setBackground(Setup.getColor());
		topMainPanel.setPreferredSize(new Dimension(width-((width*2)/10), 140));
		mainPanel.add(topMainPanel, BorderLayout.NORTH);
		
		midPanel = new WebPanel();
		midPanel.setLayout(new BorderLayout(0, 0));
		midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-320));
		mainPanel.add(midPanel, BorderLayout.CENTER);
		
		pt_info = new WebPanel();		 
		pt_info.setLayout(null);
		pt_info.setBackground(Setup.getColor());
		topMainPanel.add(pt_info, BorderLayout.CENTER);
		Locale.setDefault(new Locale("th", "TH"));
		WebLabel Label_Date = new WebLabel("วันที่");
		Label_Date.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Date.setBounds(5,12,30,20);
		pt_info.add(Label_Date);
		
		UtilDateModel model = new UtilDateModel();
		model.setSelected(true);
		Properties p = new Properties();
		p.put("text.today", "");
		p.put("text.month", "");
		p.put("text.year", "");
		JDatePanelImpl datePanel = new JDatePanelImpl(model,p);
		picker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		picker.setBackground(Setup.getColor());
		picker.getJFormattedTextField().setBackground(Setup.getColor());
		picker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(picker.getModel().getDay()<10){
					day="0"+picker.getModel().getDay();
				}else{
					day=""+picker.getModel().getDay();
				}
				if(picker.getModel().getMonth()+1<10){
					month="0"+(picker.getModel().getMonth()+1);
				}else{
					month=""+(picker.getModel().getMonth()+1);
				}
				dateSearch=picker.getModel().getYear()+"-"+month+"-"+day;
				oUserInfo.setPtVisitdate(dateSearch.trim());
				getClearData();
			}
		});
		picker.setShowYearButtons(true);
		picker.setTextEditable(false);
		 
		jPanel = new WebPanel();
		jPanel.setBounds(30, 4, 220, 30);
		jPanel.setBackground(Setup.getColor());
		jPanel.add((JComponent)picker);
		pt_info.add(jPanel);
		
		if(picker.getModel().getDay()<10){
			day="0"+picker.getModel().getDay();
		}else{
			day=""+picker.getModel().getDay();
		}
		if(picker.getModel().getMonth()+1<10){
			month="0"+(picker.getModel().getMonth()+1);
		}else{
			month=""+(picker.getModel().getMonth()+1);
		}			
		dateSearch=picker.getModel().getYear()+"-"+month+"-"+day;
		oUserInfo.setPtVisitdate(dateSearch.trim());
		Label_VN = new WebLabel("VN");
		Label_VN.setFont(new Font("Tahoma", Font.PLAIN, 13));
		pt_info.add(Label_VN);
		Label_VN.setBounds(270, 12, 20, 14);
		
		TextField_VN = new WebTextField();
		//TextField_VN.setEditable(false);
		pt_info.add(TextField_VN);
		TextField_VN.setBounds(290, 10, 80, 20);
		TextField_VN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Label_IMG.setIcon(null);
				//clearValue();
				if(TextField_VN.getText().trim().length()==2){
					oUserInfo.setPtVN("0"+TextField_VN.getText());
				}else if(TextField_VN.getText().trim().length()==1){
					oUserInfo.setPtVN("00"+TextField_VN.getText());
					
				}else{
					oUserInfo.setPtVN(TextField_VN.getText().trim());
				}
				
				oUserInfo.setPtHN("");
				oUserInfo.setPtCID("");
				
				oUserInfo.setPtName("");
				oUserInfo.setRightCode("");        
		        oUserInfo.setPtCliniccode("");         
		        oUserInfo.setPtAge("");
		        oUserInfo.setPtLabel("");              
		        oUserInfo.setMemo("");
		        oUserInfo.setMemo1("");
				searchPtvn(oUserInfo.GetPtVN());
				Label_IMG.setIcon(null);
				getClearData();
				
				String date_in=oUserInfo.GetPtVisitdate().trim();
		        String y=date_in.substring(0, 4);
		        String m=date_in.substring(5,7);
		        String d=date_in.substring(8);
 
		        //////////////////////////////////////
		        
		    	String res=getFn(oUserInfo.GetPtHN(),y+m+d).trim();
		    	if(res.substring(0, res.indexOf(":")).equals("1")){
		    		showFilePDF(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
		    		getDataFn(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
		    	}
		    	else if(res.substring(0, res.indexOf(":")).equals("0")){
					WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+oUserInfo.GetPtHN(),"File Error",WebOptionPane.ERROR_MESSAGE);
					
		    	}
		    	else{
		    		String text=res.substring(res.indexOf(":")+1);
		    		String[] parts = text.split("-");
		    		getDataFnClear();
		    		getDataFn(parts);

		    		//String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกไฟล์", "FN", WebOptionPane.QUESTION_MESSAGE,
		    	    //    null, parts, "");
		    		//showFilePDF(selected);
		    	}
		    	
		    	
		    	//
		    	if(oUserInfo.GetPtVN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
			}
		});
		
		Label_HN = new WebLabel("HN");
		Label_HN.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_HN.setBounds(390, 12, 20, 14);
		pt_info.add(Label_HN);
		
		TextField_HN = new WebTextField();
		TextField_HN.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TextField_HN.setText("");
		TextField_HN.setBounds(410, 10, 80, 20);
		pt_info.add(TextField_HN);
		TextField_HN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Label_IMG.setIcon(null);
				if(!Setup.checkEmptyTextField(TextField_HN) && TextField_HN.getText().trim().length()==7){					
					oUserInfo.setPtHN(TextField_HN.getText().trim());
				}
				 
				
				oUserInfo.setPtVN("");
				oUserInfo.setPtCID("");
				
				oUserInfo.setPtName("");
				oUserInfo.setRightCode("");        
		        oUserInfo.setPtCliniccode("");         
		        oUserInfo.setPtAge("");
		        oUserInfo.setPtLabel("");              
		        oUserInfo.setMemo("");
		        oUserInfo.setMemo1("");
				searchPthn(oUserInfo.GetPtHN());
				Label_IMG.setIcon(null);
				getClearData();
				
				String date_in=oUserInfo.GetPtVisitdate().trim();
		        String y=date_in.substring(0, 4);
		        String m=date_in.substring(5,7);
		        String d=date_in.substring(8);


		    	String res=getFn(oUserInfo.GetPtHN(),y+m+d).trim();
		    	if(res.substring(0, res.indexOf(":")).equals("1")){
		    		getDataFnClear();
		    		showFilePDF(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
		    		getDataFn(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
		    	}
		    	else if(res.substring(0, res.indexOf(":")).equals("0")){
					WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+oUserInfo.GetPtHN(),"File Error",WebOptionPane.ERROR_MESSAGE);
					
		    	}
		    	else{
		    		String text=res.substring(res.indexOf(":")+1);
		    		String[] parts = text.split("-");
		    		getDataFnClear();
		    		getDataFn(parts);
		    		//String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกไฟล์", "FN", WebOptionPane.QUESTION_MESSAGE,
		    	    //    null, parts, "");
		    		//showFilePDF(selected);
		    	}
		    	if(oUserInfo.GetPtVN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
			}
		});
		
		Label_CID = new WebLabel("ID");
		Label_CID.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_CID.setBounds(510, 12, 20, 14);
		pt_info.add(Label_CID);
		
		TextField_CID = new WebTextField();
		TextField_CID.setEditable(false);
		 
		 
		TextField_CID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TextField_CID.setBounds(530, 10, 150, 20);
		pt_info.add(TextField_CID);
		TextField_CID.setColumns(10);
		
		Label_NAME = new WebLabel(" ชื่อ  ");
		Label_NAME.setFont(new Font("Tahoma", Font.PLAIN, 14));
		Label_NAME.setBounds(4, 30, (width-((width*2)/10)-40), 30);
		pt_info.add(Label_NAME);
		
		textArea = new WebTextArea();
		textArea.setEditable(false);
		textArea.setForeground(new Color(255, 0, 0));
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textArea.setBackground(Setup.getColor());
		textArea.setBounds(70, 70, 650, 40);
		pt_info.add(textArea);
		
		Label_Memo = new WebLabel("การแพ้ยา ");
		Label_Memo.setForeground(new Color(255, 0, 0));
		Label_Memo.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Memo.setBounds(10, 65, 55, 30);
		pt_info.add(Label_Memo);
		
		Label_Extra = new WebLabel("ข้อมูลเพิ่มเติม");
		Label_Extra.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Setup.SetUnderline(Label_Extra);
		Label_Extra.setBounds(10, 110, 150, 30);
		pt_info.add(Label_Extra);
		
		Label_Extra1 = new WebLabel("");
		Label_Extra1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Setup.SetUnderline(Label_Extra1);
		Label_Extra1.setBounds(150, 110, 400, 30);
		pt_info.add(Label_Extra1);
		
		labelImage = new ScaledImageLabel();
		labelImage.setPreferredSize(new Dimension(140, 140));
		//labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
		labelImage.setBounds(width-(((width*2)/10)+200), 40, 100, 100);
		pt_info.add(labelImage);
		if(oUserInfo.GetPtVN().equals("")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
		}else {
			//System.out.println(oUserInfo.GetPtHN());
			//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
		} 
		setLeftData();
		setMainData();
		//add(mainPanel, BorderLayout.CENTER);
		
		add(split, BorderLayout.CENTER);

    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
        Label_Date.setText("วันที่ "+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()));
		TextField_HN.setText(oUserInfo.GetPtHN());
		TextField_CID.setText(oUserInfo.GetPtCID());
		TextField_VN.setText(oUserInfo.GetPtVN());
		Label_NAME.setText(oUserInfo.GetPtLabel());
		
		textArea.setText(oUserInfo.GetMemo());
		Label_Extra1.setText(oUserInfo.GetMemo1());
    }
    public void setLeftData(){
		 
		Label_Date.setText("วันที่ "+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()));
		Label_Date.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Label_Date.setBounds(5,5,(width*2)/10,30);
		topLeftPanel.add(Label_Date);
		
		columnNames = new Vector<String>();
  		columnNames.add("ที่");
		columnNames.add("  VN ");
		columnNames.add("   รายชื่อผู้รับบริการ  ");
		for (int n = 3; n < 10; n++) {	
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
				WebLabel jc = (WebLabel) c;
				
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);
					 
					String type = (String)getModel().getValueAt(modelRow, 5);
					 
					String status=type.trim();					  
					if ("1".equals(status)){
						c.setBackground(new Color(206,203,208));					 
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
		        
		        clearValue();
		        Label_IMG.setIcon(null);
		        String select_vn=table.getValueAt(row,1).toString().trim();
		        oUserInfo.setPtVN(select_vn);
		        String name=table.getValueAt(row,2).toString().trim();
		        oUserInfo.setPtName(name);
		        String hn=table.getValueAt(row,3).toString().trim();
		        oUserInfo.setPtHN(hn);
		        String clinic_pt=table.getValueAt(row,4).toString().trim();
		        oUserInfo.setPtCliniccode(clinic_pt);
		        
		        searchVN(select_vn,hn,clinic_pt);
		        String inscl="";
                if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                	
                }else {
                	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                }
                
		        oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");

		        String date_in=oUserInfo.GetPtVisitdate().trim();
		        String y=date_in.substring(0, 4);
		        String m=date_in.substring(5,7);
		        String d=date_in.substring(8);

		    	String res=getFn(hn,y+m+d).trim();
		    	if(res.substring(0, res.indexOf(":")).equals("1")){
		    		showFilePDF(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
		    		getDataFn(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
		    	}
		    	else if(res.substring(0, res.indexOf(":")).equals("0")){
					WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn,"File Error",WebOptionPane.ERROR_MESSAGE);
					
		    	}
		    	else{
		    		String text=res.substring(res.indexOf(":")+1);
		    		String[] parts = text.split("-");
		    		getDataFn(parts);
		    		//String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกไฟล์", "FN", WebOptionPane.QUESTION_MESSAGE,
		    	    //    null, parts, "");
		    		//showFilePDF(selected);

		    	}
		    	if(oUserInfo.GetPtVN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
			}
		});
		table.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table.setRowHeight(25);		
		table.setFillsViewportHeight(true);
 
		scrollPane = new WebScrollPane(table);
		getClearData();
		
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		
		bottomPanel = new WebPanel();
		leftPanel.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(null);
		 
		bottomPanel.setPreferredSize(new Dimension((width*2)/10, 30));
		ButtonRefresh = new WebButton("Refresh");
		ButtonRefresh.setForeground(UIManager.getColor("Button.darkShadow"));

		ButtonRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Label_Date.setText("วันที่ "+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()));
				clearValue();
				getData();
				 
			}
		});
		ButtonRefresh.setBounds((((width*2)/10)/2-45), 2, 90, 25);
		bottomPanel.add(ButtonRefresh);
	}
    public void setMainData(){
		mid2 = new WebPanel();
		mid2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mid2.setPreferredSize(new Dimension(width-((width*2)/10-480), height-215));		 
		mid2.setLayout(new BorderLayout(0, 0));
		
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(200, height-175));
		mid2.add(LeftSection, BorderLayout.WEST);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		//
		columnNamesFn = new Vector<String>();
  		columnNamesFn.add("ที่");
		columnNamesFn.add("   ");
		columnNamesFn.add("   ");
		dataFn = new Vector<Vector<String>>();
		 
		DefaultTableModel modelFn = new DefaultTableModel(dataFn, columnNamesFn){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		tableFn = new WebTable(modelFn){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);	
				
				return c;
			}
		};
	
		tableFn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tableFn.rowAtPoint(e.getPoint());
		        int col = tableFn.columnAtPoint(e.getPoint());
		        
		        //clearValue();
		        String fn=tableFn.getValueAt(row,2).toString().trim();
		        System.out.println(fn+"**********");
		        showFilePDF(fn);
		        
			}
		});
		tableFn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tableFn.setRowHeight(25);		
		tableFn.setFillsViewportHeight(true);
 
		scrollPaneFn = new WebScrollPane(tableFn);
		//getDataFn();
		LeftSection.add(scrollPaneFn, BorderLayout.CENTER);
		
		Label_IMG = new WebLabel("");
		Label_IMG.setHorizontalAlignment(SwingConstants.CENTER);

		scrollPaneImg = new WebScrollPane(Label_IMG);
		mid2.add(scrollPaneImg, BorderLayout.CENTER);
		midPanel.add(mid2, BorderLayout.CENTER);
		
		mid1 = new WebPanel();
		mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
		midPanel.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		Label_Info = new WebLabel(" ");
		Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info.setBounds(0, 18, 300, 15);
		
		Pr1 = new WebRadioButton("fujitsu");
		Pr1.setBackground(Setup.getColor());
		Pr1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 xprint=10;
			}
		});
		PG.add(Pr1);
		Pr1.setBounds(100, 12, 100, 25);
		mid1.add(Pr1);
		
		Pr2 = new WebRadioButton("HP");
		Pr2.setBackground(Setup.getColor());
		Pr2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 xprint=120;
			}
		});
		PG.add(Pr2);
		Pr2.setBounds(200, 12, 50, 25);
		mid1.add(Pr2);
		
		
		WebButton ButtonPrint = new WebButton("Print");
		ButtonPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				printOPD(fn);
			}
		});
		ButtonPrint.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/printer.png")));
		ButtonPrint.setBounds(300, 12, 100, 25);
		mid1.add(ButtonPrint);
	}
    public void getData(){
		table.setModel(fetchDataOPD());
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(40 );
		columnModel.getColumn(2).setPreferredWidth((width*2)/10-80);
		
		for (int n = 2; n < 9; n++) {	
			columnModel.getColumn(n+1).setPreferredWidth(0);
			columnModel.getColumn(n+1).setMinWidth(0);
			columnModel.getColumn(n+1).setMaxWidth(0);
		}
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
    public  DefaultTableModel fetchDataOPD(){
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and vnpres.clinic='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length==0){
			sql_user="";
		}
		else if(parts.length>1){
			sql_user1="and vnpres.clinic in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
			 
		}

		Connection conn;
		PreparedStatement stmt,stmt1;
		ResultSet rs,rs1;
		String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?   "+ptStatus+"   order by case IsNumeric(vnmst.vn)  when 1 then Replicate('0', 100 - Len(vnmst.vn)) + vnmst.vn else vnmst.vn end "; 
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		try {
			conn=new DBmanager().getConnMSSql();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())); 
			rs = stmt.executeQuery();
			int p=1;
			while (rs.next()) {
			
				String clinic_pt="";
				if(rs.getString(4)!=null){
					clinic_pt=rs.getString(4);
				}
				String pt_status="0";
				if(rs.getString(3)!=null){
					pt_status="1";
				}
				final Vector<String> vstring = new Vector<String>();
				vstring.add(" "+Integer.toString(p) );
				vstring.add(rs.getString(1));
				vstring.add(Setup.getName(rs.getString(2).trim()));
				vstring.add(rs.getString(2).trim());
				vstring.add(clinic_pt);
				vstring.add(pt_status);
				 
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
  
    public  void showFilePDF(String filename_in){
		String filename="";
		if(filename_in.indexOf("]")>0) {
			filename=filename_in.trim().substring(filename_in.indexOf("]")+1);
			fn=filename;
		}else {
			filename=filename_in.trim();
			fn=filename;
		}

		if(filename.length()==21 || filename.length()==25){
			ImageIcon icon=null;			
			icon = new ImageIcon(previewPDFDocumentInImage(filename));
			Label_IMG.setIcon(icon);
		}else{
			WebOptionPane.showMessageDialog(null,"filename error:"+filename ,"Infomation",WebOptionPane.WARNING_MESSAGE); 			
			Label_Info.setText("");
			clearValue();
			Label_IMG.setIcon(null);
		}
		TextField_VN.requestFocusInWindow();
		mid2.repaint();
		mid2.revalidate();
	}
	public  Image previewPDFDocumentInImage(String filescanname){
		 
		Image img=null;
		ByteBuffer buf = null;
		String hn=filescanname.substring(filescanname.length()-7).trim();
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;

		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		FileSystem fs;
		PDFFile pdffile;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path newFolderPath= new Path(path1);
			 Path path = new Path(newFolderPath+"/"+filescanname+"HN.pdf");
			 
			    if (!fs.exists(path)) {
			 
			}
			    
			FSDataInputStream in = fs.open(path);
			byte[] b= IOUtils.toByteArray(in);
			buf = ByteBuffer.wrap(b);
			
			pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(1);
			//get the width and height for the doc at the default zoom
	    	Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
	        img = page.getImage(800, 1200, //width &amp; height
	                rect, // clip rect
	                null, // null for the ImageObserver
	                true, // fill background with white
	                true) // block until drawing is done
	        ;
			fs.close(); 
		} catch (IOException e) {
			//WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn,"File Error",WebOptionPane.ERROR_MESSAGE);			
			img=null;
		}
		return img;
    	 

 	}

	public void printOPD(String fn){
		ByteBuffer buf = null;
		String hn=fn.substring(fn.length()-7).trim();
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		FileSystem fs;
		
		
		PDFFile pdffile;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path newFolderPath= new Path(path1);
			 Path path = new Path(newFolderPath+"/"+fn+"HN.pdf");
			    if (!fs.exists(path)) {
			      System.out.println("File " +newFolderPath+"/"+fn+"HN.pdf" + " does not exists");
			      return;
			}
			FSDataInputStream in = fs.open(path);
			byte[] b= IOUtils.toByteArray(in);
			buf = ByteBuffer.wrap(b);
		         
		        
			pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(1);
			
			PrinterJob pjob = PrinterJob.getPrinterJob(); 
	    	Book book = new Book();
	    	Paper paper;
	    	PageFormat pf;
	    	pf = new PageFormat();
			paper = new Paper();
			
			paper.setSize(12.0*72,25.0*72);
			paper.setImageableArea(xprint,10 ,paper.getHeight()-10,paper.getWidth() -10);
		
			pf.setPaper(paper);
	    	 
	        PDFPrintPageHP pages = new PDFPrintPageHP(pdffile);
	        book.append(pages, pf, pdffile.getNumPages());
	    
	        pjob.setPageable(book);
	        
	       new PrintThread(pages, pjob).start();

	       fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	class PrintThread extends Thread {

        PDFPrintPageHP ptPages;
        PrinterJob ptPjob;

        public PrintThread(PDFPrintPageHP pages, PrinterJob pjob) {
            ptPages = pages;
            ptPjob = pjob;
            setName(getClass().getName());
        }
        public void run() {
            try {
                ptPages.show(ptPjob);
                ptPjob.print();
            } catch (PrinterException pe) {          
            }
            ptPages.hide();
        }
    }
	public String getFn(String hn_in,String fndate){
		String fn="";
		int p=0;
		String folderhn=hn_in.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn_in;
		 
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		FileSystem fs;
		 
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path newFolderPath= new Path(path1);
			FileStatus[] fileStatus = fs.listStatus(newFolderPath);
			int num=1;
			for(FileStatus status : fileStatus){
				String pp=status.getPath().toString().trim();
				//System.out.println(pp+"*****");
    	        if(pp.length()==72) {
    	        	String ppp=pp.substring( pp.length()-31,pp.length()-23);
    	        	if(ppp.equals("25000101")) {	 
    	        	}
    	        	else{
    	        		String pp1=pp.substring(pp.length()-31,pp.length()-6);
    	        		if(pp1.substring(0,8).equals(fndate.trim())) {
    	        			fn+="["+pp1.substring(8,12)+" ("+pp1.substring(12,14)+")]"+pp1+"-";
    	        			p++;
    	        		}
            		 
    	    	         
    	        	}    		
    	        	 
    	        }
    	        //
    	        else if(pp.length()==68) {
    	        	String ppp=pp.substring( pp.length()-27,pp.length()-19);
    	        	if(ppp.equals("25000101")) {
    	        		 
    	        	}
    	        	else{
    	        		String pp1=pp.substring(pp.length()-27,pp.length()-6);
    	        		if(pp1.substring(0,8).equals(fndate.trim())) {
    	        			fn+="["+pp1.substring(8,12)+" ("+pp1.substring(12,14)+")]"+pp1+"-";
    	        			p++;
    	        		}
    	    	         
    	        	}    		
    	        	 
    	        }
    	    }
				
			fs.close();
		} catch (IOException e) {
			WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn_in,"File Error",WebOptionPane.ERROR_MESSAGE);
		}
	
		return p+":"+fn;
	}
	public void getClearData(){
		table.setModel(fetchDataOPDClear());
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(40 );
		columnModel.getColumn(2).setPreferredWidth((width*2)/10-80);
		
		for (int n = 2; n < 9; n++) {	
			columnModel.getColumn(n+1).setPreferredWidth(0);
			columnModel.getColumn(n+1).setMinWidth(0);
			columnModel.getColumn(n+1).setMaxWidth(0);
		}
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataOPDClear(){
		return new DefaultTableModel(data, columnNames);
	}
	public void clearValue(){
		 
        oUserInfo.setPtVN("");  
        oUserInfo.setPtName("");        
        oUserInfo.setPtHN("");        
        oUserInfo.setRightCode("");        
        oUserInfo.setPtCliniccode("");         
        oUserInfo.setPtAge("");
        oUserInfo.setPtLabel("");       
        oUserInfo.setPtCID("");         
        oUserInfo.setMemo("");
        oUserInfo.setMemo1("");
        Label_IMG.setIcon(null);
        fn="";
        getDataFnClear();

         
	}
	public void searchPtvn(String vn) {
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and vnpres.clinic='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length==0){
			sql_user="";
		}
		else if(parts.length>1){
			sql_user1="and vnpres.clinic in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
			 
		}
		if(!Setup.checkEmptyTextField(TextField_VN) && TextField_VN.getText().trim().length()<6){
			int p=0;
			Connection conn;
			PreparedStatement stmt,stmt1;
			ResultSet rs,rs1;
			String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnpres.vn=?  "; 
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			try {
				conn=new DBmanager().getConnMSSql();
				stmt = conn.prepareStatement(query);
				stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())); 
				stmt.setString(2, vn.trim());
				rs = stmt.executeQuery();
				 
				while (rs.next()) {
					oUserInfo.setPtHN(rs.getString(2).trim());
					String res=getClinic(vn,Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())).trim();
					//System.out.println(res+"************-----****");
			    	if(res.substring(0, res.indexOf(":")).equals("1")){
			    		searchVN(vn,(rs.getString(2)).trim(),res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    		oUserInfo.setPtCliniccode(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    	}
			    	else if(res.substring(0, res.indexOf(":")).equals("0")){
			    		
			    	}
			    	else{
			    	
			    		String text=res.substring(res.indexOf(":")+1);
			    		String[] parts1 = text.split("-");
			    		for(int i=0;i<parts1.length;i++) {
			    			System.out.println(parts1[i]+"-----****");
			    		}		    	
			    		 

			    		String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกห้องตรวจ", "Clinic", WebOptionPane.QUESTION_MESSAGE,
			    	        null, parts1, "");
			    		searchVN(vn,(rs.getString(2)).trim(),selected);
			    		oUserInfo.setPtCliniccode(selected);

			    	}
			    	String name=Setup.getName(rs.getString(2).trim());
			    	oUserInfo.setPtName(name);

				    String inscl="";
				    if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                    	
                    }else {
                    	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    }
				    oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
				    p++;  
				}
			
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(p==0) {
				clearValue();
				WebOptionPane.showMessageDialog(null,"ไม่พบ vn ในห้องตรวจนี้","VN Error",WebOptionPane.ERROR_MESSAGE);
				
			}
		}
		else if(!Setup.checkEmptyTextField(TextField_VN) && TextField_VN.getText().trim().length()==21){
			String clinic_in=TextField_VN.getText().trim().substring(8,12);
			String hn_in=TextField_VN.getText().trim().substring(14);
			String visitdate_in=Setup.DateInDBMSSQLno(TextField_VN.getText().trim().substring(0,8));
			System.out.println(clinic_in+"-"+hn_in+"-"+visitdate_in);
			oUserInfo.setPtHN(hn_in);
			System.out.println("hn22222..."+oUserInfo.GetPtHN());
			
			int p=0;
			Connection conn;
			PreparedStatement stmt,stmt1;
			ResultSet rs,rs1;
			String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnmst.hn=?  "; 
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			try {
				conn=new DBmanager().getConnMSSql();
				stmt = conn.prepareStatement(query);
				stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())); 
				stmt.setString(2,hn_in.trim());
				rs = stmt.executeQuery();
				 
				while (rs.next()) {
					oUserInfo.setPtVN(rs.getString(1).trim());
					String res=getClinic(rs.getString(1).trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())).trim();
			    	if(res.substring(0, res.indexOf(":")).equals("1")){
			    		searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    		oUserInfo.setPtCliniccode(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    	}
			    	else if(res.substring(0, res.indexOf(":")).equals("0")){
			    		
			    	}
			    	else{
			    		String text=res.substring(res.indexOf(":")+1);
			    		String[] parts1 = text.split("-");
 
			    		String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกห้องตรวจ", "Clinic", WebOptionPane.QUESTION_MESSAGE,
			    	        null, parts1, "");
			    		searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),selected);
			    		oUserInfo.setPtCliniccode(selected);

			    	}
			    	String name=Setup.getName(rs.getString(2).trim());
			    	oUserInfo.setPtName(name);

				    String inscl="";
				    if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                    	
                    }else {
                    	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    }
				    oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
				    p++;  
				}
			
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(p==0) {
				clearValue();
				WebOptionPane.showMessageDialog(null,"ไม่พบ vn ในห้องตรวจนี้","VN Error",WebOptionPane.ERROR_MESSAGE);
				
			}
		}
		else if(!Setup.checkEmptyTextField(TextField_VN) && TextField_VN.getText().trim().length()==25){
			String clinic_in=TextField_VN.getText().trim().substring(8,12);
			String hn_in=TextField_VN.getText().trim().substring(18);
			String visitdate_in=Setup.DateInDBMSSQLno(TextField_VN.getText().trim().substring(0,8));
			
			oUserInfo.setPtHN(hn_in);
			
			int p=0;
			Connection conn;
			PreparedStatement stmt,stmt1;
			ResultSet rs,rs1;
			String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnmat.hn=?  and vnpres.clinic=? "; 
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			try {
				conn=new DBmanager().getConnMSSql();
				stmt = conn.prepareStatement(query);
				stmt.setString(1, visitdate_in); 
				stmt.setString(2,hn_in.trim());
				stmt.setString(3,clinic_in.trim());
				rs = stmt.executeQuery();
				 
				while (rs.next()) {
					oUserInfo.setPtVN(rs.getString(1).trim());
					String res=getClinic(rs.getString(1).trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())).trim();
			    	//System.out.println("res  --- "+res+"--"+res.substring(0, res.indexOf(":")));
			    	if(res.substring(0, res.indexOf(":")).equals("1")){
			    		searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    		oUserInfo.setPtCliniccode(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    	}
			    	else if(res.substring(0, res.indexOf(":")).equals("0")){
			    		
			    	}
			    	else{
			    		String text=res.substring(res.indexOf(":")+1);
			    		String[] parts1 = text.split("-");


			    		String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกห้องตรวจ", "Clinic", WebOptionPane.QUESTION_MESSAGE,
			    	        null, parts1, "");
			    		searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),selected);
			    		oUserInfo.setPtCliniccode(selected);
			    	}
			    	String name=Setup.getName(rs.getString(2).trim());
			    	oUserInfo.setPtName(name);

				    String inscl="";
				    if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                    	
                    }else {
                    	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    }
				    oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
				    p++;  
				}
			
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(p==0) {
				clearValue();
				WebOptionPane.showMessageDialog(null,"ไม่พบ vn ในห้องตรวจนี้","VN Error",WebOptionPane.ERROR_MESSAGE);
				
			}
		}
		
	}
	public void searchPthn(String hn_in){
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and vnpres.clinic='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length==0){
			sql_user="";
		}
		else if(parts.length>1){
			sql_user1="and vnpres.clinic in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
			 
		}
		if(!Setup.checkEmptyTextField(TextField_HN) && TextField_HN.getText().trim().length()==7){
			
			int p=0;
			Connection conn;
			PreparedStatement stmt,stmt1;
			ResultSet rs,rs1;
			String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnmst.hn=? "; 
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			try {
				conn=new DBmanager().getConnMSSql();
				stmt = conn.prepareStatement(query);
				stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())); 
				stmt.setString(2, hn_in.trim());
				rs = stmt.executeQuery();
				 
				while (rs.next()) {
					oUserInfo.setPtVN(rs.getString(1).trim());
					String res=getClinic(rs.getString(1).trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())).trim();
			    	if(res.substring(0, res.indexOf(":")).equals("1")){
			    		searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    		oUserInfo.setPtCliniccode(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
			    	}
			    	else if(res.substring(0, res.indexOf(":")).equals("0")){
			    		
			    	}
			    	else{
			    		String text=res.substring(res.indexOf(":")+1);
			    		String[] parts1 = text.split("-");

			    		String selected=(String)WebOptionPane.showInputDialog(null, "กรุณาเลือกห้องตรวจ", "Clinic", WebOptionPane.QUESTION_MESSAGE,
			    	        null, parts1, "");
			    		searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),selected);
			    		oUserInfo.setPtCliniccode(selected);
			    	}
			    	String name=Setup.getName(rs.getString(2).trim());
			    	oUserInfo.setPtName(name);

				    String inscl="";
				    if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                    	
                    }else {
                    	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    }
				    oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
				    p++;  
				}
			
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(p==0) {
				clearValue();
				WebOptionPane.showMessageDialog(null,"ไม่พบ vn ในห้องตรวจนี้","VN Error",WebOptionPane.ERROR_MESSAGE);
				
			}
		}
	}
	public void searchVN(String vn,String hn,String clinic){
		Connection conn;
		PreparedStatement stmt,stmt1,stmt2,stmt3;
		ResultSet rs,rs1,rs2,rs3;
		
		String query_db="select  BirthDateTime from patient_info where hn='"+hn.trim()+"'" ; 
		String query_right="select  rightcode from vnpres where visitdate=? and clinic='"+clinic+"'   and vn='"+vn.trim()+"'" ; 
		String query_ref="select  ref from patient_ref where  reftype='01'  and hn='"+hn.trim()+"'" ; 
		
		conn=new DBmanager().getConnMSSql();
		 
		try {
			stmt = conn.prepareStatement(query_db);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String  right="";
				if(rs.getString(1) !=null){
					Date birthday=rs.getDate(1);
					String age_pt=Setup.AgeInAll(birthday.toString()).trim();
					oUserInfo.setPtAge(age_pt);
				}		
				
			}
			stmt.close();
			//
			stmt1 = conn.prepareStatement(query_right);
			stmt1.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
			rs1 = stmt1.executeQuery();
			while (rs1.next()) {
				String  right="";				
				if(rs1.getString(1) !=null){
					for(int i=0;i<PharFrame.rightname.length;i++){
						if(rs1.getString(1).trim().equals(PharFrame.rightname[i][0])){
							right=PharFrame.rightname[i][1];
							break;
						}
					}
					oUserInfo.setRightCode(right+"("+rs1.getString(1).trim()+")");
				}
							
			}
			stmt1.close();
			//
			stmt2 = conn.prepareStatement(query_ref);
			rs2 = stmt2.executeQuery();
			while (rs2.next()) {			
				if(rs2.getString(1) !=null){
					oUserInfo.setPtCID(rs2.getString(1).trim());
				}
							
			}
			stmt2.close();
			//
			String sql_allergy="select memo from view_MEDICINE_ALLERGIC where hn=? and suffix='8'";
			stmt3 = conn.prepareStatement(sql_allergy);
			stmt3.setString(1, hn);
			rs3 = stmt3.executeQuery();
			String memo1="",memo2="";
			while (rs3.next()) {
				if(rs3.getString(1)!=null){
					 memo1+=rs3.getString(1).trim()+"\n";
				}
			}
			rs3.close();
			stmt3.close();
			
			for(int i=0;i<PharFrame.hn_z515.length;i++){
				if(hn.equals(PharFrame.hn_z515[i])){
					memo2="Z515";
					break;
				}
			}
			//System.out.println(memo1+"----"+hn);
			 
			oUserInfo.setMemo(memo1);
			oUserInfo.setMemo1(memo2);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getClinic(String vn_in,String fndate){
		String clinic="";
		int p=0;
		try{
	    	String sql_clinic="select  clinic from vnpres where vn=? and visitdate= '"+fndate+"'  order by clinic";
			Connection conn=new DBmanager().getConnMSSql();
			PreparedStatement stmt = conn.prepareStatement(sql_clinic);
			stmt.setString(1, vn_in);
			 
			ResultSet rs = stmt.executeQuery();
			 
			while (rs.next()) {
				if(rs.getString(1)!=null){
					 
					clinic+=rs.getString(1).trim()+"-";
				}
				p++;
			}
	    	stmt.close();
	    	conn.close();
	    	 
	    } catch (SQLException e) {
			e.printStackTrace();
		}
		return p+":"+clinic;
	}
	public void getDataFnClear(){
		tableFn.setModel(fetchDataFnClear());
		TableColumnModel columnModelFn = tableFn.getColumnModel();
		columnModelFn.getColumn(0).setPreferredWidth(40);
		columnModelFn.getColumn(1).setPreferredWidth(160);
		columnModelFn.getColumn(2).setPreferredWidth(0);
		columnModelFn.getColumn(2).setMinWidth(0);
		columnModelFn.getColumn(2).setMaxWidth(0);

		((DefaultTableModel)tableFn.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataFnClear(){
		Vector<Vector<String>> dataFn = new Vector<Vector<String>>();
		return new DefaultTableModel(dataFn, columnNamesFn);
	}
	public void getDataFn(String [] fn){
		tableFn.setModel(fetchDataFn(fn));
		TableColumnModel columnModelFn = tableFn.getColumnModel();
		columnModelFn.getColumn(0).setPreferredWidth(40);
		columnModelFn.getColumn(1).setPreferredWidth(160);
		columnModelFn.getColumn(2).setPreferredWidth(0);
		columnModelFn.getColumn(2).setMinWidth(0);
		columnModelFn.getColumn(2).setMaxWidth(0);

		((DefaultTableModel)tableFn.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataFn(String [] fn){
		Vector<Vector<String>> dataFn = new Vector<Vector<String>>();
		
		 
		for(int i=0;i<fn.length;i++) {
			final Vector<String> vstringFn = new Vector<String>();
			vstringFn.add(" "+(i+1));
			vstringFn.add(fn[i].substring(0,fn[i].indexOf("]")+1)+""+msqDrug(fn[i].substring(0,fn[i].indexOf("]"))));
			vstringFn.add(fn[i]);
			dataFn.add(vstringFn);
		}
		 
		return new DefaultTableModel(dataFn, columnNamesFn);
	}
	public void getDataFn(String  fn){
		tableFn.setModel(fetchDataFn(fn));
		TableColumnModel columnModelFn = tableFn.getColumnModel();
		columnModelFn.getColumn(0).setPreferredWidth(40);
		columnModelFn.getColumn(1).setPreferredWidth(160);
		columnModelFn.getColumn(2).setPreferredWidth(0);
		columnModelFn.getColumn(2).setMinWidth(0);
		columnModelFn.getColumn(2).setMaxWidth(0);

		((DefaultTableModel)tableFn.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataFn(String  fn){
		Vector<Vector<String>> dataFn = new Vector<Vector<String>>();
		final Vector<String> vstringFn = new Vector<String>();
		vstringFn.add(" 1");
		vstringFn.add(fn.substring(0,fn.indexOf("]")+1)+""+msqDrug(fn.substring(0,fn.indexOf("]"))));
		vstringFn.add(fn);
		dataFn.add(vstringFn);
		return new DefaultTableModel(dataFn, columnNamesFn);
	}
	public String msqDrug(String from) {
		String to="";
		String in=from;
		String clinic=in.substring(in.indexOf("[")+1,in.indexOf("(")).trim();
		String page=in.substring(in.indexOf("(")+1,in.indexOf(")")).trim();
		String to1=clinic+page;
		String havedrug="";
		for(int i=0;i<OPDFrame.formdrug.length;i++){
            if(to1.equals(OPDFrame.formdrug[i][0])){
                havedrug=OPDFrame.formdrug[i][1];
                break;
            }
        }
		if(havedrug.equals("1")) {
			to=" มียา";
		}else {
			
		}
		return to;
	}
}
