package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Item
import com.chinarrental.app.data.model.ItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Long): Flow<Item?>

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name ASC")
    fun getItemsByCategory(category: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE status = :status ORDER BY name ASC")
    fun getItemsByStatus(status: ItemStatus): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE availableQuantity <= lowStockThreshold")
    fun getLowStockItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<Item>>

    @Query("SELECT DISTINCT category FROM items ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("UPDATE items SET availableQuantity = availableQuantity - :quantity WHERE id = :itemId")
    suspend fun decreaseAvailableQuantity(itemId: Long, quantity: Int)

    @Query("UPDATE items SET availableQuantity = availableQuantity + :quantity WHERE id = :itemId")
    suspend fun increaseAvailableQuantity(itemId: Long, quantity: Int)
}
