package com.bbsim.state;

public abstract class ScreenState extends State
{
	
	
	public ScreenState() {
		
	}
	
	/**
	 * Override this method with the tasks the state should perform when
	 * it is started. This might be gathering data before it's drawn to the
	 * screen on update.
	 */
	public abstract void init(Object... params);
	
	/**
	 * Override this method with the tasks the state should perform when
	 * it is ended. This might be cleanup of some kind.
	 */
	public abstract void end();
	
	/**
	 * Override this method with the desired behavior on state update.
	 * State update will automatically be triggered after each input, or can
	 * be triggered as desired.
	 */
	public abstract void update();
	
	/**
	 * Override this method with the desired behavior for inputs from the user.
	 * For example, what should we do when the user types "back"?
	 * @param input String from scanner
	 */
	public abstract void handleInput(String input);
	
	
	
	protected void run() {
		while(isActive()) {
			update();
			String input = getManager().getScanner().nextLine();
			handleInput(input);
		}
	}
}
