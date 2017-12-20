package com.keithmackay.test.processes;

import org.pmw.tinylog.Logger;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BackgroundProcess {
	private String name;
	private long time;
	private boolean running, started = false;
	private Timer timer;

	protected BackgroundProcess(String name, long time) {
		this.name = name;
		this.time = time;
		this.running = false;
		this.timer = new Timer();
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
		if (this.started) this.running = true;
		else this.start();
	}

	/**
	 * Start the process
	 */
	public void start() {
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (running) doWork();
				else Logger.info("{} scheduled but turned off", name);
			}

		}, 0, this.time);
		this.running = true;
		this.started = true;
	}

	public void pause() {
		this.running = false;
	}

	public boolean isRunning() {
		return this.running;
	}


}
