package com.google.code.fontcreator;

import java.io.File;

import android.content.Context;

public class FontUtils {
	public static String[] filenameArray;

	public FontUtils() {
		filenameArray = null;

	}

	public static String[] getFonts(Context context) {
		filenameArray = context.fileList();
		return filenameArray;
	}

	public static boolean hasFont(String name, Context context) {
		getFonts(context);

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