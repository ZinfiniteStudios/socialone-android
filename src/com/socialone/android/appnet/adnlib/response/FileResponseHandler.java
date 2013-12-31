package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.File;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class FileResponseHandler extends AppDotNetResponseEnvelopeHandler<File> {
    protected FileResponseHandler() {
        super(new TypeToken<ResponseEnvelope<File>>(){});
    }
}