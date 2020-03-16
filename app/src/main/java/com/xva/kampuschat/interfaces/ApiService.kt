package com.xva.kampuschat.interfaces


import com.xva.kampuschat.entities.*
import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Multipart



interface ApiService {

    @GET("user")
    fun getUser(): Call<Profile>


    @GET("universities")
    fun getUniversities(): Call<List<University>>

    @GET("departments/{university_id}")
    fun getDepartments(@Path("university_id") university_id: Int): Call<List<Department>>


    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("fullname") fullname: String, @Field("email") email: String, @Field("username") username: String, @Field(
            "password"
        ) password: String, @Field("gender") gender: String, @Field("date_of_birth") date_of_birth: String, @Field(
            "department_id"
        ) department_id: Int
    ): Call<AccessToken>


    @POST("login")
    @FormUrlEncoded
    fun login(@Field("username") username: String, @Field("password") password: String): Call<AccessToken>


    @GET("forgotPassword/{email}")
    fun sendCode(@Path("email") email: String): Call<String>


    @POST("updatePassword")
    @FormUrlEncoded
    fun updatePassword(@Field("email") email: String, @Field("code") code: String, @Field("password") password: String): Call<String>


    @POST("verifyEmail")
    @FormUrlEncoded
    fun verifyEmail(@Field("email") email: String, @Field("code") code: String): Call<String>


    @PUT("updateEvent")
    @FormUrlEncoded
    fun updateEvent(@Field("email") email: String): Call<Event>

    @PUT("setOnline")
    @FormUrlEncoded
    fun setOnline(@Field("user_id") user_id: Number): Call<String>

    @PUT("setOffline")
    @FormUrlEncoded
    fun setOffline(@Field("user_id") user_id: Number): Call<String>


    @GET("shuffle/{user_id}/{count}")
    fun shuffle(@Path("user_id") user_id: Number,@Path("count")count:Number): Call<Profile>

    @POST("addChat")
    @FormUrlEncoded
    fun addChat(@Field("owner_user_id") owner_user_id: Number, @Field("guest_user_id") guest_user_id: Number): Call<String>

    @GET("getChats/{user_id}")
    fun getChats(@Path("user_id") user_id: Number): Call<List<Chat>>

    @GET("getLikedUsers/{user_id}")
    fun getLikes(@Path("user_id") user_id: Int): Call<List<Profile>>


    @POST("likeUser")
    @FormUrlEncoded
    fun likeUser(@Field("user_id") user_id: Number, @Field("liked_user_id") liked_user_id: Number): Call<String>

    @GET("getBannedUsers/{user_id}")
    fun getBans(@Path("user_id") user_id: Int): Call<List<Profile>>

    @POST("banUser")
    @FormUrlEncoded
    fun banUser(@Field("user_id") user_id: Number, @Field("banned_user_id") liked_user_id: Number): Call<String>

    @POST("removeBan")
    @FormUrlEncoded
    fun removeBan(@Field("user_id") user_id: Number, @Field("banned_user_id") liked_user_id: Number): Call<String>


   // @POST("updatePhoto")
    //@FormUrlEncoded
    //fun updatePhoto(@Field("user_id") user_id: Number, @Field("image") image: String): Call<Photo>


    @PUT("updateProfile")
    @FormUrlEncoded
    fun updateProfile(@Field("user_id") user_id: Number, @Field("url") url: String?): Call<String>

    @GET("checkNewMessages/{user_id}/{last_date}")
    fun checkNewMessages(@Path("user_id")user_id:Int,@Path("last_date")last_date:String):Call<List<Message>>



    @POST("logout")
    fun logout(): Call<String>


}