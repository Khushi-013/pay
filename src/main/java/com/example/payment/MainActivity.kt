package com.example.payment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var editTextAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerAmount: Spinner = findViewById(R.id.spinnerAmount)
        editTextAmount = findViewById(R.id.editTextAmount)
        val pay: Button = findViewById(R.id.pay)

        // Populate spinner with payment amounts
        ArrayAdapter.createFromResource(
            this,
            R.array.payment_amounts,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAmount.adapter = adapter
        }

        // Listen for spinner item selection
        spinnerAmount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedAmount = parent?.getItemAtPosition(position).toString()
                editTextAmount.setText(selectedAmount)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Button click listener for starting payment
        pay.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        val co = Checkout()
        co.setKeyID("rzp_test_cMfINUFRMJozPJ") // Replace with your Razorpay API key

        try {
            val options = JSONObject()
            options.put("name", "SmartSociety")
            options.put("description", "SUBSCRIBE NOW")
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")

            // Retrieve amount from user input (assuming userInputAmount is the ID of your EditText)
            val userInputAmount = editTextAmount.text.toString().trim()

            // Check if the input is not empty
            if (userInputAmount.isNotEmpty()) {
                // Convert amount to currency subunits
                options.put("amount", userInputAmount.toInt() * 100)
            } else {
                // Handle empty input (e.g., display an error message)
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return
            }

            val prefill = JSONObject()
            prefill.put("email", "")
            prefill.put("contact", "")

            options.put("prefill", prefill)
            co.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
