var stage = new createjs.Stage("gameView");
createjs.Ticker.setFPS(30);
createjs.Ticker.addEventListener("tick",stage);

var gameView = new createjs.Container();
stage.addChild(gameView);

/**
 * 增加方块
 */
var n = 2;
function addRect(){
	//随机颜色
	var cl = parseInt(Math.random()*1000000);
	var c2 = parseInt(Math.random()*1000000);
	var color = "#" + cl;
	var color2 = "#" + c2;
	//随机位置
	var x = parseInt(Math.random()*n);
	var y = parseInt(Math.random()*n);
	for(var indexX = 0;indexX < n;indexX++){
		for(var indexY = 0; indexY < n;indexY++){
			var r = new Rect(n,color,color2);
			gameView.addChild(r);
			r.x = indexX;
			r.y = indexY;	
			//放置不同位置的矩形
			if(r.x == x && r.y ==y){
				r.setRectType(2);
			}
			r.x = indexX*(400/n);
			r.y = indexY*(400/n);
			//设置不同矩形的点击事件
			if(r.getRectType() == 2){
				r.addEventListener("click",function(){
					if(n < 7){
						++n;
					}
					//清空之前的背景
					gameView.removeAllChildren();
					//下一关
					addRect();
				})
			}
		}
		
	}
}
addRect();
