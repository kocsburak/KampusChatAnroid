package com.xva.kampuschat.api

import com.xva.kampuschat.entities.api.ApiError
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

class ApiErrorHelper {


    companion object {


        fun convertErrors(response: ResponseBody): ApiError? {

            var annotation = arrayOf<Annotation>()
            var converter: Converter<ResponseBody, ApiError> =
                RetrofitBuilder.retrofit.responseBodyConverter(ApiError::class.java, annotation)


            var apiError: ApiError? = null


            try {
                apiError = converter.convert(response)

            } catch (e: IOException) {
                e.printStackTrace()
            }


            return apiError
        }


    }


}