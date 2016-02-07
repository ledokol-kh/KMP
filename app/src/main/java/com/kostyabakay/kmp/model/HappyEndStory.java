package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 07.02.2016.
 * Экземпляр класса HappyEndStory представляет один пост в разделе "Happy End"
 */
public class HappyEndStory {
    private String storyContent;

    public HappyEndStory(String storyContent) {
        this.storyContent = storyContent;
    }

    @Override
    public String toString() {
        return storyContent;
    }
}
