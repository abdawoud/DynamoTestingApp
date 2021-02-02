package fuzzer.permission.uidchanger;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import fuzzer.permission.uidchanger.models.fuzzing.FuzzingTask;

//@TODO: merge the InvokerService class here and delete it

public class Invokator extends BroadcastReceiver {

    private final static String TAG = "COVFEFE:Invokator";

    @Override
    public void onReceive(Context context, Intent intent) {
        FuzzingTask fuzzingTask = new FuzzingTask();
        JSONObject config = readConfigurationFile(context);
        try {
            fuzzingTask.setMethod(config.getString("method"));
            fuzzingTask.setService(config.getString("service"));
            fuzzingTask.setIterations(1);
            if (config.has("default_parameters")) {
                JSONObject defaultParameters = config.getJSONObject("default_parameters");
                if (defaultParameters.has("int") && defaultParameters.has("string")) {
                    fuzzingTask.setIntDefaultValue(defaultParameters.getInt("int"));
                    fuzzingTask.setStringDefaultValue(defaultParameters.getString("string"));
                }
            } else {
                fuzzingTask.setIntDefaultValue(0);
                fuzzingTask.setStringDefaultValue("fuzzer.permission.uidchanger");
            }
            if (config.has("parameters")) {
                JSONArray jsonArray = config.getJSONArray("parameters");
                List<String> list = new ArrayList<String>();
                for(int i=0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
                fuzzingTask.setParameters(list.toArray(new String[list.size()]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to parse the configuration file!");
            return;
        }

        Intent serviceIntent = new Intent(context, InvokerService.class);
        serviceIntent.putExtra(FuzzingManager.ACTIONS.FUZZING_TASK.name(), fuzzingTask);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }



    private JSONObject readConfigurationFile(Context context) {
        JSONObject json = null;
        File file = new File(context.getFilesDir() + "/","setup.json");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            json = new JSONObject(text.toString());
        }
        catch (Exception e) {}
        return json;
    }
}
