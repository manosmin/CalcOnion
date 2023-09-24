package com.example.calconion

import android.R
import android.os.Bundle
import android.content.Context
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import com.example.calconion.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlinx.coroutines.withContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val context: Context = this
        // Define a JSON Object to save latest rates
        var myRates = JSONObject()
        // Get the latest rates automatically when app is opened
        fetchRates()
        {
                result -> myRates = result
        }
        // Define a list of currencies
        val currencies = listOf(
            "AED",
            "AFN",
            "ALL",
            "AMD",
            "ANG",
            "AOA",
            "ARS",
            "AUD",
            "AWG",
            "AZN",
            "BAM",
            "BBD",
            "BDT",
            "BGN",
            "BHD",
            "BIF",
            "BMD",
            "BND",
            "BOB",
            "BRL",
            "BSD",
            "BTC",
            "BTN",
            "BWP",
            "BYN",
            "BYR",
            "BZD",
            "CAD",
            "CDF",
            "CHF",
            "CLF",
            "CLP",
            "CNY",
            "COP",
            "CRC",
            "CUC",
            "CUP",
            "CVE",
            "CZK",
            "DJF",
            "DKK",
            "DOP",
            "DZD",
            "EGP",
            "ERN",
            "ETB",
            "EUR",
            "FJD",
            "FKP",
            "GBP",
            "GEL",
            "GGP",
            "GHS",
            "GIP",
            "GMD",
            "GNF",
            "GTQ",
            "GYD",
            "HKD",
            "HNL",
            "HRK",
            "HTG",
            "HUF",
            "IDR",
            "ILS",
            "IMP",
            "INR",
            "IQD",
            "IRR",
            "ISK",
            "JEP",
            "JMD",
            "JOD",
            "JPY",
            "KES",
            "KGS",
            "KHR",
            "KMF",
            "KPW",
            "KRW",
            "KWD",
            "KYD",
            "KZT",
            "LAK",
            "LBP",
            "LKR",
            "LRD",
            "LSL",
            "LTL",
            "LVL",
            "LYD",
            "MAD",
            "MDL",
            "MGA",
            "MKD",
            "MMK",
            "MNT",
            "MOP",
            "MRO",
            "MUR",
            "MVR",
            "MWK",
            "MXN",
            "MYR",
            "MZN",
            "NAD",
            "NGN",
            "NIO",
            "NOK",
            "NPR",
            "NZD",
            "OMR",
            "PAB",
            "PEN",
            "PGK",
            "PHP",
            "PKR",
            "PLN",
            "PYG",
            "QAR",
            "RON",
            "RSD",
            "RUB",
            "RWF",
            "SAR",
            "SBD",
            "SCR",
            "SDG",
            "SEK",
            "SGD",
            "SHP",
            "SLE",
            "SLL",
            "SOS",
            "SSP",
            "SRD",
            "STD",
            "SYP",
            "SZL",
            "THB",
            "TJS",
            "TMT",
            "TND",
            "TOP",
            "TRY",
            "TTD",
            "TWD",
            "TZS",
            "UAH",
            "UGX",
            "USD",
            "UYU",
            "UZS",
            "VEF",
            "VES",
            "VND",
            "VUV",
            "WST",
            "XAF",
            "XAG",
            "XAU",
            "XCD",
            "XDR",
            "XOF",
            "XPF",
            "YER",
            "ZAR",
            "ZMK",
            "ZMW",
            "ZWL"
        )

        // Create ArrayAdapter for Spinners
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapters for source and target Spinners
        binding.sourceCurrencySpinner.adapter = adapter
        binding.targetCurrencySpinner.adapter = adapter

        // Set default selection for Spinners
        binding.sourceCurrencySpinner.setSelection(46)
        binding.targetCurrencySpinner.setSelection(150)

        // Set listeners for the buttons
        binding.addButton.setOnClickListener { basicCalculations(1) }
        binding.minusButton.setOnClickListener { basicCalculations(2) }
        binding.multiplyButton.setOnClickListener { basicCalculations(3) }
        binding.divideButton.setOnClickListener { basicCalculations(4) }
        binding.convertButton.setOnClickListener { myConv(myRates) }
        binding.acButton.setOnClickListener { myErase() }
        binding.copyButton.setOnClickListener {
            if (binding.resultBox.text.toString().trim().isNotEmpty()) {
                val textToCopy = binding.resultBox.text.toString()
                binding.input1.requestFocus()
                binding.input1.setText(textToCopy)
                Toast.makeText(context, "Result Copied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No Result to Copy", Toast.LENGTH_SHORT).show()
            }
        }
        binding.fetchButton.setOnClickListener { fetchRates()
        { result -> myRates = result } }
    }

    // This function performs all the basic calculations
    private fun basicCalculations(flag: Int) {
        if (inputIsNotEmpty()) {
            val inputdata1 = binding.input1.text.toString().trim().toBigDecimal()
            val inputdata2 = binding.input2.text.toString().trim().toBigDecimal()
            when (flag) {
                1 -> binding.resultBox.text = inputdata1.add(inputdata2).toString()
                2 -> binding.resultBox.text = inputdata1.subtract(inputdata2).toString()
                3 -> binding.resultBox.text = inputdata1.multiply(inputdata2).toString()
                4 -> if (inputdata2.compareTo(BigDecimal.ZERO) != 0) {
                    binding.resultBox.text =
                        inputdata1.divide(inputdata2, 2, RoundingMode.HALF_UP).toString()
                } else {
                    binding.input2.error = "Divider can't be zero"
                }
            }
        }
    }

    // This function converts to unix time to date time format
    private fun unixTimestampToDateTime(unixTimestamp: Int): String {
        val timestampLong = unixTimestamp.toLong() * 1000 // Convert to milliseconds
        val date = Date(timestampLong)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return sdf.format(date)
    }

    // This function checks if text box is empty
    private fun inputIsNotEmpty(): Boolean {
        var b = true
        if (binding.input1.text.toString().trim().isEmpty()) {
            binding.input1.error = "Required"
            binding.input1.requestFocus()
            b = false
        }
        if (binding.input2.text.toString().trim().isEmpty()) {
            binding.input2.error = "Required"
            binding.input2.requestFocus()
            b = false
        }
        return b
    }

    // This function gets user amount input for conversion
    private fun myConv(rates: JSONObject) {
        val sourceCurrency = binding.sourceCurrencySpinner.selectedItem.toString()
        val targetCurrency = binding.targetCurrencySpinner.selectedItem.toString()
        val amountText = binding.input3.text.toString()

        if (amountText.isNotEmpty()) {
            val amount = amountText.toDouble()
            convertCurrency(
                sourceCurrency = sourceCurrency,
                targetCurrency = targetCurrency,
                amount = amount,
                rates = rates
            )
        } else {
            binding.input3.error = "Required"
            binding.input3.requestFocus()
        }
    }

    // This function converts source currency to target currency
    private fun convertCurrency(sourceCurrency: String, targetCurrency: String, amount: Double, rates: JSONObject) {
        GlobalScope.launch(Dispatchers.IO) {
            // Take
            try {
                val exchangeRate = rates.getDouble(targetCurrency)
                // Convert chosen amount and round to 2-decimal points
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.DOWN
                val convertedAmount = df.format(amount * exchangeRate)
                withContext(Dispatchers.Main) {
                    binding.resultBox.text = "$convertedAmount"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.textView2.text = "Error: ${e.message}"
                }
            }
        }
    }

    // This function erases all input and text boxes
    private fun myErase() {
        binding.resultBox.text = null
        binding.input1.text = null
        binding.input2.text = null
        binding.input3.text = null
    }

    // This function fetches latest rates from API
    private fun fetchRates(callback: (JSONObject) -> Unit) {
        // API Key
        val apiKey = "f9221d715fab599fc7ab6b7f7bd46816"
        // API Query
        val apiUrl = "http://data.fixer.io/api/latest?access_key=$apiKey"
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // API Query result
                val result = URL(apiUrl).readText()
                val jsonObject = JSONObject(result)
                // Get rates from JSON object
                val fetchedRates = jsonObject.getJSONObject("rates")
                println(fetchedRates)
                // Convert UNIX time to Date time
                val formattedDate = unixTimestampToDateTime(jsonObject.getInt("timestamp"))
                withContext(Dispatchers.Main) {
                    binding.textView2.text = "Last Updated: $formattedDate"
                    // Return fetchedRates to the callback on success
                    callback(fetchedRates)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.textView2.text = "Error: ${e.message}"
                }
            }
        }
    }
}



