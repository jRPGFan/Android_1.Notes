package com.example.notes;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.notes.observe.Publisher;
import com.example.notes.ui.EditNoteFragment;
import com.example.notes.ui.NotesListFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    private DrawerLayout drawerLayout;
    private Publisher publisher = new Publisher();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        if (savedInstanceState == null) initNotesList();

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout, mainToolbar, R.string.side_menu_closed, R.string.side_menu_opened);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        NavigationView sideNavigationView = findViewById(R.id.side_navigation_view);
        sideNavigationView.setNavigationItemSelectedListener(item -> {
            int itemID = item.getItemId();
            switch (itemID) {
                case R.id.lisa:
                    Toast toast = new Toast(getApplicationContext());
                    ImageView view  = new ImageView(getApplicationContext());
                    view.setImageResource(R.drawable.lisa_full);
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    toast.setView(view);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case R.id.side_menu_settings: Toast.makeText(this,
                        "Settings are going to be here", Toast.LENGTH_SHORT).show();
                break;
                case R.id.side_menu_about:
                    Toast.makeText(this, "You're running notes v0.2",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Oops, something went wrong",
                            Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        ImageView profilePictureView = sideNavigationView.getHeaderView(0).
                findViewById(R.id.user_photo);
        profilePictureView.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Ha ha ha. What a story, Mark",
                    Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        actionBarDrawerToggle.syncState();
    }

    private void initNotesList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NotesListFragment notesListFragment = new NotesListFragment();
        fragmentTransaction.add(R.id.notes_list_container, notesListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);

        MenuItem searchNotes = menu.findItem(R.id.toolbar_menu_search);
        SearchView searchView = (SearchView) searchNotes.getActionView();
        MenuItem addNote = menu.findItem(R.id.toolbar_menu_add_note);
        MenuItem sortNotes = menu.findItem(R.id.toolbar_menu_sort);
        MenuItem shareNote = menu.findItem(R.id.toolbar_menu_share);
        MenuItem attachPhoto = menu.findItem(R.id.toolbar_menu_add_photo);

        addNote.setOnMenuItemClickListener(this);
        sortNotes.setOnMenuItemClickListener(this);
        shareNote.setOnMenuItemClickListener(this);
        attachPhoto.setOnMenuItemClickListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_menu_add_note:
                EditNoteFragment editNoteFragment = EditNoteFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                boolean isPortrait = getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT;
                if (isPortrait) {
                    fragmentTransaction.addToBackStack("note_fragment");
                    fragmentTransaction.replace(R.id.notes_list_container, editNoteFragment).
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                } else fragmentTransaction.replace(R.id.note_layout, editNoteFragment).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            default:
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public Publisher getPublisher() {
        return publisher;
    }
}