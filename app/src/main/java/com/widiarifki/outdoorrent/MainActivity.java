package com.widiarifki.outdoorrent;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.widiarifki.outdoorrent.adapter.ProductAdapterRecycle;
import com.widiarifki.outdoorrent.model.Product;
import com.widiarifki.outdoorrent.model.ProductCategory;
import com.widiarifki.outdoorrent.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    
    public static final String TAG = MainActivity.class.getSimpleName();
    private Product[] mProducts;
    private User mUserDetail;

    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    //@BindView(R.id.loginButton) Button mLoginBtn;
    //@BindView(R.id.listView) ListView mListView;
    //@BindView(R.id.emptyTextView) TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        session = new SessionManager(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);

                View headerView = mNavigationView.getHeaderView(0);
                Button mLoginBtn = (Button) headerView.findViewById(R.id.loginButton);
                if(mLoginBtn != null) {
                    mLoginBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            goToLoginActivity();
                        }
                    });
                }

            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        initViews();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) mNavigationView.getHeaderView(0);

        if(session.isLoggedIn()){
            inflater.inflate(R.layout.nav_header_content_login, parent, true);
            mUserDetail = session.getUserDetails();
            TextView mUserName = (TextView) parent.findViewById(R.id.user_name_label);
            Log.v(TAG, mUserDetail.getName());
            mUserName.setText(mUserDetail.getName());
            TextView mUserEmail = (TextView) parent.findViewById(R.id.user_email_label);
            mUserEmail.setText(mUserDetail.getEmail());
        }else{
            inflater.inflate(R.layout.nav_header_content_default, parent);
            Menu mMenu = mNavigationView.getMenu();
            mMenu.findItem(R.id.nav_logout).setVisible(false);
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        //mListView.setEmptyView(mEmptyTextView);
        loadProductList();
    }

    private void loadProductList() {
        String url = "http://restapp-widiarifki.rhcloud.com/produk";
        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            /** Asynchronous process to get data from web/internet **/
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    alertUserAboutError();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                /* when request get response */
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonData = response.body().string();
                        //Log.v(TAG, jsonData);
                        try {
                            mProducts = getProducts(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    updateProductList();
                                }
                            });
                        } catch (JSONException e) {
                            alertUserAboutError();
                        }
                    } else {
                        alertUserAboutError();
                    }

                }
            });
        } else {
            Toast.makeText(this, "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProductList() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        ProductAdapterRecycle adapter = new ProductAdapterRecycle(this, mProducts);
        mRecyclerView.setAdapter(adapter);

        /*ProductAdapter adapter = new ProductAdapter(this, mProducts);
        ListView mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(adapter);*/
    }

    private Product[] getProducts(String jsonData) throws JSONException {
        /*JSONObject products = new JSONObject(jsonData);*/
        JSONArray data = new JSONArray(jsonData);
        Product[] products = new Product[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonProduct = data.getJSONObject(i);
            Product product = new Product();
            product.setId(jsonProduct.getInt("id"));
            product.setName(jsonProduct.getString("name"));
            product.setCharge(jsonProduct.getDouble("charge"));
            product.setChargeBase(jsonProduct.getInt("chargeBase"));
            product.setDescription(jsonProduct.getString("description"));
            product.setTerms(jsonProduct.getString("terms"));

            JSONObject jsonCatProduct = jsonProduct.getJSONObject("productCategory");
            ProductCategory catProduct = new ProductCategory();
            catProduct.setId(jsonCatProduct.getInt("id"));
            catProduct.setName(jsonCatProduct.getString("name"));

            product.setProductCategory(catProduct);

            products[i] = product;
        }

        return products;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hire_list) {
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.widiarifki.outdoorrent/http/host/path")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}