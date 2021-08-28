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
import com.example.drawguess.model.RealWord;
import com.example.drawguess.model.WordsAbility;
import com.example.drawguess.point.DrawPoint;
import com.example.drawguess.point.MyPoint;
import com.example.drawguess.point.PointStyles;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.DependentLayout;
import ohos.agp.components.Text;

import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.utils.TextAlignment;
import ohos.agp.window.dialog.CommonDialog;

import ohos.app.Context;
import ohos.bundle.ElementName;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.event.commonevent.*;
import ohos.rpc.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

/**
 * Math Draw Page
 *
 * @since 2021-01-11
 */
public class DrawRemSlice extends AbilitySlice {
    private static final String TAG = CommonData.TAG + DrawRemSlice.class.getSimpleName();

    public static final float DIALOG_BOX_CORNER_RADIUS = 36.0f;

    public static final int DIALOG_BOX_WIDTH = 960;

    public static final int DIALOG_BOX_HEIGHT = 900;

    public static final int BUTTON_WIDTH=300;

    public static final int BUTTON_HEIGHT=100;

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
    private String buttonText1;
    private String buttonText2;
    private String buttonText3;
    private String buttonText4;
    private PointStyles pointStyle = new PointStyles();
     private static int drawerTime = 100;
    private String hintText1;
    private String hintText2;
    private String hintText3;
    private String hintText4;

    private CommonDialog commonDialog;
    WordsAbility words;

    RealWord realWord;
    Text time_drawer;

    ProgressBar progressBar_drawer;

