package com.airguard.exception;

public class SQLException extends CommonException {

    public static final int SQL_EXCEPTION = 900;
    public static final int NULL_TARGET_EXCEPTION = 910;
    public static final int DUPLICATE_TARGET_EXCEPTION = 920;
    public static final int LIMIT_TARGET_EXCEPTION = 930;
    public static final int USER_LOGIN_ROCK_EXCEPTION = 931;
    public static final int NOT_USE_DATA_EXCEPION = 940;
    public static final int REDIS_SQL_EXCEPTION = 990;

    public SQLException(int errorCode) {
        super(errorCode);
    }

}
