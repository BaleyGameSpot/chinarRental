package com.chinarrental.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager as AndroidSmsManager

object SmsManager {

    fun sendSms(phoneNumber: String, message: String): Result<Unit> {
        return try {
            val smsManager = AndroidSmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun sendWhatsAppMessage(context: Context, phoneNumber: String, message: String) {
        try {
            val formattedNumber = phoneNumber.replace(Regex("[^0-9]"), "")
            val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getReturnReminderMessage(customerName: String, itemName: String, returnDate: String): String {
        return "Dear $customerName, this is a reminder that your rental item '$itemName' is due for return on $returnDate. Thank you!"
    }

    fun getPaymentReminderMessage(customerName: String, amount: Double): String {
        return "Dear $customerName, you have a pending payment of Rs. ${String.format("%.2f", amount)}. Please make the payment at your earliest convenience. Thank you!"
    }

    fun getOverdueMessage(customerName: String, itemName: String, overdueDays: Int): String {
        return "Dear $customerName, your rental item '$itemName' is overdue by $overdueDays days. Please return it immediately to avoid additional charges. Thank you!"
    }

    fun getBillConfirmationMessage(customerName: String, billNumber: String, amount: Double): String {
        return "Dear $customerName, your bill #$billNumber has been generated for Rs. ${String.format("%.2f", amount)}. Thank you for your business!"
    }
}
