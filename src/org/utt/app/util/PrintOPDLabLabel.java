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
package org.utt.app.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;

public class PrintOPDLabLabel implements Printable{
	PageFormat pf;
	Paper paper;
	Book bk;
	DecimalFormat FormatNumber;
	String hn1,hn2,vn1,vn2,age1,age2,name_print1,name_print2,date,clinicname;
	public PrintOPDLabLabel(){
		pf = new PageFormat();
		paper = new Paper();
		// 20.5x17.5mm
	    double width = Setup.cmsToPixel(20.5, 72);
	    double height = Setup.cmsToPixel(17.5, 72);
	 // 1 mm border...
	    
	    paper.setImageableArea(
	                    Setup.cmsToPixel(0.01, 72),
	                    Setup.cmsToPixel(0.031, 72),
	                   
	                    width - Setup.cmsToPixel(0.01, 72),
	                    height - Setup.cmsToPixel(0.03, 72));
	    pf.setOrientation(PageFormat.PORTRAIT);
		pf.setPaper(paper);	
		
	}
	public void setHN1(String hn){
		hn1=hn;		
	}
	public void setVN1(String vn){
		vn1=vn;	
	}
	public void setName1(String name){
		String name_check=name;
		//System.out.println(name_check);
		if(name_check.equals("")){
			
		}else{
		if(name_check.length()>26){
			name_print1=name_check.substring(0,26);
		}else{
			name_print1=name_check;
		}
		}
	}
	public void setAge2(String age){
		age2=age.substring(0, age.indexOf("ปี")).trim();		
	}
	
	public void setHN2(String hn){
		hn2=hn;		
	}
	public void setVN2(String vn){
		vn2=vn;	
	}
	public void setName2(String name){
		String name_check=name;
		if(name_check.equals("")){
			
		}else{
		if(name_check.length()>26){
			name_print2=name_check.substring(0,26);
		}else{
			name_print2=name_check;
		}
		}
	}
	public void setAge1(String age){
		age1=age.substring(0, age.indexOf("ปี")).trim();		
	}
	public void setDate(String date){
		this.date=date;
	}
	public void setClinicNameShort(String clinicname){
		this.clinicname=clinicname;
		
	}

	//
	public void showPrinter(){
		PrinterJob pj =  PrinterJob.getPrinterJob();
		
		bk = new Book();
		bk.append(this,pf,1);
		pj.setPageable(bk);

		
		if(pj.printDialog()){ 
			try{
				
				pj.print();  
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
	 
	}
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		 if (page > 0) {
	         return NO_SUCH_PAGE;
	    }
	    Graphics2D g2d = (Graphics2D)g;
	    g2d.translate(pf.getImageableX(), pf.getImageableY());
	    g2d.setFont(new Font("Cordia New",0,12));
	    int x_start=29;
	    int y_start=22;
	    int y_start1=84;
	    for(int i=0;i<5;i++){
	    	 
	    	g2d.drawString(clinicname+"** VN:"+vn1,x_start+(i*115),y_start);
		    g2d.drawString(name_print1,x_start+(i*115),y_start+12);
		    g2d.drawString("อายุ "+age1+" ปี  HN:"+hn1,x_start+(i*115),y_start+24);
		    
	    	 
	    }
	    for(int i=0;i<5;i++){
	      	g2d.drawString(clinicname+"** VN:"+vn2,x_start+(i*115),y_start1);
		    g2d.drawString(name_print2,x_start+(i*115),y_start1+12);
		    g2d.drawString("อายุ "+age2+" ปี  HN:"+hn2,x_start+(i*115),y_start1+24);

		    //System.out.println(an2+"--:--"+name_print2+"--*--"+hn2+"--#--"+age2);
	    }
	    return PAGE_EXISTS;
	}


}
