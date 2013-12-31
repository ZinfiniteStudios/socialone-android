package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;
import com.socialone.android.appnet.adnlib.data.StreamMarkerList;

public abstract class StreamMarkerListResponseHandler extends AppDotNetResponseEnvelopeHandler<StreamMarkerList> {
    protected StreamMarkerListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<StreamMarkerList>>(){});
    }
}