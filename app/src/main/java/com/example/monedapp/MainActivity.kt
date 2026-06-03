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
import androidx.lifecycle.lifecycleScope
import com.example.monedapp.databinding.ActivityMainBinding
import com.example.monedapp.model.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var calculationJob: Job? = null
    private val currencies = listOf(
        Currency("USD", "Dólar Estadounidense", 1.0),
        Currency("MXN", "Peso Mexicano", 17.35),
        Currency("EUR", "Euro", 0.86),
        Currency("GBP", "Libra Esterlina", 0.75),
        Currency("JPY", "Yen Japonés", 160.05),
        Currency("CAD", "Dólar Canadiense", 1.39),
        Currency("BRL", "Real Brasileño", 5.08)
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

        // Valores iniciales
        binding.spinnerFrom.setSelection(0) // USD
        binding.spinnerTo.setSelection(1)   // MXN
    }

    private fun setupListeners() {
        // Cálculo concurrente al escribir
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convert()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateToCodeLabel()
                convert()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerFrom.onItemSelectedListener = spinnerListener
        binding.spinnerTo.onItemSelectedListener = spinnerListener

        // Botón para intercambiar monedas
        binding.btnSwap.setOnClickListener {
            val fromPos = binding.spinnerFrom.selectedItemPosition
            val toPos = binding.spinnerTo.selectedItemPosition
            binding.spinnerFrom.setSelection(toPos)
            binding.spinnerTo.setSelection(fromPos)
        }
    }

    private fun updateToCodeLabel() {
        val toCurrency = binding.spinnerTo.selectedItem as Currency
        binding.tvToCode.text = toCurrency.code
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

        // Cálculo concurrente usando Corrutinas
        calculationJob?.cancel()
        calculationJob = lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) {
                // Cálculo en hilo secundario
                (amount / fromCurrency.factor) * toCurrency.factor
            }
            binding.tvResult.text = String.format(Locale.getDefault(), "%,.2f", result)
        }
    }
}
