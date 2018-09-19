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
 package org.utt.app.dent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;

import org.utt.app.common.ObjectData;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;


public class DentalReportPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
	WebTabbedPane tabbedPane,tabbedPane_in;
	DentalReport dentalReport;
	Dental43ProcPanel dental43ProcPanel;
	Dental43Panel dental43Panel;
	DentalHx dentalHx;
	DentalConf dentConf;
	WebPanel midPanel;
	
	public DentalReportPanel(ObjectData oUserInfo,int w,int h) {
		this.oUserInfo=oUserInfo;
        width=w;
        height=h;
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(width-5, height-100));
		
		dentalReport = new DentalReport();
		dental43ProcPanel = new  Dental43ProcPanel();
		dental43Panel = new Dental43Panel();
		dentalHx =new DentalHx();
		dentConf =new DentalConf();
		
		tabbedPane = new WebTabbedPane();
		tabbedPane.setTabPlacement(WebTabbedPane.LEFT);
		//tabbedPane.addTab("<html>C<br>O<br>N<br>T<br>R<br>O<br>L </html>", null, user, "control");
		add(tabbedPane, BorderLayout.CENTER);
		
		midPanel = new WebPanel();
		midPanel.setLayout(new BorderLayout(0, 0));
		midPanel.setPreferredSize(new Dimension(width-5, height-100));
		//mainPanel.add(midPanel, BorderLayout.CENTER);
		
		 
        //tabbedPane.setBackground(new Color(232, 248, 245  ));
		tabbedPane_in = new WebTabbedPane();
        midPanel.add(tabbedPane_in, BorderLayout.CENTER);
        tabbedPane_in.addTab("รายงานทันตกรรม" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dentalReport);
        tabbedPane_in.addTab("รายงาน 43 แฟ้ม" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dental43Panel);
        tabbedPane_in.addTab("รายงาน การลงข้อมูล 43 แฟ้ม" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dentalHx);
        tabbedPane_in.addTab("แฟ้ม Procedure ทันตกรรม" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dental43ProcPanel);
        tabbedPane_in.addTab("Setup Code Tx" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),dentConf);
        
        tabbedPane.addTab("<html>C<br>O<br>N<br>T<br>R<br>O<br>L </html>", null, midPanel, "control");
	}
	 public void update(Observable oObservable, Object oObject) {
	        oUserInfo = ((ObjectData)oObservable); // cast
	 }

}

