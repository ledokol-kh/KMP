package com.kostyabakay.kmp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kostyabakay.kmp.R;
import com.kostyabakay.kmp.model.NewStory;

import java.util.List;

/**
 * Created by Kostya on 10.02.2016.
 * Адаптер, который заполняет ListView из объектов NewStory
 */
public class NewStoryAdapter extends ArrayAdapter<String> {
    private List<NewStory> mList;
    private Context mContext;

    public NewStoryAdapter(Context context, List<NewStory> list) {
        super(context, R.layout.list_new_story_item);
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
        View view = inflater.inflate(R.layout.list_new_story_item, parent, false);

        // Проставляем данные для элементов
        TextView id = (TextView) view.findViewById(R.id.new_story_id);
        TextView url = (TextView) view.findViewById(R.id.new_story_url);
        TextView dateAndTime = (TextView) view.findViewById(R.id.new_story_date_and_time);
        TextView tag = (TextView) view.findViewById(R.id.new_story_tag);
        TextView content = (TextView) view.findViewById(R.id.new_story_content);
        TextView vote = (TextView) view.findViewById(R.id.new_story_vote);

        // Получаем элемент со списка
        NewStory newStory = mList.get(position);

        // Устанавливаем значения компонентам одного элемента списка
        id.setText(newStory.getStoryId());
        url.setText(newStory.getStoryUrl());
        dateAndTime.setText(newStory.getStoryDateAndTime());
        tag.setText(newStory.getStoryTag());
        content.setText(newStory.getStoryContent());
        vote.setText(newStory.getStoryVote());

        return view;
    }
}
