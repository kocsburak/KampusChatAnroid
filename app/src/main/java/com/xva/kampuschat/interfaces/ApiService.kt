package com.xva.kampuschat.interfaces


import com.xva.kampuschat.entities.AccessToken
import com.xva.kampuschat.entities.Department
import com.xva.kampuschat.entities.University
import com.xva.kampuschat.entities.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("user")
    fun getUser(): Call<User>


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
    fun forgotPassword()


    @POST("updatePassword")
    @FormUrlEncoded
    fun updatePassword(@Field("email") email: String, @Field("code") code: String, @Field("password") password: String)


    @POST("verifyEmail")
    @FormUrlEncoded
    fun verifyEmail(@Field("email") email: String, @Field("code") code: String)


}