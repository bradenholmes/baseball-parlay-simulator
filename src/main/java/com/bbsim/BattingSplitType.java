package com.bbsim;

public enum BattingSplitType
{
	LHP("date-platoon-mlb", "vs Left"), 
	RHP("date-platoon-mlb", "vs Right"), 
	HOME("type-splits-mlb", "Home Games"), 
	AWAY("type-splits-mlb", "Away Games");
	

	public static final int ROW_SPECIFIER_IDX = 2;
	public static final int PLATE_APP_IDX = 3;
	public static final int STRIKE_OUT_IDX = 12;
	public static final int WALK_IDX = 11;
	public static final int HITS_IDX = 6;
	public static final int DOUBLES_IDX = 7;
	public static final int TRIPLES_IDX = 8;
	public static final int HOMERS_IDX = 9;
	
	
	final String tableId;
	final String rowSpecifier;
	
	BattingSplitType(String tableId, String rowSpecifier) {
		this.tableId = tableId;
		this.rowSpecifier = rowSpecifier;
	}
	
	public String getTableId() {
		return tableId;
	}
	
	public String getRowSpecifier() {
		return rowSpecifier;
	}
}
