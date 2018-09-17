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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.hadoop.fs.FileSystem;
import org.utt.app.common.ObjectData;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.sun.pdfview.PDFFile;

public class MainHDFSOPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebPanel LeftSection,RightSection,MiddleSection,MainSection ;
    WebPanel right1,mid2,mid1,right2,right21,right22;
    WebLabel Label_Info,labelImage;
    WebSplitPane split;
    WebAccordion accordion;
    WebButton ButtonPrint;
    WebTable table;
    Vector<String> columnNames;
    
    String fn="";
    
    public MainHDFSOPDPanel(ObjectData oUserInfo, int w, int h) {
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
        
        WebLabel label = Setup.getLabel(I18n.lang("label.pastTx"), 13,4,SwingConstants.CENTER);
        LeftSection.add(label, BorderLayout.NORTH);
        ButtonPrint = new WebButton("");
        ButtonPrint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                printOPD(fn);
            }
        });
        ButtonPrint.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/printer.png")));
        ButtonPrint.setBounds(width-((width*2)/10)-660, 12, 100, 25);
        
        //
        accordion = new WebAccordion( );
        accordion.addPane ( null, I18n.lang("label.hdfs.3mon"), getHDFS("3mon",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.scope"), getHDFS("scope",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.all"),   getHDFS("all",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.past"),   getHDFS("past",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.ipd"),   getHDFS("ipd",oUserInfo.GetPtHN()));        
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
        mid1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mid1.setPreferredSize(new Dimension(width-((width*2)/10-480), 40));
        MiddleSection.add(mid1, BorderLayout.NORTH);
        mid1.setLayout(null);
        
        labelImage = new ScaledImageLabel();
        labelImage.setPreferredSize(new Dimension(16, 16));
        labelImage.setBounds(5, 14, 16, 16);
        mid1.add(labelImage);

        Label_Info = Setup.getLabel("", 13, 4, SwingConstants.LEFT);
        Label_Info.setBounds(20, 18, 300, 15);
        mid1.add(Label_Info);
        
        add(split, BorderLayout.CENTER);

    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public WebPanel getHDFS(String type,String hn){ 
    	WebPanel wp = new WebPanel();
    	wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension((width*2)/10,75));
		if(type.trim().equals("3mon")) {
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
			        setPermissionImg(ch2);
			        Label_Info.setText("  "+d+" "+Setup.getMonthShortThaiName(m)+" "+y+"  รหัสห้องตรวจ:  "+c+"  หน้าที่ : "+p);
			        mid2.removeAll();
			        mid2.revalidate();
			        mid2.repaint();
			        showFilePDF(filename);
				}			
			});
			WebScrollPane scrollPane = new WebScrollPane(table);      
	        wp.add(scrollPane, BorderLayout.CENTER);
		}
		 	
    	return wp;
    	
    }
    public  void showFilePDF(String filename){
    	
    }
    public void printOPD(String fn){
    	if(fn.trim().length()==21 || fn.trim().length()==25) {
    		ByteBuffer buf = null;
    		String hn=fn.substring(fn.length()-7).trim();
    		String folderhn=hn.substring(0, 2).trim();	
    		String path1="/utth/"+folderhn+"/"+hn;

    		FileSystem fs=null;
    		PDFFile pdffile;
    	}
    }
    public void setPermissionImg(String ch1) {
    	if(ch1.equals("00")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/unlock.png")));
        }
        else if(ch1.equals("01")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/unlock.png")));
        }
        else if(ch1.equals("02")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/lock.png")));
        }  	
    }
}
