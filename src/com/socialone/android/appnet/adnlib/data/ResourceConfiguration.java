package com.socialone.android.appnet.adnlib.data;

public class ResourceConfiguration implements IAppDotNetObject {
    private int annotationMaxBytes;
    private int textMaxLength;

    public int getAnnotationMaxBytes() {
        return annotationMaxBytes;
    }

    public int getTextMaxLength() {
        return textMaxLength;
    }
}
