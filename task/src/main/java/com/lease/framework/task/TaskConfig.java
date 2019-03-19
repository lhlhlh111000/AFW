package com.lease.framework.task;

import com.lease.framework.task.core.ErrorTransformer;

public class TaskConfig {

    private ErrorTransformer errorTransformer;

    public ErrorTransformer getErrorTransformer() {
        return errorTransformer;
    }

    public void setErrorTransformer(ErrorTransformer errorTransformer) {
        this.errorTransformer = errorTransformer;
    }
}