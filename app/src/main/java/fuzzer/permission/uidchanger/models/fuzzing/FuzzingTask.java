package fuzzer.permission.uidchanger.models.fuzzing;

import android.os.Parcel;
import android.os.Parcelable;

public class FuzzingTask implements Parcelable {
    private String service;
    private String method;
    private int iterations;
    private int intDefaultValue;
    private String stringDefaultValue;
    private String[] parameters;

    public FuzzingTask(String service, String method, int iterations) {
        this.setService(service);
        this.setMethod(method);
        this.setIterations(iterations);
    }

    public FuzzingTask(String service, String method) {
        this.setService(service);
        this.setMethod(method);
        this.setIterations(0);
    }

    public FuzzingTask() {
    }

    public FuzzingTask(Parcel in){
        this.setService(in.readString());
        this.setMethod(in.readString());
        this.setIterations(in.readInt());
        this.setIntDefaultValue(in.readInt());
        this.setStringDefaultValue(in.readString());
        this.setParameters(in.createStringArray());
    }

    public static final Creator<FuzzingTask> CREATOR = new Creator<FuzzingTask>() {
        @Override
        public FuzzingTask createFromParcel(Parcel in) {
            return new FuzzingTask(in);
        }

        @Override
        public FuzzingTask[] newArray(int size) {
            return new FuzzingTask[size];
        }
    };

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getIntDefaultValue() {
        return intDefaultValue;
    }

    public void setIntDefaultValue(int intDefaultValue) {
        this.intDefaultValue = intDefaultValue;
    }

    public String getStringDefaultValue() {
        return stringDefaultValue;
    }

    public void setStringDefaultValue(String stringDefaultValue) {
        this.stringDefaultValue = stringDefaultValue;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getService());
        parcel.writeString(this.getMethod());
        parcel.writeInt(this.getIterations());
        parcel.writeInt(this.getIntDefaultValue());
        parcel.writeString(this.getStringDefaultValue());
        parcel.writeStringArray(this.getParameters());
    }
}
