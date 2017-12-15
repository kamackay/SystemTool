package com.keithmackay.test;

import java.util.Locale;

public class VersionData {
	private int major, minor, revision;

	public VersionData(int major, int minor, int revision) {
		this.major = major;
		this.minor = minor;
		this.revision = revision;
	}

	public int getMajor() {
		return this.major;
	}

	public int getMinor() {
		return this.minor;
	}

	public int getRevision() {
		return this.revision;
	}

	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%d.%d.%d", this.major, this.minor, this.revision);
	}
}
