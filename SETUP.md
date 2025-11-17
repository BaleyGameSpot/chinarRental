# Chinar Rental - Setup Guide

## 🔧 Initial Setup Steps

### 1. Firebase Configuration

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing one
3. Add an Android app with package name: `com.chinarrental.app`
4. Download `google-services.json`
5. Replace the file at: `app/google-services.json`

### 2. Google Maps API Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable these APIs:
   - Maps SDK for Android
   - Places API
   - Geocoding API
3. Create API Key with Android restriction
4. Update API key in `app/src/main/AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_ACTUAL_API_KEY_HERE" />
   ```

### 3. Google Drive API Setup

1. In Google Cloud Console, enable **Google Drive API**
2. Create OAuth 2.0 credentials
3. Download the credentials JSON
4. The app will handle authentication at runtime

### 4. Building the Project

#### Using Android Studio (Recommended)
1. Open Android Studio
2. File → Open → Select `chinarRental` folder
3. Wait for Gradle sync to complete
4. Click Run (Green play button) or Shift+F10

#### Using Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### 5. Testing on Device

#### Requirements
- Android device with API 24+ (Android 7.0+)
- USB debugging enabled
- Or use Android Emulator

#### Steps
1. Connect device via USB
2. Enable USB debugging in Developer Options
3. Run: `adb devices` to verify connection
4. Click Run in Android Studio

## 🎯 Feature Configuration

### SMS Configuration

The app requires SMS permissions. To test:
1. Grant SMS permissions when prompted
2. Test with actual phone numbers
3. SMS costs may apply based on your carrier

### Notification Setup

For testing reminders:
1. Grant notification permission (Android 13+)
2. Ensure battery optimization is disabled for the app
3. Test alarm/reminder functionality

### Camera & Storage

1. Grant camera permission for CNIC photos
2. Grant storage permission for bill images
3. Files are stored in app's external storage

## 📱 Testing Checklist

- [ ] Create a customer with guarantor
- [ ] Upload CNIC photos
- [ ] Add items to inventory
- [ ] Create a rental
- [ ] Record a payment
- [ ] Generate a bill PDF
- [ ] Set a reminder
- [ ] Test SMS sending (if SIM available)
- [ ] Test location saving
- [ ] Export data to Excel
- [ ] Test Google Drive backup

## 🚨 Troubleshooting

### Build Fails
- Clean project: Build → Clean Project
- Invalidate caches: File → Invalidate Caches / Restart
- Check Gradle version compatibility
- Ensure JDK 17 is being used

### Google Services Error
- Verify `google-services.json` is in `app/` directory
- Check package name matches Firebase console
- Sync project with Gradle files

### Maps Not Showing
- Verify API key is correct
- Check API is enabled in Cloud Console
- Ensure billing is enabled for the project
- Check Android SHA-1 fingerprint is added

### Database Issues
- App may need reinstall for schema changes
- Check Room migrations if needed
- Use Database Inspector in Android Studio

## 🔑 Getting SHA-1 Fingerprint

For Firebase and Google Sign-In:

```bash
# Debug keystore
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Release keystore (if you have one)
keytool -list -v -keystore /path/to/your/keystore -alias your-alias
```

Add the SHA-1 to Firebase Console under Project Settings → Your App

## 📦 Dependencies

All dependencies are managed in `app/build.gradle.kts`. Key libraries:

- **Jetpack Compose** - Modern UI toolkit
- **Room Database** - Local data persistence
- **Hilt** - Dependency injection
- **WorkManager** - Background tasks
- **Coil** - Image loading
- **iText** - PDF generation
- **Apache POI** - Excel export
- **MPAndroidChart** - Charts and graphs

## 🎨 Customization

### Change App Name
Update in `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Your App Name</string>
```

### Change Colors
Edit `app/src/main/java/com/chinarrental/app/ui/theme/Color.kt`

### Change Package Name
Use Android Studio: Right-click package → Refactor → Rename

## 📊 Database Inspection

Android Studio includes Database Inspector:
1. Run app on device/emulator
2. View → Tool Windows → App Inspection
3. Select Database Inspector tab
4. Explore tables and data

## 🔄 Version Updates

When updating the app version:
1. Update `versionCode` and `versionName` in `app/build.gradle.kts`
2. Consider database migrations if schema changed
3. Update CHANGELOG if maintaining one

## 📞 Support

If you encounter issues:
1. Check this guide first
2. Search existing GitHub issues
3. Create a new issue with:
   - Android version
   - Device model
   - Error logs (from Logcat)
   - Steps to reproduce

---

**Happy Coding! 🚀**
