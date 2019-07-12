package xgimi.com.smbjdemo.smbjwrapper;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.mssrvs.ServerService;
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo0;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author anlong.jiang
 * @date on 2019/7/8
 * @describe TODO
 */
public class SharedConnection {
    private static volatile SMBClient mSmbClient = null;

    /**
     * New or reused mConnection.
     */
    private final Connection mConnection;
    private final Session mSession;
    private final String mServiceName;
    private final AuthenticationContext mAuthenticationContext;

    public SharedConnection(String host, String name, String password, String domain) throws IOException {
        this(host, new AuthenticationContext(name, password.toCharArray(), domain));
    }

    /**
     * @param host
     * @param authenticationContext
     * @throws IOException
     */
    public SharedConnection(String host, AuthenticationContext authenticationContext) throws IOException {
        synchronized (SharedConnection.class) {
            if (mSmbClient == null) {
                mSmbClient = new SMBClient(SmbConfig.builder()
                                                    .withSigningRequired(true)
                                                    .withDfsEnabled(true)
                                                    .withMultiProtocolNegotiate(true)
                                                    // .withReadBufferSize(1024*1024*8)
                                                    // .withWriteBufferSize(1024*1024*8)
                                                    .build());
            }
        }
        mAuthenticationContext = authenticationContext;
        mConnection = mSmbClient.connect(host);
        mSession = mConnection.authenticate(authenticationContext);
        mServiceName = host;
    }

    public AuthenticationContext getAuthenticationContext() {
        return mAuthenticationContext;
    }

    public List<String> getSharedNameList() throws IOException {
        final RPCTransport transport = SMBTransportFactories.SRVSVC.getTransport(mSession);
        final ServerService serverService = new ServerService(transport);
        final List<NetShareInfo0> shares = serverService.getShares0();
        List<String> sharedNameList = null;
        if (shares != null && shares.size() > 0) {
            sharedNameList = new ArrayList<>();
            for (NetShareInfo0 item : shares) {
                if ("IPC$".equals(item.getNetName())) {
                    continue;
                }
                sharedNameList.add(item.getNetName());
            }
        }
        return sharedNameList;

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

    public void close() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
