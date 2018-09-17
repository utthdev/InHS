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
