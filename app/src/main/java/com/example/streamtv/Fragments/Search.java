package com.example.streamtv.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.streamtv.Activity.Details;
import com.example.streamtv.Adapter.AdapterMovie;
import com.example.streamtv.Adapter.AdapterSearch;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment implements AdapterMovie.onSelectData {

    private SearchView searchView;
    private List<MovieModel> moviePopular = new ArrayList<>();
    ProgressDialog progressDialog;
    private RecyclerView myrv;
    public Search() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = RootView.findViewById(R.id.search_sv);
        searchView.setQueryHint("Search For Movies");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovie(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals(""))
                    Toast.makeText(getActivity(), "We have not got the text.", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        myrv = RootView.findViewById(R.id.recyclerViewSerach);
        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        return RootView;
    }

    private void searchMovie(String query) {
        progressDialog.show();
        String URL = "https://api.themoviedb.org/3/search/multi?api_key=bb76a685d910dea80be3ebda6381944a&query=" + query;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            moviePopular = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MovieModel dataApi = new MovieModel();
                                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMMM yyyy");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                                String datePost = jsonObject.getString("release_date");

                                dataApi.setId(Integer.parseInt(jsonObject.getString("id")));
                                dataApi.setTitle(jsonObject.getString("title"));
                                dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setReleaseDate(formatter.format(dateFormat.parse(datePost)));
                                dataApi.setPosterPath(jsonObject.getString("poster_path"));
                                dataApi.setPopularity(jsonObject.getString("popularity"));
                                moviePopular.add(dataApi);
                                showMovie();
                                progressDialog.dismiss();
                            }

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        //progressBar.setVisibility(View.GONE);
                        myrv.setAlpha(0);
                        progressDialog.dismiss();
                        //emptyView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }




    private void showMovie() {
        AdapterSearch adapterSearch = new AdapterSearch(moviePopular, getActivity(), this);
        myrv.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();
    }

    @Override
    public void onSelected(MovieModel modelMovie) {
        Intent intent = new Intent(getActivity(), Details.class);
        intent.putExtra("detailMovie", modelMovie);
        startActivity(intent);
    }
}