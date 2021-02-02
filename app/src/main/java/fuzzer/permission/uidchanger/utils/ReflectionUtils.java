package fuzzer.permission.uidchanger.utils;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    private static Method getMethod(Class clazz, String name) {
         // @TODO: Investigate why does getDeclaredMethod(String) fails in some cases.
         // -- The code below is a workaround that works fine
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().contains(name)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    public static String[] getSystemServiceNames() throws ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {

        String[] serviceNames = {};
        @SuppressLint("PrivateApi") Class localClass = Class.forName("android.os.ServiceManager");
        Method method = getMethod(localClass, "listServices");
        String[] serviceList = (String[]) method.invoke(null);
        return (serviceList != null && serviceList.length > 0) ? serviceList : serviceNames;
    }

    public static IInterface getAmsInterface() {
        IInterface iInterface = null;
        try {
            Class amn = Class.forName("android.app.ActivityManagerNative");
            Method defaultAmn  = amn.getMethod("getDefault");
            iInterface = (IInterface) defaultAmn.invoke(amn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return iInterface;
    }

    public static IInterface getResolvedSystemService(String service) throws ClassNotFoundException,
            IllegalAccessException, InvocationTargetException, RemoteException {

        @SuppressLint("PrivateApi") Class localClass = Class.forName("android.os.ServiceManager");
        Method getServiceMethod = getMethod(localClass, "getService");
        if (getServiceMethod == null)
            return null;

        IBinder binder = (IBinder) getServiceMethod.invoke(localClass, service);
        if (binder == null)
            return null;

        String stubClassName = binder.getInterfaceDescriptor() + "$Stub";
        Class interfaceStub;
        try {
            interfaceStub = Class.forName(stubClassName);
        } catch (ClassNotFoundException e) {
            /*
            String intface = binder.getInterfaceDescriptor();
            if (intface != null && intface.equals("android.display.IQService")) {
                for (int i = 0; i < 1000; i++) {
                    try {
                        Parcel data = Parcel.obtain();
                        Parcel data2 = Parcel.obtain();
                        data.writeInterfaceToken(intface);
                        binder.transact(i, data, data2, 0);
                        data.recycle();
                        data2.recycle();
                    } catch (Exception ee) {
                        Log.d("COVFEFE", intface + " = " + i + " - " + ee.getMessage());
                    }
                }
            } else {
                Log.d("COVFEFE", service + " produced null interface name!");
            }
            */
            return null;
        }

        Method asInterfaceMethod  = getMethod(interfaceStub, "asInterface");
        if (asInterfaceMethod == null)
            return null;

        Object obj = asInterfaceMethod.invoke(null, binder);
        return obj != null ? (IInterface) obj : null;
    }

    public static Class getClassByName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public static Field[] getClassFields(Class clazz) {
        return clazz.getDeclaredFields();
    }
}
