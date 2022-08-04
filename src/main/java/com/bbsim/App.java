package com.bbsim;


import com.bbsim.state.StateManager;
import com.bbsim.state.impl.BetClassState;
import com.bbsim.state.impl.BetSubjectState;
import com.bbsim.state.impl.BetTypeState;
import com.bbsim.state.impl.BetValueState;
import com.bbsim.state.impl.GamePickerState;
import com.bbsim.state.impl.MainState;
import com.bbsim.state.impl.ParlayBuilderState;
import com.bbsim.state.impl.ParlayPickerState;
import com.bbsim.state.impl.SimulationState;


public class App 
{
	public static final int LINE_WIDTH = 90;
	public static final String TABLE_END_LINE =   "------------------------------------------------------------------------------------------";
	public static final String TABLE_HORIZ_LINE = "|----------------------------------------------------------------------------------------|";
	public static final String TABLE_EMPTY_LINE = "|                                                                                        |";
	
	//Screen states
	public static final String MAIN_STATE = "main_state";
	public static final String GAME_PICKER_STATE = "game_picker_state";
	public static final String PARLAY_BUILDER_STATE = "parlay_builder_state";
	public static final String PARLAY_PICKER_STATE = "parlay_picker_state";
	public static final String BET_CLASS_STATE = "bet_class_state";
	public static final String BET_TYPE_STATE = "bet_type_state";
	public static final String BET_SUBJECT_STATE = "bet_subject_state";
	public static final String BET_VALUE_STATE = "bet_value_state";
	
	//Function states
	public static final String SIMULATION_STATE = "simulation_state";

    public static void main( String[] args ) {
    	start();
    }
    
    public static void start() {
    	StateManager manager = new StateManager();
    	manager.addState(MAIN_STATE, new MainState());
    	manager.addState(GAME_PICKER_STATE, new GamePickerState(ApiQuery.getAllGames()));
    	manager.addState(PARLAY_BUILDER_STATE, new ParlayBuilderState());
    	manager.addState(PARLAY_PICKER_STATE, new ParlayPickerState());
    	manager.addState(BET_CLASS_STATE, new BetClassState());
    	manager.addState(BET_TYPE_STATE, new BetTypeState());
    	manager.addState(BET_SUBJECT_STATE, new BetSubjectState());
    	manager.addState(BET_VALUE_STATE, new BetValueState());
    	
    	manager.addState(SIMULATION_STATE, new SimulationState());
    	
    	manager.activateManager(MAIN_STATE);
    }
    
    public static String percentage(float rate) {
    	return String.format("%.2f%%", 100 * rate);
    }
    
    public static String decimal(float val) {
    	return String.format("%.2f", val);
    }
    
    public static String centerText(String text, boolean fillDashes, boolean tableWalls) {
    	StringBuilder sb = new StringBuilder();
    	int len = text.length();
    	int space = LINE_WIDTH - len;
    	int leftPad = space / 2;
    	int rightPad = space - leftPad;
    	
    	leftPad--;
    	rightPad--;
    	
    	String endChar;
    	if (tableWalls) {
    		endChar = "|";
    	} else if (fillDashes) {
    		endChar = "-";
    	} else {
    		endChar = " ";
    	}
    	
    	String spaceChar;
    	if (fillDashes) {
    		spaceChar = "-";
    	} else {
    		spaceChar = " ";
    	}
    	
    	sb.append(endChar);
    	for (int i = 0; i < leftPad; i++) {
    		sb.append(spaceChar);
    	}
    	sb.append(text);
    	for (int i = 0; i < rightPad; i++) {
    		sb.append(spaceChar);
    	}
    	sb.append(endChar);
    	return sb.toString();
    }
    
    public static String leftJustifyText(String text, int indentSize, boolean tableWalls) {
    	StringBuilder sb = new StringBuilder();
    	int len = text.length();
    	int space = LINE_WIDTH - len - indentSize - 2;
    	
    	
    	String endChar;
    	if (tableWalls) {
    		endChar = "|";
    	} else {
    		endChar = " ";
    	}
    	
    	sb.append(endChar);
    	for (int i = 0; i < indentSize; i++) {
    		sb.append(" ");
    	}
    	sb.append(text);
    	for (int i = 0; i < space; i++) {
    		sb.append(" ");
    	}
    	sb.append(endChar);
    	return sb.toString();
    }
    
    public static String rightJustifyText(String text, int indentSize, boolean tableWalls) {
    	StringBuilder sb = new StringBuilder();
    	int len = text.length();
    	int space = LINE_WIDTH - len - indentSize - 2;
    	
    	
    	String endChar;
    	if (tableWalls) {
    		endChar = "|";
    	} else {
    		endChar = " ";
    	}
    	
    	sb.append(endChar);
    	for (int i = 0; i < space; i++) {
    		sb.append(" ");
    	}
    	sb.append(text);
    	for (int i = 0; i < indentSize; i++) {
    		sb.append(" ");
    	}
    	sb.append(endChar);
    	return sb.toString();
    }
    
    public static void clearConsole() {
    	try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
