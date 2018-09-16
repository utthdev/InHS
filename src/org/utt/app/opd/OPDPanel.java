package org.utt.app.opd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import org.utt.app.common.ObjectData;

import com.alee.laf.panel.WebPanel;

public class OPDPanel extends WebPanel implements Observer{
	ObjectData oUserInfo;
    int width,height;
    
    public OPDPanel(ObjectData oUserInfo, int w, int h) {
    	this.oUserInfo=oUserInfo;
        width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);
    }
    public void update(Observable oObservable, Object oObject) {
        oUserInfo = ((ObjectData) oObservable); // cast
    }

}
