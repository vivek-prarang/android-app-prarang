package com.riversanskiriti.prarang.adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class PostItem implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private String geoCode;
    private String liked = "false";
    private ArrayList<String> imageList;
    private ArrayList<String> tagList;
    private ArrayList<TagItem> tagItemList;
    private String postUrl, dateTime;
    private boolean saved = false;
    private String tagJsonArrayString;
    private int totalLike = 0, totalComment = 0, type = 0, postType = 0;

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public String getTagJsonArrayString() {
        return tagJsonArrayString;
    }

    public void setTagJsonArrayString(String tagJsonArrayString) {
        this.tagJsonArrayString = tagJsonArrayString;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public ArrayList<TagItem> getTagItemList() {
        return tagItemList;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getImageUrl() {
        if (imageUrl != null) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = "http://" + imageUrl;
            }
        }
        return imageUrl;
    }

    public boolean isVideoUrl() {
        String prarang_in = "prarang.in"; // present in image url
        if (imageUrl != null) {
            if (!imageUrl.contains(prarang_in)) {
                return true;
            }
        }
        return false;
    }

//    https://img.youtube.com/vi/CsVJoCKc9rA/mqdefault.jpg
//    https://img.youtube.com/vi/CsVJoCKc9rA/0.jpg
//    https://img.youtube.com/vi/CsVJoCKc9rA/maxresdefault.jpg
//    public String getVideoThumbnailUrl() {
//        if (imageUrl != null) {
//            if (isVideoUrl()) {
//                String thumbnailUrl = "https://img.youtube.com/vi/" + getVideoId() + "/mqdefault.jpg";
//                return thumbnailUrl;
//            }
//        }
//        return "";
//    }

    public String getVideoId() {
        if (imageUrl != null) {
            if (isVideoUrl()) {
                String arr[] = imageUrl.split("/");
                if (arr.length >= 2) {
                    String videoId = arr[arr.length - 2];
                    videoId.trim();
                    return videoId;
                }
            }
        }
        return "";
    }

    public String getVideoIframe() {
        if (imageUrl != null) {
            if (isVideoUrl()) {
//                String str = "<iframe frameborder=\"0\" src=\"http://www.youtube.com/embed/" + getVideoId() + "\" width=\"100%\"></iframe>";
//                String str = "<iframe src=\"http://www.youtube.com/embed/" + getVideoId() + "\" width=\"100%\"></iframe>";
                String str = "<html><body><iframe frameborder=\\\"0\\\" allowfullscreen src=\"http://www.youtube.com/embed/" + getVideoId() + "\" width=\"100%\" height=\"200vh\"></iframe></body></html>";
                return str;
            }
        }
        return getImageUrl();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public void setImageList(JSONArray imageArray) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < imageArray.length(); i++) {
            try {
                jsons.add(imageArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                Boolean lid = null, rid = null;
                try {
                    lid = lhs.getBoolean("isDefult");
                    rid = rhs.getBoolean("isDefult");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return rid.compareTo(lid);
            }
        });
        JSONArray array = new JSONArray(jsons);
        try {
            setImageUrl(array.getJSONObject(0).getString("imageUrl"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imageList = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                imageList.add(jsonObject.getString("imageUrl"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public ArrayList<String> getTagList() {
        if (tagList == null) {
            tagList = new ArrayList<>();
        }
        return tagList;
    }

    public void setTagList(JSONArray array) {
        setTagJsonArrayString(array.toString());
        tagList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject json = array.getJSONObject(i);
                TagItem item = new TagItem();
                item.setTitle(json.getString("tagUnicode"));
                item.setId(json.getString("tagId"));
                item.setIconUrl(json.getString("tagIcon"));
                tagItemList.add(item);
                tagList.add(json.getString("tagUnicode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public PostItem() {
        tagItemList = new ArrayList<>();
    }

}
