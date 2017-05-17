package com.dvlchm.fsa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ListView taskListView = (ListView)findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressbar);
        TextView emptyTextView = (TextView)findViewById(R.id.empty_view);

        if(taskListView!=null)
        {
            taskListView.setEmptyView(emptyTextView);
        }
    }
}
