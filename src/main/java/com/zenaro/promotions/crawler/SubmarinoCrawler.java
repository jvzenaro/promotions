package com.zenaro.promotions.crawler;

public class SubmarinoCrawler extends EcommerceCrawler {

	public String getCrawlDomain() {
		return "http://www.submarino.com.br/";
	}

	@Override
	protected String getSelectorContainer() {
		return ".main-product";
	}

	@Override
	protected String getSelectorName() {
		return ".prodTitle span";
	}

	@Override
	protected String getSelectorPrice() {
		return "span[itemprop='price/salesPrice']";
	}

	@Override
	protected String getSelectorPriceOld() {
		return ".mp-price-of del";
	}

}
