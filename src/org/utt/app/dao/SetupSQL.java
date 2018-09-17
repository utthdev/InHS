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


public class SetupSQL {
    public static final String sql_checkip="select computername,version from incomputer where ipuser=? and status='1' ";
    public static final String sql_check_user = "SELECT name,position,type,clinic_code,app_code,username,specialty,report_code from inaccount WHERE username = ? AND password = ? and status='1'";
    public static final String init_z515 = "select hn from utth_patient_memo where memo_type='0001' and  memo='Z515'";
    public static final String queryward="select  ward_name,ward_code from ward_ipd where status='1' order by ward_code";

    public static String getDataIPD(String ptStatus, String sql_user) {
        return "select an,firstname,lastname,hn,usedrightcode,outdatetime,BirthDateTime,sex,maritalstatus,initialnamecode,bedno,admdatetime,referfromhospital from in_view_ipd  where   "+ptStatus+"   and suffix='0' "+sql_user+" order by bedno ";
    }
    public static String getDataAdmit(String date) {
        return "select admmaster.an,admmaster.hn,admbed.outdatetime,admbed.bedno,admmaster.admdatetime from admmaster,admbed  where admmaster.an=admbed.an and admmaster.admdatetime between '"+date+" 00:00:00' and '"+date+" 23:59:59' order by admmaster.admdatetime  ";

    }
    public static String getListDataIPD(String ptStatus, String sql_user) {
        return "select admmaster.an,admmaster.hn,admbed.outdatetime,admbed.bedno,admmaster.admdatetime from admmaster,admbed  where admmaster.an=admbed.an  "+ptStatus+"  "+sql_user+" order by admbed.bedno  ";
    }
    public static String getName1(String hn) {
        return "select  BirthDateTime,sex,maritalstatus,initialnamecode from patient_info where hn='"+hn.trim()+"'";
    }
    public static String getName2(String hn) {
        return "select firstname, lastname from patient_name where  suffix='0'  and hn='"+hn.trim()+"'";
    }
    public static String getDOB(String hn) {
	    return "select  BirthDateTime from patient_info where hn='"+hn.trim()+"'";
	}
	public static String getRight(String an) {
	    return "select  usedrightcode from admmaster where an='"+an.trim()+"'";
	}
	public static String getCID(String hn) {
	    return "select  ref from patient_ref where  reftype='01'  and hn='"+hn.trim()+"'";
	}
	public static final String memo = "select memo from view_MEDICINE_ALLERGIC where hn=? and suffix='8'";
	
	public static final String initclinic = "select app_code,clinic_code,clinic_name from clinic_name order by app_code";
	public static final String initDose = "select code,thainame from sysconfig where ctrlcode=?";
	public static final String initDoseeng = "select code,englishname from sysconfig where ctrlcode=?";
	public static final String formdrug=" select clinic,page,drug from formopd where type='1'";
	 
	public static String getClinicScan(String visitdate) {
        return "select clinic  from  in_view_pt_opd where VN=? and visitdate='"+visitdate+"' and reftype='01' and suffix='0'";
    }
	public static final String getClinicName = "select clinic_code,clinic_name  from clinic_name where app_code=?";

}