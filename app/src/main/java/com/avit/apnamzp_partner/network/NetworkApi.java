package com.avit.apnamzp_partner.network;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.payment.OnlinePaymentOrderIdPostData;
import com.avit.apnamzp_partner.models.payment.PaymentMetadata;
import com.avit.apnamzp_partner.models.shop.ReviewData;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.models.shop.ShopRegistrationPostData;
import com.avit.apnamzp_partner.models.subscription.BannerData;
import com.avit.apnamzp_partner.models.subscription.Subscription;
import com.avit.apnamzp_partner.models.user.LoginPostData;
import com.avit.apnamzp_partner.models.user.ShopPartner;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkApi {
    String SERVER_URL = "http://192.168.1.5:5000";
//    String SERVER_URL = "https://apnamzp.in/";

    @GET("/partner/getOrders")
    Call<List<OrderItem>> getAllOrders(@Query("shopCategory") String shopCategory,
                                       @Query("shopId") String shopId, @Query("orderStatus") int orderStatus,
                                       @Query("ordersDateString") String ordersDateString, @Query("isMonthly") boolean isMonthly);

    @POST("/partner/order/updateStatus")
    Call<NetworkResponse> updateOrderStatus(@Query("orderId") String orderId, @Query("orderStatus") int orderStatus,@Query("shopReceivedPayment") boolean shopReceivedPayment);

    @POST("/partner/assignDeliveryBoy")
    Call<NetworkResponse> assignDeliveryBoy(@Query("orderId") String orderId,@Query("latitude") String latitude,@Query("longitude") String longitude);

    @POST("/partner/login")
    Call<NetworkResponse> login(@Body LoginPostData loginPostData);

    @POST("/user_routes/updateFCM")
    Call<NetworkResponse> updateFcmToken(@Body ShopPartner shopPartner);

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
    Call<NetworkResponse> putMenuItem(@Query("deleteItem") boolean deleteItem,@Query("shopItemsId") String shopItemsId, @Query("categoryName") String categoryName,@Query("isNewItem") boolean isNewItem,@Part MultipartBody.Part itemImagePart, @Part("shopItemData") RequestBody shopItemData);

    @POST("/partner/createNewCategory")
    Call<NetworkResponse> addNewCategory(@Query("shopItemsId") String shopItemsId, @Body ShopCategoryData shopCategoryData);

    @Multipart
    @POST("/partner/update/shopdetails")
    Call<NetworkResponse> updateShopDetails(@Part MultipartBody.Part bannerImage,@Part("shopData") RequestBody shopData);

    @GET("/partner/offers")
    Call<List<OfferItem>> getOffers(@Query("shopName") String shopName);

    @POST("/partner/offers")
    Call<NetworkResponse> putOffer(@Body OfferItem offerItem);

    @POST("/partner/changeShopStatus")
    Call<NetworkResponse> changeShopStatus(@Query("phoneNo") String phoneNo,@Query("isOpen") boolean isOpen);

    @DELETE("/partner/offer/{offer_id}")
    Call<NetworkResponse> deleteOffer(@Path("offer_id") String offerId);

    @GET("/partner/shopStatus/{id}")
    Call<ShopPartner> getShopStatus(@Path("id") String shopId);

    @POST("/partner/editcategory")
    Call<NetworkResponse> editCategories(@Query("action") String query, @Query("categoryName") String categoryName,
                                         @Query("shopItemsId") String shopItemsId, @Query("newCategoryName") String newCategoryName);

    @GET("/partner/actionNeededOrders/{shopId}")
    Call<List<OrderItem>> getActionNeededOrders(@Path("shopId") String shopId);

    @POST("/partner/register-form")
    Call<NetworkResponse> sendRegisterForm(@Body ShopRegistrationPostData shopRegistrationPostData);

    @GET("/reviews")
    Call<List<ReviewData>> getReviews(@Query("shopName") String shopId);

    @GET("/partner/subscription/banner_images")
    Call<List<BannerData>> getSubscriptionBannerImages();

    @GET("/partner/subscription/{shopId}")
    Call<Subscription> getSubscription(@Path("shopId") String shopId);

    @POST("/partner/payment/subscription-payment-id")
    Call<PaymentMetadata> getOrderPaymentId(@Body OnlinePaymentOrderIdPostData postData);

    @POST("/partner/payment/checkout")
    Call<NetworkResponse> checkout(@Body Subscription subscription);

    @GET("/partner/getOrders/{orderId}")
    Call<OrderItem> getOrder(@Path("orderId") String orderId);

}
