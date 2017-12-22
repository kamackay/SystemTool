package com.keithmackay.systemtool.processes;

import com.keithmackay.systemtool.settings.Settings;
import com.keithmackay.systemtool.settings.SettingsManager;
import org.pmw.tinylog.Logger;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BackgroundProcess {
	private String name;
	private Settings runningSetting;
	private long time;
	private boolean started = false;
	private Timer timer;
	private SettingsManager settingsManager;

	protected BackgroundProcess(String name, Settings runningSetting, long time) {
		this.name = name;
		this.runningSetting = runningSetting;
		this.time = time;
		this.timer = new Timer();
		this.settingsManager = SettingsManager.getInstance();
	}

	@Override
	public String toString() {
		return this.name;
	}

	public abstract void doWork();

	/**
	 * I am indecisive on names
	 */
	public void run() {
		if (this.started) this.setRunning(true);
		else this.start();
	}

	/**
	 * Start the process
	 */
	public void start() {
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (isRunning()) doWork();
				else Logger.info("{} scheduled but turned off", name);
			}

		}, 0, this.time);
		this.started = true;
	}

	public void pause() {
		this.setRunning(false);
	}

	public boolean isRunning() {
		return this.settingsManager.getBool(this.runningSetting, true);
	}

	private void setRunning(boolean running) {
		this.settingsManager.setBool(this.runningSetting, running);
	}


}
