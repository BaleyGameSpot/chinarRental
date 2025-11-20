package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.BranchDao
import com.chinarrental.app.data.model.Branch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepository @Inject constructor(
    private val branchDao: BranchDao
) {

    fun getAllBranches(): Flow<List<Branch>> = branchDao.getAllBranches()

    fun getBranchById(id: Long): Flow<Branch?> = branchDao.getBranchById(id)

    fun getActiveBranches(): Flow<List<Branch>> = branchDao.getActiveBranches()

    fun searchBranches(query: String): Flow<List<Branch>> = branchDao.searchBranches(query)

    suspend fun insertBranch(branch: Branch): Result<Long> {
        return try {
            val id = branchDao.insertBranch(branch)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBranch(branch: Branch): Result<Unit> {
        return try {
            branchDao.updateBranch(branch)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBranch(branch: Branch): Result<Unit> {
        return try {
            branchDao.deleteBranch(branch)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
