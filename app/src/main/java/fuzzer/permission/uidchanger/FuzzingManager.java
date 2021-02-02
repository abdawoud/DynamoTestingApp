package fuzzer.permission.uidchanger;

import android.app.ActivityManager;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import fuzzer.permission.uidchanger.models.fuzzing.BinderService;
import fuzzer.permission.uidchanger.models.printable.ErrorLog;
import fuzzer.permission.uidchanger.utils.ReflectionUtils;
import fuzzer.permission.uidchanger.utils.ShellUtils;

public class FuzzingManager {
    private final String TAG = "COVFEFE:FuzzingManager";

    public enum ACTIONS {
        FUZZING_TASK,
    }

    private static volatile FuzzingManager sInstance;
    private HashMap<String, BinderService> mServices;
    private HashMap<Type, ArrayList<Object>> mMagicValues;

    private FuzzingManager() {
        if (sInstance != null) {
            throw new RuntimeException("Use getInstance() method to get " +
                    "the single instance of this class.");
        }
        if (mServices == null) {
            mServices = new HashMap<>();
        }
        if (mMagicValues == null) {
            mMagicValues = new HashMap<>();
        }
    }

    public static FuzzingManager getInstance() {
        if (sInstance == null) {
            synchronized (FuzzingManager.class) {
                if (sInstance == null) {
                    sInstance = new FuzzingManager();
                }
            }
        }
        return sInstance;
    }

    public HashMap<String, BinderService> getBinderServices() throws IllegalAccessException,
            ClassNotFoundException, InvocationTargetException, RemoteException {

        if (mServices.size() > 0)
            return mServices;

        int numResolved = 0;
        String[] serviceList;
        BinderService binderService;

        if (!ShellUtils.isSELinuxDisabled()) {
            throw new IllegalAccessException(
                    new ErrorLog("Cannot retrieve Binder interfaces of system services",
                            "SELinux is enabled").toString());
        }

        serviceList = ReflectionUtils.getSystemServiceNames();

        for (String serviceName : serviceList) {
            IInterface binderInterface;
            if (serviceName.equals("activity")) {
                binderInterface = ReflectionUtils.getAmsInterface();
            } else {
                binderInterface = ReflectionUtils.getResolvedSystemService(serviceName);
            }

            if (binderInterface == null) {
                Log.d(TAG, String.format("Failed to resolve %s", serviceName));
                continue;
            }

            binderService = new BinderService(binderInterface, serviceName,
                    binderInterface.asBinder().getInterfaceDescriptor());
            mServices.put(serviceName, binderService);
            numResolved++;
            Log.d(TAG, String.format("Successfully resolved %s to %s ",
                    serviceName, binderService.getQualifiedName()));
        }
        Log.d(TAG, String.format("Resolved %d/%d services.", numResolved, serviceList.length));
        return mServices;
    }

    public BinderService getBinderService(String name) throws IllegalAccessException,
            ClassNotFoundException, InvocationTargetException, RemoteException {
        if (mServices.size() == 0) {
            getBinderServices();
        }
        BinderService binderService = mServices.get(name);
        return binderService;
    }

    public Method getBinderServiceMethod(BinderService service, String method)  throws IllegalAccessException,
            ClassNotFoundException, InvocationTargetException, RemoteException {
        return getBinderServiceMethod(service.getName(), method);
    }

    public Method getBinderServiceMethod(String service, String method)  throws IllegalAccessException,
            ClassNotFoundException, InvocationTargetException, RemoteException {
        Method[] methods = getBinderServiceMethods(service);
        for (Method m : methods) {
            if (m.getName().equals(method))
                return m;
        }
        return null;
    }

    public Method[] getBinderServiceMethods(BinderService service)  throws IllegalAccessException,
            ClassNotFoundException, InvocationTargetException, RemoteException {
        return getBinderServiceMethods(service.getName());
    }

    public Method[] getBinderServiceMethods(String service)  throws IllegalAccessException,
            ClassNotFoundException, InvocationTargetException, RemoteException {
        Method[] methods = null;
        BinderService binderService = getBinderService(service);
        if (service == null || binderService == null) {
            new NullPointerException(new ErrorLog("Can not get binder service methods",
                    "service=null or BinderService=null").toString()).printStackTrace();
            return null;
        }
        try {
            methods = Class.forName(binderService.getQualifiedName()).getDeclaredMethods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methods;
    }

    /**
     * Iterates over all classes preloaded in Zygote and extracts their initialized attributes.
     */
    public HashMap<Type, ArrayList<Object>> getMagicValues() throws IOException {

        if (mMagicValues.size() > 0)
            return mMagicValues;

        String preloadedClasses = "/system/etc/preloaded-classes";
        BufferedReader bufferedReader;

        bufferedReader = new BufferedReader(new FileReader(preloadedClasses));
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            try {
                if (line.contains("#"))
                    continue;
                Class clazz = ReflectionUtils.getClassByName(line);
                for (Field field : ReflectionUtils.getClassFields(clazz)) {
                    try {
                        insertUniqueMagicValue(field.getType(), field.get(clazz));
                    } catch (Exception e) {
                        //ignore
                    }
                }
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
        bufferedReader.close();

        return mMagicValues;
    }

    private void insertUniqueMagicValue(Type key, final Object value) {
        if (!mMagicValues.containsKey(key)) {
            mMagicValues.put(key, new ArrayList<Object>() {{
                add(value);
            }});
        } else {
            ArrayList<Object> list = mMagicValues.get(key);
            if (!list.contains(value)) {
                list.add(value);
                mMagicValues.put(key, list);
            }
        }
    }
}
