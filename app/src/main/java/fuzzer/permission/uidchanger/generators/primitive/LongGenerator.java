package fuzzer.permission.uidchanger.generators.primitive;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class LongGenerator implements IValueGenerator <Long> {
    @Override
    public Class getType() {
        return Long.class;
    }

    @Override
    public Long getRandomMagicValue() {
        return 1L;
    }

    @Override
    public Long[] getMagicValuesList() {
        return null;
    }
}
