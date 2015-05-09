/** 创建一个矩形 n为大小 color为正常的color2为不同的*/
function Rect(n,color,color2){
	createjs.Shape.call(this);
	this.setRectType = function(type){
		this._RectType = type;
		switch (type){
			case 1:
				this.setColor(color);
				break;
			case 2:
				this.setColor(color2);
				break;
		}
	}
	
	//绘制颜色 、大小
	this.setColor = function(colorString){
		this.graphics.beginFill(colorString);
		this.graphics.drawRect(0,0,400/n-5,400/n-5);
		this.graphics.endFill();
	}
	
	//得到矩形的类型
	this.getRectType = function(){
		return this._RectType;
	}
	this.setRectType(1);
}
Rect.prototype = new createjs.Shape();
