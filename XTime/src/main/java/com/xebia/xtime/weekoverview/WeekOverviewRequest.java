package com.xebia.xtime.weekoverview;

import android.util.Log;

import com.xebia.xtime.shared.XTimeRequest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;

public class WeekOverviewRequest extends XTimeRequest {

    private static final String URL = "https://xtime.xebia" +
            ".com/xtime/dwr/call/plaincall/TimeEntryServiceBean.getWeekOverview.dwr";
    private static final String TAG = "WeekOverviewRequest";

    public String submit() {
        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new URL(URL);

            // TODO: do not blindly allow all host names...
            trustAllCertificates();

            urlConnection = (HttpURLConnection) url.openConnection();

            CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
            List<HttpCookie> cookies = cookieManager.getCookieStore().get(new URI("https://xtime" +
                    ".xebia.com/"));
            urlConnection.setRequestProperty("Cookie", cookies.get(0).toString());

            // do not follow redirects, we need the Location header to see if the login worked
            urlConnection.setInstanceFollowRedirects(false);

            urlConnection.setDoOutput(true);
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(out);

            String location = urlConnection.getHeaderField("Location");
            if (null != location && !location.contains("error=true")) {
                Log.d(TAG, "Request denied");
                return null;
            }

            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in), 8196);
            String line;
            StringBuilder responseContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }

            return responseContent.toString();

        } catch (IOException e) {
            Log.w(TAG, "Request failed", e);
            return null;
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Security problem performing request", e);
            return null;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String getRequestData() {
        String data = "callCount=1" + "\n";
        data += "scriptSessionId=" + System.currentTimeMillis() + "\n";
        data += "c0-scriptName=TimeEntryServiceBean" + "\n";
        data += "c0-methodName=getWeekOverview" + "\n";
        data += "c0-id=" + System.currentTimeMillis() + "\n";
        data += "c0-param0=string:2013-12-09" + "\n";
        data += "c0-param1=boolean:true" + "\n";
        data += "batchId=5" + "\n";
        return data;
    }

    private void writeStream(OutputStream out) throws IOException {
        out.write(getRequestData().getBytes());
        out.flush();
        out.close();
    }
}
