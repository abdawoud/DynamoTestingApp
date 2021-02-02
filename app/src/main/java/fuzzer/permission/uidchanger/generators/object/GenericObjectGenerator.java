package fuzzer.permission.uidchanger.generators.object;

import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import fuzzer.permission.uidchanger.generators.IValueGenerator;
import fuzzer.permission.uidchanger.generators.array.ArrayGenerator;
import fuzzer.permission.uidchanger.generators.primitive.PrimitiveGenerator;

public class GenericObjectGenerator implements IValueGenerator {

    private final static String TAG = "COVFEFE:GenericObjGen";
    private Class clazz;

    private final List<String> NOT_GENERATABLE = new ArrayList<String>() {{
        add("android.os.Parcel");
        //add("android.content.Intent");
        add("android.content.Context");
        add("android.net.Uri");
        add("android.graphics.Bitmap");
    }};

    public GenericObjectGenerator(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class getType() {
        return this.clazz;
    }

    private Object getFuzzValue(Class type) {
        IValueGenerator valueGenerator;
        if (type.isPrimitive())
            valueGenerator = new PrimitiveGenerator(type);
        else if (type.isArray())
            valueGenerator = new ArrayGenerator(type.getComponentType());
        else
            valueGenerator = new ObjectGenerator(type);

        return valueGenerator.getRandomMagicValue();
    }

    private Object instantiateObject(Class type) {
        Log.d(TAG, "now trying to instantiate " + type.getName());

        if (NOT_GENERATABLE.contains(type.getName()))
            return null;

        if (!type.getName().equals(this.clazz.getName())) {
            Object res = getFuzzValue(type);
            if (res != null)
                return res;
        }

        if (type.getName().equals("java.lang.Object"))
            return null;

        try {
            Class profiler = Class.forName(type.getName());
            boolean hasConstructors = profiler.getDeclaredConstructors().length > 0;
            boolean canBeInstantiated;
            List<Object> parameters;
            Object fuzzedValue;

            if (!hasConstructors) {
                Log.d("COVFEFE", "Interface or abstract?");
                return null;
            }

            for (Constructor con : profiler.getDeclaredConstructors()) {
                con.setAccessible(true);

                parameters = new ArrayList<>();
                canBeInstantiated = true;

                for (Class param : con.getParameterTypes()) {
                    if (param.getName().equals(type.getName()) || NOT_GENERATABLE.contains(param.getName())) {
                        canBeInstantiated = false;
                        break;
                    }
                }

                if (!canBeInstantiated)
                    continue;

                for (Class param : con.getParameterTypes()) {
                    fuzzedValue = instantiateObject(param);
                    parameters.add(fuzzedValue);
                }
                return con.newInstance(parameters.toArray());
            }

            Log.d("COVFEFE", "[instantiateObject] Couldn't instantiate object of class " + type.getName());
            return null;
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "now trying to instantiate ClassNotFoundException " + type);
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.d(TAG, "now trying to instantiate IllegalAccessException " + type);
            //e.printStackTrace();
        } catch (InstantiationException e) {
            Log.d(TAG, "now trying to instantiate InstantiationException " + type);
            //e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.d(TAG, "now trying to instantiate InvocationTargetException " + type);
            //e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "now trying to instantiate Exception " + type);
            //e.printStackTrace();
        }

        return null;
    }

    @Override
    public Object getRandomMagicValue() {
        return instantiateObject(this.clazz);
    }

    @Override
    public Object[] getMagicValuesList() {
        return new Object[0];
        //return new Object[0];
    }
}
