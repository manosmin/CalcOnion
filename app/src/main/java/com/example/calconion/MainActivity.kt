package com.example.calconion

import android.R
import android.os.Bundle
import android.content.Context
import android.icu.text.SymbolTable
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
        /*fetchRates()
        { result ->
            myRates = result
        }*/
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
        binding.addButton.setOnClickListener { addSymbolToInput("+") }
        binding.minusButton.setOnClickListener { addSymbolToInput("-") }
        binding.multiplyButton.setOnClickListener { addSymbolToInput("*") }
        binding.divideButton.setOnClickListener { addSymbolToInput("/") }
        binding.convertButton.setOnClickListener { myConv(myRates) }
        binding.copyButton.setOnClickListener {}
        binding.fetchButton.setOnClickListener {
            fetchRates()
            { result -> myRates = result }
        }
        // Set listeners for num pad
        binding.num0.setOnClickListener{ addNumToInput(0) }
        binding.num1.setOnClickListener{ addNumToInput(1) }
        binding.num2.setOnClickListener{ addNumToInput(2) }
        binding.num3.setOnClickListener{ addNumToInput(3) }
        binding.num4.setOnClickListener{ addNumToInput(4) }
        binding.num5.setOnClickListener{ addNumToInput(5) }
        binding.num6.setOnClickListener{ addNumToInput(6) }
        binding.num7.setOnClickListener{ addNumToInput(7) }
        binding.num8.setOnClickListener{ addNumToInput(8) }
        binding.num9.setOnClickListener{ addNumToInput(9) }
        binding.symbolequals.setOnClickListener{ calculateExpression(binding.testBox.text.toString()) }
        binding.symbolc.setOnClickListener{ myErase() }
        binding.symboldot.setOnClickListener{ addSymbolToInput(".") }

    }


    // This function converts to unix time to date time format
    private fun unixTimestampToDateTime(unixTimestamp: Int): String {
        val timestampLong = unixTimestamp.toLong() * 1000 // Convert to milliseconds
        val date = Date(timestampLong)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return sdf.format(date)
    }


    // This function gets user amount input for conversion
    private fun myConv(rates: JSONObject) {
        val sourceCurrency = binding.sourceCurrencySpinner.selectedItem.toString()
        val targetCurrency = binding.targetCurrencySpinner.selectedItem.toString()
        val amountText = binding.testBox.text.toString()
        val symbolList = listOf('+', '-', '*', '/', '.')
        if (amountText.isNotEmpty() && doesNotContainSymbols(amountText, symbolList)) {
            val amount = amountText.toDouble()
            convertCurrency(
                sourceCurrency = sourceCurrency,
                targetCurrency = targetCurrency,
                amount = amount,
                rates = rates
            )
        } else {
            binding.testBox2.error = "Error"
        }
    }

    // This function converts source currency to target currency
    private fun convertCurrency(sourceCurrency: String, targetCurrency: String, amount: Double, rates: JSONObject) {
        GlobalScope.launch(Dispatchers.IO) {
            // Take
            try {
                val exchangeRate = rates.getDouble(targetCurrency)
                val sourceRate = rates.getDouble(sourceCurrency)
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.DOWN
                if (sourceCurrency == "EUR") {
                    // Convert chosen amount and round to 2-decimal points
                    val convertedAmount = df.format(amount * exchangeRate)
                    withContext(Dispatchers.Main) {
                        binding.testBox2.text = "$convertedAmount"
                    }
                } // Workaround: If source currency is other than EUR convert from EUR to sourceCurrency and then convert sourceCurrency to targetCurrency
                else {
                    val convertedAmount = df.format(amount * exchangeRate / sourceRate)
                    withContext(Dispatchers.Main) {
                        binding.testBox2.text = "$convertedAmount"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.textView2.text = "Error: ${e.message}"
                }
            }
        }
    }

    // This function erases last character from input
    private fun myErase() {
        if (binding.testBox.text.isNotEmpty())
            binding.testBox.text = removeLastCharacter(binding.testBox.text.toString())
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

    private fun addNumToInput(number: Int) {
        val number = number.toString()
        binding.testBox.text = "${binding.testBox.text}$number"
    }

    private fun addSymbolToInput(mySymbol: String) {
        // Define  a list of not allowed characters
        val charList = listOf('+', '.', '*', '/', '-')
        // Check if the last character of the String is included in this list else add the symbol
        val lastChar = binding.testBox.text.toString().lastOrNull()
        if (binding.testBox.text != "" && !charList.any { it == lastChar }) {
            binding.testBox.text = "${binding.testBox.text}$mySymbol"
        }
    }

    fun doesNotContainSymbols(input: String, symbols: List<Char>): Boolean {
        return input.none { char -> symbols.contains(char) }
    }

    private fun calculateExpression(myExpression: String) {
        val result = extractNumbersAndOperator(myExpression)
        if (result != null) {
            var (firstNumber, secondNumber, operator) = result
            println(firstNumber)
            println(secondNumber)
            println(operator)
            when (operator) {
                '+' -> { binding.testBox2.text = (firstNumber + secondNumber).toString() }
                '-' -> { binding.testBox2.text = (firstNumber - secondNumber).toString() }
                '*' -> { binding.testBox2.text = (firstNumber * secondNumber).toString() }
                '/' -> {
                    if (secondNumber != 0.0) {
                    binding.testBox2.text = (firstNumber / secondNumber).toString() }
                else {
                    binding.testBox2.text = "Divider can't be zero."
                }
                }
            }
        } else {
            binding.testBox2.text = "Error"
        }
        binding.testBox.text = ""
        }

    private fun extractNumbersAndOperator(input: String): Triple<Double, Double, Char>? {
        val regex = """(\d+)\s*([\/\+\-\*])\s*(\d+)""".toRegex()
        val matchResult = regex.find(input)

        return matchResult?.let {
            val (firstNumber, operator, secondNumber) = it.destructured
            Triple(firstNumber.toDouble(), secondNumber.toDouble(), operator[0])
        }
    }
    private fun removeLastCharacter(inputString: String): String {
        if (inputString.isEmpty()) {
            throw IllegalArgumentException("Input string is empty")

        } else {
            return inputString.substring(0, inputString.length - 1)
        }
    }
}



