package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 03.02.2016.
 * Экземпляр класса NewStory представляет один пост в разделе "Новые"
 */
public class NewStory {
    private String mStoryId;
    private String mStoryUrl;
    private String mStoryDateAndTime;
    private String mStoryTag;
    private String mStoryContent;
    private String mStoryVote;

    public NewStory(String storyId, String storyUrl, String storyDateAndTime, String storyTag, String storyContent, String storyVote) {
        this.mStoryId = storyId;
        this.mStoryUrl = storyUrl;
        this.mStoryDateAndTime = storyDateAndTime;
        this.mStoryTag = storyTag;
        this.mStoryContent = storyContent;
        this.mStoryVote = storyVote;
    }

    @Override
    public String toString() {
        return mStoryId + "\n" + mStoryUrl + "\n" + mStoryDateAndTime + "\n" + mStoryTag + "\n" + mStoryContent + "\n" + mStoryVote;
    }

    public String getStoryId() {
        return mStoryId;
    }

    public String getStoryUrl() {
        return mStoryUrl;
    }

    public String getStoryDateAndTime() {
        return mStoryDateAndTime;
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
