package projb.dissystems.aueb.vassilis.nycheckins;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vassilis on 9/5/2016.
 */
public class receiveFinalMap extends AsyncTask {

    // PORTS
    List<String> addr_ports = null;

    // final result from reducer
    onMapReceived completed;
    Handler handler;
    private Map<Object, Long> reducerResult = null;

    // network fields
    private int port;
    private ServerSocket server = null;
    private Socket reducer = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public receiveFinalMap(List<String> ports, Handler handler, onMapReceived completed) {
        addr_ports = ports;
        this.handler = handler;
        this.completed = completed;
    }

    public void initConnection() {
        port = Integer.valueOf(addr_ports.get(9));
        try {
            server = new ServerSocket(port);
            reducer = server.accept();
            out = new ObjectOutputStream(reducer.getOutputStream());
            in = new ObjectInputStream(reducer.getInputStream());
        } catch (IOException initEx) {
            initEx.printStackTrace();
        }
    }

    private void receiveFromReducer() {
        try {
            reducerResult = (Map<Object, Long>) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
                reducer.close();
                server.close();
            } catch (IOException e) {
                System.err.println("Could not close streams...");
            }
        }
    }


    @Override
    protected Object doInBackground(Object[] params) {
        initConnection();
        receiveFromReducer();
        handler.post(new Runnable() {
            @Override
            public void run() {
                MapsActivity.finalResult = reducerResult;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        completed.onMapReceived();
    }
}
