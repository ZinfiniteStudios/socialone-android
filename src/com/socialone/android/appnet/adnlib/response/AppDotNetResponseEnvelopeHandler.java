package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.AppDotNetResponseException;
import com.socialone.android.appnet.adnlib.data.IAppDotNetObject;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;
import com.socialone.android.appnet.adnlib.data.ResponseMeta;

import java.io.Reader;
import java.net.HttpURLConnection;

public abstract class AppDotNetResponseEnvelopeHandler<T extends IAppDotNetObject> extends AppDotNetResponseHandler<T> {
    protected ResponseMeta responseMeta;

    protected AppDotNetResponseEnvelopeHandler(TypeToken<ResponseEnvelope<T>> typeToken) {
        super(typeToken);
    }

    @Override
    public void handleResponse(Reader reader) {
        final ResponseEnvelope<T> response = parseResponse(reader);
        responseMeta = response.getMeta();

        if (responseMeta.getCode() != HttpURLConnection.HTTP_OK) {
            onError(new AppDotNetResponseException(responseMeta));
        } else {
            onSuccess(response.getData());
        }
    }

    public ResponseMeta getResponseMeta() {
        return responseMeta;
    }

    public String getMaxId() {
        if (responseMeta == null)
            return null;

        return responseMeta.getMaxId();
    }

    public String getMinId() {
        if (responseMeta == null)
            return null;

        return responseMeta.getMinId();
    }

    public boolean isMore() {
        if (responseMeta == null)
            return false;

        return responseMeta.isMore();
    }
}