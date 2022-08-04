package com.bbsim.state.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.bbsim.App;
import com.bbsim.CurrentGameData;
import com.bbsim.Parlay;
import com.bbsim.state.ScreenState;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MainState extends ScreenState
{
	private static final String ENDPOINT_PT1 = "https://baseballsavant.mlb.com/gamefeed?gamePk=";
	private static final String ENDPOINT_PT2 = "&hf=boxScore";
	private static final String MATCHUP = "matchup-";
	
	
	List<Parlay> parlays;
	List<CurrentGameData> gameData;
	
	public MainState() {
		parlays = new ArrayList<>();
		gameData = new ArrayList<>();
	}
	
	@Override
	public void init(Object... params) {
		clearConsole();
		if (params.length == 0) {
		
		} else if (params.length == 1) {
			if (!(params[0] instanceof Parlay)) {
				System.out.println("MainState parameter must be a parlay");
			} else {
				Parlay parlay = (Parlay) params[0];
				parlays.add(parlay);
				//gameData.add(new CurrentGameData(parlay.getGame()));
			}
		} else {
			System.out.println("Wrong number of params given to MainState");
		}
		
		gameData.add(new CurrentGameData(null));
	}

	@Override
	public void end() {
		
	}

	@Override
	public void update() {
		clearConsole();
		System.out.println(App.TABLE_END_LINE);
		System.out.println(App.centerText("TODAY'S PARLAYS", false, true));
		System.out.println(App.TABLE_HORIZ_LINE);
		for (int i = 0; i < parlays.size(); i++) {
			parlays.get(i).printStatus(gameData.get(i));
		}
		System.out.println(App.TABLE_END_LINE);
	}

	@Override
	public void handleInput(String input) {
		if ("add".equals(input)){
			this.changeState(App.GAME_PICKER_STATE);
		} else if ("clearAll".equals(input)) {
			
		} else if ("update".equals(input)) {
			updateAllGames();
		} else {
			System.out.println("unknown input!");
		}
	}
	
	private void updateAllGames() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		WebDriver driver = new ChromeDriver(options);

		for (CurrentGameData cg : gameData) {
			cg.update(driver);
			
			System.out.println(cg.awayPitcherStats.strikeouts);
		}
		
		driver.close();
	}

}
