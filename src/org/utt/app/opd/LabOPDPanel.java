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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.ui.HeaderCheckBoxHandler;
import org.utt.app.ui.HeaderRenderer;
import org.utt.app.ui.JCheckBoxTree;
import org.utt.app.ui.Status;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.tree.UniqueNode;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeModel;

public class LabOPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    
    WebSplitPane split;
    WebAccordion accordion;
    WebPanel LeftSection,MainSection,mid2,mid1,MiddleSection,bottomPanelForm;
    WebLabel Label_Info;
    WebTable tableLab;
    WebScrollPane scrollPaneLab;
    
    Vector<String> columnNamesLab;
    String [][] labunitname=null ;
	String [][] labroomname=null ;
	Vector<Vector<String>> dataLab = new Vector<Vector<String>>();
    
    public LabOPDPanel(ObjectData oUserInfo, int w, int h) {
    	 this.oUserInfo=oUserInfo;
         width=w;
         height=h;
         setLayout(new BorderLayout(0, 0));
         setPreferredSize(new Dimension(width, height));
         setBounds(0, 0, width, height);
         
         getLabValueName();
         split = new WebSplitPane(split.HORIZONTAL_SPLIT);
 		 split.setDividerLocation(300);
 		
 		 LeftSection = new WebPanel();
 		 LeftSection.setPreferredSize(new Dimension(300, height-175));
 		 split.setLeftComponent(LeftSection);
 		 LeftSection.setLayout(new BorderLayout(0, 0));
 		
 		 WebLabel label = new WebLabel("Lab");
 		 label.setFont(new Font("Tahoma", Font.PLAIN, 13));
 		 label.setHorizontalAlignment(SwingConstants.CENTER);
 		 LeftSection.add(label, BorderLayout.NORTH);
 		 
 		 bottomPanelForm = new WebPanel();
         bottomPanelForm.setBackground(Setup.getColor());
         LeftSection.add(bottomPanelForm, BorderLayout.SOUTH);
         bottomPanelForm.setLayout(null);

         bottomPanelForm.setPreferredSize(new Dimension((width*2)/10, 20));
         WebButton ButtonRefreshForm = new WebButton(I18n.lang("label.refresh"));
         ButtonRefreshForm.setBackground(Setup.getColor());
         ButtonRefreshForm.setForeground(UIManager.getColor("Button.darkShadow"));

         ButtonRefreshForm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	 getReq();
            }
         });
         ButtonRefreshForm.setBounds(((width*2)/20-45), 1, 90, 20);
         bottomPanelForm.add(ButtonRefreshForm);
         
         MainSection = new WebPanel();
         MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-200, height));
         MainSection.setLayout(new BorderLayout(0, 0));
         split.setRightComponent(MainSection);

         MiddleSection = new WebPanel();
         MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10-450), height));

         MainSection.add(MiddleSection, BorderLayout.CENTER);
         MiddleSection.setLayout(new BorderLayout(0, 0));

         mid2 = new WebPanel();
         mid2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
         mid2.setPreferredSize(new Dimension(width-((width*2)/10-480), height));
         MiddleSection.add(mid2, BorderLayout.CENTER);
         mid2.setLayout(new BorderLayout(0, 0));


         mid1 = new WebPanel();
         mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
         mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
         MiddleSection.add(mid1, BorderLayout.NORTH);
         mid1.setLayout(null);

         Label_Info = new WebLabel(" ");
         Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 13));
         Label_Info.setHorizontalAlignment(SwingConstants.LEFT);
         Label_Info.setBounds(20, 18, 300, 15);
         mid1.add(Label_Info);
         columnNamesLab = new Vector<String>();	
 		 columnNamesLab.add("");
 		 columnNamesLab.add("       Lab Name");
 		 columnNamesLab.add("       Value");
 		 columnNamesLab.add("       Unit");
 		 columnNamesLab.add("       Normal Value");
 		 
 		 DefaultTableModel modelLab = new DefaultTableModel(dataLab, columnNamesLab){
 			public Class getColumnClass(int column){
 				return getValueAt(0, column).getClass();
 			}
 		 };
 		 tableLab = new WebTable(modelLab){
 			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
 				Component c = super.prepareRenderer(renderer, row, column);			
 				if (!isRowSelected(row)){
 					c.setBackground(getBackground());
 				}
 				return c;
 			}
 			 public Class getColumnClass(int column) {
 	                switch (column) {
 	                    case 0:
 	                        return Boolean.class;
 	                    case 1:
 	                        return String.class;
 	                    case 2:
 	                        return  String.class;
 	                    case 3:
 	                        return String.class;
 	                    case 4:
 	                        return String.class;
 	                    default:
 	                        return  Boolean.class;
 	                }
 	            }
 		 };
 		 tableLab.setFont(new Font("Tahoma", Font.PLAIN, 12));
 		 tableLab.setRowHeight(25);		
 		 tableLab.setFillsViewportHeight(true);
 		 tableLab.addMouseListener(new MouseAdapter() {
 			@Override
 			public void mouseClicked(MouseEvent e) {			 
 				int row = tableLab.rowAtPoint(e.getPoint());
 		        int col = tableLab.columnAtPoint(e.getPoint());
 	
 			}
 		 });
 		
 		
 		 scrollPaneLab = new WebScrollPane(tableLab);
 		 mid2.add(scrollPaneLab, BorderLayout.CENTER);
 		 getDataLabClear();
  		
  		 add(split, BorderLayout.CENTER);
   
    	
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
     
    public void clearPanel() {
    	Label_Info.setText("");
		mid1.revalidate();
		mid1.repaint();	
		getDataLabClear();
		mid2.revalidate();
		mid2.repaint();	
		MainSection.revalidate();
		MainSection.repaint();
		if(accordion !=null) {
			LeftSection.remove(accordion);
			LeftSection.revalidate();
			LeftSection.repaint();
		}	 
	}
    public void getReq() {
    	WebTree  tree1 = new WebTree( );
		tree1.setPreferredSize(new Dimension(300, height-160));
		tree1.setModel(getDefaultTreeModel(oUserInfo.GetPtHN())); 
	    WebScrollPane treeScroll1 = new WebScrollPane ( tree1 );
	    treeScroll1.setPreferredSize ( new Dimension (width-((width*2)/10-480), height-160 ) );
	    tree1.addTreeSelectionListener(new TreeSelectionListener(){
	  
            public void valueChanged(TreeSelectionEvent e ) {
             
                String node = e.getNewLeadSelectionPath().getLastPathComponent().toString();
	    		String node1 = e.getNewLeadSelectionPath().getPathComponent(1).toString();
	    		//System.out.println(node+"..node"+node1+"--");
	    		String visitdate=node1.trim().substring(0, node1.indexOf("["));
	    		String [] ary=visitdate.split(" ");
	    		int yr=Integer.parseInt(ary[2])-543;
	    		String month=Setup.getINTmonth(ary[1].trim());
	    		//System.out.println(visitdate+"<<<<<<"+ary[0]+"-"+month+"-"+yr);
	    		//getDataLabClear();
	    		getDataLab(node,(yr+"-"+month+"-"+ary[0]));
	    		Label_Info.setText(node1+"( "+node+" )");

            }           
        }); 
	    
 		 accordion = new WebAccordion ( );
 		 accordion.addPane ( null, "REQ", treeScroll1);
 		 //accordion.addPane ( null, "Request", getFormReq() );
 		 accordion.setMultiplySelectionAllowed ( false );		
 		 LeftSection.add(accordion, BorderLayout.CENTER);
    }
    @SuppressWarnings({ "rawtypes", "unused" })
	public WebTreeModel getDefaultTreeModel (String hn){
    	//System.out.println(">>>"+hn);
    	UniqueNode root = new UniqueNode ( "HN :"+hn );
		UniqueNode parent=null;
		String query =  "select  distinct entrydatetime,requestfromvn,clinic from labreq where clinic is not null and cxlbyusercode is  null and entrydatetime > '"+Setup.GetDateMo(3)+" 00:00:00'  and labreq.hn=? order by entrydatetime desc ";
		String query_ =  "select  requestno from labreq where hn=? and entrydatetime=?";

		Connection conn ;
		PreparedStatement stmt,stmt1,stmt2,stmt3,stmt4 ;		
		conn=new DBmanager().getConnMSSql();
		
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1,hn);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	String lab_date=rs.getString(1).trim().substring(0, 10);
	        	String vn=rs.getString(2).trim();
	        	String clinic=rs.getString(3).trim();
	        	//String reqno=rs.getString(4).trim();
	        	//System.out.println(">>>"+vn+"--"+lab_date+"----"+clinic);
	        	
	        	parent = new UniqueNode (Setup.ShowThaiDateShort1(lab_date)+" [vn:"+vn+"]"+"["+clinic+"]") ;
	        	
	        	stmt1 = conn.prepareStatement(query_);
				stmt1.setString(1,hn);
				stmt1.setString(2,rs.getString(1).trim());
		        ResultSet rs1 = stmt1.executeQuery();
		        while (rs1.next()) {
		        	parent.add(new UniqueNode (rs1.getString(1).trim()));
		        }
	        	stmt1.close();
	        	
	        	 if(parent ==null) {
	     			root.add( new UniqueNode (""));
	     		}else {
	     			root.add ( parent );
	     		}      	
	        }
	        
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
   
    public void getDataLabClear(){
		tableLab.setModel(fetchDataLabClear());
		TableColumnModel columnModelLab = tableLab.getColumnModel();

		columnModelLab.getColumn(0).setPreferredWidth(40);
		columnModelLab.getColumn(1).setPreferredWidth(210);
		columnModelLab.getColumn(2).setPreferredWidth(150);
		columnModelLab.getColumn(3).setPreferredWidth(80);
		columnModelLab.getColumn(4).setPreferredWidth(150);
		((DefaultTableModel)tableLab.getModel()).fireTableDataChanged(); 
		
	}
    public  DefaultTableModel fetchDataLabClear( ){
		Vector<Vector<Object>> dataLab = new Vector<Vector<Object>>();
		return new DefaultTableModel(dataLab, columnNamesLab);
	}
	public void getDataLab(String req,String visitdate){
		tableLab.setModel(fetchDataLab(req,visitdate));
		HeaderCheckBoxHandler handler=null;
		tableLab.getTableHeader().removeMouseListener(handler);
		TableModel m = tableLab.getModel();
		if (Objects.nonNull(m)) {
            m.removeTableModelListener(handler);
        }
        super.updateUI();
        
        for (int i = 0; i < m.getColumnCount(); i++) {
            TableCellRenderer r = tableLab.getDefaultRenderer(m.getColumnClass(i));
            if (r instanceof Component) {
                SwingUtilities.updateComponentTreeUI((Component) r);
            }
        }
        TableColumn column = tableLab.getColumnModel().getColumn(0);
        column.setHeaderRenderer(new HeaderRenderer());
        column.setHeaderValue(Status.INDETERMINATE);

        handler = new HeaderCheckBoxHandler(tableLab, 0);
        m.addTableModelListener(handler);
        tableLab.getTableHeader().addMouseListener(handler);
		
		TableColumnModel columnModelLab = tableLab.getColumnModel();

		columnModelLab.getColumn(0).setPreferredWidth(40);
		columnModelLab.getColumn(1).setPreferredWidth(210);
		columnModelLab.getColumn(2).setPreferredWidth(150);
		columnModelLab.getColumn(3).setPreferredWidth(80);
		columnModelLab.getColumn(4).setPreferredWidth(150);
		
	
		((DefaultTableModel)tableLab.getModel()).fireTableDataChanged(); 
		
	}
	public  DefaultTableModel fetchDataLab(String req,String visitdate){
		Vector<Vector<Object>> dataLab = new Vector<Vector<Object>>();

			String req_no=req;
			//String query1="select labresult.labcode,tm_hn_labcode_mt.englishname, labresult.resultvalue,labresult.resultunitcode,labresult.normalresultvalue from labresult,tm_hn_labcode_mt where labresult.labcode=tm_hn_labcode_mt.code and labresult.requestno='"+req_no+"'  and labresult.cxldatetime is null order by labresult.labcode "; 
			String query1="select labresult.labcode,sysconfig.englishname, labresult.resultvalue,labresult.resultunitcode,labresult.normalresultvalue from labresult,sysconfig where labresult.labcode=sysconfig.code and labresult.hn='"+oUserInfo.GetPtHN()+"' and labresult.requestno='"+req_no+"'  and (labresult.entrydatetime between '"+visitdate+" 00:00:00' and '"+visitdate+" 23:59:59')  and sysconfig.ctrlcode='20067'  and labresult.cxldatetime is null order by labresult.labcode "; 
			
			Connection conn1;
			PreparedStatement stmt1;
			try {
				conn1 = new DBmanager().getConnMSSql();
				stmt1 = conn1.prepareStatement(query1);
				
				ResultSet rs1 = stmt1.executeQuery();
				int l=0,l2=0,l3=0,l4=0;
				while (rs1.next()) {
					final Vector<Object> vsLab = new Vector<Object>();
					String code="";
					String labname="";
					String labvalue="";
					String labunit="";
					String normalvalue="";
					if(rs1.getString(1) != null){
						code=rs1.getString(1).trim();
						 
					}
					if(rs1.getString(2) != null){
						labname=rs1.getString(2).substring(1).trim();
					}else if(rs1.getString(2) == null){
						String query_extra="select englishname from tm_hn_labcode_mt where  code='"+rs1.getString(1).trim()+"'"; 
						Connection conn11 = new DBmanager().getConnMSSql();
						PreparedStatement stmt11 = conn11.prepareStatement(query_extra);
						
						ResultSet rs11 = stmt11.executeQuery();
						while (rs11.next()) {
							labname=rs11.getString(1).substring(1).trim();
						}
						stmt11.close();
						conn11.close();
					}
					if(rs1.getString(3) != null){
						labvalue=rs1.getString(3).trim();
					}
					if(rs1.getString(4) != null){
					 
						for(int i=0;i<labunitname.length;i++){
							if(rs1.getString(4).trim().equals(labunitname[i][0])){
								labunit=labunitname[i][1];
								break;
							}
							//System.out.println(i+1+". "+labunitname[i][0]);
						}
					}else{
						 
					}
					if(rs1.getString(5) != null){
						normalvalue="("+rs1.getString(5).trim()+")";
					}
					//compareValue(labvalue,normalvalue);
					 
	
					//System.out.println(code+" ** "+labname+"  "+labvalue+"  "+labunit+normalvalue);
					vsLab.add(false);
					vsLab.add(" "+labname);
					//vsLab.add(" "+labname+"<"+code+">");
					vsLab.add("      "+labvalue);
					vsLab.add(" "+labunit);
					vsLab.add("          "+normalvalue);
					dataLab.add(vsLab);
					
		}
			stmt1.close();
			conn1.close();
		}
		catch  (SQLException e) {
			e.printStackTrace();
		}
			
			//System.out.println(i+1+". "+labunitname[i][0]);
		
		
		
		return new DefaultTableModel(dataLab, columnNamesLab);
	}
	public void getLabValueName(){
		int p=0;
		 
		//get unit name
		Connection conn3;
		PreparedStatement stmt31,stmt32,stmt33,stmt34;
		try {
			conn3 = new DBmanager().getConnMSSql();
			String query31="select code from sysconfig where ctrlcode='20071'";
			stmt31 = conn3.prepareStatement(query31);
			 
			ResultSet rs31 = stmt31.executeQuery();
			 
			while (rs31.next()) {
				p++;
			}
			stmt31.close();
			labunitname = new String [p][2];
			//System.out.println(p);
			String query32="select code,englishname from sysconfig where ctrlcode='20071'";
			stmt32 = conn3.prepareStatement(query32);			 
			ResultSet rs32 = stmt32.executeQuery();
			int pp=0; 
			while (rs32.next()) {
				//System.out.println(rs2.getString(1));
				 labunitname[pp][0]=rs32.getString(1);
				 labunitname[pp][1]=rs32.getString(2).substring(1);
				 pp++;
			}
			stmt32.close();
			//for(int i=0;i<labunitname.length;i++){
			//	System.out.println(i+1+". "+labunitname[i][0]);
			//}
			
			//get lab fac name 
			int p1=0;
			String query33="select code from sysconfig where ctrlcode='20045'";
			stmt33 = conn3.prepareStatement(query33);			 
			ResultSet rs33 = stmt33.executeQuery();
			 
			while (rs33.next()) {
				p1++;
			}
			stmt33.close();
			labroomname = new String [p1][2];
			//System.out.println(p);
			String query34="select code,thainame from sysconfig where ctrlcode='20045'";
			stmt34 = conn3.prepareStatement(query34);				 			 
			ResultSet rs34 = stmt34.executeQuery();
			int pp1=0; 
			while (rs34.next()) {
				//System.out.println(rs2.getString(1));
				 labroomname[pp1][0]=rs34.getString(1);
				 if(rs34.getString(2) !=null){
				 labroomname[pp1][1]=rs34.getString(2).substring(1);
				 }else{
					 labroomname[pp1][1]="";
				 }
				 pp1++;
			}
			stmt34.close();
			for(int i=0;i<labroomname.length;i++){
				//System.out.println(i+1+". "+labroomname[i][0]+"---"+labroomname[i][1]);
			}
			
			conn3.close();
		}
		catch  (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void compareValue(String lab,String norm) {
		double lab_= Double.parseDouble(lab);
		//String norm
		//double lab_= Double.parseDouble(lab);
		
	}
    
     

}
 

 
 


