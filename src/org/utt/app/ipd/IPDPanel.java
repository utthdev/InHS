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
package org.utt.app.ipd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;

import org.utt.app.common.ObjectData;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class IPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    WebSplitPane split,splitR;
    WebPanel leftPanel,midPanel,topMainPanel;
    WebTabbedPane tabbedPane;
    
    NurseFormIPD nurseFormIPD;
    
    public IPDPanel(ObjectData oUserInfo,int w,int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        split = new WebSplitPane(split.HORIZONTAL_SPLIT);
        split.setDividerLocation((width*2)/10);
        splitR = new WebSplitPane(split.VERTICAL_SPLIT);
        splitR.setDividerLocation(140);
        
        nurseFormIPD = new NurseFormIPD(oUserInfo,w,h);
        
        setTop();
        setLeft();
        setRight();

        add(split, BorderLayout.CENTER);
        split.setRightComponent(splitR);
        split.setLeftComponent(leftPanel);
        split.setOneTouchExpandable(true);
        splitR.setTopComponent(topMainPanel);
        splitR.setBottomComponent(midPanel);
    }
    public void update(Observable oObservable, Object oObject) {
    	oUserInfo = ((ObjectData) oObservable); // cast
    }
    public void initForm() {
        getData();
        nurseFormIPD.setComboBox();
    }
    public void setTop(){
    	
    }
    public void setLeft(){
    	
    }
    public void setRight(){
    	midPanel = new WebPanel();
		midPanel.setLayout(new BorderLayout(0, 0));
		midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-160));
		
		tabbedPane = new WebTabbedPane();
        midPanel.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab(" Nurse FORM ",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,nurseFormIPD);

    }
    public void getData() {
    	
    }

}
