package oceancandy.jianzhang.com.oceancandy.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import oceancandy.jianzhang.com.oceancandy.R;
import java.net.URLConnection;
import java.net.URL;

public class WebService {

    //get the json data from the given url
    public static String httpGet(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection
                    .getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line);
            }
            bufferedReader.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    //check if there is network available
    public static Boolean networkConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    //show the dialog if no network
    public static void showNetworkDialog(Context context) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle(R.string.network_titile);
        builder.setMessage(R.string.network_message);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
