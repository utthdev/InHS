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
package org.utt.app;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.h2.tools.DeleteDbFiles;
import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.kb.KbFrame;
import org.utt.app.util.I18n;
import org.utt.app.util.Prop;
import org.utt.app.util.Setup;

import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.button.WebButton;
import com.alee.laf.desktoppane.WebDesktopPane;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;

public class InApp extends WebFrame implements ActionListener{

	public final static String version =I18n.lang("label.version");
    public  static String ip = "";
    public  static String username = "";
    public  static String name = "";
    public  static String usertype = "";
    public  static String userappcode = "";
    public  static String userposition = "";
    public  static String cliniccode = "";
    public  static String reportcode = "";
    
    WebDesktopPane desktopPane;
    Dimension screen;
    int taskBarsize;
    WebToolBar toolBar;
    WebButton ButtonHome,ButtonExit,ButtonKB,ButtonJHOS;
    WebPanel lowPanel,userPanel,exitPanel,toolbarPanel;
    WebDialog dialogLogin;
    WebTextField TextField_Longin;
    WebPasswordField TextField_PassWord;
    WebButton menuButton[];
    WebLabel[] seperator = new WebLabel[]{new WebLabel(":"), new WebLabel(":")};
    WebLabel time_Label,user_Label;
    
    int SESSION_TIMEOUT = 10 * 1000;
	Timer invalidationTimer = new Timer(SESSION_TIMEOUT, this);
	
	KbFrame kbFrame;
	
