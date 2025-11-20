package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.BillDao
import com.chinarrental.app.data.model.Bill
import com.chinarrental.app.data.model.BillStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillRepository @Inject constructor(
    private val billDao: BillDao
) {

    fun getAllBills(): Flow<List<Bill>> = billDao.getAllBills()

    fun getBillById(id: Long): Flow<Bill?> = billDao.getBillById(id)

    fun getBillsByCustomer(customerId: Long): Flow<List<Bill>> =
        billDao.getBillsByCustomer(customerId)

    fun getBillsByStatus(status: BillStatus): Flow<List<Bill>> =
        billDao.getBillsByStatus(status)

    fun getBillsByDateRange(startDate: Long, endDate: Long): Flow<List<Bill>> =
        billDao.getBillsByDateRange(startDate, endDate)

    fun searchBills(query: String): Flow<List<Bill>> = billDao.searchBills(query)

    fun getTotalBillsAmount(): Flow<Double> = billDao.getTotalBillsAmount()

    fun getPendingBillsAmount(): Flow<Double> = billDao.getPendingBillsAmount()

    suspend fun insertBill(bill: Bill): Result<Long> {
        return try {
            val id = billDao.insertBill(bill)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBill(bill: Bill): Result<Unit> {
        return try {
            billDao.updateBill(bill)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBill(bill: Bill): Result<Unit> {
        return try {
            billDao.deleteBill(bill)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
