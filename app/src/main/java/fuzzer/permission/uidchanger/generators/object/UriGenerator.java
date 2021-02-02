package fuzzer.permission.uidchanger.generators.object;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class UriGenerator implements IValueGenerator<Uri> {

    public UriGenerator(){
    }

    @Override
    public Class getType() {
        return Intent.class;
    }

    @Override
    public Uri getRandomMagicValue() {
        return Uri.parse("content://user_dictionary/words");
    }

    @Override
    public Uri[] getMagicValuesList() {
        //get packages, actions, and magic values and smash them together
        return new Uri[0];
    }
}
