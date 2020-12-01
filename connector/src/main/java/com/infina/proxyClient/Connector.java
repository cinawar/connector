package com.infina.proxyClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;


/**
 * auth:oarslan
 * cmd : java -jar InfinaConnectChecker.jar
 * cmd : java -jar InfinaConnectChecker.jar http://www.google.com
 */
public class Connector 
{
    public static void main( String[] args ) throws UnsupportedOperationException, IOException
    {
    	String adress =null;
    	
    	if (args.length>0 && args[0]!=null) {
    		adress = args[0].toString();
    	} else {
    		adress = "https://www.borsaistanbul.com/data/thb/2019/01/thb201901221.zip";
    	}
    	
        System.out.println( adress+" cikmak icin cagri yapiyorum!" );
        System.out.println(getStreamFromUrl(adress));
    }
    
    public static InputStream  getStreamFromUrl(String url) throws UnsupportedOperationException, IOException  {
    	HttpEntity entity=null;
    	Properties prop = new Properties();
		try {
			URIBuilder builder = new URIBuilder(url);
			URI uri = builder.build();
			int statusCode =0;
			
			System.out.println("***************");
 
			 File jarPath=new File(Connector.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
		        System.out.println(" propertiesPath-"+propertiesPath);
		        prop.load(new FileInputStream(propertiesPath+"/infleks.properties"));
			
			System.out.println("Propertyler http.proxyHost:"+prop.getProperty("http.proxyHost"));
			
			CloseableHttpClient httpclient = getHtppClient();
			
				if(httpclient!=null) {
					HttpGet requestGet = new HttpGet(uri);		
					
					String proxyHost=prop.getProperty("http.proxyHost");
					String proxyHostPort=prop.getProperty("http.proxyPort");
					
					String proxyHostHttps=prop.getProperty("https.proxyHost");
					String proxyHostPortHttps=prop.getProperty("https.proxyPort");
					
					
					System.out.println("okunan:"+proxyHost);
					if (proxyHost!=null && proxyHostPort!=null) {
						HttpHost proxy = new HttpHost(proxyHost, Integer.valueOf(proxyHostPort), "http");
						RequestConfig config = RequestConfig.custom()
				                .setProxy(proxy)
				                .build();
						requestGet.setConfig(config);
						System.out.println("proxy http kullanilarak client olusturuldu");
					}
					if (proxyHostHttps!=null && proxyHostPortHttps!=null) {
						HttpHost proxy = new HttpHost(proxyHostHttps, Integer.valueOf(proxyHostPortHttps), "https");
						RequestConfig config = RequestConfig.custom()
				                .setProxy(proxy)
				                .build();
						requestGet.setConfig(config);
						System.out.println("proxy https kullanilarak client olusturuldu");
					}
					
					HttpResponse response = httpclient.execute(requestGet);
					entity = response.getEntity();
					org.apache.http.StatusLine statusLine = response.getStatusLine();
			        statusCode = statusLine.getStatusCode();
			        System.out.println("statusCode:"+statusCode+" entity length:"+(entity!=null ? entity.getContentLength() :"0" ));
						if (entity == null ) {
								System.out.println("response entity is null  from url:"+url);
						} else {
							InputStream xis = entity.getContent();
							byte[] b = new byte[1024];
							int len = 0;
									if (xis!=null) {
										while ((len = xis.read(b)) > 0) {
											System.out.println("data okuyorum:"+len);
										}
									}
						
			}  
				} else {
				System.out.println("cannot get  httpclient  from url:"+url);
			}
				
		} catch (Exception e) {
			System.out.println("cannot get  Stream client  Error url:"+url+" exception:" +e);
			
		}
		return entity.getContent();
	}
	private static CloseableHttpClient getHtppClient() throws Exception {
			CloseableHttpClient httpClient=null;
			try {
				int timeout = 5;
				RequestConfig config = RequestConfig.custom()
				  .setConnectTimeout(timeout * 1000)
				  .setConnectionRequestTimeout(timeout * 1000)
				  .setSocketTimeout(timeout * 1000).build();
				 
				httpClient = HttpClients
				    .custom()
				    .setSSLContext(new org.apache.http.ssl.SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
				    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				    .setDefaultRequestConfig(config)
				    .setRedirectStrategy(new LaxRedirectStrategy())
				    .build();
			} catch (Exception e) {
				System.err.println("cannot get  Http client  Error:"+e);
	
			}
			
			return httpClient;
		}

}
