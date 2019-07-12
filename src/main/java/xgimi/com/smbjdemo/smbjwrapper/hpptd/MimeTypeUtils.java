package xgimi.com.smbjdemo.smbjwrapper.hpptd;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import xgimi.com.smbjdemo.smbjwrapper.utils.ShareUtils;

public class MimeTypeUtils {
	public static final String[] VIDEO_EXTENSIONS = { "3gp", "asf", "avi",
			"dat", "f4v", "flv", "m2t", "m2ts", "m4v", "mts", "mpeg", "mpg", 
			"mkv", "mov", "mp4", "mts", "rm", "rmvb", "swf", "tp", "trp", "ts", 
			"vob", "wmv"};

	public static final String[] AUDIO_EXTENSIONS = { "aac", "ac3", "ape", 
		"dts", "flac", "m4a", "mid", "midi", "mp3", "wav", "wma", "ogg"};

	public static final String IMAGE_EXTENSIONS[] = { "png", "gif", "jpg",
			"jpeg", "bmp", "webp"};

	public static final String FILES_EXTENSIONS[] = { "chm",
			"doc", "docx", "iso", "lrc", "mht",
			"pdf", "ppt", "pptx", "rar", "srt",
			"txt", "vsd", "vsdx", "xls", "xlsx", "zip" };

	public static final HashSet<String> mHashVideo;
	public static final HashSet<String> mHashAudio;
	public static final HashSet<String> mHashImage;
	public static final HashSet<String> mHashFiles;

	static {
		mHashVideo = new HashSet<String>(Arrays.asList(VIDEO_EXTENSIONS));
		mHashAudio = new HashSet<String>(Arrays.asList(AUDIO_EXTENSIONS));
		mHashImage = new HashSet<String>(Arrays.asList(IMAGE_EXTENSIONS));
		mHashFiles = new HashSet<String>(Arrays.asList(FILES_EXTENSIONS));
	}

	public static String getFileExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase(
						Locale.getDefault());
			}
		}
		return null;
	}

	public static String getUrlExtension(String url) {
		if (!ShareUtils.isEmpty(url)) {
			int i = url.lastIndexOf('.');
			if (i > 0 && i < url.length() - 1) {
				return url.substring(i + 1).toLowerCase(Locale.getDefault());
			}
		}
		return "";
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	private static HashMap<String, String> sMimeTypeMap = new HashMap<String, String>();

	private static void addFileType(String extension, String mimeType) {
		sMimeTypeMap.put(extension, mimeType);
	}

	static {
		addFileType("3gp", "video/3gpp");
		addFileType("asf", "video/x-ms-asf");
		addFileType("avi", "video/x-divx");
		addFileType("dat", "video/dvd");
		addFileType("f4v", "video/f4v");
		addFileType("flv", "video/x-flv");
		addFileType("m2t", "video/m2t");
		addFileType("m2ts", "video/m2ts");
		addFileType("m4v", "video/mp4");
		addFileType("mpeg", "video/mpeg");
		addFileType("mpg", "video/mpeg");
		addFileType("mkv", "video/x-matroska");
		addFileType("mov", "video/quicktime");
		addFileType("mp4", "video/mp4");
		addFileType("mts", "video/mts");
		addFileType("rm", "video/rm");
		addFileType("rmvb", "video/rmvb");
		addFileType("swf", "video/flash");
		addFileType("tp", "video/mp2ts");
		addFileType("trp", "video/mp2ts");
		addFileType("ts", "video/mp2ts");
		addFileType("vob", "video/dvd");
		addFileType("wmv", "video/x-ms-wmv");
		addFileType("iso", "video/iso");

		addFileType("aac", "audio/aac");
		addFileType("ac3", "audio/ac3");
		addFileType("ape", "audio/ape");
		addFileType("dts", "audio/dts");
		addFileType("flac", "audio/flac");
		addFileType("m4a", "audio/mp4");
		addFileType("mid", "audio/midi");
		addFileType("midi", "audio/midi");
		addFileType("mp3", "audio/mpeg");
		addFileType("wav", "audio/x-wav");
		addFileType("wma", "audio/x-ms-wma");
		addFileType("ogg", "audio/ogg");

		addFileType("png", "image/png");
		addFileType("gif", "image/gif");
		addFileType("jpg", "image/jpeg");
		addFileType("jpeg", "image/jpeg");
		addFileType("bmp", "image/bmp");

		addFileType("apk", "application/vnd.android.package-archive");
		addFileType("chm", "application/mshelp");
		addFileType("doc", "application/msword");
		addFileType("docx",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document");

		addFileType("lrc", "text/plain");
		addFileType("mht", "text/html");
		addFileType("pdf", "application/pdf");
		addFileType("ppt", "application/vnd.ms-powerpoint");

		addFileType("pptx",
				"application/vnd.openxmlformats-officedocument.presentationml.presentation");
		addFileType("rar", "application/x-rar-compressed");
		addFileType("srt", "text/plain");

		addFileType("sub", "text/plain");
		addFileType("txt", "text/plain");
		addFileType("vsd", "application/vnd.visio");
		addFileType("xls", "application/msexcel");
		addFileType("xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		addFileType("zip", "application/zip");
	}

	public static String getMimeType(String url) {
		String extension = url.substring(url.lastIndexOf(".") + 1)
				.toLowerCase(Locale.getDefault());
		if (sMimeTypeMap.containsKey(extension)) {
			return sMimeTypeMap.get(extension);
		} else {
			return "*/*";
		}
	}
}
