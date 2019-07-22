
package xgimi.com.smbjdemo.model;


import java.util.ArrayList;
import java.util.List;

import xgimi.com.smbjdemo.model.bean.FileCategory;
import xgimi.com.smbjdemo.smbjwrapper.utils.ShareUtils;

public class FileCategoryUtils {

    public static List<String> videoTypeList = new ArrayList<String>();
    public static List<String> audioTypeList = new ArrayList<String>();
    public static List<String> officialTypeList = new ArrayList<String>();
    public static List<String> apkTypeList = new ArrayList<String>();
    public static List<String> pictureTypeList = new ArrayList<String>();

    public static List<String> compressedList = new ArrayList<String>();
    public static List<String> textList = new ArrayList<String>();

    static {

        videoTypeList.add(".avi");
        videoTypeList.add(".asf");
        videoTypeList.add(".wmv");
        videoTypeList.add(".m2t");
        videoTypeList.add(".mts");
        videoTypeList.add(".ts");
        videoTypeList.add(".mpg");
        videoTypeList.add(".m2p");
        videoTypeList.add(".mp4");
        videoTypeList.add(".flv");
        videoTypeList.add(".swf");
        videoTypeList.add(".vob");
        videoTypeList.add(".mkv");
        videoTypeList.add(".divx");
        videoTypeList.add(".xvid");
        videoTypeList.add(".mov");
        videoTypeList.add(".rmvb");
        videoTypeList.add(".rv");
        videoTypeList.add(".3gp");
        videoTypeList.add(".3g2");
        videoTypeList.add(".pmp");
        videoTypeList.add(".tp");
        videoTypeList.add(".trp");
        videoTypeList.add(".rm");
        videoTypeList.add(".webm");
        videoTypeList.add(".m2ts");
        videoTypeList.add(".ssif");
        videoTypeList.add(".mpeg");
        videoTypeList.add(".mpe");
        videoTypeList.add(".m3u8");
        videoTypeList.add(".m4v");
        videoTypeList.add(".xv");


        audioTypeList.add(".mp3");
        audioTypeList.add(".wma");
        audioTypeList.add(".aac");
        audioTypeList.add(".ogg");
        audioTypeList.add(".pcm");
        audioTypeList.add(".m4a");
        audioTypeList.add(".ac3");
        /*audioTypeList.add(".ec3");
    	audioTypeList.add(".dtshd");
    	audioTypeList.add(".ra");*/
        audioTypeList.add(".wav");
    	/*audioTypeList.add(".cd");
    	audioTypeList.add(".amr");
    	audioTypeList.add(".mp2");*/
//		audioTypeList.add(".ape");
    	/*audioTypeList.add(".dts");*/
        audioTypeList.add(".flac");
        audioTypeList.add(".midi");
        audioTypeList.add(".mid");
    	
    	/*pictureTypeList.add(".jpe");*/
        pictureTypeList.add(".jpeg");
        pictureTypeList.add(".jpg");
        pictureTypeList.add(".png");
        pictureTypeList.add(".bmp");
        pictureTypeList.add(".gif");
        pictureTypeList.add(".webp");
//		pictureTypeList.add(".tiff");
//		pictureTypeList.add(".tif");
    	/*pictureTypeList.add(".tiff");
    	pictureTypeList.add(".tga");*/
    	
    	/*pictureTypeList.add(".exif");
    	pictureTypeList.add(".fpx");*/
    	/*pictureTypeList.add(".svg");*/
    	/*pictureTypeList.add(".psd");
    	pictureTypeList.add(".cdr");
    	pictureTypeList.add(".pcd");
    	pictureTypeList.add(".dxf");
    	pictureTypeList.add(".ufo");
    	pictureTypeList.add(".eps");
    	pictureTypeList.add(".raw");
    	pictureTypeList.add(".mpo");*/

        officialTypeList.add(".pdf");
        officialTypeList.add(".txt");
        officialTypeList.add(".xls");
        officialTypeList.add(".doc");
        officialTypeList.add(".ppt");
        officialTypeList.add(".pptx");
        officialTypeList.add(".docx");
        officialTypeList.add(".xlsx");
    	/*officialTypeList.add(".jar");*/
    	/*officialTypeList.add(".zip");
    	officialTypeList.add(".rar");
    	officialTypeList.add(".gz");*/
    	/*officialTypeList.add(".iso");*/
    	/*officialTypeList.add(".tar");
    	officialTypeList.add(".7z");*/
    /*	officialTypeList.add(".htm");
    	officialTypeList.add(".html");
    	officialTypeList.add(".php");
    	officialTypeList.add(".css");
    	officialTypeList.add(".xml");
    	officialTypeList.add(".js");
    	officialTypeList.add(".h");
    	officialTypeList.add(".c");
    	officialTypeList.add(".cpp");*/

        apkTypeList.add(".apk");
        //压缩文件
        compressedList.add(".iso");
    	/*compressedList.add(".jar");*/
        compressedList.add(".tar");
        compressedList.add(".gz");
        compressedList.add(".zip");
        compressedList.add(".rar");
        //文本文件
        textList.add(".txt");
    	/*textList.add(".xml");
    	textList.add(".htm");
    	textList.add(".html");
    	textList.add(".php");
    	textList.add(".css");
    	textList.add(".js");
    	textList.add(".h");
    	textList.add(".c");
    	textList.add(".cpp");*/

    }

