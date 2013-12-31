package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.IdList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class IdListResponseHandler extends AppDotNetResponseEnvelopeHandler<IdList> {
    protected IdListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<IdList>>(){});
    }
}
