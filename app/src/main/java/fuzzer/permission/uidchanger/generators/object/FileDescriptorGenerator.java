package fuzzer.permission.uidchanger.generators.object;

import android.content.Intent;

import java.io.FileDescriptor;
import java.lang.reflect.Field;

import fuzzer.permission.uidchanger.generators.IValueGenerator;

public class FileDescriptorGenerator implements IValueGenerator<FileDescriptor> {

    public FileDescriptorGenerator(){
    }

    @Override
    public Class getType() {
        return Intent.class;
    }

    @Override
    public FileDescriptor getRandomMagicValue() {
        Class c = null;
        try {
            c = Class.forName("java.io.FileDescriptor");
            Object fd = c.newInstance();
            Field f = fd.getClass().getDeclaredField("descriptor");
            f.setAccessible(true);
            f.set(fd, 10);
            return (FileDescriptor) fd;
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
        } catch (InstantiationException e) {
            //e.printStackTrace();
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }
        return null;
    }

    @Override
    public FileDescriptor[] getMagicValuesList() {
        //get packages, actions, and magic values and smash them together
        return null;
    }
}
