package com.zenaro.promotions;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.zenaro.promotions.crawler.EcommerceCrawler;
import com.zenaro.promotions.to.ProductTO;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class ApplicationCrawler {

	@Inject
	@Any
	private Instance<EcommerceCrawler> crawlers;

	public void init() throws Exception {
		
		List<CrawlController> controllers = new ArrayList<>();
		List<ProductTO> products = new ArrayList<>();

		for (EcommerceCrawler ecommerceCrawler : crawlers) {

			String crawlStorageFolder = "data/crawl/root/".concat(ecommerceCrawler.toString());
			int numberOfCrawlers = 50;

			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(crawlStorageFolder);
			config.setMaxPagesToFetch(100);

			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

			controller.setCustomData(products);

			controller.addSeed(ecommerceCrawler.getCrawlDomain());

			controller.startNonBlocking(ecommerceCrawler.getClass(), numberOfCrawlers);
			
			controllers.add(controller);
		}

		for (CrawlController controller : controllers) {
			controller.waitUntilFinish();
		}
		
		try {
			Workbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet("Ecomerces");

			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue("Nome");
			row.createCell(1).setCellValue("Link");
			row.createCell(2).setCellValue("Valor R$");
			row.createCell(3).setCellValue("% de Desconto");

			for (int i = 0; i < products.size(); i++) {
				ProductTO to = products.get(i);
				int col = 0;
				Row rowProduto = sheet.createRow(i + 1);
				rowProduto.createCell(col++).setCellValue(to.getName());
				rowProduto.createCell(col++).setCellValue(to.getLink());
				rowProduto.createCell(col++).setCellValue(to.getPrice().doubleValue());
				rowProduto.createCell(col++).setCellValue(to.getDiscount().doubleValue());
			}

			FileOutputStream out = new FileOutputStream("promocoes.xlsx");
			wb.write(out);
			out.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
