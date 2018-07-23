# Zero Shutter Camera
Zero Shutter Camera is a native Google Glass application that triggers a camera snapshot at special moments for the wearer by continuously monitoring the wearer's physiological state over a Bluetooth connection.

## Prototype concept and implementation
Zero Shutter Camera is a prototype to show a potential of using wearer’s physiological signals as inputs. (This work was presented at [ACM UIST 2014](http://uist.acm.org/uist2014/).)

![Zero Shutter Camera implementation](https://raw.github.com/zshiba/zero-shutter-camera/master/images/implementation.png)


## Getting Started

### 1. Development Environment
1. Google Glass ([XE18.3](https://developers.google.com/glass/tools-downloads/system))
   - Debug mode: ON
2. [ADT Bundle](http://developer.android.com/sdk/index.html)
   - Android 4.4.2 SDK, API level 19
   - Glass Development Kit (GDK) Preview, Rev. 5

### 2. Installation
1. Download [Glass menu icons](https://developers.google.com/glass/tools-downloads/downloads).
2. Unzip the file and copy "ic_bluetooth_50.png", "ic_camera_50.png", "ic_camera_70.png", "ic_done_50.png" and "ic_no_50.png" into the "zero-shutter-camera/ZeroShutterCamera/res/drawable" directory.
3. Import zero-shutter-camera/ZeroShutterCamera project on ADT
4. Connect Google Glass with a USB cable to the computer
5. Select ZeroShutterCamera project and then Hit "Run" button


### 3. Start Zero Shutter Camera
- By Voice command, on the home screen, say "ok, glass" and then "zero shutter camera"
- By tapping, tap the home screen, and then tap "Zero Shutter Camera"

![Start Zero Shutter Camera](https://raw.github.com/zshiba/zero-shutter-camera/master/images/start.png)


### 4. Menus on live cards
Zero Shutter Camera will insert 2 live cards to the Glass timeline. To use menus, tap:  
On the left live card showing its state, then the user can:
- Start/Stop receiving requests for taking pictures
- Close Zero Shutter Camera app

![Left Live card](https://raw.github.com/zshiba/zero-shutter-camera/master/images/left-livecard.png)

On the right live card showing a connected Bluetooth device, a sender, and received information, then the user can:
- Disconnect from the connected Bluetooth device if any

![Right Live card](https://raw.github.com/zshiba/zero-shutter-camera/master/images/right-livecard.png)


### 5. How to receive information
Send information as the defined string format to Glass over Bluetooth connection.

##### How to test Zero Shutter Camera

(on Android device)
1. Unpair a sender device from Glass

(on Glass)  
2. Launch ZeroShutterCamera app  
3. Move to the right live card showing the connected Bluetooth device, which is currently None  
4. Move to the Bluetooth card under the Settings live card  
5. If Glass has already paired an Android device, tap once and choose "Forget"  
6. Tap once on the Bluetooth card, and then select "Android". While Glass is showing the instruction, move to Step. 5

(on Android device)  
7. Launch an app that sends information to Glass  
8. Pair to Glass when a popup window asking to pair to Glass  

(on Glass)  
9. Make sure Glass is showing the same message to pair to the Android device  
10. Make sure Glass is showing the sender device's name and received infromation


### 6. How to take pictures
Zero Shutter Camera will automatically trigger a camera shutter at special moments, based on received information.


### 7. How to stop
On the left live cards, tap once, and then tap on the "Done" menu.


### 8. How to force stop (Just in case)
Connect Glass to the computer, and then use the following ABD command.
```
$ adb shell am force-stop com.zshiba.android.glass.zeroshuttercamera
```


### 9. How to uninstall
Connect Glass to the computer, and then use the following ABD command.
```
$ adb shell pm uninstall com.zshiba.android.glass.zeroshuttercamera
```

## License
MIT
