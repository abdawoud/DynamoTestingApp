package fuzzer.permission.uidchanger.generators.primitive;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class PrimitiveGenerator implements IValueGenerator<Object> {

    private final Class parameterType;
    private final IValueGenerator valueGenerator;

    public PrimitiveGenerator(Class parameterType) {

        if (!parameterType.isPrimitive())
            throw new RuntimeException(parameterType.getName() + " is not primitive!");
        this.parameterType = parameterType;

        if (parameterType.equals(Byte.TYPE))
            this.valueGenerator = new ByteGenerator();
        else if (parameterType.equals(Short.TYPE))
            this.valueGenerator = new ShortGenerator();
        else if (parameterType.equals(Integer.TYPE))
            this.valueGenerator = new IntegerGenerator();
        else if (parameterType.equals(Long.TYPE))
            this.valueGenerator = new LongGenerator();
        else if (parameterType.equals(Float.TYPE))
            this.valueGenerator = new FloatGenerator();
        else if (parameterType.equals(Double.TYPE))
            this.valueGenerator = new DoubleGenerator();
        else if (parameterType.equals(Boolean.TYPE))
            this.valueGenerator = new BooleanGenerator();
        else if (parameterType.equals(Character.TYPE))
            this.valueGenerator = new CharacterGenerator();
        else
            throw new RuntimeException("undefined behaviour for primitive " + parameterType.getName());

    }

    @Override
    public Class getType() {
        return valueGenerator.getType();
    }

    @Override
    public Object getRandomMagicValue() {
        return valueGenerator.getRandomMagicValue();
    }

    @Override
    public Object[] getMagicValuesList() {
        return valueGenerator.getMagicValuesList();
    }
}
