package com.bbsim.state;

public abstract class State
{
	private StateManager manager;
	private boolean isActive;
	
	public State() {
		this.isActive = false;
	}
	
	protected void setStateManager(StateManager manager) {
		this.manager = manager;
	}
	
	
	protected void start(Object... params) {
		this.setActive();
		init(params);
	}
	
	public abstract void init(Object... params);
	protected abstract void run();
	public abstract void end();
	
	protected StateManager getManager() {
		return manager;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	protected void setActive() {
		this.isActive = true;
	}
	
	protected void setInactive() {
		this.isActive = false;
	}
	
	public void changeState(String nextStateId, Object... params) {
		this.setInactive();
		manager.changeState(nextStateId, params);
	}
	
	public void clearConsole() {
		manager.clearConsole();
	}
	
	
}
