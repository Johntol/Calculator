/**
 * 测试画一个矩形
 */
var statge = new createjs.Stage("gameView");
var gameView = new createjs.Container();
statge.addChild(gameView);
var s = new createjs.Shape();
s.graphics.beginFill("#ff0000");
s.graphics.drawRect(0,0,100,100);
s.graphics.endFill();

gameView.addChild(s);
createjs.Ticker.setFPS(30);
createjs.Ticker.addEventListener("tick",statge);
