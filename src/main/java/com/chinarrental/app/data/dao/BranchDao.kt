package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Branch
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Query("SELECT * FROM branches ORDER BY name ASC")
    fun getAllBranches(): Flow<List<Branch>>

    @Query("SELECT * FROM branches WHERE id = :id")
    fun getBranchById(id: Long): Flow<Branch?>

    @Query("SELECT * FROM branches WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveBranches(): Flow<List<Branch>>

    @Query("SELECT * FROM branches WHERE name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchBranches(query: String): Flow<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch): Long

    @Update
    suspend fun updateBranch(branch: Branch)

    @Delete
    suspend fun deleteBranch(branch: Branch)
}