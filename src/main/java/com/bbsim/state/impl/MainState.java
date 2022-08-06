package com.bbsim.state.impl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.bbsim.App;
import com.bbsim.CurrentGameData;
import com.bbsim.Parlay;
import com.bbsim.state.ScreenState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MainState extends ScreenState
{
	private static final String saveFile = "/Programming/GameWorkspace/BaseballSimulator/parlays.json";
	
	List<Parlay> parlays;
	List<CurrentGameData> gameData;
	Gson gson;
	
	public MainState() {
		WebDriverManager.chromedriver().setup();
		gson = new Gson();
		parlays = new ArrayList<>();
		gameData = new ArrayList<>();
		
		loadParlaysFromFile();
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
				
				if (!parlays.contains(parlay)) {
					parlays.add(parlay);
					gameData.add(new CurrentGameData(parlay.getGame()));
				}
				
				saveParlaysToFile();
				updateAllGames();
			}
		} else {
			System.out.println("Wrong number of params given to MainState");
		}
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
		if (parlays.size() == 0) {
			System.out.println(App.centerText("no active parlays :(", false, true));
			System.out.println(App.centerText("type 'add' to create a parlay", false, true));
		} else {
			for (int i = 0; i < parlays.size(); i++) {
				parlays.get(i).printStatus(gameData.get(i));
			}
		}
		
		System.out.println(App.leftJustifyText("options:", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'add'", 10) + "- add a parlay", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'edit'", 10) + "- edit a parlay", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'update'", 10) + "- force update live data", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'clearAll'", 10) + "- delete all parlays", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'quit'", 10) + "- exit program", 2, true));
		System.out.println(App.TABLE_END_LINE);
	}

	@Override
	public void handleInput(String input) {
		if ("add".equals(input)){
			this.changeState(App.GAME_PICKER_STATE);
			return;
		} else if ("clearAll".equals(input)) {
			System.out.println("Are you sure you want to remove all?? (y/n)");
			String answer = this.getManager().getScanner().nextLine();
			if ("y".equals(answer)) {
				clearAllParlays();
			}
			return;
		} else if ("clearLost".equals(input)) {
			System.out.println("Are you sure you want to remove lost parlays? (y/n)");
			String answer = this.getManager().getScanner().nextLine();
			if ("y".equals(answer)) {
				clearLostParlays();
			}
		} else if ("edit".equals(input)) {
			this.changeState(App.PARLAY_PICKER_STATE, parlays);
			return;
		} else if ("update".equals(input)) {
			updateAllGames();
			return;
		} else if ("quit".equals(input)) {
			getManager().killManager();
		} else {
			System.out.println("unknown input!");
		}
	}
	
	private void updateAllGames() {
		this.clearConsole();
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		WebDriver driver = new ChromeDriver(options);

		for (CurrentGameData cg : gameData) {
			cg.update(driver);
		}
		
		driver.close();
		driver.quit();
	}
	
	private void clearAllParlays() {
		parlays.clear();
		gameData.clear();
		saveParlaysToFile();
	}
	
	private void clearLostParlays() {
		List<Parlay> toDelete = new ArrayList<>();
		List<CurrentGameData> cgdToDelete = new ArrayList<>();
		for (int i = 0; i < parlays.size(); i++) {
			if (parlays.get(i).isDead()) {
				toDelete.add(parlays.get(i));
				cgdToDelete.add(gameData.get(i));
				System.out.println("would delete: " + i);
			}
		}
		
		for (Parlay p : toDelete) {
			parlays.remove(p);
		}
		for (CurrentGameData cgd : cgdToDelete) {
			gameData.remove(cgd);
		}
		
		saveParlaysToFile();
	}
	
	private void saveParlaysToFile() {
		try {
			String data = gson.toJson(parlays);
			FileOutputStream outputStream = new FileOutputStream(saveFile);
			byte[] strBytes = data.getBytes();
			outputStream.write(strBytes);
			outputStream.close();
		} catch (Exception e) {
			System.err.println("ERROR: Failed to save file");
			e.printStackTrace();
		}
	}
	
	private void loadParlaysFromFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(saveFile));
			String data = reader.readLine();
			Type parlayListType = new TypeToken<ArrayList<Parlay>>() {}.getType();
			parlays = gson.fromJson(data, parlayListType);
			
			for (Parlay p : parlays) {
				p.initialize();
				gameData.add(new CurrentGameData(p.getGame()));
			}

			reader.close();
			
			
		} catch (Exception e) {
			System.err.println("ERROR: Failed to load file");
			e.printStackTrace();
		}
	}

}
