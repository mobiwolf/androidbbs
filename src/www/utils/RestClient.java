package www.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
    
public class RestClient {

        private ArrayList <NameValuePair> params;
        private ArrayList <NameValuePair> headers;

        private String url;

        private int responseCode;
        private String message;

        private String response;
		private HttpResponse httpResponse;
        
        public String getResponse() {
            return response;
        }

        public String getErrorMessage() {
            return message;
        }

        public int getResponseCode() {
            return responseCode;
        }
        
        public enum RequestMethod
        {
        	GET,
        	POST,
        	PUT,
        	DELETE
        }

        public RestClient(String url)
        {
            this.url = url;
            params = new ArrayList<NameValuePair>();
            headers = new ArrayList<NameValuePair>();
        }
        
        public void setUrl(String url){
        	this.url = url;
        }

        public void AddParam(String name, String value)
        {
            params.add(new BasicNameValuePair(name, value));
        }

        public void AddHeader(String name, String value)
        {
            headers.add(new BasicNameValuePair(name, value));
        }
       

        private void executeRequest(HttpUriRequest request, String url) throws UnknownHostException
        {
        	 // 设置连接超时时间和数据读取超时时间 
//            HttpParams httpParams = new BasicHttpParams(); 
//            HttpConnectionParams.setConnectionTimeout(httpParams, 
//                    KeySource.CONNECTION_TIMEOUT_INT); 
//            HttpConnectionParams.setSoTimeout(httpParams, 
//                    KeySource.SO_TIMEOUT_INT); 
            //新建HttpClient对象 

        	  HttpClient client = new DefaultHttpClient();

            try {

                httpResponse = client.execute(request);
                responseCode = httpResponse.getStatusLine().getStatusCode();
                message = httpResponse.getStatusLine().getReasonPhrase();
                
                HttpEntity entity = httpResponse.getEntity();

                if (entity != null) {

                    InputStream instream = entity.getContent();
                    response = convertStreamToString(instream);

                    // Closing the input stream will trigger connection release
                    instream.close();
                }

            }
            catch (UnknownHostException e)  {
                client.getConnectionManager().shutdown();
                throw e;
            }
            catch (ClientProtocolException e)  {
                client.getConnectionManager().shutdown();
                e.printStackTrace();
            } catch (IOException e) {
                client.getConnectionManager().shutdown();
                e.printStackTrace();
            }
         
        }

        public static String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
        
        public void Execute(RequestMethod method) throws UnknownHostException ,Exception
        {
            switch(method) {
                case GET:
                {
                    //add parameters
                    String combinedParams = "";
                    if(!params.isEmpty()){
                        combinedParams += "?";
                        for(NameValuePair p : params)
                        {
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                            if(combinedParams.length() > 1)
                            {
                                combinedParams  +=  "&" + paramString;
                            }
                            else
                            {
                                combinedParams += paramString;
                            }
                        }
                    }

                    HttpGet request = new HttpGet(url + combinedParams);

                    //add headers
                    for(NameValuePair h : headers)
                    {
                        request.addHeader(h.getName(), h.getValue());
                    }

                    executeRequest(request, url);
                    break;
                }
                case POST:
                {
                	String combinedParams = "";
                    if(!params.isEmpty()){
                        combinedParams += "?";
                        for(NameValuePair p : params)
                        {
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                            if(combinedParams.length() > 1)
                            {
                                combinedParams  +=  "&" + paramString;
                            }
                            else
                            {
                                combinedParams += paramString;
                            }
                        }
                    }
//                 	HttpResponse response = new client.execute(url);
                    HttpPost request = new HttpPost(url + combinedParams);
                    //读取应答数据
//                    int statusCode = request.getS

                    //add headers
                    for(NameValuePair h : headers)
                    {
                        request.addHeader(h.getName(), h.getValue());
                    }

                    if(!params.isEmpty()){
                        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }

                    executeRequest(request, url);
                    break;
                }
                case DELETE:
                {
                	
                	String combinedParams = "";
                    if(!params.isEmpty()){
                        combinedParams += "?";
                        for(NameValuePair p : params)
                        {
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                            if(combinedParams.length() > 1)
                            {
                                combinedParams  +=  "&" + paramString;
                            }
                            else
                            {
                                combinedParams += paramString;
                            }
                        }
                    }

                    HttpDelete request = new HttpDelete(url + combinedParams);

                    //add headers
                    for(NameValuePair h : headers)
                    {
                        request.addHeader(h.getName(), h.getValue());
                    }

                    executeRequest(request, url);
     
                	break;
                }
                case PUT:
                {
                	String combinedParams = "";
                    if(!params.isEmpty()){
                        combinedParams += "?";
                        for(NameValuePair p : params)
                        {
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                            if(combinedParams.length() > 1)
                            {
                                combinedParams  +=  "&" + paramString;
                            }
                            else
                            {
                                combinedParams += paramString;
                            }
                        }
                    }

                    HttpPut request = new HttpPut(url + combinedParams);
         
                    //add headers
                    for(NameValuePair h : headers)
                    {
                        request.addHeader(h.getName(), h.getValue());
                    }

                    executeRequest(request, url);
     
                	break;
                }
             }
        }
}