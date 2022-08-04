package com.bbsim.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StateManager
{
	private Scanner scanner;
	
	private boolean running = false;
	private Map<String, State> states;
	private State activeState;
	
	private boolean didStateChange = false;
	
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
}
