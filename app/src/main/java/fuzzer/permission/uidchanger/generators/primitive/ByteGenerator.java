package fuzzer.permission.uidchanger.generators.primitive;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import fuzzer.permission.uidchanger.FuzzingManager;
import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class ByteGenerator implements IValueGenerator <Byte> {
    @Override
    public Class getType() {
        return Byte.class;
    }

    @Override
    public Byte getRandomMagicValue() {
        byte[] bytesArray = new byte[1];
        new SecureRandom().nextBytes(bytesArray);
        return bytesArray[0];
    }

    @Override
    public Byte[] getMagicValuesList() {
        try {
            FuzzingManager fuzzingManager = FuzzingManager.getInstance();
            HashMap<Type, ArrayList<Object>> magicValues = fuzzingManager.getMagicValues();
            for (Type p : magicValues.keySet()) {
                Log.d("COVFEFE", p.toString());
            }
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
