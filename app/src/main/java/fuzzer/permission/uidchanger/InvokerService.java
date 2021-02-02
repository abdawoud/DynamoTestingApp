package fuzzer.permission.uidchanger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fuzzer.permission.uidchanger.generators.ParametersGenerator;
import fuzzer.permission.uidchanger.models.fuzzing.BinderService;
import fuzzer.permission.uidchanger.models.fuzzing.FuzzingTask;
import fuzzer.permission.uidchanger.utils.Constants;
import fuzzer.permission.uidchanger.utils.HelperUtils;

public class InvokerService extends Service {
    private final static String TAG = "COVFEFE:InvokerService";
    String INVOCATION_RESULT_FILE = "invocation-result.json";
    FuzzingManager mFuzzingManager;

    public InvokerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        deleteOldInvocationResult();

        FuzzingTask fuzzingTask = intent.getParcelableExtra(FuzzingManager.ACTIONS.FUZZING_TASK.name());
        try {
            if (fuzzingTask.getService().equals(Constants.ALL_SERVICES) &&
                    fuzzingTask.getMethod().equals(Constants.PREPARE_BINDER_HANDLES)) {
                prepareBinderHandles();
            } else {
                invokeApi(fuzzingTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
    }

    private void prepareBinderHandles() {
        try {
            int count = 0;
            HashMap<String, BinderService> services = FuzzingManager.getInstance().getBinderServices();

            String arch = Build.SUPPORTED_ABIS[0];
            JSONObject json = new JSONObject();
            json.put("whoami", Build.SERIAL+"_"+ arch + "_" + Build.VERSION.SDK_INT);
            for (BinderService service : services.values()) {
                JSONArray apis = new JSONArray();
                for (Method m : Class.forName(service.getQualifiedName()).getDeclaredMethods()) {
                    String dalvikName = HelperUtils.methodNameToDalvikName(m.getName(),
                            m.getReturnType().getCanonicalName(), m.getParameterTypes());
                    apis.put(dalvikName);
                }
                json.put(service.getName(), apis);
                count += apis.length();
            }
            Log.d("COVFEFE", "count: " + count);
            FileOutputStream fos = getApplicationContext().openFileOutput("api-list.json",
                    MODE_PRIVATE);
            Writer out = new OutputStreamWriter(fos);
            out.write(json.toString(2));
            out.close();
        } catch (IllegalAccessException | ClassNotFoundException | InvocationTargetException |
                RemoteException | JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void reportParameters(Object[] args, Type[] types) {
        try {
            JSONObject obj = new JSONObject();

            String arch = Build.SUPPORTED_ABIS[0];
            obj.put("whoami", Build.SERIAL+"_"+ arch + "_" + Build.VERSION.SDK_INT);

            JSONArray params = new JSONArray();
            for (int idx = 0; idx < args.length; idx++) {
                JSONObject json = new JSONObject();
                json.put(types[idx].toString(), args[idx].toString());
                params.put(json);
            }
            obj.put("parameters", params);

            //JSONArray defaults = new JSONArray();
            //obj.put("defaults", params);
            FileOutputStream outputStream = getApplicationContext().openFileOutput("parameters-list.json",
                    getApplicationContext().MODE_PRIVATE);
            outputStream.write(obj.toString(2).getBytes());
            outputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void reportInvocationResult(FuzzingTask fuzzingTask, Object[] args, Type[] types, Object result, String exception, boolean returns) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("service", fuzzingTask.getService());
            obj.put("api", fuzzingTask.getMethod());
            obj.put("returns", returns);
            obj.put("output", (result != null) ? result.toString() : "null");
            obj.put("exception", (exception != null) ? exception : "null");

            if (args != null) {
                JSONArray params = new JSONArray();
                for (int idx = 0; idx < args.length; idx++) {
                    JSONObject json = new JSONObject();
                    json.put(types[idx].toString(), args[idx].toString());
                    params.put(json);
                }
                obj.put("parameters", params);
            }

            //JSONArray defaults = new JSONArray();
            //obj.put("defaults", params);
            FileOutputStream outputStream = getApplicationContext().openFileOutput(INVOCATION_RESULT_FILE,
                    getApplicationContext().MODE_PRIVATE);
            Log.d(TAG, obj.toString(2));
            outputStream.write(obj.toString(2).getBytes());
            outputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteOldInvocationResult() {
        try {
            JSONObject obj = new JSONObject();
            FileOutputStream outputStream = getApplicationContext().openFileOutput(INVOCATION_RESULT_FILE,
                    getApplicationContext().MODE_PRIVATE);
            outputStream.write(obj.toString(2).getBytes());
            outputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void invokeApi(FuzzingTask fuzzingTask) {
        Log.d(TAG, "===================> Before the API is called " +
                "[" + fuzzingTask.getService() + "." + fuzzingTask.getMethod() + "]");
        String[] parameters = fuzzingTask.getParameters();
        Object o = null;
        Method method = null;
        String exception = null;
        Object[] args = null;
        boolean returns = false;
        try {
            mFuzzingManager = FuzzingManager.getInstance();
            final BinderService binderService =
                    mFuzzingManager.getBinderService(fuzzingTask.getService());
            if (binderService == null)
                return;

            method = mFuzzingManager.getBinderServiceMethod(binderService,
                    fuzzingTask.getMethod());

            if (method == null)
                return;

            Log.d(TAG, "Number of parameters: " + method.getParameterTypes().length + "");
            if (method.getParameterTypes().length != 0) {
                args = new ParametersGenerator(method.getParameterTypes())
                        .getOneRandomPermutation();
            } else {
                args = new Object[0];
            }

            for (int idx = 0; idx < method.getParameterTypes().length; idx++) {
                Log.d("COVFEFE", method.getParameterTypes()[idx].toString());
                if (method.getParameterTypes()[idx].toString().equals("boolean")) {
                    args[idx] = parameters[idx].split("B:")[1].equals("true");
                }
                if (method.getParameterTypes()[idx].toString().equals("int")) {
                    if (parameters[idx].split("INT:").length == 2) {
                        String intValue = parameters[idx].split("INT:")[1];
                        if (intValue.contains("0x")) {
                            intValue = intValue.replace("0x", "").toLowerCase();
                            if (intValue.equals("7fffffff"))
                                args[idx] = Integer.MAX_VALUE;
                            else
                                args[idx] = Integer.parseInt(intValue, 16);
                        } else {
                            args[idx] = Integer.parseInt(intValue);
                        }
                    } else {
                        args[idx] = 0;
                    }
                }
                if (method.getParameterTypes()[idx].toString().equals("class android.accounts.Account")) {
                    String[] nameType;
                    if (parameters[idx].contains("ACCOUNT:")) {
                        nameType = parameters[idx].split("ACCOUNT:")[1].split("-");
                    } else {
                        nameType = new String[]{"accountName", "personal"};
                    }

                    String name = (nameType[0].equals("null"))? null : nameType[0];
                    String type = (nameType[1].equals("null"))? null : nameType[1];

                    args[idx] = new Account(name, type);
                }
                if (method.getParameterTypes()[idx].toString().equals("class android.hardware.biometrics.BiometricSourceType") ||
                        method.getParameterTypes()[idx].toString().equals("class android.hardware.usb.UsbAccessory")) {
                    args[idx] = null;
                }
                if (method.getParameterTypes()[idx].toString().contains("android.accessibilityservice.AccessibilityServiceInfo")) {
                    args[idx] = null;
                }
                if (method.getParameterTypes()[idx].toString().equals("class android.net.ConnectionInfo")) {
                    args[idx] = null;
                }
                if (method.getParameterTypes()[idx].toString().equals("class java.lang.String")) {
                    String value = parameters[idx].split("S:")[1];
                    args[idx] = (value.equals("null"))? null : value;
                }
                if (method.getParameterTypes()[idx].toString().equals("interface java.util.List")) {
                    List list = new ArrayList();
                    args[idx] = list;
                }
                if(method.getParameterTypes()[idx].toString().contains("android.app.IApplicationThread")) {
                    Context context = InvokerService.this.getBaseContext();
                    Field f = InvokerService.this.getBaseContext().getClass().getDeclaredField("mMainThread");
                    f.setAccessible(true);
                    Object oo = f.get(context);
                    Method m = oo.getClass().getDeclaredMethod("getApplicationThread");
                    m.setAccessible(true);
                    Object o1 = m.invoke(oo);
                    args[idx] = o1;
                }
                if(method.getParameterTypes()[idx].toString().contains("android.os.IBinder")) {
                    Context context = InvokerService.this.getBaseContext();
                    Method m = context.getClass().getDeclaredMethod("getActivityToken");
                    m.setAccessible(true);
                    Object oo = m.invoke(context);
                    if (oo != null) {
                        args[idx] = oo;
                    }
                }
                if(method.getParameterTypes()[idx].toString().contains("URI:")) {
                    Uri uri = Uri.parse(parameters[idx].split("URI:")[1]);
                    if (uri != null) {
                        args[idx] = uri;
                    } else {
                        args[idx] = null;
                    }
                }
                if(method.getParameterTypes()[idx].toString().contains("COMPONENT_NAME:")) {
                    String comp = parameters[idx].split("COMPONENT_NAME:")[1];
                    String[] pkgComp = comp.split(":");
                    args[idx] = new ComponentName(pkgComp[0], pkgComp[1]);
                }
                if(method.getParameterTypes()[idx].toString().contains("INTENT:")) {
                    Intent intent = new Intent();
                    String comp = parameters[idx].split("INTENT:")[1];
                    String[] pkgComp = comp.split(":");
                    ComponentName componentName = new ComponentName(pkgComp[0], pkgComp[1]);
                    intent.setComponent(componentName);
                    args[idx] = intent;
                }
                if(method.getParameterTypes()[idx].toString().contains("android.content.pm.ApplicationInfo")) {
                    ApplicationInfo app = this.getPackageManager().getApplicationInfo("fuzzer.permission.uidchanger", 0);
                    args[idx] = app;
                }
                if(method.getParameterTypes()[idx].toString().equals("class android.media.AudioAttributes")) {
                    args[idx] = null;
                }
            }

            /*
            if (args.length > 0) {
                reportParameters(args, method.getParameterTypes());
                Log.d(TAG, "===================> Parameters where reported");
            } else {
                Log.d(TAG, "===================> No parameters to report.");
            }
            */

            int idx = 1;
            for (Object arg : args) {
                Log.d(TAG, "Parameter #" + (idx++) + ": " + arg);
            }

            Log.d(TAG, "===================> Calling the API " +
                    "[" + fuzzingTask.getService() + "." + fuzzingTask.getMethod() + "]");
            o = method.invoke(binderService.getBinderInterface(), args);
            returns = true;
            Log.d(TAG, "===================> API RETURNED: " + o);
        } catch (IllegalAccessException | ClassNotFoundException | InvocationTargetException |
                RemoteException e) {
            String type = "FIRST EXCEPTION CAUGHT";
            Log.d("COVFEFE", type);
            e.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            exception =  type + "\n" +  sw.toString();
        } catch (Exception e) {
            String type = "General EXCEPTION CAUGHT";
            Log.d("COVFEFE", type);
            e.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            exception = type + "\n" + sw.toString();
        } finally {
            if (method != null)
                reportInvocationResult(fuzzingTask, args, method.getParameterTypes(), o, exception, returns);
            else
                reportInvocationResult(fuzzingTask, args, null, o, exception, returns);
        }
        Log.d(TAG, "===================> After the API is called " +
                "[" + fuzzingTask.getService() + "." + fuzzingTask.getMethod() + "]");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
