package de.feu.propra.util;

import java.util.Locale;

public class SystemInfo {
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	public static String getJavaVersionInfoString() {
		return getInfoString("Java Version:", getJavaVersion());
	}

	private static String getInfoString(String label, String value) {
		String fmt = "%-28s%s";
		return String.format(fmt, label, value);
	}

	public static String getLanguage() {
		var loc = Locale.getDefault();
		return loc.getDisplayLanguage();
	}

	public static String getCurrentWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public static String getCurrentWorkingDirectoryInfoString() {
		return getInfoString("Current Working Directory:", getCurrentWorkingDirectory());
	}
}
