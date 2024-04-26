package com.example.praktikum6;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ErrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ErrorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ErrorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ErrorFragment newInstance(String param1, String param2) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    AppCompatButton retry;
    TextView noConnectionText;
    ProgressBar loading;
    private ApiService apiService;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retry = view.findViewById(R.id.retry);
        noConnectionText = view.findViewById(R.id.text);
        loading = view.findViewById(R.id.loading);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        Handler handler = new Handler();
        Runnable runnable = () -> {
            handler.post(() -> {
                noConnectionText.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
            });
            Call<UserResponse> call = apiService.getUsers(1, 6);


            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
//                        System.out.println(response.code());
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
                        getActivity().recreate();
                        Thread.currentThread().interrupt();
                    } else {
                        handler.post(() -> {
                            loading.setVisibility(View.GONE);
                            noConnectionText.setVisibility(View.VISIBLE);
                            retry.setVisibility(View.VISIBLE);
                        });
                        Thread.currentThread().interrupt();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    handler.post(() -> {
//                        System.out.println(t);
                        loading.setVisibility(View.GONE);
                        noConnectionText.setVisibility(View.VISIBLE);
                        retry.setVisibility(View.VISIBLE);
                    });
                    Thread.currentThread().interrupt();
                }
            });
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        retry.setOnClickListener(v -> {
            executorService.execute(runnable);
        });

    }
}