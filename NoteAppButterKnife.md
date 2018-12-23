# Previously on Building A Note Taking App

In the [**previous tutorial**](https://androidpirate.github.io/NoteAppSwipeDelete/ "**previous tutorial**"), we implemented **swipeToDelete()** function in **RecyclerView** which pops an **AlertDialog** to ask the user if they want to permanently delete a note, and removes it both from the database and **RecyclerView** if the user choose to do so.  


# NoteApp ButterKnife – Tutorial 7

Start by cloning/forking the app from the link below:

[**NoteApp ButterKnife - Tutorial 7**](https://github.com/androidpirate/NoteAppButterknife "**NoteApp ButterKnife - Tutorial 7**")


### Goal of This Tutorial

The goal of this tutorial is to use **ButterKnife**, a view binding library from **Jake Wharton**, to bind views at compile time, rather
than using **findViewById()** method. We are also going to add an **updateNote()** to **NoteDbHelper** to update an existing note on database.

Check out the official **ButterKnife** page on [**Github**](http://jakewharton.github.io/butterknife/ "**ButterKnife - by Jake Wharton**")


### What’s in Starter Module?

Starter module already has a fully functional **RecyclerView** that works with a **SQLite database** to get a list of notes, an **EditActivity** that allow us to insert new notes, and finally **swipeToDelete()** method to delete notes.

You can follow the steps below and give it a shot yourself, and if you stuck at some point, check out the **solution module** or the rest
of the tutorial.


### Steps to Build

1. Add library dependencies for **ButterKnife**
2. Bind views in **MainActivity**
3. Bind views in **DetailActivity**
4. Bind views in **EditActivity**
5. Bind views in **NoteHolder**
6. Implement **updateNote()** method to **NoteDbHelper**
7. Add a new menu resource that has an **EDIT** action item and display it in **DetailActivity**
8. Refactor **EditActivity** to handle both creating new notes and editing existing ones
9. Refactor **EditActivity** 's layout file to support multiple text lines for **EditText** fields


### Including ButterKnife

Open up the **app level build.gradle** file on project pane, and insert the following dependencies:


```gradle
dependencies {
    // Other dependencies are excluded for simplicity
    compile 'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
}
```


Make sure to sync the files before closing the file.


### Injecting Views

Open **MainActivity**, and bind **ButterKnife** to the activity in **OnCreate**:


```java
public class MainActivity extends AppCompatActivity
    implements NoteAdapter.NoteClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    // Fields are excluded for simplicity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Bind ButterKnife to activity
        ButterKnife.bind(this);
   }
   .
   .
}
```


Static **bind()** method takes a **Context** to bind to, which is **MainActivity** in this case. Once the binding is done, it is time to inject views:


```java
public class MainActivity extends AppCompatActivity
    implements NoteAdapter.NoteClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private NoteDbHelper mDbHelper;
    @BindView(R.id.tv_empty_list) TextView emptyListMessage;
    @BindView(R.id.rv_note_list) RecyclerView recyclerView;

    // Callbacks are excluded for simplicity
    .
    .
}
```


**ButterKnife** uses annotations to activate specific commands. In this case, **@BindView(layout_element_id)** is used to bind view to the reference. Once bind, we don't need to call **findViewById() method**. Same thing applies to **DetailActivity**
and **EditActivity** as follows:


```java
public class DetailActivity extends AppCompatActivity {
    // Fields are excluded for simplicity
    @BindView(R.id.tv_title) TextView title;
    @BindView(R.id.tv_description) TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Bind ButterKnife to activity
        ButterKnife.bind(this);
        // Rest is excluded for simplicity
        .
        .
    }
    .
    .
}
```


```java
public class EditActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE = "extra-note";
    public static final String EXTRA_EDIT_MODE = "extra-edit-mode";
    private Note note;
    private int mode;
    @BindView(R.id.et_title) EditText title;
    @BindView(R.id.et_description) EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Bind ButterKnife to activity
        ButterKnife.bind(this);
        // Rest is excluded for simplicity
        .
        .
    }
    .
    .
}
```


Next is to use **ButterKnife** with the **NoteHolder** item views. Since we are using it to bind item views, we need to specify it as an argument it in static **bind()** method:


```java
public class NoteHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_title)
    public TextView title;
    @BindView(R.id.tv_description)
    public TextView description;
    @BindView(R.id.cv_foreground)
    public CardView foregroundView;

    public NoteHolder(View itemView) {
        super(itemView);
        // Bind ButterKnife to item view
        ButterKnife.bind(this, itemView);
        // Rest is excluded for simplicity
        .
        .
    }
    .
    .
}
```


### Updating Notes

We also need to implement an **updateNote()** method in our note database to update existing notes. To do that we need to get a writable database instance and **ContentValues** object to store the new data:


```java
public void updateNote(Note note) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(NoteEntry.NOTE_TITLE, note.getTitle());
    values.put(NoteEntry.NOTE_DESCRIPTION, note.getDescription());
    db.update(NoteEntry.TABLE_NAME,
            values,
            NoteEntry._ID + " = ?",
            new String[]{String.valueOf(note.get_id())});
}
```


As you can see above, once we store the new values to **ContentValues** object, we call database's **update()** method, passing **Table Name**, **ContentValues** and **id** of the note as arguments. That's all we need to do, on the database side.


### Creating a Menu for Updating Notes

Of course, we also need a action menu item for the user to interact with to update their notes. For that purpose, we are going to create a menu which only has one action item, **EDIT**. So go ahead and create a new resource file under **res/menu** directory, called **detail_menu.xml**:


```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/edit_note_action"
        android:title="@string/edit"
        app:showAsAction="always"/>

</menu>
```


Next is to display the menu within our **DetailActivity**, so we can use to update existing
notes in the database:


```java
public class DetailActivity extends AppCompatActivity {
    // Fields and callbacks are excluded for simplicity
    .
    .
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Returns back to MainActivity
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.edit_note_action:
                // Will be implemented below
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    .
    .
}
```


### Creating or Updating

In our implementation of **Note Taking App** so far, we used **EditActivity** to only Create
new notes. Since we implemented an **update()** method in our database, we also need an activity to update an existing note. As we all know, it is not a good practice to repeat the same code, so instead of creating a new activity that will handle updating existing notes, we
will use **EditActivity** to get the job done.

For all that to work properly, we first need to identify if a note is **new** or **already exist**, so **EditActivity** will know exactly know how to handle both cases. To do that, I will
use another **Intent Extra** to let **EditActivity** to what kind of note it is dealing with.

#### Case: New Note

New notes are created in **MainActivity**, we can let **EditActivity** to know it is
a new note by adding the following to our existing code:


```java
public class MainActivity extends AppCompatActivity
    implements NoteAdapter.NoteClickListener,
      RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
      // Fields and callbacks are excluded for simplicity
      .
      .
      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {
              case R.id.new_note_action:
                  // Start EditActivity to create a new note
                  Intent intent = new Intent(this, EditActivity.class);
                  // Used to inform EditActivity that the note is a new note
                  intent.putExtra(EditActivity.EXTRA_EDIT_MODE, 0);
                  intent.putExtra(EditActivity.EXTRA_NOTE, new Note());
                  startActivity(intent);
                  return true;
          }
          return super.onOptionsItemSelected(item);
      }
      .
      .
}
```


The **EXTRA_EDIT_MODE** is nothing but a **Integer Extra**, that defines a mode for **EditActivity** to be checked when the **Intent Extra** is received. In this case it is set to **0**, to identify the note is **new**.

#### Case: Existing Note

Likewise, we display an existing note using **DetailActivity**, so this is where we should update
an existing note too. it is time to implement **EDIT** action item that we added earlier in this tutorial:


```java
public class DetailActivity extends AppCompatActivity {
    // Fields and callbacks are excluded for simplicity
    .
    .
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
    .
    .
}
```


Only this time, we set **EXTRA_EDIT_MODE** to **1**, which represents an **existing note**. Next is
to update how we handle both situations in **EditActiviy**:


```java
public class EditActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_MODE = "extra-edit-mode";
    // New field to store mode information
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Content excluded for simplicity
        .
        .
        // Get the note from intent extra
        if(getIntent() != null) {
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            mode = getIntent().getIntExtra(EXTRA_EDIT_MODE, 0);
        }
    }
}
```


All we have to do, is to store new **Intent Extra** into a field variable and check it when it is
time to save the note, to call corresponding database method:


```java
@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note_action:
                NoteDbHelper noteDbHelper = NoteDbHelper.getInstance(this);
                // Check mode to identify whether it is new or already exists
                if(mode == 0) {
                    noteDbHelper.insertNote(note);
                } else if (mode == 1) {
                    noteDbHelper.updateNote(note);
                }
                // Content excluded for simplicity
                .
                .
        }
        return super.onOptionsItemSelected(item);
    }
```


### Multi Line Text Support

We are almost done with this tutorial, but before we are all done, there is one we need to take care
of: **Single Line EditText Fields Reaching to Infinity"**. That's right! You probably noticed if you
want to create/edit a note, both fields in **EditActivity** append words at the end of a single forever
lasting line. Actually, we can fix it easily by adding a single parameter to those fields in the layout
file:


```xml
<EditText
        android:id="@+id/et_title"
        .
        .
        android:inputType="textMultiLine"/>

    <EditText
        android:id="@+id/et_description"
        .
        .
        android:inputType="textMultiLine"/>
```


And that's all! Congratulations, you have done a fantastic job! Now, we have a fully functional **NoteApp** where you can add, update and delete notes! 


### What's In Next Tutorial

In our next tutorial, we will use **Fragments** to create re-usable views that works in coordination with **Activity Lifecycle** to reduce the number of activities in **NoteApp**.


### Resources

1. [Android Developer Guides](https://developer.android.com/guide/ "Android Developer Guides") by Google
2. [ButterKnife](http://jakewharton.github.io/butterknife/ "ButterKnife by Jake Wharton") by Jake Wharton
