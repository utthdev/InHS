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
package org.utt.app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.utt.app.util.Prop;

public class DBmanager {
    static String mysql_driver = Prop.getProperty("mysql.db.driver");
    static String mysql_url = Prop.getProperty("mysql.db.url");
    static String mysql11_url = Prop.getProperty("mysql11.db.url");
    static String mysqljhos_url = Prop.getProperty("mysqljhos.db.url");

    static String mssql_driver = Prop.getProperty("mssql.db.driver");
    static String mssql_url = Prop.getProperty("mssql.db.url");

    static String h2_driver = Prop.getProperty("h2.db.driver");
    static String h2_url = Prop.getProperty("h2.db.url");


    public DBmanager(){

    }

    public static Connection getConnMySql(){
        Connection conn=null;
        try {
            Class.forName(mysql_driver);
            conn = DriverManager.getConnection(mysql_url);
        } catch (ClassNotFoundException   e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return conn;
    }

    public static Connection getConnMSSql(){
        Connection conn=null;
        try {
            Class.forName(mssql_driver);
            conn = DriverManager.getConnection(mssql_url);
        } catch (ClassNotFoundException   e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }
   
    public static Connection getConnH2(){
        Connection conn=null;
        try {
            Class.forName(h2_driver);
            conn = DriverManager.getConnection(h2_url);
        } catch (ClassNotFoundException   e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}

