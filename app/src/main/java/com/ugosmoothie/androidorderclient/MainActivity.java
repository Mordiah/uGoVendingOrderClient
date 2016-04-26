package com.ugosmoothie.androidorderclient;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.ugosmoothie.androidorderclient.Adapters.PurchaseArrayAdapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ArrayList<Order> orders;
    PurchaseArrayAdapter purchaseAdapter;
    WifiManager wifiManager;
    private String hostIpAddress = "192.168.0.100";
    private AsyncHttpClient.WebSocketConnectCallback cb;
    WebSocket serverSocket;
    AsyncHttpClient asyncClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        orders = new ArrayList<Order>();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //Resolve IP address

        String hostAddress = "http://192.168.1.163:5858";

        cb = new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    serverSocket= null;
                    return;
                }
                serverSocket = webSocket;
                webSocket.send("Client conneceted!");

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("CLIENT", s);
                        processMessage(s);
                    }
                });
            }
        };

        asyncClient = AsyncHttpClient.getDefaultInstance();
        asyncClient.websocket(hostAddress, null, cb);

        final ListView listview = (ListView) this.findViewById(R.id.purchasesListView);

        purchaseAdapter = new PurchaseArrayAdapter(this, android.R.layout.simple_list_item_checked, orders);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(purchaseAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = orders.get(position);
                if (order != null) {
                    completeOrder(order);
                    listview.setItemChecked(position, true);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void processMessage(String s) {
        try {
            JSONObject msg = new JSONObject(s);
            if (msg.getString("eventType").equals(EventTypes.SmoothieEvent.Purchase.getSmoothieEvent())) {
                Order newOrder = new Order(
                        msg.getLong("orderId"),
                        msg.getString("smoothieName"),
                        msg.getString("liquidName"),
                        msg.getString("supplementName")
                );
                orders.add(newOrder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        purchaseAdapter.notifyDataSetChanged();
                    }
                });
            }

        } catch (JSONException jsonEx) {
            jsonEx.printStackTrace();
        }
    }

    private void completeOrder(Order completedOrder) {
        completedOrder.Complete();

        // send message to server that the order has been completed
        if (serverSocket != null) {
            try {
                JSONObject msg = new JSONObject();
                msg.put("eventType", EventTypes.SmoothieEvent.Complete.getSmoothieEvent());
                msg.put("orderId", completedOrder.getId());
                serverSocket.send(msg.toString());
                purchaseAdapter.notifyDataSetChanged();
            } catch (JSONException jsonEx) {
                jsonEx.printStackTrace();
            }
        }
    }
}
