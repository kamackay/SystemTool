package com.keithmackay.systemtool.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static com.keithmackay.systemtool.utils.Utils.getAppSettingsFolder;

public class SettingsManager {
	private static SettingsManager singleton = null;
	private Map<Settings, List<SettingChangeListener>> settingChangeListeners;

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
		this.settingChangeListeners = new HashMap<>();
		Arrays.stream(Settings.values()).forEach(setting -> this.settingChangeListeners.put(setting, new ArrayList<>()));
		File settingsFolder = getAppSettingsFolder();
		this.settingsFile = new File(Paths.get(settingsFolder.getAbsolutePath(), "settings.json").toString());
		if (!this.settingsFile.exists()) {
			try {
				boolean created = this.settingsFile.createNewFile();
				if (!created) Logger.error("Did not Create Settings File");
				/*
				else {
					try (FileWriter writer = new FileWriter(this.settingsFile)) {
						this.gson.toJson(this.settings, writer);
					}
				}//*/
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
		try (FileReader reader = new FileReader(this.settingsFile)) {
			Map<String, String> temp = this.gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, String>>() {
			}.getType());
			if (temp != null) this.settings = temp;
		} catch (FileNotFoundException e) {
			Logger.error(e, "Could not find settings file");
		} catch (IOException e) {
			Logger.error(e, "Error writing to settings file");
		}
	}

	private void invalidate(Settings setting) {
		this.changesPending = true;
		this.settingChangeListeners.get(setting).forEach(SettingChangeListener::onSettingChange);
	}

	public void setString(Settings name, String value) {
		String currentVal = this.settings.getOrDefault(name.toString(), null);
		this.settings.put(name.toString(), value);
		if (value != null && !value.equals(currentVal)) this.invalidate(name);
		Logger.info("Set \"{}\" to \"{}\"", name.toString(), value);
	}

	public void setBool(Settings name, boolean value) {
		this.setString(name, value ? "true" : "false");
	}

	public void setLong(Settings name, long value) {
		this.setString(name, String.valueOf(value));
	}

	/**
	 * Attempt to set a long value from a String
	 *
	 * @param name  The name of the setting to set
	 * @param value The string value to attempt to set to the Setting
	 * @return Whether or not the value was set correctly
	 */
	public boolean trySetLong(Settings name, String value) {
		try {
			this.setLong(name, Integer.valueOf(value));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void setDouble(Settings name, float value) {
		this.setString(name, String.valueOf(value));
	}

	public void setJsonObject(Settings name, Object object) {
		this.setString(name, gson.toJson(object));
	}

	public String getString(Settings name, String defaultVal) {
		String value = this.settings.get(name.toString());
		return value == null ? defaultVal : value;
	}

	public boolean getBool(Settings name, boolean defaultVal) {
		String str = this.getString(name, null);
		if (str == null) return defaultVal;
		return "true".equals(str);
	}

	public long getLong(Settings name, long defaultVal) {
		String str = this.getString(name, null);
		if (str == null) return defaultVal;
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			Logger.error(e, "Could not parse setting value {}", str);
			return defaultVal;
		}
	}

	public double getDouble(Settings name, double defaultVal) {
		String str = this.getString(name, null);
		if (str == null) return defaultVal;
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			Logger.error(e, "Could not parse setting value {}", str);
			return defaultVal;
		}
	}

	/**
	 * Will Default to null
	 *
	 * @param name The name of the JSON object to retrieve
	 * @param type The expected type
	 * @return The requested JSON object, or null
	 */
	public Object getJsonObject(Settings name, Class type) {
		String str = this.getString(name, null);
		if (str == null) return null;
		return gson.fromJson(str, type);
	}

	public void addSettingChangeListener(Settings setting, SettingChangeListener listener) {
		this.settingChangeListeners.get(setting).add(listener);
	}

	private void saveSettings() {
		String json = SettingsManager.this.gson.toJson(SettingsManager.this.settings);
		try (FileWriter writer = new FileWriter(SettingsManager.this.settingsFile, false)) {
			writer.write(json);
			Logger.info("Successfully wrote Settings - {}", json);
			this.changesPending = false;
		} catch (IOException e) {
			Logger.error(e, "Could not save settings data");
		}
	}
}
