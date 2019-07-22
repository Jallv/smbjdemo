package xgimi.com.smbjdemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;
import com.hierynomus.mserref.NtStatus;
import com.hierynomus.mssmb2.SMBApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import xgimi.com.smbjdemo.R;
import xgimi.com.smbjdemo.model.FileCategoryUtils;
import xgimi.com.smbjdemo.model.MediaOpenUtils;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareClient;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareDisk;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareFile;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareItem;
import xgimi.com.smbjdemo.smbjwrapper.core.SmbDevice;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.HttpBean;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.HttpHelper;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.NanoHTTPD;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.NanoStreamer;
import xgimi.com.smbjdemo.smbjwrapper.hpptd.SmbUrlTools;
import xgimi.com.smbjdemo.smbjwrapper.search.SmbSearcher;
import xgimi.com.smbjdemo.smbjwrapper.utils.log.KLog;

/**
 * @author anlong.jiang
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private EditText hostEditText, userEditText, passwordEditText, domainEditText;
    private Button loginButton;
    private TextView pathText;
    private RecyclerView mContentView;
    private FileAdapter mFileAdapter;

    private ShareClient mShareClient;
    private ShareItem mCurrentSharedFile;
    private ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new FifoPriorityThreadPoolExecutor.DefaultThreadFactory());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mFileAdapter.setOnItemClickListener((adapter, view, position) -> onFileClick(
                (ShareItem) adapter.getData().get(position)));
        mContentView.setAdapter(mFileAdapter);

        startSearch();
    }

    private SmbSearcher smbSearcher;

    private void startSearch() {
        smbSearcher = new SmbSearcher(new SmbSearcher.OnSmbSearchListener() {
            @Override
            public void onSearch(int status, SmbDevice device) {
                KLog.i(TAG, "onSearch status=" + status + ",device=" + device);
            }
        });
        smbSearcher.setLogEnable(false);
        smbSearcher.startSearchSmbDevice();
    }

    @Override
    public void onClick(View v) {
        mThreadPoolExecutor.execute(() -> {
            String host = hostEditText.getText().toString();
            connectDevice(host, userEditText.getText().toString(), passwordEditText.getText().toString(),
                    domainEditText.getText().toString());
        });
    }

    private void onFileClick(ShareItem fileInfo) {
        mThreadPoolExecutor.execute(() -> {
            if (fileInfo.isDirectory()) {
                loadFileList(fileInfo);
            } else {
                mShareClient.startNanoStreamer();
                if (FileCategoryUtils.isPictureFile(
                        FileCategoryUtils.getExtensionName(fileInfo.getName()))) {
                    Intent it=new Intent(Intent.ACTION_VIEW);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.setDataAndType(Uri.parse(SmbUrlTools.convertToHttpUrl(fileInfo.getSmbPath(),
                    HttpHelper.DEFAULT_IP,
                                    HttpHelper.DEFAULT_SERVER_PORT)),"image/*");
                    startActivityForResult(it,102);//以识别编号来启动外部程序
                } else if (FileCategoryUtils.isVideoFile(
                        FileCategoryUtils.getExtensionName(fileInfo.getName()))) {
                    Intent it=new Intent(Intent.ACTION_VIEW);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.setDataAndType(Uri.parse(SmbUrlTools.convertToHttpUrl(fileInfo.getSmbPath(),
                    HttpHelper.DEFAULT_IP,
                            HttpHelper.DEFAULT_SERVER_PORT)),"video/*");
                    startActivityForResult(it,102);//以识别编号来启动外部程序
                }
            }
        });
    }

    private void connectDevice(String hostName, String userName, String password, String domain) {
        try {
            mShareClient = new ShareClient(hostName, userName, password, domain);
            mCurrentSharedFile=mShareClient.getRootShareItem();
            showFileList("", mCurrentSharedFile.getFileList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFileList(final String path, final List<ShareItem> fileInfoList) {
        runOnUiThread(() -> {
            mFileAdapter.setNewData(fileInfoList);
            mContentView.scrollToPosition(0);
            pathText.setText(path);
        });
    }

    private void loadFileList(ShareItem fileInfo) {
        mCurrentSharedFile = fileInfo;
        mThreadPoolExecutor.execute(() -> showFileList(fileInfo.getSmbPath(), fileInfo.getFileList()));
    }

    private void showError(final NtStatus code) {
        runOnUiThread(() -> {
            if (NtStatus.STATUS_LOGON_FAILURE.equals(code)) {
                Toast.makeText(MainActivity.this, "没有访问权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smbSearcher != null) {
            smbSearcher.stop();
        }
        mThreadPoolExecutor.execute(() -> {
            try {
                if (mShareClient != null) {
                    mShareClient.closeClient();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onBackPressed() {
        ShareItem sharedFile = mCurrentSharedFile.getParentFile();
        if (sharedFile == null) {
            super.onBackPressed();
        } else {
            loadFileList(sharedFile);
        }
    }
}
