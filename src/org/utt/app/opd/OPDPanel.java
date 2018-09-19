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
package org.utt.app.opd;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.sun.pdfview.PDFFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.json.JSONObject;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.dent.DentalFormPanel;
import org.utt.app.dent.DentalFrame;
import org.utt.app.hdfs.MainHDFSOPDPanel;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OPDPanel extends WebPanel implements Observer {
    ObjectData oUserInfo;
    int width,height;
    String day="",month="",dateSearch="";
    String ptStatus="";

    WebTabbedPane tabbedPane,tabbedPane_in;
    WebSplitPane split,splitR;
    WebPanel leftPanel,topMainPanel,midPanel,topMainLeftPanelopd,bottomPanelopd,topMainLeftPanelipd,bottomPanelipd;
    WebPanel topLeftPanel,pt_info,jPanel;
    WebLabel Label_Date_l,Label_VN,Label_HN,Label_CID,Label_NAME,labelImage,Label_Extra,Label_Extra1,Label_Memo ;
    WebTextField TextField_VN,TextField_HN,TextField_CID;
    WebTextArea textArea;
    WebButton ButtonRefreshopd,ButtonRefreshipd,ButtonSmartcard;
    WebTable tableopd,tableipd;
    WebScrollPane scrollPaneopd,scrollPaneipd,scrollPaneDrugA;
    WebAccordion accordion;
    WebComboBox cbWard;

    JDatePickerImpl picker;

    MainHDFSOPDPanel mainHDFSOPDPanel;
    ScanScope scanScope;
    OPDFormPanel opdFormPanel;
    LabOPDPanel labOPDPanel;
    
    Vector<String> columnNamesopd,columnNamesipd;
    Vector<Vector<String>> dataopd,dataipd;

    public OPDPanel(ObjectData oUserInfo, int w, int h) {
        this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
        split.setDividerLocation((width*2)/10);
        splitR = new WebSplitPane(split.VERTICAL_SPLIT);
        splitR.setDividerLocation(140);

        mainHDFSOPDPanel =new MainHDFSOPDPanel(oUserInfo,w,h);
        scanScope =new ScanScope(oUserInfo,w,h);
        opdFormPanel =new OPDFormPanel(oUserInfo,w,h);
        labOPDPanel =new LabOPDPanel(oUserInfo,w,h);

        setTop();
        setLeft();
        setRight();

        split.setRightComponent(splitR);
        split.setLeftComponent(leftPanel);
        split.setOneTouchExpandable(true);
        splitR.setTopComponent(topMainPanel);
        splitR.setBottomComponent(midPanel);

        tabbedPane = new WebTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);

        tabbedPane.addTab("<html>O<br>P<br>D<br> </html>", null, split, "OPD");
        add(tabbedPane, BorderLayout.CENTER);

    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
        Label_Date_l.setText(I18n.lang("label.date")+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()));
        TextField_HN.setText(oUserInfo.GetPtHN());
        TextField_CID.setText(oUserInfo.GetPtCID());
        if(oUserInfo.GetPtVN().equals("") && oUserInfo.GetPtAN().equals("")) {
            TextField_VN.setText(oUserInfo.GetPtAN());
            Label_VN.setText("VN ");
        }
        else if(oUserInfo.GetPtVN().equals("")){
            TextField_VN.setText(oUserInfo.GetPtAN());
            Label_VN.setText("AN ");
        }
        else {
            TextField_VN.setText(oUserInfo.GetPtVN());
            Label_VN.setText("VN ");
        }
        Label_NAME.setText(oUserInfo.GetPtLabel());
        textArea.setText(oUserInfo.GetMemo());
        Label_Extra1.setText(oUserInfo.GetMemo1());
    }
    public void setTop(){
        topMainPanel = new WebPanel();
        topMainPanel.setLayout(new BorderLayout(0, 0));
        topMainPanel.setPreferredSize(new Dimension(width-((width*2)/10), 140));
        topMainPanel.setBackground(Setup.getColor());
        pt_info = new WebPanel();
        pt_info.setLayout(null);
        pt_info.setBackground(Setup.getColor());
        topMainPanel.add(pt_info, BorderLayout.CENTER);
        Setup.setLocale();
        WebLabel Label_Date = Setup.getLabel(I18n.lang("label.date"), 13,0,SwingConstants.CENTER);
        Label_Date.setBounds(5,8,30,20);
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
                getDataOPDClear();
                clearValue();
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

        Label_VN = Setup.getLabel(I18n.lang("label.vn"), 13,0,SwingConstants.CENTER);
        Label_VN.setBounds(270, 12, 20, 14);
        pt_info.add(Label_VN);

        TextField_VN = new WebTextField();
        TextField_VN.setBounds(290, 10, 80, 20);
        pt_info.add(TextField_VN);
        TextField_VN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //clearValue();
                if(TextField_VN.getText().trim().length()==2){
                    oUserInfo.setPtVN("0"+TextField_VN.getText());
                }else if(TextField_VN.getText().trim().length()==1){
                    oUserInfo.setPtVN("00"+TextField_VN.getText());

                }else{
                    oUserInfo.setPtVN(TextField_VN.getText().trim());
                }

                //System.out.println("a  "+TextField_VN.getText().trim()+"--"+oUserInfo.GetPtVN());
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
                mainHDFSOPDPanel.clear();
                opdFormPanel.clearPanel();
                labOPDPanel.clearPanel();
                //labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
                if(oUserInfo.GetPtVN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
                opdFormPanel.getInOutDateTime(oUserInfo.GetPtHN());
            }
        });

        Label_HN = Setup.getLabel(I18n.lang("label.hn"), 13,0,SwingConstants.CENTER);
        Label_HN.setBounds(390, 12, 20, 14);
        pt_info.add(Label_HN);
        TextField_HN = new WebTextField();
        TextField_HN.setBounds(410, 10, 80, 20);
        pt_info.add(TextField_HN);
        TextField_HN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(!Setup.checkEmptyTextField(TextField_HN) && TextField_HN.getText().trim().length()==7){
                    oUserInfo.setPtHN(TextField_HN.getText().trim());
                    //System.out.println("bb  "+TextField_HN.getText().trim()+"--"+oUserInfo.GetPtHN());
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
                mainHDFSOPDPanel.clear();
                opdFormPanel.clearPanel();
                labOPDPanel.clearPanel();
                //labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
                if(oUserInfo.GetPtVN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
                opdFormPanel.getInOutDateTime(oUserInfo.GetPtHN());
            }
        });

        Label_CID = Setup.getLabel(I18n.lang("label.cid"), 13,0,SwingConstants.CENTER);
        Label_CID.setBounds(510, 12, 20, 14);
        pt_info.add(Label_CID);
        TextField_CID = new WebTextField();
        TextField_CID.setBounds(530, 10, 150, 20);
        pt_info.add(TextField_CID);

        Label_NAME = Setup.getLabel(I18n.lang("label.name"), 13,0,SwingConstants.LEFT);
        Label_NAME.setBounds(4, 30, 970, 30);
        pt_info.add(Label_NAME);

        textArea = new WebTextArea();
        textArea.setEditable(false);
        textArea.setForeground(new Color(255, 0, 0));
        textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
        textArea.setBackground(Setup.getColor());
        textArea.setBounds(70, 70, 500, 40);
        //pt_info.add(textArea);
        
        scrollPaneDrugA = new WebScrollPane(textArea);
		scrollPaneDrugA.setBounds(70, 70, 500, 40);
		pt_info.add(scrollPaneDrugA);

        Label_Memo = new WebLabel("การแพ้ยา ");
        Label_Memo.setForeground(new Color(255, 0, 0));
        Label_Memo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Label_Memo.setBackground(Setup.getColor());
        Label_Memo.setBounds(10, 65, 55, 30);
        pt_info.add(Label_Memo);

        Label_Extra = new WebLabel("ข้อมูลเพิ่มเติม");
        Label_Extra.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Setup.SetUnderline(Label_Extra);
        Label_Extra.setBounds(10, 110, 80, 30);
        Label_Extra.setBackground(Setup.getColor());
        pt_info.add(Label_Extra);

        Label_Extra1 = new WebLabel("");
        Label_Extra1.setFont(new Font("Tahoma", Font.PLAIN, 13));
        Setup.SetUnderline(Label_Extra1);
        Label_Extra1.setBounds(100, 110, 400, 30);
        Label_Extra1.setBackground(Setup.getColor());
        pt_info.add(Label_Extra1);

        labelImage = new ScaledImageLabel();
        labelImage.setPreferredSize(new Dimension(140, 140));
        //labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
        labelImage.setBounds(width-(((width*2)/10)+200), 40, 100, 100);
        pt_info.add(labelImage);
        
        if(oUserInfo.GetPtVN().equals("")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
		}else {
			System.out.println(oUserInfo.GetPtHN());
			labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
		} 
        
        ButtonSmartcard = new WebButton("อ่านข้อมูลจากบัตร");
        ButtonSmartcard.setBackground(Setup.getColor());
        ButtonSmartcard.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonSmartcard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                clearValue();
                getSmartcard();
                opdFormPanel.getInOutDateTime(oUserInfo.GetPtHN());
            }
        });
        ButtonSmartcard.setBounds(700, 8, 150, 25);
        pt_info.add(ButtonSmartcard);


    }
    public void setLeft(){
        leftPanel = new WebPanel();
        leftPanel.setPreferredSize(new Dimension((width*2)/10, height));
        leftPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        leftPanel.setLayout(new BorderLayout(0, 0));
        topLeftPanel = new WebPanel();
        topLeftPanel.setLayout(new BorderLayout(0, 0));
        topLeftPanel.setPreferredSize(new Dimension((width*2)/10, 30));
        leftPanel.add(topLeftPanel, BorderLayout.NORTH);

        Label_Date_l= Setup.getLabel(I18n.lang("label.date")+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()), 12,0,SwingConstants.CENTER);
        Label_Date_l.setPreferredSize(new Dimension((width*2)/10, 30));
        topLeftPanel.add(Label_Date_l, BorderLayout.NORTH);

        accordion = new WebAccordion( );
        accordion.addPane ( new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")), " OPD ", getOPD() );
        accordion.addPane ( new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")), " IPD", getIPD() );
        if(InApp.usertype.trim().equals("83") || InApp.usertype.trim().equals("86")) {
            accordion.addPane ( new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")), " Search", getSearch() );
        }
        accordion.setMultiplySelectionAllowed ( false );
        
        
        leftPanel.add(accordion, BorderLayout.CENTER);

    }
    public void setRight(){
        midPanel = new WebPanel();
        midPanel.setLayout(new BorderLayout(0, 0));
        midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-140));
        tabbedPane_in = new WebTabbedPane();
        midPanel.add(tabbedPane_in, BorderLayout.CENTER);
        tabbedPane_in.addTab("OPD FORM" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),opdFormPanel);
        tabbedPane_in.addTab("EMR",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,mainHDFSOPDPanel);
        if(InApp.usertype.trim().equals("83")) {
            tabbedPane_in.addTab("SCAN " ,new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")),scanScope);
        }
        
        //tabbedPane_in.addTab("SCAN OPD" ,new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")),scanOPDCardUser);
        //tabbedPane_in.addTab("Past Med" ,new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")),medOPDPanel);
        
        tabbedPane_in.addTab("LAB" ,new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")),labOPDPanel);
        if(InApp.usertype.trim().equals("87")) {
        	//tabbedPane_in.addTab("Queue" ,new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")),queueOPDPanel);
        }
    
    }
    public WebPanel getOPD(){
        clearValue();
        WebPanel wp = new WebPanel();
        wp.setLayout(new BorderLayout(0, 0));
        wp.setPreferredSize(new Dimension((width*2)/10, height-405));
        topMainLeftPanelopd = new WebPanel();
        wp.add(topMainLeftPanelopd, BorderLayout.NORTH);
        topMainLeftPanelopd.setBackground(Setup.getColor());
        topMainLeftPanelopd.setLayout(null);
        topMainLeftPanelopd.setPreferredSize(new Dimension((width*2)/10, 30));

        WebLabel LabelListdata = new WebLabel("รายชื่อผู้รับบริการ",JLabel.CENTER);
        LabelListdata.setFont(new Font("Tahoma", Font.PLAIN, 13));
        LabelListdata.setBounds(5,5,(width*2)/10,20);
        topMainLeftPanelopd.add(LabelListdata);

        if(InApp.usertype.equals("87")) {
        	columnNamesopd = new Vector<String>();
            columnNamesopd.add(I18n.lang("label.order"));            
            columnNamesopd.add(I18n.lang("label.vn"));
            columnNamesopd.add("Q");
            columnNamesopd.add(I18n.lang("label.customer.name"));
            for (int n = 4; n < 7; n++) {
                columnNamesopd.add(" ");
            }

            dataopd = new Vector<Vector<String>>();

            DefaultTableModel modelopd = new DefaultTableModel(dataopd, columnNamesopd){
                public Class getColumnClass(int column){
                    return getValueAt(0, column).getClass();
                }
            };
            tableopd = new WebTable(modelopd){
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
            tableopd.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = tableopd.rowAtPoint(e.getPoint());
                    int col = tableopd.columnAtPoint(e.getPoint());

                    clearValue();
                    String select_vn=tableopd.getValueAt(row,1).toString().trim();
                    oUserInfo.setPtVN(select_vn);
                    String name=tableopd.getValueAt(row,3).toString().trim();
                    oUserInfo.setPtName(name);
                    String hn=tableopd.getValueAt(row,4).toString().trim();
                    oUserInfo.setPtHN(hn);
                    String clinic_pt=tableopd.getValueAt(row,5).toString().trim();
                    oUserInfo.setPtCliniccode(clinic_pt);
                    String inscl="";
                    //System.out.println(select_vn+"*"+hn+"*"+name+"*"+clinic_pt);
                    searchVN(select_vn,hn,clinic_pt);
                    searchHN(hn);
                    if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                    	
                    }else {
                    	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    }
                    oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
                    //System.out.println("**************"+oUserInfo.GetPtHN()+"------");
                    if(InApp.usertype.trim().equals("85")) {
                        //chemoPanel.checkLastVisit(oUserInfo.GetPtVN(), oUserInfo.GetPtHN());
                    }
                    if(InApp.usertype.trim().equals("87")) {
                        //chemoPanel.checkLastVisit(oUserInfo.GetPtVN(), oUserInfo.GetPtHN());
                    	//sendQ(tableopd.getValueAt(row,2).toString().trim());
                    }
                    if(InApp.usertype.trim().equals("86")) {
                        //chemoPanel.checkLastVisit(oUserInfo.GetPtVN(), oUserInfo.GetPtHN());
                    	//sendQ(tableopd.getValueAt(row,2).toString().trim());
                    }
                    if(oUserInfo.GetPtVN().equals("")) {
    		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
    				}else {
    					//System.out.println(oUserInfo.GetPtHN());
    					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
    				}
                    opdFormPanel.getInOutDateTime(oUserInfo.GetPtHN());
                }
            });
        }else {
        	columnNamesopd = new Vector<String>();
            columnNamesopd.add(I18n.lang("label.order"));
            columnNamesopd.add(I18n.lang("label.vn"));
            columnNamesopd.add(I18n.lang("label.customer.name"));
            for (int n = 3; n < 6; n++) {
                columnNamesopd.add(" ");
            }

            dataopd = new Vector<Vector<String>>();

            DefaultTableModel modelopd = new DefaultTableModel(dataopd, columnNamesopd){
                public Class getColumnClass(int column){
                    return getValueAt(0, column).getClass();
                }
            };
            tableopd = new WebTable(modelopd){
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                    Component c = super.prepareRenderer(renderer, row, column);
                    if (!isRowSelected(row)){
                        c.setBackground(getBackground());
                        String status=(String)getModel().getValueAt(convertRowIndexToModel(row), 5);
                        if ("1".equals(status)){
                            c.setBackground(new Color(206,203,208));
                        }
                    }
                    return c;
                }
            };
            tableopd.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = tableopd.rowAtPoint(e.getPoint());
                    int col = tableopd.columnAtPoint(e.getPoint());

                    clearValue();
                    String select_vn=tableopd.getValueAt(row,1).toString().trim();
                    oUserInfo.setPtVN(select_vn);
                    String name=tableopd.getValueAt(row,2).toString().trim();
                    oUserInfo.setPtName(name);
                    String hn=tableopd.getValueAt(row,3).toString().trim();
                    oUserInfo.setPtHN(hn);
                    String clinic_pt=tableopd.getValueAt(row,4).toString().trim();
                    oUserInfo.setPtCliniccode(clinic_pt);
                    searchVN(select_vn,hn,clinic_pt);
                    searchHN(hn);
                    String inscl="";
                    if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                    	
                    }else {
                    	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    }
                    
                    //System.out.println(select_vn+"*"+hn+"*"+name+"*"+oUserInfo.GetPtCID()+"---"+oUserInfo.GetPtVisitdate());
                   
                    oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
                    //System.out.println("**************"+oUserInfo.GetPtHN()+"------");
                    if(InApp.usertype.trim().equals("85")) {
                        //chemoPanel.checkLastVisit(oUserInfo.GetPtVN(), oUserInfo.GetPtHN());
                    }
                    if(oUserInfo.GetPtVN().equals("")) {
    		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
    				}else {
    					//System.out.println(oUserInfo.GetPtHN());
    					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
    				}
                    opdFormPanel.getInOutDateTime(oUserInfo.GetPtHN());
                     
                }
            });
        }
         
        
        
        tableopd.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tableopd.setRowHeight(25);
        tableopd.setFillsViewportHeight(true);

        scrollPaneopd = new WebScrollPane(tableopd);
        getDataOPD();

        wp.add(scrollPaneopd, BorderLayout.CENTER);
        bottomPanelopd = new WebPanel();
        bottomPanelopd.setBackground(Setup.getColor());
        wp.add(bottomPanelopd, BorderLayout.SOUTH);
        bottomPanelopd.setLayout(null);

        bottomPanelopd.setPreferredSize(new Dimension((width*2)/10, 30));
        ButtonRefreshopd = new WebButton(I18n.lang("label.refresh"));
        ButtonRefreshopd.setBackground(Setup.getColor());
        ButtonRefreshopd.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefreshopd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                getDataOPD();
                clearValue();
            }
        });
        ButtonRefreshopd.setBounds((((width*2)/10)/2-45), 2, 90, 25);
        bottomPanelopd.add(ButtonRefreshopd);

        return wp;
    }
    public void getDataOPD(){

        TableColumnModel columnModel = tableopd.getColumnModel();
        if(InApp.usertype.equals("87")) {
        	tableopd.setModel(fetchDataOPD("87"));
        	columnModel.getColumn(0).setPreferredWidth(40);
            columnModel.getColumn(1).setPreferredWidth(40 );
            columnModel.getColumn(2).setPreferredWidth(40 );
            columnModel.getColumn(3).setPreferredWidth((width*2)/10-120);
            for (int n = 3; n < 6; n++) {
                columnModel.getColumn(n+1).setPreferredWidth(0);
                columnModel.getColumn(n+1).setMinWidth(0);
                columnModel.getColumn(n+1).setMaxWidth(0);
            }
        }else {
        	tableopd.setModel(fetchDataOPD(""));
        	columnModel.getColumn(0).setPreferredWidth(40);
            columnModel.getColumn(1).setPreferredWidth(40 );
            columnModel.getColumn(2).setPreferredWidth((width*2)/10-80);
            for (int n = 2; n < 5; n++) {
                columnModel.getColumn(n+1).setPreferredWidth(0);
                columnModel.getColumn(n+1).setMinWidth(0);
                columnModel.getColumn(n+1).setMaxWidth(0);
            }
        }
         
        ((DefaultTableModel)tableopd.getModel()).fireTableDataChanged();
    }
    public  DefaultTableModel fetchDataOPD(String usertype){
    	 
    	if(usertype.equals("87")) {
    		String[] parts=null;
            parts = InApp.userappcode.split("\\,");
            String sql_user1="",sql_user="";
            if(parts.length==1){
                for(int i=0;i<parts.length;i++){
                    sql_user1=" and visit_clinic.clinic='"+parts[i]+"'";
                }
                sql_user=sql_user1;
            }
            else if(parts.length==0){
                sql_user="";
            }
            else if(parts.length>1){
                sql_user1="and visit_clinic.clinic in (";
                for(int i=0;i<parts.length;i++){
                    sql_user1+="'"+parts[i]+"',";
                }
                sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
            }
    		Connection conn,conn1;
            PreparedStatement stmt,stmt1;
            ResultSet rs,rs1;
            String query="select  visit.visitno,visit.hn,visit_clinic.clinic,visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitdate=?  "+sql_user+" order by visit_clinic.queue IS NULL ASC,visit_clinic.queue  ASC";
            Vector<Vector<String>> data = new Vector<Vector<String>>();
            try {
                conn=new DBmanager().getConnMySql();
                conn1=new DBmanager().getConnH2();
				
                stmt = conn.prepareStatement(query);
                stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                rs = stmt.executeQuery();
                int p=1;
                while (rs.next()) {

                    String clinic_pt="";
                    if(rs.getString(3)!=null){
                        clinic_pt=rs.getString(3);
                    }
                    String pt_status="0";
                    //if(rs.getString(3)!=null){
                     //   pt_status="1";
                    //}
                    String hn=""; 
                    if(rs.getString(2)!=null){
                    	hn=rs.getString(2).trim();
                    }
                    final Vector<String> vstring = new Vector<String>();
                    vstring.add(" "+Integer.toString(p) );
                    //vstring.add(rs.getString(1)+": "+initname+" "+rs.getString(2).substring(1)+" "+rs.getString(3).substring(1));
                    vstring.add(rs.getString(1));
                    String queue="";
                    if(rs.getString(4)!=null){
                       queue=rs.getString(4).trim();
                    }
                    //queue=updateQueue(rs.getString(1).trim(), Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    vstring.add(queue);
                    vstring.add(Setup.getName(hn));
                    vstring.add(hn);
                    vstring.add(clinic_pt);
                    vstring.add(pt_status);

                    data.add(vstring);
                    p++;
                    
                    int cont=0;
                    String sql_="select vn from labalert where vn='"+rs.getString(1).trim()+"'";
                    PreparedStatement stmt21=conn1.prepareStatement(sql_);
                    ResultSet rs21 = stmt21.executeQuery();
                    while (rs21.next()) {
                    	if(rs21.getString(1)!=null) {
                    		cont=1;
                    	}
                    }
                    stmt21.close();
                    if(cont==0) {
                    	PreparedStatement stmt2;
    					stmt2 = conn1.prepareStatement( "INSERT INTO labalert ( vn,clinic,reqno,status ) VALUES ( '"+rs.getString(1).trim()+"','"+clinic_pt+"','','0' )" );
    					stmt2.executeUpdate();
    					stmt2.close();
                    }
                     
                }

                stmt.close();
                conn.close();
                conn1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new DefaultTableModel(data, columnNamesopd);
    	}else {
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
            String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?   "+ptStatus+" "+sql_user+"  order by case IsNumeric(vnmst.vn)  when 1 then Replicate('0', 100 - Len(vnmst.vn)) + vnmst.vn else vnmst.vn end ";
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
                    //vstring.add(rs.getString(1)+": "+initname+" "+rs.getString(2).substring(1)+" "+rs.getString(3).substring(1));
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
            return new DefaultTableModel(data, columnNamesopd);
    	}
         
         
         
    }
    public WebPanel getIPD(){
        clearValue();
        WebPanel wp = new WebPanel();
        wp.setLayout(new BorderLayout(0, 0));
        wp.setPreferredSize(new Dimension((width*2)/10, height-405));
        topMainLeftPanelipd = new WebPanel();
        wp.add(topMainLeftPanelipd, BorderLayout.NORTH);
        topMainLeftPanelipd.setBackground(Setup.getColor());
        topMainLeftPanelipd.setLayout(null);
        topMainLeftPanelipd.setPreferredSize(new Dimension((width*2)/10, 30));

        WebLabel LabelListdata = new WebLabel("Ward",JLabel.CENTER);
        LabelListdata.setFont(new Font("Tahoma", Font.PLAIN, 13));
        LabelListdata.setBounds(5,5,50,20);
        topMainLeftPanelipd.add(LabelListdata);

        columnNamesipd = new Vector<String>();
        columnNamesipd.add(I18n.lang("label.order"));
        columnNamesipd.add(I18n.lang("label.an"));
        columnNamesipd.add(I18n.lang("label.customer.name"));
        for (int n = 3; n < 6; n++) {
            columnNamesipd.add(" ");
        }
        dataipd = new Vector<Vector<String>>();
        DefaultTableModel modelipd = new DefaultTableModel(dataipd, columnNamesipd){
            public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }
        };
        tableipd = new WebTable(modelipd){
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component c = super.prepareRenderer(renderer, row, column);
                return c;
            }
        };
        tableipd.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableipd.rowAtPoint(e.getPoint());
                int col = tableipd.columnAtPoint(e.getPoint());
                clearValue();
                String select_an=tableipd.getValueAt(row,1).toString().trim();
                oUserInfo.setPtAN(select_an);
                String name=tableipd.getValueAt(row,2).toString().trim();
                oUserInfo.setPtName(name);
                String hn=tableipd.getValueAt(row,3).toString().trim();
                oUserInfo.setPtHN(hn);

                //System.out.println(select_an+"*"+hn+"*"+name+"*");
                searchAN(select_an,hn);
                searchHN(hn);
                String inscl="";
                if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                	
                }else {
                	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                }
                
                String pt_bed=tableipd.getValueAt(row,4).toString().trim();
		        oUserInfo.setPtBed(pt_bed);
		        String pt_ward=tableipd.getValueAt(row,5).toString().trim();
		        oUserInfo.setPtWard(pt_ward);
                oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
                //System.out.println("**************"+oUserInfo.GetPtWard()+"------"+oUserInfo.GetPtBed());

            }
        });
        tableipd.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tableipd.setRowHeight(25);
        tableipd.setFillsViewportHeight(true);
        scrollPaneipd = new WebScrollPane(tableipd);
        wp.add(scrollPaneipd, BorderLayout.CENTER);

        bottomPanelipd = new WebPanel();
        bottomPanelipd.setBackground(Setup.getColor());
        wp.add(bottomPanelipd, BorderLayout.SOUTH);
        bottomPanelipd.setLayout(null);

        bottomPanelipd.setPreferredSize(new Dimension((width*2)/10, 30));

        cbWard = new WebComboBox();
        cbWard.setFont(new Font("Tahoma", Font.PLAIN, 13));
        cbWard.setSize(180, 250);
        cbWard.setBounds(60, 5, 180, 25);
        cbWard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String ward="";
                ward=((ComboItem)cbWard.getSelectedItem()).getValue();
                getDataIPD(ward);
            }
        });
        topMainLeftPanelipd.add(cbWard);
        init_cbward();
        return wp;
    }
    public void init_cbward() {
        try {
            Connection conn=new DBmanager().getConnMySql();
            PreparedStatement stmtward = conn.prepareStatement(SetupSQL.queryward);
            ResultSet rsward = stmtward.executeQuery();
            while (rsward.next()) {
                cbWard.addItem(new ComboItem(rsward.getString(1).trim(),rsward.getString(2).trim()));
            }
            rsward.close();
            stmtward.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getDataIPD(String ward){
        tableipd.setModel(fetchDataIPD(ward));
        TableColumnModel columnModel = tableipd.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80 );
        columnModel.getColumn(2).setPreferredWidth((width*2)/10-120);
        for (int n = 2; n < 5; n++) {
            columnModel.getColumn(n+1).setPreferredWidth(0);
            columnModel.getColumn(n+1).setMinWidth(0);
            columnModel.getColumn(n+1).setMaxWidth(0);
        }
        ((DefaultTableModel)tableipd.getModel()).fireTableDataChanged();
    }
    public  DefaultTableModel fetchDataIPD(String ward){
        Connection conn;
        PreparedStatement stmt;
        ResultSet rs;
        String query="select admmaster.an,admmaster.hn,admbed.ward,admbed.bedno from admmaster,admbed  where admmaster.an=admbed.an and admbed.OUTDATETIME  is null and admmaster.dischargedatetime is  null  and admmaster.admward='"+ward+"'  order by admbed.bedno ";
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        try {
            conn=new DBmanager().getConnMSSql();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            int p=1;
            while (rs.next()) {
                final Vector<String> vstring = new Vector<String>();
                vstring.add(" "+Integer.toString(p) );
                vstring.add(rs.getString(1));
                vstring.add(Setup.getName(rs.getString(2).trim()));
                vstring.add(rs.getString(2).trim());
                if(rs.getString(4) !=null) {
                	vstring.add(rs.getString(4).trim());
                }else {
                	vstring.add("");
                }
                if(rs.getString(3) !=null) {
                	vstring.add(rs.getString(3).trim());
                }else {
                	vstring.add("");
                }
                 

                data.add(vstring);
                p++;
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(data, columnNamesipd);
    }
    public WebPanel getSearch(){
        clearValue();
        WebPanel wp = new WebPanel();
        wp.setLayout(null);
        wp.setPreferredSize(new Dimension((width*2)/10, height-405));

        WebLabel Label_HNS = new WebLabel("HN",JLabel.RIGHT);
        Label_HNS.setFont(new Font("Tahoma", Font.PLAIN, 13));
        wp.add(Label_HNS);
        Label_HNS.setBounds(5, 20, 50, 14);

        WebTextField TextField_HNS = new WebTextField();
        wp.add(TextField_HNS);
        TextField_HNS.setBounds(70, 20, 80, 20);

        WebButton ButtonSearch = new WebButton("Search");
        ButtonSearch.setBackground(Setup.getColor());
        ButtonSearch.setForeground(UIManager.getColor("Button.darkShadow"));
        ButtonSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                clearValue();
                getSearchData(TextField_HNS.getText());
            }
        });
        ButtonSearch.setBounds(5, 50, 100, 25);
        wp.add(ButtonSearch);
        return wp;
    }
    public void getSearchData(String hn) {
        if(hn.trim().length()==7) {
            oUserInfo.setPtHN(hn.trim());
            String name=Setup.getName(hn.trim());
            oUserInfo.setPtName(name);
            searchHN(hn.trim());
            Label_NAME.setText(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge());
            TextField_CID.setText(oUserInfo.GetPtCID());
            TextField_HN.setText(oUserInfo.GetPtHN());
            textArea.setText(oUserInfo.GetMemo());
            Label_Extra1.setText(oUserInfo.GetMemo1());
        }

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
        if(!Setup.checkEmptyTextField(TextField_VN)){
            if(TextField_VN.getText().trim().length()==25){
                String clinic_in=TextField_VN.getText().trim().substring(8,12);
                String hn_in=TextField_VN.getText().trim().substring(18);
                String visitdate_in=Setup.DateInDBMSSQLno(TextField_VN.getText().trim().substring(0,8));

                oUserInfo.setPtHN(hn_in);
                oUserInfo.setPtCliniccode(clinic_in);
                Connection conn;
                PreparedStatement stmt,stmt1;
                ResultSet rs,rs1;
                String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnmat.hn=?  and vnpres.clinic=? ";
                conn=new DBmanager().getConnMSSql();
                try {
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, visitdate_in);
                    stmt.setString(2,hn_in.trim());
                    stmt.setString(3,clinic_in.trim());
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        oUserInfo.setPtVN(rs.getString(1).trim());
                        searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),clinic_in.trim());
                        searchHN(hn_in);
                        String name=Setup.getName(rs.getString(2).trim());
                        oUserInfo.setPtName(name);
                        String inscl="";
                        if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                        	
                        }else {
                        	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                        }
                        oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
                    }
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                Connection conn;
                PreparedStatement stmt,stmt1;
                ResultSet rs,rs1;
                String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnpres.vn=?  "+sql_user+" ";
                conn=new DBmanager().getConnMSSql();
                try {
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                    stmt.setString(2, vn.trim());
                    rs = stmt.executeQuery();

                    while (rs.next()) {
                        oUserInfo.setPtHN(rs.getString(2).trim());
                        String res=Setup.getClinic(vn,Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())).trim();
                        //System.out.println("res  --- "+res+"--"+res.substring(0, res.indexOf(":")));
                        if(res.substring(0, res.indexOf(":")).equals("1")){
                            searchVN(vn,(rs.getString(2)).trim(),res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
                            oUserInfo.setPtCliniccode(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
                            searchHN(rs.getString(2));
                        }
                        else if(res.substring(0, res.indexOf(":")).equals("0")){

                        }
                        else{
                            String text=res.substring(res.indexOf(":")+1);
                            String[] parts1 = text.split("-");
                            String selected=(String)JOptionPane.showInputDialog(null, "กรุณาเลือกห้องตรวจ", "Clinic", JOptionPane.QUESTION_MESSAGE,
                                    null, parts1, "");
                            searchVN(vn,(rs.getString(2)).trim(),selected);
                            oUserInfo.setPtCliniccode(selected);
                            searchHN(rs.getString(2));
                            //System.out.println(selected);
                        }
                        String name=Setup.getName(rs.getString(2).trim());
                        oUserInfo.setPtName(name);
                        String inscl="";
                        if(oUserInfo.GetPtCID().equals("")&& oUserInfo.GetPtCID().trim().length() !=13) {
                        	
                        }else {
                        	 inscl=Setup.getNHSO(oUserInfo.GetPtCID().trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
                        }
                        oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
        int p=0;
        Connection conn;
        PreparedStatement stmt,stmt1;
        ResultSet rs,rs1;
        String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  and vnmst.hn=?  "+sql_user+" ";
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        try {
            conn=new DBmanager().getConnMSSql();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
            stmt.setString(2, hn_in.trim());
            rs = stmt.executeQuery();

            while (rs.next()) {
                oUserInfo.setPtVN(rs.getString(1).trim());
                String res=Setup.getClinic(rs.getString(1).trim(),Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())).trim();
                //System.out.println("res  --- "+res+"--"+res.substring(0, res.indexOf(":")));
                if(res.substring(0, res.indexOf(":")).equals("1")){
                    searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
                    searchHN(rs.getString(2));
                    oUserInfo.setPtCliniccode(res.substring(res.indexOf(":")+1,(res.indexOf("-"))));
                }
                else if(res.substring(0, res.indexOf(":")).equals("0")){
                }
                else{
                    //int npage=Integer.parseInt(res.substring(0, res.indexOf(":")));
                    String text=res.substring(res.indexOf(":")+1);
                    String[] parts1 = text.split("-");
                    //String fn_[] = new String[npage];
                    String selected=(String)JOptionPane.showInputDialog(null, "กรุณาเลือกห้องตรวจ", "Clinic", JOptionPane.QUESTION_MESSAGE,
                            null, parts1, "");
                    searchVN(rs.getString(1).trim(),(rs.getString(2)).trim(),selected);
                    oUserInfo.setPtCliniccode(selected);
                    searchHN(rs.getString(2));
                    //System.out.println(selected);
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
            JOptionPane.showMessageDialog(null, "ไม่พบ vn ในห้องตรวจนี้", "VN Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void searchVN(String vn,String hn,String clinic){
        Connection conn;
        PreparedStatement stmt,stmt1,stmt2,stmt3;
        ResultSet rs,rs1,rs2,rs3;
        String query_right="select  rightcode from vnpres where visitdate=? and clinic='"+clinic+"'   and vn='"+vn.trim()+"'" ;
        conn=new DBmanager().getConnMSSql();
        try {
            stmt1 = conn.prepareStatement(query_right);
            stmt1.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
            rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                String  right="";
                if(rs1.getString(1) !=null){
                    for(int i=0;i<OPDFrame.rightname.length;i++){
                        if(rs1.getString(1).trim().equals(OPDFrame.rightname[i][0])){
                            right=OPDFrame.rightname[i][1];
                            break;
                        }
                    }
                    oUserInfo.setRightCode(right+"("+rs1.getString(1).trim()+")");
                }
            }
            stmt1.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void searchAN(String an,String hn){
        Connection conn;
        PreparedStatement stmt,stmt1,stmt2,stmt3;
        ResultSet rs,rs1,rs2,rs3;
        String query_right="select  usedrightcode from admmaster where an='"+an.trim()+"'" ;
        conn=new DBmanager().getConnMSSql();
        try {
            stmt1 = conn.prepareStatement(query_right);
            rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                String  right="";
                if(rs1.getString(1) !=null){
                    for(int i=0;i<OPDFrame.rightname.length;i++){
                        if(rs1.getString(1).trim().equals(OPDFrame.rightname[i][0])){
                            right=OPDFrame.rightname[i][1];
                            break;
                        }
                    }
                    oUserInfo.setRightCode(right+"("+rs1.getString(1).trim()+")");
                }
            }
            stmt1.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void searchHN(String hn){
        Connection conn;
        PreparedStatement stmt,stmt1,stmt2,stmt3;
        ResultSet rs,rs1,rs2,rs3;
        String query_db="select  BirthDateTime from patient_info where hn='"+hn.trim()+"'" ;
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
            stmt2 = conn.prepareStatement(query_ref);
            rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                if(rs2.getString(1) !=null){
                    oUserInfo.setPtCID(rs2.getString(1).trim());
                }
            }
            stmt2.close();
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
            for(int i=0;i<OPDFrame.hn_z515.length;i++){
                if(hn.equals(OPDFrame.hn_z515[i])){
                    memo2="Z515";
                    break;
                }
            }
            oUserInfo.setMemo(memo1);
            oUserInfo.setMemo1(memo2);
            conn.close();
            System.out.println(memo1+"----");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void getDataOPDClear(){
        tableopd.setModel(fetchDataOPDClear());
        TableColumnModel columnModel = tableopd.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(40 );
        columnModel.getColumn(2).setPreferredWidth((width*2)/10-80);
        for (int n = 2; n < 5; n++) {
            columnModel.getColumn(n+1).setPreferredWidth(0);
            columnModel.getColumn(n+1).setMinWidth(0);
            columnModel.getColumn(n+1).setMaxWidth(0);
        }
        ((DefaultTableModel)tableopd.getModel()).fireTableDataChanged();
    }
    public  DefaultTableModel fetchDataOPDClear(){
        Vector<Vector<String>> data1 = new Vector<Vector<String>>();
        return new DefaultTableModel(data1, columnNamesopd);
    }
    public String updateQueue(String vn,String visitdate) {
    	String q="";
    	Connection conn;
        PreparedStatement stmt;
        ResultSet rs,rs1;
        String query="select  visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitno='"+vn+"' and visit.visitdate='"+visitdate+"' and visit_clinic.clinic='0299'";
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        try {
            conn=new DBmanager().getConnMySql();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            int p=1;
            while (rs.next()) {
            	q=rs.getString(1).trim();
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	
    	return q;
    }
    public void getSmartcard(){
    	OkHttpClient client=Setup.getUnsafeOkHttpClient();
		Request request = new Request.Builder()
				  .url("https://localhost:8443/smartcard/data")
				  .get()
				  .build();
		Request requestimg = new Request.Builder()
				  //.url("https://localhost:8443/smartcard/data")
				  .url("https://localhost:8443/smartcard/picture")
				  .get()
				  .build();
		Response response,responseimg;
		String datafromcard="";
		String cid_="",fname_="",lname_="",prename_="";
    	try {
			response = client.newCall(request).execute();
			String data=response.body().string();
			JSONObject obj = new JSONObject(data);
			if(obj.getString("cid")!=null) {
				cid_ = obj.getString("cid").trim();
				//System.out.println("cid >>"+cid_);
			}
			if(obj.getString("prename")!=null) {
				prename_ = obj.getString("prename").trim();
				//System.out.println("prename >>"+prename_);
			}
			if(obj.getString("fname")!=null) {
				fname_ = obj.getString("fname").trim();
				//System.out.println("fname >>"+fname_);
			}
			if(obj.getString("lname")!=null) {
				lname_ = obj.getString("lname").trim();
				//System.out.println("lname >>"+lname_);
			}
			//System.out.println("inform >>"+data+"--");
			response.close();
			datafromcard=prename_+" "+fname_+" "+lname_+"\n"+cid_;
			responseimg = client.newCall(requestimg).execute();
			byte [] b=responseimg.body().bytes();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
			if(previewPDFDocumentInImage(oUserInfo.GetPtHN())!=null) {
            	labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
            }else {
            	sendImageHDFS(oUserInfo.GetPtCID());
            	labelImage.setIcon(new ImageIcon(img));
            }
			//labelImage.setIcon(new ImageIcon(img));
			searchPtcid(cid_,datafromcard);
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    public void getImage(){
    	OkHttpClient client=Setup.getUnsafeOkHttpClient();
		Request request = new Request.Builder()
				  //.url("https://localhost:8443/smartcard/data")
				  .url("https://localhost:8443/smartcard/picture")
				  .get()
				  .build();
		Response response;
    	try {
			response = client.newCall(request).execute();
			byte [] b=response.body().bytes();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
			labelImage.setIcon(new ImageIcon(img));
			//System.out.println("inform >>"+"--");
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    public void searchPtcid(String cid,String datacard) {
    	 String hn="";
         String query="select hn from patient_ref where ref='"+cid+"' and reftype='01' ";
         Connection conn=new DBmanager().getConnMSSql();
         try {
        	 PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	 if(rs.getString(1)!=null) {
	        		 hn=rs.getString(1).trim();
	        	 }
	        }
	        stmt.close();
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
         if(hn.equals("")) {
             WebOptionPane.showMessageDialog(this, datacard,"ข้อมูลจากบัตร",WebOptionPane.ERROR_MESSAGE);
             clearValue();
         }else {
        	 searchPthn(hn);
         }
         
        
        
    }
    public void sendQ(String q) {
    	int SOCKET_PORT = 17001;
    	String SERVER="172.17.71.1";
    	Socket sock = null;
    	PrintWriter toServer=null;
    	String s="";
    	 if(q.trim().length()==1){
             s="00"+q;
         }else if(q.trim().length()==2){
        	 s="0"+q;
         }
         else {
        	 s=q;
         }
     
    	try {
			sock = new Socket(SERVER, SOCKET_PORT);
			toServer=new PrintWriter(sock.getOutputStream(), true);
			//String s="25";
		    toServer.println(s);
		    toServer.flush(); 
		    //System.out.println(s+"<<<");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
    	
    }
    public  Image previewPDFDocumentInImage(String filescanname){
		BufferedImage img=null;
		ByteBuffer buf = null;
		String hn=filescanname;
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		String fnto=hn+"000001";
		//String filename="255511290001014700005HN.pdf";
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		FileSystem fs;
		PDFFile pdffile;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path newFolderPath= new Path(path1);
			 Path path = new Path(newFolderPath+"/"+fnto+".jpg");
			 //System.out.println(newFolderPath+"/"+fnto+".jpg");
			    if (!fs.exists(path)) {
			     
			 
			}
			FSDataInputStream in = fs.open(path);
		    byte[] b= IOUtils.toByteArray(in);
		    //buf = ByteBuffer.wrap(b);
		    img = ImageIO.read(new ByteArrayInputStream(b));

			

			
		} catch (IOException e) {
			e.printStackTrace();
			img=null;
		}
		return img;
    	 
	}
    public void sendImageHDFS(String cid){
    	String hn=oUserInfo.GetPtHN();
    	String path="/utth";
		String fnto=hn+"000001";
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path homeDir=fs.getHomeDirectory();
			Path workingDir=fs.getWorkingDirectory();
			String folderhn=hn.substring(0, 2).trim();
		      Path newFolderPath= new Path(path+"/"+folderhn+"/"+hn);
		      if(!fs.exists(newFolderPath)) {
		         // Create new Directory
		         fs.mkdirs(newFolderPath);
		         //System.out.println("Path "+path+" created.");
		      }
			Path localFilePath = new Path("c://JSmartCardReader1.0.0.0//picture//"+cid+".jpg");
			Path hdfsFilePath=new Path(newFolderPath+"/"+fnto+".jpg");

			fs.moveFromLocalFile(localFilePath, hdfsFilePath);
			//System.out.println("sent "+fnto);

			fs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        oUserInfo.setPtWard("");
        oUserInfo.setPtBed("");
        oUserInfo.setPtAN("");
        oUserInfo.setAdmtime("");
        opdFormPanel.clearPanel();
        mainHDFSOPDPanel.clear();
        labOPDPanel.clearPanel();
        labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
    }
}
