package fuzzer.permission.uidchanger.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShellUtils {

    public static boolean isSELinuxDisabled() {
        @SuppressLint("WrongThread")
        CommandResult result = Shell.run("getenforce");
        return true; //result.isSuccessful() && result.getStdout().contains("Permissive");
    }

    public static String[] getInstalledPackages() {
        @SuppressLint("WrongThread")
        CommandResult result = Shell.run("pm list packages -f");
        if (!result.isSuccessful())
            return null;

        ArrayList<String> packages = new ArrayList<>();
        String[] apksPackages = result.getStdout().split("\n");
        for (String value : apksPackages) {
            packages.add(value.split("=")[1]);
        }
        return packages.toArray(new String[apksPackages.length]);
    }

}
