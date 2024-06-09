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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
public class BoxAdapterForProduct extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Product> objects;
    boolean flag;
    public BoxAdapterForProduct(Context context, ArrayList<Product> products, boolean flag) {
        ctx = context;
        objects = products;
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
        if (view == null) { view = lInflater.inflate(R.layout.item_product, parent, false); }
        Product p = getProduct(position);
        ((TextView) view.findViewById(R.id.namePr)).setText(p.name);
        ((TextView) view.findViewById(R.id.ind)).setText(String.valueOf(p.ind));
        ((TextView) view.findViewById(R.id.price)).setText(String.valueOf(p.price));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(p.imageUrl, 120, 120, true);
        ((ImageView) view.findViewById(R.id.ivImagePr)).setImageBitmap(scaledBitmap);
        return view;
    }
    Product getProduct(int position) { return (Product) getItem(position); }
}


