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

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.tree.UniqueNode;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeModel;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPrintPage;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.utt.app.InApp;
import org.utt.app.common.ObjectData;
import org.utt.app.dao.DBmanager;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.ui.ZoomPane;
import org.utt.app.util.PDFPrintPageHP;
import org.utt.app.util.Prop;
import org.utt.app.util.Setup;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainHDFSOPDPanel extends WebPanel implements Observer {
    ObjectData oUserInfo;
    int width,height;
    WebPanel LeftSection,RightSection,MiddleSection,MainSection;
    WebPanel right1,mid2,mid1,right2,right21,right22;
    WebSplitPane split;
    WebAccordion accordion;
    WebTable table,table3,tablescope;
    WebLabel Label_Info,labelImage;
    WebButton ButtonSearch3,ButtonSearchscope,ButtonSearch,ButtonSearchpast,ButtonSearchAn,ButtonPrint;
    WebScrollPane scrollPaneImg;
    
    ZoomPane pane;

    Vector<String> columnNames3,columnNamesscope,columnNames;
    String fn="";
    PDFFile [] pdffile,pdffile1;
    @SuppressWarnings("rawtypes")
	WebTree tree1 = new WebTree ( );
    WebTree tree2 = new WebTree ( );
    int xprint=10;
    WebRadioButton Pr1,Pr2;
    ButtonGroup PG = new ButtonGroup();

    public MainHDFSOPDPanel(ObjectData oUserInfo, int w, int h) {
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

        WebLabel label = new WebLabel("ประวัติการรักษา");
        label.setFont(new Font("Tahoma", Font.PLAIN, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        LeftSection.add(label, BorderLayout.NORTH);
        
        ButtonPrint = new WebButton("");
        ButtonPrint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                printOPD(fn);
            }
        });
        ButtonPrint.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/printer.png")));
        ButtonPrint.setBounds(460, 12, 100, 25);

        accordion = new WebAccordion( );
        accordion.addPane ( null, "Past 3 Month", getHDFS3(oUserInfo.GetPtHN()));
        //accordion.addPane ( null, "Lab", getHDFSLab(oUserInfo.GetPtHN()) );
        //accordion.addPane ( null, "X-ray", getHDFSXray(oUserInfo.GetPtHN()) );
        accordion.addPane ( null, "Scope", getHDFSScope(oUserInfo.GetPtHN()));
        accordion.addPane ( null, "All",   getHDFS(oUserInfo.GetPtHN()));
        accordion.addPane ( null, "Past EMR",   getHDFSPast(oUserInfo.GetPtHN()));
        accordion.addPane ( null, "IPD EMR",   getHDFSAn(oUserInfo.GetPtHN()));
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
        
        Pr1 = new WebRadioButton("fujitsu");
		Pr1.setBackground(Setup.getColor());
		Pr1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 xprint=10;
			}
		});
		PG.add(Pr1);
		Pr1.setBounds(320, 12, 80, 25);
		mid1.add(Pr1);
		
		Pr2 = new WebRadioButton("HP");
		Pr2.setBackground(Setup.getColor());
		Pr2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 xprint=120;
			}
		});
		PG.add(Pr2);
		Pr2.setBounds(380, 12, 80, 25);
		mid1.add(Pr2);

         
        mid1.add(ButtonPrint);

        add(split, BorderLayout.CENTER);

    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public WebPanel getHDFS(String hn){ 
    	 
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
		        Label_Info.setText("  "+d+" "+Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
		        mid2.removeAll();
		        mid2.revalidate();
		        mid2.repaint();
				showFilePDF(filename.trim());
				
			}
		});
		        
        WebScrollPane scrollPane = new WebScrollPane ( table );      
        wp.add(scrollPane, BorderLayout.CENTER);
        
        ButtonSearch = new WebButton("Show Files");
		ButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ButtonPrint.setEnabled(true);
				labelImage.setIcon(null);
				if(Label_Info !=null) {
		    		Label_Info.setText("");		    		
		    	}
				mid2.removeAll();
				mid2.revalidate();
			    mid2.repaint();
				if(oUserInfo.GetPtHN().equals("")){
					JOptionPane.showMessageDialog(null,"กรุณาเลือก HN ","ข้อผิดพลาด",JOptionPane.WARNING_MESSAGE); 
					 
				}else{
					getData(oUserInfo.GetPtHN());
				}
				 
			}
		});
		ButtonSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
		 
		wp.add(ButtonSearch, BorderLayout.SOUTH);
		
		return wp;
	}

	public void getData(String hn){
		table.setModel(fetchData(hn));
		TableColumnModel columnModel = table.getColumnModel();		
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(220);
		columnModel.getColumn(2).setPreferredWidth(0);
		columnModel.getColumn(2).setMinWidth(0);
		columnModel.getColumn(2).setMaxWidth(0);
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
		
	}
	public  DefaultTableModel fetchData(String hn){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		 
		FileSystem fs;
		try {
			fs = connHDFS();
			Path newFolderPath= new Path(path1);
			FileStatus[] fileStatus = fs.listStatus(newFolderPath);
			//Array Sorting Descending Order
			Arrays.sort(fileStatus, Collections.reverseOrder());
			int num=1;
			for(FileStatus status : fileStatus){
				String pp=status.getPath().toString().trim();
				if(pp.substring(pp.length()-3).equals("pdf") || pp.substring(pp.length()-3).equals("PDF")) {
					if(pp.length()==52 || pp.length() ==54) {
						
					}else {
					String s3=pp.substring((((Prop.getProperty("hadoop.server")+path1).length())+1),pp.indexOf("H"));
					String ppp=s3.substring(0,8);
					String pppp=s3.substring(8,12);
					String ss=s3.substring( 12,14);
					if(s3.length()==25) {
	        			String ss1=s3.substring(14,16);
		        		String ss2=pp.substring(16,18);
		        			        		
		        		if(ppp.substring(0,2).equals("25") && (ss1.equals("00") || ss1.equals("01") || ss1.equals("02") || ss1.equals("03"))) {

	        				final Vector<String> vstring = new Vector<String>();
	        				vstring.add(" "+num );
	        				String pp1=ppp;
	        				String year_fn=pp1.substring(0, 4);	             
	        	            String month_fn=pp1.substring(4, 6);            
	        	            String date_fn=pp1.substring(6, 8);
	        	            String clinic_fn=pppp;
	        	            String page_fn=ss;
	        				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
	        				vstring.add(s3);	           				
	        				 
	        				data.add(vstring);
	        				num++;
		        		}

	        		}
	        		else if(s3.length()==21) {
	        			if(ppp.substring(0,2).equals("25")) {
	        				final Vector<String> vstring = new Vector<String>();
	        				vstring.add(" "+num );
	        				String pp1=ppp;
	        				String year_fn=pp1.substring(0, 4);	             
	        	            String month_fn=pp1.substring(4, 6);            
	        	            String date_fn=pp1.substring(6, 8);
	        	            String clinic_fn=pppp;
	        	            String page_fn=ss;
	        				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
	        				vstring.add(s3);	
	                   				 
	        				data.add(vstring);
	        				num++;
		        		}
	        			
	        			
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
    public WebPanel getHDFS3(String hn){
    	 
        WebPanel wp = new WebPanel();
        wp.setLayout(new BorderLayout(0, 0));
        wp.setPreferredSize(new Dimension((width*2)/10,75));

        columnNames3 = new Vector<String>();
        columnNames3.add("");
        columnNames3.add("");
        columnNames3.add("");

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        DefaultTableModel model = new DefaultTableModel(data, columnNames3){
            public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }
        };

        table3 = new WebTable(model){
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)){
                    c.setBackground(getBackground());
                }
                return c;
            }
        };
        table3.setEditable (false );
        table3.setFillsViewportHeight(true);
        table3.setPreferredScrollableViewportSize ( new Dimension ((width*2)/10, 100 ) );
        table3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table3.rowAtPoint(e.getPoint());
                int col = table3.columnAtPoint(e.getPoint());

                String filename=table3.getValueAt(row, 2).toString().trim();
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
                	Label_Info.setText("  "+d+" "+ Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
                    mid2.removeAll();
                    mid2.revalidate();
                    mid2.repaint();
                    showFilePDF(filename.trim());
                    fn=filename.trim();
                }
                else if(ch1.equals("01")) {
                	permission="Group";
                	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/unlock.png")));
                	Label_Info.setText("  "+d+" "+ Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
                    mid2.removeAll();
                    mid2.revalidate();
                    mid2.repaint();
                    showFilePDF(filename.trim());
                    fn=filename.trim();

                }
                else if(ch1.equals("02")) {
                	permission="Owner";
                	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/lock.png")));
                	mid2.removeAll();
                    mid2.revalidate();
                    mid2.repaint();
                    Label_Info.setText(" *** "+d+" "+ Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
                	lockFilePDF();
                	fn=filename.trim();

                }
                //Label_Info.setText("  "+d+" "+ Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
                //mid2.removeAll();
                //mid2.revalidate();
                //mid2.repaint();
                //showFilePDF(filename.trim());
                //fn=filename.trim();
               
            }
        });

        WebScrollPane scrollPane = new WebScrollPane( table3 );
        wp.add(scrollPane, BorderLayout.CENTER);

        ButtonSearch3 = new WebButton("Show Files");
        ButtonSearch3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	ButtonPrint.setEnabled(false);
            	labelImage.setIcon(null);
            	if(Label_Info !=null) {
		    		Label_Info.setText("");		    		
		    	}
				mid2.removeAll();
				mid2.revalidate();
			    mid2.repaint();
                if(oUserInfo.GetPtHN().equals("")){
                    JOptionPane.showMessageDialog(null,"กรุณาเลือก HN ","ข้อผิดพลาด",JOptionPane.WARNING_MESSAGE);

                }else{
                    getData3(oUserInfo.GetPtHN());
                }
            }
        });
        ButtonSearch3.setFont(new Font("Tahoma", Font.PLAIN, 13));

        wp.add(ButtonSearch3, BorderLayout.SOUTH);

        return wp;
    }
    public void getData3(String hn){
        table3.setModel(fetchData3(hn));
        TableColumnModel columnModel = table3.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(220);
        columnModel.getColumn(2).setPreferredWidth(0);
        columnModel.getColumn(2).setMinWidth(0);
        columnModel.getColumn(2).setMaxWidth(0);
        ((DefaultTableModel)table3.getModel()).fireTableDataChanged();

    }
    public  DefaultTableModel fetchData3(String hn){
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        String folderhn=hn.substring(0, 2).trim();
        String path1="/utth/"+folderhn+"/"+hn;

        FileSystem fs;
        try {
        	fs = connHDFS();
			Path newFolderPath= new Path(path1);
			FileStatus[] fileStatus = fs.listStatus(newFolderPath);
			//Array Sorting Descending Order
			Arrays.sort(fileStatus, Collections.reverseOrder());
			int num=1;
			for(FileStatus status : fileStatus){
				 
				 
				String pp=status.getPath().toString().trim();
				if(pp.substring(pp.length()-3).equals("pdf") || pp.substring(pp.length()-3).equals("PDF")) {
					//System.out.println(pp+"************************"+pp.length());
					if(pp.length()==52 || pp.length() ==54) {
						
					}else {
					String s3=pp.substring((((Prop.getProperty("hadoop.server")+path1).length())+1),pp.indexOf("H"));
					String ppp=s3.substring(0,8);
					String pppp=s3.substring(8,12);
					String ss=s3.substring( 12,14);
					if(s3.length()==25) {
	        			String ss1=s3.substring(14,16);
		        		String ss2=pp.substring(16,18);
		        			        		
		        		if(ppp.substring(0,2).equals("25") && (ss1.equals("00") || ss1.equals("01") || ss1.equals("02") || ss1.equals("03"))) {
		        			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		        			Date date = formatter.parse(ppp);
		        			
		        			Date m3= new DateTime().minusMonths(3).toDate();
		        			if(date.after(m3)) {
		        				final Vector<String> vstring = new Vector<String>();
	            				vstring.add(" "+num );
	            				String pp1=ppp;
	            				String year_fn=pp1.substring(0, 4);	             
	            	            String month_fn=pp1.substring(4, 6);            
	            	            String date_fn=pp1.substring(6, 8);
	            	            String clinic_fn=pppp;
	            	            String page_fn=ss;
	            				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
	            				vstring.add(s3);	           				
	            				 
	            				data.add(vstring);
	            				num++;
		        			}
	        				 
		        		}
	        		}
	        		else if(s3.length()==21) {
	        			if(ppp.substring(0,2).equals("25")) {
	        				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		        			Date date = formatter.parse(ppp);
		        			
		        			Date m3= new DateTime().minusMonths(3).toDate();
	        				if(date.after(m3)) {
	        					final Vector<String> vstring = new Vector<String>();
	            				vstring.add(" "+num );
	            				String pp1=ppp;
	            				String year_fn=pp1.substring(0, 4);	             
	            	            String month_fn=pp1.substring(4, 6);            
	            	            String date_fn=pp1.substring(6, 8);
	            	            String clinic_fn=pppp;
	            	            String page_fn=ss;
	            				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
	            				vstring.add(s3);	
	                       				 
	            				data.add(vstring);
	            				num++;
	        				}     				 
		        		}
	        				        			
	        		}

		  		  }
				}
				 
    	    }
				
			fs.close();
        } catch (IOException | ParseException e) {
            //e.printStackTrace();
            WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn,"File Error",WebOptionPane.ERROR_MESSAGE);
        }

        return new DefaultTableModel(data, columnNames3);
    }
    public WebPanel getHDFSScope(String hn){ 
    	 
		WebPanel wp = new WebPanel();
		wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension((width*2)/10,75));
	
		columnNamesscope = new Vector<String>();		
		columnNamesscope.add("");
		columnNamesscope.add("");
		columnNamesscope.add("");
		
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		DefaultTableModel model = new DefaultTableModel(data, columnNamesscope){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};	 
		tablescope = new WebTable(model){
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);			
				if (!isRowSelected(row)){
					c.setBackground(getBackground());
				}
				return c;
			}
		};
		
		tablescope.setEditable (false );
		tablescope.setFillsViewportHeight(true);
        tablescope.setPreferredScrollableViewportSize ( new Dimension ((width*2)/10, 100 ) );
        tablescope.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tablescope.rowAtPoint(e.getPoint());
		        int col = tablescope.columnAtPoint(e.getPoint());
		        
		        String filename=tablescope.getValueAt(row, 2).toString().trim();
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
		        Label_Info.setText("  "+d+" "+Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
		        mid2.removeAll();
		        mid2.revalidate();
		        mid2.repaint();
				showFilePDF(filename.trim());
			}
		});
		        
        WebScrollPane scrollPane = new WebScrollPane ( tablescope );      
        wp.add(scrollPane, BorderLayout.CENTER);
        
        ButtonSearchscope = new WebButton("Show Files");
		ButtonSearchscope.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(InApp.usertype.equals("83")) {
					ButtonPrint.setEnabled(true);
				}else {
					ButtonPrint.setEnabled(false);
				}
				 
				labelImage.setIcon(null);
				if(Label_Info !=null) {
		    		Label_Info.setText("");		    		
		    	}
				mid2.removeAll();
				mid2.revalidate();
			    mid2.repaint();
				if(oUserInfo.GetPtHN().equals("")){
					WebOptionPane.showMessageDialog(null,"กรุณาเลือก HN ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
					 
				}else{
					getDataScope(oUserInfo.GetPtHN());
				}			 
			}
		});
		ButtonSearchscope.setFont(new Font("Tahoma", Font.PLAIN, 13));	 
		wp.add(ButtonSearchscope, BorderLayout.SOUTH);
		
		return wp;
	}
    public void getDataScope(String hn){
		tablescope.setModel(fetchDataScope(hn));
		TableColumnModel columnModel = tablescope.getColumnModel();		
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(220);
		columnModel.getColumn(2).setPreferredWidth(0);
		columnModel.getColumn(2).setMinWidth(0);
		columnModel.getColumn(2).setMaxWidth(0);
		((DefaultTableModel)tablescope.getModel()).fireTableDataChanged();
		
	}
	public  DefaultTableModel fetchDataScope(String hn){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;

		FileSystem fs;
		try {
			fs = connHDFS();
			Path newFolderPath= new Path(path1);
			FileStatus[] fileStatus = fs.listStatus(newFolderPath);
			//Array Sorting Descending Order
			Arrays.sort(fileStatus, Collections.reverseOrder());
			int num=1;
			for(FileStatus status : fileStatus){
				String pp=status.getPath().toString().trim();
				if(pp.length()==52 || pp.length() ==54) {
					
				}else {
				String s3=pp.substring((((Prop.getProperty("hadoop.server")+path1).length())+1),pp.indexOf("H"));
				String ppp=s3.substring(0,8);
				String pppp=s3.substring(8,12);
				String ss=s3.substring( 12,14);
				if(s3.length()==25) {
        			String ss1=s3.substring(14,16);
	        		String ss2=pp.substring(16,18);
	        			        		
	        		if(ppp.substring(0,2).equals("25") && (ss1.equals("00") || ss1.equals("01") || ss1.equals("02") || ss1.equals("03"))) {
	        			if((pppp.equals("1802"))) {
	        				final Vector<String> vstring = new Vector<String>();
            				vstring.add(" "+num );
            				String pp1=ppp;
            				String year_fn=pp1.substring(0, 4);	             
            	            String month_fn=pp1.substring(4, 6);            
            	            String date_fn=pp1.substring(6, 8);
            	            String clinic_fn=pppp;
            	            String page_fn=ss;
            				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
            				vstring.add(s3);	           				
            				 
            				data.add(vstring);
            				num++;
	        			}
        				 
	        		}

        		  }

				}
    	    }
				
			fs.close();
		} catch (IOException e) {
			//e.printStackTrace();
			WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn,"File Error",WebOptionPane.ERROR_MESSAGE);
		}
		return new DefaultTableModel(data, columnNamesscope);
	}
    public  void showFilePDF(String filename){
		fn=filename;
		if(filename.length()==21 || filename.length()==25){
			pane = new ZoomPane(previewPDFDocumentInImage(filename,oUserInfo.GetPtHN()));       
            scrollPaneImg = new WebScrollPane(pane);
            pane.centerInViewport();
    		mid2.add(scrollPaneImg, BorderLayout.CENTER);
    					 
		}else{
			WebOptionPane.showMessageDialog(null,"filename error:"+filename ,"Infomation",WebOptionPane.WARNING_MESSAGE); 			
		}
		mid2.revalidate();
		mid2.repaint();
	}
    public  void lockFilePDF(){
    	BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(getClass().getClassLoader().getResource("images/no_img.png"));
			pane = new ZoomPane(myPicture);       
	        scrollPaneImg = new WebScrollPane(pane);
	        pane.centerInViewport();
			mid2.add(scrollPaneImg, BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		mid2.revalidate();
		mid2.repaint();
		
	}
    public  Image previewPDFDocumentInImage(String filescanname,String hn){
		Image img=null;
		ByteBuffer buf = null;
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		
		FileSystem fs ;		 
		PDFFile pdffile;
		try {
			 fs = connHDFS();
			 Path newFolderPath= new Path(path1);
			 Path path = new Path(newFolderPath+"/"+filescanname.trim()+"HN.pdf");
			    if (!fs.exists(path)) {}
			 FSDataInputStream in = fs.open(path);
		     byte[] b= IOUtils.toByteArray(in);
		     buf = ByteBuffer.wrap(b);
			
			 pdffile = new PDFFile(buf);
			 PDFPage page = pdffile.getPage(1);
	    	 Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
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
    public void clear(){
    	table.setModel(fetchDataClear());
		TableColumnModel columnModel = table.getColumnModel();		
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(220);
		columnModel.getColumn(2).setPreferredWidth(0);
		columnModel.getColumn(2).setMinWidth(0);
		columnModel.getColumn(2).setMaxWidth(0);
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
    	table3.setModel(fetchData3Clear());
		TableColumnModel columnModel3 = table3.getColumnModel();		
		columnModel3.getColumn(0).setPreferredWidth(40);
		columnModel3.getColumn(1).setPreferredWidth(220);
		columnModel3.getColumn(2).setPreferredWidth(0);
		columnModel3.getColumn(2).setMinWidth(0);
		columnModel3.getColumn(2).setMaxWidth(0);
		((DefaultTableModel)table3.getModel()).fireTableDataChanged();
		tablescope.setModel(fetchDataScopeClear());
		TableColumnModel columnModelscope = tablescope.getColumnModel();		
		columnModelscope.getColumn(0).setPreferredWidth(40);
		columnModelscope.getColumn(1).setPreferredWidth(220);
		columnModelscope.getColumn(2).setPreferredWidth(0);
		columnModelscope.getColumn(2).setMinWidth(0);
		columnModelscope.getColumn(2).setMaxWidth(0);
		((DefaultTableModel)tablescope.getModel()).fireTableDataChanged();
		

		tree1.setModel(getDefaultTreeModel("")); 
		tree2.setModel(getDefaultTreeModel("")); 
		
		fn="";
		Label_Info.setText("");
		labelImage.setIcon(null);
		mid2.removeAll();
		mid2.revalidate();
	    mid2.repaint();

    }
    public  DefaultTableModel fetchDataClear(){		 
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		return new DefaultTableModel(data, columnNames);
	}
    public  DefaultTableModel fetchData3Clear(){	 
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		return new DefaultTableModel(data, columnNames3);
	}
    public  DefaultTableModel fetchDataScopeClear()	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		return new DefaultTableModel(data, columnNamesscope);
	}
    public WebPanel getHDFSPast(String hn){ 
    	 
    	 
		WebPanel wp = new WebPanel();
		wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension(200,75));
		 
		tree1.removeAll();		
		tree1.setModel(getDefaultTreeModel(hn)); 
	    tree1.setSelectionMode ( WebTree.DISCONTIGUOUS_TREE_SELECTION );
	    WebScrollPane treeScroll1 = new WebScrollPane ( tree1 );
	    treeScroll1.setPreferredSize ( new Dimension ( 200, height-160 ) );
	    tree1.addTreeSelectionListener(new TreeSelectionListener() {
	    	public void valueChanged(TreeSelectionEvent e) {
	    		String node = e.getNewLeadSelectionPath().getLastPathComponent().toString();
	    		String node1 = e.getNewLeadSelectionPath().getPathComponent(1).toString();
	    		//System.out.println(node+"..node"+node1+"--");
	    		if(node1.length()>7) {
	    			int num=Integer.parseInt(node1.substring(8))-1;
	    			showFile(num,node);    			
	    		}else {
	    			showFile(0,node);
	    		}
	    	}
	    });
		wp.add(treeScroll1, BorderLayout.CENTER);
		
		ButtonSearchpast = new WebButton("Show Files");
		ButtonSearchpast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ButtonPrint.setEnabled(false);
				if(Label_Info !=null) {
		    		Label_Info.setText("");		    		
		    	}
				mid2.removeAll();
				mid2.revalidate();
			    mid2.repaint();
				if(oUserInfo.GetPtHN().equals("")){
					WebOptionPane.showMessageDialog(null,"กรุณาเลือก HN ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
					 
				}else{
					tree1.setModel(getDefaultTreeModel(oUserInfo.GetPtHN())); 
				}			 
			}
		});
		ButtonSearchpast.setFont(new Font("Tahoma", Font.PLAIN, 13));
		 
		wp.add(ButtonSearchpast, BorderLayout.SOUTH);
		return wp;
	}
	public TreeModel getDefaultTreeModel (String hn){
    	UniqueNode root=null;
    	if(hn.equals("")) {
    		root= new UniqueNode ( " "+hn );
    		 
		}else {
			 
			String folderhn=hn.substring(0, 2).trim();
			String path1="/utth/"+folderhn+"/"+hn;
			 
			FileSystem fs;
			root= new UniqueNode ( " "+hn );
			try {
				UniqueNode parent;
				int hnNum=0,pdfNum=0;
				fs = connHDFS();
				Path newFolderPath= new Path(path1);
				FileStatus[] fileStatus = fs.listStatus(newFolderPath);
				for(FileStatus status : fileStatus){
					String pp=status.getPath().toString().trim();
					if(pp.length()==52 || pp.length() ==54) {
						hnNum++;
					} 
					 
				}
				pdffile = new PDFFile[hnNum];
				for(FileStatus status : fileStatus){
					String pp=status.getPath().toString().trim();
					if(pp.length()==52 || pp.length() ==54) {
						String f_pp=pp.substring((((Prop.getProperty("hadoop.server")+path1).length())+1), pp.indexOf(".pdf"));
						//System.out.println(hn+">>"+path1+">>"+folderhn+">>"+f_pp+"-"+pp.length());
						parent = new UniqueNode (f_pp);
						pdffile[pdfNum]=getPDF(f_pp,pdfNum);
						int p=pdffile[pdfNum].getNumPages();
						for (int i=0;i<p;i++) {
							parent.add ( new UniqueNode (i+1));
						}
						
						root.add ( parent );
						pdfNum++;
						  
					} 				 
				}
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    	return new WebTreeModel<UniqueNode> ( root );
    }
    public PDFFile getPDF(String hn,int pdfNum) {
    	
    	ByteBuffer buf = null;
		String folderhn=hn.substring(0, 2).trim();
		String path1="";
		if(hn.length()>7) {
			path1="/utth/"+folderhn+"/"+hn.substring(0, 7);
		}else {
			path1="/utth/"+folderhn+"/"+hn;
		}
		FileSystem fs=null;
		try {
			fs = connHDFS();
			Path newFolderPath= new Path(path1);
			Path path = new Path(newFolderPath+"/"+hn.trim()+".pdf");
			FSDataInputStream in = fs.open(path);
		    byte[] b= IOUtils.toByteArray(in);
		    buf = ByteBuffer.wrap(b);			
			pdffile[pdfNum] = new PDFFile(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		if(fs !=null) {
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return pdffile[pdfNum];
    }
    public void showFile(int  pdfNum,String p ) {
    	
		mid2.removeAll();
		Image img=null;
		
		PDFPage page = pdffile[pdfNum].getPage(Integer.parseInt(p.trim()));
		//get the width and height for the doc at the default zoom
    	Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
        img = page.getImage(800, 1200, rect, null, true, true);
        pane = new ZoomPane(img);       
        scrollPaneImg = new WebScrollPane(pane);
        pane.centerInViewport();
		mid2.add(scrollPaneImg, BorderLayout.CENTER);
		
		mid2.revalidate();
		mid2.repaint();
	}
    public void lockFile() {
    	
		mid2.removeAll();
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("images/no_img.png"));
			pane = new ZoomPane(myPicture);       
	        scrollPaneImg = new WebScrollPane(pane);
	        pane.centerInViewport();
			mid2.add(scrollPaneImg, BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		mid2.revalidate();
		mid2.repaint();
	}
    public void printOPD(String fn){
    	if(fn.trim().length()==21 || fn.trim().length()==25) {
    		ByteBuffer buf = null;
    		String hn=fn.substring(fn.length()-7).trim();
    		String folderhn=hn.substring(0, 2).trim();	
    		String path1="/utth/"+folderhn+"/"+hn;

    		FileSystem fs=null;
    		PDFFile pdffile;
    		try {
    			fs = connHDFS();
    			Path newFolderPath= new Path(path1);
    			Path path = new Path(newFolderPath+"/"+fn+"HN.pdf");
    			FSDataInputStream in = fs.open(path);
    			byte[] b= IOUtils.toByteArray(in);
    			buf = ByteBuffer.wrap(b);
    			pdffile = new PDFFile(buf);
    			PDFPage page = pdffile.getPage(1);
    			System.out.println(page.getWidth()+"--"+page.getHeight());
    			PrinterJob pjob = PrinterJob.getPrinterJob(); 
    	    	Book book = new Book();
    	    	Paper paper;
    	    	PageFormat pf;
    	    	pf = new PageFormat();
    			paper = new Paper();
    			paper.setSize(12.0*72,25.0*72);
    			if(page.getHeight()<1500.00) {
    				paper.setImageableArea(xprint,10 ,paper.getHeight()-10,450 -10);	
    			}else {
    				paper.setImageableArea(xprint,10 ,paper.getHeight()-10,paper.getWidth() -10);	
    			}
    			pf.setPaper(paper);
    	    	 
    			PDFPrintPageHP pages = new PDFPrintPageHP(pdffile);
    	        book.append(pages, pf, pdffile.getNumPages());
    	    
    	        pjob.setPageable(book);
    	        
    	       new PrintThread(pages, pjob).start();
    	       fs.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		Setup.getlog(oUserInfo.GetPtHN(), InApp.ip, fn, "p");
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
    public WebPanel getHDFSAn(String hn){ 
    	 
    	WebPanel wp = new WebPanel();
		wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension(200,75));
		
		tree2.removeAll();		
		tree2.setModel(getDefaultTreeModelAn(hn)); 
	    tree2.setSelectionMode ( WebTree.DISCONTIGUOUS_TREE_SELECTION );
	    WebScrollPane treeScroll1 = new WebScrollPane ( tree2 );
	    treeScroll1.setPreferredSize ( new Dimension ( 200, height-160 ) );
	    tree2.addTreeSelectionListener(new TreeSelectionListener() {
	    	public void valueChanged(TreeSelectionEvent e) {
	    		String node = e.getNewLeadSelectionPath().getLastPathComponent().toString();
	    		String node1 = e.getNewLeadSelectionPath().getPathComponent(1).toString();
	    		//System.out.println(".."+node+"..node.."+node1+"--");
	    		showFile1(0,node);

	    	}
	    });
		wp.add(treeScroll1, BorderLayout.CENTER);
		
		ButtonSearchAn = new WebButton("Show Files");
		ButtonSearchAn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ButtonPrint.setEnabled(false);
				if(Label_Info !=null) {
		    		Label_Info.setText("");		    		
		    	}
				mid2.removeAll();
				mid2.revalidate();
			    mid2.repaint();
				if(oUserInfo.GetPtHN().equals("")){
					WebOptionPane.showMessageDialog(null,"กรุณาเลือก HN ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
					 
				}else{
					tree2.setModel(getDefaultTreeModelAn(oUserInfo.GetPtHN())); 
				}			 
			}
		});
		ButtonSearchAn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		 
		wp.add(ButtonSearchAn, BorderLayout.SOUTH);
		
		return wp;
    }
    public TreeModel getDefaultTreeModelAn (String hn){
    	UniqueNode root=null;
    	if(hn.equals("")) {
    		root= new UniqueNode ( " "+hn );
    		 
		}else {
			 
			String folderhn=hn.substring(0, 2).trim();
			String path1="/utth/"+folderhn+"/"+hn;
			 
			FileSystem fs;
			root= new UniqueNode ( " "+hn );
			try {
				UniqueNode parent;
				int hnNum=0,pdfNum=0;
				fs = connHDFS();
				Path newFolderPath= new Path(path1);
				FileStatus[] fileStatus = fs.listStatus(newFolderPath);
				for(FileStatus status : fileStatus){
					String pp=status.getPath().toString().trim();
					//System.out.println(hn+">>"+pp+"---"+pp.length());
					if(pp.length()==52 || pp.length() ==54) {
						if(pp.substring(pp.length()-6).equals("AN.PDF") || pp.substring(pp.length()-6).equals("AN.pdf")) {
							hnNum++;
							System.out.println(hn+">>"+path1+">>"+folderhn+">>"+pp+"-"+pp.length());
						}
						 
					} 
					 
				}
				
				pdffile1 = new PDFFile[hnNum];
				for(FileStatus status : fileStatus){
					String pp=status.getPath().toString().trim();
					if(pp.length()==52 || pp.length() ==54) {
						if(pp.substring(pp.length()-6).equals("AN.PDF") || pp.substring(pp.length()-6).equals("AN.pdf")) {
							//System.out.println(hn+">>"+path1+">>"+folderhn+">>"+pp+"-"+pp.length());
							String f_pp=pp.substring((((Prop.getProperty("hadoop.server")+path1).length())+1), pp.indexOf(".pdf"));
							//System.out.println(hn+"---->>"+path1+">>"+folderhn+">>"+f_pp+"-"+pp.length());
							parent = new UniqueNode (f_pp.substring(0,7));
							pdffile1[pdfNum]=getPDF1(f_pp.substring(0,7),hn,pdfNum);
							int p=pdffile1[pdfNum].getNumPages();
							for (int i=0;i<p;i++) {
								parent.add ( new UniqueNode (i+1));
							}
							root.add ( parent );
							
							pdfNum++;
						}
		
					} 				 
				}
				
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    	return new WebTreeModel<UniqueNode> ( root );
    }
    public PDFFile getPDF1(String an_,String hn,int pdfNum) {
    	
    	ByteBuffer buf = null;
		String folderhn=hn.substring(0, 2).trim();
		String path1="";
		path1="/utth/"+folderhn+"/"+hn;
		
		FileSystem fs=null;
		try {
			fs = connHDFS();
			Path newFolderPath= new Path(path1);
			Path path = new Path(newFolderPath+"/"+an_.trim()+"AN.pdf");
			FSDataInputStream in = fs.open(path);
		    byte[] b= IOUtils.toByteArray(in);
		    buf = ByteBuffer.wrap(b);			
			pdffile1[pdfNum] = new PDFFile(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		if(fs !=null) {
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return pdffile1[pdfNum];
    }
    public void showFile1(int  pdfNum,String p ) {
    	
		mid2.removeAll();
		Image img=null;
		
		PDFPage page = pdffile1[pdfNum].getPage(Integer.parseInt(p.trim()));
		//get the width and height for the doc at the default zoom
    	Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
        img = page.getImage(800, 1200, rect, null, true, true);
        pane = new ZoomPane(img);       
        scrollPaneImg = new WebScrollPane(pane);
        pane.centerInViewport();
		mid2.add(scrollPaneImg, BorderLayout.CENTER);
		
		mid2.revalidate();
		mid2.repaint();
	}
   
}
