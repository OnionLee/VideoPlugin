package com.onionlee.videoplugin;

import com.unity3d.player.UnityPlayer;

public class Test {
 public static void CallAndroid_U(String strMsg)
 {
  String strSendMsg = strMsg + " Hello Unity3D";
  UnityPlayer.UnitySendMessage("AndroidJarTest", "SetLog", strSendMsg);
 }

}