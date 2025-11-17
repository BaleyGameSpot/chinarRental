# Chinar Rental - Complete Rental Management System

A comprehensive Android application for managing rental business operations, built with Kotlin and Jetpack Compose.

## 📱 Features

### 1. Daily Rent Calculator
- Per-day rent configuration for each item
- Auto calculation from start date to return date
- **Automatic overdue rent calculation** (50% extra charge)
- Damage charges management
- Discount application (percentage or flat amount)

### 2. Customer Advance Payment Record
- Advance amount entry and tracking
- Remaining balance auto-calculation
- Payment history for each customer
- Automatic payment due reminders

### 3. Google Drive Backup & Cloud Sync
- Automatic Google Drive backup
- Export data to Excel and PDF formats
- Configurable auto-backup schedule (Daily/Weekly)
- Manual backup on demand

### 4. Comprehensive Reminder System
- **Exact time reminders** with notifications
- SMS notifications support
- Return date reminders
- Payment due reminders
- Low stock alerts
- Overdue rental notifications

### 5. Advanced Search Features
- Search items by name or category
- Customer search (name, phone, CNIC)
- Invoice/Bill search by number
- Date-wise search for all records

### 6. Location Management
- Save customer location with Google Maps integration
- Item pickup/drop location tracking
- Multiple shop branches support
- Map view for all locations

### 7. Guarantor Management (Zimmedar/Bashnakht)
- Guarantor details for each customer
- CNIC photo upload (front & back)
- Direct calling option
- SMS messaging to guarantor

### 8. Customer Messaging
- In-app messaging system
- **Automatic SMS for:**
  - Due date reminders
  - Payment reminders
  - Bill confirmations
  - Overdue notifications
- WhatsApp message templates
- Custom message templates

### 9. Bill Management
- Bill picture upload (camera + gallery)
- Professional PDF bill generation
- Bill history tracking
- Automatic bill number generation

### 10. Roznamcha (Daily/Monthly Accounting)
- Daily income & expense tracking
- Daily rent received
- Loan/advance management
- **Graphical reports** with charts
- Monthly summaries
- Category-wise tracking

### 11. Shop Inventory Management
- Add items with categories
- Set rent per day
- Quantity management
- **Availability status tracking:**
  - Available
  - Partially Rented
  - Fully Rented
  - Maintenance
  - Damaged
- Low stock alerts

### 12. Discount/Concession System
- Percentage-based discount
- Flat amount discount
- Auto-apply for specific customers
- Discount reports

### 13. Multi-User & Multi-SIM Support
- 1 or 2 SIM support for SMS
- Multiple device login
- **User roles:**
  - Admin
  - Manager
  - Staff
- Permission-based access

## 🏗️ Architecture

### Technology Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Room (SQLite)
- **Dependency Injection:** Hilt/Dagger
- **Async Operations:** Kotlin Coroutines & Flow
- **Background Tasks:** WorkManager
- **Maps:** Google Maps SDK
- **Cloud Storage:** Google Drive API
- **Messaging:** Firebase Cloud Messaging

### Project Structure
```
app/
├── data/
│   ├── model/          # Data models/entities
│   ├── dao/            # Database access objects
│   ├── repository/     # Repository pattern implementation
│   └── RentalDatabase.kt
├── di/                 # Dependency injection modules
├── notification/       # Notification & reminder services
├── ui/
│   ├── screens/        # Composable screens
│   ├── navigation/     # Navigation setup
│   └── theme/          # Material Design theme
├── util/              # Utility classes
│   ├── RentalCalculator.kt
│   ├── PdfGenerator.kt
│   └── SmsManager.kt
└── RentalApp.kt       # Application class
```

## 📊 Database Schema

### Main Entities
1. **Item** - Rental items with pricing and availability
2. **Customer** - Customer details with location and discount info
3. **Guarantor** - Guarantor information with CNIC
4. **Rental** - Active and historical rentals
5. **Payment** - All payment transactions
6. **Transaction** - Daily income/expense (Roznamcha)
7. **Reminder** - Scheduled reminders
8. **Bill** - Generated bills with images
9. **User** - Multi-user support
10. **Branch** - Multiple branch management

## 🚀 Getting Started

### Prerequisites
- Android Studio Iguana or later
- JDK 17
- Android SDK API 24+
- Google Maps API Key
- Firebase project (for FCM and Google Sign-in)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/BaleyGameSpot/chinarRental.git
   cd chinarRental
   ```

2. **Configure Google Services**
   - Replace `app/google-services.json` with your Firebase project configuration
   - Get Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Update `AndroidManifest.xml` with your Google Maps API key

3. **Update API Keys**
   ```xml
   <!-- In AndroidManifest.xml -->
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_GOOGLE_MAPS_API_KEY" />
   ```

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open the project in Android Studio and click Run.

## 📱 Key Permissions

The app requires the following permissions:
- **CAMERA** - For CNIC and bill photo capture
- **LOCATION** - For saving customer and pickup locations
- **SEND_SMS** - For sending reminders and notifications
- **CALL_PHONE** - For direct calling to customers/guarantors
- **POST_NOTIFICATIONS** - For reminder notifications (Android 13+)
- **INTERNET** - For cloud backup and sync

## 💡 Features Implementation

### Rental Calculator
```kotlin
// Automatic calculation with overdue charges
val calculation = RentalCalculator.calculateRental(
    startDate = startDate,
    expectedReturnDate = expectedReturnDate,
    actualReturnDate = actualReturnDate,
    rentPerDay = 100.0,
    quantity = 2,
    damageCharges = 500.0,
    discountAmount = 50.0
)
```

### SMS Reminders
```kotlin
// Send automatic SMS reminders
SmsManager.sendSms(
    phoneNumber = customer.phone,
    message = SmsManager.getReturnReminderMessage(
        customerName = customer.name,
        itemName = item.name,
        returnDate = formattedDate
    )
)
```

### PDF Bill Generation
```kotlin
// Generate professional PDF bills
val pdfFile = PdfGenerator.generateBillPdf(
    context = context,
    billNumber = "BILL-001",
    customerName = "John Doe",
    items = listOf(...),
    totalAmount = 5000.0
)
```

## 🎨 UI Screenshots

The app features a modern, professional UI with:
- Material Design 3 components
- Intuitive navigation
- Dashboard with quick stats
- Category-wise organization
- Beautiful charts and reports

## 📈 Future Enhancements

Potential features to add:
- Barcode/QR code scanning for items
- Biometric authentication
- Voice commands
- Multilingual support (Urdu, English)
- Online payment integration
- Customer portal/app
- Analytics dashboard

## 🔐 Security Features

- Secure local database with Room
- Encrypted preferences for sensitive data
- User authentication and authorization
- Role-based access control
- CNIC photo encryption

## 📝 License

This project is created for Chinar Rental business management.

## 👥 Contact & Support

For support or queries:
- Create an issue in this repository
- Contact: [Your contact information]

## 🙏 Acknowledgments

Built with:
- Android Jetpack libraries
- Material Design 3
- iText PDF library
- Apache POI for Excel
- MPAndroidChart for charts
- Firebase services

---

**Version:** 1.0.0
**Last Updated:** 2025
**Minimum Android Version:** Android 7.0 (API 24)
**Target Android Version:** Android 14 (API 34)
