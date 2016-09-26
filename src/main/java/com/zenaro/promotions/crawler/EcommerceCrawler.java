package com.zenaro.promotions.crawler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.zenaro.promotions.to.ProductTO;

import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public abstract class EcommerceCrawler extends WebCrawler implements WebCrawlerFactory<EcommerceCrawler> {

	private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	public abstract String getCrawlDomain();

	protected abstract String getSelectorContainer();

	protected abstract String getSelectorName();

	protected abstract String getSelectorPrice();

	protected abstract String getSelectorPriceOld();

	@Override
	public EcommerceCrawler newInstance() throws Exception {
		return this;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return (!FILTERS.matcher(href).matches() && href.startsWith(getCrawlDomain()));
	}

	@Override
	public void visit(Page page) {
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

			Document doc = Jsoup.parse(htmlParseData.getHtml());

			Elements product = doc.select(getSelectorContainer());
			if (!product.isEmpty() && !product.attr("class").contains("unavailable")) {

				System.out.println(
						"Produto " + product.select(getSelectorName()).text() + " : " + page.getWebURL().getURL());

				@SuppressWarnings("unchecked")
				List<ProductTO> products = (List<ProductTO>) myController.getCustomData();

				BigDecimal valorNormal = null;
				Elements campoPrecoNormal = product.select(getSelectorPriceOld());
				valorNormal = extractValue(campoPrecoNormal.text());

				BigDecimal pricePromoctional = null;
				Elements campoPrecoPromocao = product.select(getSelectorPrice());
				pricePromoctional = extractValue(campoPrecoPromocao.text());

				if (valorNormal == null) {
					valorNormal = pricePromoctional;
				}

				String name = product.select(getSelectorName()).text();

				if (valorNormal != null && pricePromoctional != null) {
					BigDecimal diferenca = pricePromoctional.multiply(new BigDecimal(100)).divide(valorNormal, 2,
							RoundingMode.FLOOR);
					BigDecimal discont = new BigDecimal(100).subtract(diferenca);

					ProductTO to = new ProductTO();
					to.setName(name);
					to.setLink(page.getWebURL().getURL());
					to.setPrice(pricePromoctional);
					to.setDiscount(discont);
					products.add(to);
				}
			}
		}
	}

	private BigDecimal extractValue(String content) {
		BigDecimal valor = null;
		if (StringUtils.isNotBlank(content)) {
			if (content.contains("R$")) {
				content = content.replaceAll("[R$]", "");
			}
			content = content.replaceAll("[.]", "");
			content = content.replaceAll("[,]", ".");
			content = content.replaceAll(" ", "");
			if (NumberUtils.isNumber(content)) {
				valor = new BigDecimal(content);
			}
		}
		return valor;
	}
}
