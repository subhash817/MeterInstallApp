package com.rjdev.meterinstallapp.Model;

import com.google.gson.annotations.SerializedName;

public class EndDuty {
    @SerializedName("ResponseCode")
    private String responseCode;
    @SerializedName("ResponseMessage")
    private String responseMessage;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

}
