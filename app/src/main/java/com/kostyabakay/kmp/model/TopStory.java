package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 07.02.2016.
 * Экземпляр класса TopStory представляет один пост в разделе "Самые страшные"
 */
public class TopStory {
    private String storyId;
    private String storyUrl;
    private String storyDateAndTime;
    private String storyTag;
    private String storyContent;
    private String storyVote;

    public TopStory(String storyId, String storyUrl, String storyDateAndTime, String storyTag, String storyContent, String storyVote) {
        this.storyId = storyId;
        this.storyUrl = storyUrl;
        this.storyDateAndTime = storyDateAndTime;
        this.storyTag = storyTag;
        this.storyContent = storyContent;
        this.storyVote = storyVote;
    }

    @Override
    public String toString() {
        return storyId + "\n" + storyUrl + "\n" + storyDateAndTime + "\n" + storyTag + "\n" + storyContent + "\n" + storyVote;
    }
}
