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

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import com.alee.laf.label.WebLabel;
import com.alee.laf.text.WebTextField;

public class Setup {
	public static final double CM_PER_INCH = 0.393700787d;
	public static final double INCH_PER_CM = 2.545d;
	public static final double INCH_PER_MM = 25.45d;
	/**
	 * Converts the given pixels to cm's based on the supplied DPI
	 *
	 * @param pixels
	 * @param dpi
	 * @return
	 */
	public static double pixelsToCms(double pixels, double dpi) {
	    return inchesToCms(pixels / dpi);
	}
	/**
	 * Converts the given cm's to pixels based on the supplied DPI
	 *
	 * @param cms
	 * @param dpi
	 * @return
	 */
	public static double cmsToPixel(double cms, double dpi) {
	    return cmToInches(cms) * dpi;
	}
	/**
	 * Converts the given cm's to inches
	 *
	 * @param cms
	 * @return
	 */
	public static double cmToInches(double cms) {
	    return cms * CM_PER_INCH;
	}
	/**
	 * Converts the given inches to cm's
	 *
	 * @param inch
	 * @return
	 */
	public static double inchesToCms(double inch) {
	    return inch * INCH_PER_CM;
	}
	public static void setLocale(){
        String[] parts = Prop.getProperty("app.locale").split("_");
        Locale.setDefault(new Locale(parts[0],parts[1]));
    }
    public static Color getColor() {
        return new Color(229,229,229);
    }
    public static WebTextField getTextField(int fwidth) {
        WebTextField tf = new WebTextField();
        tf.setFont(new Font("Tahoma", Font.PLAIN, fwidth));
        tf.setText("");
        return tf;
    }
    public static WebLabel getLabel(String name,int fwidth,int type ,int align) {
        WebLabel label=new WebLabel(name,align);
        label.setFont(new Font("Tahoma", type, fwidth));
        return label;
    }
    
    //caldendar
    public static String getMonthShortThaiName(String month){
        String currentMonth=month;
        String currentMonthThaiName="";
        if(currentMonth.equals("01")){
            currentMonthThaiName="ม.ค.";
        }
        else if(currentMonth.equals("02")){
            currentMonthThaiName="ก.พ.";
        }
        else if(currentMonth.equals("03")){
            currentMonthThaiName="มี.ค.";
        }
        else if(currentMonth.equals("04")){
            currentMonthThaiName="เม.ย.";
        }
        else if(currentMonth.equals("05")){
            currentMonthThaiName="พ.ค.";
        }
        else if(currentMonth.equals("06")){
            currentMonthThaiName="มิ.ย.";
        }
        else if(currentMonth.equals("07")){
            currentMonthThaiName="ก.ค.";
        }
        else if(currentMonth.equals("08")){
            currentMonthThaiName="ส.ค.";
        }
        else if(currentMonth.equals("09")){
            currentMonthThaiName="ก.ย.";
        }
        else if(currentMonth.equals("10")){
            currentMonthThaiName="ต.ค.";
        }
        else if(currentMonth.equals("11")){
            currentMonthThaiName="พ.ย.";
        }
        else if(currentMonth.equals("12")){
            currentMonthThaiName="ธ.ค.";
        }
        return currentMonthThaiName;
    }
    
}
