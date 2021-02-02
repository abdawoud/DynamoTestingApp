package fuzzer.permission.uidchanger.configurations;

public class ConfigurationManager {

    public static int getArrayMaxSize() {
        /*
         * the smallest 'largest contiguous free bytes' observed is 4096 (64 is the squared root to
         * guarantee at most an array of array)
         */
        return 64;
    }
}
