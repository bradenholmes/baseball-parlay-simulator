package com.bbsim.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StateManager
{
	private Scanner scanner;
	
	private Map<String, State> states;
	private State activeState;
	
	public StateManager() {
		scanner = new Scanner(System.in);
		states = new HashMap<>();
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
		changeState(initialStateId, params);
	}
	
	protected void changeState(String nextStateId, Object... params) {
		if (activeState != null) {
			activeState.end();
		}
		
		State nextState = states.getOrDefault(nextStateId, null);
		if (nextState != null) {
			activeState = nextState;
			activeState.start(params);
			activeState.run();
		} else {
			System.err.println("ERROR: Unknown state id of " + nextStateId);
		}
		
		
	}
	
	protected Scanner getScanner() {
		return scanner;
	}
	
    protected void clearConsole() {
    	try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
