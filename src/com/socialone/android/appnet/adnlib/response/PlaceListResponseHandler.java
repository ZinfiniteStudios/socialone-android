package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.PlaceList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class PlaceListResponseHandler extends AppDotNetResponseEnvelopeHandler<PlaceList> {
    protected PlaceListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<PlaceList>>(){});
    }
}