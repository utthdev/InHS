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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.utt.app.dao.DBmanager;
import org.utt.app.util.Prop;
import org.utt.app.util.Setup;
import org.utt.app.ui.ZoomPane;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.tree.UniqueNode;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeModel;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class IPDChartPanel extends WebPanel{
	 int width,height;
	 WebPanel leftPanel,topLeftPanel,rightPanel,mid2,mid1,MainSection,MiddleSection;
	 WebLabel Label_File,Label_info;
	 WebButton ButtonSearch,ButtonSearchpast,ButtonSend;
	 JFileChooser chooser;
	 String choosertitle;
	 String folder_in="",an="";
	 String hn_="";
	 WebAccordion accordion;
	 @SuppressWarnings("rawtypes")
	 WebTree tree1 = new WebTree ( );
	 PDFFile  pdffile;
	 WebScrollPane scrollPaneImg;
	 ZoomPane pane;
	 
	 
	public IPDChartPanel(int w,int h) {
		
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
		topLeftPanel.setPreferredSize(new Dimension((width*2)/10, 80));
		leftPanel.add(topLeftPanel, BorderLayout.NORTH);
		
		Label_File = new WebLabel("Choose File ");
		Label_File.setFont(new Font("Tahoma", Font.PLAIN, 13));
		topLeftPanel.add(Label_File);
		Label_File.setBounds(5, 5, 80, 20); 
		ButtonSearch = new WebButton("Select");
		ButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooser = new JFileChooser(); 
			    chooser.setCurrentDirectory(new java.io.File("D:\\"));
			    chooser.setDialogTitle(choosertitle);
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    //
			    // disable the "All files" option.
			    //
			    int selectedButton = chooser.showDialog ( null, "Open" );
			    chooser.setAcceptAllFileFilterUsed(false);
			    //    
			    if (selectedButton == JFileChooser.APPROVE_OPTION) { 
			     // System.out.println("getCurrentDirectory(): " 
			     //    +  chooser.getCurrentDirectory());
			     // System.out.println("getSelectedFile() : " 
			     //    +  chooser.getSelectedFile());
			      folder_in=chooser.getSelectedFile().toString().trim()+"\\";
			      an= folder_in.substring(folder_in.indexOf(".pdf")-7,folder_in.indexOf(".pdf"));
			      System.out.println("Folder Selection "+ folder_in+"--"+an);
			      //
			      //sendONE("4601239","4601239.pdf");
			      
			      }
			    else {
			      System.out.println("No Selection ");
			      }
					
					 
			}
		});
		ButtonSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonSearch.setBounds(100, 5, 100, 20);
		topLeftPanel.add(ButtonSearch);
		
		accordion = new WebAccordion ( );
		accordion.addPane ( null, "AN", anSearch (an.trim()) );
        //accordion.addPane ( null, "All", hnSearchExt () );
		accordion.setMultiplySelectionAllowed ( false );
         
        leftPanel.add( accordion, BorderLayout.CENTER);
		
		add(leftPanel, BorderLayout.WEST);
		
		MainSection = new WebPanel();
        MainSection.setPreferredSize(new Dimension((width-(width*2)/10)-200, height));
        MainSection.setLayout(new BorderLayout(0, 0));

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
        
        Label_info =Setup.getLabel("HN: ", 13,4,SwingConstants.LEFT);
		Label_info.setBounds(20, 5, 150, 25);
		mid1.add(Label_info);
        
        ButtonSend = new WebButton("Send Files");
		ButtonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(an.equals("")){
					WebOptionPane.showMessageDialog(null,"กรุณาเลือก AN ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
					 
				}else{
					if(an.length()==7) {
						String an_y=an.substring(5);
						String an_s=an.substring(0, 5);
						System.out.println("an.>>>."+an_y+"--"+an_s);				  
				        if(hn_.equals("")) {
				        	
				        }else {
				        	sendFileHDFS(hn_,an);
				        }
						 
						
					}
					 
				}			 
			}
		});
		ButtonSend.setFont(new Font("Tahoma", Font.PLAIN, 13));
		ButtonSend.setEnabled(false);
		ButtonSend.setBounds(250, 5, 100, 25);
		mid1.add(ButtonSend);


        add(MainSection, BorderLayout.CENTER);

	}
	public void setRight(){
		
	}
	public WebPanel anSearch(String an_) {
		WebPanel wp = new WebPanel();
		wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension((width*2)/10,75));
		System.out.println("an.."+an_);
		if(an_.length()==7) {
			String an_y=an_.substring(6);
			String an_s=an_.substring(0, 5);
		}	
		tree1.removeAll();		
		tree1.setModel(getDefaultTreeModel(an_)); 
	    tree1.setSelectionMode ( WebTree.DISCONTIGUOUS_TREE_SELECTION );
	    WebScrollPane treeScroll1 = new WebScrollPane ( tree1 );
	    treeScroll1.setPreferredSize ( new Dimension ( 200, height-160 ) );
	    tree1.addTreeSelectionListener(new TreeSelectionListener() {
	    	public void valueChanged(TreeSelectionEvent e) {
	    		String node = e.getNewLeadSelectionPath().getLastPathComponent().toString();
	    		String node1 = e.getNewLeadSelectionPath().getPathComponent(1).toString();
	    		System.out.println(node+"..node"+node1+"--");
	    		int num=Integer.parseInt(node);
    			showFile(num); 
	    		
	    	}
	    });
		wp.add(treeScroll1, BorderLayout.CENTER);	 		 
		
		ButtonSearchpast = new WebButton("Show Files");
		ButtonSearchpast.addActionListener(new ActionListener() {
			 
			public void actionPerformed(ActionEvent arg0) {
				tree1.setModel(getDefaultTreeModel("")); 
				if(an.equals("")){
					WebOptionPane.showMessageDialog(null,"กรุณาเลือก HN ","ข้อผิดพลาด",WebOptionPane.WARNING_MESSAGE); 
					 
				}else{
					if(an.length()==7) {
						String an_y=an.substring(5);
						String an_s=an.substring(0, 5);
						System.out.println("an.>>>."+an_y+"--"+an_s);
						System.out.println("an.------------------>>>"+an);
						tree1.setModel(getDefaultTreeModel(an)); 
						ButtonSend.setEnabled(true);
						Connection conn,conn6 ;
						PreparedStatement stmt,stmt6 ;
						String sql_="select  hn from admmaster where an='"+an.trim()+"'";
						conn = new DBmanager().getConnMSSql();
				        try {
							stmt = conn.prepareStatement(sql_);
							 ResultSet rs = stmt.executeQuery();
							 while (rs.next()) {
								 if(rs.getString(1) !=null) {
									 hn_=rs.getString(1).trim();
									 System.out.println("........"+hn_);
								 }
							 }
							 stmt.close();
							 conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				        Label_info.setText("HN: "+hn_);
					}
					 
				}			 
			}
		});
		ButtonSearchpast.setFont(new Font("Tahoma", Font.PLAIN, 13));
		 
		wp.add(ButtonSearchpast, BorderLayout.SOUTH);
		return wp;		 
	}
	@SuppressWarnings("null")
	public TreeModel getDefaultTreeModel (String an_in){
		ByteBuffer buf = null;
    	UniqueNode root=null;
    	if(an_in.equals("")) {
    		root= new UniqueNode ( " "+an_in );  		 
		}else {	  
			root= new UniqueNode ( " "+an_in );
			
			InputStream in;
			try {
				UniqueNode parent;
				in = new FileInputStream(folder_in);
				byte[] b= IOUtils.toByteArray(in);
				buf = ByteBuffer.wrap(b);
				pdffile = new PDFFile(buf);
				int tpage=pdffile.getNumPages();
				parent = new UniqueNode (an_in);
				for (int i=0;i<tpage;i++) {
					parent.add ( new UniqueNode (i+1));
				}
				
				root.add ( parent );
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
		}
    	
    	return new WebTreeModel<UniqueNode> ( root );
    }
	public void showFile(int  pdfNum) {
    	
		mid2.removeAll();
		Image img=null;
		
		PDFPage page = pdffile.getPage(pdfNum);
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
	public void sendFileHDFS(String hn,String an){
		String path="/utth";

		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Prop.getProperty("hadoop.server"));
		System.setProperty("HADOOP_USER_NAME", Prop.getProperty("hadoop.user"));
		System.setProperty("hadoop.home.dir", Prop.getProperty("hadoop.home.dir"));
		
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(Prop.getProperty("hadoop.server")), conf);
			Path homeDir=fs.getHomeDirectory();
			//System.out.println("Home folder -" +homeDir);
			Path workingDir=fs.getWorkingDirectory();
			String folderhn=hn.substring(0, 2).trim();
		      Path newFolderPath= new Path(path+"/"+folderhn+"/"+hn);
		      if(!fs.exists(newFolderPath)) {
		         // Create new Directory
		         fs.mkdirs(newFolderPath);
		      }
			Path localFilePath = new Path(folder_in);
			Path hdfsFilePath=new Path(newFolderPath+"/"+an+"AN.pdf");

			fs.moveFromLocalFile(localFilePath, hdfsFilePath);

			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
