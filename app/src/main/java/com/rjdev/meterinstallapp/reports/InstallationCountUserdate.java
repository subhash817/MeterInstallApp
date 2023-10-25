
package com.rjdev.meterinstallapp.reports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InstallationCountUserdate {

    @SerializedName("installation_id")
    @Expose
    private String installationId;
    @SerializedName("0")
    @Expose
    private String _0;
    @SerializedName("installation_type_name")
    @Expose
    private String installationTypeName;
    @SerializedName("1")
    @Expose
    private String _1;
    @SerializedName("install_count")
    @Expose
    private String installCount;
    @SerializedName("2")
    @Expose
    private String _2;

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String get0() {
        return _0;
    }

    public void set0(String _0) {
        this._0 = _0;
    }

    public String getInstallationTypeName() {
        return installationTypeName;
    }

    public void setInstallationTypeName(String installationTypeName) {
        this.installationTypeName = installationTypeName;
    }

    public String get1() {
        return _1;
    }

    public void set1(String _1) {
        this._1 = _1;
    }

    public String getInstallCount() {
        return installCount;
    }

    public void setInstallCount(String installCount) {
        this.installCount = installCount;
    }

    public String get2() {
        return _2;
    }

    public void set2(String _2) {
        this._2 = _2;
    }

}
