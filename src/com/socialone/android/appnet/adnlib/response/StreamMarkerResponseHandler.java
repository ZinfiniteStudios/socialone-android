package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;
import com.socialone.android.appnet.adnlib.data.StreamMarker;

/**
 * Created by brambley on 10/8/13.
 */
public abstract class StreamMarkerResponseHandler extends AppDotNetResponseEnvelopeHandler<StreamMarker> {
    protected StreamMarkerResponseHandler() {
        super(new TypeToken<ResponseEnvelope<StreamMarker>>(){});
    }
}