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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.utt.app.common.ObjectData;
import org.utt.app.ui.ScaledImageLabel;
import org.utt.app.util.DateLabelFormatter;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;

public class IPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    String day="",month="",dateSearch="";
    WebSplitPane split,splitR;
    WebPanel leftPanel,midPanel,topMainPanel,pt_info,jPanel;
    WebTabbedPane tabbedPane;
    WebLabel Label_AN,Label_HN,Label_CID,Label_NAME,Label_Memo,Label_Extra,Label_Extra1,Label_LOS,labelImage;
    WebTextField TextField_AN,TextField_HN,TextField_CID;
    WebTextArea textArea;
    WebButton ButtonSmartcard;
    WebScrollPane scrollPaneDrugA;
    JDatePickerImpl picker;
    
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
    	topMainPanel = new WebPanel();
        topMainPanel.setLayout(new BorderLayout(0, 0));
        topMainPanel.setPreferredSize(new Dimension(width-((width*2)/10), 140));
        topMainPanel.setBackground(Setup.getColor());
        pt_info = new WebPanel();
        pt_info.setLayout(null);
        pt_info.setBackground(Setup.getColor());
        topMainPanel.add(pt_info, BorderLayout.CENTER);
        Setup.setLocale();
        WebLabel Label_Date = Setup.getLabel(I18n.lang("label.date"), 13,0, SwingConstants.CENTER);
        Label_Date.setBounds(5,8,30,20);
        pt_info.add(Label_Date);
        UtilDateModel model = new UtilDateModel();
        model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "");
        p.put("text.month", "");
        p.put("text.year", "");
        JDatePanelImpl datePanel = new JDatePanelImpl(model,p);
        picker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        picker.setBackground(Setup.getColor());
        picker.getJFormattedTextField().setBackground(Setup.getColor());
        picker.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                if(picker.getModel().getDay()<10){
                    day="0"+picker.getModel().getDay();
                }else{
                    day=""+picker.getModel().getDay();
                }
                if(picker.getModel().getMonth()+1<10){
                    month="0"+(picker.getModel().getMonth()+1);
                }else{
                    month=""+(picker.getModel().getMonth()+1);
                }
                dateSearch=picker.getModel().getYear()+"-"+month+"-"+day;
                oUserInfo.setPtVisitdate(dateSearch.trim());


            }
 
			
        });
        picker.setShowYearButtons(true);
        picker.setTextEditable(false);
        jPanel = new WebPanel();
        jPanel.setBounds(30, 4, 220, 30);
        jPanel.setBackground(Setup.getColor());
        jPanel.add((JComponent)picker);
        pt_info.add(jPanel);

        if(picker.getModel().getDay()<10){
            day="0"+picker.getModel().getDay();
        }else{
            day=""+picker.getModel().getDay();
        }
        if(picker.getModel().getMonth()+1<10){
            month="0"+(picker.getModel().getMonth()+1);
        }else{
            month=""+(picker.getModel().getMonth()+1);
        }
        dateSearch=picker.getModel().getYear()+"-"+month+"-"+day;
        oUserInfo.setPtVisitdate(dateSearch.trim());
        
        Label_AN = Setup.getLabel(I18n.lang("label.an"), 13,4,SwingConstants.RIGHT);
        Label_AN.setBounds(270, 12, 20, 14);
		pt_info.add(Label_AN);
		 
		
		TextField_AN = Setup.getTextField(12);
		TextField_AN.setEditable(false);
		pt_info.add(TextField_AN);
		TextField_AN.setBounds(290, 10, 80, 20);
	
		
		Label_HN = Setup.getLabel(I18n.lang("label.hn"), 13,4,SwingConstants.RIGHT);
		Label_HN.setBounds(390, 12, 20, 14);
		pt_info.add(Label_HN);
		
		TextField_HN = Setup.getTextField(12);
		TextField_HN.setEditable(false);
		TextField_HN.setText("");
		TextField_HN.setBounds(410, 10, 80, 20);
		pt_info.add(TextField_HN);

		
		Label_CID = Setup.getLabel(I18n.lang("label.id"), 13,4,SwingConstants.RIGHT);
		Label_CID.setBounds(510, 12, 20, 14);
		pt_info.add(Label_CID);
		
		TextField_CID = Setup.getTextField(12);
		TextField_CID.setEditable(false);
		//TextField_CID.setBackground(new Color(229, 200, 179));
		TextField_CID.setBounds(530, 10, 150, 20);
		TextField_CID.setText("");
		pt_info.add(TextField_CID);

		
		Label_NAME = Setup.getLabel(I18n.lang("label.name"), 13,4,SwingConstants.LEFT);
		Label_NAME.setBounds(4, 30, (width-((width*2)/10)-40), 30);
		pt_info.add(Label_NAME);
		
		textArea = new WebTextArea();
		textArea.setEditable(false);
		textArea.setForeground(new Color(255, 0, 0));
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textArea.setBackground(Setup.getColor());
		//textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(70, 70, 500, 200);
		//pt_info.add(textArea);
		
		scrollPaneDrugA = new WebScrollPane(textArea);
		scrollPaneDrugA.setBounds(70, 70, 500, 40);
		pt_info.add(scrollPaneDrugA);
		
		Label_Memo = Setup.getLabel(I18n.lang("label.drugallergy"), 13,4,SwingConstants.LEFT);
		Label_Memo.setForeground(new Color(255, 0, 0));
		Label_Memo.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Memo.setBackground(Setup.getColor());
		Label_Memo.setBounds(10, 65, 55, 30);
		pt_info.add(Label_Memo);
		
		Label_Extra = Setup.getLabel(I18n.lang("label.extra"), 13,4,SwingConstants.LEFT);
		Setup.SetUnderline(Label_Extra);
		Label_Extra.setBounds(10, 110, 150, 30);
		Label_Extra.setBackground(Setup.getColor());
		pt_info.add(Label_Extra);
		
		Label_Extra1 = Setup.getLabel("", 13,4,SwingConstants.RIGHT);
		Setup.SetUnderline(Label_Extra1);
		Label_Extra1.setBounds(150, 110, 400, 30);
		Label_Extra1.setBackground(Setup.getColor());
		pt_info.add(Label_Extra1);
		
		Label_LOS = Setup.getLabel(I18n.lang("label.los"), 13,4,SwingConstants.RIGHT);
		Label_LOS.setHorizontalAlignment(SwingConstants.LEFT);
		Label_LOS.setBounds(697, 12, 200, 14);
		pt_info.add(Label_LOS);
		
		labelImage = new ScaledImageLabel();
        labelImage.setPreferredSize(new Dimension(140, 140));
        labelImage.setBounds(width-(((width*2)/10)+200), 1, 140, 140);
        pt_info.add(labelImage);
        
        
        if(oUserInfo.GetPtAN().equals("")) {
        	labelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/no_img.png")));				 			
		}else {
			//System.out.println(oUserInfo.GetPtHN());
			//labelImage.setIcon(new ImageIcon(previewPDFDocumentInImage(oUserInfo.GetPtHN())));
		}      
        
        ButtonSmartcard = new WebButton(I18n.lang("text.readsmartcard"));
        ButtonSmartcard.setBackground(Setup.getColor());
        ButtonSmartcard.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonSmartcard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //clearValue();
                //getSmartcard();
               //sendImageHDFS(oUserInfo.GetPtCID());
            }
        });
        ButtonSmartcard.setBounds(700, 70, 150, 25);
        //pt_info.add(ButtonSmartcard);
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
