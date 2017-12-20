package com.keithmackay.test.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsManager {
	private static SettingsManager singleton = null;

	public static SettingsManager getInstance() {
		if (singleton == null) singleton = new SettingsManager();
		return singleton;
	}

	private Map<String, String> settings;
	private File settingsFile;
	private Gson gson;
	private boolean changesPending = false;

	private SettingsManager() {
		settings = new HashMap<>();
		this.gson = new GsonBuilder().create();
		String appDataFolder = System.getenv("APPDATA");
		File settingsFolder = new File(Paths.get(appDataFolder, "SystemTool").toString());
		if (!settingsFolder.exists()) {
			boolean created = settingsFolder.mkdir();
			if (!created) {
				Logger.error("Could not create path");
			}
		}
		this.settingsFile = new File(Paths.get(settingsFolder.getAbsolutePath(), "settings.json").toString());
		if (!this.settingsFile.exists()) {
			try {
				boolean created = this.settingsFile.createNewFile();
				if (!created) Logger.error("Did not Create Settings File");
				else {
					this.gson.toJson(this.settings, new FileWriter(this.settingsFile));
				}
			} catch (IOException e) {
				Logger.error(e, "Could not create Settings File");
			}
		}
		this.initSettings();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (changesPending) {
					SettingsManager.this.saveSettings();
				}
			}
		}, 0, 1000);
	}

	private void initSettings() {
		try {
			Map<String, String> temp = this.gson.fromJson(new JsonReader(new FileReader(this.settingsFile)), new TypeToken<Map<String, String>>() {
			}.getType());
			if (temp != null) this.settings = temp;
		} catch (FileNotFoundException e) {
			Logger.error(e, "Could not find settings file");
		}
	}

	private void invalidate() {
		this.changesPending = true;
	}

	public void setString(String name, String value) {
		this.settings.put(name, value);
		this.invalidate();
	}

	public void setBool(String name, boolean value) {
		this.setString(name, value ? "true" : "false");
	}

	public void setInt(String name, int value) {
		this.setString(name, String.valueOf(value));
	}

	public void setDouble(String name, float value) {
		this.setString(name, String.valueOf(value));
	}

	public String getString(String name, String defaultVal) {
		String value = this.settings.get(name);
		return value == null ? defaultVal : value;
	}

	public boolean getBool(String name, boolean defaultVal) {
		String str = this.getString(name, null);
		if (str == null) return defaultVal;
		return "true".equals(str);
	}

	private void saveSettings() {
		String json = SettingsManager.this.gson.toJson(SettingsManager.this.settings);
		try (FileWriter writer = new FileWriter(SettingsManager.this.settingsFile, false)) {
			writer.write(json);
			Logger.debug("Successfully wrote Settings");
		} catch (IOException e) {
			Logger.error(e, "Could not save settings data");
		}
	}
}
