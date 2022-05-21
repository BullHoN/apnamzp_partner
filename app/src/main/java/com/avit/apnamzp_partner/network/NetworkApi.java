package com.avit.apnamzp_partner.network;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.user.ShopPartner;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetworkApi {
    String SERVER_URL = "http://192.168.1.3:5000/";
//    String SERVER_URL = "http://da22-2401-4900-1f3b-392b-189b-5688-d508-410c.ngrok.io";

    @GET("/partner/getOrders")
    Call<List<OrderItem>> getAllOrders(@Query("shopCategory") String shopCategory,
                                       @Query("shopId") String shopId, @Query("orderStatus") int orderStatus);

    @POST("/partner/order/updateStatus")
    Call<ResponseBody> updateOrderStatus(@Query("orderId") String orderId, @Query("orderStatus") int orderStatus);

    @POST("/partner/assignDeliveryBoy")
    Call<NetworkResponse> assignDeliveryBoy(@Query("orderId") String orderId,@Query("latitude") String latitude,@Query("longitude") String longitude);

    @POST("/login")
    Call<ResponseBody> login(@Query("phoneNo") String phoneNo,@Query("password") String password);

    @POST("/user_routes/updateFCM")
    Call<ResponseBody> updateFcmToken(@Body ShopPartner shopPartner);

    @GET("/partner/accept_order")
    Call<NetworkResponse> acceptOrder(@Query("order_id") String order_id,@Query("user_id") String user_id,@Query("expected_time") String exptectedTime);

    @GET("/partner/reject_order")
    Call<NetworkResponse> rejectOrder(@Query("order_id") String order_id,@Query("user_id") String user_id,@Query("cancel_reason") String cancelReason);



}
