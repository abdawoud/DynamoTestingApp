package fuzzer.permission.uidchanger.generators.object;

import android.content.ComponentName;
import android.content.Intent;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class IntentGenerator implements IValueGenerator<Intent> {

    public IntentGenerator(){
    }

    @Override
    public Class getType() {
        return Intent.class;
    }

    @Override
    public Intent getRandomMagicValue() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("fuzzer.permission.uidchanger",".InvokerService"));
        return intent;
    }

    @Override
    public Intent[] getMagicValuesList() {
        //get packages, actions, and magic values and smash them together
        return new Intent[0];
    }
}
