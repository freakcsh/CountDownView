package com.freak.countdownview;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private RecyclerView recycle_view;
    private Adapter adapter;
    private List<TestEntity> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        list.add(new TestEntity(86006));
        list.add(new TestEntity(86106));
        list.add(new TestEntity(86206));
        list.add(new TestEntity(86306));
        list.add(new TestEntity(86406));
        list.add(new TestEntity(86506));
        adapter = new Adapter(R.layout.item_view, list);
        recycle_view.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopAllTime();
    }
}
