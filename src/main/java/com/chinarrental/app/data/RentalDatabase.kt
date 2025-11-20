package com.chinarrental.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chinarrental.app.data.dao.*
import com.chinarrental.app.data.model.*

@Database(
    entities = [
        Item::class,
        Customer::class,
        Guarantor::class,
        Rental::class,
        Payment::class,
        Transaction::class,
        Reminder::class,
        Bill::class,
        User::class,
        Branch::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RentalDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun customerDao(): CustomerDao
    abstract fun guarantorDao(): GuarantorDao
    abstract fun rentalDao(): RentalDao
    abstract fun paymentDao(): PaymentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun reminderDao(): ReminderDao
    abstract fun billDao(): BillDao
    abstract fun userDao(): UserDao
    abstract fun branchDao(): BranchDao

    companion object {
        @Volatile
        private var INSTANCE: RentalDatabase? = null

        fun getDatabase(context: Context): RentalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RentalDatabase::class.java,
                    "rental_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
