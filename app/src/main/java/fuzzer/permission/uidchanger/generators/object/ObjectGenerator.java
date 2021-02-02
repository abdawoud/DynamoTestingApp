package fuzzer.permission.uidchanger.generators.object;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.io.FileDescriptor;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class ObjectGenerator implements IValueGenerator<Object> {

    private Class clazz;
    private final IValueGenerator valueGenerator;

    public ObjectGenerator(Class clazz) {
        this.clazz = clazz;

        Log.d("COVFEFE", "I am in: A new object need to be generated!: " + clazz);

        if (clazz.toString().startsWith("interface")) {
            if (clazz.toString().contains("android.os.IBinder"))
                valueGenerator = new IBinderGenerator();
            else
                valueGenerator = new InterfaceGenerator(clazz);
        } else if (clazz.equals(String.class)) {
            valueGenerator = new StringGenerator();
        } else if (clazz.equals(ComponentName.class)) {
            valueGenerator = new ComponentNameGenerator();
        } else if (clazz.equals(Intent.class)) {
            valueGenerator = new IntentGenerator();
        }  else if (clazz.equals(FileDescriptor.class)) {
            valueGenerator = new FileDescriptorGenerator();
        } else
            this.valueGenerator = new GenericObjectGenerator(clazz);
    }

    @Override
    public Class getType() {
        return this.clazz;
    }

    @Override
    public Object getRandomMagicValue() {
        //return null;
        return valueGenerator.getRandomMagicValue();
    }

    @Override
    public Object[] getMagicValuesList() {
        return new Object[0];
        //return valueGenerator.getMagicValuesList();
    }
}
