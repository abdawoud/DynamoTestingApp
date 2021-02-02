package fuzzer.permission.uidchanger.utils;

public class HelperUtils {

    public static String methodNameToDalvikName(String methodName, String returnType,
                                                Class[] paramTypes) {
        String dalvikParameters = "";
        for (Class paramType : paramTypes) {
            String dalvikParam;
            if (paramType.toString().contains(".")) {
                dalvikParam = String.format("L%s;", paramType.getCanonicalName().replaceAll("\\.", "/"));
            } else {
                dalvikParam = Constants.DALVIK_TYPES.get(paramType.toString());
                if (dalvikParam == null)
                    dalvikParam = paramType.toString().replace("class ", "");
            }
            if (dalvikParam.contains("[]")) {
                dalvikParam = "[" + dalvikParam.replace("[]", "");
            }
            dalvikParameters += dalvikParam;
        }

        String dalvikReturnType;
        if (returnType.contains(".")) {
            dalvikReturnType = String.format("L%s;", returnType.replaceAll("\\.", "/"));
        } else {
            dalvikReturnType = Constants.DALVIK_TYPES.get(returnType);
            if (dalvikReturnType == null)
                dalvikReturnType = returnType;
        }
        if (dalvikReturnType.contains("[]")) {
            dalvikReturnType = "[" + dalvikReturnType.replace("[]", "");
        }

        return String.format("%s(%s)%s", methodName, dalvikParameters, dalvikReturnType);
    }

}
