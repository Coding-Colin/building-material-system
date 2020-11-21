package com.contract.system.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


public class ImageUtil {


	// 读取图片成rgb二维数组
	public static Map<String, Object> img2Matrix(String imgfile) {
		File file = new File(imgfile);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		System.out.println("width:" + width);
		System.out.println("height:" + height);
		System.out.println("minx:" + minx);
		System.out.println("miny:" + miny);
		Color[][] allcolor = new Color[height - miny][width - minx];
		for (int j = miny; j < height; j++) {
			for (int i = minx; i < width; i++) {
				int pixel = bi.getRGB(i, j); //
				// 下面三行代码将一个数字转换为RGB数字
				allcolor[j - miny][i - minx] = new Color(pixel);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("matrix", allcolor);
		map.put("width", width - minx);
		map.put("height", height - miny);
		return map;
	}

	public static void writeImageFromArray(String imageFile, String type,
			int[][] rgbArray) {
		// 获取数组宽度和高度
		int width = rgbArray[0].length;
		int height = rgbArray.length;
		// 将二维数组转换为一维数组
		int[] data = new int[width * height];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				data[i * width + j] = rgbArray[i][j];
		// 将数据写入BufferedImage
		BufferedImage bf = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_BGR);
		bf.setRGB(0, 0, width, height, data, 0, width);
		// 输出图片
		try {
			File file = new File(imageFile);
			ImageIO.write(bf, type, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int[] convertARGBToRGB(int ARGB) {
		int[] rgb = new int[3];
		rgb[0] = (ARGB & 0xff0000) >> 16;
		rgb[1] = (ARGB & 0xff00) >> 8;
		rgb[2] = (ARGB & 0xff);
		return rgb;
	}

	public static int convertRGBToARGB(int r, int g, int b) {
		int color = ((0xFF << 24) | (r << 16) | (g << 8) | b);
		return color;
	}

	public static int[][] removeFog(Map<String, Object> map) {

		// 1.求暗通道图
		Color[][] img = (Color[][]) map.get("matrix");
		int height = (Integer) map.get("height");
		int width = (Integer) map.get("width");
		int k1 = height;
		int k2 = width;
		int k = 11; // 5*5的邻域
		int i, j, r, c, x;
		// Mat src(k1,k2,CV_8UC1);
		int[][] src = new int[k1][k2];
		Color color = null;
		for (r = 0; r < k1; r++) { // 先求出每个像素RGB分量中的最小值
			for (c = 0; c < k2; c++) {
				x = 255;
				color = img[r][c];
				if (color.getRed() < x)
					x = color.getRed();
				if (color.getGreen() < x)
					x = color.getGreen();
				if (color.getBlue() < x)
					x = color.getBlue();
				src[r][c] = x;
			}
		}
		int[][] src1 = new int[k1][k2];
		for (r = 0; r < k1; r++) { // 最小值滤波
			for (c = 0; c < k2; c++) {
				x = 255;
				for (i = -k / 2; i <= k / 2; i++) {
					for (j = -k / 2; j <= k / 2; j++) {
						if (r + i < 0 || c + j < 0 || r + i >= k1
								|| c + j >= k2)
							continue;
						if (src[r + i][c + j] < x)
							x = src[r + i][c + j];
					}
				}
				src1[r][c] = x;
			}
		}
		int[][] src2 = src1;
		// 2.计算大气光值A
		int Am = 2200; // 设定大气光值最大值
		int r1 = 0, c1 = 0;
		int a[] = { 0, 0, 0 };
		int aa[][] = new int[100][2]; // 用以记录暗通道图中亮度最大的100个像素的坐标
		// 从暗通道图中按照亮度的大小取前100的像素
		for (i = 0; i < 100; i++) {
			x = 0;
			for (r = 0; r < k1; r++) {
				for (c = 0; c < k2; c++) {
					if (src2[r][c] > x) {
						x = src2[r][c];
						r1 = r;
						c1 = c;
					}
				}
			}
			aa[i][0] = r1;
			aa[i][1] = c1;
			src2[r1][c1] = 0;
		}
		// 在这前100像素中，在原始有雾图像I中寻找对应的具有最高亮度的点的值，作为A值。
		for (i = 0; i < 100; i++) {
			color = img[aa[i][0]][aa[i][1]];
			if (color.getBlue() > a[2]) {
				a[2] = color.getBlue();
			}
			if (color.getGreen() > a[1]) {
				a[1] = color.getGreen();
			}
			if (color.getRed() > a[0]) {
				a[0] = color.getRed();
			}
		}
		// 若A超出阈值
		for (i = 0; i < 3; i++) {
			if (a[i] > Am)
				a[i] = Am;
		}

		// 计算t值
		double dst[][] = new double[k1][k2];
		double t[][] = new double[k1][k2];
		double w = 0.95;// 修正因子
		double t0 = 0.2;// t的最小阈值
		double y;
		for (r = 0; r < k1; r++) {
			for (c = 0; c < k2; c++) {
				y = 255;
				color = img[r][c];
				if (color.getRed() * 1.0 / a[0] < y)
					y = color.getRed() * 1.0 / a[0];
				if (color.getGreen() * 1.0 / a[1] < y)
					y = color.getGreen() * 1.0 / a[1];
				if (color.getBlue() * 1.0 / a[2] < y)
					y = color.getBlue() * 1.0 / a[2];
				dst[r][c] = y;
			}
		}
		for (r = 0; r < k1; r++) {
			for (c = 0; c < k2; c++) {
				y = 255;
				for (i = -k / 2; i <= k / 2; i++) {
					for (j = -k / 2; j <= k / 2; j++) {
						if (r + i < 0 || c + j < 0 || r + i >= k1
								|| c + j >= k2)
							continue;

						if (dst[r + i][c + j] < y)
							y = dst[r + i][c + j];
					}
				}
				dst[r][c] = y;
				t[r][c] = 1 - y;
			}
		}
		for (r = 0; r < k1; r++) {
			for (c = 0; c < k2; c++) {
				if (t[r][c] < t0)
					t[r][c] = t0;
			}
		}

		int[][] img2 = new int[k1][k2];
		for (r = 0; r < k1; r++) {
			for (c = 0; c < k2; c++) {
				Color cr = img[r][c];
				int rr = ((Double) ((cr.getRed() - a[0]) / t[r][c] + a[0]))
						.intValue();
				int gg = ((Double) ((cr.getGreen() - a[1]) / t[r][c] + a[1]))
						.intValue();
				int bb = ((Double) ((cr.getBlue() - a[2]) / t[r][c] + a[2]))
						.intValue();
				img2[r][c] = convertRGBToARGB(rr, gg, bb);
			}
		}
		return img2;
	}

	// ////////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws SocketException, IOException {
		Map<String, Object> map = img2Matrix("/Users/hujinghan/Desktop/11.png");
		writeImageFromArray("/Users/hujinghan/Desktop/22.png", "png", removeFog(map));
	}
}
