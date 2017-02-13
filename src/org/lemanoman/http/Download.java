package org.lemanoman.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

public class Download {
	public static void main(String[] args) {
		String[] valores = new String[]{"x","y","z","a","b"};
		String[] panel = new String[]{"Cat1","Cat2","Cat3"};
		
		int maxLoops = 20;
		int loop = 0;
		
		int last = 0;
		int max = 3;
		while(loop<maxLoops){
			for(int v=last;v<valores.length;v++){
				System.out.println(valores[v]);
				if(v==max){
					last = v;
					break;
				}
			}
			System.out.printf("Panel[0]: %s\t",panel[0]);
			System.out.printf("Panel[1]: %s\t",panel[1]);
			System.out.printf("Panel[2]: %s\t\n\n",panel[2]);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
	}
	public static void login(String[] args){
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("http://servicedesk2.flexvision.com.br/login.jsp??os_username=kevim.such&os_password=r04o10o2&os_destination=&user_role=&atl_token=&login=Log+In");
		post.setHeader("User-Agent", " Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
		HttpResponse response;
		try {
			response = client.execute(post);
			System.out.print("Response Code : " +response.getStatusLine().getStatusCode()+"\n");
			for(Header h:response.getAllHeaders()){
				System.out.println("Key: "+h.getName());
				System.out.println("Value: "+h.getValue());
			};
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void lancarHora(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("http://servicedesk2.flexvision.com.br/rest/tempo-rest/1.0/worklogs/SB-4632?user=kevim.such&id=&type=issue&selected-panel=&startTimeEnabled=true&tracker=false&preSelectedIssue=SB-4632&planning=false&issue=SB-4632&date=15%2FJul%2F15+09%3A30&enddate=22%2FJul%2F15&time=9h+23m&billedTime=9h+23m&remainingEstimate=0h&comment=");
		post.setHeader("User-Agent", " Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
		post.setHeader("Cookie", "JSESSIONID=2B761B4F397C2390E989CB221EEB9E0B;atlassian.xsrf.token=BKAN-P2DW-LTI4-4ME8|f4737a6d06c95c5d818944e994001883304f848e|lin;crowd.token_key=0lIoi4A6aSDOXrWEcWAFHQ00");
		HttpResponse response;
		try {
			response = client.execute(post);
			System.out.print("Response Code : " +response.getStatusLine().getStatusCode()+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public Download(String link, File f) {
		try {
			System.out.println("Trying Download: "+link);
			URL url = new URL(link);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(response);
			fos.close();
			System.out.println("OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
