package com.zenaro.promotions.crawler;

public class AmericanasCrawler extends EcommerceCrawler {

	public String getCrawlDomain() {
		return "http://www.americanas.com.br/";
	}

	@Override
	protected String getSelectorContainer() {
		return ".a-main-product";
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
