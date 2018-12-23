package com.example.android.noteappbutterknife;

import com.example.android.noteappbutterknife.model.Note;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE = "extra-note";
    private Note note;
    @BindView(R.id.tv_title) TextView title;
    @BindView(R.id.tv_description) TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Bind ButterKnife to activity
        ButterKnife.bind(this);
        // Enable up button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get the note data from intent extra
        if(getIntent().getExtras() != null) {
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
        }
        // Subscribe ui
        title = findViewById(R.id.tv_title);
        description = findViewById(R.id.tv_description);
        if(note.getTitle() != null && note.getDescription() != null) {
            title.setText(note.getTitle());
            description.setText(note.getDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Returns back to MainActivity
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.edit_note_action:
                // Starts EditActivity in Edit Mode
                Intent intent = new Intent(this, EditActivity.class);
                // Used to inform EditActivity that the note already exists
                intent.putExtra(EditActivity.EXTRA_EDIT_MODE, 1);
                intent.putExtra(EditActivity.EXTRA_NOTE, note);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
