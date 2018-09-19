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
package org.utt.app.admit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.ipd.IPDFrame;
import org.utt.app.ipd.NurseFormIPD;
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
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.sun.pdfview.PDFFile;

public class AdmitPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    
    WebPanel leftPanel,midPanel,topMainPanel,bottomPanel,pt_info,jPanel,topLeftPanel,mainLeftPanel,topMainLeftPanel;
    WebPanel bottomPanelipd,topMainLeftPanelipd;
    WebLabel Label_Date_l,LabelListdata;
    WebSplitPane split,splitR;
    WebTextArea textArea;
    WebButton ButtonRefresh,ButtonSmartcard;
    WebTextField TextField_AN,TextField_HN,TextField_CID;
    WebLabel Label_AN,Label_HN,Label_CID,Label_NAME,Label_Memo,Label_Extra,Label_Extra1,Label_LOS,labelImage;
    WebScrollPane scrollPane,scrollPaneDrugA,scrollPaneipd;
    WebTabbedPane tabbedPane;
    WebAccordion accordion;
    WebComboBox cbWard;
    Vector<String> columnNames,columnNamesipd;
    Vector<Vector<String>> data,dataipd;
    WebTable table,tableipd;
    JDatePickerImpl picker;
    MainHDFSOPDPanel mainHDFSOPDPanel;
    MedReconPanel medReconPanel;
    MedIPDPanel medIPDPanel;
    
    String day="",month="",dateSearch="";
    
    public AdmitPanel(ObjectData oUserInfo,int w,int h) {
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
        medReconPanel = new MedReconPanel(oUserInfo,w,h);
        medIPDPanel = new MedIPDPanel(oUserInfo,w,h);
    	
        setTop();
        setLeft();
        setRight();
        
        add(split, BorderLayout.CENTER);
        split.setRightComponent(splitR);
        split.setLeftComponent(leftPanel);
        split.setOneTouchExpandable(true);
        splitR.setTopComponent(topMainPanel);
        splitR.setBottomComponent(midPanel);
        
        
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
        Label_Date_l.setText(I18n.lang("label.date")+Setup.ShowThaiDate(oUserInfo.GetPtVisitdate()));
        TextField_HN.setText(oUserInfo.GetPtHN());
		TextField_CID.setText(oUserInfo.GetPtCID());
		TextField_AN.setText(oUserInfo.GetPtAN());
		Label_NAME.setText(oUserInfo.GetPtLabel());
		textArea.setText(oUserInfo.GetMemo());
		Label_Extra1.setText(oUserInfo.GetMemo1());
		Label_LOS.setText(" LOS :: "+oUserInfo.GetAdmtime()+" วัน");
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
        WebLabel Label_Date = Setup.getLabel(I18n.lang("label.date"), 13,0, SwingConstants.CENTER);
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
        
        Label_AN = Setup.getLabel("AN", 13,4,SwingConstants.RIGHT);
        Label_AN.setBounds(270, 12, 20, 14);
		pt_info.add(Label_AN);
		 
		
		TextField_AN = new WebTextField();
		TextField_AN.setEditable(true);
		TextField_AN.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pt_info.add(TextField_AN);
		TextField_AN.setBounds(290, 10, 80, 20);
		 TextField_AN.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent arg0) {
				 if(TextField_AN.getText().trim().length()==7){
					 searchANTop(TextField_AN.getText().trim());
				 }
			 }
			 
		 });
	
		
		Label_HN = Setup.getLabel("HN", 13,4,SwingConstants.RIGHT);
		Label_HN.setBounds(390, 12, 20, 14);
		pt_info.add(Label_HN);
		
		TextField_HN = new WebTextField();
		TextField_HN.setEditable(false);
		TextField_HN.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TextField_HN.setText("");
		TextField_HN.setBounds(410, 10, 80, 20);
		pt_info.add(TextField_HN);

		
		Label_CID = Setup.getLabel("ID", 13,4,SwingConstants.RIGHT);
		Label_CID.setBounds(510, 12, 20, 14);
		pt_info.add(Label_CID);
		
		TextField_CID = new WebTextField();
		TextField_CID.setEditable(false);
		TextField_CID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		//TextField_CID.setBackground(new Color(229, 200, 179));
		TextField_CID.setBounds(530, 10, 150, 20);
		TextField_CID.setText("");
		pt_info.add(TextField_CID);

		
		Label_NAME = Setup.getLabel("ชื่อ", 13,4,SwingConstants.LEFT);
		Label_NAME.setBounds(4, 30, (width-((width*2)/10)-40), 30);
		pt_info.add(Label_NAME);
		
		textArea = new WebTextArea();
		textArea.setEditable(false);
		textArea.setForeground(new Color(255, 0, 0));
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textArea.setBackground(Setup.getColor());
		//textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(70, 70, 500, 200);
		//pt_info.add(textArea);
		
		scrollPaneDrugA = new WebScrollPane(textArea);
		scrollPaneDrugA.setBounds(70, 70, 500, 40);
		pt_info.add(scrollPaneDrugA);
		
		Label_Memo = Setup.getLabel("การแพ้ยา", 13,4,SwingConstants.LEFT);
		Label_Memo.setForeground(new Color(255, 0, 0));
		Label_Memo.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Memo.setBackground(Setup.getColor());
		Label_Memo.setBounds(10, 65, 55, 30);
		pt_info.add(Label_Memo);
		
		Label_Extra = Setup.getLabel("ข้อมูลเพิ่มเติม", 13,4,SwingConstants.LEFT);
		Setup.SetUnderline(Label_Extra);
		Label_Extra.setBounds(10, 110, 150, 30);
		Label_Extra.setBackground(Setup.getColor());
		pt_info.add(Label_Extra);
		
		Label_Extra1 = Setup.getLabel("", 13,4,SwingConstants.RIGHT);
		Setup.SetUnderline(Label_Extra1);
		Label_Extra1.setBounds(150, 110, 400, 30);
		Label_Extra1.setBackground(Setup.getColor());
		pt_info.add(Label_Extra1);
		
		Label_LOS = Setup.getLabel("LOS", 13,4,SwingConstants.RIGHT);
		Label_LOS.setHorizontalAlignment(SwingConstants.LEFT);
		Label_LOS.setBounds(697, 12, 200, 14);
		pt_info.add(Label_LOS);
		
		labelImage = new ScaledImageLabel();
        labelImage.setPreferredSize(new Dimension(140, 140));
        //labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
        labelImage.setBounds(width-(((width*2)/10)+200), 1, 140, 140);
        pt_info.add(labelImage);
        
        
        if(oUserInfo.GetPtAN().equals("")) {
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
                //clearValue();
                //getSmartcard();
               //sendImageHDFS(oUserInfo.GetPtCID());
            }
        });
        ButtonSmartcard.setBounds(700, 70, 150, 25);
        //pt_info.add(ButtonSmartcard);


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

        topMainLeftPanel = new WebPanel();
        mainLeftPanel.add(topMainLeftPanel, BorderLayout.NORTH);
        topMainLeftPanel.setBackground(Setup.getColor());
        topMainLeftPanel.setLayout(null);
        topMainLeftPanel.setPreferredSize(new Dimension((width*2)/10, 30));

        LabelListdata= Setup.getLabel(I18n.lang("label.customer"), 13,0,SwingConstants.CENTER);
        LabelListdata.setBounds(1,5,100,20);
        topMainLeftPanel.add(LabelListdata);

        accordion = new WebAccordion( );
        accordion.addPane ( new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")), " IPD", getIPD() );
        accordion.setMultiplySelectionAllowed ( false );
        
        
        mainLeftPanel.add(accordion, BorderLayout.CENTER);
        /*
        columnNames = new Vector<String>();
        columnNames.add(I18n.lang("label.order"));
        columnNames.add(I18n.lang("label.an"));
        columnNames.add(I18n.lang("label.customer.name"));
        for (int n = 3; n < 6; n++) {
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
                    String status=(String)getModel().getValueAt(convertRowIndexToModel(row), 3);
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
                clearValue();
                String select_an=table.getValueAt(row,1).toString().trim();
                oUserInfo.setPtAN(select_an);
                String name=table.getValueAt(row,2).toString().trim();
                oUserInfo.setPtName(name);
                String hn=table.getValueAt(row,3).toString().trim();
                oUserInfo.setPtHN(hn);
                String inscl="";
                //System.out.println(select_an+"*"+hn+"*"+name+"*");
                searchAN(select_an,hn);
                searchHN(hn);
                String pt_bed=table.getValueAt(row,4).toString().trim();
		        oUserInfo.setPtBed(pt_bed);
		        String pt_ward=table.getValueAt(row,5).toString().trim();
		        oUserInfo.setPtWard(pt_ward);
                oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
                
		        
		        if(oUserInfo.GetPtAN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
		        medReconPanel.setMid();
		        
            }
        });
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        scrollPane = new WebScrollPane(table);
        mainLeftPanel.add(scrollPane, BorderLayout.CENTER);

        getData();
        bottomPanel = new WebPanel();
        bottomPanel.setBackground(Setup.getColor());
        mainLeftPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(null);

        bottomPanel.setPreferredSize(new Dimension((width*2)/10, 30));
        ButtonRefresh = new WebButton(I18n.lang("label.refresh"));
        ButtonRefresh.setBackground(Setup.getColor());
        ButtonRefresh.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                getData();
                clearValue();
            }
        });
        ButtonRefresh.setBounds((((width*2)/10)/2-45), 2, 90, 25);
        bottomPanel.add(ButtonRefresh);
        */

    }
    public void setRight(){
    	midPanel = new WebPanel();
		midPanel.setLayout(new BorderLayout(0, 0));
		midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-160));
		//mainPanel.add(midPanel, BorderLayout.CENTER);
		
		tabbedPane = new WebTabbedPane();
        midPanel.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab(" Medication",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,medIPDPanel);
        
        tabbedPane.addTab(" Medication Reconciliation ",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,medReconPanel);
        tabbedPane.addTab("EMR ",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,mainHDFSOPDPanel);
        

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
		System.setProperty("hadoop.home.dir",Prop.getProperty("hadoop.home.dir"));
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
    public void searchANTop(String an){
    	clearValue();

        oUserInfo.setPtAN(an);
        String hn="",pt_ward="",pt_bed="",select_admtime="";
    	Connection conn;
        PreparedStatement stmt,stmt1,stmt2,stmt3;
        ResultSet rs,rs1,rs2,rs3;
       //String query_right="select  usedrightcode,hn from admmaster where an='"+an.trim()+"'" ;
        //String date= Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate());
        String query="select admmaster.usedrightcode,admmaster.hn,admbed.ward,admbed.bedno,admmaster.admdatetime from admmaster,admbed  where admmaster.an=admbed.an and admbed.OUTDATETIME  is null and admmaster.an='"+an+"'";
       
        conn=new DBmanager().getConnMSSql();
        try {
            stmt1 = conn.prepareStatement(query);
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
                if(rs1.getString(2) !=null){
                	hn=rs1.getString(2).trim();
                }
                if(rs1.getString(3) !=null){
                	pt_ward=rs1.getString(3).trim();
                }
                if(rs1.getString(4) !=null){
                	pt_bed=rs1.getString(4).trim();
                }
                if(rs1.getString(5) !=null){
                	System.out.println(rs1.getString(5).trim()+"---");
                	select_admtime=rs1.getString(5).trim().substring(0, 10);
                }
            }
            stmt1.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String name=Setup.getName(hn);
        oUserInfo.setPtName(name);
        oUserInfo.setPtHN(hn);
        String inscl="";
        //System.out.println(select_an+"*"+hn+"*"+name+"*");
        searchHN(hn);

        oUserInfo.setPtBed(pt_bed);

        oUserInfo.setPtWard(pt_ward);

        oUserInfo.setAdmtime(Setup.LOSInDayNow(select_admtime));
        oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
        //System.out.println("**************"+oUserInfo.GetPtWard()+"------"+oUserInfo.GetPtBed());
        if(oUserInfo.GetPtAN().equals("")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
		}else {
			//System.out.println(oUserInfo.GetPtHN());
			//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
		}
        medReconPanel.setMid();
        medIPDPanel.setMid();
    	
    }
    /*
    public void getData(){
        table.setModel(fetchDataIPD());
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80 );
        columnModel.getColumn(2).setPreferredWidth((width*2)/10-120);

        for (int n = 3; n < 6; n++) {
            columnModel.getColumn(n).setPreferredWidth(0);
            columnModel.getColumn(n).setMinWidth(0);
            columnModel.getColumn(n).setMaxWidth(0);
        }
        ((DefaultTableModel)table.getModel()).fireTableDataChanged();
    }
    */
    public  DefaultTableModel fetchDataIPD(){

        Connection conn ;
        PreparedStatement stmt;
        ResultSet rs;
        String date= Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate());
        String query="select admmaster.an,admmaster.hn,admbed.ward,admbed.bedno from admmaster,admbed  where admmaster.an=admbed.an and admbed.OUTDATETIME  is null and  admmaster.admdatetime between '"+date+" 00:00:00' and '"+date+" 23:59:59'  order by admmaster.admward";
        
        //String query= SetupSQL.getDataAdmit(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
        //String query="select an,firstname,lastname,hn,usedrightcode,outdatetime,BirthDateTime,sex,maritalstatus,initialnamecode,ref,bedno from in_view_pt_ipd  where   "+ptStatus+"  and reftype='01'  and suffix='0' "+sql_user+" order by bedno ";
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        try {
            conn = new DBmanager().getConnMSSql();
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
        return new DefaultTableModel(data, columnNames);
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
        for (int n = 3; n < 7; n++) {
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
		        String select_admtime=tableipd.getValueAt(row,6).toString().trim().substring(0, 10);
		        oUserInfo.setAdmtime(Setup.LOSInDayNow(select_admtime));
                oUserInfo.setPtLabel(" ชื่อ   "+oUserInfo.GetPtName()+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+"   [ NHSO   :  "+ inscl+" ]");
                //System.out.println("**************"+oUserInfo.GetPtWard()+"------"+oUserInfo.GetPtBed());
                if(oUserInfo.GetPtAN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
                medReconPanel.setMid();
                medIPDPanel.setMid();
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
        for (int n = 2; n < 6; n++) {
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
        String query="select admmaster.an,admmaster.hn,admbed.ward,admbed.bedno,admmaster.admdatetime from admmaster,admbed  where admmaster.an=admbed.an and admbed.OUTDATETIME  is null and admmaster.dischargedatetime is  null and admmaster.admward='"+ward+"'  order by admbed.bedno ";
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
                	vstring.add("0000");
                }
                if(rs.getString(3) !=null) {
                	vstring.add(rs.getString(3).trim());
                }else {
                	vstring.add("");
                }
                vstring.add(rs.getString(5).trim());

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
    public void clearValue(){
        oUserInfo.setPtAN("");
        oUserInfo.setPtName("");
        oUserInfo.setPtHN("");
        oUserInfo.setRightCode("");
        oUserInfo.setPtBed("");
        oUserInfo.setPtWard("");
        oUserInfo.setPtAge("");
        oUserInfo.setPtLabel("");
        oUserInfo.setPtCID("");
        oUserInfo.setMemo("");
        oUserInfo.setMemo1("");
        oUserInfo.setAdmtime("");
        mainHDFSOPDPanel.clear();
        medReconPanel.clearPanel();
        medIPDPanel.clearPanel();
    }
}
