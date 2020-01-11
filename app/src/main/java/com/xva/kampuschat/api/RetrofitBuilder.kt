package com.xva.kampuschat.api

import com.xva.kampuschat.interfaces.ApiService
import com.xva.kampuschat.utils.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class RetrofitBuilder {


    companion object {

        private val BASE_URL = "https://kampuschat.herokuapp.com/api/"
        var client = buildClient()
        var retrofit = buildRetrofit()


        fun buildClient(): OkHttpClient {

            var builder = OkHttpClient.Builder()
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        var request = chain.request()

                        var mBuilder = request.newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Connection", "close")


                        request = mBuilder.build()

                        return chain.proceed(request)

                    }

                })


            return builder.build()

        }


        fun buildRetrofit(): Retrofit {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(OkHttpClient())
                .build()

        }


        fun createService(service: Class<ApiService>): ApiService {

            return retrofit.create(service)

        }


        fun createServiceWithAuth(
            service: Class<ApiService>,
            sharedPreferencesHelper: SharedPreferencesHelper
        ) : ApiService {


            var newClient = client.newBuilder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {

                    var request = chain.request()

                    var builder = request.newBuilder()

                    if (sharedPreferencesHelper.getAccessToken().access_token != null) {
                        builder.addHeader(
                            "Authorization",
                            "Bearer " + sharedPreferencesHelper.getAccessToken().access_token
                        )

                    }

                    request = builder.build()

                    return chain.proceed(request)



                }

            }).build()


            var newRetrofit = retrofit.newBuilder().client(newClient).build()
            return newRetrofit.create(service)

        }


    }


}