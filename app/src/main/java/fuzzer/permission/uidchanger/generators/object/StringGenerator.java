package fuzzer.permission.uidchanger.generators.object;

import java.security.SecureRandom;

import fuzzer.permission.uidchanger.generators.IValueGenerator;
import fuzzer.permission.uidchanger.utils.ShellUtils;

public class StringGenerator implements IValueGenerator<String> {
    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public String getRandomMagicValue() {
        SecureRandom secureRandom = new SecureRandom();
        String[] values = getMagicValuesList();
        //return values[secureRandom.nextInt(values.length)];
        return "fuzzer.permission.uidchanger";//"fuzzer.permission.uidchanger.InvokerService";
    }

    @Override
    public String[] getMagicValuesList() {
        String[] pkgs = ShellUtils.getInstalledPackages();
        return pkgs;
    }
}