	public InApp() {
		try {
            InetAddress ipcom= InetAddress.getLocalHost();
            ip=ipcom.getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        checkIP(ip);
        desktopPane = new WebDesktopPane();
        screen = Toolkit.getDefaultToolkit().getScreenSize();
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        taskBarsize = scnMax.bottom;
        setPreferredSize(new Dimension(screen.width, screen.height-taskBarsize));
        setBounds(0, 0, screen.width, screen.height-taskBarsize);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle(I18n.lang("desktop.title")+"  :  "+I18n.lang("label.version"));
        ImageIcon img = new ImageIcon(getClass().getClassLoader().getResource("images/inlogo.png"));
        setIconImage ( img.getImage());
        getContentPane().add(desktopPane, BorderLayout.CENTER);
        desktopPane.setBackground(Setup.getColor());
        getContentPane().add(createStatusBar (), BorderLayout.SOUTH);
        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                if (confirmBeforeExit()) {
                    System.exit(0);
                }
            }
        });
        //
        
        invalidationTimer.setRepeats(false);
        invalidationTimer.restart();
        final AWTEventListener l = new AWTEventListener() {

            @Override
            public void eventDispatched(AWTEvent event) {
                // if any input event invoked - restart the timer to prolong the session
                invalidationTimer.restart();
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(l, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        
        //
        createDialog(this,I18n.lang("label.dialog.title"));
	}
	public void checkIP(String ipUser){
        int passport=0;
        String ver="";
        Connection con=new DBmanager().getConnMySql();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(SetupSQL.sql_checkip);
            stmt.setString(1, ipUser.trim());
            ResultSet rs = stmt.executeQuery();
            int n=0;
            while( rs.next() ){
                n++;
                if(rs.getString(2) !=null){
                    ver=rs.getString(2).trim();
                }
            }
            if(n<1){
                passport=1;
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(passport==0){
            if(versionCompare(ver, Prop.getProperty("app.version"))<0){
                int result = WebOptionPane.showConfirmDialog(rootPane, I18n.lang("version.compare.text"),
                        I18n.lang("version.compare.title"), WebOptionPane.OK_OPTION);
                if (result == WebOptionPane.OK_OPTION) {
                    System.exit(0);
                }else{
                    System.exit(0);
                }
            }else if (versionCompare(ver,Prop.getProperty("app.version"))>=0){}
        }
        else{
            dispose();
            System.exit(0);
        }
    }
    private Integer versionCompare(String str1, String str2){
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])){
            i++;
        }
        if (i < vals1.length && i < vals2.length){
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }else{
            return Integer.signum(vals1.length - vals2.length);
        }
    }
    public boolean confirmBeforeExit(){
        if (WebOptionPane.showConfirmDialog(this, I18n.lang("desktop.confirmbeforeexitdialog.title"), I18n.lang("desktop.confirmbeforeexitdialog.text"), WebOptionPane.YES_NO_OPTION) == 0){
            return true;
        }
        return false;
    }
    public WebStatusBar createStatusBar () {
        WebStatusBar statusBar = new WebStatusBar ();
        statusBar.setBackground(Setup.getColor());
        statusBar.add( getUser ());
        statusBar.addSpacing ();
        statusBar.addToEnd( getClock ());
        return statusBar;
    }
    public WebStatusBar createToolBar () {
        WebStatusBar toolBar = new WebStatusBar ();
        toolBar.add( getToolBar ());
        toolBar.addSpacing ();
        toolBar.addToEnd( getExit ());
        return toolBar;
    }
    public WebPanel getUser() {
        userPanel = new WebPanel();
        userPanel.setPreferredSize(new Dimension(300, 20));
        userPanel.setLayout(null);
        user_Label =Setup.getLabel(I18n.lang("label.date"), 13,1,SwingConstants.LEFT);
        user_Label.setBounds(1, 1, 300, 20);
        userPanel.add(user_Label);

        return userPanel;
    }
    public WebPanel getClock() {
        lowPanel = new WebPanel();
        lowPanel.setPreferredSize(new Dimension(300, 20));
        lowPanel.setLayout(null);
        time_Label =Setup.getLabel(I18n.lang("label.date"), 13,1,SwingConstants.CENTER);
        time_Label.setBounds(1, 1, 300, 20);
        lowPanel.add(time_Label);
        Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Setup.setLocale();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat(I18n.lang("clock.date.pattern"));
                Date currentTime = cal.getTime( );
                time_Label.setText(dateFormat.format(currentTime));
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
        return lowPanel;
    }
    public WebPanel getToolBar() {
        toolbarPanel = new WebPanel();
        toolbarPanel.setPreferredSize(new Dimension(screen.width-80, 30));
        toolbarPanel.setBackground(Setup.getColor());
        toolbarPanel.setLayout(null);   
        toolBar = new WebToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(Setup.getColor());
        toolBar.setBounds(1, 1, screen.width-80, 30);
        toolbarPanel.add(toolBar);

        return toolbarPanel;
    }
    public WebPanel getExit() {
        exitPanel = new WebPanel();
        exitPanel.setPreferredSize(new Dimension(80, 30));
        exitPanel.setBackground(Setup.getColor());
        exitPanel.setLayout(null);

        
        ButtonKB = new WebButton("KB");
        ButtonKB.setBackground(Setup.getColor());
        ButtonKB.setBounds(5, 1, 40, 28);
        ButtonKB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	disableFrame();
                if(kbFrame !=null) {
                    kbFrame.setVisible(true);
                }else {
                    kbFrame = new KbFrame(screen.width, screen.height-taskBarsize);
                    desktopPane.add(kbFrame);
                    kbFrame.setVisible(true);
                    try {
                        kbFrame.setMaximum(true);
                    } catch (PropertyVetoException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        exitPanel.add(ButtonKB); 
        
        ButtonExit = new WebButton("");
        ButtonExit.setBackground(Setup.getColor());
        ButtonExit.setBounds(45, 1, 30, 28);
        ButtonExit.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/exit.gif")));
        ButtonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (confirmBeforeExit()){
                    //DeleteDbFiles.execute("~", "indb", true);
                    System.exit(0);
                }
            }
        });

        exitPanel.add(ButtonExit);

        return exitPanel;
    }
    public void createDialog(JFrame f,String title){
    	
    }
    public void disableFrame() {
    	
    }
    @SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
        // provide session invalidation here (show login dialog or do something else)
        WebOptionPane.showMessageDialog(this, "Your session is invalid");
        disableFrame();
        createDialog(this,I18n.lang("label.dialog.title"));
        //invalidationTimer.restart();
    }
    public void initH2OPD() {
		DeleteDbFiles.execute("~", "indb", true);
		Connection conn=new DBmanager().getConnH2();
		try {
			PreparedStatement stmt1 = conn.prepareStatement("CREATE TABLE labalert ( vn varchar(5),clinic varchar(7),reqno varchar(20),status varchar(5) )");
			stmt1.executeUpdate();
			stmt1.close();
  
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
