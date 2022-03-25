package com.task4j;

public class ExceptionUtil {

    private static final String packageNamePrefix = "com.task4j.";

    public static String simplifyException(Exception e) {
        StringBuilder s = new StringBuilder();
        s.append("\n------------------------------------------");
        s.append("\n").append(e.getMessage()).append(" ");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            StackTraceElement st = e.getStackTrace()[i];
            if (!st.getClassName().startsWith(packageNamePrefix)) continue;
            if (st.getFileName() != null) {
                s.append(st.getFileName().replace(".java", ""));
            } else {
                s.append(st.getClassName());
            }
            s.append(".").append(st.getMethodName()).append(":").append(st.getLineNumber());
            if (i < e.getStackTrace().length - 1) {
                s.append(" → ");
            }
        }
        s.append("\n------------------------------------------\n");
        return s.toString();
    }

}
