package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 03.02.2016.
 * Экземпляр класса NewStory представляет один пост в разделе "Новые"
 */
public class NewStory {
    private String mStoryId;
    private String mStoryUrl;
    private String mStoryDate;
    private String mStoryTime;
    private String mStoryTag;
    private String mStoryContent;
    private String mStoryVote;

    public NewStory(String storyId, String storyUrl, String storyDate, String storyTime, String storyTag, String storyContent, String storyVote) {
        this.mStoryId = storyId;
        this.mStoryUrl = storyUrl;
        this.mStoryDate = storyDate;
        this.mStoryTime = storyTime;
        this.mStoryTag = storyTag;
        this.mStoryContent = storyContent;
        this.mStoryVote = storyVote;
    }

    @Override
    public String toString() {
        return mStoryId + "\n" + mStoryUrl + "\n" + mStoryDate + "\n" + mStoryTime + "\n" + mStoryTag + "\n" + mStoryContent + "\n" + mStoryVote;
    }

    public String getStoryId() {
        return mStoryId;
    }

    public String getStoryUrl() {
        return mStoryUrl;
    }

    public String getStoryDate() {
        return mStoryDate;
    }

    public String getStoryTime() {
        return mStoryTime;
    }

    public String getStoryTag() {
        return mStoryTag;
    }

    public String getStoryContent() {
        return mStoryContent;
    }

    public String getStoryVote() {
        return mStoryVote;
    }
}
