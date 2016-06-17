package com.widiarifki.outdoorrent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.widiarifki.outdoorrent.R;
import com.widiarifki.outdoorrent.model.Product;

/**
 * Created by Widia Rifkianti on 16/06/2016.
 */
public class ProductAdapterRecycle extends RecyclerView.Adapter<ProductAdapterRecycle.ProductViewHolder>{

    private Context mContext;
    private Product[] mProducts;

    public ProductAdapterRecycle(Context context, Product[] products){
        mContext = context;
        mProducts = products;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        ProductViewHolder viewHolder = new ProductViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.bindProduct(mProducts[position]);
    }

    @Override
    public int getItemCount() {
        return mProducts.length;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        public TextView mProductNameLabel;
        public TextView mProductChargeLabel;
        public ImageView mProductImage;

        public ProductViewHolder(View itemView){
            super(itemView);

            mProductNameLabel = (TextView) itemView.findViewById(R.id.list_product_name_label);
            mProductChargeLabel = (TextView) itemView.findViewById(R.id.list_product_charge_label);
            mProductImage = (ImageView) itemView.findViewById(R.id.list_product_image);
        }

        public void bindProduct(Product product){
            mProductNameLabel.setText(product.getName());
            mProductChargeLabel.setText(product.getCharge()+"");
            Picasso.with(mContext).load(product.getImageUrl()).into(mProductImage);
        }
    }
}
