package fuzzer.permission.uidchanger.utils;

import java.util.HashMap;

public interface Constants {
    String ALL_SERVICES = "*";
    String PREPARE_BINDER_HANDLES = "prepare_binder_handles";
    HashMap<String, String> DALVIK_TYPES = new HashMap<String, String>(){{
        put("void", "V");
        put("boolean", "Z");
        put("byte", "B");
        put("short", "S");
        put("char", "C");
        put("int", "I");
        put("long", "J");
        put("float", "F");
        put("double", "D");
    }};
}
