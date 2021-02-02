package fuzzer.permission.uidchanger.generators.primitive;

import java.security.SecureRandom;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class BooleanGenerator implements IValueGenerator <Boolean> {
    @Override
    public Class getType() {
        return Boolean.class;
    }

    @Override
    public Boolean getRandomMagicValue() {
        SecureRandom random = new SecureRandom();
        return random.nextBoolean();
    }

    @Override
    public Boolean[] getMagicValuesList() {
        return new Boolean[]{true, false};
    }
}
