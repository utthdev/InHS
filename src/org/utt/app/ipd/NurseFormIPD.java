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

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.alee.laf.tree.UniqueNode;
import com.alee.laf.tree.WebTreeModel;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;

import org.apache.commons.lang3.StringUtils;

import org.utt.app.InApp;
import org.utt.app.common.ObjectData;

import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.opd.OPDFrame;
import org.utt.app.ui.JCheckBoxTree;
import org.utt.app.util.ComboItem;
import org.utt.app.util.I18n;

import org.utt.app.util.PrintIPDLabLabel;

import org.utt.app.util.Setup;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Document;
import javax.swing.tree.TreePath;

public class NurseFormIPD extends WebPanel implements Observer,ActionListener {
    ObjectData oUserInfo;
    int width,height;
    String formcode="",doctorname="";
    String an1="",an2="",age1="",age2="",hn1="",hn2="",name1="",name2="";
    String name="",an="",hn="",age="",bed="",memo="",cid="";
    String wardcode,department_id,ward_name_short,wardname,department,departmentName,doctor,formward;
    
    Vector<String> columnNamesIPDform;
    WebScrollPane scrollPaneIPDform;
    WebTable tableIPDform;
    WebSplitPane split;
    WebComboBox cbDR,cbRow1,cbRow2;
    WebPanel LeftSection,MainSection,MiddleSection,RightSection,mid1,mid2,bottomPanelForm;
    JPanel right1;
    WebLabel Label_Info,Label_Row1,Label_Row2,Label_Setting;
    WebButton ButtonPrint,ButtonRefreshForm,ButtonRefreshDr;
    WebTextField txtInput;
    
    GregorianCalendar day ;
    ArrayList<String> indr = new ArrayList<String>();
    ButtonGroup dr = new ButtonGroup();
    WebRadioButton DrStatus1,DrStatus2;
    String line_med_ipd="";
    WebAccordion accordion_med;
    
    
    public NurseFormIPD(ObjectData oUserInfo,int w,int h) {
    	width=w;
        height=h;
        this.oUserInfo=oUserInfo;
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(width-(width*2)/10, height-160));
		 
		cbRow1 =new WebComboBox(); 
		cbRow1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		cbRow2 =new WebComboBox();
		cbRow2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		day = new GregorianCalendar();
		
		split = new WebSplitPane(split.HORIZONTAL_SPLIT);
		split.setDividerLocation(200);
		
		LeftSection = new WebPanel();
		LeftSection.setPreferredSize(new Dimension(200, height-160));
		split.setLeftComponent(LeftSection);
		LeftSection.setLayout(new BorderLayout(0, 0));
		
		WebLabel label = new WebLabel("แบบฟอร์ม");
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		LeftSection.add(label, BorderLayout.NORTH);
		 
		
		columnNamesIPDform = new Vector<String>();		
		columnNamesIPDform.add("");
		columnNamesIPDform.add("");
		
