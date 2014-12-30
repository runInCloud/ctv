package com.ctv.registration.core.exception;

import com.ctv.shared.model.ErrorCode;

/**
 * @author Timur Yarosh
 */
public class ResourceNotFoundException extends CoreException {
    public ResourceNotFoundException(ErrorCode errorData) {
        super(errorData);
    }
}
