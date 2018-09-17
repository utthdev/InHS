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
package org.utt.app.kb;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.alee.laf.panel.WebPanel;

public class KbPanel extends WebPanel{
	int width,height;
	
	 public KbPanel(int w,int h) {
		 width=w;
	     height=h;
	     setLayout(new BorderLayout(0, 0));
	     setPreferredSize(new Dimension(width, height));
	     setBounds(0, 0, width, height);
	     
	     
		 
	 }

}
