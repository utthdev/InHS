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
package org.utt.app.ipd;

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

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
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
import org.utt.app.hdfs.MainHDFSOPDPanel;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.DateLabelFormatter;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;

public class IPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    String day="",month="",dateSearch="";
    String ptStatus=" and admbed.OUTDATETIME  is null";
    WebSplitPane split,splitR;
    WebTabbedPane tabbedPane;
    WebLabel Label_AN,Label_HN,Label_CID,Label_NAME,Label_Memo,Label_Extra,Label_Extra1,Label_LOS,labelImage;
    WebLabel Label_Date_l,LabelListdata;
    WebPanel leftPanel,midPanel,topMainPanel,bottomPanel,pt_info,jPanel,topLeftPanel,mainLeftPanel,topMainLeftPanel;
    WebTextField TextField_AN,TextField_HN,TextField_CID;
    WebTextArea textArea;
    WebButton ButtonRefresh,ButtonSmartcard;
    WebScrollPane scrollPane,scrollPaneDrugA;
    WebRadioButton PtStatus1,PtStatus2;
    WebTable table;
    ButtonGroup PtGroup = new ButtonGroup();
    Vector<String> columnNames;
    Vector<Vector<String>> data;
    JDatePickerImpl picker;
    
    NurseFormIPD nurseFormIPD;
    MainHDFSOPDPanel mainHDFSOPDPanel;
    LabIPDPanel labIPDPanel;
    
    public IPDPanel(ObjectData oUserInfo,int w,int h) {
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
        
        nurseFormIPD = new NurseFormIPD(oUserInfo,w,h);
        mainHDFSOPDPanel =new MainHDFSOPDPanel(oUserInfo,w,h);
        labIPDPanel =new LabIPDPanel(oUserInfo,w,h);
        
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
    public void initForm() {
        getData();
        nurseFormIPD.setComboBox();
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
        
        Label_AN = Setup.getLabel(I18n.lang("label.an"), 13,4,SwingConstants.RIGHT);
        Label_AN.setBounds(270, 12, 20, 14);
		pt_info.add(Label_AN);
		 
		
		TextField_AN = Setup.getTextField(12);
		TextField_AN.setEditable(false);
		pt_info.add(TextField_AN);
		TextField_AN.setBounds(290, 10, 80, 20);
	
		
		Label_HN = Setup.getLabel(I18n.lang("label.hn"), 13,4,SwingConstants.RIGHT);
		Label_HN.setBounds(390, 12, 20, 14);
		pt_info.add(Label_HN);
		
		TextField_HN = Setup.getTextField(12);
		TextField_HN.setEditable(false);
		TextField_HN.setText("");
		TextField_HN.setBounds(410, 10, 80, 20);
		pt_info.add(TextField_HN);

		
		Label_CID = Setup.getLabel(I18n.lang("label.id"), 13,4,SwingConstants.RIGHT);
		Label_CID.setBounds(510, 12, 20, 14);
		pt_info.add(Label_CID);
		
		TextField_CID = Setup.getTextField(12);
		TextField_CID.setEditable(false);
		//TextField_CID.setBackground(new Color(229, 200, 179));
		TextField_CID.setBounds(530, 10, 150, 20);
		TextField_CID.setText("");
		pt_info.add(TextField_CID);

		
		Label_NAME = Setup.getLabel(I18n.lang("label.name"), 13,4,SwingConstants.LEFT);
		Label_NAME.setBounds(4, 30, (width-((width*2)/10)-40), 30);
		pt_info.add(Label_NAME);
		
		textArea = new WebTextArea();
		textArea.setEditable(false);
		textArea.setForeground(new Color(255, 0, 0));
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textArea.setBackground(Setup.getColor());
		//textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(70, 70, 500, 200);
		
		
		scrollPaneDrugA = new WebScrollPane(textArea);
		scrollPaneDrugA.setBounds(70, 70, 500, 40);
		pt_info.add(scrollPaneDrugA);
		
		Label_Memo = Setup.getLabel(I18n.lang("label.drugallergy"), 13,4,SwingConstants.LEFT);
		Label_Memo.setForeground(new Color(255, 0, 0));
		Label_Memo.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Memo.setBackground(Setup.getColor());
		Label_Memo.setBounds(10, 65, 55, 30);
		pt_info.add(Label_Memo);
		
		Label_Extra = Setup.getLabel(I18n.lang("label.extra"), 13,4,SwingConstants.LEFT);
		Setup.SetUnderline(Label_Extra);
		Label_Extra.setBounds(10, 110, 150, 30);
		Label_Extra.setBackground(Setup.getColor());
		pt_info.add(Label_Extra);
		
		Label_Extra1 = Setup.getLabel("", 13,4,SwingConstants.RIGHT);
		Setup.SetUnderline(Label_Extra1);
		Label_Extra1.setBounds(150, 110, 400, 30);
		Label_Extra1.setBackground(Setup.getColor());
		pt_info.add(Label_Extra1);
		
		Label_LOS = Setup.getLabel(I18n.lang("label.los"), 13,4,SwingConstants.RIGHT);
		Label_LOS.setHorizontalAlignment(SwingConstants.LEFT);
		Label_LOS.setBounds(697, 12, 200, 14);
		pt_info.add(Label_LOS);
		
		labelImage = new ScaledImageLabel();
        labelImage.setPreferredSize(new Dimension(140, 140));
        labelImage.setBounds(width-(((width*2)/10)+200), 1, 140, 140);
        pt_info.add(labelImage);
        
        
        if(oUserInfo.GetPtAN().equals("")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
		}else {
			//System.out.println(oUserInfo.GetPtHN());
			//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
		}      
        
        ButtonSmartcard = new WebButton(I18n.lang("text.readsmartcard"));
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
        //
        PtStatus1 = new WebRadioButton("All");
        PtStatus1.setBackground(Setup.getColor());
		PtStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus=" and (admbed.OUTDATETIME  is null or  admbed.OUTDATETIME between '"+Setup.DateInDBMSSQL(dateSearch)+" 00:00:00' and '"+Setup.DateInDBMSSQL(dateSearch)+" 23:59:59') ";
							}
		});
		PtGroup.add(PtStatus1);
		PtStatus1.setBounds(100, 5, 40, 25);
		topMainLeftPanel.add(PtStatus1);
		
		PtStatus2 = new WebRadioButton("Active");
		PtStatus2.setBackground(Setup.getColor());
		PtStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ptStatus="and admbed.OUTDATETIME  is null";
			}
		});
		PtGroup.add(PtStatus2);
		PtStatus2.setSelected(true);
		PtStatus2.setBounds(143, 5, 100, 25);
		topMainLeftPanel.add(PtStatus2);
        //
        columnNames = new Vector<String>();
        columnNames.add(I18n.lang("label.order"));
        columnNames.add(I18n.lang("label.bedno"));
        columnNames.add(I18n.lang("label.customer.name"));
        for (int n = 3; n < 7; n++) {
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
                String select_an=table.getValueAt(row,4).toString().trim();
		        oUserInfo.setPtAN(select_an);
		        String name=table.getValueAt(row,2).toString().trim();
		        oUserInfo.setPtName(name);
		        String hn=table.getValueAt(row,6).toString().trim();
		        oUserInfo.setPtHN(hn);
		        
		        searchAN(select_an,hn);
        
		        String pt_bed=table.getValueAt(row,1).toString().trim();
		        oUserInfo.setPtBed(pt_bed);

		        String inscl="";
		        oUserInfo.setPtLabel(" ชื่อ   "+name+"     อายุ .. "+oUserInfo.GetPtAge()+"     :สิทธิ.. "+oUserInfo.GetRightCode()+" [ NHSO   :  "+ inscl+" ]");		        
		        String select_admtime=table.getValueAt(row,5).toString().trim().substring(0, 10);
		        oUserInfo.setAdmtime(Setup.LOSInDayNow(select_admtime));
		        
		        
		        if(oUserInfo.GetPtAN().equals("")) {
		        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
				}else {
					//System.out.println(oUserInfo.GetPtHN());
					//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
				}
		        
            }
        });
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        scrollPane = new WebScrollPane(table);
        mainLeftPanel.add(scrollPane, BorderLayout.CENTER);


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
                nurseFormIPD.setInit();
                clearValue();
            }
        });
        ButtonRefresh.setBounds((((width*2)/10)/2-45), 2, 90, 25);
        bottomPanel.add(ButtonRefresh);
        
    }
    public void setRight(){
    	midPanel = new WebPanel();
		midPanel.setLayout(new BorderLayout(0, 0));
		midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-160));
		
		tabbedPane = new WebTabbedPane();
        midPanel.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab(" Nurse FORM ",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,nurseFormIPD);
        tabbedPane.addTab("EMR ",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,mainHDFSOPDPanel);
        tabbedPane.addTab("LAB" ,new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")),labIPDPanel);
        
    }
    public void getData() {
    	table.setModel(fetchDataIPD());
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80 );
        columnModel.getColumn(2).setPreferredWidth((width*2)/10-120);

        for (int n = 3; n < 7; n++) {
            columnModel.getColumn(n).setPreferredWidth(0);
            columnModel.getColumn(n).setMinWidth(0);
            columnModel.getColumn(n).setMaxWidth(0);
        }
        ((DefaultTableModel)table.getModel()).fireTableDataChanged();
    }
    public  DefaultTableModel fetchDataIPD(){
    	String[] parts=null;
        parts = InApp.userappcode.split("\\,");
        String sql_user1="",sql_user="";
        if(parts.length==1){
            for(int i=0;i<parts.length;i++){
                sql_user1=" and admmaster.admward='"+parts[i]+"'";
            }
            sql_user=sql_user1;
        }
        else if(parts.length==0){
            sql_user="";
        }
        else if(parts.length>1){
            sql_user1=" and admmaster.admward in (";
            for(int i=0;i<parts.length;i++){
                sql_user1+="'"+parts[i]+"',";
            }
            sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
        }
        Connection conn ;
        PreparedStatement stmt;
        ResultSet rs;
        String query= SetupSQL.getListDataIPD(ptStatus, sql_user);
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        try {
            conn = new DBmanager().getConnMSSql();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            int p=1;
            while (rs.next()) {
                String pt_status="0";
                final Vector<String> vstring = new Vector<String>();
                vstring.add(" "+Integer.toString(p) );
                String bedno="";
                if(rs.getString(4)!=null){
                    bedno=rs.getString(4);
                }
                else{
                    bedno="0000";
                }
                vstring.add(bedno);
                vstring.add(SetupSQL.getName(rs.getString(2).trim()));
                if(rs.getString(3)!=null){
                    pt_status="1";
                }
                vstring.add(pt_status);
                vstring.add(rs.getString(1));
                vstring.add(rs.getString(5));
                vstring.add(rs.getString(2));
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
    public void searchAN(String an,String hn){
    	Connection conn;
		PreparedStatement stmt,stmt1,stmt2,stmt3;
		ResultSet rs,rs1,rs2,rs3;
		
		String query_db=SetupSQL.getDOB(hn); 
		String query_right=SetupSQL.getRight(an); 
		String query_ref=SetupSQL.getCID(hn) ; 
		
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
			
			rs1 = stmt1.executeQuery();
			while (rs1.next()) {
				String  right="";				
				if(rs1.getString(1) !=null){
					for(int i=0;i<IPDFrame.rightname.length;i++){
						if(rs1.getString(1).trim().equals(IPDFrame.rightname[i][0])){
							right=IPDFrame.rightname[i][1];
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
			String sql_allergy=SetupSQL.memo;
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
			
			for(int i=0;i<IPDFrame.hn_z515.length;i++){
				if(hn.equals(IPDFrame.hn_z515[i])){
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
        labIPDPanel.clearPanel();
    }

}
