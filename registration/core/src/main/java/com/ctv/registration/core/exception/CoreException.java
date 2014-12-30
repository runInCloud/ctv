package com.ctv.registration.core.exception;

import com.ctv.shared.model.ErrorCode;

/**
 * @author Timur Yarosh
 */
public class CoreException extends RuntimeException {

    private final int errorCode;

    public CoreException(ErrorCode errorData) {
        super(errorData.getMessage());
        errorCode = errorData.getCode();
    }

    public CoreException(ErrorCode errorData, Throwable cause) {
        super(errorData.getMessage(), cause);
        errorCode = errorData.getCode();
    }

    public int getErrorCode() {
        return errorCode;
    }

}
