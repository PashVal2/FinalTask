package com.example.finaltask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.finaltask.databinding.ActivityInstructionsBinding;
import com.google.android.material.navigation.NavigationView;
public class InstructionsActivity extends AppCompatActivity implements
        ExitForMenu {
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    private MediaPlayer mediaPlayer;
    ActivityInstructionsBinding binding;
    private static final String PREF_NAME = "Name", PREF_ROLE = "Role", PREF_UID = "Uid";
    SharedPreferences settings;
    private static final String PREFS_FILE = "Account";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mediaPlayer = MediaPlayer.create(this, R.raw.heartman);
        mediaPlayer.setLooping(true); // Зацикливание воспроизведения
        startMusic();
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        getName();
        ActionBar actionBar = getSupportActionBar();
        drawer = findViewById(R.id.drawer_layout3);
        toggle = new ActionBarDrawerToggle(
                InstructionsActivity.this, drawer, R.string.drawer_close,
                R.string.drawer_open);
        if (drawer != null) {
            actionBar.setTitle("Инструкции"); // Установить заголовок
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
                    Intent i = new Intent(InstructionsActivity.this, DevActivity.class);
                    stopMusic();
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_prog) {
                    Intent i = new Intent(InstructionsActivity.this, AboutActivity.class);
                    stopMusic();
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_user) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else if (item.getItemId() == R.id.nav_home) {
                    Intent i = new Intent(InstructionsActivity.this, SecondActivity.class);
                    stopMusic();
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
        stopMusic();
        Intent i = new Intent(InstructionsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    public void getName() {
        String name = settings.getString(PREF_NAME, "Не определено");
        binding.textHello.setText("Привет, " + name + ", добро пожаловать! " +
                "Здесь вы найдете все необходимое для удовольствия от " +
                "поиска аптек и лекарств прямо с вашего мобильного устройства. " +
                "Вот некоторые инструкции для удобного использования приложения:");
    }
    private void startMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Используйте pause(), чтобы воспроизведение могло быть возобновлено с того же места
        }
    }
}