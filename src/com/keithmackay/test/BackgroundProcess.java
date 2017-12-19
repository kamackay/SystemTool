package com.keithmackay.test;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BackgroundProcess {
	private String name;
	private long time;
	private boolean running;
	private Timer timer;
	
	public BackgroundProcess(String name, long time) {
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
		this.start();
	}
	
	/**
	 * Start the process
	 */
	public void start() {
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				doWork();				
			}
			
		}, 0, this.time);
		this.running = true;
	}
	
	public boolean isRunning() {
		return this.running;
	}

}
