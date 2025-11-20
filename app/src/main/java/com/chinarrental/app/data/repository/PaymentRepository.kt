package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.PaymentDao
import com.chinarrental.app.data.model.Payment
import com.chinarrental.app.data.model.PaymentMethod
import com.chinarrental.app.data.model.PaymentType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentDao: PaymentDao
) {

    fun getAllPayments(): Flow<List<Payment>> = paymentDao.getAllPayments()

    fun getPaymentById(id: Long): Flow<Payment?> = paymentDao.getPaymentById(id)

    fun getPaymentsByCustomer(customerId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsByCustomer(customerId)

    fun getPaymentsByRental(rentalId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsByRental(rentalId)

    fun getPaymentsByType(type: PaymentType): Flow<List<Payment>> =
        paymentDao.getPaymentsByType(type)

    fun getPaymentsByMethod(method: PaymentMethod): Flow<List<Payment>> =
        paymentDao.getPaymentsByMethod(method)

    fun getPaymentsByDateRange(startDate: Long, endDate: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsByDateRange(startDate, endDate)

    fun getTotalPaymentsAmount(): Flow<Double> = paymentDao.getTotalPaymentsAmount()

    fun getTodayPayments(): Flow<List<Payment>> = paymentDao.getTodayPayments()

    fun getTodayTotalAmount(): Flow<Double> = paymentDao.getTodayTotalAmount()

    suspend fun insertPayment(payment: Payment): Result<Long> {
        return try {
            val id = paymentDao.insertPayment(payment)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePayment(payment: Payment): Result<Unit> {
        return try {
            paymentDao.updatePayment(payment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePayment(payment: Payment): Result<Unit> {
        return try {
            paymentDao.deletePayment(payment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
