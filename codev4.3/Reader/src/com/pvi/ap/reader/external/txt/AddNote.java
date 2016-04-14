package com.pvi.ap.reader.external.txt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.pvi.ap.reader.data.content.NotesInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
/**
 * 
 * @author Elvis
 *
 */
public class AddNote {
	private Context mCall = null;
	private SimpleDateFormat formatter = new SimpleDateFormat(
	"MM/dd/yyyy HH:mm:ss");
	
	public AddNote(Context call){
		this.mCall = call ;
	}
	
	public boolean addNoteInfo(Map<String, String> map) {
		String columns[] = new String[] { NotesInfo._ID, NotesInfo.NoteName };
		String where = NotesInfo.NoteName + "='" + (String) map.get("noteName")
				+ "'";
		Cursor cur = null;
		try {
			cur = mCall.getContentResolver().query(NotesInfo.CONTENT_URI,
					columns, where, null, null);
			if(cur != null && cur.moveToFirst())
				return false ;
			if(cur != null && cur.getCount() > 0) {
				return false;
			}
			ContentValues values = new ContentValues();
			values.put(NotesInfo.NoteName, (String) map.get("noteName"));
			values.put(NotesInfo.NoteText, (String) map.get("noteText"));
			values.put(NotesInfo.CreateDate, formatter.format(new Date(System
					.currentTimeMillis())));
			mCall.getContentResolver().insert(NotesInfo.CONTENT_URI, values);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}

}
