package xgimi.com.smbjdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mserref.NtStatus;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import xgimi.com.smbjdemo.smbjwrapper.SharedConnection;
import xgimi.com.smbjdemo.smbjwrapper.SharedDisk;
import xgimi.com.smbjdemo.smbjwrapper.SharedFile;
import xgimi.com.smbjdemo.smbjwrapper.core.SharedItem;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.HttpBean;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.NanoHTTPD;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.NanoStreamer;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.SmbHelper;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.SmbTools;
import xgimi.com.smbjdemo.smbjwrapper.utils.MethodAverageTime;

/**
 * @author anlong.jiang
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText hostEditText, userEditText, passwordEditText, domainEditText;
    private Button loginButton;
    private TextView pathText;
    private RecyclerView mContentView;
    private FileAdapter mFileAdapter;
    private ImageView mImageView;

    private SharedConnection mSharedConnection;
    private List<SharedFile> currentFileList;
    private SharedItem mCurrentSharedFile;
    private List<SharedFile> sharedDiskList = new ArrayList<>();
    private SharedDisk currentSharedDisk;
    private NanoHTTPD mNanoHTTPD;
    private MethodAverageTime mMethodAverageTime = new MethodAverageTime("SharedInputStream");
    private long allSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // mImageView = findViewById(R.id.picture);
        // new Thread() {
        //     @Override
        //     public void run() {
        //         super.run();
        //         try {
        //             SMBClient smbClient = new SMBClient(SmbConfig.builder()
        //                                                          .withSigningRequired(true)
        //                                                          .withDfsEnabled(true)
        //                                                          .withMultiProtocolNegotiate(true)
        //                                                          // .withBufferSize(1028*1024*8)
        //                                                          .build());
        //             Connection connection=smbClient.connect("192.168.31.127");
        //             Session session = connection.authenticate(new AuthenticationContext("anlong.jiang", "Xgimi000000".toCharArray(), ""));
        //             DiskShare diskShare= (DiskShare) session.connectShare("F");
        //             com.hierynomus.smbj.share.File smbInFile=diskShare.openFile("DSC_8450.JPG",
        //                     EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL,
        //                     SMB2CreateDisposition.FILE_OPEN, null);
        //
        //             int maxReadSize = diskShare.getTreeConnect().getSession().getConnection().getNegotiatedProtocol().getMaxReadSize();
        //             byte[] buffer = new byte[maxReadSize];
        //             long offset = 0;
        //             long remaining = smbInFile.getFileInformation(FileStandardInformation.class).getEndOfFile();
        //             Log.i("SharedInputStream", "file size=" + remaining);
        //             File localOutFile = new File(getFilesDir() + "DSC_8450.JPG");
        //             if (!localOutFile.exists()) {
        //                 localOutFile.createNewFile();
        //             }
        //             FileOutputStream out = new FileOutputStream(localOutFile);
        //             while(remaining > 0) {
        //                 mMethodAverageTime.start();
        //                 int amount = remaining > buffer.length ? buffer.length : (int)remaining;
        //                 int amountRead = smbInFile.read(buffer, offset, 0, amount);
        //                 allSize+=amountRead;
        //                 Log.i("SharedInputStream", "allSize=" + allSize);
        //                 if (amountRead == -1) {
        //                     remaining = 0;
        //                 } else {
        //                     out.write(buffer, 0, amountRead);
        //                     remaining -= amountRead;
        //                     offset += amountRead;
        //                 }
        //                 mMethodAverageTime.end();
        //             }
        //             Log.i("SharedInputStream", "end");
        //             out.close();
        //             smbInFile.close();
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //         runOnUiThread(new Runnable() {
        //             @Override
        //             public void run() {
        //                 mImageView.setVisibility(View.VISIBLE);
        //                 Glide.with(MainActivity.this).load(new File(getFilesDir() + "DSC_8450.JPG")).into(mImageView);
        //             }
        //         });
        //     }
        // }.start();


        mImageView = findViewById(R.id.picture);
        hostEditText = findViewById(R.id.host);
        userEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.password);
        domainEditText = findViewById(R.id.domain);
        pathText = findViewById(R.id.path);
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        mContentView = findViewById(R.id.content_view);
        mContentView.setLayoutManager(new LinearLayoutManager(this));
        mFileAdapter = new FileAdapter(R.layout.file_item_layout);
        mFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SharedFile fileInfo = currentFileList.get(position);
                if (fileInfo.getDiskShare() == null) {
                    loadSharedDisk(fileInfo.getName());
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (fileInfo.isDirectory()) {
                                loadFileList(fileInfo);
                            } else {
                                HttpBean.setmName(mSharedConnection.getAuthenticationContext().getUsername());
                                HttpBean.setmPassword(
                                        String.valueOf(mSharedConnection.getAuthenticationContext().getPassword()));
                                if (mNanoHTTPD == null) {
                                    mNanoHTTPD = NanoStreamer.INSTANCE();
                                    try {
                                        mNanoHTTPD.start();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (FileCategoryUtils.isPictureFile(
                                        FileCategoryUtils.getExtensionName(fileInfo.getName()))) {
                                    // showPicture(fileInfo);
                                    ArrayList<String> pathList = new ArrayList<>();
                                    pathList.add(SmbTools.convertToHttpUrl(fileInfo.getSmbPath(), SmbHelper.DEFAULT_IP,
                                            SmbHelper.DEFAULT_SERVER_PORT));
                                    MediaOpenUtils.playPictureList(MainActivity.this, pathList, 0);
                                    // Intent it=new Intent(Intent.ACTION_VIEW);
                                    // it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    // it.setDataAndType(Uri.parse(SmbTools.convertToHttpUrl(fileInfo.getSmbPath(), SmbHelper.DEFAULT_IP,
                                    //                 SmbHelper.DEFAULT_SERVER_PORT)),"image/*");
                                    // startActivityForResult(it,102);//以识别编号来启动外部程序
                                } else if (FileCategoryUtils.isVideoFile(
                                        FileCategoryUtils.getExtensionName(fileInfo.getName()))) {
                                    ArrayList<String> pathList = new ArrayList<>();
                                    pathList.add(SmbTools.convertToHttpUrl(fileInfo.getSmbPath(), SmbHelper
        .DEFAULT_IP,
                                            SmbHelper.DEFAULT_SERVER_PORT));
                                    ArrayList<String> nameList = new ArrayList<>();
                                    nameList.add(fileInfo.getName());
                                    MediaOpenUtils.playVideoList(MainActivity.this, pathList, nameList, 0);
                                    // Intent it=new Intent(Intent.ACTION_VIEW);
                                    // it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    // it.setDataAndType(Uri.parse(SmbTools.convertToHttpUrl(fileInfo.getSmbPath(), SmbHelper.DEFAULT_IP,
                                    //         SmbHelper.DEFAULT_SERVER_PORT)),"video/*");
                                    // startActivityForResult(it,102);//以识别编号来启动外部程序
                                }
                            }
                        }
                    }.start();
                }
            }
        });
        mContentView.setAdapter(mFileAdapter);
    }

    private void showPicture(SharedFile file) {
        try {
            InputStream fis = file.getInputStream();
            File srcFile = new File(getFilesDir() + file.getName());
            if (!srcFile.exists()) {
                srcFile.createNewFile();
            }
            int n;
            FileOutputStream fos = new FileOutputStream(srcFile);
            byte[] bs = new byte[1024 * 1024];

            while ((n = fis.read(bs)) != -1) {
                fos.write(bs, 0, n);
            }

            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.setVisibility(View.VISIBLE);
                Glide.with(MainActivity.this).load(new File(getFilesDir() + file.getName())).into(mImageView);
            }
        });
    }

    @Override
    public void onClick(View v) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String host = hostEditText.getText().toString();
                if ("".equals(host)) {
                    connectDevice("192.168.43.166", "anlong.jiang", "Xgimi000000", "");
                } else {
                    connectDevice(host, userEditText.getText().toString(), passwordEditText.getText().toString(),
                            domainEditText.getText().toString());
                }
            }
        }.start();
    }


    private void connectDevice(String hostName, String userName, String password, String domain) {
        try {
            mSharedConnection = new SharedConnection(hostName, userName, password, domain);
            List<String> sharedNames = mSharedConnection.getSharedNameList();
            List<SharedFile> fileInfoList = new ArrayList<>();
            for (String name : sharedNames) {
                System.out.println(name);
                SharedFile fileInfo = new SharedFile(mSharedConnection, null, name);
                fileInfoList.add(fileInfo);
            }
            sharedDiskList.addAll(fileInfoList);
            showFileList("", fileInfoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFileList(final String path, final List<SharedFile> fileInfoList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFileAdapter.setNewData(fileInfoList);
                mContentView.scrollToPosition(0);
                currentFileList = fileInfoList;
                pathText.setText(path);
            }
        });
    }

    private void loadSharedDisk(String name) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    currentSharedDisk = new SharedDisk(mSharedConnection, name);
                    mCurrentSharedFile = currentSharedDisk;
                    showFileList(currentSharedDisk.getSmbPath(), currentSharedDisk.getFileList());
                } catch (SMBApiException e) {
                    showError(e.getStatus());
                }

            }
        }.start();

    }

    private void loadFileList(SharedFile fileInfo) {
        mCurrentSharedFile = fileInfo;
        new Thread() {
            @Override
            public void run() {
                super.run();
                showFileList(fileInfo.getSmbPath(), fileInfo.getFileList());
            }
        }.start();
    }

    private void showError(final NtStatus code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (NtStatus.STATUS_LOGON_FAILURE.equals(code)) {
                    Toast.makeText(MainActivity.this, "没有访问权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread() {
            @Override
            public void run() {
                super.run();
                mSharedConnection.close();
                mNanoHTTPD.stop();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (View.VISIBLE == mImageView.getVisibility()) {
            mImageView.setVisibility(View.GONE);
            return;
        }
        if (mCurrentSharedFile != null) {
            SharedItem sharedFile = mCurrentSharedFile.getParentPath();
            if (sharedFile == null) {
                if (mCurrentSharedFile == currentSharedDisk) {
                    showFileList("", sharedDiskList);
                    mCurrentSharedFile = null;
                } else {
                    showFileList(currentSharedDisk.getSmbPath(), currentSharedDisk.getFileList());
                    mCurrentSharedFile = currentSharedDisk;
                }
            } else {
                loadFileList((SharedFile) sharedFile);
            }
            return;
        }
        super.onBackPressed();
    }
}
