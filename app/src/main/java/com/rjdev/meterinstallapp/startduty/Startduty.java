
package com.rjdev.meterinstallapp.startduty;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Startduty {

    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
