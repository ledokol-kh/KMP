package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 04.02.2016.
 * Экземпляр класса ModerationStory представляет один пост в разделе "Модерация"
 */
public class ModerationStory {
    private String storyContent;

    public ModerationStory(String storyContent) {
        this.storyContent = storyContent;
    }

    @Override
    public String toString() {
        return storyContent;
    }
}
