package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Guarantor
import kotlinx.coroutines.flow.Flow

@Dao
interface GuarantorDao {
    @Query("SELECT * FROM guarantors WHERE customerId = :customerId")
    fun getGuarantorsByCustomer(customerId: Long): Flow<List<Guarantor>>

    @Query("SELECT * FROM guarantors WHERE customerId = :customerId")
    fun getGuarantorsByCustomerId(customerId: Long): Flow<List<Guarantor>>

    @Query("SELECT * FROM guarantors WHERE id = :id")
    fun getGuarantorById(id: Long): Flow<Guarantor?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuarantor(guarantor: Guarantor): Long

    @Update
    suspend fun updateGuarantor(guarantor: Guarantor)

    @Delete
    suspend fun deleteGuarantor(guarantor: Guarantor)
}