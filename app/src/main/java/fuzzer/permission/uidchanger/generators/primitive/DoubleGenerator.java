package fuzzer.permission.uidchanger.generators.primitive;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class DoubleGenerator implements IValueGenerator <Double> {
    @Override
    public Class getType() {
        return Double.class;
    }

    @Override
    public Double getRandomMagicValue() {
        return null;
    }

    @Override
    public Double[] getMagicValuesList() {
        return null;
    }
}
