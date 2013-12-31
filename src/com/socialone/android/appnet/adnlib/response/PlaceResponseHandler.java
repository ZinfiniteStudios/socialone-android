package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.Place;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class PlaceResponseHandler extends AppDotNetResponseEnvelopeHandler<Place> {
    protected PlaceResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Place>>(){});
    }
}