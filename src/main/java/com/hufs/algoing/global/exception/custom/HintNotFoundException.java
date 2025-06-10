package com.hufs.algoing.global.exception.custom;

import com.hufs.algoing.global.code.BaseErrorCode;
import com.hufs.algoing.global.exception.GeneralException;

public class HintNotFoundException extends GeneralException {
    public HintNotFoundException(BaseErrorCode errorCode) {super(errorCode);
    }
}
