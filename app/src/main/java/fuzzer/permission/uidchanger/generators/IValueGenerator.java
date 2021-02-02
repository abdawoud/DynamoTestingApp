package fuzzer.permission.uidchanger.generators;

public interface IValueGenerator<T> {
    public Class getType();
    public T getRandomMagicValue();
    public T[] getMagicValuesList();
}
