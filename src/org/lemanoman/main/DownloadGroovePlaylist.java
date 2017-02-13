package org.lemanoman.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DownloadGroovePlaylist {

	public static void main(String[] args) {
		HttpClient client = HttpClientBuilder.create().build();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"/home/kevim/groove-playlist"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
				
			}
			Document doc = Jsoup.parse(sb.toString());
			Elements elements = doc.getElementsByTag("a");
			for(Element e:elements){
				String url = e.attr("href").replace("/exportPlaylist/", "")+".txt";
				String filename = e.text().replace(" ","_").replace("#","").replace("_:","").replace(">","").replace(")","").replace("(","").replace("/","-").replace("...","_").replace("+","_");
				//System.out.println(filename);
				System.out.println("mv "+url+" "+filename+".txt;");
				//download(client,url,"/home/kevim/grooveshark-playlists"+"/"+filename);
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

	public static void download(HttpClient client, String url,String destinationPath) {
		try {
			String[] urlSeparada = url.split("/");
			String imageName = urlSeparada[urlSeparada.length - 1];
			String destinationFile = destinationPath;
			if (!new File(destinationFile).exists()) {
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				System.out.print("Searching : " + url + "\n");
				System.out.print("Response Code : "
						+ response.getStatusLine().getStatusCode() + "\n");
				download(response.getEntity(), destinationFile);
			} else {
				System.out.println("Skipping: " + destinationFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void download(HttpEntity entity, String path) {
		try {
			System.out.print("Trying: " + path);
			File f = new File(path);
			if (f.exists()) {
				System.out.print(" : skip\n");
			} else {
				InputStream is = entity.getContent();
				FileOutputStream fos = new FileOutputStream(f);
				int inByte;
				while ((inByte = is.read()) != -1)
					fos.write(inByte);
				is.close();
				fos.close();
				System.out.print(" : OK\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print(" : Fail\n");
		}
	}

}
