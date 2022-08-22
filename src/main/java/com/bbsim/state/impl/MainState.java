package com.bbsim.state.impl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;

import com.bbsim.App;
import com.bbsim.Constants;
import com.bbsim.CurrentGameData;
import com.bbsim.Parlay;
import com.bbsim.Simularity;
import com.bbsim.UpdateTask;
import com.bbsim.state.ScreenState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MainState extends ScreenState
{
	private static final String PARLAY_SAVE_FILE = "/Programming/GameWorkspace/BaseballSimulator/data/parlays.json";
	
	
	List<Parlay> parlays;
	List<CurrentGameData> gameData;
	Gson gson;
	
	private boolean showLostParlays = true;
	
	private boolean isUpdating;
	private Timer timer;
	private UpdateTask updateTask;
	
	private float simBrierScore = App.UNSET_INT;
	
	public MainState() {
		WebDriverManager.chromedriver().setup();
		gson = new Gson();
		parlays = new ArrayList<>();
		gameData = new ArrayList<>();
		
		updateTask = new UpdateTask(this, gameData);
		
		loadParlaysFromFile();
		timer = new Timer();
		timer.scheduleAtFixedRate(updateTask, 0, 90000);
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
		if (simBrierScore != App.UNSET_INT) {
			System.out.println(App.centerText(Constants.ANSI_YELLOW + "Simularity Score: " + App.percentage(simBrierScore) + Constants.ANSI_RESET, false, true));
			System.out.println(App.TABLE_HORIZ_LINE);
		}
		if (parlays.size() == 0) {
			System.out.println(App.centerText("no active parlays :(", false, true));
			System.out.println(App.centerText("type 'add' to create a parlay", false, true));
		} else {
			for (int i = 0; i < parlays.size(); i++) {
				parlays.get(i).printStatus(gameData.get(i), showLostParlays);
			}
		}
		
		System.out.println(App.leftJustifyText("options:", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'add'", 10) + "- add a parlay", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'remove'", 10) + "- delete a parlay", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'update'", 10) + "- force update live data", 2, true));
		System.out.println(App.leftJustifyText(StringUtils.leftPad("'analyze'", 10) + "- run brier score analysis of simulation", 2, true));
		if (showLostParlays) {	
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'hideLost'", 10) + "- hide lost parlays", 2, true));
		} else {
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'showLost'", 10) + "- show lost parlays", 2, true));
		}
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
			if (this.getManager().askConfirmation("Are you sure you want to remove all parlays?")) {
				clearAllParlays();
			}
			return;
		} else if ("hideLost".equals(input)) {
			hideLostParlays();
			return;
		} else if ("showLost".equals(input)) {
			showLostParlays();
			return;
		} else if ("remove".equals(input)) {
			this.clearConsole();
			System.out.println("Select a parlay: ");
			for (int i = 0; i < parlays.size(); i++) {
				System.out.println("    " + i + ".) " + parlays.get(i).getGame().toString());
			}
			int sel = this.getManager().getIntegerInput(0, parlays.size(), true);
			if (sel != App.UNSET_INT) {
				parlays.remove(sel);
				gameData.remove(sel);
			}
			saveParlaysToFile();
			return;
		} else if ("update".equals(input)) {
			updateAllGames();
			return;
		} else if ("analyze".equals(input)) {
			System.out.println("analyzing...");
			float res = Simularity.analyze();
			this.simBrierScore = res;
		} else if ("quit".equals(input)) {
			timer.cancel();
			getManager().killManager();
		} else {
			System.out.println("unknown input!");
		}
	}
	
	public void updateAllGames() {
		if (isUpdating) {
			return;
		}
		isUpdating = true;
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		options.setLogLevel(ChromeDriverLogLevel.OFF);
		
		WebDriver driver = new ChromeDriver(options);

		for (CurrentGameData cg : gameData) {
			cg.update(driver);
		}
		
		driver.close();
		driver.quit();
		isUpdating = false;
	}
	
	private void clearAllParlays() {
		Simularity.cleanup();
		parlays.clear();
		gameData.clear();
		saveParlaysToFile();
	}
	
	private void hideLostParlays() {
		this.showLostParlays = false;
	}
	
	private void showLostParlays() {
		this.showLostParlays = true;
	}
	
	private void saveParlaysToFile() {
		try {
			String data = gson.toJson(parlays);
			FileOutputStream outputStream = new FileOutputStream(PARLAY_SAVE_FILE);
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
			BufferedReader reader = new BufferedReader(new FileReader(PARLAY_SAVE_FILE));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			Type parlayListType = new TypeToken<ArrayList<Parlay>>() {}.getType();
			parlays = gson.fromJson(sb.toString(), parlayListType);
			
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
