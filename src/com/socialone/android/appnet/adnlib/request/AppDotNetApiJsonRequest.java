package com.socialone.android.appnet.adnlib.request;


import com.socialone.android.appnet.adnlib.QueryParameters;
import com.socialone.android.appnet.adnlib.data.IAppDotNetObject;
import com.socialone.android.appnet.adnlib.response.AppDotNetResponseHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

public class AppDotNetApiJsonRequest extends AppDotNetApiRequest {
    public AppDotNetApiJsonRequest(AppDotNetResponseHandler handler, String requestMethod, IAppDotNetObject body,
                                   QueryParameters queryParameters, String... pathComponents) {
        super(handler, requestMethod, queryParameters, pathComponents);

        if (body == null)
            throw new IllegalArgumentException("body cannot be null");

        setBody(gson.toJson(body));
    }

    public AppDotNetApiJsonRequest(AppDotNetResponseHandler handler, String requestMethod, Map<String, Object> body, QueryParameters queryParameters,
                                   String... pathComponents) {
        super(handler, requestMethod, queryParameters, pathComponents);

        if (body == null)
            throw new IllegalArgumentException("body cannot be null");

        setBody(gson.toJson(body));
    }


    public AppDotNetApiJsonRequest(AppDotNetResponseHandler handler, IAppDotNetObject body, QueryParameters queryParameters,
                                   String... pathComponents) {
        this(handler, "POST", body, queryParameters, pathComponents);
    }

    @Override
    public void writeBody(HttpURLConnection connection) throws IOException {
        connection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        writeFixedLengthBody(connection);
    }
}