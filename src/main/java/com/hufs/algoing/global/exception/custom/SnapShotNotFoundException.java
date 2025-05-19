package com.hufs.algoing.global.exception.custom;

import com.hufs.algoing.global.code.BaseErrorCode;
import com.hufs.algoing.global.exception.GeneralException;

public class SnapShotNotFoundException extends GeneralException {
    public SnapShotNotFoundException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
