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
package org.utt.app.adm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.utt.app.util.I18n;
import org.utt.app.util.Setup;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebTextField;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

public class PDFAdjust extends WebPanel{
	int width,height;
	 WebPanel leftPanel,bottomPanel,mainLeftPanel,MiddleSection,mid1,mid2;
	 WebButton ButtonScan,ButtonView;
	 WebLabel Label_Info1,Label_Info,Label_IMG;
	 WebTable table;
	 WebScrollPane scrollPane;
	 Vector<String> columnNames;
	 
	 WebButton ButtonRefresh;
	 String nameBefore="",nameAfter="",filename="";
	 ImageIcon icon;
	 WebTextField TextField_page,TextField_hn;
	 
	public PDFAdjust(int w,int h) {
		width=w;
        height=h;
        setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(width-5, height));
		
		setLeft();
		setRight();
		
	}
	public void setRight(){
		MiddleSection = new WebPanel();
		add(MiddleSection, BorderLayout.CENTER);
		MiddleSection.setPreferredSize(new Dimension(width-((width*2)/10), height));
		MiddleSection.setLayout(new BorderLayout(0, 0));
		
		mid1 = new WebPanel();
		mid1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mid1.setPreferredSize(new Dimension(width-((width*2)/10), 30));
		MiddleSection.add(mid1, BorderLayout.NORTH);
		mid1.setLayout(null);
		
		Label_Info = new WebLabel(" ");
		Label_Info.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info.setBounds(15,5,252,20);
		mid1.add(Label_Info);
		
		TextField_page = new WebTextField();
		TextField_page.setBounds(20, 5, 40, 20);
		TextField_page.setText("01");
		mid1.add(TextField_page);
	
		
		TextField_hn = new WebTextField();
		TextField_hn.setBounds(80, 5, 100, 20);
		mid1.add(TextField_hn);

		
		ButtonScan = new WebButton("Scan");
		ButtonScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scanOPD();
			}
		});
		ButtonScan.setBounds(width-((width*2)/10)-400, 4, 89, 23);
		mid1.add(ButtonScan);
		
		
		Label_Info1 = new WebLabel(" ");
		Label_Info1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Label_Info1.setHorizontalAlignment(SwingConstants.LEFT);
		Label_Info1.setBounds(277, 5, 269, 20);
		mid1.add(Label_Info1);
		
		mid2 = new WebPanel();
		mid2.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(176, 224, 230), null, new Color(250, 235, 215), null));
		mid2.setPreferredSize(new Dimension(800, 600));
		MiddleSection.add(mid2, BorderLayout.CENTER);
		mid2.setLayout(null);
		
		Label_IMG = new WebLabel("");
		Label_IMG.setHorizontalAlignment(SwingConstants.CENTER);
		Label_IMG.setBounds(30,1,800,600);
		mid2.add(Label_IMG);
		
	}
	public void setLeft(){
		leftPanel = new WebPanel();
		leftPanel.setPreferredSize(new Dimension((width*2)/10, height));
		leftPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		leftPanel.setLayout(new BorderLayout(0, 0));
		add(leftPanel, BorderLayout.WEST);
		
		columnNames = new Vector<String>();
        columnNames.add(I18n.lang("label.order"));
        columnNames.add("File Name");
		
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }
        };
        table = new WebTable(model){
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component c = super.prepareRenderer(renderer, row, column);
                return c;
            }
        };
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	TextField_hn.setText("");
            	nameBefore="";
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
		       
                String select=table.getValueAt(row,1).toString().trim();

                nameBefore= select;
                FileInputStream in;
    			try {
    				in = new FileInputStream("V:\\DentalFile\\"+nameBefore);
    				FileChannel channel = in.getChannel();
    			    ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
    			    channel.read(buffer);
    			    Image image = load(buffer.array());			 
    				Image imageScaled = image.getScaledInstance(800, 600,  Image.SCALE_SMOOTH);
    				icon = new ImageIcon(imageScaled);
    				
    				Label_IMG.setIcon(icon);
    				//close load
    				channel.close();
    				in.close();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
                
            }
        });
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        scrollPane = new WebScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel = new WebPanel();
        bottomPanel.setBackground(Setup.getColor());
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(null);

        bottomPanel.setPreferredSize(new Dimension((width*2)/10, 30));
        ButtonRefresh = new WebButton(I18n.lang("label.refresh"));
        ButtonRefresh.setBackground(Setup.getColor());
        ButtonRefresh.setForeground(UIManager.getColor("Button.darkShadow"));

        ButtonRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                getData();
            }
        });
        ButtonRefresh.setBounds((((width*2)/10)/2-45), 2, 90, 25);
        bottomPanel.add(ButtonRefresh);	
	}
	public void getData(){
        table.setModel(fetchData());
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(200 );

        ((DefaultTableModel)table.getModel()).fireTableDataChanged();
    }
	public  DefaultTableModel fetchData(){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		String folder="V:\\DentalFile\\";
		File f = new File(folder);
		File[] listOfFiles = f.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {     		
        		System.out.println("File>>"+listOfFiles[i].getName());
        		final Vector<String> vstring = new Vector<String>();
        		vstring.add(""+(i+1));
                vstring.add(listOfFiles[i].getName().trim()); 
                data.add(vstring);
        	}
		}	
		return new DefaultTableModel(data, columnNames);
	}
	public Image load(byte[] data) throws IOException{
		Image image = null;
	    SeekableStream stream = new ByteArraySeekableStream(data);
	    String[] names = ImageCodec.getDecoderNames(stream);
	    ImageDecoder dec = 
	      ImageCodec.createImageDecoder(names[0], stream, null);
	    RenderedImage im = dec.decodeAsRenderedImage();
	    image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
	    return image;
	}
	public void scanOPD(){
		filename=Setup.DateInDBMSSQLRef(nameBefore.substring(0,10))+"1100"+TextField_page.getText().trim()+"0001"+TextField_hn.getText().trim();
		System.out.println(filename);
		nameAfter=filename+".tif";
		saveFile(nameBefore,nameAfter);
		
	}
	public void saveFile(String input,String output){
		
		String Before=input.trim();
		String After=output.trim();		
		String hn_folder=After.substring(14);
		String tiff_in=Before;
		String pdf_after=After.substring(0,After.length()-4);
		String pdf_out="C:\\utthscan\\"+pdf_after+".pdf";
		makePDF(tiff_in,pdf_out);	 
		sendFileHDFS(pdf_after);
		nameBefore="";
		nameAfter="";		
	}
	public void makePDF(String tiff_in,String pdf_out){
		String pdf=pdf_out;
		String tiff="V:\\DentalFile\\"+tiff_in;
		RandomAccessFileOrArray ra;
		try {
			ra = new RandomAccessFileOrArray(tiff);
			int n = TiffImage.getNumberOfPages(ra);
			float x, y;
	        for (int p = 1; p <= n; p++) {
	        	com.lowagie.text.Image image = TiffImage.getTiffImage(ra, 1);
	        	com.lowagie.text.Rectangle pageSize = new com.lowagie.text.Rectangle(image.getWidth(), image.getHeight()+80);
	        	Document document = new Document(pageSize);
	        	PdfWriter writer = PdfWriter.getInstance(document,  new FileOutputStream(pdf));
	        	
	        	writer.setStrictImageSequence(true);
	        	document.open();
	        	document.add(image);        	
	        	Phrase p1 = new Phrase(tiff.trim(),FontFactory.getFont(FontFactory.COURIER, 24, com.lowagie.text.Font.BOLD,new GrayColor(0.5f)));
	        	PdfContentByte canvas = writer.getDirectContent();
	        	x = (pageSize.getLeft() + pageSize.getRight()) / 4;
	        	y = (pageSize.getTop() + pageSize.getBottom()) / 10;
	        	canvas.saveState();
	        	//document.newPage();
	        	PdfGState state = new PdfGState();
	            state.setFillOpacity(0.2f);
	            canvas.setGState(state);
	            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, p1, x, y, 0);
	            canvas.restoreState();
	        	document.close();      	
	            ra.close();
	        }
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}
         
	}
	public void sendFileHDFS(String fn){
		String path="/utth";

		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://172.17.71.13:9000");
		System.setProperty("HADOOP_USER_NAME", "hduser");
		System.setProperty("hadoop.home.dir", "/");
		
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create("hdfs://172.17.71.13:9000"), conf);
			Path homeDir=fs.getHomeDirectory();
			//System.out.println("Home folder -" +homeDir);
			Path workingDir=fs.getWorkingDirectory();
			String hn=fn.trim().substring(fn.length()-7);
			String folderhn=hn.substring(0, 2).trim();
		      Path newFolderPath= new Path(path+"/"+folderhn+"/"+hn);
		      if(!fs.exists(newFolderPath)) {
		         // Create new Directory
		         fs.mkdirs(newFolderPath);
		      }
			Path localFilePath = new Path("c://utthscan//"+fn+".pdf");
			Path hdfsFilePath=new Path(newFolderPath+"/"+fn+"HN.pdf");
			System.out.println(">>"+fn);
			fs.moveFromLocalFile(localFilePath, hdfsFilePath);

			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClearFile();
		renameFilescan("V:\\DentalFile\\"+nameBefore,"V:\\DentalFile_finish\\"+nameBefore);
		getData();
	}
	public void ClearFile(){		
		File folder = new File("C:\\utthscan\\");
		File[] listOfFiles = folder.listFiles();
        //System.out.println(listOfFiles.length);
        String file_name_input[]=new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
        	if (listOfFiles[i].isFile()) {     		
        		listOfFiles[i].delete();
        	}
        	else if(listOfFiles[i].isDirectory()){
        		listOfFiles[i].delete();
        	}
        }
	}
	public void renameFilescan(String fileinput ,String newfilename){
		File infile =new File(fileinput);
		File outfile =new File(newfilename);
		if (!outfile.exists()) {
			 if(infile.renameTo(new File(newfilename))){
				 
			 }
		}else{
			outfile.delete();
			if(infile.renameTo(new File(newfilename))){
				
			}
		}
	}

}
