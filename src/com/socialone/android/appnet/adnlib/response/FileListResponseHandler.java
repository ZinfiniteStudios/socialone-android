package com.socialone.android.appnet.adnlib.response;

import com.google.gson.reflect.TypeToken;
import com.socialone.android.appnet.adnlib.data.FileList;
import com.socialone.android.appnet.adnlib.data.ResponseEnvelope;

public abstract class FileListResponseHandler extends AppDotNetResponseEnvelopeHandler<FileList> {
    protected FileListResponseHandler() {
        super(new TypeToken<ResponseEnvelope<FileList>>(){});
    }
}