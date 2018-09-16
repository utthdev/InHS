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
package org.utt.app.common;

import java.util.Observable;

public class ObjectData extends Observable{
    String visit_date="",vn="",an="",hn="",ptcid="",clinic_code_pt="",pt_name="",ptLabel="",rightcode="",memo="",memo1="",pt_age="",pt_bed="",admtime="",ward="";

    //
    public void setPtVisitdate(String visit_date ){
        this.visit_date=visit_date;
        setChanged();
        notifyObservers();
    }
    public String GetPtVisitdate(){
        return visit_date;
    }
    public void setPtVN(String vn ){
        this.vn=vn;
        setChanged();
        notifyObservers();
    }
    public String GetPtVN(){
        return vn;
    }
    public void setPtAN(String an ){
        this.an=an;
        setChanged();
        notifyObservers();
    }
    public String GetPtAN(){
        return an;
    }
    public void setPtHN(String hn ){
        this.hn=hn;
        setChanged();
        notifyObservers();
    }
    public String GetPtHN(){
        return hn;
    }
    public void setPtCliniccode(String clinic_code_pt ){
        this.clinic_code_pt=clinic_code_pt;
        setChanged();
        notifyObservers();
    }
    public String GetPtCliniccode(){
        return clinic_code_pt;
    }
    public void setPtName(String pt_name ){
        this.pt_name=pt_name;
        setChanged();
        notifyObservers();
    }
    public String GetPtName(){
        return pt_name;
    }
    public void setPtLabel(String ptLabel ){
        this.ptLabel=ptLabel;
        setChanged();
        notifyObservers();
    }
    public String GetPtLabel(){
        return ptLabel;
    }
    public void setRightCode(String rightcode ){
        this.rightcode=rightcode;
        setChanged();
        notifyObservers();
    }
    public String GetRightCode(){
        return rightcode;
    }
    public void setPtCID(String ptcid ){
        this.ptcid=ptcid;
        setChanged();
        notifyObservers();
    }
    public String GetPtCID(){
        return ptcid;
    }
    public void setMemo(String memo ){
        this.memo=memo;
        setChanged();
        notifyObservers();
    }
    public String GetMemo(){
        return memo;
    }
    public void setMemo1(String memo1 ){
        this.memo1=memo1;
        setChanged();
        notifyObservers();
    }
    public String GetMemo1(){
        return memo1;
    }
    public void setPtAge(String pt_age ){
        this.pt_age=pt_age;
        setChanged();
        notifyObservers();
    }
    public String GetPtAge(){
        return pt_age;
    }
    public void setPtBed(String pt_bed ){
        this.pt_bed=pt_bed;
        setChanged();
        notifyObservers();
    }
    public String GetPtBed(){
        return pt_bed;
    }
    public void setPtWard(String ward){
        this.ward=ward;
        setChanged();
        notifyObservers();
    }
    public String GetPtWard(){
        return ward;
    }
    public void setAdmtime(String admtime ){
        this.admtime=admtime;
        setChanged();
        notifyObservers();
    }
    public String GetAdmtime(){
        return admtime;
    }
}
