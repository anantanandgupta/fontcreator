package com.google.code.fontcreator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.util.LangUtils;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.util.Xml.Encoding;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Font.PlatformId;
import com.google.typography.font.sfntly.Font.UnicodeEncodingId;
import com.google.typography.font.sfntly.Font.WindowsEncodingId;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.NameTable;
import com.google.typography.font.sfntly.table.core.CMapTable.CMapFilter;
import com.google.typography.font.sfntly.table.core.CMapTable.CMapId;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.Glyph.Builder;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;

public class FontManager {
	// the actual font object
	private Font mFont;
	// Font factory
	private FontFactory mFontFactory;
	// Font builder
	private Context context;
	
	private String fileName;
	
	private boolean isDefault;

	/**
	 * Constructor
	 */
	public FontManager(Context context) {
		this.context = context;
		mFontFactory = FontFactory.getInstance();
		initDefaultFont();
		fileName = "";
		isDefault = true;
	}
	
	public FontManager(Context context, String filename) {
	  this.context = context;
    mFontFactory = FontFactory.getInstance();
    initFont(filename);
    isDefault = false;
	}

	private void initFont(String filename) {
	  try {
      mFont = mFontFactory.loadFonts(context.openFileInput(filename))[0];
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("failed to load font " + filename);
    }
	}
	private void initDefaultFont() {
		try {
			mFont = mFontFactory.loadFonts(context.getAssets().open(
					"fonts/arial.ttf"))[0];

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to load font");
		}

	}

	public int getGlyphId(String glyphCharacter) {
		// Get the cMap table from the font
		CMapTable cMapTable = mFont.getTable(Tag.cmap);
		Iterator<CMap> iter = cMapTable.iterator(new CMapFilter() {

			@Override
			public boolean accept(CMapId cmapId) {
				return cmapId.platformId() == PlatformId.Windows.value()
						&& cmapId.encodingId() == 1;
			}
		});
		CMap cMap = iter.next();
		// get glyph Id for specified character
		int glyphId = cMap.glyphId(glyphCharacter.codePointAt(0));
		// get glyphLength and Offset
		return glyphId;
	}

	public Glyph getGlyph(String glyphCharacter) {
		// get the glyph table
		GlyphTable glyphTable = mFont.getTable(Tag.glyf);
		// get the loca table to get the offsets of each individual glyph
		LocaTable locaTable = mFont.getTable(Tag.loca);
		int glyphId = getGlyphId(glyphCharacter);
		int glyphLength = locaTable.glyphLength(glyphId);
		int glyphOffset = locaTable.glyphOffset(glyphId);

		return glyphTable.glyph(glyphOffset, glyphLength);
	}