    public static boolean isAudioFile(String exts) {
        if (exts != null && exts != "") {
            return audioTypeList.contains(exts);
        }
        return false;
    }

    public static boolean isVideoFile(String exts) {
        if (exts != null && exts != "") {
            return videoTypeList.contains(exts);
        }
        return false;
    }

    public static boolean isApkFile(String exts) {
        if (exts != null && exts != "") {
            return apkTypeList.contains(exts);
        }
        return false;
    }

    public static boolean isPictureFile(String exts) {
        if (exts != null && exts != "") {
            return pictureTypeList.contains(exts);
        }
        return false;
    }

    public static boolean isOfficialFile(String exts) {
        if (exts != null && exts != "") {
            return officialTypeList.contains(exts);
        }
        return false;
    }

    public static int getTypeByNameOrPath(String nameOrPath) {
        String extension = getExtensionName(nameOrPath);
        if (ShareUtils.isEmpty(extension))
            return FileCategory.Other.ordinal();
        if (FileCategoryUtils.isAudioFile(extension)) {
            return FileCategory.Music.ordinal();
        } else if (FileCategoryUtils.isPictureFile(extension)) {
            return FileCategory.Picture.ordinal();
        } else if (FileCategoryUtils.isVideoFile(extension)) {
            return FileCategory.Video.ordinal();
        } else if (FileCategoryUtils.isApkFile(extension)) {
            return FileCategory.Apk.ordinal();
        } else if (FileCategoryUtils.isOfficialFile(extension)) {
            return FileCategory.Document.ordinal();
        } else {
            return FileCategory.Other.ordinal();
        }
    }
    private static final int DOC_PDF = 5;
    private static final int DOC_XLSX = 4;
    private static final int DOC_DOCX = 3;
    private static final int DOC_PPT = 2;
    private static final int DOC_TXT = 1;
    private static final int DOC_OTHER = 0;

    public static int getDocType(String path) {
        if (!ShareUtils.isEmpty(path)) {
            String ext = getExtensionName(path);
            if (!ShareUtils.isEmpty(ext)) {
                if (".pdf".equals(ext))
                    return DOC_PDF;
                else if (".xlsx".equals(ext) || ".xls".equals(ext))
                    return DOC_XLSX;
                else if (".doc".equals(ext) || ".docx".equals(ext))
                    return DOC_DOCX;
                else if (".ppt".equals(ext) || ".pptx".equals(ext))
                    return DOC_PPT;
                else if(".txt".equals(ext))
                    return DOC_TXT;
            }
        }
        return DOC_OTHER;
    }

    /**
     * 获取扩展名
     *
     * @param path
     * @return
     */
    public static String getExtensionName(String path) {
        int pos = path.lastIndexOf(".");
        if (pos > 0)
            return path.toLowerCase().substring(pos);
        return null;
    }
}
