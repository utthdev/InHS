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
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import org.utt.app.common.ObjectData;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class AdminPanel extends WebPanel  implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebTabbedPane tabbedPane,tabbedPane_in;
    WebPanel midPanel;
    
    UserControl user;
    CompControl comp;
    PDFAdmin pdfAdmin;
    OPDReport opdReport;
    IPDReport ipdReport;
    WardControl wardControl;
    PDFAdjust pdfAdjust;
    IPDChartPanel ipdChartPanel ;
    
    public AdminPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        user = new UserControl(w,h);
        comp = new CompControl(w,h);
        pdfAdmin =new PDFAdmin(w,h);
        opdReport = new OPDReport(w,h);
        ipdReport = new IPDReport(w,h);
        wardControl = new WardControl(w,h);
        pdfAdjust =new PDFAdjust(w,h);
        ipdChartPanel  =new IPDChartPanel (w,h);
        
        midPanel = new WebPanel();
        midPanel.setLayout(new BorderLayout(0, 0));
        midPanel.setPreferredSize(new Dimension(width, height));

        tabbedPane_in = new WebTabbedPane();
        midPanel.add(tabbedPane_in, BorderLayout.CENTER);
        tabbedPane_in.addTab("User Management" ,null,user);
        tabbedPane_in.addTab("Computer Management" ,null,comp);
        tabbedPane_in.addTab("PDF Admin" ,null,pdfAdmin);
        tabbedPane_in.addTab("OPD Report" ,null,opdReport);
        tabbedPane_in.addTab("IPD Report" ,null,ipdReport);
        //tabbedPane_in.addTab("Doctor Management" ,null,dr);
        tabbedPane_in.addTab("Ward Management" ,null,wardControl);
        tabbedPane_in.addTab("Dental Adjument PDF" ,null, pdfAdjust);
        tabbedPane_in.addTab("Import AN" ,null, ipdChartPanel);

        tabbedPane = new WebTabbedPane();
        tabbedPane.setTabPlacement(WebTabbedPane.LEFT);
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("<html>C<br>O<br>N<br>T<br>R<br>O<br>L </html>", null, midPanel, "control");
        
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
}
