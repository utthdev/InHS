package org.utt.app.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class ZoomPane extends JPanel{
	Image background;
	Image scaled;
    float zoom = 1f;

    Dimension scaledSize,screen;
    JViewport con;

    public ZoomPane(Image img) {
    	screen = Toolkit.getDefaultToolkit().getScreenSize();
    	setPreferredSize(new Dimension(screen.width-((screen.width*2)/10-480), screen.height-215));
    	setLayout(null);
        background = img;
        if(background !=null) {
        	scaled = background;
            scaledSize = new Dimension(background.getWidth(this), background.getHeight(this));
        }else {
        	scaled = null;
        }
         
        setFocusable(true);
        requestFocusInWindow();
        addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		if (SwingUtilities.isLeftMouseButton(e)) {
        			 setZoom(getZoom() + 0.1f);
        		}
        		if (SwingUtilities.isRightMouseButton(e)) {
        			setZoom(getZoom() - 0.1f);
        		}
        		if (SwingUtilities.isMiddleMouseButton(e)) {
        			setZoom(zoom);
        		}
        	}
        });
    }
    public float getZoom() {
        return zoom;
    }

    public void setZoom(float value) {
        if (zoom != value) {
            zoom = value;

            if (zoom < 0) {
                zoom = 0f;
            }

            int width = (int) Math.floor(background.getWidth(this) * zoom);
            int height = (int) Math.floor(background.getHeight(this) * zoom);
            scaled = background.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            scaledSize = new Dimension(width, height);

            if (getParent() instanceof JViewport) {

                int centerX = width / 2;
                int centerY = height / 2;

                JViewport parent = (JViewport) getParent();
                Rectangle viewRect = parent.getViewRect();
                viewRect.x = centerX - (viewRect.width / 2);
                viewRect.y = centerY - (viewRect.height / 2);
                scrollRectToVisible(viewRect);
            }

            invalidate();
            repaint();

        }
    }

    public Dimension getPreferredSize() {
        return scaledSize;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scaled != null) {
            g.drawImage(scaled, 0, 0, this);
        }else {
        	return;
        }
    }
    public void centerInViewport() {
        Container container = getParent();
        if (container instanceof JViewport) {

            JViewport port = (JViewport) container;
            Rectangle viewRect = port.getViewRect();

            int width = getWidth();
            int height = getHeight();

            viewRect.x = (width - viewRect.width) / 2;
            viewRect.y = (height - viewRect.height) / 2;

            scrollRectToVisible(viewRect);

        }

    }
    public void clearImg() {
    	scaled = null;
    	invalidate();
        repaint();
    }


}
