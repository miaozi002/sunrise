package com.sunrise.model;

public class Level3Data {
	private String id;
	private String code;
	private String name;
	private String NFC;
	private String rundate;
	private String medev;
	private String isused;
	private String isfault;
	private String vlid;
	private String type;
	private String manufactor;
	private String model;
	private String capacity;


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

    public String getRundate() {
        return rundate;
    }

    public void setRundate(String rundate) {
        this.rundate = rundate;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
