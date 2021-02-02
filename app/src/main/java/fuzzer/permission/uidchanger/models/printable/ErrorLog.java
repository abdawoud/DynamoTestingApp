package fuzzer.permission.uidchanger.models.printable;

public class ErrorLog {

    private String msg;
    private String cause;

    public ErrorLog(String msg, String cause) {
        this.setMsg(msg);
        this.setCause(cause);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String toString() {
        return String.format("Error: %s - Cause: %s", this.msg, this.cause);
    }
}
