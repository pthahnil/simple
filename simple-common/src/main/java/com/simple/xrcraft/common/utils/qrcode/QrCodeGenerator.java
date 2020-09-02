package com.simple.xrcraft.common.utils.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthahnil on 2019/4/4.
 */
@Slf4j
public class QrCodeGenerator {

	/**
	 * 生成带logo的二维码
	 * @param qrCodeContent 二维码内容
	 * @param logo logo图片
	 * @param width logo 的宽度，暂时宽高一致
	 * @return
	 */
	public static BufferedImage genQrCodeWithLogo(String qrCodeContent, BufferedImage logo, int width) {
		try {
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			BitMatrix matrix = multiFormatWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, width, width, getHints());

			BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);

			int logoWidth = width * 1 / 5;
			int logoHeight = width * 1 / 5;

			BufferedImage logoBuffer = scale(logo, logoWidth, logoHeight);

			if(null != logoBuffer){
				int x1 = width/2 - logoBuffer.getWidth()/2;
				int x2 = width/2 + logoBuffer.getWidth()/2;
				int y1 = width/2 - logoBuffer.getHeight()/2;
				int y2 = width/2 + logoBuffer.getHeight()/2;

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < width; y++) {

						boolean inRange = x > x1 && x < x2 && y > y1 && y < y2;

						int color;
						if(inRange){
							//logo范围内，取logo颜色
							color = logoBuffer.getRGB(x - x1, y - y1);
						} else {
							//取二维码的颜色
							color = matrix.get(x, y) ? Color.black.getRGB() : Color.white.getRGB();
						}
						image.setRGB(x, y, color);
					}
				}
			} else {
				image = MatrixToImageWriter.toBufferedImage(matrix);
			}
			return image;
		} catch (Exception e) {
			log.error("二维码生成异常{}", e);
		}
		return null;
	}

	/**
	 * 第一张图片前叠加一张小的图片
	 * 中心
	 * @param bkImg
	 * @param ftImg
	 * @return
	 */
	public static BufferedImage mergeImg(BufferedImage bkImg, BufferedImage ftImg, int width, int height, int xOffset, int yOffset) {
		try {
			//读取二维码图片，并构建绘图对象
			Graphics2D graphics = bkImg.createGraphics();

			//开始绘制图片
			graphics.drawImage(ftImg, xOffset, yOffset, width, height, null);
			graphics.dispose();

			ftImg.flush();
			bkImg.flush();

		} catch (Exception e) {
			log.error("图片合并异常{}", e);
		}
		return bkImg;
	}

	/**
	 * 缩放， 有添加白底
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

				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
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

	/**
	 * 图片延长，底部印上文字
	 * @param image
	 * @param qrCodeName
	 * @return
	 */
	public static BufferedImage addQrCodeName(BufferedImage image, String qrCodeName, int width){
		if (StringUtils.isNotBlank(qrCodeName)) {

			String[] nameSegnaments = qrCodeName.split(";");

			//文字高度
			int lineHeight = 43;
			int accLintHeight = 0;
			//行间距高度
			int lineBlankHeight = 10;
			int acclineBlankHeight = 0;
			//字体大小
			int fontSize = 25;

			Font font = new Font("宋体", Font.BOLD, fontSize);
			for (String segnament : nameSegnaments) {
				if(StringUtils.isNotBlank(segnament)){
					accLintHeight += lineHeight;

					int height = width + accLintHeight;
					//新的图片，把带logo的二维码下面加上文字
					BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

					Graphics2D outg = outImage.createGraphics();
					//画二维码到新的面板
					outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
					//画文字到新的面板
					outg.setColor(Color.BLACK);
					outg.setFont(font); //字体、字型、字号

					int xoffSet = outg.getFontMetrics().stringWidth(segnament);
					int lineBlank = 0;
					if(acclineBlankHeight == 0){
						lineBlank = 10;
					}
					int yoffSet = image.getHeight() + (outImage.getHeight() - image.getHeight()) / 2 + lineBlank;

					//写文字
					outg.drawString(segnament, (width - xoffSet) / 2, yoffSet);

					outg.dispose();
					outImage.flush();
					image = outImage;
					acclineBlankHeight += lineBlankHeight;
				}
			}
		}
		return image;
	}

	/**
	 *  图片不延长，底部印上文字
	 * @param image
	 * @param line
	 * @return
	 */
	public static BufferedImage addLine(BufferedImage image, String line){
		if (StringUtils.isNotBlank(line)) {

			String[] nameSegnaments = line.split(";");

			//字体大小
			int fontSize = 25;
			int imgWidth = image.getWidth();

			//距离底部累计高度
			int accuHeight = -15;
			Font font = new Font("宋体", Font.BOLD, fontSize);
			for (int i = nameSegnaments.length - 1 ; i >= 0; i--) {
				String segnament = nameSegnaments[i];
				if(StringUtils.isNotBlank(segnament)){

					BufferedImage outImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

					Graphics2D outg = outImage.createGraphics();
					//画二维码到新的面板
					outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
					//画文字到新的面板
					outg.setColor(Color.BLACK);
					outg.setFont(font); //字体、字型、字号

					int xoffSet = outg.getFontMetrics().stringWidth(segnament);
					int lineHeight = outg.getFontMetrics().getHeight();

					accuHeight += lineHeight;
					int yoffSet = image.getHeight() - accuHeight;

					//写文字
					outg.drawString(segnament, (imgWidth - xoffSet) / 2, yoffSet);

					outg.dispose();
					outImage.flush();
					image = outImage;
				}
			}
		}
		return image;
	}

	/**
	 * 设置二维码的格式参数
	 *
	 * @return
	 */
	private static Map<EncodeHintType, Object> getHints() {
		// 用于设置QR二维码参数
		Map<EncodeHintType, Object> hints = new HashMap<>();
		// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 设置编码方式
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 0);

		return hints;
	}

}