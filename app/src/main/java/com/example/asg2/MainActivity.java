package com.example.asg2;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue requestQueue;
    ArrayList<Item> itemList;
    Button myButton;
    ArrayList<Item> searchList;
    String result;
    private ArrayList<Item> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        // on MainActivity startup, read items.txt into ArrayList<Item> and call generateListView()


        itemList = readItems();
        searchList = new ArrayList<>();
        generateListView(itemList);
        myButton = findViewById(R.id.button);


        search();

        this.itemsList = readItems();
        generateListView(this.itemsList);
    }

    public void search() {
        myButton.setOnClickListener(v -> {
            searchList.clear();
            // Do something in response to button click
            EditText txtDescription = findViewById(R.id.itemSearch);
            result = txtDescription.getText().toString();
            for (Item i : itemList) {
                if (i.getName().contains(result)) {
                    searchList.add(i);
                }
            }
            generateListView(searchList);

        });

    }

    /**
     * This method is called when the New Item button is clicked and takes users to a second page
     *
     * @param v
     */
    public void newItemClick(View v){
        Intent intent = new Intent(MainActivity.this, CreateItem.class);
        startActivityForResult(intent, 0);
        //Asks to receive bundle when activity finishes
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle b = data.getExtras();
        int id = b.getInt("id");
        String name = b.getString("name");
        int quantity = b.getInt("quantity");
        double cost = b.getDouble("cost");
        int supID = b.getInt("supId");

        Item newItem = new Item(id, name, quantity, cost, supID);
        //write newItem to the file
        writeItem(newItem);
        generateListView(this.itemsList);

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * This method will rewrite the ListView with the supplied ArrayList<Item>. When a ListView
     * item is clicked, it will be passed along to its corresponding ItemPage through an onClick listener
     *
     * @param myList: ArrayList of Item objects which will populate the ListView
     */
    private void generateListView(ArrayList<Item> myList) {
        ListView mainList = findViewById(R.id.mainList);
        // Facilitates custom creation of ListView from ArrayList
        ItemsAdapter adapter = new ItemsAdapter(this, myList);
        mainList.setAdapter(adapter);

        // Click listener for each item in ListView
        // Original code from StackOverflow, refactored to match this project and converted to lambda expression
        mainList.setOnItemClickListener((parent, view, position, id) -> {
            Item item = adapter.getItem(position);

            Bundle b = new Bundle();
            b.putSerializable("item", item);

            Intent intent = new Intent(MainActivity.this, ItemPage.class);
            intent.putExtras(b);
            startActivity(intent);
        });
    }

    /**
     * Instead of writing to the items.txt file (because res is a read-only folder)
     * we will instead only create new items within the current instance. On reload
     * newly created items will not persist
     */
    public void writeItem(Item item){
        this.itemsList.add(item);

//        String itemLine = item.getId()+";"+item.getName()+";"+item.getQuantity()+";"+item.getCost()+";"+item.getSuppId();
//        FileOutputStream fOut = openFileOutput(getResources().openRawResource(getResources().getIdentifier("items", "raw", getPackageName())), true);
//        OutputStreamWriter osw = new OutputStreamWriter(fOut);
//        try {
//            osw.write(itemLine);
//            osw.flush();
//            osw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    /**
     *  You can call this method as such:
     *  ArrayList<Item> testName = readItems();
     *
     * @return ArrayList<String>: Returns an ArrayList of String;
     *      Will be converted to ArrayList<Items> once items has been uploaded.
     */
    public ArrayList<Item> readItems() {

        String baseURl = "http://34.68.196.188:8080";
        String route = "/api/items/";
        ArrayList<Item> fileArr = new ArrayList<>();


        String url = baseURl + route;

        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, url, null,
               new Response.Listener<JSONArray>() {
                   @Override
                   public void onResponse(JSONArray response) {
                       try {

                           for (int i = 0; i < response.length(); i++) {

                               JSONObject json_data = response.getJSONObject(i);
                               int id = json_data.getInt("id");
                               String name = json_data.getString("name");
                               int quantity = json_data.getInt("quantity");
                               double cost = json_data.getDouble("price");
                               int suppId = json_data.getInt("supplier_id");

                               fileArr.add(new Item(id, name, quantity, cost, suppId));
                           }



                       } catch (JSONException e) {
                           e.printStackTrace();
                       }

                   }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Some thing went wrong: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        requestQueue.add(jRequest);
        return fileArr;
    }

    }

