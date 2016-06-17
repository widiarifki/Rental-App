package com.widiarifki.outdoorrent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.widiarifki.outdoorrent.R;
import com.widiarifki.outdoorrent.model.Product;

/**
 * Created by Widia Rifkianti on 15/06/2016.
 */
public class ProductAdapter extends BaseAdapter {
    private Context mContext;
    private Product[] mProducts;

    public ProductAdapter(Context context, Product[] products){
        mContext = context;
        mProducts = products;
    }

    @Override
    public int getCount() {
        return mProducts.length;
    }

    @Override
    public Object getItem(int position) {
        return mProducts[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            // brand new item
            convertView = LayoutInflater.from(mContext).inflate(R.layout.product_list_item, null);
            holder = new ViewHolder();
            holder.productName = (TextView) convertView.findViewById(R.id.list_product_name_label);
            holder.productCharge = (TextView) convertView.findViewById(R.id.list_product_charge_label);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = mProducts[position];

        holder.productName.setText(product.getName());
        holder.productCharge.setText(String.valueOf(product.getCharge()));

        return convertView;
    }

    private static class ViewHolder {
        TextView productName;
        TextView productCharge;
    }
}