package fuzzer.permission.uidchanger.generators.object;

import android.content.ComponentName;
import android.content.Intent;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class ComponentNameGenerator implements IValueGenerator<ComponentName> {

    public ComponentNameGenerator(){
    }

    @Override
    public Class getType() {
        return Intent.class;
    }

    @Override
    public ComponentName getRandomMagicValue() {
        return new ComponentName("com.meraki.sm", "com.meraki.sm.DeviceAdmin");
    }

    @Override
    public ComponentName[] getMagicValuesList() {
        return new ComponentName[] {new ComponentName("com.meraki.sm", "com.meraki.sm.DeviceAdmin")};
    }
}
