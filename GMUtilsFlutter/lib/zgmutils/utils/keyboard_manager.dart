import 'package:flutter/material.dart';

class KeyboardManage {
  FocusScopeNode? focusScopeNode;
  bool _isKeyboardVisible = false;

  KeyboardManage(BuildContext context) {
    focusScopeNode = FocusScope.of(context);
  }

  bool get isKeyboardVisible => _isKeyboardVisible;

  void registerObserver(KeyboardManageObserver observer) {
    focusScopeNode?.addListener(() {
      if (focusScopeNode == null) return;
      var fs = focusScopeNode!;

      if (fs.hasFocus) {
        bool showKb;
        showKb = fs.focusedChild?.context?.toString().contains('EditableText') == true;
        if (showKb) {
          if (_isKeyboardVisible != true) {
            _isKeyboardVisible = true;
            observer(true);
          }
        } else {
          if (_isKeyboardVisible != false) {
            _isKeyboardVisible = false;
            observer(false);
          }
        }
      } else {
        if (_isKeyboardVisible != false) {
          _isKeyboardVisible = false;
          observer(false);
        }
      }
    });
  }

  void dismissKeyboard() {
    focusScopeNode?.unfocus();
  }

  static void dismissKeyboardS(BuildContext context) {
    FocusScope.of(context).unfocus();
  }

  void dispose() {
    Future.delayed(const Duration(milliseconds: 500), () {
      try {
        focusScopeNode?.dispose();
      } catch (e) {}

      focusScopeNode = null;
    });
  }
}

typedef KeyboardManageObserver = void Function(bool visible);
