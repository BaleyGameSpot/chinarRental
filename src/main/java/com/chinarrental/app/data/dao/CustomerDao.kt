package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Long): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE phone = :phone OR phone2 = :phone")
    fun getCustomerByPhone(phone: String): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR cnicNumber LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE hasDiscount = 1")
    fun getCustomersWithDiscount(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE cnicNumber LIKE '%' || :cnic || '%'")
    fun getCustomersByCnic(cnic: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id > 0 ORDER BY name ASC")
    fun getActiveCustomers(): Flow<List<Customer>>

    @Query("SELECT COUNT(*) FROM customers")
    fun getTotalCustomersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)
}