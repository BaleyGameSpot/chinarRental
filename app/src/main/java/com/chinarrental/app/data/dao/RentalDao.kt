package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Rental
import com.chinarrental.app.data.model.RentalStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {
    @Query("SELECT * FROM rentals ORDER BY startDate DESC")
    fun getAllRentals(): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE id = :id")
    fun getRentalById(id: Long): Flow<Rental?>

    @Query("SELECT * FROM rentals WHERE customerId = :customerId ORDER BY startDate DESC")
    fun getRentalsByCustomer(customerId: Long): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE itemId = :itemId ORDER BY startDate DESC")
    fun getRentalsByItem(itemId: Long): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE status = :status ORDER BY startDate DESC")
    fun getRentalsByStatus(status: RentalStatus): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE status = :status AND expectedReturnDate < :currentTime")
    fun getOverdueRentals(status: RentalStatus = RentalStatus.ACTIVE, currentTime: Long = System.currentTimeMillis()): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE startDate >= :startDate AND startDate <= :endDate ORDER BY startDate DESC")
    fun getRentalsByDateRange(startDate: Long, endDate: Long): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE remainingAmount > 0")
    fun getRentalsWithPendingPayment(): Flow<List<Rental>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: Rental): Long

    @Update
    suspend fun updateRental(rental: Rental)

    @Delete
    suspend fun deleteRental(rental: Rental)

    @Query("SELECT SUM(finalAmount) FROM rentals WHERE status = :status AND startDate >= :startDate AND startDate <= :endDate")
    suspend fun getTotalRentalAmount(status: RentalStatus, startDate: Long, endDate: Long): Double?
}
