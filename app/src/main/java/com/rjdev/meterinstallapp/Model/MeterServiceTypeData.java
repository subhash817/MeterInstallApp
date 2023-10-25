package com.rjdev.meterinstallapp.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MeterServiceTypeData  implements Serializable {
    @SerializedName("meter_service_type_id")
    @Expose
    private String meterServiceTypeId;
    @SerializedName("meter_service_type_name")
    @Expose
    private String meterServiceTypeName;

    public String getMeterServiceTypeId() {
        return meterServiceTypeId;
    }

    public void setMeterServiceTypeId(String meterServiceTypeId) {
        this.meterServiceTypeId = meterServiceTypeId;
    }

    public String getMeterServiceTypeName() {
        return meterServiceTypeName;
    }

    public void setMeterServiceTypeName(String meterServiceTypeName) {
        this.meterServiceTypeName = meterServiceTypeName;
    }
    @NonNull
    @Override
    public String toString() {
        return meterServiceTypeName;
    }
}