		Vector<Vector<String>> dataIPDform = new Vector<Vector<String>>();
		DefaultTableModel modelIPDform = new DefaultTableModel(dataIPDform, columnNamesIPDform){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		tableIPDform = new WebTable(modelIPDform){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
				}
				return c;
			}
		};
		
		tableIPDform.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				int row = tableIPDform.rowAtPoint(e.getPoint());
		        int col = tableIPDform.columnAtPoint(e.getPoint());	        
		        formcode=tableIPDform.getValueAt(row, 0).toString().trim();
		        String form_name=tableIPDform.getValueAt(row, 1).toString().trim();
		        Label_Info.setText(form_name);
		        if(formcode.equals("0019") || formcode.equals("0066")){
		        	//PrintLabLabel();
		        	 formPanel(formcode);
		        }
		        else if(formcode.equals("0016")){
		        	mid2.removeAll();
		        	createForm(formcode,"2");
		        	mid2.revalidate();
		    		mid2.repaint();
		        }
		        else{
		        	mid2.removeAll();
		        	//PrintFORM(formcode);
		        	createForm(formcode,"1");
		        	mid2.revalidate();
		    		mid2.repaint();
		        }
			}
		});
		tableIPDform.setFont(new Font("Tahoma", Font.PLAIN, 12));
		tableIPDform.setRowHeight(25);		
		tableIPDform.setFillsViewportHeight(true);
 
		scrollPaneIPDform = new WebScrollPane(tableIPDform);
		//getValueWard();
		//setInit();
		//getDataForm();
		LeftSection.add(scrollPaneIPDform, BorderLayout.CENTER);
		
		bottomPanelForm = new WebPanel();
        bottomPanelForm.setBackground(Setup.getColor());
        LeftSection.add(bottomPanelForm, BorderLayout.SOUTH);
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
		
		
		MainSection = new WebPanel();
		MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-200, height-160));
		MainSection.setLayout(new BorderLayout(0, 0));
		split.setRightComponent(MainSection);
		add(split, BorderLayout.CENTER);
		
		RightSection = new WebPanel();
		RightSection.setPreferredSize(new Dimension(200, height-160));
		MainSection.add(RightSection, BorderLayout.EAST);
		RightSection.setLayout(new BorderLayout(0, 0));
		
		right1 = new JPanel();
		right1.setBorder(new TitledBorder(null, "\u0E23\u0E32\u0E22\u0E0A\u0E37\u0E48\u0E2D\u0E41\u0E1E\u0E17\u0E22\u0E4C", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		right1.setPreferredSize(new Dimension(200, 150));
		RightSection.add(right1, BorderLayout.NORTH);
		right1.setLayout(null);
		
		DrStatus1 = new WebRadioButton("1");
		DrStatus1.setBackground(Setup.getColor());
		DrStatus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doctorname=((ComboItem)cbDR.getSelectedItem()).getKey();			
			}
		});
		dr.add(DrStatus1);
		DrStatus1.setBounds(5, 15, 50, 25);
		//right1.add(DrStatus1);
		
		DrStatus2 = new WebRadioButton("2");
		DrStatus2.setBackground(Setup.getColor());
		DrStatus2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doctorname="  "+txtInput.getText().trim().substring(txtInput.getText().trim().indexOf(":")+1);
			}
		});
		dr.add(DrStatus2);
		//DrStatus2.setSelected(true);
		DrStatus2.setBounds(5, 65, 50, 25);
		//right1.add(DrStatus2);
		
		ButtonRefreshDr = new WebButton(I18n.lang("label.refresh"));
        ButtonRefreshDr.setBackground(Setup.getColor());
        ButtonRefreshDr.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefreshDr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	getDoctor();
            }
        });
        ButtonRefreshDr.setBounds(100, 15, 80, 20);
        right1.add(ButtonRefreshDr);
		cbDR = new WebComboBox();
		cbDR.setFont(new Font("Tahoma", Font.PLAIN, 13));		 
		
		cbDR.setSize(180, 250);
		//cbDR.setSelectedIndex(0);
		cbDR.setBounds(5, 40, 180, 20);
		right1.add(cbDR);
		
		getDR();
		
		ArrayList<String> items = new ArrayList<String>();
        txtInput = new WebTextField();
        setupAutoComplete(txtInput, indr);
        txtInput.setColumns(30);
        txtInput.setBounds(5, 95, 180, 20);
        //right1.add(txtInput);
		
		MiddleSection = new WebPanel();
		MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10+400), height-160));
		MainSection.add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid1 = new WebPanel();
		mid1.setPreferredSize(new Dimension(width-((width*2)/10+400), 30));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		Label_Info = Setup.getLabel("", 13,4,SwingConstants.LEFT);
		Label_Info.setBounds(20, 5, 300, 20);
		mid1.add(Label_Info);
		
		mid2 = new WebPanel();
		MiddleSection.add(mid2, BorderLayout.CENTER);
		mid2.setLayout(null);

    }

    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData)oObservable); // cast
    }
    public void setComboBox() {
    	wardcode= InApp.reportcode.trim();
		getValueWard();
		setInit();
		cbDR.setSelectedIndex(0);
		getDataForm();
    }
    public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ButtonPrint){			 
			PrintLabLabel();
			 
		}
		else if(e.getSource() == cbRow1){
			String valueRow1=((ComboItem)cbRow1.getSelectedItem()).getValue();
			an1=valueRow1.substring(0, valueRow1.indexOf(":")).trim();
			name1=valueRow1.substring(valueRow1.indexOf(":")+1, valueRow1.indexOf("*")).trim();
			hn1=valueRow1.substring(valueRow1.indexOf("*")+1, valueRow1.indexOf("#")).trim();		 
			age1=valueRow1.substring(valueRow1.indexOf("#")+1).trim();
			 
		}
		else if(e.getSource() == cbRow2){
			String valueRow2=((ComboItem)cbRow2.getSelectedItem()).getValue();
			an2=valueRow2.substring(0, valueRow2.indexOf(":")).trim();
			name2=valueRow2.substring(valueRow2.indexOf(":")+1, valueRow2.indexOf("*")).trim();
			hn2=valueRow2.substring(valueRow2.indexOf("*")+1, valueRow2.indexOf("#")).trim();			 
			age2=valueRow2.substring(valueRow2.indexOf("#")+1).trim();
			 
		}
	}
    public void formPanel(String informcode){
		mid2.removeAll();
		String formcode=informcode.trim();
		if(formcode.equals("0019")){
			//list of patient
			Label_Row1 = Setup.getLabel("Row 1", 13,4,SwingConstants.CENTER);
			Label_Row1.setBounds(10,35,100,20);
			mid2.add(Label_Row1);
			Label_Row2 = Setup.getLabel("Row 2", 13,4,SwingConstants.CENTER);
			Label_Row2.setBounds(10,75,100,20);
			mid2.add(Label_Row2);
			cbRow1.setSize(300, 20);
			cbRow2.setSize(300, 20);
			cbRow1.setSelectedIndex(0);
			cbRow1.setBounds(120, 35, 300, 20);
			cbRow1.addActionListener(this);	
			mid2.add(cbRow1);
			
			cbRow2.setSelectedIndex(0);
			cbRow2.setBounds(120, 75, 300, 20);
			cbRow2.addActionListener(this);	
			mid2.add(cbRow2);
			
			Label_Setting = Setup.getLabel("หมายเหตุ: ขอบกระดาษด้านบนที่ห่างจากสติ๊กเกอร์แถวแรก ไม่ควรเกิน 2.5 มิลลิเมตร", 13,4,SwingConstants.CENTER);
			Label_Setting.setBounds(22,150,500,20);
			mid2.add(Label_Setting);
			
			ButtonPrint = new WebButton("Print");				 
			ButtonPrint.setBounds(200,280,120,60);
			mid2.add(ButtonPrint);
			ButtonPrint.addActionListener(this);
			ButtonPrint.setMnemonic(KeyEvent.VK_V);
		}
		else if(formcode.equals("0066")){
			
			ButtonPrint = new WebButton("Print");				 
			ButtonPrint.setBounds(200,20,100,30);
			mid2.add(ButtonPrint);
			//ButtonPrint.addActionListener(this);
			ButtonPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createForm(formcode,"2");
				}
			});
			ButtonPrint.setMnemonic(KeyEvent.VK_V);
		}
		/*else if(formcode.equals("0051")) {

        	line_med_ipd="";
			JCheckBoxTree  tree2 = new JCheckBoxTree(1000, 200);
			tree2.setPreferredSize(new Dimension(1000, 200));
			tree2.setModel(getDefaultTreeModelIPD(oUserInfo.GetPtAN())); 
		    WebScrollPane treeScroll2 = new WebScrollPane ( tree2 );
		    treeScroll2.setPreferredSize ( new Dimension (600,200) );
		    tree2.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
	            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
	            	line_med_ipd="";
	                TreePath[] paths = tree2.getCheckedPaths();
	                for (TreePath tp : paths) {
	                	 System.out.println("===>"+tp.getLastPathComponent()+"===>"+tp.getPathCount());
	                	 line_med_ipd+=tp.getLastPathComponent()+"@";
	                	                                  
	                }
	                System.out.println("*****"+line_med_ipd);
	            }           
	        }); 
		    accordion_med = new WebAccordion ( );	
		    accordion_med.addPane ( null, "Med Cont.", treeScroll2);
		    accordion_med.setBounds(5,50,600,300);
		    mid2.add(accordion_med, BorderLayout.CENTER);
		    WebButton ButtonPrint = new WebButton("print");
			ButtonPrint.addActionListener(new ActionListener() {			
				@SuppressWarnings("unused")
				public void actionPerformed(ActionEvent e) {
					 String[] parts_ipd=null;
				        if(line_med_ipd.equals("")) {
				        	
				        }else {
				        	
				        	parts_ipd = line_med_ipd.split("\\@");
				        }
				        System.out.println("*****"+line_med_ipd);
				        String test="";
			        if(parts_ipd !=null) {
			        	if(parts_ipd.length>1) {
				        	 for(int i=0;i<parts_ipd.length;i++) {
									test+=i+1+"."+parts_ipd[i]+"\n";
									test+=".....................................................................................................\n";
								}
				        }
			        }
			         
			        System.out.println(">>>>----"+test);   
			        String confFile="report/"+formcode.trim()+".jrxml";
			        JasperReportBuilder report = DynamicReports.report(); 
					//report.setPageFormat(590, 840, PageOrientation.PORTRAIT);
					InputStream in = this.getClass().getClassLoader().getResourceAsStream(confFile );
					try {
						report.setTemplateDesign(in);
						report.setPageMargin(DynamicReports.margin().setLeft(50).setRight(10).setTop(10).setBottom(10));
			
						String inBarcode=oUserInfo.GetPtVisitdate()+formcode+an;
						report.setParameter("line1", "ชื่อ " +name+" อายุ "+age+"   HN:"+hn+"   AN:"+an+"   Bed: "+bed);
						report.setParameter("line2", "Dapartment : "+department+ "   Ward: "+wardname+"   Attending Physician: " +doctorname);
			 			report.setParameter("barcode",inBarcode);
			 			report.setParameter("medcont",test);
			 			 
			 			report.setParameter("line3",formcode.trim()+".jrxml");
						report.setDataSource(new JREmptyDataSource());
						//report.print(true);
						report.show(false);
					} catch (DRException e1) {
						e1.printStackTrace();
					}
		        	line_med_ipd="";
				}
			});
			ButtonPrint.setBounds(200, 4, 89, 23);
			mid2.add(ButtonPrint);		
			mid2.validate();
			mid2.repaint();	
			
		}*/else{
			mid2.removeAll();
		}
		mid2.revalidate();
		mid2.repaint();
	}
    public void createForm(String formcode,String typeofprint)   {
		//System.out.println(formcode);
		name=oUserInfo.GetPtName().trim();
		an=oUserInfo.GetPtAN().trim();
		hn=oUserInfo.GetPtHN().trim();
		age=oUserInfo.GetPtAge().trim();
		bed=oUserInfo.GetPtBed();
		doctorname=((ComboItem)cbDR.getSelectedItem()).getKey();
		
		//System.out.println(">>"+txtInput.getText());
		//doctorname="  "+txtInput.getText().trim().substring(txtInput.getText().trim().indexOf(":")+1);
		
		cid=oUserInfo.GetPtCID();
		String date_print=Setup.ConvertDateTimePrint(day)+" น. ";
		String label=oUserInfo.GetPtLabel();
		String right=label.substring(label.indexOf(":")+1);
		String h_print=typeofprint.trim();
		String confFile="report/"+formcode.trim()+".jrxml";
		InputStream is;
		JasperReportBuilder report = DynamicReports.report(); 
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(confFile );
			report.setTemplateDesign(is);
			
			if(formcode.trim().equals("0021") || formcode.trim().equals("0022") || formcode.trim().equals("0023") || formcode.trim().equals("0024")){
				String inBarcode=Setup.DateInDBMSSQLRef43no543(oUserInfo.GetPtVisitdate())+formcode+InApp.reportcode.trim()+"0000"+an;
				report.setParameter("barcode",inBarcode);
				report.setParameter("line3", "ชื่อ " +name+" อายุ "+age+" เลขที่บัตรประชาชน: "+cid);
				report.setParameter("line8", "HN:    "+hn+"   AN:   "+an+"   Bed: "+bed+"    VN: .................................");
				report.setParameter("line4","วันที่ "+date_print +"  "+right);
				report.setParameter("line5","***การแพ้ยา***"+oUserInfo.GetMemo());
				report.setParameter("line6",oUserInfo.GetMemo1());
				report.setParameter("line7",formcode.trim()+".jrxml");
				//System.out.println(inBarcode);
				report.setDataSource(new JREmptyDataSource());
			}
			else if(formcode.trim().equals("0026")){
				String inBarcode=Setup.DateInDBMSSQLRef43no543(oUserInfo.GetPtVisitdate())+formcode+InApp.reportcode.trim()+"0000"+an;
				report.setParameter("barcode",inBarcode);
				report.setParameter("line2", "ชื่อ " +name+"    อายุ "+age+"    HN: "+hn+"  AN: "+an+"  แพทย์: " +doctorname);
				report.setParameter("line1", " "+bed);
				
				report.setParameter("line7",formcode.trim()+".jrxml");
				//System.out.println(inBarcode);
				report.setDataSource(new JREmptyDataSource());
			}
			else if( formcode.trim().equals("0027")){
				String inBarcode=Setup.DateInDBMSSQLRef43no543(oUserInfo.GetPtVisitdate())+formcode+InApp.reportcode.trim()+"0000"+an;
				report.setParameter("barcode",inBarcode);
				report.setParameter("line2", "ชื่อ " +name+"    อายุ "+age+"    HN: "+hn+"  AN: "+an+"  แพทย์: " +doctorname);
				report.setParameter("line1", " "+bed);
				
				report.setParameter("line7",formcode.trim()+".jrxml");
				//System.out.println(inBarcode);
				report.setDataSource(new JREmptyDataSource());
			}
			else if(formcode.trim().equals("0038")){
				String inBarcode=Setup.DateInDBMSSQLRef43no543(oUserInfo.GetPtVisitdate())+formcode+InApp.reportcode.trim()+"0000"+an;
				report.setParameter("barcode",inBarcode);
				report.setParameter("line1", "ชื่อ " +name+" อายุ "+age+"   HN:"+hn+"   AN:"+an+"   Bed: "+bed);
				report.setParameter("line2", "Dapartment : "+department+ "   Ward: "+wardname+"   Attending Physician: " +doctorname);
				report.setParameter("line3", "ชื่อผู้ป่วย " +name+" อายุ "+age);
				report.setParameter("line4", "ตึก: "+wardname+"  เตียง: "+bed);
				report.setParameter("line7",formcode.trim()+".jrxml");
				//System.out.println(inBarcode);
				report.setDataSource(new JREmptyDataSource());
			}
			else if(formcode.trim().equals("0066")){
				String inBarcode=Setup.DateInDBMSSQLRef43no543(oUserInfo.GetPtVisitdate())+formcode+"0000"+an+hn;
				report.setParameter("line1", "ชื่อ " +name+" อายุ "+age+"   HN:"+hn+"   AN:"+an+"   Bed: "+bed);
				report.setParameter("line2", "Dapartment : "+department+ "   Ward: "+wardname+"   Attending Physician: " +doctorname);
	 			report.setParameter("barcode",inBarcode);
	 			report.setParameter("line3",formcode.trim()+".jrxml");
				report.setDataSource(new JREmptyDataSource());
			}
			else{
				String inBarcode=oUserInfo.GetPtVisitdate()+formcode+an;
				report.setParameter("line1", "ชื่อ " +name+" อายุ "+age+"   HN:"+hn+"   AN:"+an+"   Bed: "+bed);
				report.setParameter("line2", "Dapartment : "+department+ "   Ward: "+wardname+"   Attending Physician: " +doctorname);
	 			report.setParameter("barcode",inBarcode);
	 			report.setParameter("line3",formcode.trim()+".jrxml");
				report.setDataSource(new JREmptyDataSource());
			}
			 			 
			report.print(true);
			//report.show(false);
		} catch (DRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public void setInit(){
		
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and ward='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length>1){
			sql_user1="and ward in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
		}
		
		//String[] dr=null;
		//dr = doctor.split("\\,");
		 
		 
		int p=0;
		 //String query="select an,firstname,lastname,hn,usedrightcode,outdatetime,BirthDateTime,sex,maritalstatus,initialnamecode,ref,bedno from in_view_pt_ipd  where  OUTDATETIME  is null   and suffix='0' "+sql_user+" order by bedno ";			
		 String query="select admmaster.an,admmaster.hn,admbed.outdatetime,admbed.bedno,admmaster.admdatetime from admmaster,admbed  where admmaster.an=admbed.an and  OUTDATETIME  is null  "+sql_user+" order by admbed.bedno ";
		try {

			Connection conn3=new DBmanager().getConnMSSql();
			
			PreparedStatement stmtRow = conn3.prepareStatement(query);
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
				cbRow1.addItem(new ComboItem("AN: "+rsRow.getString(1).trim()+"   "+Setup.getName(rsRow.getString(2).trim()),rsRow.getString(1).trim()+":"+Setup.getName(rsRow.getString(2).trim())+"*"+rsRow.getString(2).trim()+"#"+age_pt));
				cbRow2.addItem(new ComboItem("AN: "+rsRow.getString(1).trim()+"   "+Setup.getName(rsRow.getString(2).trim()),rsRow.getString(1).trim()+":"+Setup.getName(rsRow.getString(2).trim())+"*"+rsRow.getString(2).trim()+"#"+age_pt));
				
			}
			stmtRow.close();
			conn3.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getDoctor();
		 
	}
	public void PrintLabLabel() {
		PrintIPDLabLabel pr1= new PrintIPDLabLabel();	
		pr1.setName1(name1);
		pr1.setHN1(hn1);
		pr1.setAN1(an1);
		pr1.setAge1(age1);
		pr1.setDate(oUserInfo.GetPtVisitdate());
		pr1.setWardNameShort(ward_name_short);
		pr1.setPosition("1");
		
		pr1.setName2(name2);
		pr1.setHN2(hn2);
		pr1.setAN2(an2);
		pr1.setAge2(age2);
		
		pr1.showPrinter();
	}
	public void getValueWard(){
		 
		Connection conn;
		PreparedStatement stmt;
		String query =  "select ward_name, ward_department,department_id,ward_name_short,doctor,report from ward_ipd where  status='1'  and ward_code='"+wardcode+"'" ;
		
		try {
			conn=new DBmanager().getConnMySql();
	        stmt = conn.prepareStatement(query);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {	        	 
	        	wardname=rs.getString(1).trim();
		    	department=rs.getString(2).trim();
		    	department_id=rs.getString(3).trim();
		    	ward_name_short=rs.getString(4).trim();
		    	doctor=rs.getString(5).trim();
		    	formward=rs.getString(6).trim();
	        }
	        stmt.close();
	    	conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getDataForm(){
		tableIPDform.setModel(fetchDataForm());
		TableColumnModel columnModelIPDform = tableIPDform.getColumnModel();		
		columnModelIPDform.getColumn(0).setPreferredWidth(0);
		columnModelIPDform.getColumn(0).setMinWidth(0);
		columnModelIPDform.getColumn(0).setMaxWidth(0);
		columnModelIPDform.getColumn(1).setPreferredWidth(260);

		((DefaultTableModel)tableIPDform.getModel()).fireTableDataChanged();
		 
	}
	public  DefaultTableModel fetchDataForm(){
		/*
		String[] parts=null;
		parts = InApp.userappcode.split("\\,");
		String sql_user1="",sql_user="";
		if(parts.length==1){
			for(int i=0;i<parts.length;i++){
				sql_user1=" and formipdnurse.ward_code='"+parts[i]+"'";
			}
			sql_user=sql_user1;
		}
		else if(parts.length==0){
			sql_user="";
		}
		else if(parts.length>1){
			sql_user1="and formipdnurse.ward_code in (";
			for(int i=0;i<parts.length;i++){
				sql_user1+="'"+parts[i]+"',";				 
			}
			sql_user=sql_user1.substring(0,sql_user1.length()-1)+")";
		}
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Connection conn ;
		PreparedStatement stmt ;
		String query =  "select distinct formipdnurse.form_code, formipd_master.form_name from formipdnurse,formipd_master where formipdnurse.form_code=formipd_master.form_code and formipdnurse.type='1'  "+sql_user+" order by formipdnurse.form_code" ;
		try {
			conn=new DBmanager().getConnMySql();
	        stmt = conn.prepareStatement(query);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	final Vector<String> vstring = new Vector<String>();
	        	vstring.add(" "+rs.getString(1).trim());
	        	vstring.add(" "+rs.getString(2).trim());
	      
	        	data.add(vstring);
	        }
	        stmt.close();
	    	conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		 
		String query =  "select ward_name, ward_department,department_id,ward_name_short,doctor,report from ward_ipd where  status='1'  and ward_code='"+wardcode+"'" ;
		
		Connection confw=new DBmanager().getConnMySql();
		PreparedStatement stmtfw;
		try {
			stmtfw = confw.prepareStatement(query);
			ResultSet rsfw = stmtfw.executeQuery();
			while (rsfw.next()) {
				formward=rsfw.getString(6).trim();
			}
			stmtfw.close();
			confw.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		 
		String[] report=null;
		report = formward.split("\\,");
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		 
		for(int i=0;i<report.length;i++){
			try {
				Connection conn=new DBmanager().getConnMySql();
				String queryDR =  "select  distinct form_name from formipd_master where form_code=?" ;
				PreparedStatement stmtDR = conn.prepareStatement(queryDR);
				stmtDR.setString(1,report[i].trim());
				ResultSet rsDR = stmtDR.executeQuery();
				
				while (rsDR.next()) {	
					final Vector<String> vstring = new Vector<String>();
					vstring.add(" "+report[i].trim());
		        	vstring.add(" "+rsDR.getString(1).trim());
		      
		        	data.add(vstring);

				}
				rsDR.close();
				stmtDR.close();			
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		return new DefaultTableModel(data, columnNamesIPDform);
	}
	public void getDoctor() {
		String[] dr=null;
		dr = doctor.split("\\,");
		
		for(int i=0;i<dr.length;i++){
			//System.out.println("--++"+dr[i]);
			try {
				 
				Connection conn=new DBmanager().getConnMySql();
				String queryDR =  "select  distinct doctor_name from doctor where doctor_id=?" ;
				PreparedStatement stmtDR = conn.prepareStatement(queryDR);
				stmtDR.setString(1,dr[i].trim());
				ResultSet rsDR = stmtDR.executeQuery();
				
				while (rsDR.next()) {	
					//System.out.println(rsDR.getString(1).trim()+" -"+dr[i].trim());
					cbDR.addItem(new ComboItem(rsDR.getString(1).trim(),dr[i].trim()));
				}
				rsDR.close();
				stmtDR.close();			
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
		
	}
	public void getDR() {
		String query="select doctor,thainame from hndoctor  order by doctor";
		Connection conn = new DBmanager().getConnMSSql();
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	String drcode="",drname="";
	        	if(rs.getString(1)!=null) {
	        		drcode=rs.getString(1).trim();
	        	}
	        	if(rs.getString(2)!=null) {
	        		if(rs.getString(2).trim().indexOf("\\")>0) {
	        			drname=rs.getString(2).trim().substring(rs.getString(2).trim().indexOf("\\")+1)+" "+rs.getString(2).trim().substring(1,rs.getString(2).trim().indexOf("\\"));
	        		}else {
	        			drname=rs.getString(2).trim().substring(1);
	        		}
	        	}
	        	if(drcode !="" && drname !="") {
	        		if(StringUtils.isNumeric(drcode)) {
	        			//System.out.println(drcode+":"+drname);
	        			//indr.add(drcode+":"+drname);
	        			indr.add(drcode+":"+drname);
	        		}
	        		 
	        	}
	        	  
	        }
	        stmt.close();
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
    public WebTreeModel getDefaultTreeModelIPD (String an){
		UniqueNode root = new UniqueNode ( "AN :"+an );
		//UniqueNode parent=null;
		int node_=0;
		Connection conn ;
		conn=new DBmanager().getConnMSSql();
		PreparedStatement stmt,stmt1,stmt2,stmt3,stmt4,stmt31 ;
		String query_ipd =  "select  ipdcontdrug.firststartdatetime,ipdcontdrug.offdatetime,stock_master.englishname,ipdcontdrug.qty,ipdcontdrug.dosecode,ipdcontdrug.dosetype,ipdcontdrug.doseunitcode,ipdcontdrug.doseqtycode,ipdcontdrug.dosememo,ipdcontdrug.auxlabel1,ipdcontdrug.auxlabel2,ipdcontdrug.auxlabel3 from ipdcontdrug,stock_master where ipdcontdrug.stockcode=stock_master.stockcode and ipdcontdrug.an='"+an+"' ";
		try {			 
			node_=1;
			stmt2 = conn.prepareStatement(query_ipd);
			//stmt2.setString(1,an);
	        ResultSet rs2 = stmt2.executeQuery();
	        while (rs2.next()) {
	        	String drug="",q="",dc="",dt="",dqc="",duc="",aux1="",aux2="",aux3="",dosememo="";
	        	if(rs2.getString(3) !=null){
	        		drug=rs2.getString(3).trim().substring(1);
	        	}
	        	if(rs2.getString(4) !=null){
	        		q=rs2.getString(4).trim();
	        	}
	        	if(rs2.getString(5) !=null){
	        		for(int i=0;i<IPDFrame.getDoseCode().length;i++){
						if(rs2.getString(5).trim().equals(IPDFrame.getDoseCode()[i][0])){
							dc=IPDFrame.getDoseCode()[i][1];
						}
					}
	        	}
	        	if(rs2.getString(6) !=null){
	        		for(int i=0;i<IPDFrame.getDoseType().length;i++){
						if(rs2.getString(6).trim().equals(IPDFrame.getDoseType()[i][0])){
							dt=IPDFrame.getDoseType()[i][1];
						}
					}
	        	}
	        	if(rs2.getString(7) !=null){
	        		for(int i=0;i<IPDFrame.getDoseUnitCode().length;i++){
						if(rs2.getString(7).trim().equals(IPDFrame.getDoseUnitCode()[i][0])){
							duc=IPDFrame.getDoseUnitCode()[i][1];
						}
					}
	        	}
	        	if(rs2.getString(8) !=null){
	        		dqc=rs2.getString(8).trim();
	        	}
	        	if(rs2.getString(9) !=null){
	        		dosememo=rs2.getString(9).trim();
	        	}
	        	if(rs2.getString(10) !=null){
	        		for(int i=0;i<IPDFrame.getAuxLabel1().length;i++){
						if(rs2.getString(10).trim().equals(IPDFrame.getAuxLabel1()[i][0])){
							aux1="--"+IPDFrame.getAuxLabel1()[i][1]+"";
						}
					}
	        	}
	        	if(rs2.getString(11) !=null){
	        		for(int i=0;i<IPDFrame.getAuxLabel1().length;i++){
						if(rs2.getString(11).trim().equals(IPDFrame.getAuxLabel1()[i][0])){
							aux2="--"+IPDFrame.getAuxLabel1()[i][1]+"";
						}
					}
	        	}
	        	if(rs2.getString(12) !=null){
	        		for(int i=0;i<IPDFrame.getAuxLabel1().length;i++){
						if(rs2.getString(12).trim().equals(IPDFrame.getAuxLabel1()[i][0])){
							aux3="--"+IPDFrame.getAuxLabel1()[i][1]+"";
						}
					}
	        	}
	        	//System.out.println(">>>>>>>"+an+">>"+drug);
	        	//parent = new UniqueNode (" [An:"+an+"]");
	        	//parent.add( new UniqueNode (drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3));
	        	UniqueNode parent = new UniqueNode(""+drug+ " "+dt+" "+dqc+" "+duc+" "+dc+"          \n\t"+dosememo+"          \n\t"+aux1+" "+aux2+" "+aux3+"\n\n");
	        	//System.out.println(drug+ " "+dt+" "+dqc+" "+duc+" --"+dc+"-- "+dosememo+" "+aux1+" "+aux2+" "+aux3);
	        	root.add ( parent );	
	        }
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
	/*select row =?
	SELECT ward
	FROM (
	    SELECT ward, ROW_NUMBER() OVER (ORDER BY makedatetime) AS RowNum
	    FROM admbed where an='6114691'
	) AS MyDerivedTable
	WHERE MyDerivedTable.RowNum =2
	*/
}
