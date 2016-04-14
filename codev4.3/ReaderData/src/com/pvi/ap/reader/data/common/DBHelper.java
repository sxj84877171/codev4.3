package com.pvi.ap.reader.data.common;
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.util.Log;  
  
/** 
 * ���ݿ���������� 
 *  
 * @author daguangspecial@gmail.com 
 *  
 */  
public class DBHelper {  
    private static final String TAG = "DBDemo_DBHelper";// ���Ա�ǩ  
  
    private static final String DATABASE_NAME = "dbdemo.db";// ���ݿ���  
    SQLiteDatabase db;  
    Context context;//Ӧ�û���������   Activity ��������  
  
    public DBHelper(Context _context) {  
        context = _context;  
        //�������ݿ�  
           
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null);  
        CreateTable();  
        Log.v(TAG, "db path=" + db.getPath());  
    }  
  
    /** 
     * ���� 
     * ���� ���ִ�Сд�� 
     * ����ʲô�������ͣ� 
     * SQLite 3  
     *  TEXT    �ı� 
        NUMERIC ��ֵ 
        INTEGER ���� 
        REAL    С�� 
        NONE    ������ 
     * ��ѯ�ɷ���select ? 
     */  
    public void CreateTable() {  
        try {  
            db.execSQL("CREATE TABLE t_log (TIME TEXT,TAG TEXT,MSG TEXT);");  
            Log.v(TAG, "Create Table t_log ok");  
        } catch (Exception e) {  
            Log.v(TAG, "Create Table t_log err,table exists.");  
        }  
    }  

//	�������� 
//	@param time
//	@param tag  
//	@param msg 
//	@return 
//       
    public boolean savelog(String time,String tag,String msg){  
        String sql="";  
        try{  
            sql="insert into t_log (TIME,TAG,MSG) values('"+time+"','"+tag+"','"+msg+"')";  
            db.execSQL(sql);  
            Log.v(TAG,"insert Table t_log ok");  
            return true;  
            
        }catch(Exception e){  
            Log.v(TAG,"insert Table t_log err ,sql: "+sql);  
            return false;  
        }  
    }  
    /** 
     * ��ѯ���м�¼ 
     *  
     * @return Cursor ָ������¼��ָ�룬������JDBC �� ResultSet 
     */  
//    public Cursor loadAll(){  
//          
//        Cursor cur=db.query("t_log", new String[]{"_ID","NAME"}, null,null, null, null, null);  
//          
//        return cur;  
//    }  
      public void close(){  
        db.close();  
    }  
}  