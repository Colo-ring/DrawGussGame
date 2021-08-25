/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License,Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.drawguess.slice;

import com.example.drawguess.ResourceTable;
import com.example.drawguess.point.DrawPoint;
import com.example.drawguess.point.MyPoint;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.DependentLayout;
import ohos.agp.components.Text;
import ohos.app.Context;
import ohos.bundle.ElementName;
import ohos.event.commonevent.*;
import ohos.rpc.*;

import java.util.List;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

/**
 * Math Draw Page
 *
 * @since 2021-01-11
 */
public class GuesserAbilitySlice extends AbilitySlice {
    private static final String TAG = CommonData.TAG + GuesserAbilitySlice.class.getSimpleName();

    private DependentLayout area;

    private DrawPoint drawl;

    private float[] pointXs;

    private float[] pointYs;

    private IRemoteObject remoteObject;

    private MathRemoteProxy proxy;

    private Context context;

    private boolean[] isLastPoints;

    private MyCommonEventSubscriber subscriber;

    private boolean isLocal;

    @Override
    public void onStart(Intent intent) {
        LogUtil.info(TAG, "GuesserAbilitySlice::onStart");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_guesser_page);
        initAndConnectDevice(intent);
        initDraw();
        subscribe();
    }

    /**
     * Initialize and connect the device
     *
     * @param intent intent
     */
    private void initAndConnectDevice(Intent intent) {
        // Page initialization
        context = GuesserAbilitySlice.this;
        String remoteDeviceId = intent.getStringParam(CommonData.KEY_REMOTE_DEVICEID);
        isLocal = intent.getBooleanParam(CommonData.KEY_IS_LOCAL, false);

        // Connect to remote service
        if (!remoteDeviceId.isEmpty()) {
            connectRemotePa(remoteDeviceId);
        } else {
            LogUtil.info(TAG, "localDeviceId is null");
        }
    }

    private void connectRemotePa(String deviceId) {
        if (!deviceId.isEmpty()) {
            Intent connectPaIntent = new Intent();
            Operation operation = new Intent.OperationBuilder().withDeviceId(deviceId)
                    .withBundleName(getBundleName())
                    .withAbilityName(CommonData.DRAWER_SERVICE_NAME)
                    .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                    .build();
            connectPaIntent.setOperation(operation);

            IAbilityConnection conn = new IAbilityConnection() {
                @Override
                public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
                    LogUtil.info(TAG, "onAbilityConnectDone......");
                    connectAbility(elementName, remote, resultCode);
                }

                @Override
                public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
                    LogUtil.info(TAG, "onAbilityDisconnectDone......");
                    disconnectAbility(this);
                }
            };

            context.connectAbility(connectPaIntent, conn);
        }
    }

    private void connectAbility(ElementName elementName, IRemoteObject remote, int resultCode) {
        remoteObject = remote;
        proxy = new MathRemoteProxy(remote);
        LogUtil.info(TAG, "connectRemoteAbility done connected to local service");
        if (proxy != null) {
            try {
                proxy.senDataToRemote(MathRemoteProxy.REQUEST_START_ABILITY);
            } catch (RemoteException e) {
                LogUtil.error(TAG, "onAbilityConnectDone RemoteException:" + e.getMessage());
            }
        }
    }

    private void initDraw() {
        if (findComponentById(ResourceTable.Id_drawer_area) instanceof DependentLayout) {
            area = (DependentLayout) findComponentById(ResourceTable.Id_drawer_area);
        }
        drawl = new DrawPoint(this, isLocal);
        drawl.setWidth(MATCH_PARENT);
        drawl.setWidth(MATCH_PARENT);
        area.addComponent(drawl);
        drawl.setOnDrawBack(points -> {
            drawPoint(points);
        });
    }

    private void drawPoint(List<MyPoint> points) {
        if (points != null && points.size() > 1) {
            pointXs = new float[points.size()];
            pointYs = new float[points.size()];
            isLastPoints = new boolean[points.size()];
            for (int i = 0; i < points.size(); i++) {
                pointXs[i] = points.get(i).getPositionX();
                pointYs[i] = points.get(i).getPositionY();
                isLastPoints[i] = points.get(i).isLastPoint();
            }

            // After the drawing is completed, send the data to the remote
            if (remoteObject != null && proxy != null) {
                try {
                    proxy.senDataToRemote(MathRemoteProxy.REQUEST_SEND_DATA);
                } catch (RemoteException e) {
                    LogUtil.info(TAG, "processEvent RemoteException");
                }
            }
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribe();
    }

    private void subscribe() {
        MatchingSkills matchingSkills = new MatchingSkills();
        matchingSkills.addEvent(CommonData.DRAW_EVENT);
        matchingSkills.addEvent(CommonEventSupport.COMMON_EVENT_SCREEN_ON);
        CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(matchingSkills);
        subscriber = new MyCommonEventSubscriber(subscribeInfo);
        try {
            CommonEventManager.subscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtil.error("", "subscribeCommonEvent occur exception.");
        }
    }

    private void unSubscribe() {
        try {
            CommonEventManager.unsubscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "unSubscribe Exception");
        }
    }

    /**
     * Establish a remote connection
     *
     * @since 2021-01-11
     */
    class MathRemoteProxy implements IRemoteBroker {
        private static final int ERR_OK = 0;

        private static final int REQUEST_START_ABILITY = 1;

        private static final int REQUEST_SEND_DATA = 2;

        private final IRemoteObject remote;

        MathRemoteProxy(IRemoteObject remote) {
            this.remote = remote;
        }

        @Override
        public IRemoteObject asObject() {
            return remote;
        }

        private void senDataToRemote(int requestType) throws RemoteException {
            LogUtil.info(TAG, "send data to local draw service");
            MessageParcel data = MessageParcel.obtain();
            MessageParcel reply = MessageParcel.obtain();
            MessageOption option = new MessageOption(MessageOption.TF_SYNC);
            try {
                if (pointXs != null && pointYs != null && isLastPoints != null) {
                    data.writeFloatArray(pointXs);
                    data.writeFloatArray(pointYs);
                    data.writeBooleanArray(isLastPoints);
                }
                remote.sendRequest(requestType, data, reply, option);
                int ec = reply.readInt();
                if (ec != ERR_OK) {
                    LogUtil.error(TAG, "RemoteException:");
                }
            } catch (RemoteException e) {
                LogUtil.error(TAG, "RemoteException:");
            } finally {
                data.reclaim();
                reply.reclaim();
            }
        }
    }

    /**
     * MyCommonEventSubscriber
     *
     * @since 2021-01-11
     */
    class MyCommonEventSubscriber extends CommonEventSubscriber {
        MyCommonEventSubscriber(CommonEventSubscribeInfo info) {
            super(info);
        }

        @Override
        public void onReceiveEvent(CommonEventData commonEventData) {
            Intent intent = commonEventData.getIntent();
            pointXs = intent.getFloatArrayParam(CommonData.KEY_POINT_X);
            pointYs = intent.getFloatArrayParam(CommonData.KEY_POINT_Y);
            isLastPoints = intent.getBooleanArrayParam(CommonData.KEY_IS_LAST_POINT);
            // After receiving the data, draw on the remote canvas
            drawl.setDrawParams(isLastPoints, pointXs, pointYs);
            LogUtil.info(TAG, "onReceiveEvent.....");
        }
    }
}
