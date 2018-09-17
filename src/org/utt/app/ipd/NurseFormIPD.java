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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import org.utt.app.common.ObjectData;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;

public class NurseFormIPD extends WebPanel implements Observer,ActionListener{
	ObjectData oUserInfo;
    int width,height;
    WebButton ButtonPrint;
    
    public NurseFormIPD(ObjectData oUserInfo,int w,int h) {
    	width=w;
        height=h;
        this.oUserInfo=oUserInfo;
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(width-(width*2)/10, height-160));
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData)oObservable); // cast
    }
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == ButtonPrint){			 
			//PrintLabLabel();
			 
		}
    }
    public void setComboBox() {
    	
    }

}
