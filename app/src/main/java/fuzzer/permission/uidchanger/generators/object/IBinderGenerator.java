package fuzzer.permission.uidchanger.generators.object;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class IBinderGenerator implements IValueGenerator<IBinder> {

    public IBinderGenerator(){}

    @Override
    public Class getType() {
        return Intent.class;
    }

    @Override
    public IBinder getRandomMagicValue() {
        return new Binder();
    }

    @Override
    public IBinder[] getMagicValuesList() {
        //get packages, actions, and magic values and smash them together
        return new IBinder[0];
    }
}
