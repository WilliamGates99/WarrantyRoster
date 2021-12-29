package com.xeniac.warrantyroster_manager.mainactivity.warrantiesfragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xeniac.warrantyroster_manager.Constants;
import com.xeniac.warrantyroster_manager.R;
import com.xeniac.warrantyroster_manager.database.WarrantyRosterDatabase;
import com.xeniac.warrantyroster_manager.databinding.ListAdContainerBinding;
import com.xeniac.warrantyroster_manager.databinding.ListWarrantyBinding;
import com.xeniac.warrantyroster_manager.model.ListItemType;
import com.xeniac.warrantyroster_manager.model.Warranty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ir.tapsell.plus.AdHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;

public class WarrantyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity activity;
    private final Context context;
    private final WarrantyRosterDatabase database;
    private final List<Warranty> warrantyList;
    private final WarrantyListClickInterface warrantyListClickInterface;

    private static final int VIEW_TYPE_WARRANTY = 0;
    private static final int VIEW_TYPE_AD = 1;

    private int requestAdCounter;

    public WarrantyAdapter(Activity activity, Context context, WarrantyRosterDatabase database,
                           List<Warranty> warrantyList,
                           WarrantyListClickInterface warrantyListClickInterface) {
        this.activity = activity;
        this.context = context;
        this.database = database;
        this.warrantyList = warrantyList;
        this.warrantyListClickInterface = warrantyListClickInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_WARRANTY) {
            ListWarrantyBinding warrantyBinding = ListWarrantyBinding.inflate(inflater, parent, false);
            return new WarrantyViewHolder(warrantyBinding);
        } else {
            ListAdContainerBinding adContainerBinding = ListAdContainerBinding.inflate(inflater, parent, false);
            return new AdViewHolder(adContainerBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (warrantyList.get(position).getItemType() == ListItemType.WARRANTY) {
            return VIEW_TYPE_WARRANTY;
        } else {
            return VIEW_TYPE_AD;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_WARRANTY) {
            ((WarrantyViewHolder) holder).bindView(context, warrantyList.get(position));
        } else {
            ((AdViewHolder) holder).bindView();
        }
    }

    @Override
    public int getItemCount() {
        return warrantyList.size();
    }

    class WarrantyViewHolder extends RecyclerView.ViewHolder {

        private final ListWarrantyBinding warrantyBinding;

        public WarrantyViewHolder(@NonNull ListWarrantyBinding warrantyBinding) {
            super(warrantyBinding.getRoot());
            this.warrantyBinding = warrantyBinding;
        }

        public void bindView(Context context, Warranty warrantyItem) {
            warrantyBinding.tvTitle.setText(warrantyItem.getTitle());
            warrantyBinding.tvCategory.setText(context.getResources().getString(
                    database.categoryDAO().getCategoryById(warrantyItem.getCategoryId()).getTitle()));
            warrantyBinding.ivIcon.setImageResource(
                    database.categoryDAO().getCategoryById(warrantyItem.getCategoryId()).getIcon());

            Calendar expiryCalendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
            try {
                expiryCalendar.setTime(Objects.requireNonNull(dateFormat.parse(warrantyItem.getExpiryDate())));
                warrantyBinding.tvExpiryDate.setText(String.format(("%s %s, %s"),
                        expiryCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                        getDayWithSuffix(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                        expiryCalendar.get(Calendar.YEAR)));

                long daysUntilExpiry = getDaysUntilExpiry(expiryCalendar);
                if (daysUntilExpiry < 0) {
                    warrantyBinding.tvStatus.setText(context.getResources().getString(R.string.warranties_list_status_expired));
                    warrantyBinding.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                    warrantyBinding.flStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
                } else if (daysUntilExpiry <= 30) {
                    warrantyBinding.tvStatus.setText(context.getResources().getString(R.string.warranties_list_status_soon));
                    warrantyBinding.tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
                    warrantyBinding.flStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));
                } else {
                    warrantyBinding.tvStatus.setText(context.getResources().getString(R.string.warranties_list_status_valid));
                    warrantyBinding.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                    warrantyBinding.flStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
                }

                warrantyBinding.cvWarranty.setOnClickListener(view ->
                        warrantyListClickInterface.onItemClick(warrantyItem, daysUntilExpiry));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("warrantyAdapter", "ParseException: " + e.getMessage());
            }
        }
    }

    private String getDayWithSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }

        switch (day % 10) {
            case 1:
                return day + "st";
            case 2:
                return day + "nd";
            case 3:
                return day + "rd";
            default:
                return day + "th";
        }
    }

    private long getDaysUntilExpiry(Calendar expiryCalendar) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

        return TimeUnit.MILLISECONDS.toDays(expiryCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis());
    }

    class AdViewHolder extends RecyclerView.ViewHolder {

        private final AdHolder adHolder;

        public AdViewHolder(@NonNull ListAdContainerBinding adContainerBinding) {
            super(adContainerBinding.getRoot());
            adHolder = TapsellPlus.createAdHolder(activity,
                    adContainerBinding.cvAdContainer, R.layout.list_ad_banner);
        }

        void bindView() {
            requestAdCounter = 0;
            requestNativeAd(adHolder);
        }
    }

    private void requestNativeAd(AdHolder adHolder) {
        TapsellPlus.requestNativeAd(activity, Constants.WARRANTIES_NATIVE_ZONE_ID,
                new AdRequestCallback() {
                    @Override
                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel);
                        Log.i("requestNativeAd", "response: " + tapsellPlusAdModel.toString());
                        TapsellPlus.showNativeAd(activity, tapsellPlusAdModel.getResponseId(),
                                adHolder, new AdShowListener() {
                                    @Override
                                    public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                                        super.onOpened(tapsellPlusAdModel);
                                    }

                                    @Override
                                    public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                                        super.onError(tapsellPlusErrorModel);
                                    }
                                });
                    }

                    @Override
                    public void error(String s) {
                        super.error(s);
                        Log.e("requestNativeAd", "error: " + s);
                        if (requestAdCounter < 3) {
                            requestAdCounter++;
                            requestNativeAd(adHolder);
                        }
                    }
                });
    }
}