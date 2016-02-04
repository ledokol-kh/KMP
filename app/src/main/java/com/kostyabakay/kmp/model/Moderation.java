package com.kostyabakay.kmp.model;

/**
 * Created by Kostya on 04.02.2016.
 * Экземпляр класса Moderation представляет один пост в разделе "Модерация"
 */
public class Moderation {
    private String moderationContent;

    public Moderation(String moderationContent) {
        this.moderationContent = moderationContent;
    }

    @Override
    public String toString() {
        return moderationContent;
    }
}
