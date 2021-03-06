package com.keithmackay.systemtool;

import com.keithmackay.systemtool.settings.Settings;
import com.keithmackay.systemtool.settings.SettingsManager;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.platform.win32.Wtsapi32;
import org.pmw.tinylog.Logger;

import java.util.Locale;

public abstract class WorkstationLockListener implements WindowProc {

	/**
	 * Instantiates a new win32 window systemtool.
	 */
	WorkstationLockListener() {
		// define new window class
		final WString windowClass = new WString("MyWindowClass");
		final HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");

		WNDCLASSEX wClass = new WNDCLASSEX();
		wClass.hInstance = hInst;
		wClass.lpfnWndProc = WorkstationLockListener.this;
		wClass.lpszClassName = windowClass;

		// register window class
		User32.INSTANCE.RegisterClassEx(wClass);
		getLastError("Register Class Segment");

		// create new window
		final HWND hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass, "'TimeTracker hidden helper window to catch Windows events", 0, 0, 0, 0, 0, null, null, hInst, null);

		getLastError("Creating Window");
		Logger.info("window successfully created! window hwnd: " + hWnd.getPointer().toString());

		Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, Wtsapi32.NOTIFY_FOR_THIS_SESSION);

		MSG msg = new MSG();
		while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
			User32.INSTANCE.TranslateMessage(msg);
			User32.INSTANCE.DispatchMessage(msg);
		}

		/// This code is to clean at the end. You can attach it to your custom
		/// application shutdown listener
		Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
		User32.INSTANCE.UnregisterClass(windowClass, hInst);
		User32.INSTANCE.DestroyWindow(hWnd);
		System.out.println("program exit!");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sun.jna.platform.win32.User32.WindowProc#callback(com.sun.jna.platform
	 * .win32.WinDef.HWND, int, com.sun.jna.platform.win32.WinDef.WPARAM,
	 * com.sun.jna.platform.win32.WinDef.LPARAM)
	 */
	public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
		switch (uMsg) {
			case WinUser.WM_DESTROY: {
				User32.INSTANCE.PostQuitMessage(0);
				return new LRESULT(0);
			}
			case WinUser.WM_SESSION_CHANGE: {
				this.onSessionChange(wParam, lParam);
				return new LRESULT(0);
			}
			default:
				return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
		}
	}

	/**
	 * Gets the last error.
	 *
	 * @return the last error
	 */
	private int getLastError(String message) {
		int rc = Kernel32.INSTANCE.GetLastError();
		if (rc != 0) {
			Logger.error(String.format(Locale.getDefault(), "%s - %d", message == null ? "WorkstationLockListener" : message, rc));
		}
		return rc;
	}

	public int getLastError() {
		return getLastError(null);
	}

	/**
	 * On session change.
	 *
	 * @param wParam the w param
	 * @param lParam the l param
	 */
	private void onSessionChange(WPARAM wParam, LPARAM lParam) {
		if (SettingsManager.getInstance().getBool(Settings.WORKSPACE_MONITOR_RUNNING, true)) {
			switch (wParam.intValue()) {
				case Wtsapi32.WTS_SESSION_LOCK: {
					this.onMachineLocked(lParam.intValue());
				}
				case Wtsapi32.WTS_SESSION_UNLOCK: {
					this.onMachineUnlocked(lParam.intValue());
				}
			}
		}
	}

	/**
	 * On machine locked.
	 *
	 * @param sessionId the session id
	 */
	protected abstract void onMachineLocked(int sessionId);

	/**
	 * On machine unlocked.
	 *
	 * @param sessionId the session id
	 */
	protected abstract void onMachineUnlocked(int sessionId);
}