package com.socialone.android.socialauth;

import android.os.Bundle;

public interface LoginListener {
    public void onComplete(Bundle bundle);
    public void onError(Throwable error);
    public void onCancel();
    public void onBack();
}
