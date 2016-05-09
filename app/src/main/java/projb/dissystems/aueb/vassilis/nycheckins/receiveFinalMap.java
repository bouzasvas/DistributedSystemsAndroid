package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.res.AssetManager;
import android.os.AsyncTask;

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
public class receiveFinalMap extends AsyncTask<Void, Void, Map<Object, Long>> {

    // PORTS
    List<String> addr_ports = null;

    // final result from reducer
    private Map<Object, Long> finalResult = null;

    // network fields
    private int port;
    private ServerSocket server = null;
    private Socket reducer = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public receiveFinalMap(List<String> ports) {
        addr_ports = ports;
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
            finalResult = (Map<Object, Long>) in.readObject();
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
    protected Map<Object, Long> doInBackground(Void... params) {
        initConnection();
        receiveFromReducer();
        return finalResult;
    }
}
