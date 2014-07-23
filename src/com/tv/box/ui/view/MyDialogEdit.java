
/**
 * MyDialog.java
 * <p>
 * ���ļ�Ϊ�ɶ��Լݱ�������޹�˾˽�У�δ����Ȩ�������⴫�����޸ģ�Υ��׷���������Ρ�
 * </p><p>
 * Copyright (c) 2012 �ɶ��Լݱ�������޹�˾
 * </p>
 * @author yujing
 * date 2013-5-29 ����10:58:28
 * @version Totoole V100R001S002
 * 
 */
package com.tv.box.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tv.box.R;

public final class MyDialogEdit extends Dialog implements android.view.View.OnClickListener{
	
	public static final int INPUT_DIALOG_TYPE_TEXT 				= 1 ;
	public static final int INPUT_DIALOG_TYPE_DECIMAL			= 2 ;
	public static final int INPUT_DIALOG_TYPE_PHONE_NUMBER		= 3 ;
	public static final int INPUT_DIALOG_TYPE_INTEGER			= 4 ;
	
	private TextView titleTxt;
	private EditText contentTxt;
	private String title;
	private String content;
	private TextView cancelBtn,okBtn;
	private int type;
	private OnMyEditClickListener onMyEditClickListener;
	private int maxLength ;
	
	public MyDialogEdit(Context context,String title,String content,int type,OnMyEditClickListener onMyEditClickListener) {
		super(context,R.style.mydialog);
		this.title = title;
		this.type = type;
		this.content = content;
		this.onMyEditClickListener = onMyEditClickListener;
		setCanceledOnTouchOutside(true);
	}
	
	public void setMaxLength(int max){
		maxLength = max ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_edit);
		setView();
	}
	
	public void setView(){
		
		titleTxt = (TextView)findViewById(R.id.activity_dialog_edit_title);
		contentTxt = (EditText)findViewById(R.id.activity_dialog_edit_content);
		okBtn = (TextView)findViewById(R.id.activity_dialog_edit_ok);
		cancelBtn = (TextView)findViewById(R.id.activity_dialog_edit_cancel);
		
		if(maxLength > 0){
			contentTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});  
		}
		
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		titleTxt.setText(title);
		contentTxt.setText(content);
		switch (type) {
			case INPUT_DIALOG_TYPE_TEXT:
				contentTxt.setInputType(InputType.TYPE_CLASS_TEXT);
				break;
			case INPUT_DIALOG_TYPE_DECIMAL:
				contentTxt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL) ;
				contentTxt.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {
						
					}
					
					@Override
					public void afterTextChanged(Editable edt) {
						
						String temp = edt.toString();
						
						int posDot = temp.indexOf(".");
						
						if (posDot <= 0)
							return;
						
						if (temp.length() - posDot - 1 > 2) {
							edt.delete(posDot + 3, posDot + 4);
						}
					}
				}) ;
				
				break;
			case INPUT_DIALOG_TYPE_PHONE_NUMBER:
				contentTxt.setInputType(InputType.TYPE_CLASS_PHONE);
				break;
			case INPUT_DIALOG_TYPE_INTEGER :
				contentTxt.setInputType(InputType.TYPE_CLASS_NUMBER) ;
				break ;
		}
	
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.activity_dialog_edit_cancel:
				MyDialogEdit.this.dismiss();
				break;
			case R.id.activity_dialog_edit_ok:
				content = contentTxt.getText().toString();
				onMyEditClickListener.onMyEditClick(content);
				MyDialogEdit.this.dismiss();
		}
	}

}

