package org.lemanoman.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lemanoman.http.Download;

import javax.script.*;
public class StartCentralDeMangas {
	// public StartCentralDeMangas() {
	// for(int i=134;i<=138;i++){
	// HttpClient client = HttpClientBuilder.create().build();
	// System.out.println("Starting download cap: "+i);
	// downloadCapitulo(i,client);
	// }
	// }

	public void getGantzPages() {
		HttpClient client = HttpClientBuilder.create().build();
		try {
			String content = "";
			for (int i = 1; i <= 383; i++) {
				String capString = "00" + i;
				if (i > 9) {
					capString = "0" + i;
				}
				if (i > 99) {
					capString = "" + i;
				}
				content += "\n"+"("+i+")"+getInfos(null,"http://centraldemangas.org/online/gantz/"+capString).replace("		", "").replace("var pages = ", "");
				System.out.print(content);
				
			}
			createFileTxt(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void lerEbaixar(){
		HttpClient client = HttpClientBuilder.create().build();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("gantz-pages.txt"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			Integer capitulo = 1;
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
				if(line!=null&&line!=""){
					line = line.split("\\[")[1].replace("\\/", "/").replace("\\]", "").replace("\"", "").replace(";", "");
					for(String link:line.split(",")){
						download(client, link, "/home/kevim/gantz/", capitulo);
						break;
					}
					//System.out.println(line);
				}
				//line = line.split("\\[")[1].replace("\\/", "/")
				//		.replace("\\]", "").replace("\"", "").replace(";", "");
				
//				for (String link : line.split(",")) {
//					if (link != null && link != "") {
//						System.out.println(link);
//						//download(client, link, "/home/kevim/gantz/", capitulo);
//						capitulo++;
//					}
//
//				}
				
			}
			// String everything = sb.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public StartCentralDeMangas() {
		//getGantzPages();
		lerEbaixar();
	}

	public void download(HttpClient client, String url, String destinationPath,
			Integer capitulo) {
		try {
			File destination = new File(destinationPath);
			if (!destination.exists()) {
				destination.mkdir();
			}
			File capDir = new File(destinationPath + capitulo);
			if (!capDir.exists()) {
				capDir.mkdir();
			}

			if (url.contains(".jpg")) {
				String[] urlSeparada = url.split("/");
				String imageName = urlSeparada[urlSeparada.length - 1];
				String destinationFile = capDir.getPath() + "/" + imageName;
				if (!new File(destinationFile).exists()) {
					HttpGet get = new HttpGet(url);
					get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					get.setHeader("Accept-Language","en-US,en;q=0.5");
					get.setHeader("Accept-Encoding","gzip, deflate");
					get.setHeader("Connection","keep-alive");
					get.setHeader("If-Modified-Since","Sun, 09 Dec 2012 17:03:23 GMT");
					get.setHeader("If-None-Match","50c4c45b-18d73");
					get.setHeader("Cache-Control","max-age=0");
					get.setHeader("User-Agent",	" Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
					get.setHeader("Cookie","PHPSESSID=obutim140g2gg6oesvqj2re062; __utma=92631823.1868150189.1426774023.1426854677.1426869695.3; __utmc=92631823; __utmz=92631823.1426854677.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __atuvc=20%7C11; __utmb=92631823.7.10.1426869695; __utmt=1; __atuvs=550c4dbfcd891037004");
					HttpResponse response = client.execute(get);
					System.out.print("Searching : " + url + "\n");
					System.out.print("Response Code : "
							+ response.getStatusLine().getStatusCode() + "\n");
					download(response.getEntity(), destinationFile);
				} else {
					System.out.println("Skipping: " + destinationFile);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadCapitulo(Integer capitulo, HttpClient client) {
		// http://centraldemangas.org/online/gantz/001#1
		// http://centraldosquadrinhos.com/wp-content/manga/Quadrinhos/thewalkingdead/edicao0134/www.thewalkingdead-online.com-003.jpg
		// String defaultImagemRepoLink =
		// "http://www.maxmangas.com.br/wp-content/manga/1/"+capitulo+"/";
		String defaultImagemRepoLink = "http://centraldosquadrinhos.com/wp-content/manga/Quadrinhos/thewalkingdead/edicao0"
				+ capitulo + "/";
		try {
			File destination = new File("/home/kevim/the-walking-dead-hqs/");
			if (!destination.exists()) {
				destination.mkdir();
			}
			File capDir = new File("/home/kevim/the-walking-dead-hqs/"
					+ capitulo);
			if (!capDir.exists()) {
				capDir.mkdir();
			}

			String html = getInfos(client, defaultImagemRepoLink);
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByTag("li");
			for (Element e : elements) {
				if (e.text().contains(".jpg")) {
					String image = e.text();
					String destinationFile = capDir.getPath() + "/" + image;
					if (!new File(destinationFile).exists()) {
						String url = defaultImagemRepoLink + image;
						url = url.replace(" ", "%20");
						HttpGet get = new HttpGet(url);
						get.setHeader("User-Agent",
								" Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
						get.setHeader("Cookie",
								"__cfduid=da015e4ef87311ec5b9b85383c504587f1424188606; popup_user_login=yes");
						HttpResponse response = client.execute(get);
						System.out.print("Searching : " + url + "\n");
						System.out.print("Response Code : "
								+ response.getStatusLine().getStatusCode()
								+ "\n");
						download(response.getEntity(), destinationFile);
					} else {
						System.out.println("Skipping: " + destinationFile);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getInfos(HttpClient client, String url)
			throws ClientProtocolException, IOException {
		if (client == null) {
			client = HttpClientBuilder.create().build();
		}
		HttpGet request = new HttpGet(url);

		// http://centraldemangas.org/online/gantz/001

		// add request header
		request.addHeader("User-Agent",
				" Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
		HttpResponse response = client.execute(request);
		//System.out.print("Response :" + response.getStatusLine() + "\t");

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			if (line.contains("var pages")) {
				result.append(line);
			}

		}
		return result.toString();

	}

	public static void main(String[] args) {
		new StartCentralDeMangas();

	}

	public void download(HttpEntity entity, String path) {
		try {
			System.out.print("Trying: " + path);
			File f = new File(path);
			if (f.exists()) {
				System.out.print(" : skip\n");
			} else {
				InputStream is = entity.getContent();
				FileOutputStream fos = new FileOutputStream(f);
				int inByte;
				while ((inByte = is.read()) != -1)fos.write(inByte);
				is.close();
				fos.close();
				System.out.print(" : OK\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print(" : Fail\n");
		}
	}

	public boolean exists(String link, HttpClient client) {
		System.out.println("Trying: " + link);
		HttpGet get = new HttpGet(link);
		try {
			HttpResponse response = client.execute(get);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void createFileTxt(String value) {
		try {

			String content = "This is the content to write into file";

			File file = new File("gantz-pages.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(value);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public Object evalJavaScript(String command){
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        try {
			return engine.eval(command);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
	/**
	 * \& x+'Eq^@@Ph^]PhP/POST /wpm-ajx/mng-vst-log/ HTTP/1.1 Host:
	 * www.maxmangas.com.br User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64;
	 * rv:36.0) Gecko/20100101 Firefox/36.0 Accept: Accept-Language:
	 * en-US,en;q=0.5 Accept-Encoding: gzip, deflate Content-Type:
	 * application/x-www-form-urlencoded; charset=UTF-8 X-Requested-With:
	 * XMLHttpRequest Referer: http://www.maxmangas.com.br/the-walking-dead/113/
	 * Content-Length: 21 Cookie:
	 * __cfduid=da015e4ef87311ec5b9b85383c504587f1424188606;
	 * popup_user_login=yes Connection: keep-alive Pragma: no-cache
	 * Cache-Control: no-cache
	 * 
	 * val=mng_chp-1-16682-1 1542 2015-03-16 15:36:18.966573000 172.16.4.6
	 * 104.28.3.94 HTTP 639 POST /wpm-ajx/mng-vst-log/ HTTP/1.1
	 * (application/x-www-form-urlencoded)
	 */
}
