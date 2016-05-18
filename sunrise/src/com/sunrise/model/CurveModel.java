package com.sunrise.model;

import java.io.Serializable;
import java.util.Date;


public class CurveModel implements Serializable{

	private static final long serialVersionUID = 1L;
	private String[] mCurveTypes = { "有功", "无功", "电压", "电流","功率因数", "需量" };
	private String[] mCurveTypesAlias = {"P", "Q", "U", "I", "PF", "MD"};
 	private String[] mLoops;
	private String[] mLoopsId;
	private String[] mCurveTypesId;
	private String[] mCurveNames;
	
	private int mCurveTypesItem;
	private int mLoopItem;
	private String mTitle;
	private String mPath;
	private Date mDate;
	private int mDateTag;
	private int[] mSelectCurves;
	private boolean mSelect;
	
	public CurveModel() {
		mDate = new Date();
	}
	
	public String[] getCurveTypes() {
		return mCurveTypes;
	}
	
	public String[] getCurveTypesAlias() {
		return mCurveTypesAlias;
	}

	public String[] getLoops() {
		return mLoops;
	}
	
	public void setLoops(String[] loops) {
		mLoops = loops;
	}

	public int getCurveTypesItem() {
		return mCurveTypesItem;
	}

	public void setCurveTypesItem(int curveTypesItem) {
		mCurveTypesItem = curveTypesItem;
	}

	public int getLoopItem() {
		return mLoopItem;
	}

	public void setLoopItem(int loopItem) {
		mLoopItem = loopItem;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		mPath = path;
	}

	public String[] getLoopsId() {
		return mLoopsId;
	}

	public void setLoopsId(String[] loopsId) {
		mLoopsId = loopsId;
	}

	public String[] getCurveTypesId() {
		return mCurveTypesId;
	}

	public void setCurveTypesId(String[] curveTypesId) {
		mCurveTypesId = curveTypesId;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public int getDateTag() {
		return mDateTag;
	}

	public void setDateTag(int dateTag) {
		mDateTag = dateTag;
	}

	public String[] getCurveNames() {
		return mCurveNames;
	}

	public void setCurveNames(String[] curveNames) {
		mCurveNames = curveNames;
	}

	public int[] getSelectCurves() {
		return mSelectCurves;
	}

	public void setSelectCurves(int[] selectCurves) {
		mSelectCurves = selectCurves;
	}

	public boolean isSelect() {
		return mSelect;
	}

	public void setSelect(boolean select) {
		mSelect = select;
	}

}
