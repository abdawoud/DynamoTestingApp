package fuzzer.permission.uidchanger.models.fuzzing;

import android.os.IInterface;

public class BinderService {
    private IInterface binderInterface;
    private String name;
    private String qualifiedName;

    public BinderService(IInterface binderInterface, String name, String qualifiedName) {
        this.setBinderInterface(binderInterface);
        this.setName(name);
        this.setQualifiedName(qualifiedName);
    }

    public IInterface getBinderInterface() {
        return binderInterface;
    }

    public void setBinderInterface(IInterface binderInterface) {
        this.binderInterface = binderInterface;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
}
