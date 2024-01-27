package com.riversanskiriti.prarang.adapter;

/**
 * Created by ARPIT on 07-04-2017.
 */

public class CommentItem {
    String id, subscriberId, name, chittiId, comment, imageUrl, profilePic;
    byte[] attachement;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getAttachement() {
        return attachement;
    }

    public void setAttachement(byte[] attachement) {
        this.attachement = attachement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChittiId() {
        return chittiId;
    }

    public void setChittiId(String chittiId) {
        this.chittiId = chittiId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
