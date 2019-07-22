# smbjdemo
Android realizes samba access and file playback through SMBJ, smbj rpc, ServerSocket

# problem
Now the transmission speed is about 100kb/s, this problem is still to be solved.
Solution:
Looking at the smbj source code, we find that the packet signature verification algorithm is very time-consuming. First, we cancel the packet signature verification through reflection proxy.
```java
private void cancelSignatureVerification() {
        PacketSignatory packetSignatory = Reflect.on(mSession).get("packetSignatory");
        ProxyPacketSignatory proxyPacketSignatory = new ProxyPacketSignatory(packetSignatory);
        Reflect.on(mSession).set("packetSignatory", proxyPacketSignatory);
    }
```

