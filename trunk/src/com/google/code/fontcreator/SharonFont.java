package com.google.code.fontcreator;

import java.io.IOException;
import java.util.Iterator;

import android.content.Context;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;

public class font {
	//the actual font object
	private Font mFont;
	//Font factory
	private FontFactory mFontFactory;
	private Context context;
	/**
	 * Constructor
	 */
	public font(){
		mFontFactory = FontFactory.getInstance();
		initDefaultFont();
	}
	
	private void initDefaultFont(){
		try {
			mFont = mFontFactory.loadFonts(context.getAssets().open("fonts/arial.ttf"))[0];
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to load font");
		}
				
	}
	
	private Glyph getGlyph(String glyphCharacter){
		//get the glyph table
		GlyphTable glyphTable = mFont.getTable(Tag.glyf);
		//get the loca table to get the offsets of each individual glyph
		LocaTable locaTable = mFont.getTable(Tag.loca);
		//Get the cMap table from the font
		CMapTable cMapTable =  mFont.getTable(Tag.cmap);
		Iterator<CMap> iter = cMapTable.iterator();
		CMap cMap = iter.next();
		//get glyph Id for specified character
		int glyphId = cMap.glyphId(glyphCharacter.codePointAt(0));
		//get glyphLength and Offset
		int glyphLength = locaTable.glyphLength(glyphId);
		int glyphOffset = locaTable.glyphOffset(glyphId);
	
		return glyphTable.glyph(glyphOffset, glyphLength);
	}
	
}
