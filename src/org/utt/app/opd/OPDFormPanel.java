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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.ui.JCheckBoxTree;
import org.utt.app.util.ComboItem;
import org.utt.app.util.I18n;
import org.utt.app.util.PrintIPDLabLabel;
import org.utt.app.util.PrintOPDLabLabel;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.extended.tree.WebCheckBoxTree;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.tree.UniqueNode;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeModel;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;

public class OPDFormPanel extends WebPanel implements Observer,ActionListener{
	boolean acPrint = false;
	ObjectData oUserInfo;
    int width,height;
    Vector<String> columnNamesOPDform;
    String nameBefore="";
    GregorianCalendar day = new GregorianCalendar();
    
    WebPanel LeftSection,MainSection,MiddleSection,mid2,mid1,mid3,mid4,mid5,middle,bottomPanelForm,LeftNorthSection,LeftMidSection;
    WebSplitPane split;
    WebAccordion accordion,accordion_med;
    WebTable tableOPDform;
    WebScrollPane scrollPaneOPDform,scrollPaneImg;
    WebLabel Label_IMG;
    WebButton ButtonRefreshForm,ButtonIn,ButtonOut;
    
    String line_med_opd="",line_med_ipd="",line_opd="",line_ipd="";
    
    String [][] shortname=null ;
    String [][] engname=null ;
    String [][] thainame=null ;
    
    WebComboBox cbRow1,cbRow2;
    String vn1="",vn2="",age1="",age2="",hn1="",hn2="",name1="",name2="";
    String runnoQ="";
    WebLabel Label_Info_in,Label_Info_out;
    
	public OPDFormPanel(ObjectData oUserInfo, int w, int h) {
		this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
		split.setDividerLocation(200);
		
		initDrugName();
		
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(200, height-175));
		split.setLeftComponent(LeftSection);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		LeftNorthSection = new WebPanel();
		LeftNorthSection.setPreferredSize(new Dimension(200, 80));
		LeftSection.add(LeftNorthSection, BorderLayout.NORTH);
		LeftNorthSection.setLayout(new BorderLayout(0, 0));
		LeftNorthSection.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		 
		
		LeftMidSection = new WebPanel();
		LeftMidSection.setPreferredSize(new Dimension(200, 30));
		LeftNorthSection.add(LeftMidSection, BorderLayout.CENTER);
		LeftMidSection.setLayout(null);
		
		WebLabel label_infotime = new WebLabel("บันทึกเวลาการรับริการ");
		label_infotime.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_infotime.setHorizontalAlignment(SwingConstants.CENTER);
		label_infotime.setBounds(10, 1, 200, 23);
		LeftMidSection.add(label_infotime);
		 
		
		WebLabel label = new WebLabel("แบบฟอร์ม");
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		LeftNorthSection.add(label, BorderLayout.SOUTH);
		
		accordion = new WebAccordion ( );
		accordion.addPane ( null, "OPD", getFormOPD() );
		//accordion.addPane ( null, "Request", getFormReq() );
		accordion.setMultiplySelectionAllowed ( false );		
		LeftSection.add(accordion, BorderLayout.CENTER);
		
