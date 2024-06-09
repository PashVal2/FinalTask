package com.example.finaltask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.finaltask.databinding.ActivityDevBinding;
import com.google.android.material.navigation.NavigationView;
public class DevActivity extends AppCompatActivity
        implements ExitForMenu {
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    private static final String PREF_NAME = "Name", PREF_ROLE = "Role", PREF_UID = "Uid", PREFS_FILE = "Account";
    SharedPreferences settings;
    ActivityDevBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDevBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        ActionBar actionBar = getSupportActionBar();
        drawer = findViewById(R.id.drawer_layout1);
        toggle = new ActionBarDrawerToggle(DevActivity.this, drawer, R.string.drawer_close, R.string.drawer_open);
        if (drawer != null) {
            actionBar.setTitle("Об авторе"); // Установить заголовок
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_exit) {
                    CustomDialogFragment dialog = new CustomDialogFragment();
                    Bundle args = new Bundle();
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), "custom");
                }
                else if (item.getItemId() == R.id.nav_dev) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else if (item.getItemId() == R.id.nav_prog) {
                    Intent i = new Intent(DevActivity.this, AboutActivity.class);
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_user) {
                    Intent i = new Intent(DevActivity.this, InstructionsActivity.class);
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_home) {
                    Intent i = new Intent(DevActivity.this, SecondActivity.class);
                    startActivity(i);
                }
                return false;
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) { return true; }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void IntentF() {
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(PREF_NAME, "");
        prefEditor.putString(PREF_ROLE, "");
        prefEditor.putString(PREF_UID, "");
        prefEditor.apply();
        Intent i = new Intent(DevActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}