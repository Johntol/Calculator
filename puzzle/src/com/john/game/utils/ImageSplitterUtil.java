package com.john.game.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;


/**
 * ��ͼ������
 * @author John
 *
 */
public class ImageSplitterUtil {
	/**
	 * 
	 * @param bitmap  ����ͼƬ
	 * @param piece   ��Ϊpiece*piece��
	 * @return        List<ImagePiece>
	 */
	public static List<ImagePiece> splitImage(Bitmap bitmap, int piece){
		
		List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
		
		int width  = bitmap.getWidth();//ͼƬ���
		int height = bitmap.getHeight();//ͼƬ�߶�
		
		int pieceWidth = Math.min(width, height)/piece;//ÿһ��Ŀ��  
		
	
		for(int i = 0;i< piece;i ++){
			for(int j = 0;j<piece; j++){
				ImagePiece imagePiece = new ImagePiece();
				
				//����ÿһ�������
				imagePiece.setIndex(j + i * piece);
				
				//��ͼ
				int x = j * pieceWidth;
				int y = i * pieceWidth;
				
				imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
				
				imagePieces.add(imagePiece);
			}
		}

		
		return imagePieces;
	}
}