		MainSection = new WebPanel();
		MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-200, height-175));
		MainSection.setLayout(new BorderLayout(0, 0));
		split.setRightComponent(MainSection);
		
		MiddleSection = new WebPanel();
		MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10-450), height-175));
		 
		MainSection.add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid2 = new WebPanel();
		mid2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		//mid2.setBackground(new Color(206,203,208));
		mid2.setPreferredSize(new Dimension(width-((width*2)/10-480), height-215));
		MiddleSection.add(mid2, BorderLayout.CENTER);
		mid2.setLayout(new BorderLayout(0, 0));

		//scrollPaneImg = new JScrollPane(Label_IMG);
		 
		mid1 = new WebPanel();
		mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		 
		
		Label_Info_in = new WebLabel(" ");
		ImageIcon img_in = new ImageIcon(getClass().getClassLoader().getResource("images/home.png"));
		Label_Info_in.setIcon(img_in);
		Label_Info_in.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info_in.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info_in.setBounds(20, 18, 100, 15);
		mid1.add(Label_Info_in);
		
		Label_Info_out = new WebLabel(" ");
		ImageIcon img_out = new ImageIcon(getClass().getClassLoader().getResource("images/delivery.png"));
		Label_Info_out.setIcon(img_out);
		Label_Info_out.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info_out.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info_out.setBounds(120, 18, 200, 15);
		mid1.add(Label_Info_out);
		ButtonIn = new WebButton("In");
		ButtonIn.setEnabled(true);
		
		ButtonIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {				 
				ButtonOut.setEnabled(true);
				Connection con;
				PreparedStatement stmt,stmt1;
				ResultSet rs1;
				String id="";
				String sql_hn="select id from visit where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' ";
				String sql_in = "insert into txlog (visitdate,hn,vn,txdatetime,txtype,servroom,txip,dupdate,status) values ('"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"','"+oUserInfo.GetPtHN()+"','"+oUserInfo.GetPtVN()+"','"+Setup.GetDateTimeNow()+"','1','"+oUserInfo.GetPtCliniccode().trim()+"','"+InApp.ip+"','"+Setup.GetDateTimeNow()+"','1') ";
				try {
					con = new DBmanager().getConnMySql();
					//System.out.println(id+"******************"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"----"+oUserInfo.GetPtHN()+"---****--"+oUserInfo.GetPtCliniccode()+"-----"+Setup.GetDateTimeNow());
					stmt = con.prepareStatement(sql_in);
					int rs_save =stmt.executeUpdate();
					stmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				 
				Label_Info_in.setText(getTxDatetimeIn(oUserInfo.GetPtHN()));
				Label_Info_out.setText(getTxDatetimeOut(oUserInfo.GetPtHN()));
				
				ButtonIn.setEnabled(false);
			}
			
		});
		ButtonIn.setBounds(10, 25, 50, 23);
		 
		LeftMidSection.add(ButtonIn);
		
		ButtonOut = new WebButton("Out");
		ButtonOut.setEnabled(false);
		ButtonOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ButtonIn.setEnabled(false);
				Connection con;
				PreparedStatement stmt,stmt1;
				ResultSet rs1;
				String id="";
				String sql_hn="select id from visit where hn='"+oUserInfo.GetPtHN()+"'  and visitdate='"+Setup.DateInDBMSSQLno(oUserInfo.GetPtVisitdate())+"' ";
				String sql_out = "insert into txlog (visitdate,hn,vn,txdatetime,txtype,servroom,txip,dupdate,status) values ('"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"','"+oUserInfo.GetPtHN()+"','"+oUserInfo.GetPtVN()+"','"+Setup.GetDateTimeNow()+"','2','"+oUserInfo.GetPtCliniccode().trim()+"','"+InApp.ip+"','"+Setup.GetDateTimeNow()+"','1') ";
				try {
					con = new DBmanager().getConnMySql();
					stmt = con.prepareStatement(sql_out);
					int rs_save =stmt.executeUpdate();
					stmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Label_Info_in.setText(getTxDatetimeIn(oUserInfo.GetPtHN()));
				Label_Info_out.setText(getTxDatetimeOut(oUserInfo.GetPtHN()));
				ButtonOut.setEnabled(false);	

			}
		});
		ButtonOut.setBounds(60, 25, 50, 23);
		LeftMidSection.add(ButtonOut);
		
		add(split, BorderLayout.CENTER);
	}
	public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
	public WebPanel getFormOPD(){
		WebPanel wp = new WebPanel();
		wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension((width*2)/10,75));
		
		columnNamesOPDform = new Vector<String>();		
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		columnNamesOPDform.add("");
		
		Vector<Vector<String>> dataOPDform = new Vector<Vector<String>>();
		DefaultTableModel modelOPDform = new DefaultTableModel(dataOPDform, columnNamesOPDform){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		tableOPDform = new WebTable(modelOPDform){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
				}
				return c;
			}
		};		
		tableOPDform.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String clinic_name="",clinic_scan="";
				int row_tableOPDform = tableOPDform.rowAtPoint(e.getPoint());
		        int col_tableOPDform = tableOPDform.columnAtPoint(e.getPoint());
		        String form_code=tableOPDform.getValueAt(row_tableOPDform,1).toString().trim();
		        String typeofprint=tableOPDform.getValueAt(row_tableOPDform,5).toString().trim();
		        String form_clinic_name=tableOPDform.getValueAt(row_tableOPDform,4).toString().trim();
		        String page=tableOPDform.getValueAt(row_tableOPDform,3).toString().trim();
		        String clinic_pt=oUserInfo.GetPtCliniccode();
		        for(int i=0;i<OPDFrame.clinicname.length;i++){
					if(clinic_pt.equals(OPDFrame.clinicname[i][0])){
						clinic_name=OPDFrame.clinicname[i][1];
					}
				}
				for(int i=0;i<OPDFrame.clinicno.length;i++){
					if(clinic_pt.equals(OPDFrame.clinicno[i][0])){
						clinic_scan=OPDFrame.clinicno[i][1];
					}
				}
				panelreport(form_code,typeofprint,clinic_name,clinic_scan,form_clinic_name,page);	
				//System.out.println(">>>>"+oUserInfo.GetPtWard());
				//getGFR(oUserInfo.GetPtHN());
			}
		});
		tableOPDform.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tableOPDform.setRowHeight(25);		
		tableOPDform.setFillsViewportHeight(true);
		scrollPaneOPDform = new WebScrollPane(tableOPDform);
		getDataForm();
		wp.add(scrollPaneOPDform, BorderLayout.CENTER);
		
		bottomPanelForm = new WebPanel();
        bottomPanelForm.setBackground(Setup.getColor());
        wp.add(bottomPanelForm, BorderLayout.SOUTH);
        bottomPanelForm.setLayout(null);

        bottomPanelForm.setPreferredSize(new Dimension((width*2)/10, 20));
        ButtonRefreshForm = new WebButton(I18n.lang("label.refresh"));
        ButtonRefreshForm.setBackground(Setup.getColor());
        ButtonRefreshForm.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefreshForm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	getDataForm();
            }
        });
        ButtonRefreshForm.setBounds(55, 1, 90, 20);
        bottomPanelForm.add(ButtonRefreshForm);
		
		return wp;
		
	}
	public void getDataForm(){
		tableOPDform.setModel(fetchDataForm());
		TableColumnModel columnModelOPDform = tableOPDform.getColumnModel();
		columnModelOPDform.getColumn(0).setPreferredWidth(40);
		columnModelOPDform.getColumn(1).setPreferredWidth(0);
		columnModelOPDform.getColumn(1).setMinWidth(0);
		columnModelOPDform.getColumn(1).setMaxWidth(0);
		columnModelOPDform.getColumn(2).setPreferredWidth(360);
		for (int n = 2; n < 5; n++) {	
			columnModelOPDform.getColumn(n+1).setPreferredWidth(0);
			columnModelOPDform.getColumn(n+1).setMinWidth(0);
			columnModelOPDform.getColumn(n+1).setMaxWidth(0);
		}

		((DefaultTableModel)tableOPDform.getModel()).fireTableDataChanged();
	}
	public  DefaultTableModel fetchDataForm(){
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and clinic='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length==0){
			sql_user="";
		}
		else if(parts.length>1){
			sql_user1="and clinic in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
		}
		Connection conn;
		PreparedStatement stmt;
		ResultSet rs;
		
		Vector<Vector<String>> dataForm = new Vector<Vector<String>>();
		String sql_formopd = "select form_code,form_name,page,form_name_print,typeofprint  from formopd where type='1'  "+sql_user+"  order by clinic,orderpage";
		try {
			conn=new DBmanager().getConnMySql();
			stmt = conn.prepareStatement(sql_formopd);
			rs = stmt.executeQuery();
			int p=1;
			while (rs.next()) {
				final Vector<String> vstringForm = new Vector<String>();
				vstringForm.add(" "+Integer.toString(p) );
				vstringForm.add(rs.getString(1).trim());	           
	        	vstringForm.add(rs.getString(2));
	        	if(rs.getString(3) ==null){
	        		vstringForm.add("88");
	        	}else{
	        	vstringForm.add(rs.getString(3).trim());
	        	}
	        	if(rs.getString(4) ==null){
	        		vstringForm.add("");
	        	}else{
	        	vstringForm.add(rs.getString(4).trim());
	        	}
	        	if(rs.getString(5) ==null){
	        		vstringForm.add("1");
	        	}else{
	        	vstringForm.add(rs.getString(5).trim());
	        	}
				dataForm.add(vstringForm);
				p++;
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new DefaultTableModel(dataForm, columnNamesOPDform);
	}
	public void panelreport(String form_code,String typeofprint,String clinic_name,String clinic_scan,String form_clinic_name,String page) {
		if(form_code.equals("00208")){
			mid1.removeAll();
			clearPanel();
			
			Label_IMG = new WebLabel("");
			Label_IMG.setHorizontalAlignment(SwingConstants.CENTER);
			
			scrollPaneImg = new WebScrollPane(Label_IMG);
			mid2.add(scrollPaneImg, BorderLayout.CENTER);
			
			WebButton ButtonView = new WebButton("View");
			ButtonView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						view();	 
				}
			});
			ButtonView.setBounds(20, 4, 89, 23);
			mid1.add(ButtonView);
			
			WebButton ButtonPrint = new WebButton("print");
			ButtonPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createreport(form_code,"2",clinic_name,clinic_scan,form_clinic_name,page);
					ClearFile();					
				}
			});
			ButtonPrint.setBounds(110, 4, 89, 23);
			mid1.add(ButtonPrint);
			
			mid1.validate();
			mid1.repaint();
			mid2.revalidate();
			mid2.repaint();	
			MiddleSection.revalidate();
			MiddleSection.repaint();
		}
		else if(form_code.equals("10027")){
			mid1.removeAll();
			clearPanel();
			
			Label_IMG = new WebLabel("");
			Label_IMG.setHorizontalAlignment(SwingConstants.CENTER);
			
			scrollPaneImg = new WebScrollPane(Label_IMG);
			mid2.add(scrollPaneImg, BorderLayout.CENTER);
			
			WebButton ButtonView = new WebButton("View");
			ButtonView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						view();	 
				}
			});
			ButtonView.setBounds(20, 4, 89, 23);
			mid1.add(ButtonView);
			
			WebButton ButtonPrint = new WebButton("print");
			ButtonPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createreport(form_code,"2",clinic_name,clinic_scan,form_clinic_name,page);
					ClearFile();					
				}
			});
			ButtonPrint.setBounds(110, 4, 89, 23);
			mid1.add(ButtonPrint);
			
			mid1.validate();
			mid1.repaint();
			mid2.revalidate();
			mid2.repaint();	
			MiddleSection.revalidate();
			MiddleSection.repaint();
		}
		else if(form_code.equals("00299")) {
			
			WebPanel mid22 = new WebPanel();
			mid22.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			//mid2.setBackground(new Color(206,203,208));
			mid22.setPreferredSize(new Dimension(width-((width*2)/10-480), height-215));
			mid2.add(mid22, BorderLayout.CENTER);
			mid22.setLayout(null);

			cbRow1 =new WebComboBox(); 
			cbRow1.setFont(new Font("Tahoma", Font.PLAIN, 13));
			cbRow2 =new WebComboBox();
			cbRow2.setFont(new Font("Tahoma", Font.PLAIN, 13));
			
			setInitSticker();
			
			//list of patient
			WebLabel Label_Row1 = Setup.getLabel("Row 1", 13,4,SwingConstants.CENTER);
			Label_Row1.setBounds(10,35,100,20);
			mid22.add(Label_Row1);
			WebLabel Label_Row2 = Setup.getLabel("Row 2", 13,4,SwingConstants.CENTER);
			Label_Row2.setBounds(10,75,100,20);
			mid22.add(Label_Row2);
			
			
			cbRow1.setSize(300, 20);
			cbRow2.setSize(300, 20);
			//cbRow1.setSelectedIndex(0);
			cbRow1.setBounds(120, 35, 300, 20);
			cbRow1.addActionListener(this);	
			mid22.add(cbRow1);
			
			//cbRow2.setSelectedIndex(0);
			cbRow2.setBounds(120, 75, 300, 20);
			cbRow2.addActionListener(this);	
			mid22.add(cbRow2);
			
			
			WebLabel Label_Setting = Setup.getLabel("หมายเหตุ: ขอบกระดาษด้านบนที่ห่างจากสติ๊กเกอร์แถวแรก ไม่ควรเกิน 2.5 มิลลิเมตร", 13,4,SwingConstants.CENTER);
			Label_Setting.setBounds(22,5,500,20);
			mid22.add(Label_Setting);
			
			WebButton ButtonPrint = new WebButton("Print");				 
			ButtonPrint.setBounds(450,35,120,60);
			mid22.add(ButtonPrint);
			ButtonPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PrintOPDLabLabel pr1= new PrintOPDLabLabel();	
					pr1.setName1(name1);
					pr1.setHN1(hn1);
					pr1.setVN1(vn1);
					pr1.setAge1(age1);
					pr1.setDate(oUserInfo.GetPtVisitdate());
					pr1.setClinicNameShort(oUserInfo.GetPtCliniccode());
					//pr1.setPosition("1");
					
					pr1.setName2(name2);
					pr1.setHN2(hn2);
					pr1.setVN2(vn2);
					pr1.setAge2(age2);
					

					
					
					pr1.showPrinter();
				}
			});
			ButtonPrint.setMnemonic(KeyEvent.VK_V);
			mid1.validate();
			mid1.repaint();
			mid2.revalidate();
			mid2.repaint();	
			MiddleSection.revalidate();
			MiddleSection.repaint();
		}
		else if(form_code.equals("10011") || form_code.equals("10019") || form_code.equals("10001") || form_code.equals("10012") || form_code.equals("00204") || form_code.equals("00214") || form_code.equals("10016")|| form_code.equals("10010") || form_code.equals("10006") || form_code.equals("10007") || form_code.equals("10020")  || form_code.equals("10026") || form_code.equals("00301") || form_code.equals("10021") || form_code.equals("10023") || form_code.equals("00218") || form_code.equals("10025") || form_code.equals("10028")) {
			clearPanel();
			line_opd="";
        	line_med_opd="";
        	line_ipd="";
        	line_med_ipd="";
			JCheckBoxTree  tree1 = new JCheckBoxTree(width-((width*2)/10-480), 5000);
			tree1.setPreferredSize(new Dimension((width*2)/10-480, 5000));
			tree1.setModel(getDefaultTreeModel(oUserInfo.GetPtHN())); 
		    WebScrollPane treeScroll1 = new WebScrollPane ( tree1 );
		    treeScroll1.setPreferredSize ( new Dimension (width-((width*2)/10-480), 5000 ) );
		    tree1.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
		  
	            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
	            	line_opd="";
	            	line_med_opd="";
	                TreePath[] paths = tree1.getCheckedPaths();
	                //System.out.println(paths.toString()+"<<");
	                for (TreePath tp : paths) {
	                	 //System.out.println("===>"+tp.getLastPathComponent()+"===>"+tp.getPathCount());
	                	if(tp.getPathCount()==2) {
	                		 //System.out.println("วันที่เดิม "+tp.getLastPathComponent());
	                		 line_opd="วันที่เดิม "+tp.getLastPathComponent();
	                	 } 
	                	 if(tp.getPathCount()==3) {
	                		 System.out.println("===>"+tp.getLastPathComponent());
	                		 line_med_opd+=tp.getLastPathComponent()+"@";
	                	 }                                  
	                }
	            }           
	        }); 		    
			
			JCheckBoxTree  tree2 = new JCheckBoxTree(width-((width*2)/10-480), height-160);
			tree2.setPreferredSize(new Dimension((width*2)/10-480, height-160));
			tree2.setModel(getDefaultTreeModelIPD(oUserInfo.GetPtHN())); 
		    WebScrollPane treeScroll2 = new WebScrollPane ( tree2 );
		    treeScroll2.setPreferredSize ( new Dimension (width-((width*2)/10-480), height-160 ) );
		    tree2.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
	            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
	            	line_ipd="";
	            	line_med_ipd="";
	                TreePath[] paths = tree2.getCheckedPaths();
	                for (TreePath tp : paths) {
	                	 //System.out.println("===>"+tp.getLastPathComponent()+"===>"+tp.getPathCount());
	                	if(tp.getPathCount()==2) {
	                		 //System.out.println(tp.getLastPathComponent());
	                		 line_ipd=""+tp.getLastPathComponent();	                		  
	                	 }
	                	 if(tp.getPathCount()==3) {
	                		 //System.out.println("===>"+tp.getLastPathComponent());
	                		 line_med_ipd+=tp.getLastPathComponent()+"@";
	                	 }                                  
	                }
	            }           
	        }); 
		    /*
		    JCheckBoxTree  tree3 = new JCheckBoxTree(width-((width*2)/10-480), height-160);
			tree3.setPreferredSize(new Dimension((width*2)/10-480, height-160));
			tree3.setModel(getDefaultTreeModelLab(oUserInfo.GetPtHN())); 
		    WebScrollPane treeScroll3 = new WebScrollPane ( tree3 );
		    treeScroll3.setPreferredSize ( new Dimension (width-((width*2)/10-480), height-160 ) );
		    tree3.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
	            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
	            	line_ipd="";
	            	line_med_ipd="";
	                TreePath[] paths = tree3.getCheckedPaths();
	                for (TreePath tp : paths) {
	                	 //System.out.println("===>"+tp.getLastPathComponent()+"===>"+tp.getPathCount());
	                	if(tp.getPathCount()==2) {
	                		 //System.out.println(tp.getLastPathComponent());
	                		 line_ipd=""+tp.getLastPathComponent();	                		  
	                	 }
	                	 if(tp.getPathCount()==3) {
	                		 //System.out.println("===>"+tp.getLastPathComponent());
	                		 line_med_ipd+=tp.getLastPathComponent()+"@";
	                	 }                                  
	                }
	            }           
	        }); 	
			*/
			accordion_med = new WebAccordion ( );			 
			accordion_med.addPane ( null, "Past Medicine OPD", treeScroll1 );
			accordion_med.addPane ( null, "Past Medicine IPD", treeScroll2);
			//accordion_med.addPane ( null, "LAB", treeScroll3);
			//accordion_med.setMultiplySelectionAllowed ( false );		
			mid2.add(accordion_med, BorderLayout.CENTER);
			
			WebButton ButtonPrint = new WebButton("print");
			ButtonPrint.addActionListener(new ActionListener() {
				
				@SuppressWarnings("unused")
				public void actionPerformed(ActionEvent e) {

					String filename=Setup.ConverttoScanDate(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()))+clinic_scan+"130001"+oUserInfo.GetPtHN().trim();
					
					String[] parts_opd=null;
					if(line_med_opd.equals("")) {
						 
			        }else {
			        	//System.out.println("*****"+line_med_opd);
			        	parts_opd = line_med_opd.split("\\@");
			        }
			         
			        String[] parts_ipd=null;
			        if(line_med_ipd.equals("")) {
			        	
			        }else {
			        	//System.out.println("*****"+line_med_ipd);
			        	parts_ipd = line_med_ipd.split("\\@");
			        				        }
			         
			        String test="",lab="",date_print="";
			        if(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim().equals(Setup.GetDateNow().trim())){
			    		date_print=Setup.ConvertDateTimePrint(day)+" น. ";
			    	}else{
			    		date_print=Setup.ShowThaiDate1(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim())+" เวลา.......................น.";
			    	}
			        String msg1="";
			        msg1=getMsg(oUserInfo.GetPtHN())+"\n";
			        if(msg1.equals("")){
			        	
			        }else {
			        	test+=getMsg(oUserInfo.GetPtHN())+"\n";
			        }
			         
			        if(parts_opd !=null) {
			        	if(parts_opd.length>=1) {
				        	 test+=line_opd+"\n";
				        	 Arrays.sort(parts_opd);
				        	 for(int i=0;i<parts_opd.length;i++) {
									//System.out.println(i+1+"."+parts_opd[i]);
									test+=i+1+"."+parts_opd[i].substring(parts_opd[i].indexOf(".")+1)+"\n";
									//if(InApp.username.equals("med0143")) {
									//	test+="...................................................................................................................\n";
									//}
								}
				        }
			        }
			         
			        if(parts_ipd !=null) {
			        	if(parts_ipd.length>=1) {
				        	test+=line_ipd+"\n";
				        	 for(int i=0;i<parts_ipd.length;i++) {
				        		 //System.out.println(i+1+"."+parts_ipd[i]);
									test+=i+1+"."+parts_ipd[i]+"\n";
								}
				        }
			        }
			         
			        	        
			        String confFile="report/"+form_code.trim()+".jrxml";
			        JasperReportBuilder report = DynamicReports.report(); 
					report.setPageFormat(590, 840, PageOrientation.PORTRAIT);
					InputStream in = this.getClass().getClassLoader().getResourceAsStream(confFile );
					try {
						report.setTemplateDesign(in);
						report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
						report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
						report.setParameter("line2",form_clinic_name);
						report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
						report.setParameter("line4","วันที่ "+date_print +"  "+oUserInfo.GetRightCode());
						report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
						report.setParameter("line6",oUserInfo.GetMemo1());
						report.setParameter("line7",form_code+".jrxml");
						report.setParameter("labresult",lab);
						 
						report.setParameter("gfr",getGFR(oUserInfo.GetPtHN().trim()));
						report.setParameter("med1",test);
						report.setParameter("barcode",filename);
						report.setDataSource(new JREmptyDataSource());
						//report.print(true);
						report.show(acPrint);
					} catch (DRException e1) {
						e1.printStackTrace();
					}
					line_opd="";
		        	line_med_opd="";
		        	line_ipd="";
		        	line_med_ipd="";
				}
			});
			ButtonPrint.setBounds(320, 4, 89, 23);
			mid1.add(ButtonPrint);		
			mid1.add(Label_Info_in);
			mid1.add(Label_Info_out);
			mid1.validate();
			mid1.repaint();		
			mid2.revalidate();
			mid2.repaint();	
			MiddleSection.revalidate();
			MiddleSection.repaint();
		}
		/*
		else if(form_code.equals("00201")) {
			
			
		
			WebButton ButtonPrint = new WebButton("print");
			ButtonPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createreport(form_code,"2",clinic_name,clinic_scan,form_clinic_name,page);					
				}
			});
			ButtonPrint.setBounds(10, 6, 89, 23);
			mid1.add(ButtonPrint);
			
			mid1.validate();
			mid1.repaint();
			mid2.revalidate();
			mid2.repaint();	
			MiddleSection.revalidate();
			MiddleSection.repaint();	
		}
		*/
		else {
			createreport(form_code,typeofprint,clinic_name,clinic_scan,form_clinic_name,page);

		}
	}
	public WebTreeModel getDefaultTreeModel (String hn){
		UniqueNode root = new UniqueNode ( "HN :"+hn );
		UniqueNode parent=null;
		String query =  "select distinct vnmst.visitdate,vnmst.vn,vnpres.clinic,vnpres.suffix from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate > '"+Setup.GetDateMo(12)+"'  and vnmst.hn=? order by vnmst.visitdate desc ";
		//String query_d =  "select  stock_master.shortname,vnmedicine.dosecode,vnmedicine.dosetype,vnmedicine.doseunitcode,vnmedicine.doseqtycode ,vnmedicine.auxlabel1,vnmedicine.RETURNDRUGREASON,vnmedicine.paidamt,vnmedicine.auxlabel2,vnmedicine.auxlabel3,vnmedicine.dosememo from vnmedicine,stock_master where "
		//		+ " vnmedicine.stockcode=stock_master.stockcode and vnmedicine.vn=? and vnmedicine.visitdate=?  and vnmedicine.cxldatetime is null  order by vnmedicine.subsuffix ";
		//String query_d =  "select  stockcode,dosecode,dosetype,doseunitcode,doseqtycode ,auxlabel1,RETURNDRUGREASON,paidamt,auxlabel2,auxlabel3,dosememo from vnmedicine where "
		//		+ " vn=? and visitdate=?  and cxldatetime is null  order by subsuffix ";
		 
		Connection conn ;
		PreparedStatement stmt,stmt1,stmt2,stmt3,stmt4,stmt11 ;		
		conn=new DBmanager().getConnMSSql();
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1,hn);
	        ResultSet rs = stmt.executeQuery();
	        int p=1;
	        while (rs.next()) {
	        	String drug_date=rs.getString(1).trim().substring(0, 10);
	        	String vn=rs.getString(2).trim();
	        	String clinic=rs.getString(3).trim();
	        	String suffix=rs.getString(4).trim();
	        	parent = new UniqueNode (Setup.ShowThaiDate1(drug_date)+" [vn:"+vn+" ("+clinic+") ]");
	        	System.out.println(Setup.ShowThaiDate1(drug_date)+" [vn:"+vn+"]");
	        	String query_d =  "select  stockcode,dosecode,dosetype,doseunitcode,doseqtycode ,auxlabel1,RETURNDRUGREASON,paidamt,auxlabel2,auxlabel3,dosememo,subsuffix from vnmedicine where "
	    				+ "  vn=? and visitdate=? and suffix='"+suffix+"'  and cxldatetime is null  order by subsuffix ";
	    		
	        	stmt1 = conn.prepareStatement(query_d);
	        	stmt1.setString(1,vn.trim());
		        stmt1.setString(2,drug_date.trim());
	        	ResultSet rs1=stmt1.executeQuery();
	        	while (rs1.next()) {
	        		String drug="",ned="",dc="",dt="",dqc="",aux="",duc="",q="",aux2="",aux3="",dmemo="",subsuffix="";
	        		if(rs1.getString(1) !=null){
		        		//drug=rs1.getString(1).trim().substring(1);
		        		//drug=rs1.getString(1).trim();
		        		
		        		for(int i=0;i<shortname.length;i++){
							if(rs1.getString(1).trim().equals(shortname[i][0])){
								drug=shortname[i][1];
								break;
							}
							//System.out.println(i+1+". "+labunitname[i][0]);
						}
		        		if(drug.equals("")) {
		        			for(int i=0;i<engname.length;i++){
								if(rs1.getString(1).trim().equals(engname[i][0])){
									drug=engname[i][1];
									break;
								}
								//System.out.println(i+1+". "+labunitname[i][0]);
							}
		        		}
		        		
		        	}
	        		/*
	        		else if(rs1.getString(1) ==null){
	        			String query_d1="select  stock_master.englishname from vnmedicine,stock_master where "
	        					+ " vnmedicine.stockcode=stock_master.stockcode and vnmedicine.vn=? and vnmedicine.visitdate=?  and vnmedicine.cxldatetime is null  order by vnmedicine.subsuffix ";
	        			stmt11 = conn.prepareStatement(query_d1);
	    	        	stmt11.setString(1,vn.trim());
	    		        stmt11.setString(2,drug_date.trim());
	    	        	ResultSet rs11=stmt11.executeQuery();
	    	        	while (rs11.next()) {
	    	        		if(rs11.getString(1) !=null) {
	    	        			drug=rs11.getString(1).trim();
	    	        		}
	    	        		 
	    	        	}
	    	        	stmt11.close();
	        			
	        		}
	        		*/
	        		if(rs1.getString(2) !=null){
		        		for(int i=0;i<OPDFrame.getDoseCode().length;i++){
							if(rs1.getString(2).trim().equals(OPDFrame.getDoseCode()[i][0])){
								dc=OPDFrame.getDoseCode()[i][1];
							}
						}
		        	}
		        	if(rs1.getString(3) !=null){
		        		for(int i=0;i<OPDFrame.getDoseType().length;i++){
							if(rs1.getString(3).trim().equals(OPDFrame.getDoseType()[i][0])){
								dt=OPDFrame.getDoseType()[i][1];
							}
						}
		        	}
		        	if(rs1.getString(4) !=null){
		        		for(int i=0;i<OPDFrame.getDoseUnitCode().length;i++){
							if(rs1.getString(4).trim().equals(OPDFrame.getDoseUnitCode()[i][0])){
								duc=OPDFrame.getDoseUnitCode()[i][1];
							}
						}
		        	}
		        	if(rs1.getString(5) !=null){
		        		for(int i=0;i<OPDFrame.getQtyCode().length;i++){
							if(rs1.getString(5).trim().equals(OPDFrame.getQtyCode()[i][0])){
								dqc=OPDFrame.getQtyCode()[i][1];
							}
						}
		        	}
		        	if(rs1.getString(6) !=null){
		        		for(int i=0;i<OPDFrame.getAuxLabel1().length;i++){
							if(rs1.getString(6).trim().equals(OPDFrame.getAuxLabel1()[i][0])){
								aux=""+OPDFrame.getAuxLabel1()[i][1]+"";
							}
						}
		        	}
		        	if(rs1.getString(7) !=null){
		        		ned="NED เหตุผล="+rs1.getString(7).trim();
		        	}
		        	if(rs1.getString(8) !=null){
		        		q=rs1.getString(8).trim();
		        	}
		        	/*
		        	if(rs1.getString(9) !=null){
		        		for(int i=0;i<OPDFrame.getAuxLabel1().length;i++){
							if(rs1.getString(9).trim().equals(OPDFrame.getAuxLabel1()[i][0])){
								aux2=""+OPDFrame.getAuxLabel1()[i][1]+"";
							}
						}
		        	}
		        	*/
		        	if(rs1.getString(10) !=null){
		        		for(int i=0;i<OPDFrame.getAuxLabel1().length;i++){
							if(rs1.getString(10).trim().equals(OPDFrame.getAuxLabel1()[i][0])){
								aux3="--"+OPDFrame.getAuxLabel1()[i][1]+"--";
							}
						}
		        	}
		        	if(rs1.getString(11) !=null){
		        		dmemo=rs1.getString(11).trim();
		        	}
		        	if(rs1.getString(12) !=null){
		        		String sx=rs1.getString(12).trim();
		        		if(sx.length()==1) {
		        			subsuffix="0"+sx;
		        		}else {
		        			subsuffix=sx;
		        		}
		        		 
		        	}
	        		parent.add( new UniqueNode (subsuffix+"."+drug+" "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3+" "+dmemo+" "+ned));   
	        		//System.out.println(rs1.getString(1).trim()+"----******----"+drug+" "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3+" "+dmemo+" "+ned);
	        	}
	        	 
	        	 
	    			
	        	stmt1.close();	
	        	if(parent ==null) {
	    			root.add( new UniqueNode (""));
	    		}else {
	    			root.add ( parent );
	    		}
	        }
	        // 
	        stmt.close();	        
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		if(parent ==null) {
			root.add( new UniqueNode (""));
		}
		return new WebTreeModel<UniqueNode> ( root );
	}
	public WebTreeModel getDefaultTreeModelIPD (String hn){
		UniqueNode root = new UniqueNode ( "HN :"+hn );
		//UniqueNode parent=null;
		int node_=0;
		Connection conn ;
		conn=new DBmanager().getConnMSSql();
		PreparedStatement stmt,stmt1,stmt2,stmt3,stmt4,stmt31 ;
		String query_ipd =  "select an from admmaster where dischargedatetime > '"+Setup.GetDateMo(12)+"'  and hn=? order by dischargedatetime desc ";
		try {			 
			 
			stmt2 = conn.prepareStatement(query_ipd);
			stmt2.setString(1,hn);
	        ResultSet rs2 = stmt2.executeQuery();
	        while (rs2.next()) {
	        	node_=1;
	        	String an=rs2.getString(1).trim();
	        	//System.out.println(">>>>>>>"+an);
	        	//parent = new UniqueNode (" [An:"+an+"]");
	        	String query_file="select  distinct orderno from ipddrughist where an='"+an+"' and medicineordertype='3' and orderno is not null ";    			        	
	        	stmt3 = conn.prepareStatement(query_file);
				ResultSet rs3 = stmt3.executeQuery();
				while (rs3.next()) {
					 if(rs3.getString(1) !=null) {
						String orderno=rs3.getString(1).trim();
						//String makedate=rs3.getString(2).trim();
						//System.out.println(orderno+"***");
						String query_file1="select  makedatetime from ipddrughist where an='"+an+"' and medicineordertype='3' and orderno='"+orderno+"' ";    			        	
			        	stmt31 = conn.prepareStatement(query_file1);
						ResultSet rs31 = stmt31.executeQuery();
						String makedate="";
						while (rs31.next()) {
							makedate=rs31.getString(1).trim();
						}
						stmt31.close();
						UniqueNode parent = new UniqueNode ("เลขที่  "+orderno+" วันที่ ทำรายการ  "+Setup.ShowThaiDate1(makedate.substring(0,10))+" An: "+an);
						//UniqueNode parent = new UniqueNode ("เลขที่  "+orderno+" AN: "+an);
						
						//System.out.println("เลขที่  "+orderno+" วันที่ ทำรายการ  "+Setup.ShowThaiDate1(makedate.substring(0,10))+" An: "+an);
						//String qu="select ipddrughist.an ,stock_master.shortname,ipddrughist.qty,ipddrughist.dosecode,ipddrughist.dosetype,ipddrughist.doseunitcode,ipddrughist.doseqtycode,ipddrughist.auxlabel1,ipddrughist.auxlabel2,ipddrughist.auxlabel3,ipddrughist.makedatetime from ipddrughist,stock_master where ipddrughist.stockcode=stock_master.stockcode and ipddrughist.orderno='"+orderno+"'";
						String qu="select an ,stockcode,qty,dosecode,dosetype,doseunitcode,doseqtycode,auxlabel1,auxlabel2,auxlabel3,makedatetime,dosememo from ipddrughist where  orderno='"+orderno+"'";
						
						stmt4 = conn.prepareStatement(qu);
						ResultSet rs4 = stmt4.executeQuery();
						while (rs4.next()) {
							String drug="",ned="",dc="",dt="",dqc="",aux="",aux2="",aux3="",duc="",q="",dcdate="",dmemo="";
							if(rs4.getString(2) !=null){
				        		//drug=rs4.getString(2).trim();
				        		for(int i=0;i<shortname.length;i++){
									if(rs4.getString(2).trim().equals(shortname[i][0])){
										drug=shortname[i][1];
										break;
									}
									//System.out.println(i+1+". "+labunitname[i][0]);
								}
				        		if(drug.equals("")) {
				        			for(int i=0;i<engname.length;i++){
										if(rs4.getString(2).trim().equals(engname[i][0])){
											drug=engname[i][1];
											break;
										}
										//System.out.println(i+1+". "+labunitname[i][0]);
									}
				        		}
				        	}
							if(rs4.getString(3) !=null){
				        		q=rs4.getString(3).trim();
				        	}
				        	if(rs4.getString(4) !=null){
				        		for(int i=0;i<OPDFrame.getDoseCode().length;i++){
									if(rs4.getString(4).trim().equals(OPDFrame.getDoseCode()[i][0])){
										dc=OPDFrame.getDoseCode()[i][1];
									}
								}
				        	}
				        	if(rs4.getString(5) !=null){
				        		for(int i=0;i<OPDFrame.getDoseType().length;i++){
									if(rs4.getString(5).trim().equals(OPDFrame.getDoseType()[i][0])){
										dt=OPDFrame.getDoseType()[i][1];
									}
								}
				        	}
				        	if(rs4.getString(6) !=null){
				        		for(int i=0;i<OPDFrame.getDoseUnitCode().length;i++){
									if(rs4.getString(6).trim().equals(OPDFrame.getDoseUnitCode()[i][0])){
										duc=OPDFrame.getDoseUnitCode()[i][1];
									}
								}
				        	}
				        	if(rs4.getString(7) !=null){
				        		dqc=rs4.getString(7).trim();
				        	}
				        	/*
				        	if(rs4.getString(8) !=null){
				        		for(int i=0;i<OPDFrame.getAuxLabel1().length;i++){
									if(rs4.getString(8).trim().equals(OPDFrame.getAuxLabel1()[i][0])){
										aux=""+OPDFrame.getAuxLabel1()[i][1]+"";
									}
								}
				        	}
				        	if(rs4.getString(9) !=null){
				        		for(int i=0;i<OPDFrame.getAuxLabel1().length;i++){
									if(rs4.getString(9).trim().equals(OPDFrame.getAuxLabel1()[i][0])){
										aux2=""+OPDFrame.getAuxLabel1()[i][1]+"";
									}
								}
				        	}
				        	*/
				        	if(rs4.getString(10) !=null){
				        		for(int i=0;i<OPDFrame.getAuxLabel1().length;i++){
									if(rs4.getString(10).trim().equals(OPDFrame.getAuxLabel1()[i][0])){
										aux3="--"+OPDFrame.getAuxLabel1()[i][1]+"";
									}
								}
				        	}
				        	if(rs4.getString(12) !=null){
				        		dmemo="-"+rs4.getString(12).trim();
				        	}
				        	//if(rs4.getString(11) !=null){
				        	//	dcdate=rs4.getString(11).trim();
				        	//}
							//parent.add( new UniqueNode (drug+ "  "+dt+"  "+dqc+"  "+duc+" "+dc+"  "+aux+"  "+aux2+" -- "+aux3));
							parent.add( new UniqueNode (drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3+" "+dmemo));
							//System.out.println(orderno+"***"+makedate+"---"+drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3);
							//System.out.println(orderno+"***---"+drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3);
							System.out.println(orderno+"memo ***---"+drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3+ "++++++++--- "+dmemo);
						}
						stmt4.close();
						root.add ( parent );	
					 }
					 
				}
				stmt3.close();
				if(node_==0) {
					root.add( new UniqueNode (""));
				} 
				//else{
					//root.add ( parent );	
				//}
				 	 
	        }
	        // 	
	        stmt2.close();        
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		if(node_==0) {
			root.add( new UniqueNode (""));
		} 
		 	
		return new WebTreeModel<UniqueNode> ( root );
		
	}
	public WebTreeModel getDefaultTreeModelLab (String hn){
		UniqueNode root = new UniqueNode ( "HN :"+hn );
		int node_=0;
		Connection conn ;
		conn=new DBmanager().getConnMSSql();
		PreparedStatement stmt,stmt1,stmt2,stmt3,stmt4,stmt31 ;
		String query =  "select  distinct entrydatetime,requestfromvn,clinic from labreq where clinic is not null and cxlbyusercode is  null and (entrydatetime between '"+Setup.GetDateNow()+" 00:00:00' and '"+Setup.GetDateNow()+" 23:59:59' )  and labreq.hn=? order by entrydatetime desc ";
		String query_ =  "select  requestno from labreq where hn=? and entrydatetime=?";

		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1,hn);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	node_=1;
	        	String lab_date=rs.getString(1).trim().substring(0, 10);
	        	String vn=rs.getString(2).trim();
	        	String clinic=rs.getString(3).trim();

	        	System.out.println(">>>"+vn+"--"+lab_date+"----"+clinic);
	        	
	        	UniqueNode parent = new UniqueNode (Setup.ShowThaiDateShort1(lab_date)+" [vn:"+vn+"]"+"["+clinic+"]") ;
	        	
	        	root.add ( parent );	
	        }
	        stmt.close();
	        conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
        
		if(node_==0) {
			root.add( new UniqueNode (""));
		} 
		return new WebTreeModel<UniqueNode> ( root );
	}
	public WebTreeModel getDefaultTreeModelOR (String hn){
		UniqueNode root = new UniqueNode ( "HN :"+hn );
		
		String quer_or="select requestno from orreq where hn='"+hn+"' and cxlbyusercode is null";
		String quer_orusage="select stockcode,unitcode from orusage where requestno='"+hn+"' ";
		
		return new WebTreeModel<UniqueNode> ( root );
	}
	public void createreport(String report_code,String typeofprint,String clinic_name,String clinic_scan,String form_clinic_name,String page){
		String h_print=typeofprint.trim();
		int h_page=0;
    	if(h_print.equals("1")){
    		h_page=430;
    	}else if(h_print.equals("2")){
    		h_page=840;
    	}
    	String note="",lab="";
    	if(report_code.trim().equals("00041") || report_code.trim().equals("00049")){
    		note="เวลาคัดแยก..........................น.";
    	}
    	String date_print="";
    	if(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim().equals(Setup.GetDateNow().trim())){
    		date_print=Setup.ConvertDateTimePrint(day)+" น. ";
    	}else{
    		date_print=Setup.ShowThaiDate1(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim())+" เวลา.......................น.";
    	}
    	
    	String filename=Setup.ConverttoScanDate(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()))+clinic_scan+page+"0001"+oUserInfo.GetPtHN().trim();
    	JasperReportBuilder report = DynamicReports.report(); 
		report.setPageFormat(590, h_page, PageOrientation.PORTRAIT);
		String confFile="report/"+report_code.trim()+".jrxml";
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(confFile );
		try {
			report.setTemplateDesign(in);
			report.setDataSource(new JREmptyDataSource());
			report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
			
			if(report_code.trim().equals("00208") || report_code.trim().equals("10027")){
				String img=nameBefore;
				report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+date_print+" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("line8",img);
				report.setParameter("barcode",filename);
			}
			else if(report_code.trim().equals("01000") || report_code.trim().equals("01001")){
				String runno="";
				report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+date_print +" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("line9",runno);
				report.setParameter("barcode",filename);
				 
			}
			else if(report_code.trim().equals("01003") || report_code.trim().equals("01005") || report_code.trim().equals("01007")){
				String runno="";
				runno=getRunno(oUserInfo.GetPtVN(),oUserInfo.GetPtCliniccode());
				report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+date_print +" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("line10",runno.substring(0,runno.indexOf(":")));
				//report.setParameter("line9",runno.substring(runno.indexOf(":")+1));
				report.setParameter("line11"," HN:"+oUserInfo.GetPtHN());
				report.setParameter("line12",oUserInfo.GetPtVN());
				report.setParameter("line13",oUserInfo.GetPtCliniccode());
				report.setParameter("barcode",filename);
			}
			else if(report_code.trim().equals("01004") || report_code.trim().equals("01006") ){
				 		
				report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" VN:"+oUserInfo.GetPtVN()+" CID: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+date_print +" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				 
				if(InApp.usertype.equals("87")) {
					runnoQ=getQ(oUserInfo.GetPtCliniccode(),oUserInfo.GetPtVN(),oUserInfo.GetPtHN());
					report.setParameter("line9",runnoQ);
					report.setParameter("line10","");
					System.out.println(runnoQ+"<<<");
					//new OPDPanel(oUserInfo,width,height).getOPD();
					
				}else {
					
					runnoQ=getRunno(oUserInfo.GetPtVN(),oUserInfo.GetPtCliniccode());
					report.setParameter("line10",runnoQ.substring(0,runnoQ.indexOf(":")));
					report.setParameter("line9","");
					System.out.println(runnoQ+",,,");
				}
				 
				report.setParameter("line11"," HN:"+oUserInfo.GetPtHN());
				report.setParameter("barcode",filename);
				 
			}
			else if(report_code.trim().equals("09102") ){
				report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" VN:"+oUserInfo.GetPtVN()+" HN:"+oUserInfo.GetPtHN()+" CID: "+oUserInfo.GetPtCID());
				report.setParameter("line4","สิทธิการรักษา  "+oUserInfo.GetRightCode()+"   Request No. ...................................... ");
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("barcode",filename);
				
			}
			else if(report_code.trim().substring(0,3).equals("022") || report_code.trim().substring(0,3).equals("023")){
				report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line8", "HN:    "+oUserInfo.GetPtHN()+"   VN:   "+oUserInfo.GetPtVN()+"    AN: ................................. เตียง................................");
				report.setParameter("line4","วันที่ "+date_print+" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("barcode",filename);

			}
			else if(report_code.trim().equals("00230") || report_code.trim().equals("00231") || report_code.trim().equals("00232")){
				report.setParameter("line1",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4", "HN:    "+oUserInfo.GetPtHN()+"   สิทธิครั้งที่แล้ว:   "+oUserInfo.GetRightCode());				
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("barcode",oUserInfo.GetPtHN().trim());

			}
			else if(report_code.trim().equals("09001")){
				report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4", "HN:    "+oUserInfo.GetPtHN()+"   สิทธิครั้งที่แล้ว:   "+oUserInfo.GetRightCode());				
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("barcode",oUserInfo.GetPtHN().trim());

			}
			else if(report_code.trim().substring(0,2).equals("18")) {
				String dept="",wa="";
				if(oUserInfo.GetPtWard().equals("")) {
					
				}else {
					String name_r=getValueWard(oUserInfo.GetPtWard().trim());
					
					dept=name_r.substring(0, name_r.indexOf("@"));
					wa=name_r.substring(name_r.indexOf("@")+1);
				}
				 
				report.setPageMargin(DynamicReports.margin().setLeft(30).setRight(10).setTop(10).setBottom(10));
				
				String inBarcode=oUserInfo.GetPtVisitdate()+report_code+oUserInfo.GetPtAN().trim();
				report.setParameter("line1", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge().trim()+"   HN:"+oUserInfo.GetPtHN().trim()+"   AN:"+oUserInfo.GetPtAN().trim()+"  Bed: "+oUserInfo.GetPtBed());
				report.setParameter("line2", "Dapartment : "+dept+ "   Ward: "+wa);
	 			report.setParameter("barcode",inBarcode);
	 			report.setParameter("line7",report_code.trim()+".jrxml");
				report.setDataSource(new JREmptyDataSource());
			}
			else if(report_code.equals("10002")) {
				report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+date_print +" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("labresult",lab);
				report.setParameter("barcode",filename);
				report.setParameter("gfr",getGFR(oUserInfo.GetPtHN().trim()));
				
			}
			else{
				
				report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
				report.setParameter("line1","โรงพยาบาลอุตรดิตถ์   ห้องตรวจ: "+clinic_name);
				report.setParameter("line2",form_clinic_name);
				report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
				report.setParameter("line4","วันที่ "+date_print +" "+note+" "+oUserInfo.GetRightCode());
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",report_code.trim()+".jrxml");
				report.setParameter("labresult",lab);
				report.setParameter("barcode",filename);
			}
			report.print(true);
			//report.show(acPrint);
		} catch (DRException e) {
			e.printStackTrace();
		}
    	
    	
	}
	public void view(){
		nameBefore="";
		String folder="C:\\utthscan\\";
		File f = new File(folder);
		File[] listOfFiles = f.listFiles();
		String file_name[]=new String[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {     		
        		file_name[i]=listOfFiles[i].getName();
        	}
		}
		if(file_name.length>1){
			WebOptionPane.showMessageDialog(this,"มีจำนวนไฟล์ไม่ถูกต้อง ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
		}else{
			for (int i = 0; i < file_name.length; i++) {			
				if(file_name[i] !=null){
					//System.out.println("file from scan show.."+file_name[i]);
					nameBefore=folder+file_name[i];
					break;
				} 
				else{			
					WebOptionPane.showMessageDialog(this,"ไม่มีไฟล์นี้แล้ว กรุณา Scanไฟล์แล้วทำงานต่อไป ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
			 		  break;
				}
			}
		}
		if(nameBefore.equals("")){
			WebOptionPane.showMessageDialog(this,"กรุณาตรวจสอบ Folder C:\\utthscan","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
		}else{
			FileInputStream in;
			try {
				in = new FileInputStream(nameBefore);
				FileChannel channel = in.getChannel();
			    ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
			    channel.read(buffer);
			    Image image = load(buffer.array());			 
				Image imageScaled = image.getScaledInstance(560, 600,  Image.SCALE_SMOOTH);
				ImageIcon icon = new ImageIcon(imageScaled);				
				Label_IMG.setIcon(icon);
				//close load
				channel.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		     
		}
		
		mid2.validate();
		mid2.repaint();
	}
	public Image load(byte[] data) throws IOException{
		Image image = null;
	    SeekableStream stream = new ByteArraySeekableStream(data);
	    String[] names = ImageCodec.getDecoderNames(stream);
	    ImageDecoder dec = 
	    ImageCodec.createImageDecoder(names[0], stream, null);
	    RenderedImage im = dec.decodeAsRenderedImage();
	    image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
	    return image;
	}
	/*
	public String getID(String hn,String vn) {
		String id="";
		String query_no="select id from visit where visitno='"+vn.trim()+"'  and (visitdate between '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+" 00:00:00' and '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+" 23:59:59')";					
		try {
			Connection conn=new DBmanager().getConnMySql();
			PreparedStatement stmt4 = conn.prepareStatement(query_no);
			ResultSet rs4 = stmt4.executeQuery();
			while (rs4.next()) {				
				id=rs4.getString(1).trim();
				break;
			}
			stmt4.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
*/
	///
	public String getQ(String clinic,String vn,String hn) {
		int extra=0;
		String q="";
		int q_all=0,id=0;
		System.out.println(vn+"<<<"+clinic+"<<"+hn);
		String sql_q="select visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitno='"+vn+"' and visit.hn='"+hn+"' and visit_clinic.clinic='"+clinic+"'";
		Connection conn=new DBmanager().getConnMySql();
		try {
			PreparedStatement stmt4 = conn.prepareStatement(sql_q);
			ResultSet rs4 = stmt4.executeQuery();
			while (rs4.next()) {	
				if(rs4.getString(1) !=null) {
					q=rs4.getString(1).trim();
					extra=1;
				}
				 		 
			}
			stmt4.close();
			 
			if(extra==1) {
				
			}
			else if(extra==0){
				if(q.equals("")) {
					System.out.println(vn+"<<<"+clinic+"<<"+hn+"---"+extra);
					String get_id="select id from visit where visitdate='"+Setup.GetDateNow()+"' and visitno='"+vn+"' and hn='"+hn+"'";
					PreparedStatement stmt41 = conn.prepareStatement(get_id);
					ResultSet rs41 = stmt41.executeQuery();
					while (rs41.next()) {	
						if(rs41.getString(1) !=null) {
							//System.out.println(vn+"<<<"+clinic+"<<"+hn+"---"+extra+"*****"+rs41.getString(1));
							//String insert_clinic = "insert into visit_clinic (id,clinic,userupdate,dateupdate,ipupdate) values ('"+rs41.getInt(1)+"','"+clinic+"','2','"+Setup.GetDateTimeNow()+"','172.17.71.11')";
							//PreparedStatement stmt62 = conn.prepareStatement(insert_clinic);
							//int rs_save =  stmt62.executeUpdate();
							//stmt62.close();
							
							
							String check="select id from visit_clinic where id='"+rs41.getInt(1)+"' and clinic='"+clinic+"'";
							PreparedStatement stmt411 = conn.prepareStatement(check);
							ResultSet rs411 = stmt411.executeQuery();
							while (rs411.next()) {	
								if(rs411.getString(1) !=null) {								 
									System.out.println("+++++++++++++"+vn+"<<<"+clinic+"<<"+hn+"---"+extra+"*****"+rs41.getString(1));
								}else {
									System.out.println("-------*******---"+vn+"<<<"+clinic+"<<"+hn+"---"+extra+"*****"+rs41.getString(1));
									
									String insert_clinic = "insert into visit_clinic (id,clinic,userupdate,dateupdate,ipupdate) values ('"+rs41.getInt(1)+"','"+clinic+"','2','"+Setup.GetDateTimeNow()+"','172.17.71.11')";
									PreparedStatement stmt62 = conn.prepareStatement(insert_clinic);
									int rs_save =  stmt62.executeUpdate();
									stmt62.close();
								}
							}
							stmt411.close();
							
							 
						}					 		 
					}
					stmt41.close();
				}else {

				}
		 
			}
			/////////////////////////////
			if(q.equals("")) {
				 
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
				String sql="select visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitdate='"+Setup.GetDateNow()+"' "+sql_user+"  order by visit_clinic.queue desc LIMIT 1";
				PreparedStatement stmt41 = conn.prepareStatement(sql);
				ResultSet rs41 = stmt41.executeQuery();
				while (rs41.next()) {
					if(rs41.getString(1) !=null) {
						q_all=rs41.getInt(1);
					}
					 			 
				}
				stmt41.close();
				if(q_all==0) {
					int id1=0;
					System.out.println("Queue..All..****"+q_all+"--"+id1+"---"+clinic);
					String sql42="select id from visit where visitno='"+vn+"'and hn='"+hn+"' and visitdate='"+Setup.GetDateNow()+"'  LIMIT 1";
					PreparedStatement stmt42 = conn.prepareStatement(sql42);
					ResultSet rs42 = stmt42.executeQuery();
					while (rs42.next()) {
						if(rs42.getString(1) !=null) {
							id1=rs42.getInt(1);
						}					 			 
					}
					stmt41.close();
					String sql_update1 = "update visit_clinic set  queue='1' where id='"+id1+"' and clinic='"+clinic+"' ";
					PreparedStatement stmt611 = conn.prepareStatement(sql_update1);
					int rs_update1 = stmt611.executeUpdate();
					 
					stmt611.close();
					   q="1";
					
				}else {
					 
					int id1=0;
					 
					String sql42="select id from visit where visitno='"+vn+"'and hn='"+hn+"' and visitdate='"+Setup.GetDateNow()+"'  LIMIT 1";
					PreparedStatement stmt42 = conn.prepareStatement(sql42);
					ResultSet rs42 = stmt42.executeQuery();
					while (rs42.next()) {
						if(rs42.getString(1) !=null) {
							id1=rs42.getInt(1);
						}					 			 
					}
					stmt41.close();
					System.out.println("Queue..All.."+q_all+"--"+id1+"---"+clinic);
					///
					String sql_update1 = "update visit_clinic set  queue='"+(q_all+1)+"' where id='"+id1+"' and clinic='"+clinic+"' ";
					PreparedStatement stmt611 = conn.prepareStatement(sql_update1);
					int rs_update1 = stmt611.executeUpdate();
					q_all=q_all+1;
					stmt611.close();
					q=Integer.toString(q_all);
				}
				 
				
				
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Queue.."+q);
		return q;
	}
	/*
	public String getQ(String hn,String vn,String clinic,String room) {
		String q="",id="";
		//get id
		if(getID(hn,vn).equals("")) {
			
		}
		id=getID(hn,vn).trim();
		//get q
		String [] qq=getQ1(hn,vn,clinic,room,id).split("@");
		if(qq[0].equals("")) {
			String sql_addQ = "insert into  visit_clinic ( id,clinic,roomno,queue,servicestatus,dateupdate,ipupdate,urgent) values ("+id+",'"+clinic+"','"+room+"',"+qq[1]+",'0','"+Setup.GetDateTimeNow()+"','"+InApp.ip+"','0')";
			try {
				Connection con6=new DBmanager().getConnMySql();
				PreparedStatement stmt6 = con6.prepareStatement(sql_addQ);
				int rs_save = stmt6.executeUpdate();
				stmt6.close();
				con6.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String [] qq1=getQ1(hn,vn,clinic,room,id).split("@");
			q=qq1[0];
		}else {
			q=qq[0];
		}
		

		return q;
	}
	public  String getQ1(String hn,String vn,String clinic,String room,String id) {
		String to="";
		int p=0;
		String query_no="select queue from visit_clinic where roomno='"+room.trim()+"' and servicestatus='0' and (dateupdate between '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+" 00:00:00' and '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+" 23:59:59') and clinic='"+clinic+"' and id='"+id+"'";				
		try {
			Connection conn=new DBmanager().getConnMySql();
			PreparedStatement stmt4 = conn.prepareStatement(query_no);
			ResultSet rs4 = stmt4.executeQuery();
			while (rs4.next()) {				
				to=rs4.getString(1).trim();
				 
			}
			stmt4.close();
			
			if(to.equals("")) {
				String query_no1="select queue from visit_clinic where roomno='"+room.trim()+"' and servicestatus='0' and (dateupdate between '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+" 00:00:00' and '"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+" 23:59:59') and clinic='"+clinic+"' ";				
				PreparedStatement stmt41 = conn.prepareStatement(query_no1);
				ResultSet rs41 = stmt41.executeQuery();
				while (rs41.next()) {				
					p++;
					 
				}
				stmt4.close();
				
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		System.out.println("to..."+to+"@"+(p+1));	
		
		return to+"@"+(p+1);
		
	}
*/
	public void ClearFile(){		
		File folder = new File("C:\\utthscan\\");
		File[] listOfFiles = folder.listFiles();
        String file_name_input[]=new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
        	if (listOfFiles[i].isFile()) {     		
        		listOfFiles[i].delete();
        	}
        	else if(listOfFiles[i].isDirectory()){
        		listOfFiles[i].delete();
        	}
        }
	}
	public String getMsg(String hn) {
		String msg="";
		Connection conn;
		PreparedStatement stmt;
		String query="select memo from patient_msg where hn='"+hn+"' and suffix='5'";
		conn = new DBmanager().getConnMSSql();
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();			 
			while (rs.next()) {
				if(rs.getString(1)!=null) {
					msg=rs.getString(1).trim();
				}				 
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}			 	
		return msg;
	}
	public String getGFR(String hn) {
		String lab="",date="",result="";
		Connection conn;
		PreparedStatement stmt;
		String query="select top 1 resultvalue,entrydatetime from labresult where hn='"+hn+"' and labcode='C73' and resultdatetime is not null order by entrydatetime desc";
		conn = new DBmanager().getConnMSSql();
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();			 
			while (rs.next()) {
				if(rs.getString(1)!=null) {
					lab=rs.getString(1).trim();
					date=rs.getString(2).trim().substring(0, 10);
					System.out.println("-------->"+lab+"--"+date);
				}				 
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(date.equals("")) {
			result="GFR ล่าสุด  วันที่เจาะ ...............";
		}else {
			System.out.println("-------->"+lab+"--"+Setup.ShowThaiDateShort1(date));
			result="GFR ล่าสุด  วันที่เจาะ "+Setup.ShowThaiDateShort1(date)+"="+lab;
		}
		 
		return result;
	}
	public void clearPanel() {
		nameBefore="";
		mid1.removeAll();
		mid1.revalidate();
		mid1.repaint();	
		mid2.removeAll();
		mid2.revalidate();
		mid2.repaint();	
		MiddleSection.revalidate();
		MiddleSection.repaint();
		runnoQ="";
		Label_Info_in.setText("");
		Label_Info_out.setText("");

	}
	public  String getRunno(String vn,String clinic) {
		String to="";
		Connection conn;
		PreparedStatement stmt,stmt1,stmt3,stmt4;
		ResultSet rs,rs1,rs3,rs4;
		conn=new DBmanager().getConnMSSql();
		String name_c="";
		for(int j=0;j<OPDFrame.clinicname.length;j++){
			if(clinic.equals(OPDFrame.clinicname[j][0])){
				name_c=OPDFrame.clinicname[j][1];
			}
			//System.out.println(i+1+". "+clinic_name[i][0]);
		}
		try {

			String query_no="select vn from vnpres where clinic='"+clinic+"' and visitdate=? order by regindatetime ";
				
			//String query_no="select vn from vnpres where clinic='"+clinic+"' and visitdate=? order by case IsNumeric(vn)  when 1 then Replicate('0', 100 - Len(vn)) + vn else vn end ";
			
			/*	
			int n4=1;
				stmt4 = conn.prepareStatement(query_no);
				stmt4.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())); 
				rs4 = stmt4.executeQuery();

				while (rs4.next()) {				
					if(oUserInfo.GetPtVN().equals(rs4.getString(1).trim())) {
						//System.out.println(n4+"--clinic..."+clinic_pt[i]);
						break;
					}
					n4++;
				}
				stmt4.close();
				
				*/
				//to+="ท่าน มีเลขที่ VN คือ "+oUserInfo.GetPtVN()+"  เข้ารับบริการห้องตรวจ  "+name_c+"("+clinic+")"+"---ลำดับที่ "+n4;
				to+="ท่าน มีเลขที่ VN คือ "+oUserInfo.GetPtVN()+"  เข้ารับบริการห้องตรวจ  "+name_c+"("+clinic+")";
				System.out.println("ท่าน มีเลขที่ VN คือ "+oUserInfo.GetPtVN()+"  เข้ารับบริการห้องตรวจ  "+name_c+"("+clinic+")");
			
			to=to+":";
			 
			 
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//System.out.println("vn"+oUserInfo.GetPtVN());
		
		return to;
	}
	public String getValueWard(String ward){
		String nameto="";
		Connection conn;
		PreparedStatement stmt;
		String query =  "select ward_name, ward_department from ward_ipd where  status='1'  and ward_code='"+ward+"'" ;
		
		try {
			conn=new DBmanager().getConnMySql();
	        stmt = conn.prepareStatement(query);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {	  
	        	nameto=rs.getString(1).trim()+"@"+rs.getString(2).trim();
	        }
	        stmt.close();
	    	conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameto;
	}
	public void initDrugName() {
		int p=0,p1=0;
		 
		//get unit name
		Connection conn3;
		PreparedStatement stmt31,stmt32,stmt33,stmt34;
		
		conn3 = new DBmanager().getConnMSSql();
		String query31="select stockcode,englishname from stock_master where englishname is not null ";
		String query32="select stockcode,shortname from stock_master where shortname is not null ";
		try {
			stmt31 = conn3.prepareStatement(query31);
			ResultSet rs31 = stmt31.executeQuery();
			 
			while (rs31.next()) {
				p++;
			}
			stmt31.close();
			engname=new String [p][2];
			stmt32 = conn3.prepareStatement(query31);			 
			ResultSet rs32 = stmt32.executeQuery();
			int pp=0; 
			while (rs32.next()) {
				//System.out.println(rs2.getString(1));
				 engname[pp][0]=rs32.getString(1);
				 engname[pp][1]=rs32.getString(2).substring(1);
				 pp++;
			}
			stmt32.close();
			
			stmt33 = conn3.prepareStatement(query32);
			ResultSet rs33 = stmt33.executeQuery();
			 
			while (rs33.next()) {
				p1++;
			}
			stmt33.close();
			shortname=new String [p1][2];
			stmt34 = conn3.prepareStatement(query32);			 
			ResultSet rs34 = stmt34.executeQuery();
			int pp1=0; 
			while (rs34.next()) {
				//System.out.println(rs2.getString(1));
				 shortname[pp1][0]=rs34.getString(1);
				 shortname[pp1][1]=rs34.getString(2);
				 pp1++;
			}
			stmt34.close();
			
			conn3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	 public void actionPerformed(ActionEvent e) {
			if(e.getSource() == cbRow1){
				String valueRow1=((ComboItem)cbRow1.getSelectedItem()).getValue();
				vn1=valueRow1.substring(0, valueRow1.indexOf(":")).trim();
				name1=valueRow1.substring(valueRow1.indexOf(":")+1, valueRow1.indexOf("*")).trim();
				hn1=valueRow1.substring(valueRow1.indexOf("*")+1, valueRow1.indexOf("#")).trim();		 
				age1=valueRow1.substring(valueRow1.indexOf("#")+1).trim();
				 
			}
			else if(e.getSource() == cbRow2){
				String valueRow2=((ComboItem)cbRow2.getSelectedItem()).getValue();
				vn2=valueRow2.substring(0, valueRow2.indexOf(":")).trim();
				name2=valueRow2.substring(valueRow2.indexOf(":")+1, valueRow2.indexOf("*")).trim();
				hn2=valueRow2.substring(valueRow2.indexOf("*")+1, valueRow2.indexOf("#")).trim();			 
				age2=valueRow2.substring(valueRow2.indexOf("#")+1).trim();
				 
			}
	}
	 public void setInitSticker(){
			
			String[] parts=null;
			parts = InApp.userappcode.split("\\,");
			String sql_user1="",sql_user="";
			if(parts.length==1){
				for(int i=0;i<parts.length;i++){
					sql_user1=" and vnpres.clinic='"+parts[i]+"'";
				}
				sql_user=sql_user1;
			}
			else if(parts.length>1){
				sql_user1="and vnpres.clinic in (";
				for(int i=0;i<parts.length;i++){
					sql_user1+="'"+parts[i]+"',";
				}
				sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
			}
			
			 
			 
			int p=0;
			 //String query="select an,firstname,lastname,hn,usedrightcode,outdatetime,BirthDateTime,sex,maritalstatus,initialnamecode,ref,bedno from in_view_pt_ipd  where  OUTDATETIME  is null   and suffix='0' "+sql_user+" order by bedno ";			
			//String query="select  visit.visitno,visit.hn,visit_clinic.clinic,visit_clinic.queue from visit,visit_clinic where visit.id=visit_clinic.id and visit.visitdate=?  "+sql_user+" order by visit_clinic.queue IS NULL ASC,visit_clinic.queue  ASC";
			String query="select  vnmst.vn, vnmst.hn, vnpres.diagoutdatetime ,vnpres.clinic from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate=?  "+sql_user+"  order by case IsNumeric(vnmst.vn)  when 1 then Replicate('0', 100 - Len(vnmst.vn)) + vnmst.vn else vnmst.vn end ";
            
			//String query="select admmaster.an,admmaster.hn,admbed.outdatetime,admbed.bedno,admmaster.admdatetime from admmaster,admbed  where admmaster.an=admbed.an and  OUTDATETIME  is null  "+sql_user+" order by admbed.bedno ";
			try {

				Connection conn3=new DBmanager().getConnMSSql();
				
				PreparedStatement stmtRow = conn3.prepareStatement(query);
				stmtRow.setString(1, Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()));
				ResultSet rsRow = stmtRow.executeQuery();
			 
				while (rsRow.next()) {	
					String sex_pt="",mar_pt="";
					String initname="",right="",age_pt="";
					String query_db=SetupSQL.getDOB(rsRow.getString(2).trim());
					PreparedStatement stmt = conn3.prepareStatement(query_db);
					 
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {

						if(rs.getString(1) !=null){
							Date birthday=rs.getDate(1);
							age_pt=Setup.AgeInAll(birthday.toString()).trim();
						}		
						
					}
					stmt.close();
					String name_=Setup.getName(rsRow.getString(2).trim());
					cbRow1.addItem(new ComboItem("VN: "+rsRow.getString(1).trim()+"   "+name_,rsRow.getString(1).trim()+":"+name_+"*"+rsRow.getString(2).trim()+"#"+age_pt));
					cbRow2.addItem(new ComboItem("VN: "+rsRow.getString(1).trim()+"   "+name_,rsRow.getString(1).trim()+":"+name_+"*"+rsRow.getString(2).trim()+"#"+age_pt));
					 
				}
				stmtRow.close();
				conn3.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			 
		}
	 public String getTxDatetimeIn(String hn) {
			String to="";
			Connection conn=new DBmanager().getConnMySql();
			String sql="select txdatetime from txlog where txtype='1' and hn='"+hn+"' and status='1' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' order by txdatetime desc limit 1";
			 
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if(rs.getString(1) !=null) {
						String to1=rs.getString(1).trim().substring(10);
						to=" "+to1.trim().substring(0,8)+" น.";
					}					 			 
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			System.out.println(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"-----"+to);
			return to;
		}
		public String getTxDatetimeOut(String hn) {
			String to="";
			Connection conn=new DBmanager().getConnMySql();
			String sql="select txdatetime from txlog where txtype='2' and hn='"+hn+"'  and status='1' and visitdate='"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"' order by txdatetime desc limit 1";
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if(rs.getString(1) !=null) {
						String to1=rs.getString(1).trim().substring(10);
						to=" "+to1.trim().substring(0,8)+" น.";
					}					 			 
				}
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

				 
			return to;
		}
		public void getInOutDateTime(String hn) {
			mid1.add(Label_Info_in);
			mid1.add(Label_Info_out);
			Label_Info_in.setText(getTxDatetimeIn(oUserInfo.GetPtHN()));
			Label_Info_out.setText(getTxDatetimeOut(oUserInfo.GetPtHN()));
		}

}
