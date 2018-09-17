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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.utt.app.common.ObjectData;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.splitpane.WebSplitPane;

public class MainHDFSOPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebPanel LeftSection;
    WebSplitPane split;
    WebAccordion accordion;
    WebButton ButtonPrint;
    
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
        accordion.addPane ( null, I18n.lang("label.hdfs.3mon"), getHDFS("3Mon",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.scope"), getHDFS("scope",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.all"),   getHDFS("all",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.past"),   getHDFS("past",oUserInfo.GetPtHN()));
        accordion.addPane ( null, I18n.lang("label.hdfs.ipd"),   getHDFS("ipd",oUserInfo.GetPtHN()));        
        accordion.setMultiplySelectionAllowed ( false );
        LeftSection.add(accordion, BorderLayout.CENTER);
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public WebPanel getHDFS(String type,String hn){ 
    	WebPanel wp = new WebPanel();
    	wp.setLayout(new BorderLayout(0, 0));
		wp.setPreferredSize(new Dimension((width*2)/10,75));
		
		
		
    	
    	return wp;
    	
    }
}
