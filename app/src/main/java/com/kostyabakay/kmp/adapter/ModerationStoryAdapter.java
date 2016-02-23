package com.kostyabakay.kmp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kostyabakay.kmp.R;
import com.kostyabakay.kmp.model.ModerationStory;

import java.util.List;

/**
 * Created by Kostya on 23.02.2016.
 * Адаптер, который заполняет ListView из объектов ModerationStory
 */
public class ModerationStoryAdapter extends ArrayAdapter<String> {
    private List<ModerationStory> mList;
    private Context mContext;

    public ModerationStoryAdapter(Context context, List<ModerationStory> list) {
        super(context, R.layout.list_moderation_story_item);
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        // Возвращаем количество элементов списка
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        // Получение одного элемента по индексу
        return mList.get(position).getStoryId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Заполнение элементов списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Задаем вид элемента списка, который мы создали выше
        View view = inflater.inflate(R.layout.list_moderation_story_item, parent, false);

        // Проставляем данные для элементов
        TextView content = (TextView) view.findViewById(R.id.moderation_story_content);

        // Получаем элемент со списка
        ModerationStory moderationStory = mList.get(position);

        // Устанавливаем значения компонентам одного элемента списка
        content.setText(moderationStory.getStoryContent());

        return view;
    }
}
