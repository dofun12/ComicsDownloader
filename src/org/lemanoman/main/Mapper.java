package org.lemanoman.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Mapper {
	private static String rootFolderPath = "/home/kevim/HQandMangas/";
	private static String folderPath = "/home/kevim/HQandMangas/the-walking-dead-hqs";
	private Map<String,String> links;
	
	public Mapper() {
		ExecutorService executor = Executors.newCachedThreadPool();
		links =  new TreeMap<String,String>();
		for(int i=100;i<=157;i++){
			executor.execute(new Queue(i));
		}
		executor.shutdown();
		while(!executor.isTerminated()){}
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayLinks = mapper.createArrayNode();
		
		Map<String,String> uniqueCaps = new LinkedHashMap<String,String>();
		for(String key:links.keySet()){
			uniqueCaps.put(key.split(":")[0], key.split(":")[0]);
		}	
		
		for(String key:uniqueCaps.keySet()){
			ObjectNode capNode = mapper.createObjectNode();
			ArrayNode urls = mapper.createArrayNode();
			capNode.put("capitulo", key);
			int index =0;
			while(links.get(key+":"+getFormatedIndex(index))!=null){
				System.out.println("Getting: "+(key+":"+getFormatedIndex(index)));
				ObjectNode epNode = mapper.createObjectNode();
				epNode.put("episodio", getFormatedIndex(index));
				epNode.put("url", links.get(key+":"+getFormatedIndex(index)));
				urls.add(epNode);
				index++;
			}
			capNode.set("urls", urls);
			arrayLinks.add(capNode);
		}
		System.out.println(arrayLinks);
		try {
			mapper.writeValue(new File("links.json"), arrayLinks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFormatedIndex(int index){
		String imgIndex = String.valueOf(index+1); 
		if(imgIndex.toCharArray().length<2){
			imgIndex = "0"+imgIndex;
		}
		return imgIndex;
	}

	public class Queue implements Runnable{
		int cap;
		
		public Queue(int cap) {
			this.cap = cap;
		}
		
		@Override
		public void run() {
			HttpClient client = HttpClientBuilder.create().build();
			//System.out.println("Starting download cap: "+cap);
			mapCapitulo(cap,client);
		}
		
	}

	public void mapCapitulo(Integer capitulo,HttpClient client){
		//http://centraldosquadrinhos.com/wp-content/manga/Quadrinhos/thewalkingdead/edicao0134/www.thewalkingdead-online.com-003.jpg
		//String defaultImagemRepoLink = "http://www.maxmangas.com.br/wp-content/manga/1/"+capitulo+"/";
		String defaultImagemRepoLink = "http://centraldosquadrinhos.com/wp-content/manga/Quadrinhos/thewalkingdead/edicao0"+capitulo+"/";
		
		try{
			
			String html = getInfos(client,defaultImagemRepoLink);
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByTag("li");
			int totalFiles = 0;
			int imageIndex = 0;
			for(Element e:elements){
				if(e.text().contains(".jpg")){
					String image = e.text();
					//String destinationFile = capDir.getPath()+"/"+image; 
					String url = defaultImagemRepoLink+image;
					url=url.replace(" ","%20");
					//HttpGet get = new HttpGet(url);
					//get.setHeader("User-Agent", " Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
					//get.setHeader("Cookie", "__cfduid=da015e4ef87311ec5b9b85383c504587f1424188606; popup_user_login=yes");
					//HttpResponse response = client.execute(get);
					//System.out.print("Searching : " +url+"\n");
					//System.out.print("Response Code : " +response.getStatusLine().getStatusCode()+"\n");
					//if(response.getStatusLine().getStatusCode()==200){
						//System.out.println(capitulo+", url: "+url);
					//}
					//download(response.getEntity(),destinationFile);
						
					
					links.put(capitulo+":"+getFormatedIndex(imageIndex), url);
					totalFiles++;
					imageIndex++;
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
		new Mapper();

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
