package com.xeniac.warrantyroster.mainactivity.warrantydetailsfragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
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

import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.TapsellPlusInitListener;
import ir.tapsell.plus.model.AdNetworkError;
import ir.tapsell.plus.model.AdNetworks;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;

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
        adInit();
        editWarrantyOnClick();
        deleteWarrantyOnClick();
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

    private void adInit() {
        TapsellPlus.initialize(context, Constants.TAPSELL_KEY, new TapsellPlusInitListener() {
            @Override
            public void onInitializeSuccess(AdNetworks adNetworks) {
                Log.i("adInit", "onInitializeSuccess: " + adNetworks.name());
            }

            @Override
            public void onInitializeFailed(AdNetworks adNetworks, AdNetworkError adNetworkError) {
                Log.e("adInit", "onInitializeFailed: " + adNetworks.name() + ", error: " + adNetworkError.getErrorMessage());
            }
        });
    }

    private void editWarrantyOnClick() {
        warrantyDetailsBinding.fabWarrantyDetails.setOnClickListener(view -> {
            WarrantyDetailsFragmentDirections.ActionWarrantyDetailsFragmentToEditWarrantyFragment action =
                    WarrantyDetailsFragmentDirections.actionWarrantyDetailsFragmentToEditWarrantyFragment(warranty);
            navController.navigate(action);
        });
    }

    private void deleteWarrantyOnClick() {
        warrantyDetailsBinding.toolbarWarrantyDetails.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            deleteWarranty();
            return false;
        });
    }

    private void deleteWarranty() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            showDeleteWarrantyDialog();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> deleteWarranty()).show();
        }
    }

    private void showDeleteWarrantyDialog() {
        MaterialAlertDialogBuilder deleteWarrantyDialogBuilder = new MaterialAlertDialogBuilder(context)
                .setTitle(context.getResources().getString(R.string.warranty_details_delete_dialog_title))
                .setMessage(String.format(context.getResources().getString(R.string.warranty_details_delete_dialog_message), warranty.getTitle()))
                .setPositiveButton(context.getResources().getString(R.string.warranty_details_delete_dialog_positive), (dialogInterface, i) ->
                        deleteWarrantyMutation())
                .setNegativeButton(context.getResources().getString(R.string.warranty_details_delete_dialog_negative), (dialogInterface, i) -> {
                });
        deleteWarrantyDialogBuilder.show();
    }

    private void deleteWarrantyMutation() {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new DeleteWarrantyMutation(warranty.getId()))
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<DeleteWarrantyMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<DeleteWarrantyMutation.Data> response) {
                        if (warrantyDetailsBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("deleteWarranty", "onResponse: " + response);
                                    Toast.makeText(context, String.format(
                                            context.getResources().getString(R.string.warranty_details_delete_success),
                                            warranty.getTitle()), Toast.LENGTH_LONG).show();
                                    requestInterstitialAd();
                                    activity.onBackPressed();
                                } else {
                                    Log.e("deleteWarranty", "onResponse Errors: " + response.getErrors());
                                    Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                            BaseTransientBottomBar.LENGTH_INDEFINITE)
                                            .setAction(context.getResources().getString(R.string.network_error_retry), v -> deleteWarranty()).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("deleteWarranty", "onFailure: " + e.getMessage());
                        if (warrantyDetailsBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();
                                Snackbar.make(view, context.getResources().getString(R.string.network_error_failure),
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                            });
                        }
                    }
                });
    }

    private void showLoadingAnimation() {
        warrantyDetailsBinding.toolbarWarrantyDetails.getMenu().getItem(0).setVisible(false);
        warrantyDetailsBinding.fabWarrantyDetails.setClickable(false);
        warrantyDetailsBinding.cpiWarrantyDetails.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        warrantyDetailsBinding.toolbarWarrantyDetails.getMenu().getItem(0).setVisible(true);
        warrantyDetailsBinding.fabWarrantyDetails.setClickable(true);
        warrantyDetailsBinding.cpiWarrantyDetails.setVisibility(View.GONE);
    }

    private void requestInterstitialAd() {
        TapsellPlus.requestInterstitialAd(activity, Constants.DELETE_WARRANTY_Interstitial_ZONE_ID,
                new AdRequestCallback() {
                    @Override
                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel);
                        Log.i("requestInterstitialAd", "response: " + tapsellPlusAdModel.toString());
                        showInterstitialAd(tapsellPlusAdModel.getResponseId());
                    }

                    @Override
                    public void error(String s) {
                        super.error(s);
                        Log.e("requestInterstitialAd", "error: " + s);
                    }
                });
    }

    private void showInterstitialAd(String responseId) {
        TapsellPlus.showInterstitialAd(activity, responseId, new AdShowListener() {
            @Override
            public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                super.onOpened(tapsellPlusAdModel);
            }

            @Override
            public void onClosed(TapsellPlusAdModel tapsellPlusAdModel) {
                super.onClosed(tapsellPlusAdModel);
            }

            @Override
            public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                super.onError(tapsellPlusErrorModel);
            }
        });
    }
}