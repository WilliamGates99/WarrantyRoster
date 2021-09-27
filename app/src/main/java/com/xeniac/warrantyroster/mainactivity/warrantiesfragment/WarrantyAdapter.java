package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.database.WarrantyRosterDatabase;
import com.xeniac.warrantyroster.databinding.ListWarrantyBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WarrantyAdapter extends RecyclerView.Adapter<WarrantyAdapter.ViewHolder> {

    private final Context context;
    private final WarrantyRosterDatabase database;
    private final List<WarrantyDataModel> warrantyList;
    private final WarrantyListClickInterface warrantyListClickInterface;
    private long daysUntilExpiry;

    public WarrantyAdapter(Context context, WarrantyRosterDatabase database,
                           List<WarrantyDataModel> warrantyList,
                           WarrantyListClickInterface warrantyListClickInterface) {
        this.context = context;
        this.database = database;
        this.warrantyList = warrantyList;
        this.warrantyListClickInterface = warrantyListClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListWarrantyBinding warrantyBinding = ListWarrantyBinding.inflate(inflater, parent, false);
        return new ViewHolder(warrantyBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(context, warrantyList.get(position));
    }

    @Override
    public int getItemCount() {
        return warrantyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ListWarrantyBinding warrantyBinding;

        public ViewHolder(@NonNull ListWarrantyBinding warrantyBinding) {
            super(warrantyBinding.getRoot());
            this.warrantyBinding = warrantyBinding;
        }

        public void bindView(Context context, WarrantyDataModel warrantyItem) {
            warrantyBinding.tvListWarrantyTitle.setText(warrantyItem.getTitle());
            warrantyBinding.tvListWarrantyCategory.setText(context.getResources().getString(
                    database.categoryDAO().getCategoryById(warrantyItem.getCategoryId()).getTitle()));
            warrantyBinding.ivListWarrantyIcon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                    database.categoryDAO().getCategoryById(warrantyItem.getCategoryId()).getIcon(), context.getTheme()));

            Calendar expiryCalendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
            try {
                expiryCalendar.setTime(Objects.requireNonNull(dateFormat.parse(warrantyItem.getExpiryDate())));
                warrantyBinding.tvListWarrantyExpiryDate.setText(String.format(("%s %s, %s"),
                        expiryCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                        getDayWithSuffix(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                        expiryCalendar.get(Calendar.YEAR)));

                daysUntilExpiry = getDaysUntilExpiry(expiryCalendar);
                if (daysUntilExpiry < 0) {
                    warrantyBinding.tvListWarrantyStatus.setText(context.getResources().getString(R.string.warranties_list_status_expired));
                    warrantyBinding.tvListWarrantyStatus.setTextColor(context.getResources().getColor(R.color.red));
                    warrantyBinding.flListWarrantyStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
                } else if (daysUntilExpiry <= 30) {
                    warrantyBinding.tvListWarrantyStatus.setText(context.getResources().getString(R.string.warranties_list_status_soon));
                    warrantyBinding.tvListWarrantyStatus.setTextColor(context.getResources().getColor(R.color.orange));
                    warrantyBinding.flListWarrantyStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));
                } else {
                    warrantyBinding.tvListWarrantyStatus.setText(context.getResources().getString(R.string.warranties_list_status_valid));
                    warrantyBinding.tvListWarrantyStatus.setTextColor(context.getResources().getColor(R.color.green));
                    warrantyBinding.flListWarrantyStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("warrantyAdapter", "ParseException: " + e.getMessage());
            }

            warrantyBinding.cvListWarranty.setOnClickListener(view ->
                    warrantyListClickInterface.onItemClick(warrantyItem, daysUntilExpiry));
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
}