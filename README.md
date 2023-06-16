# Account Picker Plugin

This Flutter plugin prompt suser to pick an Email or a Phone number saved on device without requiring extra permission
It is a lightweight plugin that does not require extra permssion.
This plugin does not require any special device permission.

It uses [AccountPicker](https://developers.google.com/android/reference/com/google/android/gms/common/AccountPicker#newChooseAccountIntent(com.google.android.gms.common.AccountPicker.AccountChooserOptions)) API.

This currently works for Android only.


<p align="center">
  <img src="https://raw.githubusercontent.com/tolotrasamuel/account_picker/master/screenshot/demo.gif" alt="Demo App" style="margin:auto" width="372" height="686">
</p>


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





## TODO
Accepting PR for:
 - https://github.com/tolotrasamuel/account_picker/issues/8 