	public FontManager changeGlyph(String glyphCharacter, Glyph newGlyph, String nameOfFont)
			throws IOException {
		// Iterate through each of the glyphs in the arraylist
		// and insert each of the new glyphs into the font.
		// get the glyph table
		Font.Builder mFontBuilder;
		if (isDefault)
			mFontBuilder = mFontFactory.loadFontsForBuilding(context.getAssets()
				.open("fonts/arial.ttf"))[0];
		else 
		  mFontBuilder = mFontFactory.loadFontsForBuilding(context.openFileInput(fileName))[0];

		LocaTable.Builder locaTableBuilder = (LocaTable.Builder) mFontBuilder
				.getTableBuilder(Tag.loca);
		GlyphTable.Builder glyphTableBuilder = (GlyphTable.Builder) mFontBuilder
				.getTableBuilder(Tag.glyf);
		
		List<Integer> originalLocas = locaTableBuilder.locaList();
		glyphTableBuilder.setLoca(originalLocas);

		ReadableFontData glyphData = glyphTableBuilder.data();
		WritableFontData glyphBytes = WritableFontData
				.createWritableFontData(0);
		
		glyphData.copyTo(glyphBytes);

		/*
		 * int numLocas = locaTableBuilder.numLocas(); int lastLoca =
		 * locaTableBuilder.loca(numLocas - 1); int numGlyphs =
		 * locaTableBuilder.numGlyphs(); int firstGlyphOffset =
		 * locaTableBuilder.glyphOffset(0); int firstGlyphLength =
		 * locaTableBuilder.glyphLength(0); int glyphTableSize =
		 * glyphTableBuilder.header().length();
		 */
		int glyphId = getGlyphId(glyphCharacter);

		List<? extends Glyph.Builder<? extends Glyph>> glyphBuilders = glyphTableBuilder
				.glyphBuilders();
		Builder<? extends Glyph> glyphBuilder = glyphBuilders.get(glyphId);
		glyphBuilder.setData(newGlyph.readFontData());
		List<Integer> locaList = glyphTableBuilder.generateLocaList();
		locaTableBuilder.setLocaList(locaList);

		Log.v("GlyphBuilderLength: ", glyphBuilders.size()+"");
		Font font = mFontBuilder.build();

		try {
			mFontFactory.serializeFont(font, context.openFileOutput(
					nameOfFont, Context.MODE_WORLD_READABLE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new FontManager(context, nameOfFont);
	}

	public Glyph makeGlyph(Glyph originalGlyph, List<Stroke> contourList,
			int baselineHeight, int baselineWidth, int screenWidth) {
		WritableFontData data = WritableFontData.createWritableFontData(0);
		int numContours = contourList.size();
		int offset = 0;
		

//		HorizontalHeaderTable hheaTable = mFont.getTable(Tag.hhea);
		

		float scaleFactor = 1300.0f/(screenWidth - baselineWidth);
		
		// write the number of contours as int16
		String tag = "Font";
		Log.v(tag, "num contours: " + numContours);
		byte[] b = intToInt16(numContours);
		data.writeBytes(0, b);
		offset = offset + b.length;
		int xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE, xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
		for (Stroke s : contourList) {
			for (Point p : s.getSegments()) {
				int xcoor = (int)(scaleFactor*(p.x - baselineWidth));
				int ycoor = (int)(scaleFactor*(baselineHeight - p.y));
//				Log.v("X:", xcoor+"");
//				Log.v("Y: ", ycoor+"");
						

				if (xcoor > xMax) {
					xMax = xcoor;
				}
				if (xcoor < xMin) {
					xMin = xcoor;
				}
				if (ycoor < yMin) {
					yMin = ycoor;
				}
				if (ycoor > yMax) {
					yMax = ycoor;
				}
			}
		}

		Log.v(tag, "xMin: " + xMin);
		// write xmin, ymin, xmax, ymax
		b = intToInt16(xMin);
		data.writeBytes(offset, b);
		offset = offset + b.length;
		Log.v(tag, "yMin: " + yMin);
		b = intToInt16(yMin);
		data.writeBytes(offset, b);
		offset = offset + b.length;
		Log.v(tag, "xMax: " + xMax);
		b = intToInt16(xMax);
		data.writeBytes(offset, b);
		offset = offset + b.length;
		Log.v(tag, "yMax: " + yMax);
		b = intToInt16(yMax);
		data.writeBytes(offset, b);
		offset = offset + b.length;

		// store indices of the end points of each contour
		int pointIndex = 0;
		for (Stroke s : contourList) {
			int endIndex = pointIndex + s.getSegments().size() - 1;
			b = intToInt16(endIndex);
			pointIndex = endIndex+1;
			data.writeBytes(offset, b);
			offset = offset + b.length;
			Log.v(tag, "end index: " + endIndex);
		}
		/*
		 * int instrSize = originalGlyph.instructionSize(); ReadableFontData
		 * instructions = originalGlyph.instructions(); b =
		 * intToInt16(instrSize); data.writeBytes(offset, b); offset = offset +
		 * b.length; b = new byte[instructions.length()];
		 * instructions.readBytes(0, b, 0, instructions.length());
		 * data.writeBytes(offset, b); offset = offset + b.length;
		 */
		b = intToInt16(0);
		data.writeBytes(offset, b);
		offset = offset + b.length;

		byte onCurve = (byte) 1, offCurve = (byte) 0;
		boolean isOnCurve = true;
		for (Stroke s : contourList) {
			for (Point p : s.getSegments()) {
				if (isOnCurve) {
					data.writeByte(offset, onCurve);
				} else {
					data.writeByte(offset, offCurve);
				}
				offset++;
				isOnCurve = !isOnCurve;
			}
			isOnCurve = true;
		}

		int last = 0;
		for (Stroke s : contourList) {
			for (Point p : s.getSegments()) {
				b = intToInt16((int)(scaleFactor*(p.x - baselineWidth)) - last);
				Log.v("Xrel:", ((int)(scaleFactor*(p.x - baselineWidth)) - last)+"");
				data.writeBytes(offset, b);
				offset = offset + b.length;
				last = (int)(scaleFactor*(p.x - baselineWidth));
			}
		}
		last = 0;
		
		for (Stroke s : contourList) {
			for (Point p : s.getSegments()) {
				b = intToInt16((int)(scaleFactor*(baselineHeight - p.y)) - last);
				Log.v("Yrel:", ((int)(scaleFactor*(baselineHeight - p.y)) - last)+"");
				data.writeBytes(offset, b);
				offset = offset + b.length;
				last = (int)(scaleFactor*(baselineHeight - p.y));
			}
		}

		if (offset % 4 != 0) {
			data.writePadding(offset, 4 - offset % 4);
			offset += 4 - offset % 4;
		}
		return new MySimpleGlyph(data);
	}

	public static final byte[] intToFWord(int value) {
		if (value >= 0)
			return new byte[] { (byte) (0x00), (byte) (0x00),
					(byte) (value >>> 24), (byte) (value >> 16 & 0xff),
					(byte) (value >> 8 & 0xff), (byte) (value & 0xff) };

		else {
			return new byte[] { (byte) (0xFF), (byte) (0xFF),
					(byte) (value >>> 24), (byte) (value >> 16 & 0xff),
					(byte) (value >> 8 & 0xff), (byte) (value & 0xff) };
		}
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >> 16 & 0xff),
				(byte) (value >> 8 & 0xff), (byte) (value & 0xff) };
	}

	public static final byte[] intToInt16(int value) {
		return new byte[] { (byte) (value >> 8 & 0xff), (byte) (value & 0xff) };
	}
}
