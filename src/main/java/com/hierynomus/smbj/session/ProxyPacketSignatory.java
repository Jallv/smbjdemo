package com.hierynomus.smbj.session;


import com.hierynomus.mssmb2.SMB2Packet;

/**
 * @author anlong.jiang
 * @date on 2019/7/19
 * @describe TODO
 */
public class ProxyPacketSignatory extends PacketSignatory {
    private PacketSignatory mPacketSignatory;

    public ProxyPacketSignatory(PacketSignatory packetSignatory) {
        super(null, null);
        mPacketSignatory = packetSignatory;
    }

    @Override
    void init(byte[] secretKey) {
        mPacketSignatory.init(secretKey);
    }

    @Override
    boolean isInitialized() {
        return mPacketSignatory.isInitialized();
    }

    @Override
    SMB2Packet sign(SMB2Packet packet) {
        return mPacketSignatory.sign(packet);
    }

    @Override
    public boolean verify(SMB2Packet packet) {
        return true;
    }
}
