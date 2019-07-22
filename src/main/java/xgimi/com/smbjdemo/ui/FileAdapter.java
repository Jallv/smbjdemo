package xgimi.com.smbjdemo.ui;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import xgimi.com.smbjdemo.R;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareFile;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareItem;


/**
 * @author anlong.jiang
 * @date on 2019/7/4
 * @describe TODO
 */
public class FileAdapter extends BaseQuickAdapter<ShareItem, FileAdapter.FileBaseViewHolder> {
    public FileAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(FileBaseViewHolder helper, ShareItem item) {
        helper.setText(R.id.name, item.getName());
    }

    static class FileBaseViewHolder extends BaseViewHolder {

        public FileBaseViewHolder(View view) {
            super(view);
        }
    }
}
