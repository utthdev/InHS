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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Prop {
    static Properties properties;
    public static Properties getInstance() {
        InputStream input = null;
        if (properties == null) {
            properties = new Properties();
            try {
                input=Prop.class.getClassLoader().getResourceAsStream("app.properties");
                properties.load(input);
            } catch (IOException e) {
                System.exit(0);
            }
        }
        return properties;
    }
    public static String getProperty(String key) {
        return Prop.getInstance().getProperty(key);
    }
    public static void setProperty(String key,String value) {
        Prop.getInstance().setProperty(key,value);
    }
    public static void init() {
        Prop.getInstance();
    }
}
