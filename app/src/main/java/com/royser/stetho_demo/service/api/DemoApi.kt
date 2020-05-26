package com.royser.stetho_demo.service.api

import com.royser.stetho_demo.service.response.DataResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Royser on 20/5/2020 AD.
 */
interface DemoApi {

    @GET(DemoURL.WEATHER)
    suspend fun getWeather(
        @Path("woeid") woeid: String,
        @Path("date") date: String
    ): DataResponse

}