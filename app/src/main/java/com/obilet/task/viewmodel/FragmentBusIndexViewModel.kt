package com.obilet.task.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.obilet.task.di.Service
import com.obilet.task.model.Location
import com.obilet.task.model.LocationResponse
import com.obilet.task.model.User
import com.obilet.task.utilities.ButtonType
import com.obilet.task.utilities.Constants
import com.obilet.task.utilities.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FragmentBusIndexViewModel @Inject constructor(
    application: Application,
    private val travelAPIService: Service,
    private val sharedPreferencesManager: SharedPreferencesManager
) : BaseViewModel(application) {

    var busLocations = MutableLiveData<List<Location>>()
    var date = MutableLiveData<String>()
    var btnType = MutableLiveData<ButtonType>()
    var cityId = MutableLiveData<String>()
    var originName: String = "İstanbul Avrupa"
    var destinationName: String = "İstanbul Anadolu"
    var sessionId: String = ""
    var originID:Int=0
    var destinationID:Int=0
    var deviceID:String=""
    var dateTravel:String=""
    val progressLoading = MutableLiveData<Boolean>()
    private val disposable = CompositeDisposable()

    fun refreshFromAPI() {
        getSessionFromAPI()
    }

    fun saveLocalDate(date: String) {
        sharedPreferencesManager.putString("date", date)
    }

    fun swapTravelCity() {
        val orgId = originID
        originID = destinationID
        destinationID = orgId
    }

    private fun getSessionFromAPI() {
        disposable.add(
            travelAPIService.getSession()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<User>() {
                    override fun onSuccess(user: User) {
                        sessionId = user.data.sessionId
                       deviceID = user.data.deviceId
                        getBusLocationFromAPI()
                    }

                    override fun onError(e: Throwable) {
                        progressLoading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }
    fun compareDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy EEEE", Locale("tr", "TR"))
        val today = Calendar.getInstance().time
        val targetDate = dateFormat.parse(date)
        if (targetDate.before(today)) {
            return true
        } else {
            return false
        }
    }

    fun findCity(cityId: String): Location {

        val location = busLocations.value?.find { it.id == cityId.toInt() } as Location
        return location
    }
    private fun getBusLocationFromAPI() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val currentDateTimeString = "$year-$month-$dayOfMonth" + "T" + "$hour:$minute:$second"
        disposable.add(
            travelAPIService.getBusLocations(currentDateTimeString, sessionId, deviceID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<LocationResponse>() {
                    override fun onSuccess(busLocation: LocationResponse) {
                        busLocations.value = busLocation.data
                        //Constants.listLocation = busLocation.data
                        progressLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        progressLoading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }
}
