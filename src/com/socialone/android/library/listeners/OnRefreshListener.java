/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.socialone.android.library.listeners;

import android.view.View;

/**
 * Simple Listener to listen for any callbacks to Refresh.
 */
public interface OnRefreshListener {
    /**
     * Called when the user has initiated a refresh by pulling.
     *
     * @param view
     *            - View which the user has started the refresh from.
     */
    public void onRefreshStarted(View view);
}
