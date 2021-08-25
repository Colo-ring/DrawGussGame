package com.example.drawguess;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.rpc.*;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;

public class DrawerServiceAbility extends Ability {
    private static final String TAG = CommonData.TAG + DrawerServiceAbility.class.getSimpleName();

    private DrawerRemote remote = new DrawerRemote();

    @Override
    public void onStart(Intent intent) {
        LogUtil.info(TAG, "RemoteServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        super.onBackground();
        LogUtil.info(TAG, "RemoteServiceAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.info(TAG, "RemoteServiceAbility::onStop");
    }

    @Override
    protected IRemoteObject onConnect(Intent intent) {
        super.onConnect(intent);
        return remote.asObject();
    }

    private void sendEvent(boolean[] isLastPoint, float[] pointsX, float[] pointsY) {
        LogUtil.info(TAG, "sendEvent......");
        try {
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder().withAction(CommonData.DRAW_EVENT).build();
            intent.setOperation(operation);
            intent.setParam(CommonData.KEY_POINT_X, pointsX);
            intent.setParam(CommonData.KEY_POINT_Y, pointsY);
            intent.setParam(CommonData.KEY_IS_LAST_POINT, isLastPoint);
            CommonEventData eventData = new CommonEventData(intent);
            CommonEventManager.publishCommonEvent(eventData);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "publishCommonEvent occur exception.");
        }
    }

    /**
     * MathRemote Establish a remote connection
     */
    public class DrawerRemote extends RemoteObject implements IRemoteBroker {
        private static final int ERR_OK = 0;

        private DrawerRemote() {
            super("DrawerRemote");
        }

        @Override
        public IRemoteObject asObject() {
            return this;
        }

        @Override
        public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
            LogUtil.info(TAG, "onRemoteRequest......");
            float[] pointsX = data.readFloatArray();
            float[] pointsY = data.readFloatArray();
            boolean[] isLastPoint = data.readBooleanArray();
            reply.writeInt(ERR_OK);
            sendEvent(isLastPoint, pointsX, pointsY);
            return true;
        }
    }
}