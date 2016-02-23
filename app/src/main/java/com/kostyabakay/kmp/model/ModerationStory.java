package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 04.02.2016.
 * Экземпляр класса ModerationStory представляет один пост в разделе "Модерация"
 */
public class ModerationStory {
    private String mStoryId;
    private String mStoryContent;

    public ModerationStory(String storyContent) {
        this.mStoryContent = storyContent;
    }

    @Override
    public String toString() {
        return mStoryContent;
    }

    public String getStoryId() {
        return mStoryId;
    }

    public String getStoryContent() {
        return mStoryContent;
    }
}
