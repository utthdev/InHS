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
package org.utt.app.opd;

import java.awt.BorderLayout;
import java.awt.Dimension;

import org.utt.app.common.ObjectData;
import org.utt.app.ui.IFrame;
import org.utt.app.util.Prop;
import org.utt.app.util.Setup;

public class OPDFrame extends IFrame{
	Dimension screen;
    ObjectData objectData;
    OPDPanel opdPanel;

    public OPDFrame(int w,int h) {
        super();
        setBounds(0, 0, w, h);
        setPreferredSize(new Dimension(w, h));
        setTitle(Prop.getProperty("module.name.opd"));
        setLocation(0, 0);
        //setClosable(true);
        //setIconifiable(true);
        //setMaximizable(true);
        objectData = new ObjectData();
        opdPanel = new OPDPanel(objectData,getBounds().width,getBounds().height);
        objectData.addObserver(opdPanel);
        getContentPane().add(opdPanel, BorderLayout.CENTER);


        updateUI();

    }

}
