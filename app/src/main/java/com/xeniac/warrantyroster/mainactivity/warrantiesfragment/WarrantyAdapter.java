package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.ListWarrantyBinding;

import java.util.List;

public class WarrantyAdapter extends RecyclerView.Adapter<WarrantyAdapter.ViewHolder> {

    private final Context context;
    private final List<WarrantyDataModel> warrantyList;
    private final WarrantyListClickInterface warrantyListClickInterface;

    public WarrantyAdapter(Context context, List<WarrantyDataModel> warrantyList,
                           WarrantyListClickInterface warrantyListClickInterface) {
        this.context = context;
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
            warrantyBinding.tvListWarrantyExpiryDate.setText(warrantyItem.getExpiryDate());
            warrantyBinding.ivListWarrantyIcon.setImageDrawable(ResourcesCompat.getDrawable(
                    context.getResources(), warrantyItem.getIcon(), context.getTheme()));
            warrantyBinding.tvListWarrantyCategory.setText(warrantyItem.getCategory());

            switch (warrantyItem.getStatus()) {
                case -1:
                    warrantyBinding.tvListWarrantyStatus.setText(context.getResources().getString(R.string.warranties_list_status_expired));
                    warrantyBinding.tvListWarrantyStatus.setTextColor(context.getResources().getColor(R.color.red));
                    warrantyBinding.flListWarrantyStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
                    break;
                case 0:
                    warrantyBinding.tvListWarrantyStatus.setText(context.getResources().getString(R.string.warranties_list_status_soon));
                    warrantyBinding.tvListWarrantyStatus.setTextColor(context.getResources().getColor(R.color.orange));
                    warrantyBinding.flListWarrantyStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));
                    break;
                case 1:
                    warrantyBinding.tvListWarrantyStatus.setText(context.getResources().getString(R.string.warranties_list_status_valid));
                    warrantyBinding.tvListWarrantyStatus.setTextColor(context.getResources().getColor(R.color.green));
                    warrantyBinding.flListWarrantyStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
                    break;
            }

            warrantyBinding.cvListWarranty.setOnClickListener(view ->
                    warrantyListClickInterface.onItemClick(getAdapterPosition()));
        }
    }
}