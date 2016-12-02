package com.example.pc.nitiphon_restaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShopProduct extends AppCompatActivity {
    private ListView listProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product);

        bindWidget();

        SynJSON synJSON = new SynJSON();
        synJSON.execute();
    }
    private void bindWidget() {
        listProduct = (ListView) findViewById(R.id.listView12);
    }//Bind Widget



    private class SynJSON extends AsyncTask<Void , Void ,String>{

        @Override
        protected String doInBackground(Void... params) {

            try {
                String strURL = "http://csclub.ssru.ac.th/s56122201022/csc4202/php_get_foodTABLE.php";
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strURL).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (Exception e) {
                Log.d("23/11/59", "doIn ==> " + e.toString());
            }

            return null;

        }//do Inback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.d("23/11/59", "SucesOnpost ==> " + s);
                JSONArray jsonArray = new JSONArray(s);

                String[] iconString = new String[jsonArray.length()];
                String[] titleString = new String[jsonArray.length()];
                String[] priceString = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    iconString[i] = jsonObject.getString("Source");
                    titleString[i] = jsonObject.getString("Food");
                    priceString[i] = jsonObject.getString("Price");
                }

                ProductAdapter productAdapter = new ProductAdapter(ShopProduct.this,iconString,titleString,priceString);
                listProduct.setAdapter(productAdapter);



            } catch (Exception e) {
                Log.d("23/11/59", "onPost ==> " + e.toString());
            }

        }//onPost

    }//SynJson
}