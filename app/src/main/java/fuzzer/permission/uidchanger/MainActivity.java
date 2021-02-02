package fuzzer.permission.uidchanger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.Button;

import fuzzer.permission.uidchanger.models.fuzzing.FuzzingTask;
import fuzzer.permission.uidchanger.utils.Constants;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "COVFEFE:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "FuzzingApp just started");

        prepareBinderHandles();

        Button sendFuzzingTaskButton = findViewById(R.id.sendFuzzingTask);
        sendFuzzingTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(MainActivity.this, InvokerService.class);
                serviceIntent.putExtra(FuzzingManager.ACTIONS.FUZZING_TASK.name(),
                        new FuzzingTask("package", "addPreferredActivity", 1));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }
        });
    }

    private void prepareBinderHandles() {
        Intent serviceIntent = new Intent(MainActivity.this, InvokerService.class);
        serviceIntent.putExtra(FuzzingManager.ACTIONS.FUZZING_TASK.name(),
                new FuzzingTask(Constants.ALL_SERVICES, Constants.PREPARE_BINDER_HANDLES));
        startService(serviceIntent);
    }
}
