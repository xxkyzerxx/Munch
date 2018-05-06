package com.crazyhitty.chdev.ks.munch.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazyhitty.chdev.ks.munch.R;
import com.crazyhitty.chdev.ks.munch.models.Categories;
import com.crazyhitty.chdev.ks.munch.models.FeedItem;
import com.crazyhitty.chdev.ks.munch.models.SettingsPreferences;
import com.crazyhitty.chdev.ks.munch.ui.activities.ArticleActivity;
import com.crazyhitty.chdev.ks.munch.utils.FadeAnimationUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kartik_ch on 11/5/2015.
 */
public class FeedsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<FeedItem> mFeedItems;
    private List<Integer> mColorDrawables = new ArrayList<>();
    private int mCurrentCircleBgId = -1;
    private int mLastPosition = -1;

    private NativeAdsManager mAds;
    private NativeAd mAd = null;
    private int POST_TYPE = 1;
    private int AD_TYPE = 2;
    public static int AD_FORM = 1;

    public FeedsRecyclerViewAdapter(Context mContext, List<FeedItem> mFeedItems, NativeAdsManager ads) {
        clear();
        this.mContext = mContext;
        this.mFeedItems = mFeedItems;
        mColorDrawables = getColors();
        mAds = ads;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return AD_TYPE;
        }
        else {
            return POST_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View inflatedView;
            if (AD_FORM == 2) {
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ad_unit2, parent, false);
            }
            else {
                inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ad_unit, parent, false);
            }
            return new AdHolder(inflatedView);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_small, parent, false);
            FeedsViewHolder viewHolder = new FeedsViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == AD_TYPE) {
            if (mAd != null) {
                ((AdHolder)holder).bindView(mAd);
            }
            else if (mAds != null && mAds.isLoaded()) {
                mAd = mAds.nextNativeAd();
                ((AdHolder)holder).bindView(mAd);
            }
            else {
                ((AdHolder)holder).bindView(null);
            }
        }
        else {
            int index = position;
            if (index != 0) {
                index--;
            }
            FeedsViewHolder holder1 = (FeedsViewHolder)holder;
            holder1.mTxtTitle.setText(mFeedItems.get(index).getItemTitle());
            holder1.mTxtSource.setText(mFeedItems.get(index).getItemSource());
            holder1.mTxtSourceUrl.setText(mFeedItems.get(index).getItemSourceUrl());
            holder1.mTxtCategory.setText(mFeedItems.get(index).getItemCategory());
            holder1.mTxtPubDate.setText(mFeedItems.get(index).getItemPubDate());
            //holder.mImgCategory.setImageResource(mFeedItems.get(position).getItemCategoryImgId());
            holder1.mImgCategory.setImageResource(new Categories(mContext).getDrawableId(mFeedItems.get(index).getItemCategory()));

            //get a randomized background resource id from a set of available drawables
            mCurrentCircleBgId = mColorDrawables.get(getRandomIndex(0, mColorDrawables.size() - 1));
            //set this drawable in feeditem
            mFeedItems.get(index).setItemBgId(mCurrentCircleBgId);
            //set the retrieved drawable into the category image view background
            holder1.mImgCategoryBg.setImageResource(mCurrentCircleBgId);

            //add fading animation as the items start loading
            if (SettingsPreferences.FEEDS_RECYCLER_VIEW_ANIMATION) {
                setAnimation(holder1.mItemView, index);
            }
        }


    }

    private void setAnimation(View view, int position) {
        if (position > mLastPosition) {
            FadeAnimationUtil fadeAnimationUtil = new FadeAnimationUtil(mContext);
            fadeAnimationUtil.fadeInAlpha(view, 500);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        if (mFeedItems == null) {
            return 0;
        }
        return mFeedItems.size() + 1;
    }

    //use to remove the sticky animation
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getItemViewType() != AD_TYPE) {
            ((FeedsViewHolder)holder).mItemView.clearAnimation();
        }
    }

    public void addItem(FeedItem feedItem, int position) {
        mFeedItems.add(position, feedItem);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mFeedItems.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if (this.mFeedItems != null) {
            this.mFeedItems.clear();
        }
    }

    private List<Integer> getColors() {
        List<Integer> colors = new ArrayList<>();
        colors.add(R.drawable.cyan_circle);
        colors.add(R.drawable.green_circle);
        colors.add(R.drawable.red_circle);
        colors.add(R.drawable.orange_circle);
        colors.add(R.drawable.lime_circle);
        colors.add(R.drawable.teal_circle);
        colors.add(R.drawable.purple_circle);
        colors.add(R.drawable.grey_circle);
        return colors;
    }

    private int getRandomIndex(int start, int end) {
        Random random = new Random();
        return random.nextInt(end - start) + start;
    }

    public class FeedsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTxtTitle, mTxtSource, mTxtSourceUrl, mTxtCategory, mTxtPubDate;
        private ImageView mImgCategory, mImgCategoryBg;
        private View mItemView;

        public FeedsViewHolder(View itemView) {
            super(itemView);
            mTxtTitle = (TextView) itemView.findViewById(R.id.text_view_feed_title);
            mTxtSource = (TextView) itemView.findViewById(R.id.text_view_feed_source);
            mTxtSourceUrl = (TextView) itemView.findViewById(R.id.text_view_feed_source_url);
            mTxtCategory = (TextView) itemView.findViewById(R.id.text_view_feed_category);
            mTxtPubDate = (TextView) itemView.findViewById(R.id.text_view_feed_pub_date);
            mImgCategory = (ImageView) itemView.findViewById(R.id.image_view_category);
            mImgCategoryBg = (ImageView) itemView.findViewById(R.id.image_view_category_bg);
            this.mItemView = itemView;
            itemView.setOnClickListener(this);
            setFontSize();
        }

        @Override
        public void onClick(View view) {
            Log.e("title", mFeedItems.get(getAdapterPosition()).getItemTitle());
            Log.e("link", mFeedItems.get(getAdapterPosition()).getItemLink());
            Log.e("img_url", mFeedItems.get(getAdapterPosition()).getItemImgUrl());
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(mContext, "lollipop+ material transition", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "below lollipop+ material transition", Toast.LENGTH_SHORT).show();
            }*/
            //redirect user to article activity
            Intent intent = new Intent(mContext, ArticleActivity.class);
            intent.putExtras(getFeedItemBundle(getAdapterPosition()));
            mContext.startActivity(intent);
        }

        private Bundle getFeedItemBundle(int position) {
            if (position != 0)
                position--;
            FeedItem feedItem = mFeedItems.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("title", feedItem.getItemTitle());
            bundle.putString("category", feedItem.getItemCategory());
            bundle.putString("description", feedItem.getItemDesc());
            bundle.putString("img_url", feedItem.getItemImgUrl());
            bundle.putInt("image_id", feedItem.getItemCategoryImgId());
            bundle.putString("link", feedItem.getItemLink());
            bundle.putString("pub_date", feedItem.getItemPubDate());
            bundle.putString("source", feedItem.getItemSource());
            bundle.putString("source_url", feedItem.getItemSourceUrl());
            bundle.putInt("category_bg_img_id", feedItem.getItemBgId());
            bundle.putString("article_content", feedItem.getItemWebDesc());
            bundle.putString("sync_desc", feedItem.getItemWebDesc());
            return bundle;
        }

        private void setFontSize() {
            //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
            mTxtSource.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.SOURCE_NAME_SIZE);
            mTxtCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.SOURCE_CATEGORY_SIZE);
            mTxtSourceUrl.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.SOURCE_URL_SIZE);
            mTxtPubDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.FEED_PUBLISH_DATE_SIZE);
            mTxtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.FEED_TITLE_SIZE);
        }

    }

    public static class AdHolder extends RecyclerView.ViewHolder {
        private MediaView mAdMedia;
        private ImageView mAdIcon;
        private TextView mAdTitle;
        private TextView mAdBody;
        private TextView mAdSocialContext;
        private Button mAdCallToAction;

        public AdHolder(View view) {
            super(view);

            if (AD_FORM == 2) {
                mAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
                mAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
                mAdCallToAction = (Button)view.findViewById(R.id.native_ad_call_to_action);
            }
            else {
                mAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
                mAdTitle = (TextView) view.findViewById(R.id.native_ad_title);
                mAdBody = (TextView) view.findViewById(R.id.native_ad_body);
                mAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
                mAdCallToAction = (Button)view.findViewById(R.id.native_ad_call_to_action);
                mAdIcon = (ImageView)view.findViewById(R.id.native_ad_icon);

            }
        }

        public void bindView(NativeAd ad) {
            if (ad == null) {
                if (AD_FORM == 2) {
                    mAdSocialContext.setText("No Ad");
                }
                else {
                    mAdTitle.setText("No Ad");
                    mAdBody.setText("Ad is not loaded.");
                }
            }
            else {
                if (AD_FORM == 2) {
                    mAdSocialContext.setText(ad.getAdSocialContext());
                    mAdCallToAction.setText(ad.getAdCallToAction());
                    mAdMedia.setNativeAd(ad);
                }
                else {
                    mAdTitle.setText(ad.getAdTitle());
                    mAdBody.setText(ad.getAdBody());
                    mAdSocialContext.setText(ad.getAdSocialContext());
                    mAdCallToAction.setText(ad.getAdCallToAction());
                    mAdMedia.setNativeAd(ad);
                    NativeAd.Image adIcon = ad.getAdIcon();
                    NativeAd.downloadAndDisplayImage(adIcon, mAdIcon);

                    // Register the Title and CTA button to listen for clicks.
                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(mAdTitle);
                    clickableViews.add(mAdCallToAction);
                    ad.registerViewForInteraction(this.itemView,clickableViews);
                }
            }

            ad.setAdListener(new AdListener() {

                @Override
                public void onError(Ad ad, AdError error) {
                    // Ad error callback
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Ad loaded callback
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                }
            });
        }

    }
}
