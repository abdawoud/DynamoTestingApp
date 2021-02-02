package fuzzer.permission.uidchanger.generators.object;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fuzzer.permission.uidchanger.FuzzingManager;
import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class InterfaceGenerator implements IValueGenerator {

    private final Class clazz;

    public InterfaceGenerator(Class clazz){
        this.clazz = clazz;
    }

    @Override
    public Class getType() {
        return clazz;
    }

    @Override
    public Object getRandomMagicValue() {
        try {
            final String type = clazz.getName();
            final FuzzingManager mFuzzingManager = FuzzingManager.getInstance();
            Object interfaceObj = Proxy.newProxyInstance(
                    Class.forName(clazz.getName()).getClassLoader(),
                    new Class[]{Class.forName(type)},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            String method_name = method.getName();
                            if (method_name.equals("toString")) {
                                return String.format("[Generated Interface : %s]", type);
                            }

                            if (method_name.equals("asBinder")) {
                                try {
                                    return mFuzzingManager.getBinderService("wifi")
                                            .getBinderInterface().asBinder();
                                } catch (Exception e) {
                                    return null;
                                }

                            }
                            return proxy;
                        }
                    });;
                    return interfaceObj;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object[] getMagicValuesList() {
        return new Object[0];
        //return new Object[] {getRandomMagicValue()};
    }
}
