package xgimi.com.smbjdemo.smbjwrapper.core;

import com.hierynomus.msdtyp.FileTime;
import com.hierynomus.msfscc.fileinformation.FileBasicInformation;
import com.hierynomus.smbj.common.SmbPath;
import com.hierynomus.smbj.share.DiskShare;


import xgimi.com.smbjdemo.smbjwrapper.utils.ShareUtils;


/**
 * This class provides a common abstracted class that represents a directory/file like node.
 *
 * @param <T> Generic type that defined a directory/file like node that is used for item creation
 * @author Simon Wächter
 */
public abstract class AbstractShareItem<T extends ShareItem> implements ShareItem {
    public static final String SHARE_ROOT="smb:shareRoot";
    public static final String DISK_ROOT="smb:diskRoot";
    protected String shareName;
    /**
     * String used to separate paths.
     */
    public static final String PATH_SEPARATOR = "\\";

    /**
     * Shared connection to access the server.
     */
    protected final ShareClient mShareClient;

    /**
     * Path name of the abstract shared item.
     */
    private final String pathName;

    private DiskShare diskShare;

    /**
     * Create a new abstract shared item based on the shared connection and the path name.
     *
     * @param shareClient Shared connection
     * @param pathName    Path name
     * @throws RuntimeException Exception in case of an invalid path name
     */
    public AbstractShareItem(ShareClient shareClient, DiskShare diskShare, String pathName) {
        this.mShareClient = shareClient;
        this.diskShare = diskShare;
        if (ShareUtils.isValidSharedItemName(pathName)) {
            this.pathName = pathName;
        } else {
            throw new RuntimeException("The given path name is not a valid share path");
        }
    }

    public synchronized DiskShare getDiskShare() {
        if (diskShare == null && shareName!=null) {
            diskShare = (DiskShare) mShareClient.getSession().connectShare(shareName);
        }
        return diskShare;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExisting() {
        return isDirectory() || isFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirectory() {
        return getDiskShare().folderExists(pathName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFile() {
        return getDiskShare().fileExists(pathName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (!pathName.isEmpty()) {
            int lastIndex = pathName.lastIndexOf(PATH_SEPARATOR);
            return pathName.substring(lastIndex + 1, pathName.length());
        } else {
            return pathName;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        return mShareClient.getServerName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShareName() {
        return getDiskShare().getSmbPath().getShareName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return pathName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSmbPath() {
        SmbPath smbPath = new SmbPath(getServerName(), getShareName(), pathName.replace(PATH_SEPARATOR, "\\"));
        return smbPath.toUncPath();
    }

    @Override
    public String getParentPath() {
        int lastIndex = pathName.lastIndexOf(PATH_SEPARATOR);
        if(lastIndex!=-1){
            return pathName.substring(0, lastIndex);
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime getCreationTime() {
        FileBasicInformation fileBasicInformation = getDiskShare().getFileInformation(pathName).getBasicInformation();
        return fileBasicInformation.getCreationTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime getLastAccessTime() {
        FileBasicInformation fileBasicInformation = getDiskShare().getFileInformation(pathName).getBasicInformation();
        return fileBasicInformation.getLastAccessTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime getLastWriteTime() {
        FileBasicInformation fileBasicInformation = getDiskShare().getFileInformation(pathName).getBasicInformation();
        return fileBasicInformation.getLastWriteTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime getChangeTime() {
        FileBasicInformation fileBasicInformation = getDiskShare().getFileInformation(pathName).getBasicInformation();
        return fileBasicInformation.getChangeTime();
    }

    /**
     * Check if the current and the given objects are equals.
     *
     * @param object Given object to compare against
     * @return Status of the check
     */
    @Override
    public abstract boolean equals(Object object);

    /**
     * Get the shared connection.
     *
     * @return Shared connection
     */
    protected ShareClient getShareClient() {
        return mShareClient;
    }

    @Override
    public boolean isRoot() {
        return false;
    }
}
