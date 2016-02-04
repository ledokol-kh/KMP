package com.kostyabakay.kmp;

import android.app.ProgressDialog;
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

import com.kostyabakay.kmp.model.Moderation;
import com.kostyabakay.kmp.model.Story;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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

    public ArrayList<Story> storyArrayList = new ArrayList<Story>();
    public ArrayList<Moderation> moderationArrayList = new ArrayList<Moderation>();
    private ArrayAdapter<Story> storyArrayAdapter;
    private ArrayAdapter<Moderation> moderationArrayAdapter;
    private ListView listView;

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
        initSwipeRefreshLayout();

        listView = (ListView) findViewById(R.id.listView);
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

        if (navigationDrawerItemId == R.id.new_posts) {
            Log.i(TAG, "Выбрано раздел \"Новые\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            isRefreshed = false;
            loadedPagesCount = 0;
            newPostsAsyncTask = new NewPostsAsyncTask();
            newPostsAsyncTask.execute();
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.moderation) {
            Log.i(TAG, "Выбрано раздел \"Модерация\" в Navigation Drawer");
            moderationArrayAdapter = new ArrayAdapter<Moderation>(this, R.layout.list_moderation_item, R.id.moderation_content, moderationArrayList);
            isRefreshed = false;
            moderationAsyncTask = new ModerationAsyncTask();
            moderationAsyncTask.execute();
            if (!moderationArrayAdapter.isEmpty()) moderationArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.tell_story) {
            Log.i(TAG, "Выбрано раздел \"Рассказать историю\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.most_terrible_stories) {
            Log.i(TAG, "Выбрано раздел \"Самые страшные\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.random_story) {
            Log.i(TAG, "Выбрано раздел \"Случайная\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.happy_end) {
            Log.i(TAG, "Выбрано раздел \"Happy end\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.about_project) {
            Log.i(TAG, "Выбрано раздел \"О проекте\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        } else if (navigationDrawerItemId == R.id.help_all) {
            Log.i(TAG, "Выбрано раздел \"Хочу помочь всем\" в Navigation Drawer");
            storyArrayAdapter = new ArrayAdapter<Story>(this, R.layout.list_story_item, R.id.story_content, storyArrayList);
            if (!storyArrayAdapter.isEmpty()) storyArrayAdapter.clear();
        }

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
                    storyArrayAdapter.clear();
                    newPostsAsyncTask = new NewPostsAsyncTask();
                    newPostsAsyncTask.execute();
                } else if (navigationDrawerItemId == R.id.moderation) {
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
            ArrayList<Story> storyArrayList = new ArrayList<Story>();

            Elements storiesIds = null; // doc.getElementsByClass("col-xs-6");
            Elements storiesUrls = null;
            Elements storiesDateAndTime = null;
            Elements storiesTags = doc.select("[style=text-align:right]");
            Elements storiesContent = doc.select("[style=margin:0.5em 0;line-height:1.785em]");
            Elements storiesVotes = null; // doc.getElementsByClass("votelink");

            // Iterator<Element> storiesIdIterator = storiesIds.iterator();
            // Iterator<Element> storiesUrlIterator = storiesUrls.iterator();
            // Iterator<Element> storiesDateAndTimeIterator = storiesDateAndTime.iterator();
            Iterator<Element> storiesTagIterator = storiesTags.iterator();
            Iterator<Element> storiesContentIterator = storiesContent.iterator();
            // Iterator<Element> storiesVoteIterator = storiesVotes.iterator();

            while (storiesTagIterator.hasNext() && storiesContentIterator.hasNext()) {
                storyArrayList.add(new Story(null, null, null, storiesTagIterator.next().text(), storiesContentIterator.next().text(), null));
            }

            for (Story story : storyArrayList) {
                MainActivity.this.storyArrayList.add(story);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(storyArrayAdapter);
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
            ArrayList<Moderation> moderationArrayList = new ArrayList<Moderation>();

            Elements moderationContent = doc.select("[style=margin:0.5em 0;line-height:1.785em]");

            Iterator<Element> moderationContentIterator = moderationContent.iterator();

            while (moderationContentIterator.hasNext()) {
                moderationArrayList.add(new Moderation(moderationContentIterator.next().text()));
            }

            for (Moderation moderation : moderationArrayList) {
                MainActivity.this.moderationArrayList.add(moderation);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(moderationArrayAdapter);
            if (navigationDrawerItemId == R.id.moderation) progressDialog.dismiss();
        }
    }
}
