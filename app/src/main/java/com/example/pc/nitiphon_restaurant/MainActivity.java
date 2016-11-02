package com.example.pc.nitiphon_restaurant;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private  UserTABLE objUserTABLE;
    private  FoodTABLE objFoodTABLE;
    private  OrderTABLE objOrderTABLE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedSQLite();
        //Test Add Value
        //deleteAllData();
        //testAddValue();
        synJSONtoSQLite();
    }

    private void deleteAllData() {
        SQLiteDatabase objSqLiteDatabase=openOrCreateDatabase("Restaurant.db",MODE_APPEND,null);
        objSqLiteDatabase.delete("userTABLE",null,null);
        objSqLiteDatabase.delete("foodTABLE",null,null);
        objSqLiteDatabase.delete("orderTABLE",null,null);
    }

    private void synJSONtoSQLite() {
        StrictMode.ThreadPolicy myPolicy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);
        int intTime=0;
        while (intTime<=1){
            InputStream objInputStream = null;
            String strJSON=null;
            String strUserURL ="http://csclub.ssru.ac.th/s56122201022/CSC4202/php_get_userTABLE.php";
            String strFoodURL="http://csclub.ssru.ac.th/s56122201022/CSC4202/php_get_foodTABLE.php";
            HttpPost objHttpPost;
            //1.Create InputStream
            try{
                HttpClient objHttpClient =new DefaultHttpClient();
                switch (intTime){
                    case 0:
                        objHttpPost=new HttpPost(strUserURL);
                        break;
                    default:
                        objHttpPost=new HttpPost(strFoodURL);
                        break;
                }
                HttpResponse objHttpResponse =objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity=objHttpResponse.getEntity();
                objInputStream=objHttpEntity.getContent();
            }catch (Exception e){
                Log.d("masterUNG","InputStream==>"+e.toString());
            }
            //2.Create strJSON
            try {
                BufferedReader objBufferedReader=new BufferedReader(new InputStreamReader(objInputStream,"UTF-8"));
                StringBuilder objStringBuilder =new StringBuilder();
                String strLine=null;
                while((strLine=objBufferedReader.readLine())!=null){
                    objStringBuilder.append(strLine);
                }
                objInputStream.close();
                strJSON=objStringBuilder.toString();
            }catch (Exception e){
                Log.d("masterUNG","strJSON==>"+e.toString());
            }
            //3.Update to SQLite
            try {
                JSONArray objJsonArray =new JSONArray(strJSON);
                for(int i=0;i<objJsonArray.length();i++){
                    JSONObject jsonObject=objJsonArray.getJSONObject(i);
                    switch (intTime){
                        case  0:
                            String strUser = jsonObject.getString("User");
                            String strPassword=jsonObject.getString("Password");
                            String strName=jsonObject.getString("Name");
                            objUserTABLE.addNewUser(strUser,strPassword,strName);
                            break;
                        default:
                            String strFood = jsonObject.getString("Food");
                            String strSource=jsonObject.getString("Source");
                            String strPrice=jsonObject.getString("Price");
                            objFoodTABLE.addNewFood(strFood,strSource,strPrice);
                            break;
                    }
                }
            }catch (Exception e){
                Log.d("masterUNG","Update SQLite==>"+e.toString());
            }
            intTime+=1;

        }
    }

    private void testAddValue(){
        //objUserTABLE.addNewUser("testUser","testPass","testName");
        objFoodTABLE.addNewFood("testFood","testSoure","testPrice");
        objUserTABLE.addNewUser("BOYZA408","123456","NNN");
        objOrderTABLE.addNewOrder("dasd","dasd","das","dasd");
        //objOrderTABLE.addOrder("testOfficer","testDesk","testFood","testItem");
    }
    private  void connectedSQLite(){
        objUserTABLE=new UserTABLE(this);
        objFoodTABLE=new FoodTABLE(this);
        objOrderTABLE=new OrderTABLE(this);
    }//connectedSQLite
}   //MainClass
