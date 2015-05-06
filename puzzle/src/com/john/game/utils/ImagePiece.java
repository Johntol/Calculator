package com.john.game.utils;

import android.graphics.Bitmap;
/**
 * ´æ´¢Í¼Æ¬µÄÃ¿Ò»¿é
 * @author John
 *
 */
public class ImagePiece {
	
	private int index;     //Í¼Æ¬¿éË÷Òý
	private Bitmap bitmap; //µ±Ç°Í¼Æ¬
	
	public ImagePiece(){
		
	}

	public ImagePiece(int index, Bitmap bitmap) {
		super();
		this.index = index;
		this.bitmap = bitmap;
	}

	
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public String toString() {
		return "ImagePiece [index=" + index + ", bitmap=" + bitmap + "]";
	}
	
	
	
}
