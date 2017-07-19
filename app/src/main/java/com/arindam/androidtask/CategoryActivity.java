package com.arindam.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, RecyclerAdapter.ClickListener {

    String[] names = {"Shopping Mall", "Hospital", "Office", "Guest House", "College"};

    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Category> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Choose category");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        int count = 0;
        for (String Name: names) {
            arrayList.add(new Category(Name, names[count]));
            count++;
        }

        adapter = new RecyclerAdapter(arrayList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Category> newList = new ArrayList<>();

        for (Category category: arrayList) {
            String name = category.getName().toLowerCase();
            if (name.contains(newText)) {
                newList.add(category);
            }
        }

        adapter.setFilter(newList);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("name", arrayList.get(position).getName());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
