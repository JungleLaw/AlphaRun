package cn.alpha.imageloader.i;

import java.io.File;

/**
 * 文件获取借口
 * Created by Administrator on 2017/5/3.
 */
public interface FileGetter {
    /**
     *成功
     */
    void onSuccess(File file);

    /**
     * 失败
     */
    void onFail();

}
