package com.threess.summership.treasurehunt.model.repository;

public class GenericResponse {
    private String mErrorMessage;
    private boolean mIsSuccess;

    GenericResponse(boolean IsSuccess) {
        this.mIsSuccess = IsSuccess;
    }

    public GenericResponse(String ErrorMessage, boolean IsSuccess) {
        this.mErrorMessage = ErrorMessage;
        this.mIsSuccess = IsSuccess;
    }

    public boolean IsSuccess() {
        return mIsSuccess;
    }

    void setIsSuccess(boolean IsSuccess){
            this.mIsSuccess = IsSuccess;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }

}

