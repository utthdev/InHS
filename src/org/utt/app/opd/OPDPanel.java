package org.utt.app.opd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;

import org.utt.app.common.ObjectData;
import org.utt.app.common.UserView;
import org.utt.app.hdfs.MainHDFSOPDPanel;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class OPDPanel extends WebPanel implements Observer,UserView{
	ObjectData oUserInfo;
    int width,height;
    
    WebTabbedPane tabbedPane,tabbedPane_in;
    WebPanel midPanel;
    
    MainHDFSOPDPanel mainHDFSOPDPanel;
    ScanScope scanScope;
    OPDFormPanel opdFormPanel;
    
    
    public OPDPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
        
        mainHDFSOPDPanel =new MainHDFSOPDPanel(oUserInfo,w,h);
        scanScope =new ScanScope(oUserInfo,w,h);
        opdFormPanel =new OPDFormPanel(oUserInfo,w,h);
        
        setRight();
        
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }
    public void setRight(){
    	midPanel = new WebPanel();
        midPanel.setLayout(new BorderLayout(0, 0));
        midPanel.setPreferredSize(new Dimension(width-((width*2)/10), height-140));
        tabbedPane_in = new WebTabbedPane();
        midPanel.add(tabbedPane_in, BorderLayout.CENTER);
        tabbedPane_in.addTab("OPD FORM" ,new ImageIcon(getClass().getClassLoader().getResource("images/indent_out.gif")),opdFormPanel);
        tabbedPane_in.addTab("EMR",new ImageIcon(getClass().getClassLoader().getResource("images/list_unordered.gif")) ,mainHDFSOPDPanel);
        
    }
    public void getData() {
    	
    }

}
