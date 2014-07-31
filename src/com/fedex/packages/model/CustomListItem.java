package com.fedex.packages.model;

/**
 * Represents a list item that can be a header or a normal item.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class CustomListItem {
	private String text;
	private boolean header;

	public CustomListItem(String text) {
		this.text = text;
		this.header = false;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the header
	 */
	public boolean isHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(boolean header) {
		this.header = header;
	}

	public CustomListItem(String text, boolean header) {
		this.text = text;
		this.header = header;
	}
}
