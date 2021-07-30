import 'dart:async';
import 'dart:io';

import 'package:account_picker/account_picker.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _email;
  String _accountType;
  String _phoneNumber;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Text('Running on: ${Platform.operatingSystem}\n'),
              Text('Email: $_email'),
              Text('Email Type: $_accountType'),
              Text('Phone Number: $_phoneNumber'),
              ElevatedButton(
                onPressed: () async {
                  final String phone = await AccountPicker.phoneHint();
                  setState(() {
                    _phoneNumber = phone;
                  });
                },
                child: Text('Pick Phone'),
              ),
              ElevatedButton(
                onPressed: () async {
                  final EmailResult emailResult =
                      await AccountPicker.emailHint();
                  print(emailResult);
                  setState(() {
                    _email = emailResult.email;
                    _accountType = emailResult.type;
                  });
                },
                child: Text('Pick Email'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
