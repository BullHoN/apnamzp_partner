package com.avit.apnamzp_partner.network;

import com.avit.apnamzp_partner.models.orders.OrderItem;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetworkApi {
    String SERVER_URL = "http://192.168.1.3:5000/";

    @GET("/partner/getOrders")
    Call<List<OrderItem>> getAllOrders(@Query("shopCategory") String shopCategory,
                                       @Query("shopId") String shopId, @Query("orderStatus") int orderStatus);

    @POST("/partner/order/updateStatus")
    Call<ResponseBody> updateOrderStatus(@Query("orderId") String orderId, @Query("orderStatus") int orderStatus);

    @POST("/login")
    Call<ResponseBody> login(@Query("phoneNo") String phoneNo,@Query("password") String password);

}
