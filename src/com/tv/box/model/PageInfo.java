package com.tv.box.model;

import java.util.ArrayList;
import java.util.List;

public final class PageInfo<T>{

	private int pageIndex ;
	
	private int pageCount ;
	
	private final List<T> list = new ArrayList<T>() ;

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	
	public void addObject(T t){
		list.add(t);
	}

	public List<T> getList() {
		return list;
	}
	
	public void clear(){
		pageIndex	= 0 ;
		pageCount	= 0 ;
		list.clear() ;
	}
}
