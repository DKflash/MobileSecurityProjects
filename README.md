# Mobile Security App

This is a simple Android application designed to implement a unique login mechanism that checks various conditions from the device before allowing the user to log in.
The application verifies the WiFi status, battery level, device orientation (pointing north), and if the phone is upside down.

## Features

- **WiFi Status Check:** Ensures that WiFi is enabled.
- **Battery Level Check:** Verifies that the first two digits of the entered password match the current battery level percentage.
- **Orientation Check:** Ensures the device is pointing north.
- **Upside Down Check:** Ensures the device is upside down.

## Screens

- **MainActivity:** Contains the login interface with the password field and login button.
- **ResultActivity:** A simple activity that displays a button to go back to the login screen.

## Dependencies

This project uses the following Android libraries:
- `com.google.android.material:material`

## Permissions

The following permissions are required in the AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.BATTERY_STATS"
    tools:ignore="ProtectedPermissions" />
<uses-permission android:name="android.permission.ACCELEROMETER"
        tools:ignore="WrongManifestParent" />
