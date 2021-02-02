package fuzzer.permission.uidchanger.generators.primitive;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class CharacterGenerator implements IValueGenerator <Character> {
    @Override
    public Class getType() {
        return Character.class;
    }

    @Override
    public Character getRandomMagicValue() {
        return null;
    }

    @Override
    public Character[] getMagicValuesList() {
        return null;
    }
}
