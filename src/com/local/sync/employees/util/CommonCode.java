package com.local.sync.employees.util;

public class CommonCode
{
    public static final int OK = 0;
    public static final int ERR_FILE_NOT_CREATED = -1;
    public static final int ERR_FTP_LOGIN_FAIL = -2;
    public static final int ERR_FTP_SENDING = -3;
    public static final int ERR_REMOTE_SERVER_TRG_FILE_ALREADY_EXIST = -4;
    public static final int ERR_FTP_CONNECT = -5;

    public static String getCodeValue(int code){
        String value = "";
        switch(code){
            case OK:
                value = "OK";
                break;
            case ERR_FILE_NOT_CREATED:
                value = "data file is not created";
                break;
            case ERR_FTP_LOGIN_FAIL:
                value = "Ftp login function is failed";
                break;
            case ERR_FTP_SENDING:
                value = "Error occurred when ftp processing";
                break;
            case ERR_REMOTE_SERVER_TRG_FILE_ALREADY_EXIST:
                value = "remote server's trigger file already exist";
                break;
            case ERR_FTP_CONNECT:
                value = "disconnect to the remote server";
                break;
            default:
                break;
        }
        return value;
    }
}
