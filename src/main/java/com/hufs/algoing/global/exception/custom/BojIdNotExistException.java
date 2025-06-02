package com.hufs.algoing.global.exception.custom;

import com.hufs.algoing.global.code.BaseErrorCode;
import com.hufs.algoing.global.exception.GeneralException;

public class BojIdNotExistException extends GeneralException {
    public BojIdNotExistException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
