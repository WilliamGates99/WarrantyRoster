package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import android.content.Context;
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

//            switch (warrantyItem.getStatus()) {
//                case "Valid":
//                    warrantyBinding.ivListWarrantyIcon.setBackgroundColor(context.getResources().getColor(R.color.green));
//                    break;
//                case "Soon":
//                    warrantyBinding.ivListWarrantyIcon.setBackgroundColor(context.getResources().getColor(R.color.orange));
//                    break;
//                case "Expired":
//                    warrantyBinding.ivListWarrantyIcon.setBackgroundColor(context.getResources().getColor(R.color.red));
//                    break;
//            }

            warrantyBinding.cvListWarranty.setOnClickListener(view ->
                    warrantyListClickInterface.onItemClick(getAdapterPosition()));
        }
    }
}