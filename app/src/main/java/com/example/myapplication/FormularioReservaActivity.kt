package com.example.myapplication

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.myapplication.databinding.ActivityFormularioReservaBinding
import com.example.myapplication.databinding.ActivityMainBinding
import java.time.Month
import java.time.MonthDay
import java.util.Calendar

class FormularioReservaActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityFormularioReservaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormularioReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fecha.setOnClickListener(this)

        binding.convencional.setOnClickListener {
            val intent = Intent(applicationContext, PagoActivity::class.java)
            intent.putExtra("CantidadPersonas", binding.cant.text.toString())
            intent.putExtra("Fecha", binding.fecha.text.toString())
            intent.putExtra("Hora", binding.hora1.text.toString())
            intent.putExtra("Comentario", binding.comentario.text.toString())
            startActivity(intent)
        }
    }

    override fun onClick(p0: View?) {
        val fecha = DataPickerFragment {
            year, month, day -> mostrarResultado(year,month,day)
        }

        fecha.show(supportFragmentManager, "DatePicker")
    }

    private fun mostrarResultado(year: Int, month: Int, day: Int) {
        binding.fecha.setText("$year/$month/$day")
    }

    class DataPickerFragment (val listener: (year:Int, month:Int, day: Int) -> Unit):DialogFragment(),DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH) + 1
            var day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(), this,year, month,day)
        }
        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
            listener(year,month,day)
        }
    }

    // Este método se llama cuando se hace clic en el EditText
    fun showTimePickerDialog(view: android.view.View) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // Aquí obtienes la hora seleccionada y puedes actualizar el EditText
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.hora1.setText(formattedTime)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }
}