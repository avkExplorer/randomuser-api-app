package com.randomuser.model;

public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;
    private int totalRecords;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, Object data, int totalRecords) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.totalRecords = totalRecords;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

}
