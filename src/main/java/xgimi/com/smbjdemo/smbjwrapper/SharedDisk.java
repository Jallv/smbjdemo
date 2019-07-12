package xgimi.com.smbjdemo.smbjwrapper;




import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.share.DiskShare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xgimi.com.smbjdemo.smbjwrapper.core.AbstractSharedItem;
import xgimi.com.smbjdemo.smbjwrapper.core.SharedItem;
import xgimi.com.smbjdemo.smbjwrapper.utils.ShareUtils;

/**
 * @author anlong.jiang
 * @date on 2019/7/8
 * @describe TODO
 */
public class SharedDisk extends AbstractSharedItem<SharedDisk> {
    public SharedDisk(SharedConnection connection, String sharedName) {
        super(connection,(DiskShare) connection.getSession().connectShare(sharedName),"");
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }

    @Override
    protected SharedDisk createSharedNodeItem(String pathName) {
        return null;
    }

    @Override
    public SharedItem renameTo(String newFileName, boolean replaceIfExist) {
        return null;
    }
    public List<SharedFile> getFileList() {
        List<SharedFile> fileList = new ArrayList<>();
        for (FileIdBothDirectoryInformation item : getDiskShare().list(getPath())) {
            if(!ShareUtils.isValidSharedItemName(item.getFileName())){
                continue;
            }
            fileList.add(new SharedFile(getSharedConnection(), getDiskShare(),
                    getPath() + (ShareUtils.isEmpty(getPath()) ? "" : PATH_SEPARATOR) + item.getFileName()));
        }
        Collections.sort(fileList, new Comparator<SharedFile>() {
            @Override
            public int compare(SharedFile o1, SharedFile o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return fileList;
    }
}
