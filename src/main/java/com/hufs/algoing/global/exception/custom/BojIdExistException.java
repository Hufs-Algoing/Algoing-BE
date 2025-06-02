package com.hufs.algoing.global.exception.custom;

import com.hufs.algoing.global.code.BaseErrorCode;
import com.hufs.algoing.global.exception.GeneralException;

public class BojIdExistException extends GeneralException {
    public BojIdExistException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
