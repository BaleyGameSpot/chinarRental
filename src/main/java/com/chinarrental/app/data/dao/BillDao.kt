package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Bill
import com.chinarrental.app.data.model.BillStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY billDate DESC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE id = :id")
    fun getBillById(id: Long): Flow<Bill?>

    @Query("SELECT * FROM bills WHERE customerId = :customerId ORDER BY billDate DESC")
    fun getBillsByCustomer(customerId: Long): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE rentalId = :rentalId ORDER BY billDate DESC")
    fun getBillsByRental(rentalId: Long): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE status = :status ORDER BY billDate DESC")
    fun getBillsByStatus(status: BillStatus): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE billNumber LIKE '%' || :query || '%'")
    fun searchBills(query: String): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE billDate >= :startDate AND billDate <= :endDate ORDER BY billDate DESC")
    fun getBillsByDateRange(startDate: Long, endDate: Long): Flow<List<Bill>>

    @Query("SELECT SUM(amount) FROM bills")
    fun getTotalBillsAmount(): Flow<Double>

    @Query("SELECT SUM(amount - paidAmount) FROM bills WHERE status != 'PAID' AND status != 'CANCELLED'")
    fun getPendingBillsAmount(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    @Update
    suspend fun updateBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)
}