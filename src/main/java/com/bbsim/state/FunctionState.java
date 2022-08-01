package com.bbsim.state;

public abstract class FunctionState extends State
{
	
	public class FunctionResult {
		String nextStateId;
		Object[] params;
		
		public FunctionResult(String nextId, Object... params) {
			this.nextStateId = nextId;
			this.params = params;
		}
	}
	
	public FunctionState() {
		
	}
	
	public abstract void init(Object... params);
	public abstract void end();
	
	/**
	 * Override this method with the desired behavior of this state. Method should return a FunctionResult object, 
	 * which contains the next state id to travel to and the parameters it should be given.
	 * @return FunctionResult object
	 */
	public abstract FunctionResult function();

	
	protected void run() {
		FunctionResult result = function();
		this.changeState(result.nextStateId, result.params);
	}
}
