package com.xeniac.warrantyroster.mainactivity.setingsfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieDrawable;
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
import com.xeniac.warrantyroster.databinding.FragmentSettingsBinding;
import com.xeniac.warrantyroster.landingactivity.LandingActivity;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import ir.tapsell.plus.AdHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.TapsellPlusInitListener;
import ir.tapsell.plus.model.AdNetworkError;
import ir.tapsell.plus.model.AdNetworks;
import ir.tapsell.plus.model.TapsellPlusAdModel;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding settingsBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;

    private SharedPreferences settingsPrefs;
    private String currentLanguage;
    private String currentCountry;
    private int currentTheme;
    private String accountEmail;
    private ColorStateList verifyEmailBackgroundTint;

    private int requestAdCounter;
    private String responseId;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        view = settingsBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyAd();
        settingsBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
        ((MainActivity) context).showNavBar();

        settingsPrefs = context.getSharedPreferences(Constants.PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
        currentLanguage = settingsPrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en");
        currentCountry = settingsPrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US");
        currentTheme = settingsPrefs.getInt(Constants.PREFERENCE_THEME_KEY, 0);

        getAccountDetails();
        setCurrentLanguageText();
        setCurrentThemeText();
        verifyOnClick();
        changeEmailOnClick();
        changePasswordOnClick();
        languageOnClick();
        themeOnClick();
        privacyPolicyOnClick();
        logoutOnClick();
        adInit();
    }

    private void getAccountDetails() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            getAccountDetailsQuery();
        } else {
            hideAccountDetails();
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> getAccountDetails()).show();
        }
    }

    private void getAccountDetailsQuery() {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.query(new GetAccountDetailsQuery())
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetAccountDetailsQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GetAccountDetailsQuery.Data> response) {
                        if (settingsBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("getAccountDetails", "onResponse: " + response);
                                    accountEmail = response.getData().getAccountDetails().email();
                                    setAccountDetails(accountEmail,
                                            response.getData().getAccountDetails().is_email_verified());
                                } else {
                                    Log.e("getAccountDetails", "onResponse Errors: " + response.getErrors());
                                    Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                            BaseTransientBottomBar.LENGTH_INDEFINITE)
                                            .setAction(context.getResources().getString(R.string.network_error_retry), v -> getAccountDetails()).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("getAccountDetails", "onFailure: " + e.getMessage());
                        if (settingsBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();
                                hideAccountDetails();
                                Snackbar.make(view, context.getResources().getString(R.string.network_error_failure),
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                            });
                        }
                    }
                });
    }

    private void setAccountDetails(String email, boolean isEmailVerified) {
        showAccountDetails();
        settingsBinding.tvSettingsAccountEmail.setText(email);

        if (isEmailVerified) {
            settingsBinding.btnSettingsAccountEmail.setClickable(false);
            settingsBinding.btnSettingsAccountEmail.setText(context.getResources().getString(R.string.settings_btn_account_verified));
            settingsBinding.btnSettingsAccountEmail.setTextColor(context.getResources().getColor(R.color.green));
            settingsBinding.btnSettingsAccountEmail.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green20)));
            settingsBinding.ivSettingsAccountEmail.setBackgroundColor(context.getResources().getColor(R.color.green10));
            settingsBinding.lavSettingsAccountEmail.setRepeatCount(0);
            settingsBinding.lavSettingsAccountEmail.setSpeed(0.60f);
            settingsBinding.lavSettingsAccountEmail.setAnimation(R.raw.anim_account_verified);
        } else {
            settingsBinding.btnSettingsAccountEmail.setClickable(true);
            settingsBinding.btnSettingsAccountEmail.setText(context.getResources().getString(R.string.settings_btn_account_verify));
            settingsBinding.btnSettingsAccountEmail.setTextColor(context.getResources().getColor(R.color.blue));
            settingsBinding.btnSettingsAccountEmail.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blue20)));
            settingsBinding.ivSettingsAccountEmail.setBackgroundColor(context.getResources().getColor(R.color.red10));
            settingsBinding.lavSettingsAccountEmail.setRepeatCount(LottieDrawable.INFINITE);
            settingsBinding.lavSettingsAccountEmail.setSpeed(1.00f);
            settingsBinding.lavSettingsAccountEmail.setAnimation(R.raw.anim_account_not_verified);
        }

        settingsBinding.lavSettingsAccountEmail.playAnimation();
        verifyEmailBackgroundTint = settingsBinding.btnSettingsAccountEmail.getBackgroundTintList();
    }

    private void setCurrentLanguageText() {
        switch (currentLanguage) {
            case "en":
                settingsBinding.tvSettingsSettingsLanguageCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_language_english));
                break;
            case "fa":
                settingsBinding.tvSettingsSettingsLanguageCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_language_persian));
                break;
        }
    }

    private void setCurrentThemeText() {
        switch (currentTheme) {
            case 0:
                settingsBinding.tvSettingsSettingsThemeCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_theme_default));
                break;
            case 1:
                settingsBinding.tvSettingsSettingsThemeCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_theme_light));
                break;
            case 2:
                settingsBinding.tvSettingsSettingsThemeCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_theme_dark));
                break;
        }
    }

    private void verifyOnClick() {
        settingsBinding.btnSettingsAccountEmail.setOnClickListener(view ->
                sendVerificationEmail());
    }

    private void sendVerificationEmail() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            sendVerificationEmailMutation();
        } else {
            hideVerificationLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> sendVerificationEmail()).show();
        }
    }

    private void sendVerificationEmailMutation() {
        showVerificationLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new SendVerificationEmailMutation())
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<SendVerificationEmailMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<SendVerificationEmailMutation.Data> response) {
                        if (settingsBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideVerificationLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("sendVerificationEmail", "onResponse: " + response);
                                    MaterialAlertDialogBuilder sendVerificationEmailDialogBuilder = new MaterialAlertDialogBuilder(context)
                                            .setMessage(context.getResources().getString(R.string.settings_dialog_message))
                                            .setPositiveButton(context.getResources().getString(R.string.settings_dialog_positive), (dialog, which) -> {
                                            });
                                    sendVerificationEmailDialogBuilder.show();
                                } else {
                                    Log.e("sendVerificationEmail", "onResponse Errors: " + response.getErrors());
                                    Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                            BaseTransientBottomBar.LENGTH_INDEFINITE)
                                            .setAction(context.getResources().getString(R.string.network_error_retry), v -> sendVerificationEmail())
                                            .show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("sendVerificationEmail", "onFailure: " + e.getMessage());
                        if (settingsBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideVerificationLoadingAnimation();
                                Snackbar.make(view, context.getResources().getString(R.string.network_error_failure),
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                            });
                        }
                    }
                });
    }

    private void changeEmailOnClick() {
        settingsBinding.clSettingsAccountChangeEmail.setOnClickListener(view -> {
            SettingsFragmentDirections.ActionSettingsFragmentToChangeEmailFragment action =
                    SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment(accountEmail);
            navController.navigate(action);
        });
    }

    private void changePasswordOnClick() {
        settingsBinding.clSettingsAccountChangePassword.setOnClickListener(view ->
                navController.navigate(R.id.action_settingsFragment_to_changePasswordFragment));
    }

    private void languageOnClick() {
        settingsBinding.clSettingsSettingsLanguage.setOnClickListener(view ->
                Toast.makeText(context, "Language", Toast.LENGTH_SHORT).show());
    }

    private void themeOnClick() {
        settingsBinding.clSettingsSettingsTheme.setOnClickListener(view -> {

            String[] themeItems = new String[]{context.getResources().getString(R.string.settings_text_settings_theme_default),
                    context.getResources().getString(R.string.settings_text_settings_theme_light),
                    context.getResources().getString(R.string.settings_text_settings_theme_dark)};

            MaterialAlertDialogBuilder themeDialogBuilder = new MaterialAlertDialogBuilder(context)
                    .setTitle(context.getResources().getString(R.string.settings_text_settings_theme))
                    .setSingleChoiceItems(themeItems, currentTheme, (dialogInterface, i) -> {
                        switch (i) {
                            case 0:
                                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                                    settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i).apply();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                }
                                break;
                            case 1:
                                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                                    settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i).apply();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                }
                                break;
                            case 2:
                                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                                    settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i).apply();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                }
                                break;
                        }
                        currentTheme = i;
                        setCurrentThemeText();
                        dialogInterface.dismiss();
                    });
            themeDialogBuilder.show();
        });
    }

    private void privacyPolicyOnClick() {
        settingsBinding.clSettingsSettingsPrivacyPolicy.setOnClickListener(view ->
                Toast.makeText(context, "Privacy Policy", Toast.LENGTH_SHORT).show());
    }

    private void logoutOnClick() {
        settingsBinding.btnSettingsLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = context
                    .getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE).edit();
            editor.remove(Constants.PREFERENCE_USER_TOKEN_KEY).apply();
            editor.remove(Constants.PREFERENCE_IS_LOGGED_IN_KEY).apply();

            startActivity(new Intent(context, LandingActivity.class));
            activity.finish();
        });
    }

    private void showAccountDetails() {
        settingsBinding.groupSettingsAccount.setVisibility(View.VISIBLE);
    }

    private void hideAccountDetails() {
        settingsBinding.groupSettingsAccount.setVisibility(View.GONE);
    }

    private void showLoadingAnimation() {
        settingsBinding.lpiSettingsAccountEmail.setVisibility(View.VISIBLE);

    }

    private void hideLoadingAnimation() {
        settingsBinding.lpiSettingsAccountEmail.setVisibility(View.GONE);
    }

    private void showVerificationLoadingAnimation() {
        settingsBinding.btnSettingsAccountEmail.setClickable(false);
        settingsBinding.btnSettingsAccountEmail.setText(null);
        settingsBinding.btnSettingsAccountEmail.setBackgroundTintList(null);
        settingsBinding.cpiSettingsAccountEmailVerification.setVisibility(View.VISIBLE);

    }

    private void hideVerificationLoadingAnimation() {
        settingsBinding.cpiSettingsAccountEmailVerification.setVisibility(View.GONE);
        settingsBinding.btnSettingsAccountEmail.setBackgroundTintList(verifyEmailBackgroundTint);
        settingsBinding.btnSettingsAccountEmail.setText(context.getResources().getString(R.string.settings_btn_account_verify));
        settingsBinding.btnSettingsAccountEmail.setClickable(true);
    }

    private void adInit() {
        TapsellPlus.initialize(context, Constants.TAPSELL_KEY, new TapsellPlusInitListener() {
            @Override
            public void onInitializeSuccess(AdNetworks adNetworks) {
                Log.i("adInit", "onInitializeSuccess: " + adNetworks.name());
                AdHolder adHolder = TapsellPlus.createAdHolder(activity,
                        settingsBinding.flSettingsAdContainer, R.layout.ad_banner_settings);
                requestAdCounter = 0;
                requestNativeAd(adHolder);
            }

            @Override
            public void onInitializeFailed(AdNetworks adNetworks, AdNetworkError adNetworkError) {
                Log.e("adInit", "onInitializeFailed: " + adNetworks.name() + ", error: " + adNetworkError.getErrorMessage());
            }
        });
    }

    private void requestNativeAd(AdHolder adHolder) {
        TapsellPlus.requestNativeAd(activity, Constants.SETTINGS_NATIVE_ZONE_ID,
                new AdRequestCallback() {
                    @Override
                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel);
                        Log.i("requestNativeAd", "response: " + tapsellPlusAdModel.toString());
                        responseId = tapsellPlusAdModel.getResponseId();
                        showNativeAd(adHolder, responseId);
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

    private void showNativeAd(AdHolder adHolder, String responseId) {
        settingsBinding.groupSettingsAd.setVisibility(View.VISIBLE);

        TapsellPlus.showNativeAd(activity, responseId, adHolder,
                new AdShowListener() {
                    @Override
                    public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.onOpened(tapsellPlusAdModel);
                    }

                    @Override
                    public void onClosed(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.onClosed(tapsellPlusAdModel);
                    }
                });
    }

    private void destroyAd() {
        if (responseId != null) {
            TapsellPlus.destroyNativeBanner(activity, responseId);
        }
    }
}