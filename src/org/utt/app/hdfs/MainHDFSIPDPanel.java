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
package org.utt.app.hdfs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Prop;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.Setup;
import org.utt.app.ui.ZoomPane;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class MainHDFSIPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebPanel LeftSection,RightSection,MiddleSection,MainSection;
    WebPanel right1,mid2,mid1,right2,right21,right22;
    WebSplitPane split;
    WebAccordion accordion;
    WebLabel Label_Info,labelImage;
    Vector<String> columnNames;
    WebTable table;
    WebButton ButtonSearch;
    WebScrollPane scrollPaneImg;
    ZoomPane pane;
    String fn="";
    
	public MainHDFSIPDPanel(ObjectData oUserInfo, int w, int h) {
		 this.oUserInfo=oUserInfo;
	        width=w;
	        height=h;
	        setLayout(new BorderLayout(0, 0));
	        setPreferredSize(new Dimension(width, height));
	        setBounds(0, 0, width, height);
	        
	        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
	        split.setDividerLocation(150);
	        LeftSection = new WebPanel();
	        LeftSection.setPreferredSize(new Dimension(200, height));
	        split.setLeftComponent(LeftSection);
	        LeftSection.setLayout(new BorderLayout(0, 0));

	        WebLabel label = new WebLabel("รายการสแกน");
	        label.setFont(new Font("Tahoma", Font.PLAIN, 13));
	        label.setHorizontalAlignment(SwingConstants.CENTER);
	        LeftSection.add(label, BorderLayout.NORTH);
	        
	        accordion = new WebAccordion( );
	        accordion.addPane ( null, "Doctor Order Sheet", getHDFS(oUserInfo.GetPtAN()));
	        accordion.setMultiplySelectionAllowed ( false );
	        LeftSection.add(accordion, BorderLayout.CENTER);
	        
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
	        mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u0E23\u0E32\u0E22\u0E25\u0E30\u0E40\u0E2D\u0E35\u0E22\u0E14\u0E02\u0E2D\u0E07\u0E44\u0E1F\u0E25\u0E4C", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
	        mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
	        MiddleSection.add(mid1, BorderLayout.NORTH);
	        mid1.setLayout(null);
	        
	        labelImage = new ScaledImageLabel();
	        labelImage.setPreferredSize(new Dimension(16, 16));
	        //labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));
	        labelImage.setBounds(5, 14, 16, 16);
	        mid1.add(labelImage);

	        Label_Info = new WebLabel(" ");
	        Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 13));
	        Label_Info.setHorizontalAlignment(SwingConstants.LEFT);
	        Label_Info.setBounds(20, 18, 300, 15);
	        mid1.add(Label_Info);

	        add(split, BorderLayout.CENTER);
	        
	}
	 public void update(Observable oObservable, Object oObject) {
	        oUserInfo = ((ObjectData) oObservable); // cast
	}
	 public WebPanel getHDFS(String an){ 
    	 
			WebPanel wp = new WebPanel();
			wp.setLayout(new BorderLayout(0, 0));
			wp.setPreferredSize(new Dimension((width*2)/10,75));
			
			columnNames = new Vector<String>();		
			columnNames.add("");
			columnNames.add("");
			columnNames.add("");
			
			Vector<Vector<String>> data = new Vector<Vector<String>>();
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
					}
					return c;
				}
			};
			
			table.setEditable (false );
			table.setFillsViewportHeight(true);
	        table.setPreferredScrollableViewportSize ( new Dimension ((width*2)/10, 100 ) );
	        table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int row = table.rowAtPoint(e.getPoint());
			        int col = table.columnAtPoint(e.getPoint());
			        
			        String filename=table.getValueAt(row, 2).toString().trim();
			        int l=filename.length();
			        String d=filename.substring(6, 8).trim();
			        String m=filename.substring(4, 6).trim();
			        String y=filename.substring(0, 4).trim();
			        String c=filename.substring(8, 12).trim();
			        String p=filename.substring(12,14).trim();
			        String ch1=filename.substring(14,16).trim();
	                String ch2=filename.substring(16,18).trim();
	                String permission="";
	                if(ch1.equals("00")) {
	                	permission="Public";
	                	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/unlock.png")));
	                }
	                else if(ch1.equals("01")) {
	                	permission="Group";
	                	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/unlock.png")));

	                }
	                else if(ch1.equals("02")) {
	                	permission="Owner";
	                	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/lock.png")));

	                }
			        Label_Info.setText("  "+d+" "+Setup.getMonthShortThaiName(m)+" "+y+"  Formcode:  "+c);
			        mid2.removeAll();
			        mid2.revalidate();
			        mid2.repaint();
			        
					showFilePDF(filename.trim().substring(0,filename.trim().indexOf(":")),filename.trim().substring(filename.trim().indexOf(":")+1));
					System.out.println(filename.trim().substring(0,filename.trim().indexOf(":"))+"--**--"+filename.trim().substring(filename.trim().indexOf(":")+1));
					
				}
			});
			        
	        WebScrollPane scrollPane = new WebScrollPane ( table );      
	        wp.add(scrollPane, BorderLayout.CENTER);
	        
	        ButtonSearch = new WebButton("Show Files");
			ButtonSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					labelImage.setIcon(null);
					if(Label_Info !=null) {
			    		Label_Info.setText("");		    		
			    	}
					mid2.removeAll();
					mid2.revalidate();
				    mid2.repaint();
					if(oUserInfo.GetPtAN().equals("")){
						JOptionPane.showMessageDialog(null,"กรุณาเลือก AN ","ข้อผิดพลาด",JOptionPane.WARNING_MESSAGE); 
						 
					}else{
						getData(oUserInfo.GetPtHN(),oUserInfo.GetPtAN());
					}
					 
				}
			});
			ButtonSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
			 
			wp.add(ButtonSearch, BorderLayout.SOUTH);
			
			return wp;
		}

		public void getData(String hn,String an){
			table.setModel(fetchData(hn,an));
			TableColumnModel columnModel = table.getColumnModel();		
			columnModel.getColumn(0).setPreferredWidth(40);
			columnModel.getColumn(1).setPreferredWidth(220);
			columnModel.getColumn(2).setPreferredWidth(0);
			columnModel.getColumn(2).setMinWidth(0);
			columnModel.getColumn(2).setMaxWidth(0);
			((DefaultTableModel)table.getModel()).fireTableDataChanged();
			
		}
		public  DefaultTableModel fetchData(String hn,String an){
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			String folderhn=hn.substring(0, 2).trim();
			String path1="/utth/"+folderhn+"/"+hn+"/"+an;
			 
			FileSystem fs;
			try {
				fs = connHDFS();
				Path newFolderPath= new Path(path1);
				FileStatus[] fileStatus = fs.listStatus(newFolderPath);
				//Array Sorting Descending Order
				Arrays.sort(fileStatus, Collections.reverseOrder());
				 
				for(FileStatus status : fileStatus){
					String pp=status.getPath().toString().trim();
					System.out.println(pp+"---"+pp.length());
					if(pp.substring(pp.length()-3).equals("pdf") || pp.substring(pp.length()-3).equals("PDF")) {
						if(pp.length()==85) {
							String s3=pp.substring((((Prop.getProperty("hadoop.server")+path1).length())+1),pp.indexOf("A"));
							String ppp=s3.substring(0,8);
							String pppp=s3.substring(8,12);
							String ss=s3.substring( 12,14);
							System.out.println(pp+"---"+pp.length()+"--"+s3);
							 
	        				 
	        				String pp1=ppp;
	        				String year_fn=pp1.substring(0, 4);	             
	        	            String month_fn=pp1.substring(4, 6);            
	        	            String date_fn=pp1.substring(6, 8);
	        	            String formcode=pppp;
	        	            String page_fn=ss;
	        	            
	        	            String visitdate=Setup.DateInDBMSSQLno(s3.substring(0,8));
	        	            Connection con;
	        				String sql_in = "select flag from txlog where hn='"+hn+"' and an='"+an+"' and visitdate='"+visitdate+"' and servroom='"+pppp+"' and status='1' order by flag";
	        				try {
	        					Connection conn = new DBmanager().getConnMySql();
	        					//System.out.println(id+"******************"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"----"+oUserInfo.GetPtHN()+"---****--"+oUserInfo.GetPtCliniccode()+"-----"+Setup.GetDateTimeNow());
	        					PreparedStatement stmt = conn.prepareStatement(sql_in);
	        					ResultSet rs = stmt.executeQuery();
	        					int num=1;
	        			        while (rs.next()) {
	        			        	final Vector<String> vstring = new Vector<String>();
	        			        	if(rs.getString(1)!=null) {
	        			        		vstring.add(" "+num );
	        			        		vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+":"+rs.getString(1).trim());
	        	        				vstring.add(s3+":"+rs.getString(1).trim());	
	        	        				data.add(vstring);
	        	        				num++;
	        			        	}
	        			        }
	        					stmt.close();
	        					conn.close();
	        				} catch (SQLException e) {
	        					e.printStackTrace();
	        				}
	        	            
	        				            				
	        				 
	        				 
	        				 
						}
		
					}
					 
	    	    }
					
				fs.close();
			} catch (IOException e) {
				//e.printStackTrace();
				WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn,"File Error",WebOptionPane.ERROR_MESSAGE);
			}
		
			return new DefaultTableModel(data, columnNames);
		}
		public FileSystem connHDFS() {
	    	FileSystem f=null;
	    	
	    	Configuration conf = new Configuration();
			conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
			System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
			System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
			try {
				f = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	return f;   	
	    }
		public  void showFilePDF(String filename,String point){
			mid2.removeAll();
			fn=filename;
			pane = new ZoomPane(previewPDFDocumentInImage(filename,oUserInfo.GetPtHN(),point));       
            scrollPaneImg = new WebScrollPane(pane);
            pane.centerInViewport();
    		mid2.add(scrollPaneImg, BorderLayout.CENTER);
			
			mid2.revalidate();
			mid2.repaint();
		}
		public  Image previewPDFDocumentInImage(String filescanname,String hn,String point){
			Image img=null;
			Rectangle rect=null;
			ByteBuffer buf = null;
			String folderhn=hn.substring(0, 2).trim();
			String an=filescanname.substring(filescanname.length()-14, filescanname.length()-7);
			System.out.println(an+"***");
			String path1="/utth/"+folderhn+"/"+hn+"/"+an;
			
			FileSystem fs ;		 
			PDFFile pdffile;
			try {
				 fs = connHDFS();
				 Path newFolderPath= new Path(path1);
				 Path path = new Path(newFolderPath+"/"+filescanname.trim()+"AN.pdf");
				    if (!fs.exists(path)) {}
				 FSDataInputStream in = fs.open(path);
			     byte[] b= IOUtils.toByteArray(in);
			     buf = ByteBuffer.wrap(b);
				
				 pdffile = new PDFFile(buf);
				 PDFPage page = pdffile.getPage(1);
				 int height_a;
				 if(point.equals("1")) {
					 height_a=(int)page.getBBox().getHeight();
				 }
				 else if(point.equals("2")) {
					 height_a=(int)page.getBBox().getHeight()-860;
				 }
				 else if(point.equals("3")) {
					 height_a=(int)page.getBBox().getHeight()-1440;
				 }
				 else {
					 height_a=(int)page.getBBox().getHeight();
				 }
		    	 rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(),  height_a);
		         img = page.getImage(800, 1200, rect, null, true, true);
				 in.close();
				 fs.close();
				 Setup.getlog(hn, InApp.ip, fn, "");
		
			} catch (IOException e) {
				//e.printStackTrace();
				img=null;
			}
			return img;    	 
		}
		public String getPoint(String fn) {
			String to="";
			String visitdate=Setup.DateInDBMSSQLno(fn.substring(0,8));
			//String visitdate_db=Setup.ConvertScantoDBDate(nameBefore.substring(0,8));
			String formcode=fn.substring(8,12);
			
			String an= fn.substring(fn.length()-14,fn.length()-7);
			String hn= fn.substring(fn.length()-7);
			
			System.out.println(visitdate+"-"+hn+"-"+an+"-"+formcode);
			Connection con;
			String sql_in = "select flag from txlog where hn='"+hn+"' and an='"+an+"' and visitdate='"+visitdate+"' and servroom='"+formcode+"' and status='1'";
			try {
				Connection conn = new DBmanager().getConnMySql();
				//System.out.println(id+"******************"+Setup.DateInDBMSSQL(oUserInfo.GetPtVisitdate())+"----"+oUserInfo.GetPtHN()+"---****--"+oUserInfo.GetPtCliniccode()+"-----"+Setup.GetDateTimeNow());
				PreparedStatement stmt = conn.prepareStatement(sql_in);
				ResultSet rs = stmt.executeQuery();
		        while (rs.next()) {
		        	if(rs.getString(1)!=null) {
		        		to=rs.getString(1).trim();
		        	}
		        }
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println(to+"*************");
			return to;
		}
}

