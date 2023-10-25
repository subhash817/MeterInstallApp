
package com.rjdev.meterinstallapp.reports;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportByUserDate {

    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("duty_start_data")
    @Expose
    private DutyStartData dutyStartData;
    @SerializedName("installation_count_userdate")
    @Expose
    private List<InstallationCountUserdate> installationCountUserdate;

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

    public DutyStartData getDutyStartData() {
        return dutyStartData;
    }

    public void setDutyStartData(DutyStartData dutyStartData) {
        this.dutyStartData = dutyStartData;
    }

    public List<InstallationCountUserdate> getInstallationCountUserdate() {
        return installationCountUserdate;
    }

    public void setInstallationCountUserdate(List<InstallationCountUserdate> installationCountUserdate) {
        this.installationCountUserdate = installationCountUserdate;
    }

}
