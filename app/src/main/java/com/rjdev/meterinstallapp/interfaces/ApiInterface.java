package com.rjdev.meterinstallapp.interfaces;

import com.rjdev.meterinstallapp.Model.AddMeter;
import com.rjdev.meterinstallapp.Model.EndDuty;
import com.rjdev.meterinstallapp.Model.MeterServiceType;
import com.rjdev.meterinstallapp.Model.StartDuty;
import com.rjdev.meterinstallapp.reports.ReportByUserDate;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiInterface {
    @Multipart
    @POST("index.php?apicall=duty_start")
    Call<StartDuty> updateStartDuty(@Part MultipartBody.Part startimage,
                                          @Part("start_reading") RequestBody start_reading,
                                          @Part("start_latitude") RequestBody start_latitude,
                                          @Part("start_longitude") RequestBody start_longitude,
                                          @Part("start_location") RequestBody start_location,
                                          @Part("userid") RequestBody userid,
                                          @Part("vehicle_id") RequestBody reading,
                                          @Part("group_id") RequestBody group_id);

    @Multipart
    @POST("index.php?apicall=duty_end")
    Call<EndDuty> updateEndDuty(@Part MultipartBody.Part endimage,
                                @Part("end_reading") RequestBody end_reading,
                                @Part("end_latitude") RequestBody end_latitude,
                                @Part("end_longitude") RequestBody end_longitude,
                                @Part("end_location") RequestBody end_location,
                                @Part("userid") RequestBody userid);

    @Multipart
    @POST("index.php?apicall=addmeter")
    Call<AddMeter> updateMeterDetail(@Part MultipartBody.Part meterimage,
                                     @Part MultipartBody.Part protocolimage,
                                     @PartMap Map<String, RequestBody> partMap);

    @Multipart
    @POST("index.php?apicall=addmeter")
    Call<AddMeter> updateMeterDetailOldMeter(
                                     @Part MultipartBody.Part meterimage,
                                     @Part MultipartBody.Part protocolimage,
                                     @Part MultipartBody.Part oldMeterImage,
                                     @PartMap Map<String, RequestBody> partMap);

    @Multipart
    @POST("index.php?apicall=addmeter")
    Call<AddMeter> updateMeterDetailDumpData(@PartMap Map<String, RequestBody> partMap);




    @GET("index.php?apicall=get_meter_service_type")
    Call<MeterServiceType> getMeterServiceType();



    @Multipart
    @POST("index.php?apicall=addmeter")
    Call<AddMeter> updateMeterDetailOthers(@Part MultipartBody.Part meterimage,
                                     @Part MultipartBody.Part protocolimage,
                                     @PartMap Map<String, RequestBody> partMap);


    @FormUrlEncoded
    @POST("index.php?apicall=getreportbyuserdate")
    Call<ReportByUserDate> getReportByUserDate(
            @Field("users_id") String users_id,
            @Field("sel_date") String sel_date);


}
