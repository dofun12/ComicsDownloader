package org.lemanoman.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Start {
	private static String rootFolderPath = "/home/kevim/HQandMangas/";
	private static String folderPath = "/home/kevim/HQandMangas/the-walking-dead-hqs";
	
	public Start() {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(int i=144;i<=163;i++){
			executor.execute(new Queue(i));
		}
		executor.shutdown();
	}
	
	public class Queue implements Runnable{
		int cap;
		
		public Queue(int cap) {
			this.cap = cap;
		}
		
		@Override
		public void run() {
			HttpClient client = HttpClientBuilder.create().build();
			System.out.println("Starting download cap: "+cap);
			downloadCapitulo(cap,client);
		}
		
	}

	public void downloadCapitulo(Integer capitulo,HttpClient client){
		//http://centraldosquadrinhos.com/wp-content/manga/Quadrinhos/thewalkingdead/edicao0134/www.thewalkingdead-online.com-003.jpg
		//String defaultImagemRepoLink = "http://www.maxmangas.com.br/wp-content/manga/1/"+capitulo+"/";
		String defaultImagemRepoLink = "http://centraldosquadrinhos.com/wp-content/manga/Quadrinhos/thewalkingdead/edicao0"+capitulo+"/";
		
		try{
			File rootFolder = new File(rootFolderPath);
			if(!rootFolder.exists()){
				rootFolder.mkdirs();
			}
			File destination = new File(folderPath);
			if(!destination.exists()){
				destination.mkdir();
			}
			File capDir = new File(folderPath+capitulo);
			if(!capDir.exists()){
				capDir.mkdir();
			}
			
			String html = getInfos(client,defaultImagemRepoLink);
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByTag("li");
			for(Element e:elements){
				if(e.text().contains(".jpg")){
					String image = e.text();
					String destinationFile = capDir.getPath()+"/"+image; 
					if(!new File(destinationFile).exists()){
						String url = defaultImagemRepoLink+image;
						url=url.replace(" ","%20");
						HttpGet get = new HttpGet(url);
						get.setHeader("User-Agent", " Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
						get.setHeader("Cookie", "__cfduid=da015e4ef87311ec5b9b85383c504587f1424188606; popup_user_login=yes");
						HttpResponse response = client.execute(get);
						System.out.print("Searching : " +url+"\n");
						System.out.print("Response Code : " +response.getStatusLine().getStatusCode()+"\n");
						download(response.getEntity(),destinationFile);
					}else{
						System.out.println("Skipping: "+destinationFile);
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getInfos(HttpClient client,String url) throws ClientProtocolException, IOException{
		HttpGet request = new HttpGet(url);
		 
		// add request header
		request.addHeader("User-Agent", " Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
		HttpResponse response = client.execute(request);
	 
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
		
	}
	
	public static void main(String[] args) {
		new Start();

	}

	public void download(HttpEntity entity,String path) {
		try {
			System.out.print("Trying: "+path);
			File f = new File(path);
			if(f.exists()){
				System.out.print(" : skip\n");
			}else{
				InputStream is = entity.getContent();
				FileOutputStream fos = new FileOutputStream(f);
				int inByte;
				while((inByte = is.read()) != -1) fos.write(inByte);
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
/**
 * \&
 *x+'Eq^@@Ph^]PhP/POST /wpm-ajx/mng-vst-log/ HTTP/1.1
*Host: www.maxmangas.com.br
*User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0
*Accept: 
*Accept-Language: en-US,en;q=0.5
*Accept-Encoding: gzip, deflate
*Content-Type: application/x-www-form-urlencoded; charset=UTF-8
*X-Requested-With: XMLHttpRequest
*Referer: http://www.maxmangas.com.br/the-walking-dead/113/
*Content-Length: 21
*Cookie: __cfduid=da015e4ef87311ec5b9b85383c504587f1424188606; popup_user_login=yes
*Connection: keep-alive
*Pragma: no-cache
*Cache-Control: no-cache

val=mng_chp-1-16682-1
 * 1542	2015-03-16 15:36:18.966573000	172.16.4.6	104.28.3.94	HTTP	639	POST /wpm-ajx/mng-vst-log/ HTTP/1.1  (application/x-www-form-urlencoded)
 */
}
