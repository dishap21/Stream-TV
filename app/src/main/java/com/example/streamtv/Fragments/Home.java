package com.example.streamtv.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.streamtv.Activity.Details;
import com.example.streamtv.Adapter.AdapterAnimation;
import com.example.streamtv.Adapter.AdapterHorror;
import com.example.streamtv.Adapter.AdapterMovie;
import com.example.streamtv.Adapter.AdapterAction;
import com.example.streamtv.Adapter.AdapterScifi;
import com.example.streamtv.Adapter.AdapterThriller;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment implements AdapterAction.onSelectData, AdapterMovie.onSelectData {

    private List<MovieModel> moviePlayNow = new ArrayList<>();
    private List<MovieModel> movieAction = new ArrayList<>();
    private List<MovieModel> moviePlayNowRo = new ArrayList<>();
    private List<MovieModel> movieSciFi = new ArrayList<>();
    private List<MovieModel> movieHorror = new ArrayList<>();
    private List<MovieModel> movieAnimation = new ArrayList<>();
    private RecyclerView myrv, myrv2, myrv3, myrv4, myrv5, myrv6;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Trending movies recycler view
        myrv = RootView.findViewById(R.id.recyclerViewMovie);
        myrv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        getMovieList();

        //Trending action recycler view
        myrv2 = RootView.findViewById(R.id.recyclerViewTv);
        myrv2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        getTvList();

        //Thriller Movies recycler view
        myrv3 = RootView.findViewById(R.id.recyclerViewThriller);
        myrv3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        getRomanceList();

        //Sci-Fi Movies recycler view
        myrv4 = RootView.findViewById(R.id.recyclerViewScifi);
        myrv4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        getSciFiList();

        //Horror Movies recycler view
        myrv5 = RootView.findViewById(R.id.recyclerViewHorror);
        myrv5.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        getHorrorList();

        //Animation Movies recycler view
        myrv6 = RootView.findViewById(R.id.recyclerViewAnimation);
        myrv6.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        getAnimationList();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return RootView;
    }

    private void getMovieList() {
        String URL = "https://api.themoviedb.org/3/movie/popular?api_key=bb76a685d910dea80be3ebda6381944a";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(jsonObject.getString("release_date"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                moviePlayNow.add(dataApi);
                                showMovieHorizontal();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        myrv.setAlpha(0);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getTvList() {
        String URL = "https://api.themoviedb.org/3/discover/movie?api_key=bb76a685d910dea80be3ebda6381944a&with_genres=28";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(jsonObject.getString("release_date"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                movieAction.add(dataApi);
                                showMovieHorizontal2();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        myrv.setAlpha(0);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getRomanceList() {
        String URL = "https://api.themoviedb.org/3/discover/movie?api_key=bb76a685d910dea80be3ebda6381944a&with_genres=10749";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(jsonObject.getString("release_date"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                moviePlayNowRo.add(dataApi);
                                showMovieHorizontal3();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        myrv.setAlpha(0);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getSciFiList(){
        String URL = "https://api.themoviedb.org/3/discover/movie?api_key=bb76a685d910dea80be3ebda6381944a&with_genres=878";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(jsonObject.getString("release_date"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                movieSciFi.add(dataApi);
                                showMovieHorizontal4();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        myrv.setAlpha(0);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getHorrorList(){
        String URL = "https://api.themoviedb.org/3/discover/movie?api_key=bb76a685d910dea80be3ebda6381944a&with_genres=27";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(jsonObject.getString("release_date"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                movieHorror.add(dataApi);
                                showMovieHorizontal5();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        myrv.setAlpha(0);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getAnimationList(){
        String URL = "https://api.themoviedb.org/3/discover/movie?api_key=bb76a685d910dea80be3ebda6381944a&with_genres=16";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(jsonObject.getString("release_date"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                movieAnimation.add(dataApi);
                                showMovieHorizontal6();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        myrv.setAlpha(0);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void showMovieHorizontal() {
        AdapterMovie AdapterMovie = new AdapterMovie(getActivity(), moviePlayNow, this);
        myrv.setAdapter(AdapterMovie);
        AdapterMovie.notifyDataSetChanged();
    }

    private void showMovieHorizontal2() {
        AdapterAction AdapterAction = new AdapterAction(getActivity(), movieAction, this);
        myrv2.setAdapter(AdapterAction);
        AdapterAction.notifyDataSetChanged();
    }

    private void showMovieHorizontal3() {
        AdapterThriller AdapterThriller = new AdapterThriller(getActivity(), moviePlayNowRo, this);
        myrv3.setAdapter(AdapterThriller);
        AdapterThriller.notifyDataSetChanged();
    }

    private void showMovieHorizontal4(){
        AdapterScifi AdapterScifi = new AdapterScifi(getActivity(), movieSciFi, this);
        myrv4.setAdapter(AdapterScifi);
        AdapterScifi.notifyDataSetChanged();
    }

    private void showMovieHorizontal5(){
        AdapterHorror AdapterHorror = new AdapterHorror(getActivity(), movieHorror, this);
        myrv5.setAdapter(AdapterHorror);
        AdapterHorror.notifyDataSetChanged();
    }

    private void showMovieHorizontal6(){
        AdapterAnimation AdapterAnimation = new AdapterAnimation(getActivity(), movieAnimation, this);
        myrv6.setAdapter(AdapterAnimation);
        AdapterAnimation.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    @Override
    public void onSelected(MovieModel modelMovie) {
        Intent intent = new Intent(getActivity(), Details.class);
        intent.putExtra("detailMovie", modelMovie);
        startActivity(intent);
    }
}