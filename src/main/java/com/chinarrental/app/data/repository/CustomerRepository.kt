package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.CustomerDao
import com.chinarrental.app.data.dao.GuarantorDao
import com.chinarrental.app.data.model.Customer
import com.chinarrental.app.data.model.Guarantor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private val guarantorDao: GuarantorDao
) {

    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()

    fun getCustomerById(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)

    fun searchCustomers(query: String): Flow<List<Customer>> =
        customerDao.searchCustomers(query)

    fun getActiveCustomers(): Flow<List<Customer>> = customerDao.getActiveCustomers()

    fun getCustomersByCnic(cnic: String): Flow<List<Customer>> =
        customerDao.getCustomersByCnic(cnic)

    fun getTotalCustomersCount(): Flow<Int> = customerDao.getTotalCustomersCount()

    fun getGuarantorsByCustomerId(customerId: Long): Flow<List<Guarantor>> =
        guarantorDao.getGuarantorsByCustomerId(customerId)

    suspend fun insertCustomer(customer: Customer): Result<Long> {
        return try {
            val id = customerDao.insertCustomer(customer)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCustomer(customer: Customer): Result<Unit> {
        return try {
            customerDao.updateCustomer(customer)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCustomer(customer: Customer): Result<Unit> {
        return try {
            customerDao.deleteCustomer(customer)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertGuarantor(guarantor: Guarantor): Result<Long> {
        return try {
            val id = guarantorDao.insertGuarantor(guarantor)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGuarantor(guarantor: Guarantor): Result<Unit> {
        return try {
            guarantorDao.updateGuarantor(guarantor)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGuarantor(guarantor: Guarantor): Result<Unit> {
        return try {
            guarantorDao.deleteGuarantor(guarantor)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
