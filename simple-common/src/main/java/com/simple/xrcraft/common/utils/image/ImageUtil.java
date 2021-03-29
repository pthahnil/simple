package com.simple.xrcraft.common.utils.image;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * @description:
 * @author pthahnil
 * @date 2021/3/29 17:43
 */
@Slf4j
public class ImageUtil {

	/**
	 * 图片旋转
	 * @param image
	 * @param angle
	 * @return
	 */
	public static BufferedImage rotate(BufferedImage image, double angle) {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		return rotate(image, angle, gc);
	}

	/**
	 * 图片旋转
	 * @param image
	 * @param angle
	 * @param gc
	 * @return
	 */
	public static BufferedImage rotate(BufferedImage image, double angle,
			GraphicsConfiguration gc) {
		double sin = Math.abs(Math.sin(angle));
		double cos = Math.abs(Math.cos(angle));

		int w = image.getWidth();
		int h = image.getHeight();

		int neww = (int) Math.floor(w * cos + h * sin);
		int newh = (int) Math.floor(h * cos + w * sin);

		int transparency = image.getColorModel().getTransparency();
		BufferedImage result = gc.createCompatibleImage(neww, newh, transparency);

		Graphics2D g = result.createGraphics();
		g.translate((neww - w) / 2, (newh - h) / 2);
		g.rotate(angle, w / 2, h / 2);
		g.drawRenderedImage(image, null);

		return result;
	}

	/**
	 * 图片缩放
	 * @param srcImage
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scale(BufferedImage srcImage, int width, int height){

		double ratio; // 缩放比例

		int frameWidth = 10;
		int picWidth = width - frameWidth;
		int picHeight = height - frameWidth;
		try {
			if(null != srcImage){

				double xRatio = (new Integer(picWidth)).doubleValue() / srcImage.getWidth();
				double yRatio = (new Integer(picHeight)).doubleValue() / srcImage.getHeight();

				//扩大或者缩小，比例都取小的
				ratio = xRatio > yRatio ? yRatio : xRatio;

				AffineTransformOp
						op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
				Image destImage = op.filter(srcImage, null);

				// 补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				Graphics2D graphic = image.createGraphics();
				graphic.setColor(Color.white);
				graphic.fillRect(0, 0, width, height);
				graphic.drawImage(destImage, frameWidth / 2, frameWidth / 2, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);

				graphic.dispose();
				destImage = image;

				return (BufferedImage) destImage;
			}
		} catch (Exception e){
			log.error("图片缩放异常异常{}", e);
		}
		return null;
	}
}
