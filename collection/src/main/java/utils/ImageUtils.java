package utils;

import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

	public static String getCoverUrl(String coverId, ImageSize size) {
		if (coverId == null || coverId.isEmpty())
			return "";
		return "https://images.igdb.com/igdb/image/upload/t_" + size.getCode() + "/" + coverId + ".jpg";
	}

	public static List<String> getScreenshotUrls(List<String> screenshotIds, ImageSize size) {
		List<String> urls = new ArrayList<>();
		for (String imageId : screenshotIds) {
			urls.add(getCoverUrl(imageId, size));
		}
		return urls;
	}

	public enum ImageSize {
		COVER_SMALL("cover_small"),
		COVER_BIG("cover_big"),
		SCREENSHOT_MEDIUM("screenshot_med"),
		SCREENSHOT_BIG("screenshot_big"),
		SCREENSHOT_HUGE("screenshot_huge"),
		LOGO_MED("logo_med"),
		THUMB("thumb"),
		MICRO("micro");

		private final String code;

		ImageSize(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

}
