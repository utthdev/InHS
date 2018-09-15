package org.utt.app;

import org.utt.app.util.Prop;

import javax.swing.*;
import java.awt.*;

public class In {
    public In() {
        new InApp();
    }
    public static void main( String[] args ){
    	
        try {
            UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Prop.init();
                    macosConfig();
                    new In();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public static void macosConfig() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }
}
