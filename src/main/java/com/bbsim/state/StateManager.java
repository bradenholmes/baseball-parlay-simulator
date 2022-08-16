package com.bbsim.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.App;

public class StateManager
{
	private static StateManager instance = null;
	
	private Scanner scanner;
	
	private boolean running = false;
	private Map<String, State> states;
	private State activeState;
	
	private boolean didStateChange = false;
	
	private StateManager() {
		scanner = new Scanner(System.in);
		states = new HashMap<>();
	}
	
	public static StateManager get() {
		if (instance == null) {
			instance = new StateManager();
		}
		
		return instance;
	}
	
	/**
	 * Add a state to the state manager.
	 * @param stateId Unique ID for the state
	 * @param state actual state instance to store
	 */
	public void addState(String stateId, State state) {
		state.setStateManager(this);
		states.put(stateId, state);
	}
	
	/**
	 * Activate the manager on a specific state with parameters
	 * @param initialStateId
	 * @param params
	 */
	public void activateManager(String initialStateId, Object... params) {
		running = true;
		changeState(initialStateId, params);
		
		while(running) {
			update();
		}
	}
	
	public void killManager() {
		activeState.end();
		activeState.setInactive();
		running = false;
	}
	
	protected void changeState(String nextStateId, Object... params) {
		
		if (activeState != null) {
			activeState.end();
		}
		
		State nextState = states.getOrDefault(nextStateId, null);
		if (nextState != null) {
			activeState = nextState;
			activeState.start(params);
			didStateChange = true;
		} else {
			System.err.println("ERROR: Unknown state id of " + nextStateId);
		}
		
		
	}
	
	private void update() {
		if (didStateChange) {
			activeState.run();
		}
	}
	
	public Scanner getScanner() {
		return scanner;
	}
	
    protected void clearConsole() {
    	try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public boolean askConfirmation(String questionString) {
		System.out.println(questionString + " (y/n)");
		String answer = scanner.nextLine();
		if (StringUtils.equalsIgnoreCase("y", answer)) {
			return true;
		} else {
			return false;
		}
    }
    
    public int getIntegerInput(int min, int max, boolean optional) {
    	int value = 0;
    	boolean gotten = false;
    	while(!gotten) {
    		String in = scanner.nextLine();
    		try {
    			value = handleIntegerInput(in, min, max, optional);
    			gotten = true;
    		} catch (Exception e) {
    			System.out.println("Please enter an integer between " + min + " and " + (max - 1));
    		}
    	}
    	
    	return value;
    }
    
    public List<Integer> getManyIntegerInput(int minVal, int maxVal, boolean optional) {
    	List<Integer> values = new ArrayList<>();
    	
    	boolean gotten = false;
    	while(!gotten) {
    		String in = scanner.nextLine();
    		try {
    			values = handleManyIntegerInput(in, minVal, maxVal, optional);
    			gotten = true;
    		} catch (Exception e) {
    			System.out.println("Please enter integers between " + minVal + " and " + (maxVal - 1) + ", separated by spaces. e.g '2 4 6 8'");
    			if (optional) {
    				System.out.println("Or type 'back'");
    			}
    		}
    	}
    	
    	return values;
    }
    
    public int handleIntegerInput(String input, int min, int max, boolean optional) throws Exception {
    	if (StringUtils.equalsIgnoreCase("help", input)) {
    		System.out.print("Type an integer between " + min + " and " + (max - 1) + " and press enter");
    		if (optional) {
    			System.out.println(", or type 'back'");
    		}
    		return handleIntegerInput(scanner.nextLine(), min, max, optional);
    	}
		if (optional && isValidOptionalString(input)) {
			return App.UNSET_INT;
		}
		try {
			int v = Integer.parseInt(input);
			if (v >= min && v < max) {
				return v;
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			throw e;
		}
    }
    
    public List<Integer> handleManyIntegerInput(String input, int minVal, int maxVal, boolean optional) throws Exception {
    	if (StringUtils.equalsIgnoreCase("help", input)) {
			System.out.println("Enter integers between " + minVal + " and " + (maxVal - 1) + ", separated by spaces. e.g '2 4 6 8'");
			if (optional) {
				System.out.println("Or type 'back'");
			}
    		return handleManyIntegerInput(scanner.nextLine(), minVal, maxVal, optional);
    	}
    	
    	if (optional && isValidOptionalString(input) ) {
    		return null;
    	}
    	
    	List<Integer> values = new ArrayList<>();
		String[] vals = StringUtils.split(input, ' ');
		for (int i = 0; i < vals.length; i++) {
			int v = Integer.parseInt(vals[i]);
			if (v >= minVal && v < maxVal) {
				values.add(v);
			} else {
				throw new Exception();
			}
		}
		
		return values;
    }
    
    private boolean isValidOptionalString(String input) {
    	if (StringUtils.isEmpty(input)) {
    		return true;
    	} else if (StringUtils.equalsIgnoreCase("back", input)) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
