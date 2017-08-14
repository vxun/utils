/**
 * Project Name:utils 
 * Class Name:zhangxw.utils.common.HttpsUtils  
 * Date:2017年5月7日下午2:17:47 
 * Copyright (c) 2017, zhangxw All Rights Reserved. 
 */
package zhangxw.utils.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author zhangminpeng on 2016-06-13 14:50
 * @Version 1.0 Date 2017-05-07 14:17:47
 */
public class HttpsUtils {

	/**
     * 创建 HTTPS 链接客户端,默认信任证书,不跟随重定向
     *
     * @return HTTP 连接
     */
	public static CloseableHttpClient createHttpsClient()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
		return HttpClients.custom()
				.setUserAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36")
				.setSSLSocketFactory(sslConnectionSocketFactory)
				.build();
	}
	/**
     * 发送 get 请求
     *
     * @param url     请求 url
     * @param headers 请求头数组
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String get(String url, Header[] headers)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = createHttpsClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeaders(headers);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        String result = entity2String(httpResponse.getEntity());

        // 关闭资源
        httpClient.close();
        httpGet.releaseConnection();
        return result;
    }
    
    /**
     * 发送 get 请求
     *
     * @param url 请求 url
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String get(String url)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        return get(url, null);
    }
    
    /**
     * 发送 post 请求
     *
     * @param url     请求 url
     * @param headers 请求头
     * @param entity  请求实体
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String post(String url, Header[] headers, HttpEntity entity)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        CloseableHttpClient httpClient = createHttpsClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        String result = entity2String(httpResponse.getEntity());

        // 关闭资源
        httpClient.close();
        httpPost.releaseConnection();
        return result;
    }

    /**
     * 发送 post 请求
     *
     * @param url 请求 url
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String post(String url)
            throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return post(url, null, null);
    }

    /**
     * 发送 post 请求
     *
     * @param url     请求 url
     * @param headers 请求头
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String post(String url, Header[] headers)
            throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return post(url, headers, null);
    }
    
    /**
     * 发送 post 请求
     *
     * @param url    请求 url
     * @param entity 请求体
     * @return 响应结果字符串
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String post(String url, HttpEntity entity)
            throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return post(url, null, entity);
    }

    private static String entity2String(HttpEntity entity){
    	String result = null;
    	try {
			result = EntityUtils.toString(entity, Charset.forName("UTF-8"));
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    /**
     * 将响应实体拼接成字符串返回
     *
     * @param entity 响应实体
     * @return 实体字符串
     */
    @SuppressWarnings("unused")
	private static String entity2String2(HttpEntity entity) {
    	
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = entity.getContent();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            // 读取数据
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    
    @SuppressWarnings("unused")
	public static void main(String[] args) {
    	String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxe94f12df539de649&secret=03b541cda6b45e4a2738ed7f1b55c154";
    	String token = "VMMsJaTOHIhowyvVZm1nYMHce6thyQUfEfVbpMT8LzCogsrByhGQfaIUkgrEtHi6vgKwPrxe-zBfocVe6WaRtJcEoNgcRiTnaEv2ShWWiPhpYaGwW1B8phUePwlTEjBDPQTiAJABCF";
    	String url = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token="+token;
		try {
			String result = get(url);
			System.out.println(result);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
			e.printStackTrace();
		}
	}
    // VMMsJaTOHIhowyvVZm1nYMHce6thyQUfEfVbpMT8LzCogsrByhGQfaIUkgrEtHi6vgKwPrxe-zBfocVe6WaRtJcEoNgcRiTnaEv2ShWWiPhpYaGwW1B8phUePwlTEjBDPQTiAJABCF
    
}
