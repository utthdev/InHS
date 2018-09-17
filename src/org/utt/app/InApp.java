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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.utt.app.dao.DBmanager;
import org.utt.app.dao.SetupSQL;
import org.utt.app.util.I18n;
import org.utt.app.util.Prop;

import com.alee.laf.desktoppane.WebDesktopPane;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.rootpane.WebFrame;

public class InApp extends WebFrame{
	Dimension screen;
    int taskBarsize;
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
}
