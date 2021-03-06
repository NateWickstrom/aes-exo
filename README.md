Encrypted playback with ExoPlayer
=======

Example App that plays a local aes encrytped aac file using Exo Player

Encryption
-------

[Sample files][1] can be encypted using the following OpenSSL command:
```
openssl enc -aes-128-cbc -nosalt -p -in sample.aac -out sample.enc
```
Replace the `AES_KEY` and `AES_IV` values in `MainActivity` with the output from the above command.
```java
byte[] AES_KEY = hexStringToByteArray("<your_new_aes_key>");
byte[] AES_IV = hexStringToByteArray("<your_aes_iv>");
```
If you change the ecryption method, remember to update the `Cipher` instance inside the `AesDataSource`:
```java
Cipher.getInstance("AES/CBC/NoPadding")
```

ToDo
-------

Only files with AAC containers work for music playback.  M4A and MP4 containers will not work due to some unresolved bugs. 

License
--------

    Copyright 2016 Nathan Wickstrom.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[1]: http://download.wavetlan.com/SVV/Media/HTTP/http-aac.htm
