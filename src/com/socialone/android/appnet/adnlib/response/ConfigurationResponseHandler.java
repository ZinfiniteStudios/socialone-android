package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.Configuration;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class ConfigurationResponseHandler extends AppDotNetResponseEnvelopeHandler<Configuration> {
    protected ConfigurationResponseHandler() {
        super(new TypeToken<ResponseEnvelope<Configuration>>(){});
    }
}