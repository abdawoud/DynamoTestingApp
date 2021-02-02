package fuzzer.permission.uidchanger.generators.primitive;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class ShortGenerator implements IValueGenerator <Short> {
    @Override
    public Class getType() {
        return Short.class;
    }

    @Override
    public Short getRandomMagicValue() {
        return null;
    }

    @Override
    public Short[] getMagicValuesList() {
        return null;
    }
}
