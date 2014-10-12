package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

public enum ReqType {
    PUTCOMMAND("put"),
    GETCOMMAND("get"),
    REMOVECOMMAND("remove"),
    LISTCOMMAND("list"),
    EXITCOMMAND("exit");
    private String retVal;
    private ReqType(String type) {
        retVal = type;
    }

    public static ReqType getType(String type) {
        for (ReqType ctType : ReqType.values()) {
            if (ctType.getTypeValue().equals(type)) {
                return ctType;
            }
        }
        throw new IllegalArgumentException(type + ": unknown command");
    }

    public String getTypeValue() {
        return retVal;
    }
}
