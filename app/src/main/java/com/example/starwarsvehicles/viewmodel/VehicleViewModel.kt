package com.example.swapivehicles.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swapivehicles.di.DaggerApiComponent
import com.example.swapivehicles.model.Vehicle
import com.example.swapivehicles.service.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VehicleViewModel : ViewModel() {

    @Inject
    lateinit var networkService: NetworkService

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private val vehicleList by lazy { MutableLiveData<List<Vehicle>>() }
    val vehicleListLD: LiveData<List<Vehicle>>
        get() = vehicleList
    private val inProgress by lazy { MutableLiveData<Boolean>() }
    val inProgressLD: LiveData<Boolean>
        get() = inProgress
    private val isError by lazy { MutableLiveData<Boolean>() }
    val isErrorLD: LiveData<Boolean>
        get() = isError


    init {
        DaggerApiComponent.create().inject(this)
        fetchVehicles()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun refresh() {
        fetchVehicles()
    }

    private fun fetchVehicles() {
        compositeDisposable.add( //API call get stored in compositeDisposable
            networkService.fetchVehicle() //Makes the call to the endpoint
                .subscribeOn(Schedulers.io()) //Subscribes on a background thread, which is Schedulers.io()
                .observeOn(AndroidSchedulers.mainThread()) //Displays the result on the main thread (UI thread)
                .map { it.results } //Takes the list of vehicles in VehiclesResult pass it on to the next operator
                .subscribeWith(createVehicleObserver()) //The glue that connects networkService.fetchVehicle() with createVehicleObserver()
        )
    }

    private fun createVehicleObserver(): DisposableSingleObserver<List<Vehicle>> {
        return object : DisposableSingleObserver<List<Vehicle>>() {

            override fun onSuccess(vehicles: List<Vehicle>) {
                inProgress.value = true
                isError.value = false
                vehicleList.value = vehicles
                inProgress.value = false
            }

            override fun onError(e: Throwable) {
                inProgress.value = true
                isError.value = true
                Log.e("onError()", "Error: ${e.message}")
                inProgress.value = false
            }
        }
    }
}
