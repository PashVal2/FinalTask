package com.example.finaltask;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import org.checkerframework.checker.index.qual.LengthOf;

import java.util.ArrayList;
public class BoxAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Pharm> objects = new ArrayList<Pharm>();
    private ArrayList<Pharm> objectsAll = new ArrayList<Pharm>();
    boolean flag;
    public BoxAdapter(Context context, ArrayList<Pharm> products, boolean flag) {
        ctx = context;
        objects = products;
        objectsAll = new ArrayList<>(products);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.flag = flag;
    }
    @Override
    public int getCount() {
        return objects.size();
    }
    @Override
    public Object getItem(int position) { return objects.get(position); }
    @Override
    public long getItemId(int position) { return position; }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) { view = lInflater.inflate(R.layout.item, parent, false); }
        Pharm p = getProduct(position);
        ((TextView) view.findViewById(R.id.name)).setText(p.name);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(p.imageUrl, 120, 120, true);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageBitmap(scaledBitmap);
        return view;
    }
    Pharm getProduct(int position) { return (Pharm) getItem(position); }
    ArrayList<Pharm> getBox() {
        ArrayList<Pharm> box = new ArrayList<>();
        for (Pharm p : objects) {
            if (p.box) {
                box.add(p);
            }
        }
        return box;
    }
    // Метод для изменения списка данных
    public void updateData(ArrayList<String> newNames) {
        ArrayList<Pharm> newPharms = new ArrayList<>();
        // Перебираем новый список названий аптек
        for (String name : newNames) {
            // Поиск объекта Pharm с соответствующим названием аптеки
            for (Pharm pharm : objectsAll) {
                if (pharm.getName().equals(name)) {
                    newPharms.add(pharm);
                    break;
                }
            }
        }
        // Обновляем данные в адаптере
        objects.clear();
        objects.addAll(newPharms);
        notifyDataSetChanged();
    }
    public void clearFilter() {
        objects.clear();
        objects.addAll(objectsAll);
        notifyDataSetChanged();
    }
}
