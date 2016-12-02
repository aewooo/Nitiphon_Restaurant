package com.example.pc.nitiphon_restaurant;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SignUp extends AppCompatActivity {

    private EditText userEditText, passwordEditText, nameEditText;
    private String userString, passwordString, nameString;

    private  UserTABLE objUserTABLE;
    private  FoodTABLE objFoodTABLE;
    private  OrderTABLE objOrderTABLE;

    private MySQLite mySQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mySQLite=new MySQLite(this);
        // bindwidget
        bindWidget();

    } //onCreate

    public void clickSignUp (View view){
        userString = userEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();
        nameString = nameEditText.getText().toString().trim();

        // check space
        if (userString.equals("") || passwordString.equals("") || nameString.equals("")){
            // have space
            MyAlert myAlert = new MyAlert();
            myAlert.myDialog(this, "มีช่องว่าง", "กรุณากรอกให้ครบ");
        }else {
            // no space
            updateValueToServer();
        }

    } // clickSignUp

    private void updateValueToServer(){
        String strURL = "http://www.csclub.ssru.ac.th/s56122201022/csc4202/php_add_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd","true")
                .add("User",userString)
                .add("Password",passwordString)
                .add("Name",nameString)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(strURL).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    connectedSQLite();
                    synJSONtoSQLite();
                    finish();
                }catch (Exception e){
                    Log.d("Restaurant", "error " + e.toString());
                }

            }
        });
    } //

    private void bindWidget(){
        userEditText = (EditText) findViewById(R.id.editTextUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        nameEditText = (EditText) findViewById(R.id.editTextName);
    } //bindWidget
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
    private  void connectedSQLite(){
        objUserTABLE=new UserTABLE(this);
        objFoodTABLE=new FoodTABLE(this);
        objOrderTABLE=new OrderTABLE(this);
    }//connectedSQLite
}
