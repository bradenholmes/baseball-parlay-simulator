package com.bbsim.state.impl;

import com.bbsim.App;
import com.bbsim.Bet;
import com.bbsim.state.ScreenState;

public class BetValueState extends ScreenState
{
	Bet bet;
	@Override
	public void init(Object... params) {
		clearConsole();
		if (params.length != 1) {
			System.err.println("ERROR: Wrong number of arguments passed to BetValueState init method!");
			return;
		}
		
		if (!(params[0] instanceof Bet)) {
			System.err.println("ERROR: BetValueState argument must be a Bet object");
			return;
		}
		this.bet = (Bet)params[0];
		
		System.out.println("Set a value: ");
	}

	@Override
	public void end()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInput(String input)
	{
		try {
			float value = Float.parseFloat(input);
			bet.setValue(value);
			
			this.changeState(App.PARLAY_BUILDER_STATE, bet);
			
		} catch (Exception e) {
			System.out.println("Please enter a float value!");
		}
		// TODO Auto-generated method stub
		
	}

}
