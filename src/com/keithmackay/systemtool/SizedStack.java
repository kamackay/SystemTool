package com.keithmackay.systemtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SizedStack<T> extends Stack<T> {
	private int maxSize;

	public SizedStack(int size) {
		super();
		this.maxSize = size;
	}

	@Override
	public T push(T object) {
		//If the stack is too big, remove elements until it's the right size.
		while (this.size() >= this.maxSize) this.remove(0);
		return super.push(object);
	}

	public List<T> toList() {
		return new ArrayList<>(this);
	}

	public void setMaxSize(int size) {
		this.maxSize = size;
		while (this.size() >= this.maxSize) this.remove(0);
	}
}