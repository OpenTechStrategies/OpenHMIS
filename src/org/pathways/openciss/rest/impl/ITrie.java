package org.pathways.openciss.rest.impl;

public interface ITrie {
    abstract void insert(String s);
	abstract boolean search(String s);
}
