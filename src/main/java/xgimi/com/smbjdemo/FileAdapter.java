package xgimi.com.smbjdemo;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import xgimi.com.smbjdemo.smbjwrapper.SharedFile;
import xgimi.com.smbjdemo.smbjwrapper.core.SharedItem;


/**
 * @author anlong.jiang
 * @date on 2019/7/4
 * @describe TODO
 */
public class FileAdapter extends BaseQuickAdapter<SharedFile, FileAdapter.FileBaseViewHolder> {
    public FileAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(FileBaseViewHolder helper, SharedFile item) {
        helper.setText(R.id.name, item.getName());
    }

    static class FileBaseViewHolder extends BaseViewHolder {

        public FileBaseViewHolder(View view) {
            super(view);
        }
    }
}
