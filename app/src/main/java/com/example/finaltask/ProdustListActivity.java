package com.example.finaltask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.finaltask.databinding.ActivityProdustListBinding;
import com.example.finaltask.databinding.ActivitySecondBinding;
import com.google.android.material.snackbar.Snackbar;
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
public class ProdustListActivity extends AppCompatActivity {
    ActivityProdustListBinding binding;
    BoxAdapterForProduct boxAdapter;
    private static final String PREF_ROLE = "Role";
    private static final String PREFS_FILE = "Account";
    String title;
    SharedPreferences settings;
    String role = "";
    Comparator<Product> comparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            return o1.name.compareTo(o2.name);
        }
    };
    ArrayList<Product> products = new ArrayList<Product>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProdustListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        getRole();
        title = getIntent().getStringExtra("name");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Лекарства"); // Установить заголовок
        actionBar.setSubtitle("Аптека " + title); // Установить подзаголовок
        ListView lvMain = (ListView) findViewById(R.id.lvMain1);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pharmacy-d6081.appspot.com/");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pharmacy-d6081-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference productsReference = database.getReference("Products/");
        DatabaseReference tableReference = productsReference.child(title);
        tableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    if (productSnapshot.exists()) {
                        String productName = productSnapshot.child("name").getValue(String.class);
                        int productCost = productSnapshot.child("cost").getValue(Integer.class);
                        int productInd = productSnapshot.child("ind").getValue(Integer.class);
                        String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                        // Загрузка изображения из Firebase Storage
                        StorageReference imageRef = storage.getReference(imageUrl);
                        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            // Успешно загруженные байты изображения
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Product product = new Product(productName, productInd, productCost, bitmap, false); // Создание объекта Product с учетом загруженного изображения
                            products.add(product);
                            // Обновление пользовательского интерфейса после загрузки всех изображений
                            if (products.size() == dataSnapshot.getChildrenCount()) {
                                Collections.sort(products, comparator);
                                boxAdapter = new BoxAdapterForProduct(ProdustListActivity.this, products, false);
                                lvMain.setAdapter(boxAdapter);
                            }
                        }).addOnFailureListener(exception -> {
                            // Обработка ошибок загрузки изображения
                            Log.e("TAG", "Failed to load image from Firebase Storage", exception);
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        binding.lvMain1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получаем ссылку на элемент списка, который был нажат
                Product item = (Product) parent.getAdapter().getItem(position);
                Intent i = new Intent(ProdustListActivity.this, ProductActivity.class);
                i.putExtra("name", item.name);
                i.putExtra("phar_name", title);
                i.putExtra("id", item.ind);

                startActivity(i);
            }
        });
        initView();
    }
    public void initView() {
        if (!"employee".equals(role)) {
            binding.fab.setVisibility(View.INVISIBLE);
        }
    }
    public void onClick(View view) {
        Intent i = new Intent(ProdustListActivity.this, AddNewProductActivity.class);
        i.putExtra("name", title);
        startActivity(i);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            startActivityAfterCleanup(SecondActivity.class);
            return true;
        }
        else if (menuItem.getItemId() == R.id.item1) {
            if (!title.isEmpty()) {
                // Формируем URI для поиска местоположения по ключевому слову
                Uri uri = Uri.parse("geo:0,0?q=" + title);
                // Создаем интент с действием ACTION_VIEW и передаем URI
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // Устанавливаем флаги для установки метки и увеличения
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Проверяем, есть ли приложение для отображения карт на устройстве
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Запускаем картовое приложение
                    startActivity(intent);
                } else {
                    // Показываем сообщение, если нет приложения для отображения карт
                    showToast("Нет приложения для отображения карт");
                }
            } else {
                // Показываем сообщение, если не введено ключевое слово
                showToast("Введите ключевое слово");
            }
        }
        return (super.onOptionsItemSelected(menuItem));
    }
    private void startActivityAfterCleanup(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void showToast(String message) {
        Snackbar.make(binding.getRoot(), message,
                Snackbar.LENGTH_SHORT).show();
    }
    public void getRole() {
        role = settings.getString(PREF_ROLE, "Не определено");
    }
}