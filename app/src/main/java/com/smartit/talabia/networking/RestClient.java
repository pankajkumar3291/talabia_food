package com.smartit.talabia.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartit.talabia.BuildConfig;
import com.smartit.talabia.entity.account.EOForgetPassword;
import com.smartit.talabia.entity.account.EOProfileImage;
import com.smartit.talabia.entity.account.EORegister;
import com.smartit.talabia.entity.account.EOUserDetails;
import com.smartit.talabia.entity.account.EOVerifyUser;
import com.smartit.talabia.entity.allproducts.EOAllProducts;
import com.smartit.talabia.entity.dashboard.EOBannerImageList;
import com.smartit.talabia.entity.dashboard.EODashboardCategoryList;
import com.smartit.talabia.entity.dashboard.EOImageCategoryList;
import com.smartit.talabia.entity.productDetails.EOProductDetails;
import com.smartit.talabia.expabdable.EOAllCategoryList;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

import static com.smartit.talabia.util.Constants.BASE_URL;

public class RestClient {

    public static APIInterface getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(logging)
                .readTimeout(60, TimeUnit.SECONDS).build();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        return retrofit.create(APIInterface.class);
    }

    public interface APIInterface {

        /*@Headers("Content-Type: application/json")*/
        @FormUrlEncoded
        @POST
        Call<EORegister> registerCustomer(@Url String url,
                                          @Field("fname") String fName,
                                          @Field("lname") String lName,
                                          @Field("email") String email,
                                          @Field("password") String password,
                                          @Field("phone") String phone,
                                          @Field("dob") String dob);


        @FormUrlEncoded
        @POST
        Call<EORegister> loginCustomer(@Url String url,
                                       @Field("email") String email,
                                       @Field("password") String password);


        @FormUrlEncoded
        @POST
        Call<EOVerifyUser> verifyCustomer(@Url String url,
                                          @Field("otp") String otp,
                                          @Field("user_id") String userId);


        @GET()
        Call<EOAllCategoryList> getAllCategories(@Url String url);

        @GET()
        Call<EOBannerImageList> getBannerImages(@Url String url);

        @GET()
        Call<EOImageCategoryList> getImagesCategory(@Url String url);

        @GET()
        Call<EODashboardCategoryList> getCollectionsApi(@Url String url);

        @FormUrlEncoded
        @POST
        Call<EOAllProducts> dashboardProductCategory(@Url String url,
                                                     @Field("id") String productId,
                                                     @Field("perpage") String perPageItems);

        @FormUrlEncoded
        @POST
        Call<EORegister> socialLoginCustomer(@Url String url,
                                             @Field("fname") String fName,
                                             @Field("lname") String lName,
                                             @Field("email") String email,
                                             @Field("googleId") String googleId);

        @FormUrlEncoded
        @POST
        Call<EOForgetPassword> forgetPassword(@Url String url,
                                              @Field("email") String emailId);

        @FormUrlEncoded
        @POST
        Call<EOForgetPassword> matchOtpApi(@Url String url,
                                           @Field("user_id") String userId,
                                           @Field("otp") String otp);

        @FormUrlEncoded
        @POST
        Call<EOForgetPassword> resetPassword(@Url String url,
                                             @Field("email") String userId,
                                             @Field("password") String password);

        @FormUrlEncoded
        @POST
        Call<EOForgetPassword> changePassword(@Url String url,
                                              @Field("user_id") String userId,
                                              @Field("old_password") String oldPassword,
                                              @Field("new_password") String newPassword);

        @FormUrlEncoded
        @POST
        Call<EOUserDetails> userDetails(@Url String url, @Field("user_id") String userId);

        @FormUrlEncoded
        @POST
        Call<EOForgetPassword> updateProfile(@Url String url,
                                             @Field("user_id") String userId,
                                             @Field("first_name") String firstName,
                                             @Field("last_name") String lastName,
                                             @Field("mobile_no") String mobileNumber,
                                             @Field("dob") String dob,
                                             @Field("gender") String gender);

        @Multipart
        @POST
        Call<EOProfileImage> uploadProfileImage(@Url String url,
                                                @Part("user_id") String userId,
                                                @Part MultipartBody.Part file);

        @FormUrlEncoded
        @POST
        Call<EOProductDetails> getProductDetails(@Url String url, @Field("product_id") String productId);


    }


}
