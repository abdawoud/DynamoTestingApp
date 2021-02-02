package fuzzer.permission.uidchanger.generators.primitive;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class FloatGenerator implements IValueGenerator <Float> {
    @Override
    public Class getType() {
        return Float.class;
    }

    @Override
    public Float getRandomMagicValue() {
        return 1.0f;
    }

    @Override
    public Float[] getMagicValuesList() {
        return new Float[]{1.0f};
    }
}
