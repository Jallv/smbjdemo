package xgimi.com.smbjdemo.smbjwrapper.core;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.PacketSignatory;
import com.hierynomus.smbj.session.ProxyPacketSignatory;
import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.mssrvs.ServerService;
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo0;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xgimi.com.smbjdemo.smbjwrapper.hpptd.HttpBean;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.NanoStreamer;
import xgimi.com.smbjdemo.smbjwrapper.utils.Reflect;

/**
 * @author anlong.jiang
 * @date on 2019/7/8
 */
public class ShareClient {
    private static volatile SMBClient mSmbClient = null;

    /**
     * New or reused mConnection.
     */
    private final Connection mConnection;
    private final Session mSession;
    private final String mServiceName;
    private final AuthenticationContext mAuthenticationContext;

    public ShareClient(String host, String name, String password, String domain) throws IOException {
        this(host, new AuthenticationContext(name, password.toCharArray(), domain));
    }

    public ShareClient(String host, AuthenticationContext authenticationContext) throws IOException {
        synchronized (ShareClient.class) {
            if (mSmbClient == null) {
                mSmbClient = new SMBClient();
            }
        }
        mAuthenticationContext = authenticationContext;
        mConnection = mSmbClient.connect(host);
        mSession = mConnection.authenticate(authenticationContext);
        cancelSignatureVerification();
        mServiceName = host;
    }

    /**
     * 取消数据包签名校验
     */
    private void cancelSignatureVerification() {
        PacketSignatory packetSignatory = Reflect.on(mSession).get("packetSignatory");
        ProxyPacketSignatory proxyPacketSignatory = new ProxyPacketSignatory(packetSignatory);
        Reflect.on(mSession).set("packetSignatory", proxyPacketSignatory);
    }

    public ShareItem getRootShareItem() {
        return new ShareRoot(this);
    }

    /**
     * Get the server name of the server.
     *
     * @return Name of the server
     */
    public String getServerName() {
        return mServiceName;
    }

    public Session getSession() {
        return mSession;
    }

    public void startNanoStreamer() {
        HttpBean.setmName(mAuthenticationContext.getUsername());
        HttpBean.setmPassword(
                String.valueOf(mAuthenticationContext.getPassword()));
        try {
            NanoStreamer.INSTANCE().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeClient() {
        try {
            if (mSession != null) {
                mSession.close();
            }
            if (mConnection != null) {
                mConnection.close(true);
            }
            if (mSmbClient != null) {
                mSmbClient.close();
            }
            NanoStreamer.INSTANCE().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
