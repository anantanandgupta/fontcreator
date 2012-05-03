package com.google.code.fontcreator;

import java.io.File;

import android.content.Context;

public class FontUtils {

	private FontUtils() {
	}

	public static String[] getFonts(Context context) {
		return context.fileList();
	}

	public static boolean hasFont(String name, Context context) {
		String[] filenameArray = getFonts(context);

		if (filenameArray != null) {
			for (int i = 0; i < filenameArray.length; i++) {
				if (filenameArray[i].equalsIgnoreCase(name)) {
					return true;
				}
			}
		}

		return false;
	}

	public static File getFont(String name, Context context) {
		if (hasFont(name, context)) {
			return context.getFileStreamPath(name);
		}
		return null;
	}

}