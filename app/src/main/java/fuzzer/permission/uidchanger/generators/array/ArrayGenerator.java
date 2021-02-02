package fuzzer.permission.uidchanger.generators.array;

import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;

import fuzzer.permission.uidchanger.generators.IValueGenerator;
import fuzzer.permission.uidchanger.generators.object.ObjectGenerator;
import fuzzer.permission.uidchanger.generators.primitive.PrimitiveGenerator;

public class ArrayGenerator implements IValueGenerator<Object> {

    private Class clazz;
    private final IValueGenerator valueGenerator;

    public ArrayGenerator(Class clazz) {
        this.clazz = clazz;
        if (clazz.isPrimitive())
            this.valueGenerator = new PrimitiveGenerator(clazz);
        else if (clazz.isArray())
            this.valueGenerator = new ArrayGenerator(clazz.getComponentType());
        else
            this.valueGenerator = new ObjectGenerator(clazz);
    }

    @Override
    public Class getType() {
        return this.clazz;
    }

    @Override
    public Object getRandomMagicValue() {
        int length = 2;
        Object o = Array.newInstance(clazz, length);
        for (int i = 0; i< length; i++){
            Array.set(o, i, valueGenerator.getRandomMagicValue());
        }
        return o;
    }

    @Override
    public Object[] getMagicValuesList() {
        return new Object[0];
    }
}
