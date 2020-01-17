/*
 * Copyright © 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sjl.rfm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.sjl.rfm.util.NetUtils;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class CoreService extends Service {

    private Server mServer;

    @Override
    public void onCreate() {
        mServer = AndServer.serverBuilder(this)
            .inetAddress(NetUtils.getLocalIPAddress())
            .port(8090)
            .timeout(10, TimeUnit.SECONDS)
            .listener(new Server.ServerListener() {
                @Override
                public void onStarted() {
                    String hostAddress = mServer.getInetAddress().getHostAddress();
                    ServerManager.onServerStart(CoreService.this, hostAddress);
                }

                @Override
                public void onStopped() {
                    ServerManager.onServerStop(CoreService.this);
                }

                @Override
                public void onException(Exception e) {
                    ServerManager.onServerError(CoreService.this, e.getMessage());
                }
            })
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * Start server.
     */
    private void startServer() {
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerManager.onServerStart(CoreService.this, hostAddress);
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}