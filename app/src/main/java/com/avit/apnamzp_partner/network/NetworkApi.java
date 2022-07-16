package com.avit.apnamzp_partner.network;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.models.user.LoginPostData;
import com.avit.apnamzp_partner.models.user.ShopPartner;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkApi {
    String SERVER_URL = "http://192.168.63.85:5000/";
//    String SERVER_URL = "https://e5c8-2409-4063-2109-67d5-fccd-ef2e-fd04-126f.ngrok.io";

    @GET("/partner/getOrders")
    Call<List<OrderItem>> getAllOrders(@Query("shopCategory") String shopCategory,
                                       @Query("shopId") String shopId, @Query("orderStatus") int orderStatus,
                                       @Query("ordersDateString") String ordersDateString);

    @POST("/partner/order/updateStatus")
    Call<ResponseBody> updateOrderStatus(@Query("orderId") String orderId, @Query("orderStatus") int orderStatus,@Query("shopReceivedPayment") boolean shopReceivedPayment);

    @POST("/partner/assignDeliveryBoy")
    Call<NetworkResponse> assignDeliveryBoy(@Query("orderId") String orderId,@Query("latitude") String latitude,@Query("longitude") String longitude);

    @POST("/partner/login")
    Call<NetworkResponse> login(@Body LoginPostData loginPostData);

    @POST("/user_routes/updateFCM")
    Call<ResponseBody> updateFcmToken(@Body ShopPartner shopPartner);

    @GET("/partner/accept_order")
    Call<NetworkResponse> acceptOrder(@Query("order_id") String order_id,@Query("user_id") String user_id,@Query("expected_time") String exptectedTime);

    @GET("/partner/reject_order")
    Call<NetworkResponse> rejectOrder(@Query("order_id") String order_id,@Query("user_id") String user_id,@Query("cancel_reason") String cancelReason);

    @GET("/partner/getShopItems/{itemsId}")
    Call<List<ShopCategoryData>> getShopData(@Path("itemsId") String itemsId);

    @POST("/partner/update/menuitem")
    Call<NetworkResponse> updateMenuItem(@Query("shopItemsId") String shopItemsId, @Query("categoryName") String categoryName,@Query("isNewItem") boolean isNewItem,@Body ShopItemData shopItemData);

    @Multipart
    @POST("/partner/update/menuitem")
    Call<NetworkResponse> putMenuItem(@Query("shopItemsId") String shopItemsId, @Query("categoryName") String categoryName,@Query("isNewItem") boolean isNewItem,@Part MultipartBody.Part itemImagePart, @Part("shopItemData") RequestBody shopItemData);

    @POST("/partner/createNewCategory")
    Call<NetworkResponse> addNewCategory(@Query("shopItemsId") String shopItemsId, @Body ShopCategoryData shopCategoryData);

    @Multipart
    @POST("/partner/update/shopdetails")
    Call<NetworkResponse> updateShopDetails(@Part MultipartBody.Part bannerImage,@Part("shopData") RequestBody shopData);

    @GET("/partner/offers")
    Call<List<OfferItem>> getOffers(@Query("shopName") String shopName);

    @POST("/partner/offers")
    Call<NetworkResponse> putOffer(@Body OfferItem offerItem);

}
