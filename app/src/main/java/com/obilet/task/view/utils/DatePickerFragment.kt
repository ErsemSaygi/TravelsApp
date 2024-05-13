package com.obilet.task.view.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.obilet.task.utilities.SharedPreferencesManager
import com.obilet.task.viewmodel.FragmentBusIndexViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val sharedViewModel: FragmentBusIndexViewModel by activityViewModels()





    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        return DatePickerDialog(requireActivity(), this, year, month, day)
    }
    //For date selection
    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int){
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)


        val outputFormat = SimpleDateFormat("dd MMMM yyyy EEEE", Locale("tr"))

        val dateS=outputFormat.format(dateFormat.parse(formattedDate)!!)


        sharedViewModel.date.value=dateS
        sharedPreferencesManager.putString("dateTravel", formattedDate)
        sharedPreferencesManager.putString("date", dateS)
    }


}