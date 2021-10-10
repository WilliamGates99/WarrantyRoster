package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.database.CategoryDataModel;
import com.xeniac.warrantyroster.database.CategoryDataProvider;
import com.xeniac.warrantyroster.database.WarrantyRosterDatabase;
import com.xeniac.warrantyroster.databinding.FragmentWarrantiesBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarrantiesFragment extends Fragment implements WarrantyListClickInterface {

    private FragmentWarrantiesBinding warrantiesBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;
    private WarrantyRosterDatabase database;

    public WarrantiesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        warrantiesBinding = FragmentWarrantiesBinding.inflate(inflater, container, false);
        view = warrantiesBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WarrantyRosterDatabase.destroyInstance();
        warrantiesBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
        database = WarrantyRosterDatabase.getInstance(context);
        ((MainActivity) context).showNavBar();

        seedCategories();
        getWarrantiesList();
    }

    private void seedCategories() {
        int itemCount = database.categoryDAO().countItems();

        if (itemCount >= 0 && itemCount < 21) {
            List<CategoryDataModel> categoriesList = CategoryDataProvider.categoriesList;
            database.categoryDAO().deleteAllCategories();
            database.categoryDAO().insertAllCategories(categoriesList);
            Log.i("seedCategories", "seedCategories: data inserted");
        }
    }

    private void getWarrantiesList() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            getWarrantiesListQuery();
        } else {
            warrantiesBinding.tvWarrantiesNetworkError.setText(
                    context.getResources().getString(R.string.network_error_connection));
            showNetworkError();
        }
    }

    private void getWarrantiesListQuery() {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.query(new GetWarrantiesListQuery())
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetWarrantiesListQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GetWarrantiesListQuery.Data> response) {
                        if (warrantiesBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("getWarrantiesList", "onResponse: " + response);

                                    int warrantiesListSize = response.getData().getAllWarranties().size();
                                    if (warrantiesListSize == 0) {
                                        showWarrantiesEmptyList();
                                    } else {
                                        List<WarrantyDataModel> warrantiesList = new ArrayList<>();

                                        for (int i = 0; i < warrantiesListSize; i++) {
                                            warrantiesList.add(new WarrantyDataModel(
                                                    response.getData().getAllWarranties().get(i).id(),
                                                    response.getData().getAllWarranties().get(i).title(),
                                                    response.getData().getAllWarranties().get(i).brand(),
                                                    response.getData().getAllWarranties().get(i).model(),
                                                    response.getData().getAllWarranties().get(i).serial_number(),
                                                    response.getData().getAllWarranties().get(i).starting_date().toString(),
                                                    response.getData().getAllWarranties().get(i).expiry_date().toString(),
                                                    response.getData().getAllWarranties().get(i).description(),
                                                    response.getData().getAllWarranties().get(i).category_id()
                                            ));
                                        }

                                        showWarrantiesList(warrantiesList);
                                    }
                                } else {
                                    Log.e("getWarrantiesList", "onResponse Errors: " + response.getErrors());
                                    warrantiesBinding.tvWarrantiesNetworkError.setText(
                                            context.getResources().getString(R.string.network_error_response));
                                    showNetworkError();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("getWarrantiesList", "onFailure: " + e.getMessage());
                        if (warrantiesBinding != null) {
                            activity.runOnUiThread(() -> {
                                warrantiesBinding.tvWarrantiesNetworkError.setText(
                                        context.getResources().getString(R.string.network_error_failure));
                                showNetworkError();
                            });
                        }
                    }
                });
    }

    private void showNetworkError() {
        hideLoadingAnimation();
        warrantiesBinding.groupWarrantiesEmptyList.setVisibility(View.GONE);
        warrantiesBinding.rvWarranties.setVisibility(View.GONE);
        warrantiesBinding.groupWarrantiesNetwork.setVisibility(View.VISIBLE);
        retryNetworkBtn();
    }

    private void retryNetworkBtn() {
        warrantiesBinding.btnWarrantiesNetworkRetry.setOnClickListener(view -> getWarrantiesList());
    }

    private void showLoadingAnimation() {
        warrantiesBinding.searchWarranties.setVisibility(View.GONE);
        warrantiesBinding.groupWarrantiesNetwork.setVisibility(View.GONE);
        warrantiesBinding.groupWarrantiesEmptyList.setVisibility(View.GONE);
        warrantiesBinding.rvWarranties.setVisibility(View.GONE);
        warrantiesBinding.cpiWarranties.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        warrantiesBinding.cpiWarranties.setVisibility(View.GONE);
    }

    private void showWarrantiesEmptyList() {
        warrantiesBinding.searchWarranties.setVisibility(View.GONE);
        warrantiesBinding.groupWarrantiesNetwork.setVisibility(View.GONE);
        warrantiesBinding.rvWarranties.setVisibility(View.GONE);
        warrantiesBinding.groupWarrantiesEmptyList.setVisibility(View.VISIBLE);
    }

    private void showWarrantiesList(List<WarrantyDataModel> warrantiesList) {
        warrantiesBinding.groupWarrantiesNetwork.setVisibility(View.GONE);
        warrantiesBinding.groupWarrantiesEmptyList.setVisibility(View.GONE);
        warrantiesBinding.rvWarranties.setVisibility(View.VISIBLE);

        WarrantyAdapter warrantyAdapter = new WarrantyAdapter(context,
                database, sortWarrantiesAlphabetically(warrantiesList), this);
        warrantiesBinding.rvWarranties.setAdapter(warrantyAdapter);

        //TODO remove comment after adding search function
//        searchWarrantiesList();
    }

    @Override
    public void onItemClick(WarrantyDataModel warranty, long daysUntilExpiry) {
        WarrantiesFragmentDirections.ActionWarrantiesFragmentToWarrantyDetailsFragment action =
                WarrantiesFragmentDirections.actionWarrantiesFragmentToWarrantyDetailsFragment(warranty, daysUntilExpiry);
        navController.navigate(action);
    }

    private List<WarrantyDataModel> sortWarrantiesAlphabetically(List<WarrantyDataModel> warrantiesList) {
        //noinspection ComparatorCombinators
        Collections.sort(warrantiesList, (warranty1, warranty2) ->
                warranty1.getTitle().compareTo(warranty2.getTitle()));
        return warrantiesList;
    }

    private void searchWarrantiesList() {
        warrantiesBinding.searchWarranties.setVisibility(View.VISIBLE);

        warrantiesBinding.searchWarranties.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                warrantiesBinding.toolbarWarranties.setTitle(null);
            } else {
                warrantiesBinding.toolbarWarranties.setTitle(context.getResources().getString(R.string.warranties_text_title));
            }
        });

        warrantiesBinding.searchWarranties.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    Toast.makeText(context, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                    warrantiesBinding.searchWarranties.onActionViewCollapsed();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    Toast.makeText(context, "Input: " + newText, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}