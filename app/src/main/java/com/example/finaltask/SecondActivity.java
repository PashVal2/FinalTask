package com.example.finaltask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.finaltask.databinding.ActivitySecondBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class SecondActivity extends AppCompatActivity
        implements ExitForMenu {
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    BoxAdapter boxAdapter;
    ArrayList<Pharm> products = new ArrayList<Pharm>();
    private static final String PREF_NAME = "Name", PREF_ROLE = "Role", PREF_UID = "Uid";
    SharedPreferences settings;
    private static final String PREFS_FILE = "Account";
    ActivitySecondBinding binding;
    Comparator<Pharm> nameComparator = new Comparator<Pharm>() {
        @Override
        public int compare(Pharm p1, Pharm p2) {
            return p1.name.compareTo(p2.name);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pharmacy-d6081.appspot.com/");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pharmacy-d6081-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference tableReference = database.getReference("PharmList");
        // получение списка & заполнение списка с помощью СУБД firebse
        tableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    if (productSnapshot.exists()) {
                        String productName = productSnapshot.child("name").getValue(String.class);
                        String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                        // Загрузка изображения из Firebase Storage
                        StorageReference imageRef = storage.getReference(imageUrl);
                        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            // Успешно загруженные байты изображения
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Pharm product = new Pharm(productName, bitmap, false); // Создание объекта Product с учетом загруженного изображения
                            products.add(product);
                            // Обновление пользовательского интерфейса после загрузки всех изображений
                            if (products.size() == dataSnapshot.getChildrenCount()) {
                                Collections.sort(products, nameComparator);
                                boxAdapter = new BoxAdapter(SecondActivity.this, products, false);
                                lvMain.setAdapter(boxAdapter);
                            }
                        }).addOnFailureListener(exception -> {
                            // Обработка ошибок загрузки изображения
                            Log.e("TAG", "Ошибка загрузки рисунка с зранилища", exception);
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        // обработчик нажатия на элемент списка
        binding.lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получаем ссылку на элемент списка, который был нажат
                Pharm item = (Pharm) parent.getAdapter().getItem(position);
                Intent i = new Intent(SecondActivity.this, ProdustListActivity.class);
                i.putExtra("name", item.name);
                startActivity(i);
            }
        });
        // Работа с меню-бургером
        ActionBar actionBar = getSupportActionBar();
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                SecondActivity.this, drawer, R.string.drawer_open,
                R.string.drawer_close);
        if (drawer != null) {
            actionBar.setTitle("Главная"); // Установить заголовок
            drawer.addDrawerListener(toggle);

        }
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // обработка нажатия на пункт в выдвижном меню
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
                    Intent i = new Intent(SecondActivity.this, DevActivity.class);
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_prog) {
                    Intent i = new Intent(SecondActivity.this, AboutActivity.class);
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_user) {
                    Intent i = new Intent(SecondActivity.this, InstructionsActivity.class);
                    startActivity(i);
                }
                else if (item.getItemId() == R.id.nav_home) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            });
        }
        // Прослушиватель поисковика
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (boxAdapter != null) {
                    a(query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && boxAdapter != null) {
                    Log.d("myLogs", String.valueOf(2));
                    boxAdapter.clearFilter();
                }
                return false;
            }
        });
    }
    public void a(String query) { // изменение списка аптек на основе запроса
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pharmacy-d6081-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference productsRef = database.getReference("Products");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Создаем список для хранения аптек, в которых есть нужные лекарства
                ArrayList<String> filteredPharmacies = new ArrayList<>();
                // Перебираем каждую аптеку
                for (DataSnapshot pharmacySnapshot : dataSnapshot.getChildren()) {
                    // Получаем список продуктов в текущей аптеке
                    List<String> productList = new ArrayList<>();
                    for (DataSnapshot productSnapshot : pharmacySnapshot.getChildren()) {
                        String name = productSnapshot.child("name").getValue(String.class);
                        productList.add(name);
                    }
                    // Проверяем, содержит ли аптека нужные лекарства
                    boolean containsMedicine = false;
                    for (String name : productList) {
                        int distance = LevenshteinDistance.getLevenshteinDistance(name.toLowerCase(), query.toLowerCase());
                        int maxSize = Integer.max(name.length(), query.length());
                        double pr = Double.valueOf(maxSize - distance) / maxSize; // процентаж схожести строк
                        if (pr >= 0.7) {
                            containsMedicine = true;
                            break;
                        }
                    }
                    // Если аптека содержит нужные лекарства, добавляем ее в список
                    if (containsMedicine) {
                        filteredPharmacies.add(pharmacySnapshot.getKey());
                    }
                }
                boxAdapter.updateData(filteredPharmacies);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    public static class LevenshteinDistance { // для определения шагов, необходимых, чтобы одна строка стала другой
        public static int getLevenshteinDistance(String X, String Y) {
            int m = X.length();
            int n = Y.length();
            int[][] T = new int[m + 1][n + 1];
            for (int i = 1; i <= m; i++) {
                T[i][0] = i;
            }
            for (int j = 1; j <= n; j++) {
                T[0][j] = j;
            }
            int cost;
            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0 : 1;
                    T[i][j] = Integer.min(Integer.min(T[i - 1][j - 1] + cost, T[i][j - 1] + 1),
                            T[i - 1][j] + 1);
                }
            }
            return T[m][n];
        }
    }
    @Override // Обработка нажатия на иконки в ActionBar
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override // переод на активность регистрации/входа
    public void IntentF() {
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(PREF_NAME, "");
        prefEditor.putString(PREF_ROLE, "");
        prefEditor.putString(PREF_UID, "");
        prefEditor.apply();
        Intent i = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}