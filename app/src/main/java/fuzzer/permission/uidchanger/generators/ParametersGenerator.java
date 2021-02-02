package fuzzer.permission.uidchanger.generators;

import android.os.IBinder;
import android.util.Log;

import fuzzer.permission.uidchanger.generators.array.ArrayGenerator;
import fuzzer.permission.uidchanger.generators.object.ObjectGenerator;
import fuzzer.permission.uidchanger.generators.primitive.PrimitiveGenerator;

public class ParametersGenerator {

    private final static String TAG = "COVFEFE:ParamsGenerator";
    private final Class[] parameterTypes;
    private IValueGenerator[] valueGenerators;

    public ParametersGenerator(Class...parameterTypes) {
        this.parameterTypes = parameterTypes;
        if (parameterTypes == null || parameterTypes.length == 0){
            throw new RuntimeException("parameterTypes is null or has no elements");
        }

        StringBuilder stringBuilder = new StringBuilder("new parameters generator: ");
        for (Class klass : parameterTypes){
            stringBuilder.append(klass.getName());
            stringBuilder.append(" ");
        }

        valueGenerators = new IValueGenerator[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
        {
            if (parameterTypes[i].isPrimitive())
                valueGenerators[i] = new PrimitiveGenerator(parameterTypes[i]);
            else if (parameterTypes[i].isArray())
                valueGenerators[i] = new ArrayGenerator(parameterTypes[i].getComponentType());
            else
                valueGenerators[i] = new ObjectGenerator(parameterTypes[i]);
        }


    }

    public Class[] getTypes() {
        return parameterTypes;
    }

    public Object[] getOneRandomPermutation() {
        Object[] parameters = new Object[valueGenerators.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = valueGenerators[i].getRandomMagicValue();

            valueGenerators[i].getMagicValuesList();

            try {
                Log.d(TAG, valueGenerators[i].getType() + " - " + parameters[i]);
            } catch (Exception e) {
                //pass
            }
        }
        return parameters;
    }

    public Object[][] getAllMagicValuesPermutations() {
        return null;
    }
    //generate all permutations of magic values
    //construct random magic parameters for one run
    //construct random parameters for one run
}
