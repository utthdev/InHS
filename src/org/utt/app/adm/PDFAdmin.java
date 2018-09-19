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
package org.utt.app.adm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.utt.app.util.Prop;
import org.utt.app.util.Setup;
import org.utt.app.ui.ZoomPane;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PDFAdmin extends WebPanel{
	 int width,height;
	 WebPanel leftPanel,topLeftPanel,rightPanel,topRightPanel,midRightPanel,mid1,mid2,mid3;
	 WebButton ButtonSave,ButtonDel,ButtonEdit,ButtonClear;
	 WebLabel Label_info;
	 WebAccordion accordion;
	 WebTextField TextField_ymd,TextField_clinic,TextField_page,TextField_hni,TextField_ss,TextField_ss1;
	 Vector<String> columnNames,columnNamesext;
	 WebScrollPane scrollPaneImg;
	 WebTable table,tableext;
	 
	 ZoomPane pane;
	
	 String hn="", filename="";
	 
	public PDFAdmin(int w,int h) {
		width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(width-5, height));
		
		setLeft();
		setRight();
	}
	public void setLeft(){
		leftPanel = new WebPanel();
		leftPanel.setPreferredSize(new Dimension((width*2)/10, height));
		leftPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		topLeftPanel = new WebPanel();
		topLeftPanel.setLayout(null);
		topLeftPanel.setPreferredSize(new Dimension((width*2)/10, 30));
		leftPanel.add(topLeftPanel, BorderLayout.NORTH);
		
		WebLabel LabelHN = Setup.getLabel("HN", 13,4,SwingConstants.LEFT);
		LabelHN.setBounds(5, 5, 50, 20);
		topLeftPanel.add(LabelHN);
		WebTextField TextField_HN =  new WebTextField(10);
		TextField_HN.setBounds(30, 5, 130, 20);
		topLeftPanel.add(TextField_HN);
		WebButton ButtonSearchHN = new WebButton("Search");
		ButtonSearchHN.setBounds(170, 5, 80, 20);
		ButtonSearchHN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Check_l_HN(TextField_HN.getText().trim())) {
					//accordion.removeAll();
					hn=TextField_HN.getText().trim();
					getData(hn); 
					
			         
				}else {
					WebOptionPane.showMessageDialog ( null, "Check HN Value", "Message", WebOptionPane.INFORMATION_MESSAGE );
				}
			}
		});
		topLeftPanel.add(ButtonSearchHN);
		
		accordion = new WebAccordion ( );
		accordion.addPane ( null, "Active", hnSearch () );
        //accordion.addPane ( null, "All", hnSearchExt () );
		accordion.setMultiplySelectionAllowed ( false );
         
        leftPanel.add( accordion, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.WEST);
		
	}
	public void setRight(){
		rightPanel = new WebPanel();
		rightPanel.setPreferredSize(new Dimension(width-(width*2)/10, height));
		rightPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		topRightPanel = new WebPanel();
		topRightPanel.setLayout(null);
		topRightPanel.setPreferredSize(new Dimension(width-(width*2)/10, 60));
		rightPanel.add(topRightPanel, BorderLayout.NORTH);
		
		midRightPanel = new WebPanel();
		midRightPanel.setLayout(new BorderLayout(0, 0));
		midRightPanel.setPreferredSize(new Dimension(width-(width*2)/10, height-235));
		rightPanel.add(midRightPanel, BorderLayout.CENTER);
		
		mid1 = new WebPanel();
		mid1.setLayout(null);
		mid1.setPreferredSize(new Dimension(width-(width*2)/10, 30));
		midRightPanel.add(mid1, BorderLayout.NORTH);
		
		Label_info =Setup.getLabel("", 13,4,SwingConstants.LEFT);
		Label_info.setBounds(20, 5, 500, 25);
		//mid1.add(Label_info);
		
		mid2 = new WebPanel();
		mid2.setLayout(new BorderLayout(0, 0));
		mid2.setPreferredSize(new Dimension(width-((width*2)/10+200), height-265));
		midRightPanel.add(mid2, BorderLayout.CENTER);
		
		mid3 = new WebPanel();
		mid3.setLayout(null);
		mid3.setPreferredSize(new Dimension(200, height-265));
		midRightPanel.add(mid3, BorderLayout.EAST);
		
		
		WebLabel Label_ymd =Setup.getLabel("Year-Month-Day", 13,4,SwingConstants.LEFT);
		Label_ymd.setBounds(10, 5, 100, 25);
		mid3.add(Label_ymd);
		
		TextField_ymd = new WebTextField();
		TextField_ymd.setEditable(false);
		TextField_ymd.setBounds(10, 30, 100, 25);
		mid3.add(TextField_ymd);
		
		WebLabel Label_clinic = Setup.getLabel("Clinic", 13,4,SwingConstants.LEFT);
		Label_clinic.setBounds(10, 55, 50, 25);
		mid3.add(Label_clinic);
		
		TextField_clinic = new WebTextField();
		TextField_clinic.setEditable(false);
		TextField_clinic.setBounds(60, 55, 50, 25);
		mid3.add(TextField_clinic);
		
		WebLabel Label_page =  Setup.getLabel("Page", 13,4,SwingConstants.LEFT);
		Label_page.setBounds(10, 80, 50, 25);
		mid3.add(Label_page);
		
		TextField_page = new WebTextField();
		TextField_page.setEditable(false);
		TextField_page.setBounds(60, 80, 50, 25);
		mid3.add(TextField_page);
		
		WebLabel Label_ss = Setup.getLabel("Status", 13,4,SwingConstants.LEFT);
		Label_ss.setBounds(10, 105, 50, 25);
		mid3.add(Label_ss);
		
		TextField_ss = new WebTextField();
		TextField_ss.setEditable(false);
		TextField_ss.setBounds(60, 105, 50, 25);
		mid3.add(TextField_ss);
		
		WebLabel Label_ss1 =Setup.getLabel("Visit", 13,4,SwingConstants.LEFT);
		Label_ss1.setBounds(10, 135, 50, 25);
		mid3.add(Label_ss1);
		
		TextField_ss1 = new WebTextField();
		TextField_ss1.setEditable(false);
		TextField_ss1.setBounds(60, 135, 50, 25);
		mid3.add(TextField_ss1);
		
		WebLabel Label_hni =Setup.getLabel("HN", 13,4,SwingConstants.LEFT);
		Label_hni.setBounds(10, 160, 80, 25);
		mid3.add(Label_hni);
		
		TextField_hni = new WebTextField();
		TextField_hni.setEditable(false);
		TextField_hni.setBounds(10, 185, 100, 25);
		mid3.add(TextField_hni);
	
		ButtonSave = new WebButton("Save");
		ButtonSave.setEnabled(false);
		ButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 
				ButtonDel.setEnabled(false);
				ButtonEdit.setEnabled(false);
				
				save(filename);
				
				clearData();
				ButtonSave.setEnabled(false);
				TextField_ymd.setEditable(false);
		 		TextField_clinic.setEditable(false);
		 		TextField_page.setEditable(false);
		 		TextField_ss.setEditable(false);
		 		TextField_ss1.setEditable(false);
		 		TextField_hni.setEditable(false);
			}
		});
		ButtonSave.setBounds(10,220, 100, 70);
		mid3.add(ButtonSave);
		
		ButtonEdit = new WebButton("Edit");
		ButtonEdit.setEnabled(false);
		ButtonEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonSave.setEnabled(true);
				ButtonDel.setEnabled(false);
				TextField_ymd.setEditable(true);
		 		TextField_clinic.setEditable(true);
		 		TextField_page.setEditable(true);
		 		TextField_ss.setEditable(true);
		 		TextField_ss1.setEditable(true);
		 		TextField_hni.setEditable(true);
				 
			}
		});
		ButtonEdit.setBounds(10,300, 100, 30);
		mid3.add(ButtonEdit);
		
		ButtonDel = new WebButton("Delete");
		ButtonDel.setEnabled(false);
		ButtonDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonEdit.setEnabled(false);
				ButtonSave.setEnabled(false);
				del(filename);
				clearData();
				ButtonDel.setEnabled(false);
				TextField_ymd.setEditable(false);
		 		TextField_clinic.setEditable(false);
		 		TextField_page.setEditable(false);
		 		TextField_ss.setEditable(false);
		 		TextField_ss1.setEditable(false);
		 		TextField_hni.setEditable(false);
				
			}
		});
		ButtonDel.setBounds(10,380, 100, 30);
		mid3.add(ButtonDel);
		
		ButtonClear = new WebButton("Clear");
		ButtonClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonEdit.setEnabled(false);
				ButtonSave.setEnabled(false);
				ButtonDel.setEnabled(false);
				clearData();
				TextField_ymd.setEditable(false);
		 		TextField_clinic.setEditable(false);
		 		TextField_page.setEditable(false);
		 		TextField_ss.setEditable(false);
		 		TextField_ss1.setEditable(false);
		 		TextField_hni.setEditable(false);
			}
		});
		ButtonClear.setBounds(10,330, 100, 30);
		mid3.add(ButtonClear);
		
		add(rightPanel, BorderLayout.CENTER);
	}
	public boolean Check_l_HN(String hn) {
		if(hn.length()==7) {
			return true;
		}else {
			return false;
		}		
	}
	public WebPanel hnSearch() {
		WebPanel wp = new WebPanel();
		wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension((width*2)/10,75));

		columnNames = new Vector<String>();		
		columnNames.add("ที่");
  		columnNames.add(" ");
		columnNames.add("  FN ");
		

		Vector<Vector<String>> data = new Vector<Vector<String>>();
		DefaultTableModel model = new DefaultTableModel(data, columnNames){
			public Class getColumnClass(int column){
				return getValueAt(0, column).getClass();
			}
		};
		 
        
        //WebTable table = new WebTable ( data, headers );
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
				clearData();
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        filename=table.getValueAt(row, 1).toString().trim();
		        String y="",m="",d="",c="",hni="",p="",ss="",ss1="";
		        if(filename.length()==21 || filename.length()==25){
					 
		    		 
		    		if(filename.length()==21) {
		    			y=filename.substring(0,4);
		    			m=filename.substring(4,6);
		    			d=filename.substring(6,8);
		    			c=filename.substring(8,12);
		    			p=filename.substring(12,14);
		    			hni=filename.substring(14);
		    		}
		    		else if(filename.length()==25) {
		    			y=filename.substring(0,4);
		    			m=filename.substring(4,6);
		    			d=filename.substring(6,8);
		    			c=filename.substring(8,12);
		    			p=filename.substring(12,14);
		    			ss=filename.substring(14,16);
		    			ss1=filename.substring(16,18);
		    			hni=filename.substring(18);
		    		
		    		}
		    		pane = new ZoomPane(previewPDFDocumentInImage(hni,filename));       
		            scrollPaneImg = new WebScrollPane(pane);
		            pane.centerInViewport();
		    		mid2.add(scrollPaneImg, BorderLayout.CENTER);
		    		
		    		//Label_info.setText(y+"-"+m+"-"+d+"::"+c+"-page-"+p+"-ss-"+ss+"-ss1-"+ss1+"-hn-"+hni);
		    		TextField_ymd.setText(y+m+d);
		    		TextField_clinic.setText(c);
		    		TextField_page.setText(p);
		    		TextField_ss.setText(ss);
		    		TextField_ss1.setText(ss1);
		    		TextField_hni.setText(hni);
		    		
		    		ButtonEdit.setEnabled(true);
		    		ButtonDel.setEnabled(true);
		    					 
				}else{
					TextField_ymd.setText(y+m+d);
		    		TextField_clinic.setText(c);
		    		TextField_page.setText(p);
		    		TextField_ss.setText(ss);
		    		TextField_ss1.setText(ss1);
		    		TextField_hni.setText(hni);
					//JOptionPane.showMessageDialog(null,"filename error:"+filename ,"Infomation",JOptionPane.WARNING_MESSAGE); 			
				}
				
			}
		});
		 
       
        WebScrollPane scrollPane = new WebScrollPane ( table );
        
        //getData(hn);
        wp.add(scrollPane, BorderLayout.CENTER);
		return wp;
	}
	public void getData(String hn){
		table.setModel(fetchData(hn));
		TableColumnModel columnModel = table.getColumnModel();		
		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(1).setPreferredWidth(0);
		columnModel.getColumn(1).setMinWidth(0);
		columnModel.getColumn(1).setMaxWidth(0);
		columnModel.getColumn(2).setPreferredWidth((width*2)/10-80);
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
		
	}
	public  DefaultTableModel fetchData(String hn){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		 
		//String filename="255511290001014700005HN.pdf";
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
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
					if(ppp.equals("25000101")) {	
		        		 
		        	}
		        	else{
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
	                	            vstring.add(s3);
	                				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
	                				//vstring.add(s3);	           				
	                				 
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
		            	            vstring.add(s3);	
		            				vstring.add(" "+date_fn+" "+Setup.getMonthShortThaiName(month_fn)+" "+year_fn+"   |"+clinic_fn);
		            				
		                       				 
		            				data.add(vstring);
		            				num++;
		        				}
	            				 
	    	        		}
		        			
		        			
		        		}else {
		        			
		        		}
		        	}
				}
				}
				 
				 
    	    }
				
			fs.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			WebOptionPane.showMessageDialog(null,"ไม่พบ file ของ HN:"+hn,"File Error",WebOptionPane.ERROR_MESSAGE);
		}
	
		return new DefaultTableModel(data, columnNames);
	}
	
	public  Image previewPDFDocumentInImage(String hn,String filescanname){
		 
		Image img=null;
		ByteBuffer buf = null;

		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		 
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		FileSystem fs;
		PDFFile pdffile;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path newFolderPath= new Path(path1);
			 Path path = new Path(newFolderPath+"/"+filescanname.trim()+"HN.pdf");
			    if (!fs.exists(path)) {
			      System.out.println("File " + filescanname+"HN.pdf" + " does not exists");
			 
			}
			FSDataInputStream in = fs.open(path);
		    byte[] b= IOUtils.toByteArray(in);
		    buf = ByteBuffer.wrap(b);

			
			pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(1);
			//get the width and height for the doc at the default zoom
	    	Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
	        img = page.getImage(800, 1200, //width &amp; height
	                rect, // clip rect
	                null, // null for the ImageObserver
	                true, // fill background with white
	                true) // block until drawing is done
	        ;
			in.close();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
			img=null;
		}
		return img;
    	 
	}
	public void clearData() {
		 filename="";

		 TextField_ymd.setText("");
 		 TextField_clinic.setText("");
 		 TextField_page.setText("");
 		 TextField_ss.setText("");
 		 TextField_ss1.setText("");
 		 TextField_hni.setText("");
		 
		 mid2.removeAll();
		 mid2.revalidate();
		 mid2.repaint();
	}
	public void del(String fn) {
		String hn="",start="",stop="";
		if(fn.length()==21) {
			hn=fn.substring(14);
			start=fn.substring(0,14);
			stop="00"+hn;
		}else if(fn.length()==25) {
			hn=fn.substring(18);
			start=fn.substring(0,14);
			stop=fn.substring(16);
		} 
		String fn_new=start+"05"+stop;
		 
		String folderhn=hn.substring(0, 2).trim();
		String path1="/utth/"+folderhn+"/"+hn;
		Path newFolderPath= new Path(path1);
		Path src=new Path(newFolderPath+"/"+fn+"HN.pdf");
		Path dsc=new Path(newFolderPath+"/"+fn_new+"HN.pdf");
		int reply = WebOptionPane.showConfirmDialog(null, "Delete File Name : "+fn, "Delete File", WebOptionPane.YES_NO_OPTION);
        if (reply == WebOptionPane.YES_OPTION) {
        	rename(src, dsc,path1);
        	getData(hn); 
        	
        }

	}
	public void save(String fn) {
		String hn="",ymd_edit="",c_edit="",p_edit="",ss_edit="",ss1_edit="";
		 
 		
 		if((TextField_ymd.getText().trim().length()==8) && (TextField_clinic.getText().trim().length()==4) && (TextField_page.getText().trim().length()==2) && (TextField_ss.getText().trim().length()==2) && (TextField_ss1.getText().trim().length()==2) && (TextField_hni.getText().trim().length()==7)) {
 			ymd_edit=TextField_ymd.getText();
 	 		c_edit=TextField_clinic.getText();
 	 		p_edit=TextField_page.getText();
 	 		ss_edit=TextField_ss.getText();
 	 		ss1_edit=TextField_ss1.getText();
 	 		hn=TextField_hni.getText();
 	 		String fn_new=ymd_edit+c_edit+p_edit+ss_edit+ss1_edit+hn;
 	 		String folderhn=hn.substring(0, 2).trim();
 			String path1="/utth/"+folderhn+"/"+hn;
 			Path newFolderPath= new Path(path1);
 			Path src=new Path(newFolderPath+"/"+fn+"HN.pdf");
 			Path dsc=new Path(newFolderPath+"/"+fn_new+"HN.pdf");
 			int reply = WebOptionPane.showConfirmDialog(null, "Save File Name : "+fn_new, "Save File", WebOptionPane.YES_NO_OPTION);
 	        if (reply == WebOptionPane.YES_OPTION) {
 	        	rename(src, dsc,path1);
 	        	getData(hn); 
 	        	
 	        }
 	 		
 		}else {
 			WebOptionPane.showMessageDialog(null,"Please check File Name"+hn,"File Name Error",WebOptionPane.ERROR_MESSAGE);
 		}
 		
	 
	}
	public void rename(Path src,Path dsc,String folder_dsc) {
		Configuration conf = new Configuration();
  		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
  		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
  		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
  		FileSystem fs;
  		try {
  			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
  			Path newFolderPath= new Path(folder_dsc);
  			if(!fs.exists(newFolderPath)) {
  				fs.mkdirs(newFolderPath);
  			}

  			fs.rename(src, dsc);
  			fs.close();
  			//System.out.println("rename:"+src+" to "+dsc);
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
	}

}
