package com.example.praktikum6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DetailsViewInterface{

    private ApiService apiService;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private AppCompatButton showMore;
    private ProgressBar loading, loading2;
    private ConstraintLayout screen;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        recyclerView = findViewById(R.id.recyclerView);
        showMore = findViewById(R.id.show_more);
        loading = findViewById(R.id.loading);
        loading2 = findViewById(R.id.loading2);
        screen = findViewById(R.id.screen);

        FragmentManager fragmentManager = getSupportFragmentManager();
        ErrorFragment errorFragment = new ErrorFragment();

        Fragment fragment = fragmentManager.findFragmentByTag(ErrorFragment.class.getSimpleName());

        Handler handler = new Handler();
        Runnable runnable = () -> {
            handler.post(() -> screen.setVisibility(View.GONE));
            handler.post(() -> loading.setVisibility(View.VISIBLE));

            Call<UserResponse> call = apiService.getUsers(1, 6);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        users = response.body().getData();
                        adapter = new UserAdapter(users, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        handler.post(() -> screen.setVisibility(View.VISIBLE));
                        handler.post(() -> loading.setVisibility(View.GONE));
                    } else {
//                        System.out.println(response);
                        if (!(fragment instanceof ErrorFragment)){
                            fragmentManager
                                    .beginTransaction()
                                    .add(R.id.screen, errorFragment)
                                    .commit();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
//                    System.out.println(t);
                    if (!(fragment instanceof ErrorFragment)){
                        fragmentManager
                                .beginTransaction()
                                .add(R.id.screen, errorFragment)
                                .commit();
                    }
                }
            });
        };

        Runnable runnable2 = () -> {
            handler.post(() -> {
                showMore.setVisibility(View.GONE);
                loading2.setVisibility(View.VISIBLE);
            });

            Call<UserResponse> call = apiService.getUsers(2, 6);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        users.addAll(response.body().getData());
                        adapter = new UserAdapter(users, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        handler.post(() -> loading2.setVisibility(View.GONE));
                    } else {
//                        System.out.println(response);
                        if (!(fragment instanceof ErrorFragment)){
                            fragmentManager
                                    .beginTransaction()
                                    .add(R.id.screen, errorFragment)
                                    .commit();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
//                    System.out.println(t);
                    if (!(fragment instanceof ErrorFragment)){
                        fragmentManager
                                .beginTransaction()
                                .add(R.id.screen, errorFragment)
                                .commit();
                    }
                }
            });
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable);

        showMore.setOnClickListener(v -> {
            executorService.execute(runnable2);
        });
    }

    @Override
    public void onItemClickToProfile(int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("id", users.get(position).getId());
        startActivity(intent);
    }
}