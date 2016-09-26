package com.zenaro.promotions.crawler;

public class WalmartCrawler extends EcommerceCrawler {

	public String getCrawlDomain() {
		return "https://www.walmart.com.br/";
	}

	@Override
	protected String getSelectorContainer() {
		return "article[itemtype='//schema.org/Product']";
	}

	@Override
	protected String getSelectorName() {
		return ".product-title-header h1";
	}

	@Override
	protected String getSelectorPrice() {
		return ".buy-box-container .opened .payment-price strong";
	}

	@Override
	protected String getSelectorPriceOld() {
		return ".buy-box-container .opened .payment-price-old del";
	}

}
