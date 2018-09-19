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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.opd.OPDFrame;
import org.utt.app.hdfs.MainHDFSOPDPanel;
import org.utt.app.util.ComboItem;
import org.utt.app.util.DateLabelFormatter;
import org.utt.app.util.I18n;
import org.utt.app.util.Prop;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;

public class DentalCommPanel extends WebPanel implements Observer{
	 ObjectData oUserInfo;
	 int width,height;
	 String day="",month="",dateSearch="";
	 
	 WebTabbedPane tabbedPane,tabbedPane_in;
	 WebSplitPane split,splitR;
	 WebPanel leftPanel,topMainPanel,midPanel,mainLeftPanel,bottomPanel;
	 WebPanel topLeftPanel,pt_info,jPanel,topMainLeftPanelstudent;
	 WebLabel Label_Date_l,LabelListdata,Label_VN,Label_HN,Label_CID,Label_NAME,labelImage,Label_Extra,Label_Extra1,Label_Memo ;
	 WebTextField TextField_VN,TextField_HN,TextField_CID;
	 WebButton ButtonRefresh,ButtonSmartcard;
	 WebTable table;
	 WebScrollPane scrollPane;
	 WebTextArea textArea;
	 DentalCommFormPanel dentalCommFormPanel;
	 DentalCommTxPanel dentalCommTxPanel;
	 Dental43Result dental43Result;
	 WebAccordion accordion;
	 JDatePickerImpl picker;
	 
	 WebTable tablestudent;
	 WebComboBox cbSchool;
	 Vector<String> columnNamestudent;
	 Vector<Vector<String>> datastudent;
	 WebScrollPane scrollPanestudent;
	
	 public DentalCommPanel(ObjectData oUserInfo,int w,int h) {
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
	        dentalCommFormPanel =new DentalCommFormPanel(oUserInfo,w,h);
	        dentalCommTxPanel =new DentalCommTxPanel(oUserInfo,w,h);
	        dental43Result = new Dental43Result(oUserInfo,w,h);
	        //dentalStatus = new DentalStatus(oUserInfo,w,h);
	        //dentalEDCPanel = new DentalEDCPanel(oUserInfo,w,h);

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
	        oUserInfo = ((ObjectData)oObservable); // cast
	        Label_Date_l.setText(I18n.lang("label.date")+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()));
	        TextField_HN.setText(oUserInfo.GetPtHN());
	        TextField_CID.setText(oUserInfo.GetPtCID());
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
	                //getDataOPDClear();
	                clearValue();
	                dentalCommFormPanel.clearPanel();
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
	        TextField_VN.setEnabled(false);

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
	                labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
	                dentalCommFormPanel.clearPanel();
	                dentalCommTxPanel.setupPanel();
	                dentalCommFormPanel.setupPanel();
	                dentalCommTxPanel.setupPanel();
	                dental43Result.setup();
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
	        pt_info.add(textArea);

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
	        labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
	        labelImage.setBounds(width-(((width*2)/10)+200), 1, 140, 140);
	        pt_info.add(labelImage);


	        ButtonSmartcard = new WebButton("อ่านข้อมูลจากบัตร");
	        ButtonSmartcard.setBackground(Setup.getColor());
	        ButtonSmartcard.setForeground(UIManager.getColor("Button.darkShadow"));

