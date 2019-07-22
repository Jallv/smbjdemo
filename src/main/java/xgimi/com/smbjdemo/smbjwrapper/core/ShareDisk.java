package xgimi.com.smbjdemo.smbjwrapper.core;


import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.share.DiskShare;

import java.util.ArrayList;
import java.util.List;

import xgimi.com.smbjdemo.smbjwrapper.utils.ShareUtils;

/**
 * @author anlong.jiang
 * @date on 2019/7/8
 * @describe TODO
 */
public class ShareDisk extends AbstractShareItem<ShareDisk> {

    public ShareDisk(ShareClient connection, String sharedName) {
        super(connection, null, "");
        this.shareName = sharedName;
    }

    public ShareDisk(ShareClient connection, DiskShare diskShare) {
        super(connection, diskShare, "");
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ShareDisk) {
            return getShareName().equals(((ShareDisk) object).getShareName());
        }
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public ShareItem renameTo(String newFileName, boolean replaceIfExist) {
        return null;
    }

    @Override
    public List<ShareItem> getFileList() {
        List<ShareItem> fileList = new ArrayList<>();
        for (FileIdBothDirectoryInformation item : getDiskShare().list(getPath())) {
            if (!ShareUtils.isValidSharedItemName(item.getFileName())) {
                continue;
            }
            fileList.add(new ShareFile(getShareClient(), getDiskShare(),
                    getPath() + (ShareUtils.isEmpty(getPath()) ? "" : PATH_SEPARATOR) + item.getFileName()));
        }
        return fileList;
    }

    @Override
    public ShareItem getParentFile() {
        if (isRoot()) {
            return null;
        }
        return getShareClient().getRootShareItem();
    }

    @Override
    public String getShareName() {
        return shareName != null ? shareName : super.getShareName();
    }

    @Override
    public String getName() {
        return shareName;
    }
}
