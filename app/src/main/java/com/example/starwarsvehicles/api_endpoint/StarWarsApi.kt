package com.example.swapivehicles.api_endpoint

import com.example.swapivehicles.model.People
import com.example.swapivehicles.model.VehiclesResult
import io.reactivex.Single
import retrofit2.http.GET

interface StarWarsApi {

    @GET("api/vehicles")
    fun getVehicles(): Single<VehiclesResult>

    @GET("api/people")
    fun getPeoples(): Single<People>
}