    private List<DeviceInfo> devices;
    @Override
    public void onStart(Intent intent) {
        LogUtil.info(TAG, "DrawRemSlice::onStart");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_drawer_page);
        initAndConnectDevice(intent);
        words = new WordsAbility(this);
        realWord = RealWord.getSingleton();
        initButton();
        chooseWordDialog();
        initDraw();
        subscribe();
        //StartGameTime();
    }

    /**
     * Initialize and connect the device
     *
     * @param intent intent
     */
    private void initAndConnectDevice(Intent intent) {
        // Page initialization
        context = DrawRemSlice.this;
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

    private void initButton() {
//        //findComponentById(ResourceTable.Id_tttt).setClickedListener(new DrawRemSlice.ButtonClick());
        buttonText1 = words.getFirstWord();
        buttonText2 = words.getSecendWord();
        buttonText3 = words.getThirdWord();
        buttonText4 = words.getForthWord();

        hintText1 = words.getFirstHint();
        hintText2 = words.getSecendHint();
        hintText3 = words.getThirdHint();
        hintText4 = words.getForthHint();
    }

    //选词弹框
    private void chooseWordDialog(){
        LogUtil.info(TAG, "进入选词");
        Component dialogLayout =
                LayoutScatter.getInstance(this).parse(ResourceTable.Layout_dialog_select_words, null, false);
        Button button1 = (Button) dialogLayout.findComponentById(ResourceTable.Id_button1);
        Button button2 = (Button) dialogLayout.findComponentById(ResourceTable.Id_button2);
        Button button3 = (Button) dialogLayout.findComponentById(ResourceTable.Id_button3);
        Button button4 = (Button) dialogLayout.findComponentById(ResourceTable.Id_button4);
        Text cancel = (Text) dialogLayout.findComponentById(ResourceTable.Id_cancel);

        button1.setText(buttonText1);
        button2.setText(buttonText2);
        button3.setText(buttonText3);
        button4.setText(buttonText4);

        button1.setClickedListener(new DrawRemSlice.ButtonClick());
        button2.setClickedListener(new DrawRemSlice.ButtonClick());
        button3.setClickedListener(new DrawRemSlice.ButtonClick());
        button4.setClickedListener(new DrawRemSlice.ButtonClick());
        cancel.setClickedListener(new DrawRemSlice.ButtonClick());

        commonDialog = new CommonDialog(context);
        commonDialog.setAlignment(LayoutAlignment.CENTER);
        commonDialog.setSize(840, 900);
        commonDialog.setAutoClosable(true);
        commonDialog.setContentCustomComponent(dialogLayout);
        commonDialog.show();
    }

    private void setSelectedWord() {
        Text text = (Text) findComponentById(ResourceTable.Id_selectedword);
        text.setText(RealWord.getSingleton().getChoosedWord());
    }

    public void setHint() {
        Text text = (Text) findComponentById(ResourceTable.Id_selectedword);
        text.setText(WordsAbility.CHOOSEDWORD);
    }
    /**
     * Establish a remote connection
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

    private class ButtonClick implements Component.ClickedListener {
        @Override
        public void onClick(Component component) {
            int btnId = component.getId();
            switch (btnId) {
                case ResourceTable.Id_cancel:
                    commonDialog.hide();
                    break;
                case ResourceTable.Id_button1:
//                    WordsAbility.CHOOSEDWORD = buttonText1;
//                    WordsAbility.CHOOSEDHINT = hintText1;
                    realWord.setChoosedWord(buttonText1);
                    realWord.setChoosedHint(hintText1);
                    setSelectedWord();
                    commonDialog.hide();
                    break;
                case ResourceTable.Id_button2:
//                    WordsAbility.CHOOSEDWORD = buttonText2;
//                    WordsAbility.CHOOSEDHINT = hintText2;
                    realWord.setChoosedWord(buttonText2);
                    realWord.setChoosedHint(hintText2);
                    setSelectedWord();
                    commonDialog.hide();
                    break;
                case ResourceTable.Id_button3:
//                    WordsAbility.CHOOSEDWORD = buttonText3;
//                    WordsAbility.CHOOSEDHINT = hintText3;
                    realWord.setChoosedWord(buttonText3);
                    realWord.setChoosedHint(hintText3);
                    setSelectedWord();
                    commonDialog.hide();
                    break;
                case ResourceTable.Id_button4:
//                    WordsAbility.CHOOSEDWORD = buttonText4;
//                    WordsAbility.CHOOSEDHINT = hintText4;
                    realWord.setChoosedWord(buttonText4);
                    realWord.setChoosedHint(hintText4);
                    setSelectedWord();
                    commonDialog.hide();
                    break;
                default:
                    LogUtil.info(TAG, "Click default");
                    break;
            }
        }
    }
    private void StartGameTime() {
        LogUtil.info(TAG, " GuesserAbilitySlice::StartGameTime: ");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                drawerTime--;
                LogUtil.info(TAG, " drawerTime-1: " + drawerTime);
                context.getMainTaskDispatcher().asyncDispatch(() -> {
                    //initTime();
                    progressBar_drawer.setProgressValue(drawerTime);
                });
                LogUtil.info(TAG, " setProgressValue  success ");
                if(drawerTime <= 0) {
                    LogUtil.info(TAG, " Game Over ");
                    startNewGames(); // 时间到了
                    cancel(); // 停止计时器
                    System.gc(); // 结束线程
                }
            }
        }, 0, 1000);
    }

//    private void initTime(){
//        LogUtil.info(TAG, " DrawerAbilitySlice::initTime ："+ drawerTime );
//        Component textLayout = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_drawer_page, null, false);
//        LogUtil.info(TAG,"准备判断");
//        LogUtil.info(TAG,  "判断："+(textLayout.findComponentById(ResourceTable.Id_time_drawer) instanceof Text));
//        if (textLayout.findComponentById(ResourceTable.Id_time_drawer) instanceof Text) {
//            LogUtil.info(TAG,"if已进入");
//            LogUtil.info(TAG, " DrawerAbilitySlice::initTime2 ："+ drawerTime );
//            time_drawer = (Text) textLayout.findComponentById(ResourceTable.Id_time_drawer);
//            LogUtil.info(TAG, " DrawerAbilitySlice::initTime3 ："+ drawerTime );
//            time_drawer.setText(String.valueOf(drawerTime));
//            LogUtil.info(TAG, " DrawerAbilitySlice::initTime4 ："+ drawerTime );
//        }
//    }

    public void startNewGames(){
        LogUtil.info(TAG, " DrawerAbilitySlice::DoSomething ：" );
        LogUtil.info(TAG, "DrawerAbilitySlice CommonData.devices_list get:" + CommonData.devices_list);
        devices = new ArrayList<>();
//        ArrayList arrayList = new ArrayList<>();
//        LogUtil.info(TAG, "DrawerAbilitySlice devices_list getsize+++++++++++++++++++++:" + CommonData.devices_list.size());
//        devices.addAll(CommonData.devices_list);
//
//        LogUtil.info(TAG, "DrawerAbilitySlice devices getsize+++++++++++++++++++++:" + devices.size());
        List<DeviceInfo> deviceInfos =
                DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        devices.addAll(deviceInfos);
//        //这里可能需要加个标识符，加if
        startRemoteFa(devices.get(0).getDeviceId());
        startLocalFa(devices.get(1).getDeviceId());//本地跳转的问题出在这
    }

    private void startLocalFa(String deviceId) {
        LogUtil.info(TAG, "DrawerAbilitySlice::startLocalFa：......");
        Intent intent = new Intent();
        intent.setParam(CommonData.KEY_REMOTE_DEVICEID, deviceId);
        intent.setParam(CommonData.KEY_IS_LOCAL, true);
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(getBundleName())
                .withAbilityName(CommonData.ABILITY_DRAWER)
                .withAction(CommonData.DRAW_PAGE)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
        LogUtil.info(TAG, "DrawerAbilitySlice::startLocalFa::startAbility......");
    }
    //本地跳转的问题出在这
    private void startRemoteFa(String deviceId) {
        LogUtil.info(TAG, "DrawerAbilitySlice::startRemoteFa：......");
        String localDeviceId =
                KvManagerFactory.getInstance().createKvManager(new KvManagerConfig(this)).getLocalDeviceInfo().getId();
        Intent intent = new Intent();
        intent.setParam(CommonData.KEY_REMOTE_DEVICEID, localDeviceId);
        intent.setParam(CommonData.KEY_IS_LOCAL, false);
        Operation operation = new Intent.OperationBuilder().withDeviceId(deviceId)
                .withBundleName(getBundleName())
                .withAbilityName(CommonData.ABILITY_DRAWER)//测试：原本ABILITY_DRAWER，测试改为ABILITY_GUESSER
                .withAction(CommonData.GUESS_PAGE)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)//等会测试删除这行,测试结果：删除了也不行
                .build();
        intent.setOperation(operation);
        startAbility(intent);
        LogUtil.info(TAG, "DrawerAbilitySlice::startRemoteFa::startAbility......");
    }
}
