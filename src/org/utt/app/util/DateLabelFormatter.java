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

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private String datePattern = " dd MMMM yyyy";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }
}