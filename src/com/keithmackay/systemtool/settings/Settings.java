package com.keithmackay.systemtool.settings;

public enum Settings {
	VERSION("SETTINGS.VERSION"), CLIPBOARD_HISTORY("SETTINGS.CLIPBOARD_HISTORY"), DOWNLOAD_MANAGER_RUNNING("SETTINGS.DOWNLOAD_MANAGER_RUNNING"), WORKSPACE_MONITOR_RUNNING("SETTINGS.WORKSPACE_MONITOR_RUNNING"), MAX_CLIPBOARD_ELEMENTS("SETTINGS.MAX_CLIPBOARD_ELEMENTS");

	private final String name;

	private Settings(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
