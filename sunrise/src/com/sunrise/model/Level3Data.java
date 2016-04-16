package com.sunrise.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Level3Data {
    private String tbl;
    private String stid;
    private String id;
    private String vlid;
    private String code;
    private String name;
    private String NFC;
    private String manufactor;
    private String facCode;
    private String installdate;
    private String putdate;
    private String faultdate;
    private String updatedate;
    private String repairdate;
    private String testdate;
    private String makedate;
    private String remark;
    private String capacity;
    private String devtype;
    private String maintbl;
    private String maindev;
    private String medev;
    private String isused;
    private String isfault;
    private String define1;
    private String define2;
    private String define3;

    public JsonObject asJsonObj() {
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(this);
        return element.getAsJsonObject();
    }

    public List<String> valueList() {
        JsonObject jsonObject = asJsonObj();
        List<String> valueList = new ArrayList<String>();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            valueList.add(entry.getValue().getAsString());
        }
        return valueList;
    }

    public List<String> keyList() {
        JsonObject jsonObject = asJsonObj();
        List<String> keyList = new ArrayList<String>();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            keyList.add(entry.getKey());
        }
        return keyList;
    }

    public String getTbl() {
        return tbl;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getFacCode() {
        return facCode;
    }

    public void setFacCode(String facCode) {
        this.facCode = facCode;
    }

    public String getInstalldate() {
        return installdate;
    }

    public void setInstalldate(String installdate) {
        this.installdate = installdate;
    }

    public String getPutdate() {
        return putdate;
    }

    public void setPutdate(String putdate) {
        this.putdate = putdate;
    }

    public String getFaultdate() {
        return faultdate;
    }

    public void setFaultdate(String faultdate) {
        this.faultdate = faultdate;
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate;
    }

    public String getRepairdate() {
        return repairdate;
    }

    public void setRepairdate(String repairdate) {
        this.repairdate = repairdate;
    }

    public String getTestdate() {
        return testdate;
    }

    public void setTestdate(String testdate) {
        this.testdate = testdate;
    }

    public String getMakedate() {
        return makedate;
    }

    public void setMakedate(String makedate) {
        this.makedate = makedate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getMaintbl() {
        return maintbl;
    }

    public void setMaintbl(String maintbl) {
        this.maintbl = maintbl;
    }

    public String getMaindev() {
        return maindev;
    }

    public void setMaindev(String maindev) {
        this.maindev = maindev;
    }

    public String getDefine1() {
        return define1;
    }

    public void setDefine1(String define1) {
        this.define1 = define1;
    }

    public String getDefine2() {
        return define2;
    }

    public void setDefine2(String define2) {
        this.define2 = define2;
    }

    public String getDefine3() {
        return define3;
    }

    public void setDefine3(String define3) {
        this.define3 = define3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNFC() {
        return NFC;
    }

    public void setNFC(String nFC) {
        NFC = nFC;
    }

    public String getMedev() {
        return medev;
    }

    public void setMedev(String medev) {
        this.medev = medev;
    }

    public String getIsused() {
        return isused;
    }

    public void setIsused(String isused) {
        this.isused = isused;
    }

    public String getIsfault() {
        return isfault;
    }

    public void setIsfault(String isfault) {
        this.isfault = isfault;
    }

    public String getVlid() {
        return vlid;
    }

    public void setVlid(String vlid) {
        this.vlid = vlid;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
