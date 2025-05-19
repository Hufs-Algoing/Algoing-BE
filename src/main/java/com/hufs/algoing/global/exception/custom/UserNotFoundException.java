package com.hufs.algoing.global.exception.custom;

import com.hufs.algoing.global.code.BaseErrorCode;
import com.hufs.algoing.global.exception.GeneralException;

public class UserNotFoundException extends GeneralException {
    public UserNotFoundException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
