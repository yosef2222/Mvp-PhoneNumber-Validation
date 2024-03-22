package com.example.mvpphonenumbervalidation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// 1. Define Contract Interface
interface PhoneNumberContract {
    interface View {
        fun showFormattedPhoneNumber(formattedPhoneNumber: String)
        fun showError(errorMessage: String)
    }

    interface Presenter {
        fun formatPhoneNumber(phoneNumber: String)
        fun onDestroy()
    }

    interface Model {
        fun isValidPhoneNumber(phoneNumber: String): Boolean
        fun formatPhoneNumber(phoneNumber: String): String
    }
}

// 2. Implement Model
class PhoneNumberModel : PhoneNumberContract.Model {
    override fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Check if the phone number starts with +7 or 8 and then followed by exactly 10 digits
        return phoneNumber.matches(Regex("^(\\+7|8)?\\d{10}$"))
    }

    override fun formatPhoneNumber(phoneNumber: String): String {
        // Check if the phone number is valid
        if (!isValidPhoneNumber(phoneNumber)) {
            throw IllegalArgumentException("Invalid phone number")
        }

        // Remove leading '+' if present
        val cleanPhoneNumber = phoneNumber.replace("+", "")

        // Get the last 10 digits of the phone number
        val last10Digits = cleanPhoneNumber.takeLast(10)

        // Format the phone number to +7 (xxx) xxx xx xx
        val formattedPhoneNumber = StringBuilder("+7 (")
        formattedPhoneNumber.append(last10Digits.substring(0, 3))
            .append(") ")
            .append(last10Digits.substring(3, 6))
            .append(" ")
            .append(last10Digits.substring(6, 8))
            .append(" ")
            .append(last10Digits.substring(8))

        return formattedPhoneNumber.toString()
    }

}

// 3. Implement Presenter
class PhoneNumberPresenter(private var view: PhoneNumberContract.View? = null, private val model: PhoneNumberContract.Model) :
    PhoneNumberContract.Presenter {
    override fun formatPhoneNumber(phoneNumber: String) {
        if (model.isValidPhoneNumber(phoneNumber)) {
            val formattedPhoneNumber = model.formatPhoneNumber(phoneNumber)
            view?.showFormattedPhoneNumber(formattedPhoneNumber)
        } else {
            view?.showError("Invalid phone number")
        }
    }

    override fun onDestroy() {
        view = null
    }
}

// 4. Implement View (Activity or Fragment)
class MainActivity : AppCompatActivity(), PhoneNumberContract.View {
    private lateinit var presenter: PhoneNumberContract.Presenter
    private lateinit var inputField: EditText
    private lateinit var button: Button
    private lateinit var resultField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputField = findViewById(R.id.phone_input)
        button = findViewById(R.id.convert_button)
        resultField = findViewById(R.id.result_text)

        presenter = PhoneNumberPresenter(this, PhoneNumberModel())

        button.setOnClickListener {
            val phoneNumber = inputField.text.toString()
            presenter.formatPhoneNumber(phoneNumber)
        }
    }

    override fun showFormattedPhoneNumber(formattedPhoneNumber: String) {
        resultField.text = formattedPhoneNumber
    }

    override fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
