package com.derysudrajat.mynews;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String NEWS_KEY = "news_key";
    private final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
    @BindView(R.id.recycler_news)
    RecyclerView rvNews;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_no_query)
    TextView tvNoQuery;
    private List<News> newsList;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        if (savedInstanceState == null) {
            loadNews();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }

        refreshLayout.setOnRefreshListener(() -> {
            newsAdapter.clear();
            loadNews();
            refreshLayout.setRefreshing(false);
        });
    }

    private void initView() {
        newsAdapter = new NewsAdapter(this);
        rvNews.setLayoutManager(new LinearLayoutManager(this));
        rvNews.setHasFixedSize(true);
        rvNews.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadNews() {
        showLoading(true);
        Call<NewsItems> call = apiService.getNewsHeadlines("id", "sport", BuildConfig.API_KEY);
        newsList = new ArrayList<>();
        call.enqueue(new Callback<NewsItems>() {
            @Override
            public void onResponse(@NonNull Call<NewsItems> call, @NonNull Response<NewsItems> response) {
                if (response.body() != null) {
                    newsList = response.body().getNewsList();
                    tvNoQuery.setVisibility(View.GONE);
                }
                newsAdapter.setNewsList(newsList);
                rvNews.setAdapter(newsAdapter);
                showLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<NewsItems> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.cant_load_data, Toast.LENGTH_SHORT).show();
                tvNoQuery.setVisibility(View.VISIBLE);
                tvNoQuery.setText(R.string.no_connection);
                showLoading(false);
            }
        });

    }

    private void searchNews(String search) {
        showLoading(true);
        Call<NewsItems> call = apiService.getNews(search, BuildConfig.API_KEY);
        newsList = new ArrayList<>();
        call.enqueue(new Callback<NewsItems>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<NewsItems> call, @NonNull Response<NewsItems> response) {
                if (response.body() != null) {
                    newsList = response.body().getNewsList();
                    tvNoQuery.setVisibility(View.GONE);
                }
                if (newsList.size() == 0) {
                    tvNoQuery.setVisibility(View.VISIBLE);
                    tvNoQuery.setText(getString(R.string.no_news) + search);
                }
                newsAdapter.setNewsList(newsList);
                rvNews.setAdapter(newsAdapter);
                showLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<NewsItems> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.cant_load_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            SearchView searchView = (SearchView) (menu.findItem(R.id.search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    showLoading(true);
                    newsAdapter.clear();
                    searchNews(s);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NEWS_KEY, new ArrayList<>(newsAdapter.getNewsList()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<News> list;
        list = savedInstanceState.getParcelableArrayList(NEWS_KEY);
        newsAdapter.setNewsList(list);
        rvNews.setAdapter(newsAdapter);
    }
}