	        ButtonSmartcard.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent arg0) {
	                //clearValue();
	                //getSmartcard();
	            }
	        });
	        ButtonSmartcard.setBounds(700, 8, 150, 25);
	       // pt_info.add(ButtonSmartcard);



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

	        mainLeftPanel = new WebPanel();
	        mainLeftPanel.setPreferredSize(new Dimension((width*2)/10, height-30));
	        mainLeftPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	        mainLeftPanel.setLayout(new BorderLayout(0, 0));
	        leftPanel.add(mainLeftPanel, BorderLayout.CENTER);
	        
	        accordion = new WebAccordion( );
	        accordion.addPane ( new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")), " โรงเรียน", getSchool() );
	        
	        accordion.setMultiplySelectionAllowed ( false );
	        
	        
	        leftPanel.add(accordion, BorderLayout.CENTER);

	        

	    }
	    public void setRight(){
	        midPanel = new WebPanel();
	        midPanel.setLayout(new BorderLayout(0, 0));
	        midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-140));
	        tabbedPane_in = new WebTabbedPane();
	        midPanel.add(tabbedPane_in, BorderLayout.CENTER);
	        tabbedPane_in.addTab(Prop.getProperty("tab.dental.form") ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dentalCommFormPanel);
	        tabbedPane_in.addTab("บริการทันตกรรม" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dentalCommTxPanel);
	        tabbedPane_in.addTab("43 Detail" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dental43Result);
	        
	        //tabbedPane_in.addTab("EMR",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,mainHDFSOPDPanel);
	        //tabbedPane_in.addTab("Dental Status",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,dentalStatus);
	        
	        //tabbedPane_in.addTab("EDC",new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dentalEDCPanel);

	    }
	    public void searchPthn(String hn_in){
	    	
	    	searchHN(hn_in);
	    	String name=Setup.getName(hn_in);
	    	oUserInfo.setPtName(name);
	    	searchRight(hn_in);
            String inscl="";
            oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");	    
	    }
	    public void clearValue(){
	        oUserInfo.setPtVN("");
	        // oUserInfo.setFN("");
	        oUserInfo.setPtName("");
	        oUserInfo.setPtHN("");
	        oUserInfo.setRightCode("");
	        oUserInfo.setPtCliniccode("");
	        oUserInfo.setPtAge("");
	        oUserInfo.setPtLabel("");
	        oUserInfo.setPtCID("");
	        oUserInfo.setMemo("");
	        oUserInfo.setMemo1("");
	        //oUserInfo.setFormReport("");
	        //mainHDFSOPDPanel.clear();
	        //mainOldHDFSOPDPanel.clear();
	        //dentalFormPanel.clearPanel();
	        labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
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
	                    memo1=rs3.getString(1).trim();
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
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }


	    }
	    public void searchRight(String hn){
	        Connection conn;
	        PreparedStatement stmt,stmt1,stmt2,stmt3;
	        ResultSet rs,rs1,rs2,rs3;
	        String query_right="select  rightcode from patient_right where  hn='"+hn.trim()+"' and defaultright='1'" ;
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
	    public WebPanel getSchool(){
	        clearValue();
	        WebPanel wp = new WebPanel();
	        wp.setLayout(new BorderLayout(0, 0));
	        wp.setPreferredSize(new Dimension((width*2)/10, height-405));
	        topMainLeftPanelstudent = new WebPanel();
	        wp.add(topMainLeftPanelstudent, BorderLayout.NORTH);
	        topMainLeftPanelstudent.setBackground(Setup.getColor());
	        topMainLeftPanelstudent.setLayout(null);
	        topMainLeftPanelstudent.setPreferredSize(new Dimension((width*2)/10, 30));

	        WebLabel LabelListdata = new WebLabel("โรงเรียน",JLabel.CENTER);
	        LabelListdata.setFont(new Font("Tahoma", Font.PLAIN, 13));
	        LabelListdata.setBounds(5,5,50,20);
	        topMainLeftPanelstudent.add(LabelListdata);

	        columnNamestudent = new Vector<String>();
	        columnNamestudent.add(I18n.lang("label.order"));
	        columnNamestudent.add(I18n.lang("label.hn"));
	        columnNamestudent.add(I18n.lang("label.customer.name"));
	        for (int n = 3; n < 6; n++) {
	            columnNamestudent.add(" ");
	        }
	        datastudent = new Vector<Vector<String>>();
	        DefaultTableModel modelipd = new DefaultTableModel(datastudent, columnNamestudent){
	            public Class getColumnClass(int column){
	                return getValueAt(0, column).getClass();
	            }
	        };
	        tablestudent = new WebTable(modelipd){
	            public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
	                Component c = super.prepareRenderer(renderer, row, column);
	                if (!isRowSelected(row)){
                        c.setBackground(getBackground());
                        String status=(String)getModel().getValueAt(convertRowIndexToModel(row), 3);
                        if ("1".equals(status)){
                            c.setBackground(new Color(206,203,208));
                        }
                    }
	                return c;
	            }
	        };
	        tablestudent.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                int row = tablestudent.rowAtPoint(e.getPoint());
	                int col = tablestudent.columnAtPoint(e.getPoint());
	                /* 
	                clearValue();
	                String select_hn=tablestudent.getValueAt(row,1).toString().trim();
	                oUserInfo.setPtHN(select_hn);
	                String name=tablestudent.getValueAt(row,2).toString().trim();
	                oUserInfo.setPtName(name);
	                //String hn=tablestudent.getValueAt(row,3).toString().trim();
	                //oUserInfo.setPtHN(hn);
	                String inscl="";
	                //System.out.println(select_an+"*"+hn+"*"+name+"*");
	                //searchAN(select_an,hn);
	                searchHN(select_hn);
	                searchRight(select_hn);
	               // String pt_bed=tablestudent.getValueAt(row,4).toString().trim();
			        //oUserInfo.setPtBed(pt_bed);
			        //String pt_ward=tablestudent.getValueAt(row,5).toString().trim();
			        //oUserInfo.setPtWard(pt_ward);
	                oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
	                //System.out.println("**************"+oUserInfo.GetPtWard()+"------"+oUserInfo.GetPtBed());
*/
	            }
	        });
	        tablestudent.setFont(new Font("Tahoma", Font.PLAIN, 13));
	        tablestudent.setRowHeight(25);
	        tablestudent.setFillsViewportHeight(true);
	        scrollPanestudent = new WebScrollPane(tablestudent);
	        wp.add(scrollPanestudent, BorderLayout.CENTER);

	        cbSchool = new WebComboBox();
	        cbSchool.setFont(new Font("Tahoma", Font.PLAIN, 13));
	        cbSchool.setSize(180, 250);
	        cbSchool.setBounds(60, 5, 180, 25);
	        cbSchool.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent arg0) {
	                String code="";
	                code=((ComboItem)cbSchool.getSelectedItem()).getValue();
	                getDataSchool(code);
	            }
	        });
	        topMainLeftPanelstudent.add(cbSchool);
	        init_code();
	        return wp;
	    }
	    public void init_code() {
	    	cbSchool.addItem(new ComboItem("รร.อนุบาล  ป.6","5300106"));
	        
	    }
	    public void getDataSchool(String code){
	        tablestudent.setModel(fetchDataSchool(code));
	        TableColumnModel columnModel = tablestudent.getColumnModel();
	        columnModel.getColumn(0).setPreferredWidth(40);
	        columnModel.getColumn(1).setPreferredWidth(80 );
	        columnModel.getColumn(2).setPreferredWidth((width*2)/10-120);
	        for (int n = 2; n < 5; n++) {
	            columnModel.getColumn(n+1).setPreferredWidth(0);
	            columnModel.getColumn(n+1).setMinWidth(0);
	            columnModel.getColumn(n+1).setMaxWidth(0);
	        }
	        ((DefaultTableModel)tablestudent.getModel()).fireTableDataChanged();
	    }
	    public  DefaultTableModel fetchDataSchool(String code){
	        Connection conn;
	        PreparedStatement stmt;
	        ResultSet rs;
	        String query="select hn,typearea from patient where nickname='"+code+"' ";
	        Vector<Vector<String>> data = new Vector<Vector<String>>();
	        try {
	            conn=new DBmanager().getConnMySql();
	            stmt = conn.prepareStatement(query);
	            rs = stmt.executeQuery();
	            int p=1;
	            while (rs.next()) {
	                final Vector<String> vstring = new Vector<String>();
	                vstring.add(" "+Integer.toString(p) );
	                vstring.add(rs.getString(1));
	                vstring.add(Setup.getName(rs.getString(1).trim()));
	                String type="";
	                if(rs.getString(2) !=null) {
	                	type=rs.getString(2).trim();
	                }
	                vstring.add(type);
	                vstring.add("");
	                vstring.add("");
	                 
	                 

	                data.add(vstring);
	                p++;
	            }
	            stmt.close();
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return new DefaultTableModel(data, columnNamestudent);
	    }
	    

}


 