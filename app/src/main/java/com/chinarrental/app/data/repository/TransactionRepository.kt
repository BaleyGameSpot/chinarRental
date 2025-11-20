package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.TransactionDao
import com.chinarrental.app.data.model.Transaction
import com.chinarrental.app.data.model.TransactionCategory
import com.chinarrental.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionById(id: Long): Flow<Transaction?> = transactionDao.getTransactionById(id)

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByCategory(category: TransactionCategory): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(category)

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTotalIncome(): Flow<Double> = transactionDao.getTotalIncome()

    fun getTotalExpense(): Flow<Double> = transactionDao.getTotalExpense()

    fun getBalance(): Flow<Double> = transactionDao.getBalance()

    fun getTodayTransactions(): Flow<List<Transaction>> = transactionDao.getTodayTransactions()

    fun getTodayIncome(): Flow<Double> = transactionDao.getTodayIncome()

    fun getTodayExpense(): Flow<Double> = transactionDao.getTodayExpense()

    suspend fun insertTransaction(transaction: Transaction): Result<Long> {
        return try {
            val id = transactionDao.insertTransaction(transaction)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return try {
            transactionDao.updateTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
        return try {
            transactionDao.deleteTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
