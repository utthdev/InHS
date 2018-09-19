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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreePath;

import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.opd.OPDFrame;
import org.utt.app.ui.JCheckBoxTree;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tree.UniqueNode;
import com.alee.laf.tree.WebTreeModel;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;

public class MedIPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebSplitPane split;
    WebPanel leftPanel,MiddleSection,mid1,mid2,MainSection;
    WebAccordion accordion_med;
    String line_med_ipd="",line_ipd="";
    GregorianCalendar day = new GregorianCalendar();
    
    String [][] shortname=null ;
    String [][] engname=null ;
    String [][] thainame=null ;
    
    public MedIPDPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        initDrugName();
        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
		split.setDividerLocation(300);

		split.setLeftComponent(leftPanel);
		
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
		
		WebLabel Label_Info = new WebLabel(" ");
		Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info.setBounds(20, 18, 300, 15);
		mid1.add(Label_Info);
		add(split, BorderLayout.CENTER);
		
		 
    	
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public void setMid() {
    	clearPanel();
    	line_ipd="";
    	line_med_ipd="";
			    	
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
		
		accordion_med = new WebAccordion ( );			 
		//accordion_med.addPane ( null, "Past Medicine OPD", treeScroll1 );
		accordion_med.addPane ( null, "Medicine IPD", treeScroll2);
		//accordion_med.setMultiplySelectionAllowed ( false );		
		mid2.add(accordion_med, BorderLayout.CENTER);
		
		WebButton ButtonPrint = new WebButton("print");
		ButtonPrint.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {

		        String[] parts_ipd=null;
		        if(line_med_ipd.equals("")) {
		        	
		        }else {
		        	//System.out.println("*****"+line_med_ipd);
		        	parts_ipd = line_med_ipd.split("\\@");
		        				        }
		         
		        String date_print="";
		        if(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim().equals(Setup.GetDateNow().trim())){
		    		date_print=Setup.ConvertDateTimePrint(day)+" น. ";
		    	}else{
		    		date_print=Setup.ShowThaiDate1(Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate()).trim())+" เวลา.......................น.";
		    	}
		        String msg1="",test="";
		        msg1=getMsg(oUserInfo.GetPtHN())+"\n";
		        if(msg1.equals("")){
		        	
		        }else {
		        	test+=getMsg(oUserInfo.GetPtHN())+"\n";
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
    
		        String confFile="report/0059.jrxml";
		        JasperReportBuilder report = DynamicReports.report(); 
				report.setPageFormat(590, 840, PageOrientation.PORTRAIT);
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(confFile );
				String inBarcode=oUserInfo.GetPtVisitdate()+"0059"+oUserInfo.GetPtAN();
				
				String name_r=getValueWard(oUserInfo.GetPtWard().trim());
				String dept="",wa="";
				dept=name_r.substring(0, name_r.indexOf("@"));
				wa=name_r.substring(name_r.indexOf("@")+1);
				try {
					report.setTemplateDesign(in);
					report.setPageMargin(DynamicReports.margin().setLeft(10).setRight(10).setTop(10).setBottom(10));
					//report.setParameter("line1", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+"   HN:"+oUserInfo.GetPtHN()+"   AN:"+oUserInfo.GetPtAN()+"   Bed: "+oUserInfo.GetPtBed());
					//report.setParameter("line2", "Dapartment : "+dept+ "   Ward: "+wa);
					report.setParameter("line1", oUserInfo.GetPtName());
					report.setParameter("line2", oUserInfo.GetPtAge());
					report.setParameter("line3", oUserInfo.GetPtAN());
					//report.setParameter("line4", wa);
					//report.setParameter("line5", wa);
					//report.setParameter("line3", "ชื่อ " +oUserInfo.GetPtName()+" อายุ "+oUserInfo.GetPtAge()+" HN:"+oUserInfo.GetPtHN()+" VN:"+oUserInfo.GetPtVN()+" เลขที่บัตรประชาชน: "+oUserInfo.GetPtCID());
					//report.setParameter("line4","วันที่ "+date_print +"  "+oUserInfo.GetRightCode());
					report.setParameter("line50",""+oUserInfo.GetMemo());
					//report.setParameter("line6",oUserInfo.GetMemo1());
					report.setParameter("line7","0059.jrxml");
					report.setParameter("labresult","");
					
					//report.setParameter("med1",parts_opd[0]);
					if(line_med_ipd.equals("")&& line_ipd.equals("")) {
						report.setParameter("med1","\n....................................................................\n... ผู้ป่วยไม่มีประวัติได้รับยาจากโรงพยาบาลอุตรดิตถ์\nรับยาจาก ...........................................");
					}else {
						test+="\n\t.......................................................แพทย์ผู้สั่ง\n";
						report.setParameter("med1",test);
					}
					 
					/*
					int line=0;
					if(parts_opd !=null) {
			        	if(parts_opd.length>=1) {
			        		 report.setParameter("med"+1,""+line_opd);
			        		 line=line+1;
				        	 for(int i=0;i<parts_opd.length;i++) {
									//System.out.println((line)+"."+parts_opd[i]);
									report.setParameter("... med"+(line+1),i+1+"."+parts_opd[i]);
									line=line+1;
							}
				        }
			        }
					//System.out.println("total line1."+line);
					//
					if(parts_ipd !=null) {
			        	if(parts_ipd.length>=1) {
			        		 report.setParameter("med"+(line+1),""+line_ipd);
			        		 line=line+1;
				        	 for(int i=0;i<parts_ipd.length;i++) {
									//System.out.println((line+1)+"."+parts_ipd[i]);
									report.setParameter("... med"+((line+1)),i+1+"."+parts_ipd[i]);
									line=line+1;
							}
				        }
			        }
					//System.out.println("total line2."+line);
					if(line<23) {
						int line_add=22-line;
						line=line+1;
						report.setParameter("med"+((line)),"... ไม่มีประวัติใช้ยาในโรงพยาบาลอุตรดิตถ์");
						System.out.println("total line2noooo."+line);
						for(int i=0;i<line_add-1;i++) {
							report.setParameter("med"+((line+1)),"");
							line=line+1;
							System.out.println("total line2noooo."+line);
					}
						 
						
					}
					
					*/
					
					report.setParameter("barcode",inBarcode);
					report.setDataSource(new JREmptyDataSource());
					//report.print(true);
					report.show(false);
				} catch (DRException e1) {
					e1.printStackTrace();
				}

	        	line_ipd="";
	        	line_med_ipd="";
			}
		});
		ButtonPrint.setBounds(200, 4, 89, 23);
		mid1.add(ButtonPrint);		
		mid1.validate();
		mid1.repaint();	
    	
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
    public WebTreeModel getDefaultTreeModel (String hn){
		UniqueNode root = new UniqueNode ( "HN :"+hn );
		UniqueNode parent=null;
		String query =  "select distinct vnmst.visitdate,vnmst.vn,vnpres.clinic,vnpres.suffix from vnmst,vnpres where vnmst.vn=vnpres.vn and vnmst.visitdate=vnpres.visitdate and vnmst.visitdate > '"+Setup.GetDateMo(6)+"'  and vnmst.hn=? order by vnmst.visitdate desc ";
		//String query_d =  "select  stock_master.shortname,vnmedicine.dosecode,vnmedicine.dosetype,vnmedicine.doseunitcode,vnmedicine.doseqtycode ,vnmedicine.auxlabel1,vnmedicine.RETURNDRUGREASON,vnmedicine.paidamt,vnmedicine.auxlabel2,vnmedicine.auxlabel3,vnmedicine.dosememo from vnmedicine,stock_master where "
		//		+ " vnmedicine.stockcode=stock_master.stockcode and vnmedicine.vn=? and vnmedicine.visitdate=?  and vnmedicine.cxldatetime is null  order by vnmedicine.subsuffix ";
		 
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
	        	//System.out.println(Setup.ShowThaiDate1(drug_date)+" [vn:"+vn+"]");
	        	String query_d =  "select  stockcode,dosecode,dosetype,doseunitcode,doseqtycode ,auxlabel1,RETURNDRUGREASON,paidamt,auxlabel2,auxlabel3,dosememo from vnmedicine where "
	    				+ "  vn=? and visitdate=? and suffix='"+suffix+"'  and cxldatetime is null  order by subsuffix ";
	    		
	        	stmt1 = conn.prepareStatement(query_d);
	        	stmt1.setString(1,vn.trim());
		        stmt1.setString(2,drug_date.trim());
	        	ResultSet rs1=stmt1.executeQuery();
	        	while (rs1.next()) {
	        		String drug="",ned="",dc="",dt="",dqc="",aux="",duc="",q="",aux2="",aux3="",dmemo="";
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
	        		parent.add( new UniqueNode (drug+" "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3+" "+dmemo+" "+ned));        		         		 
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
		String query_ipd =  "select an from admmaster where  hn=? and dischargedatetime is null order by admdatetime desc ";
		
		//String query_ipd =  "select an from admmaster where dischargedatetime > '"+Setup.GetDateMo(6)+"'  and hn=? order by dischargedatetime desc ";
		try {			 
			 
			stmt2 = conn.prepareStatement(query_ipd);
			stmt2.setString(1,hn);
	        ResultSet rs2 = stmt2.executeQuery();
	        while (rs2.next()) {
	        	node_=1;
	        	String an=rs2.getString(1).trim();
	        	//System.out.println(">>>>>>>"+an);
	        	//parent = new UniqueNode (" [An:"+an+"]");
	        	String query_file="select  distinct orderno from ipddrughist where an='"+an+"' and  orderno is not null ";    			        	
	        	
	        	//String query_file="select  distinct orderno from ipddrughist where an='"+an+"' and medicineordertype='3' and orderno is not null ";    			        	
	        	stmt3 = conn.prepareStatement(query_file);
				ResultSet rs3 = stmt3.executeQuery();
				while (rs3.next()) {
					 if(rs3.getString(1) !=null) {
						String orderno=rs3.getString(1).trim();
						//String makedate=rs3.getString(2).trim();
						//System.out.println(orderno+"***");
						String query_file1="select  makedatetime from ipddrughist where an='"+an+"'  and orderno='"+orderno+"' ";    			        	
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
							//parent.add( new UniqueNode (drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3));
							parent.add( new UniqueNode (drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+dmemo));
							//System.out.println(orderno+"***"+makedate+"---"+drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3);
							//System.out.println(orderno+"***---"+drug+ " "+dt+" "+dqc+" "+duc+" "+dc+" "+aux3);
							
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
			
			//for(int i=0;i<shortname.length;i++){
			//	System.out.println(i+1+". "+shortname[i][0]);
			//}
			conn3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    public void clearPanel() {
		mid1.removeAll();
		mid1.revalidate();
		mid1.repaint();	
		mid2.removeAll();
		mid2.revalidate();
		mid2.repaint();	
		MiddleSection.revalidate();
		MiddleSection.repaint();

	}
    
}

