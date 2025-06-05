package com.hufs.algoing.global.exception.custom;

import com.hufs.algoing.global.code.BaseErrorCode;
import com.hufs.algoing.global.exception.GeneralException;

public class RecommendationLogNotFoundException extends GeneralException {
    public RecommendationLogNotFoundException(BaseErrorCode errorCode) {super(errorCode);
    }
}
