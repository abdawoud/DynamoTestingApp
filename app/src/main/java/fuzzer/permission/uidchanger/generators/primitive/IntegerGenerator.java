package fuzzer.permission.uidchanger.generators.primitive;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import fuzzer.permission.uidchanger.FuzzingManager;
import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class IntegerGenerator implements IValueGenerator <Integer> {
    private int DUMMY = 0;

    @Override
    public Class getType() {
        return Integer.class;
    }

    @Override
    public Integer getRandomMagicValue() {
        return 1;
        //return new SecureRandom().nextInt();
    }

    @Override
    public Integer[] getMagicValuesList() {
        try {
            FuzzingManager fuzzingManager = FuzzingManager.getInstance();
            HashMap<Type, ArrayList<Object>> magicValues = fuzzingManager.getMagicValues();
            Field field = Class.forName(IntegerGenerator.this.getClass().getCanonicalName())
                    .getDeclaredField("DUMMY");
            field.setAccessible(true);
            Type type = field.getType();
            return magicValues.get(type).toArray(new Integer[magicValues.get(type).size()]);
        } catch (Exception e) {/* do nothing */
        e.printStackTrace();}
        return null;
    }
}
