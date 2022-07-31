package com.bbsim;

public enum BattingSplitType
{
	LHP("mlb_platoonSplits-tr_0"), 
	RHP("mlb_platoonSplits-tr_1"), 
	HOME("mlb_typeSplits-tr_0"), 
	AWAY("mlb_typeSplits-tr_1");
	
	final String tableRowId;
	
	public static final int PLATE_APP_IDX = 3;
	public static final int STRIKE_OUT_IDX = 12;
	public static final int WALK_IDX = 11;
	public static final int HITS_IDX = 6;
	public static final int DOUBLES_IDX = 7;
	public static final int TRIPLES_IDX = 8;
	public static final int HOMERS_IDX = 9;
	
	
	BattingSplitType(String rowId) {
		this.tableRowId = rowId;
	}
	
	public String getRowId() {
		return tableRowId;
	}
}
