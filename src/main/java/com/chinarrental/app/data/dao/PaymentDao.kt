package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Payment
import com.chinarrental.app.data.model.PaymentMethod
import com.chinarrental.app.data.model.PaymentType
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE id = :id")
    fun getPaymentById(id: Long): Flow<Payment?>

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY paymentDate DESC")
    fun getPaymentsByCustomer(customerId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE rentalId = :rentalId ORDER BY paymentDate DESC")
    fun getPaymentsByRental(rentalId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE paymentType = :type ORDER BY paymentDate DESC")
    fun getPaymentsByType(type: PaymentType): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE paymentMethod = :method ORDER BY paymentDate DESC")
    fun getPaymentsByMethod(method: PaymentMethod): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE paymentDate >= :startDate AND paymentDate <= :endDate ORDER BY paymentDate DESC")
    fun getPaymentsByDateRange(startDate: Long, endDate: Long): Flow<List<Payment>>

    @Query("SELECT SUM(amount) FROM payments WHERE customerId = :customerId AND paymentType = :type")
    fun getTotalPaymentByCustomerAndType(customerId: Long, type: PaymentType): Flow<Double?>

    @Query("SELECT SUM(amount) FROM payments WHERE paymentDate >= :startDate AND paymentDate <= :endDate AND paymentType = :type")
    suspend fun getTotalPaymentByDateAndType(startDate: Long, endDate: Long, type: PaymentType): Double?

    @Query("SELECT SUM(amount) FROM payments")
    fun getTotalPaymentsAmount(): Flow<Double>

    @Query("SELECT * FROM payments WHERE paymentDate >= :todayStart ORDER BY paymentDate DESC")
    fun getTodayPayments(todayStart: Long = System.currentTimeMillis() / 86400000 * 86400000): Flow<List<Payment>>

    @Query("SELECT SUM(amount) FROM payments WHERE paymentDate >= :todayStart")
    fun getTodayTotalAmount(todayStart: Long = System.currentTimeMillis() / 86400000 * 86400000): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long

    @Update
    suspend fun updatePayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)
}