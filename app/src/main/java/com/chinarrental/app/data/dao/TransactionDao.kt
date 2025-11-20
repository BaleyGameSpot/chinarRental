package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Transaction
import com.chinarrental.app.data.model.TransactionCategory
import com.chinarrental.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Long): Flow<Transaction?>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: TransactionCategory): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type IN (:types) AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalIncomeByDateRange(types: List<TransactionType>, startDate: Long, endDate: Long): Double?

    @Query("SELECT DISTINCT category FROM transactions WHERE type = :type ORDER BY category ASC")
    fun getCategoriesByType(type: TransactionType): Flow<List<TransactionCategory>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double>

    @Query("SELECT (SELECT SUM(amount) FROM transactions WHERE type = 'INCOME') - (SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE')")
    fun getBalance(): Flow<Double>

    @Query("SELECT * FROM transactions WHERE date >= :todayStart ORDER BY date DESC")
    fun getTodayTransactions(todayStart: Long = System.currentTimeMillis() / 86400000 * 86400000): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date >= :todayStart")
    fun getTodayIncome(todayStart: Long = System.currentTimeMillis() / 86400000 * 86400000): Flow<Double>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :todayStart")
    fun getTodayExpense(todayStart: Long = System.currentTimeMillis() / 86400000 * 86400000): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
