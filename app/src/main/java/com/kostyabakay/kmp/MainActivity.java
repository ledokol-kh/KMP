package com.kostyabakay.kmp;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kostyabakay.kmp.adapter.NewStoryAdapter;
import com.kostyabakay.kmp.fragment.HappyEndStoryFragment;
import com.kostyabakay.kmp.fragment.ModerationStoryFragment;
import com.kostyabakay.kmp.fragment.NewStoryFragment;
import com.kostyabakay.kmp.fragment.RandomStoryFragment;
import com.kostyabakay.kmp.fragment.TellStoryFragment;
import com.kostyabakay.kmp.fragment.TopStoryFragment;
import com.kostyabakay.kmp.model.ModerationStory;
import com.kostyabakay.kmp.model.NewStory;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kostya on 08.01.2016.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipyRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";
    private static final int LAYOUT = R.layout.activity_main;
    private static final String URL = "http://killpls.me";
    private static final String URL_MODERATION = "http://killpls.me/moderation/";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ProgressDialog progressDialog;
    private SwipyRefreshLayout mSwipyRefreshLayout;

    private NewPostsAsyncTask newPostsAsyncTask;
    private ModerationAsyncTask moderationAsyncTask;

    public ArrayList<NewStory> newStoryArrayList;
    public ArrayList<ModerationStory> moderationStoryArrayList = new ArrayList<ModerationStory>();
    private NewStoryAdapter newStoryAdapter;
    private ArrayAdapter<NewStory> storyArrayAdapter; // TODO: удалить
    private ArrayAdapter<ModerationStory> moderationArrayAdapter; // TODO: удалить
    private ListView listView;

    NewStoryFragment newStoryFragment;
    ModerationStoryFragment moderationStoryFragment;
    TellStoryFragment tellStoryFragment;
    TopStoryFragment topStoryFragment;
    RandomStoryFragment randomStoryFragment;
    HappyEndStoryFragment happyEndStoryFragment;

    private int navigationDrawerItemId;
    private int loadedPagesCount = 0;
    private boolean isRefreshed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initNavigationView();
        initActionBarDrawerToggle(); // Добавляет возможность открыть NavigationDrawer через значок
        initFloatingActionButton();
        // initSwipeRefreshLayout(); TODO: NPE

        listView = (ListView) findViewById(R.id.listView);

        newStoryFragment = new NewStoryFragment();
        moderationStoryFragment = new ModerationStoryFragment();
        tellStoryFragment = new TellStoryFragment();
        topStoryFragment = new TopStoryFragment();
        randomStoryFragment = new RandomStoryFragment();
        happyEndStoryFragment = new HappyEndStoryFragment();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initActionBarDrawerToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(this);
        mSwipyRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        navigationDrawerItemId = item.getItemId();

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (navigationDrawerItemId == R.id.new_posts) {
            Log.i(TAG, "Выбрано раздел \"Новые\" в Navigation Drawer");
            ft.replace(R.id.container, newStoryFragment);
            isRefreshed = false;
            loadedPagesCount = 0;
            newPostsAsyncTask = new NewPostsAsyncTask(this);
            newPostsAsyncTask.execute();
        } else if (navigationDrawerItemId == R.id.moderation_posts) {
            Log.i(TAG, "Выбрано раздел \"Модерация\" в Navigation Drawer");
            ft.replace(R.id.container, moderationStoryFragment);
            // TODO: Custom adapter
            // moderationArrayAdapter = new ArrayAdapter<ModerationStory>(this, R.layout.list_moderation_story_item, R.id.moderation_content, moderationStoryArrayList);
            isRefreshed = false;
            moderationAsyncTask = new ModerationAsyncTask();
            moderationAsyncTask.execute();
        } else if (navigationDrawerItemId == R.id.tell_story) {
            Log.i(TAG, "Выбрано раздел \"Рассказать историю\" в Navigation Drawer");
            ft.replace(R.id.container, tellStoryFragment);
        } else if (navigationDrawerItemId == R.id.most_terrible_stories) {
            Log.i(TAG, "Выбрано раздел \"Самые страшные\" в Navigation Drawer");
            ft.replace(R.id.container, topStoryFragment);
        } else if (navigationDrawerItemId == R.id.random_story) {
            Log.i(TAG, "Выбрано раздел \"Случайная\" в Navigation Drawer");
            ft.replace(R.id.container, randomStoryFragment);
        } else if (navigationDrawerItemId == R.id.happy_end) {
            Log.i(TAG, "Выбрано раздел \"Happy end\" в Navigation Drawer");
            ft.replace(R.id.container, happyEndStoryFragment);
        } else if (navigationDrawerItemId == R.id.about_project) {
            Log.i(TAG, "Выбрано раздел \"О проекте\" в Navigation Drawer");
        } else if (navigationDrawerItemId == R.id.help_all) {
            Log.i(TAG, "Выбрано раздел \"Хочу помочь всем\" в Navigation Drawer");
        }

        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (navigationDrawerItemId == R.id.new_posts) {
                    Log.i(TAG, "Обновленно раздел \"Новые\" в Navigation Drawer");
                    isRefreshed = true;
                    newStoryAdapter.clear();
                    newPostsAsyncTask = new NewPostsAsyncTask(MainActivity.this);
                    newPostsAsyncTask.execute();
                } else if (navigationDrawerItemId == R.id.moderation_posts) {
                    Log.i(TAG, "Обновленно раздел \"Модерация\" в Navigation Drawer");
                    isRefreshed = true;
                    moderationArrayAdapter.clear();
                    moderationAsyncTask = new ModerationAsyncTask();
                    moderationAsyncTask.execute();
                } else if (navigationDrawerItemId == R.id.tell_story) {
                    Log.i(TAG, "Обновленно раздел \"Рассказать историю\" в Navigation Drawer");
                } else if (navigationDrawerItemId == R.id.most_terrible_stories) {
                    Log.i(TAG, "Обновленно раздел \"Самые страшные\" в Navigation Drawer");
                } else if (navigationDrawerItemId == R.id.random_story) {
                    Log.i(TAG, "Обновленно раздел \"Случайная\" в Navigation Drawer");
                } else if (navigationDrawerItemId == R.id.happy_end) {
                    Log.i(TAG, "Обновленно раздел \"Happy end\" в Navigation Drawer");
                } else if (navigationDrawerItemId == R.id.about_project) {
                    Log.i(TAG, "Обновленно раздел \"О проекте\" в Navigation Drawer");
                } else if (navigationDrawerItemId == R.id.help_all) {
                    Log.i(TAG, "Обновленно раздел \"Хочу помочь всем\" в Navigation Drawer");
                } else {
                    Log.i(TAG, "Попытка обновить главную страницу");
                }

                // Когда обновление закончено, вызываем метод setRefreshing(boolean) и передаем ему false.
                mSwipyRefreshLayout.setRefreshing(false);
            }
        }, 4000);
    }

    class NewPostsAsyncTask extends AsyncTask<String, Void, String> {
        private Context mContext;

        public NewPostsAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isRefreshed) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Новые");
                progressDialog.setMessage("Загрузка...");
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Document doc;
            try {
                doc = Jsoup.connect(URL).get(); // Считываем заголовок страницы
                Elements pageSpan = doc.select("div.paginator > span:first-child");
                int pageCount = Integer.parseInt(pageSpan.first().text());
                // Стоит еще проверить, что элементы нашлись, вызовом !pageSpan.isEmpty(),
                // first() для пустого списка возвращает null.
                String pageCountString = null;

                if (loadedPagesCount == 0) {
                    pageCountString = Integer.toString(pageCount);
                } else if (loadedPagesCount > 0) {
                    pageCountString = Integer.toString(pageCount - loadedPagesCount);
                }

                doc = Jsoup.connect("http://killpls.me/page/" + pageCountString).get();
                parseDocument(doc);
                loadedPagesCount++;

            } catch (IOException e) {
                e.printStackTrace(); // Если не получилось считать
            }
            return null;
        }

        public void parseDocument(Document doc) {
            // Выбрать внутри div с классом .row такие div с классом .col-xs-6, у которых нет атрибута style.
            Elements storiesHeadInfo = doc.select("div.row>div.col-xs-6:not([style])"); // id, дата, время
            Elements storiesTags = doc.select("[style=text-align:right]"); // теги
            Elements storiesContent = doc.select("[style=margin:0.5em 0;line-height:1.785em]"); // история
            Elements storiesVotes = doc.select("[style=text-align:center]"); // голоса

            Iterator<Element> storiesInfoIterator = storiesHeadInfo.iterator();
            Iterator<Element> storiesTagIterator = storiesTags.iterator();
            Iterator<Element> storiesContentIterator = storiesContent.iterator();
            Iterator<Element> storiesVoteIterator = storiesVotes.iterator();

            newStoryArrayList = new ArrayList<NewStory>();
            newStoryAdapter = new NewStoryAdapter(mContext, parseNewStoryData(storiesInfoIterator, storiesTagIterator, storiesContentIterator, storiesVoteIterator));
        }

        // Этот медот разбирает каждый элемент поста и передает эти данные в конструктор модели
        private ArrayList<NewStory> parseNewStoryData(Iterator<Element> storiesInfoIterator, Iterator<Element> storiesTagIterator, Iterator<Element> storiesContentIterator, Iterator<Element> storiesVoteIterator) {
            while (storiesInfoIterator.hasNext()) {
                String info = storiesInfoIterator.next().text();
                String[] splittedInfo = info.split("\u0097"); // Разделяем по символу '—'
                String id = splittedInfo[0].trim();
                String[] dateAndTime = (splittedInfo[1].trim()).split(","); // Разделяем по символу ','
                String date = dateAndTime[0].trim();
                String time = dateAndTime[1].trim();

                String url = null;
                String tag = storiesTagIterator.next().text();
                String content = storiesContentIterator.next().text();

                String vote = storiesVoteIterator.next().text();
                String[] voteStr = vote.split("\\s+\\s+"); // Разделяем по строке ' &nbsp'
                vote = voteStr[1];
                voteStr = vote.split("\\s+"); // Разделяем по символу пробела ' '
                vote = voteStr[0];

                NewStory newStory = new NewStory(id, url, date, time, tag, content, vote);
                newStoryArrayList.add(newStory);
            }

            return newStoryArrayList;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(newStoryAdapter); // TODO: NPE
            if (navigationDrawerItemId == R.id.new_posts) progressDialog.dismiss();
        }
    }

    class ModerationAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isRefreshed) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Модерация");
                progressDialog.setMessage("Загрузка...");
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Document doc;
            try {
                doc = Jsoup.connect(URL_MODERATION).get();
                parseDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void parseDocument(Document doc) {
            ArrayList<ModerationStory> moderationStoryArrayList = new ArrayList<ModerationStory>();

            Elements moderationContent = doc.select("[style=margin:0.5em 0;line-height:1.785em]");

            Iterator<Element> moderationContentIterator = moderationContent.iterator();

            while (moderationContentIterator.hasNext()) {
                moderationStoryArrayList.add(new ModerationStory(moderationContentIterator.next().text()));
            }

            for (ModerationStory moderationStory : moderationStoryArrayList) {
                MainActivity.this.moderationStoryArrayList.add(moderationStory);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(moderationArrayAdapter); // TODO: NPE
            if (navigationDrawerItemId == R.id.moderation_posts) progressDialog.dismiss();
        }
    }
}
