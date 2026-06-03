package com.example.monedapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monedapp.databinding.ActivityMainBinding
import com.example.monedapp.model.Currency
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val currencies = listOf(
        Currency("USD", "Dólar Estadounidense", 1.0),
        Currency("MXN", "Peso Mexicano", 20.0),
        Currency("EUR", "Euro", 0.92),
        Currency("GBP", "Libra Esterlina", 0.78),
        Currency("JPY", "Yen Japonés", 150.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerFrom.adapter = adapter
        binding.spinnerTo.adapter = adapter

        // Valores por defecto
        binding.spinnerFrom.setSelection(0) // USD
        binding.spinnerTo.setSelection(1)   // MXN
    }

    private fun setupListeners() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convert()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convert()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerFrom.onItemSelectedListener = spinnerListener
        binding.spinnerTo.onItemSelectedListener = spinnerListener
    }

    private fun convert() {
        val amountStr = binding.etAmount.text.toString()
        if (amountStr.isEmpty()) {
            binding.tvResult.text = "0.00"
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val fromCurrency = binding.spinnerFrom.selectedItem as Currency
        val toCurrency = binding.spinnerTo.selectedItem as Currency

        // Conversión: (Monto / Factor Origen) * Factor Destino
        val result = (amount / fromCurrency.factor) * toCurrency.factor
        binding.tvResult.text = String.format(Locale.getDefault(), "%.2f", result)
    }
}
