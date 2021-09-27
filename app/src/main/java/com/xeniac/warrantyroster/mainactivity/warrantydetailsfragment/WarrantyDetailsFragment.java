package com.xeniac.warrantyroster.mainactivity.warrantydetailsfragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.database.WarrantyRosterDatabase;
import com.xeniac.warrantyroster.databinding.FragmentWarrantyDetailsBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;
import com.xeniac.warrantyroster.mainactivity.warrantiesfragment.WarrantyDataModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class WarrantyDetailsFragment extends Fragment {

    private FragmentWarrantyDetailsBinding warrantyDetailsBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;
    private WarrantyRosterDatabase database;
    private WarrantyDataModel warranty;

    public WarrantyDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        warrantyDetailsBinding = FragmentWarrantyDetailsBinding.inflate(inflater, container, false);
        view = warrantyDetailsBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WarrantyRosterDatabase.destroyInstance();
        warrantyDetailsBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
        database = WarrantyRosterDatabase.getInstance(context);
        ((MainActivity) context).hideNavBar();

        returnToMainActivity();
        handleExtendedFAB();
        getWarranty();
        fabOnClick();
        removeWarrantyOnClick();
    }

    private void returnToMainActivity() {
        warrantyDetailsBinding.toolbarWarrantyDetails.setNavigationOnClickListener(view ->
                activity.onBackPressed());
    }

    private void handleExtendedFAB() {
        warrantyDetailsBinding.svWarrantyDetails.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > oldScrollY) {
                        warrantyDetailsBinding.fabWarrantyDetails.shrink();
                    } else {
                        warrantyDetailsBinding.fabWarrantyDetails.extend();
                    }
                });
    }

    private void getWarranty() {
        if (getArguments() != null) {
            WarrantyDetailsFragmentArgs args = WarrantyDetailsFragmentArgs.fromBundle(getArguments());
            warranty = args.getWarranty();
            long daysUntilExpiry = args.getDaysUntilExpiry();
            setWarrantyDetails(daysUntilExpiry);
        }
    }

    private void setWarrantyDetails(long daysUntilExpiry) {
        warrantyDetailsBinding.toolbarWarrantyDetails.setTitle(warranty.getTitle());
        warrantyDetailsBinding.tvWarrantyDetailsBrand.setText(warranty.getBrand());
        warrantyDetailsBinding.tvWarrantyDetailsModel.setText(warranty.getModel());
        warrantyDetailsBinding.tvWarrantyDetailsSerial.setText(warranty.getSerialNumber());
        warrantyDetailsBinding.tvWarrantyDetailsDescription.setText(warranty.getDescription());
        warrantyDetailsBinding.ivWarrantyDetailsIcon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                database.categoryDAO().getCategoryById(warranty.getCategoryId()).getIcon(), context.getTheme()));

        Calendar startingCalendar = Calendar.getInstance();
        Calendar expiryCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
        DecimalFormat decimalFormat = new DecimalFormat("00");

        try {
            startingCalendar.setTime(Objects.requireNonNull(dateFormat.parse(warranty.getStartingDate())));
            expiryCalendar.setTime(Objects.requireNonNull(dateFormat.parse(warranty.getExpiryDate())));

            warrantyDetailsBinding.tvWarrantyDetailsDateStarting.setText(String.format(("%s/%s/%s"),
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
                    startingCalendar.get(Calendar.YEAR)));

            warrantyDetailsBinding.tvWarrantyDetailsDateExpiry.setText(String.format(("%s/%s/%s"),
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
                    expiryCalendar.get(Calendar.YEAR)));

            if (daysUntilExpiry < 0) {
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setText(context.getResources().getString(R.string.warranty_details_status_expired));
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setTextColor(context.getResources().getColor(R.color.red));
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red20)));
            } else if (daysUntilExpiry <= 30) {
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setText(context.getResources().getString(R.string.warranty_details_status_soon));
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setTextColor(context.getResources().getColor(R.color.orange));
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange20)));
            } else {
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setText(context.getResources().getString(R.string.warranty_details_status_valid));
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setTextColor(context.getResources().getColor(R.color.green));
                warrantyDetailsBinding.tvWarrantyDetailsStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green20)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("setWarrantyDetails", "ParseException: " + e.getMessage());
        }
    }

    private void fabOnClick() {
        warrantyDetailsBinding.fabWarrantyDetails.setOnClickListener(view ->
                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show());
    }

    private void removeWarrantyOnClick() {
        warrantyDetailsBinding.toolbarWarrantyDetails.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            removeWarranty();
            return false;
        });
    }

    private void removeWarranty() {
        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
    }
}