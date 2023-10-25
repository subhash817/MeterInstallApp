package com.rjdev.meterinstallapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MeterServiceType implements Serializable {
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("Response")
    @Expose
    private List<MeterServiceTypeData> meterServiceTypeDataList = null;

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

    public List<MeterServiceTypeData> getMeterServiceTypeDataList() {
        return meterServiceTypeDataList;
    }

    public void setResponse(List<MeterServiceTypeData> meterServiceTypeDataList) {
        this.meterServiceTypeDataList = meterServiceTypeDataList;
    }
}
