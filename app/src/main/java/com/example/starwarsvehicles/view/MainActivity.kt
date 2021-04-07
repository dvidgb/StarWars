package com.example.swapivehicles.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swapivehicles.R
import com.example.swapivehicles.adapter.VehicleAdapter
import com.example.swapivehicles.di.DaggerApiComponent
import com.example.swapivehicles.viewmodel.VehicleViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var vehicleAdapter: VehicleAdapter

    private val vehicleViewModel: VehicleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerApiComponent.create().inject(this)

        main_swipe_refresh_layout.setOnRefreshListener {
            main_swipe_refresh_layout.isRefreshing = false
            vehicleViewModel.refresh()
        }

        main_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vehicleAdapter
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        observeInProgress()
        observeIsError()
        observeVehicleList()
    }

    private fun observeVehicleList() {
        vehicleViewModel.vehicleListLD.observe(this, Observer { allVehicles ->
            allVehicles.let {
                main_recycler_view.visibility = View.VISIBLE
                vehicleAdapter.setUpVehicles(it)
            }
        })
    }

    private fun observeInProgress() {
        vehicleViewModel.inProgressLD.observe(this, Observer { isLoading ->
            isLoading.let {
                if (it) {
                    vehicle_fetch_error.visibility = View.GONE
                    main_recycler_view.visibility = View.GONE
                    vehicle_fetch_progress.visibility = View.VISIBLE
                } else {
                    vehicle_fetch_progress.visibility = View.GONE
                }
            }
        })
    }

    private fun observeIsError() {
        vehicleViewModel.isErrorLD.observe(this, Observer { isError ->
            isError.let { vehicle_fetch_error.visibility = if (it) View.VISIBLE else View.GONE }
        })
    }
}
