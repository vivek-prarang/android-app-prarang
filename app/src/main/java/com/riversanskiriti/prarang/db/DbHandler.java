
package com.riversanskiriti.prarang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.riversanskiriti.prarang.adapter.PostItem;
import com.riversanskiriti.utils.BaseUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arpit on 11/05/16.
 */
public class DbHandler extends SQLiteOpenHelper {

    private String TAG = "PrarangDB";
    Context mContext;
    private String mSql = null;
    static final int dbVersion = 1;
    static final String dbName = "db_gulak";
    private BaseUtils baseUtils;

    //Tables
    static final String tbl_MST_Gulak = "tbl_MST_Gulak";
    private static DbHandler sInstance;

    public static synchronized DbHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DbHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public DbHandler(Context context) {
        super(context, dbName, null, dbVersion);
        baseUtils = new BaseUtils(context);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Tables
        mSql = "CREATE TABLE IF NOT EXISTS " + tbl_MST_Gulak + "(id TEXT PRIMARY KEY, name TEXT, imageUrl TEXT, postUrl TEXT, description TEXT, liked TEXT, imageList TEXT, tagList TEXT,tagJSONArray TEXT, timestamp TEXT, totalLike TEXT, totalComment TEXT, type TEXT)";
        db.execSQL(mSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tbl_MST_Gulak);
        onCreate(db);
    }

    //Read and Write Methods
    public void saveIntoGulak(PostItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String imageList = "";
            for (int i = 0; i < item.getImageList().size(); i++) {
                if (i == 0) {
                    imageList = item.getImageList().get(i);
                } else {
                    imageList = imageList + "##" + item.getImageList().get(i);
                }
            }
            String tagList = "";
            for (int i = 0; i < item.getTagList().size(); i++) {
                if (i == 0) {
                    tagList = item.getTagList().get(i);
                } else {
                    tagList = tagList + "##" + item.getTagList().get(i);
                }
            }
            ContentValues v = new ContentValues();
            v.put("id", item.getId());
            v.put("name", item.getName());
            v.put("imageUrl", item.getImageUrl());
            v.put("postUrl", item.getPostUrl());
            v.put("description", item.getDescription());
            v.put("liked", item.getLiked());
            v.put("imageList", imageList);
            v.put("tagList", tagList);
            v.put("tagJSONArray", item.getTagJsonArrayString());
            v.put("timestamp", System.currentTimeMillis() + "");
            v.put("totalLike", item.getTotalLike());
            v.put("totalComment", item.getTotalComment());
            v.put("timestamp", item.getDateTime());
            v.put("type", item.getPostType() + "");
            db.insert(tbl_MST_Gulak, null, v);

        } catch (Exception ee) {
            Log.e("saveIntoGulak", ee.getMessage());
        }
        db.close();
    }

    public List<PostItem> getGulak(String id) {
        ArrayList<PostItem> mList = new ArrayList<PostItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        String whereClause = "";
        mSql = "SELECT id, name, imageUrl,postUrl, description, liked, imageList, tagList,tagJSONArray,totalLike,totalComment,timestamp FROM '" + tbl_MST_Gulak + "'";
        if (id != null) {
            whereClause = " WHERE id='" + id + "'";
            mSql = mSql + whereClause + " ORDER BY timestamp";
        } else {
            mSql = mSql + " ORDER BY timestamp";
//            String cityFilter = baseUtils.getStringData("cityFilter");
//            String regionFilter = baseUtils.getStringData("regionFilter");
//            String countryFilter = baseUtils.getStringData("countryFilter");
//            if (cityFilter != null) {
//                whereClause = " WHERE type='1' AND name='" + cityFilter + "'";
//            } else {
//                whereClause = " WHERE type='1'";
//                cityFilter = "";
//            }
//            String cityQuery = mSql + whereClause;
//            if (regionFilter != null) {
//                whereClause = " WHERE type='2' AND name='" + regionFilter + "'";
//            } else {
//                whereClause = " WHERE type='2'";
//                regionFilter = "";
//            }
//            String regionQuery = mSql + whereClause;
//            if (countryFilter != null) {
//                whereClause = " WHERE type='3' AND name='" + countryFilter + "'";
//            } else {
//                countryFilter = "";
//                whereClause = " WHERE type='3'";
//            }
//            String countryQuery = mSql + whereClause;
//            String generalQuery = mSql + " WHERE type='0' AND (name='" + cityFilter + "' OR name='" + regionFilter + "' OR name='" + countryFilter + "')";
//            mSql = cityQuery + " UNION " + regionQuery + " UNION " + countryQuery + " UNION " + generalQuery + " ORDER BY timestamp";
        }


        Cursor c = db.rawQuery(mSql, null);
        while (c.moveToNext()) {
            PostItem mItem = new PostItem();
            mItem.setId(c.getString(0));
            mItem.setName(c.getString(1));
            mItem.setImageUrl(c.getString(2));
            mItem.setPostUrl(c.getString(3));
            mItem.setDescription(c.getString(4));
            mItem.setLiked(c.getString(5));
            mItem.setImageList(new ArrayList<String>(Arrays.asList(c.getString(6).split("##"))));
            mItem.setTagList(new ArrayList<String>(Arrays.asList(c.getString(7).split("##"))));
            mItem.setTagJsonArrayString(c.getString(8));
      /*      try {
                mItem.setTagList(new JSONArray(c.getString(8)));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            mItem.setTotalLike(Integer.parseInt(c.getString(9)));
            mItem.setTotalComment(Integer.parseInt(c.getString(10)));
            mItem.setDateTime(c.getString(11));
            mList.add(mItem);
        }
        c.close();
        db.close();
        return mList;
    }

    public void deleteFromGulak(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tbl_MST_Gulak, "id" + "=" + id, null);
    }

    public void updateGulalItem(PostItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE '" + tbl_MST_Gulak + "' SET liked='" + item.getLiked() + "', totalLike='" + item.getTotalLike() + "', totalComment='" + item.getTotalComment() + "' WHERE id='" + item.getId() + "'");
        } catch (Exception e) {

        }
        db.close();

    }
}