package com.bbsim.state.impl;

import com.bbsim.App;
import com.bbsim.state.ScreenState;

public class OtherState extends ScreenState
{

	@Override
	public void init(Object... params) {
		System.out.println("Start of other state");
	}

	@Override
	public void end() {
		System.out.println("End of other state");
	}

	@Override
	public void update() {
		//clearConsole();
		System.out.println("Update other state");
	}

	@Override
	public void handleInput(String input) {
		if ("back".equals(input)) {
			this.changeState(App.MAIN_STATE);
		} else {
			System.out.println("unknown input!");
		}
	}

}
