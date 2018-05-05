package com.crazyhitty.chdev.ks.munch.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.crazyhitty.chdev.ks.munch.R;
import com.crazyhitty.chdev.ks.munch.models.CategoryItem;
import com.crazyhitty.chdev.ks.munch.models.SettingsPreferences;

import java.util.List;
import com.afollestad.materialdialogs.internal.MDAdapter;
/**
 * Created by Kartik_ch on 11/11/2015.
 */
public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ImageHolder> implements  MDAdapter {

    private MaterialDialog dialog;
    private TextView mTxtCategory;
    private ImageView mImgCategory;
    private static List<CategoryItem> mCategoryItems;
    LinearLayout categoryLayout;
    public CategoryListAdapter(Context mContext, List<CategoryItem> mCategoryItems, TextView mTxtCategory, ImageView mImgCategory, LinearLayout categoryLayout) {
        this.mTxtCategory = mTxtCategory;
        this.mImgCategory = mImgCategory;
        this.mCategoryItems = mCategoryItems;
        this.categoryLayout = categoryLayout;
    }

    @Override
    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        return new ImageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryListAdapter.ImageHolder holder, final int position) {
        holder.txtCategoryName.setText(mCategoryItems.get(position).getCategoryName());
        holder.imgCategory.setImageDrawable(mCategoryItems.get(position).getCategoryImg());

        //add a white color filter to the images if dark theme is selected
        if (!SettingsPreferences.THEME) {
            holder.txtCategoryName.setTextColor(ContextCompat.getColor(holder.mContext, R.color.md_grey_100));
            holder.imgCategory.setColorFilter(ContextCompat.getColor(holder.mContext, R.color.md_grey_100));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgCategory.setImageDrawable(mCategoryItems.get(position).getCategoryImg());
                mTxtCategory.setText(mCategoryItems.get(position).getCategoryName());
                if (categoryLayout != null)
                    categoryLayout.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mCategoryItems.size();
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        public TextView txtCategoryName;
        public ImageView imgCategory;


        public ImageHolder(View itemView){
            super(itemView);
            mContext = itemView.getContext();
            txtCategoryName = (TextView) itemView.findViewById(R.id.text_view_category);
            imgCategory = (ImageView) itemView.findViewById(R.id.image_view_category);


        }
    }
}
