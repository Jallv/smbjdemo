package xgimi.com.smbjdemo.smbjwrapper.core;

/**
 * @author anlong.jiang
 * @date on 2019/7/19
 * @describe TODO
 */
public class SmbDevice {
    private String address;
    private String mUser;
    private String mPassWord;
    private String mName = "Default Smb Device";
    public SmbDevice() {
        this.address = "";
        this.mUser = "";
        this.mPassWord = "";
        this.mName = "Default Smb Device";
    }

    public SmbDevice(String ipaddress) {
        this.address = ipaddress;
        this.mUser = "";
        this.mPassWord = "";
        this.mName = "Default Smb Device";
    }

    @Override
    public String toString() {
        return "SmbDevice{" +
                "address='" + address + '\'' +
                ", mUser='" + mUser + '\'' +
                ", mPassWord='" + mPassWord + '\'' +
                ", mName='" + mName + '\'' +
                '}';
    }
}
