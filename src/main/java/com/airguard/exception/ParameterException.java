package com.airguard.exception;

public class ParameterException extends  CommonException {

    public static final int PARAMETER_EXCEPTION = 700;
    public static final int NULL_PARAMETER_EXCEPTION = 710;
    public static final int NULL_ID_PARAMETER_EXCEPTION = 711;
    public static final int NULL_PW_PARAMETER_EXCEPTION = 712;
    public static final int NULL_PHONE_PARAMETER_EXCEPTION = 713;
    public static final int NULL_EMAIL_PARAMETER_EXCEPTION = 714;
    public static final int NULL_REGION_NUMBER_PARAMETER_EXCEPTION = 715;
    public static final int NULL_SERIAL_PARAMETER_EXCEPTION = 716;
    public static final int NULL_LAT_LON_PARAMETER_EXCEPTION = 717;
    public static final int NULL_D_CODE_PARAMETER_EXCEPTION = 718;
    public static final int NULL_TYPE_PARAMETER_EXCEPTION = 719;
    public static final int WRONG_PARAMETER_EXCEPTION = 730;
    public static final int ILLEGAL_ID_PW_PARAMETER_EXCEPTION = 731;
    public static final int ILLEGAL_PHONE_PARAMETER_EXCEPTION = 732;
    public static final int ILLEGAL_EMAIL_PARAMETER_EXCEPTION = 733;
    public static final int ILLEGAL_REGION_NUMBER_PARAMETER_EXCEPTION = 734;
    public static final int ILLEGAL_TELEPHONE_PARAMETER_EXCEPTION = 735;
    public static final int ILLEGAL_SERIAL_PARAMETER_EXCEPTION = 736;
    public static final int ILLEGAL_LAT_LON_PARAMETER_EXCEPTION = 737;
    public static final int ILLEGAL_D_CODE_PARAMETER_EXCEPTION = 738;
    public static final int ILLEGAL_TYPE_PARAMETER_EXCEPTION = 739;
    public static final int ILLEGAL_MODE_PARAMETER_EXCEPTION = 740;

    public ParameterException(int errorCode) {
        super(errorCode);
    }

}
