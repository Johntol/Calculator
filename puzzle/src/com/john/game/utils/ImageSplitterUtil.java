package com.john.game.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;


/**
 * 切图工具类
 * @author John
 *
 */
public class ImageSplitterUtil {
	/**
	 * 
	 * @param bitmap  完整图片
	 * @param piece   切为piece*piece块
	 * @return        List<ImagePiece>
	 */
	public static List<ImagePiece> splitImage(Bitmap bitmap, int piece){
		
		List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
		
		int width  = bitmap.getWidth();//图片宽度
		int height = bitmap.getHeight();//图片高度
		
		int pieceWidth = Math.min(width, height)/piece;//每一块的宽度  
		
	
		for(int i = 0;i< piece;i ++){
			for(int j = 0;j<piece; j++){
				ImagePiece imagePiece = new ImagePiece();
				
				//设置每一块的索引
				imagePiece.setIndex(j + i * piece);
				
				//切图
				int x = j * pieceWidth;
				int y = i * pieceWidth;
				
				imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
				
				imagePieces.add(imagePiece);
			}
		}

		
		return imagePieces;
	}
}
