package com.john.calculator;

import java.util.Arrays;

import bsh.Interpreter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity  implements OnClickListener {
	
	//结果屏
	EditText resText = null;
	//清空
	boolean  isClear = false;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		intitBtn();
	}

	/**
	 * 初始化按钮
	 */
	private void intitBtn() {
		//结果显示
		resText = (EditText)findViewById(R.id.rsText);
		//功能键
		Button btnDel = (Button)findViewById(R.id.delete);
		Button btnPlu = (Button)findViewById(R.id.plus);
		Button btnMin = (Button)findViewById(R.id.minus);
		Button btnMul = (Button)findViewById(R.id.multiply);
		Button btnDiv = (Button)findViewById(R.id.division);
		Button btnEqu = (Button)findViewById(R.id.equ);
		Button btnCle = (Button)findViewById(R.id.clean);
		Button btnLeft = (Button)findViewById(R.id.left);
		Button btnRight = (Button)findViewById(R.id.right);
		
		//数字键
		Button num0 = (Button)findViewById(R.id.num0);
		Button num1 = (Button)findViewById(R.id.num1);
		Button num2 = (Button)findViewById(R.id.num2);
		Button num3 = (Button)findViewById(R.id.num3);
		Button num4 = (Button)findViewById(R.id.num4);
		Button num5 = (Button)findViewById(R.id.num5);
		Button num6 = (Button)findViewById(R.id.num6);
		Button num7 = (Button)findViewById(R.id.num7);
		Button num8 = (Button)findViewById(R.id.num8);
		Button num9 = (Button)findViewById(R.id.num9);
		Button dot = (Button)findViewById(R.id.dot);
		
		//设置监听
		btnCle.setOnClickListener(this);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		btnPlu.setOnClickListener(this);
		btnMin.setOnClickListener(this);
		btnMul.setOnClickListener(this);
		btnDiv.setOnClickListener(this);
		btnEqu.setOnClickListener(this);
		  num0.setOnClickListener(this);
		  num1.setOnClickListener(this);
		  num2.setOnClickListener(this);
		  num3.setOnClickListener(this);
		  num4.setOnClickListener(this);
		  num5.setOnClickListener(this);
		  num6.setOnClickListener(this);
		  num7.setOnClickListener(this);
		  num8.setOnClickListener(this);
		  num9.setOnClickListener(this);
		  dot.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		Button btn = (Button) v;
		//结果
		String result = resText.getText().toString();
		
		if(isClear &&(
			  btn.getText().equals("0")
			  ||btn.getText().equals("1")
			  ||btn.getText().equals("2")
			  ||btn.getText().equals("3")
			  ||btn.getText().equals("4")
			  ||btn.getText().equals("5")
			  ||btn.getText().equals("6")
			  ||btn.getText().equals("7")
			  ||btn.getText().equals("8")
			  ||btn.getText().equals("9")
			  ||btn.getText().equals("."))  
			  ||btn.getText().equals("算数公式错误")){
				resText.setText("");
				isClear = false;
		}
		
		
		 if(btn.getText().equals("C")){//直接清除结果
			 resText.setText("");
		 }else if(btn.getText().equals("清除")){//消除一个输入
			 //什么都没有
			 if(result == null || result.trim().length() == 0){
				 return;}
			 else{
				 //清除最后输入的一个
				 resText.setText(result.substring(0,result.length()-1));}	 
			 }else if(btn.getText().equals("=")){
				 if(result==null || result.trim().length()==0){
						return; }
				 result = result.replace("×", "*");
				 result = result.replace("÷", "/");
				 result = getResult(result);
				 if(result.endsWith(".0")){
					 result = result.substring(0,result.indexOf(".0"));
				 }
				 resText.setText(result);
				 isClear = false;
			 }else{
				 resText.setText(resText.getText()+""+btn.getText());
				 isClear = false;
			 }
		
	}
	
	/**
	 * 
	 * @param result 算数表达式
	 * @return 计算结果
	 */
	private String getResult(String result) {
		Interpreter bsh = new Interpreter();
		Number res = null;
		try {
			result = filterExp(result);
			res = (Number) bsh.eval(result);
		} catch (Exception e) {
			e.printStackTrace();
			isClear = true;
			return "算数公式错误";
		}
		return res.doubleValue() + "";
	}

	/**
	 * 
	 * @param result 算数表达式
	 * @return
	 */
	private String filterExp(String result) {
		String num[] = result.split("");
		String temp = null;
		int begin=0,end=0;
		for(int i = 1; i < num.length; i++){
			temp = num[i];
			if(temp.matches("[+-/()*]")){
				//小数
				if(temp.equals(".")) continue;
				end = i - 1;  
				temp = result.substring(begin, end);
				if(temp.trim().length() > 0 && temp.indexOf(".")<0)
					 num[i-1] = num[i-1]+".0";
					 begin = end + 1;
			}
		}
		return Arrays.toString(num).replaceAll("[\\[\\], ]", "");
	}
}
