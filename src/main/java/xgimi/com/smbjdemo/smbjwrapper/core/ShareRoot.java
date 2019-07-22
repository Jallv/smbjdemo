package xgimi.com.smbjdemo.smbjwrapper.core;

import com.hierynomus.msdtyp.FileTime;
import com.hierynomus.smbj.common.SmbPath;
import com.rapid7.client.dcerpc.mssrvs.ServerService;
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo0;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anlong.jiang
 * @date on 2019/7/22
 * @describe TODO
 */
public class ShareRoot implements ShareItem {
    public ShareClient mShareClient;

    public ShareRoot(ShareClient shareClient) {
        mShareClient = shareClient;
    }

    private List<ShareItem> shareItemCache = null;

    private List<ShareItem> getSharedNameList() throws IOException {
        if (shareItemCache == null) {
            final RPCTransport transport = SMBTransportFactories.SRVSVC.getTransport(mShareClient.getSession());
            final ServerService serverService = new ServerService(transport);
            final List<NetShareInfo0> shares = serverService.getShares0();
            List<ShareItem> sharedNameList = null;
            if (shares != null && shares.size() > 0) {
                sharedNameList = new ArrayList<>();
                for (NetShareInfo0 item : shares) {
                    if ("IPC$".equals(item.getNetName())) {
                        continue;
                    }
                    ShareDisk shareDisk = new ShareDisk(mShareClient, item.getNetName());
                    sharedNameList.add(shareDisk);
                }
            }
            shareItemCache = sharedNameList;
        }
        return shareItemCache;
    }

    @Override
    public boolean isExisting() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public String getServerName() {
        return mShareClient.getServerName();
    }

    @Override
    public String getShareName() {
        return null;
    }

    @Override
    public String getName() {
        return getServerName();
    }

    @Override
    public String getPath() {
        return getServerName();
    }

    @Override
    public String getSmbPath() {
        return new SmbPath(getServerName(), "","").toUncPath();
    }

    @Override
    public ShareItem renameTo(String newFileName, boolean replaceIfExist) {
        return null;
    }

    @Override
    public FileTime getCreationTime() {
        return null;
    }

    @Override
    public FileTime getLastAccessTime() {
        return null;
    }

    @Override
    public FileTime getLastWriteTime() {
        return null;
    }

    @Override
    public FileTime getChangeTime() {
        return null;
    }

    @Override
    public List<ShareItem> getFileList() {
        try {
            return getSharedNameList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ShareItem getParentFile() {
        return null;
    }

    @Override
    public String getParentPath() {
        return null;
    }

    @Override
    public boolean isRoot() {
        return true;
    }
}
