# account_picker

Pick Email and Phone number saved on device in dialog without  extra permssion.
This plugin does not require any special permission.
It uses (AccountPicker)[https://developers.google.com/android/reference/com/google/android/gms/common/AccountPicker#newChooseAccountIntent(com.google.android.gms.common.AccountPicker.AccountChooserOptions)] API.

This currently works for Android only.




## TODO
Implement the Ios method channel


## Usage to request Phone
```dart
final String phone = await AccountPicker.phoneHint();
setState(() {
    _phoneNumber = phone;
});
```
## Usage to request Email

```dart
final EmailResult emailResult = await AccountPicker.emailHint();
print(emailResult);
setState(() {
    _email = emailResult.email;
    _accountType = emailResult.type;
});
```