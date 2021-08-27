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

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.security.SystemPermission.DISTRIBUTED_DATASYNC;

import com.example.drawguess.ResourceTable;
import com.example.drawguess.devices.SelectDeviceDialog;
import com.example.drawguess.model.readSqliteFile;
import com.example.drawguess.model.sqliteData;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Text;

import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.utils.TextAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;

import ohos.bundle.IBundleManager;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MainAbilitySlice
 *
 * @since 2021-01-11
 */
public class MainAbilitySlice extends AbilitySlice {
    private static final String TAG = CommonData.TAG + MainAbilitySlice.class.getSimpleName();

    private Button startBtn;

    private Button ruleBtn;

    public static final float DIALOG_BOX_CORNER_RADIUS = 36.0f;

    public static final int DIALOG_BOX_WIDTH = 984;

    private static final int PERMISSION_CODE = 10000000;

    private Text resultText;

    @Override
    public void onStart(Intent intent) {
        LogUtil.info(TAG, "MainAbilitySlice::onStart");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        grantPermission();
        initButton();
    }

    void grantPermission() {
        if (verifySelfPermission(DISTRIBUTED_DATASYNC) != IBundleManager.PERMISSION_GRANTED) {
            if (canRequestPermission(DISTRIBUTED_DATASYNC)) {
                requestPermissionsFromUser(new String[] {DISTRIBUTED_DATASYNC}, PERMISSION_CODE);
            }
        }
    }

    private void initButton() {
        findComponentById(ResourceTable.Id_start).setClickedListener(new ButtonClick());
        findComponentById(ResourceTable.Id_rule).setClickedListener(new ButtonClick());
    }

    private List<DeviceInfo> devices = new ArrayList<>();

    private void getDevices() {
        if (devices.size() > 0) {
            devices.clear();
        }
        List<DeviceInfo> deviceInfos =
                DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        LogUtil.info(TAG, "DrawerAbilitySlice deviceInfos size is :" + deviceInfos.size());
        devices.addAll(deviceInfos);
        showDevicesDialog();
    }

    private void showDevicesDialog() {
        new SelectDeviceDialog(this, devices, deviceInfo -> {
            startLocalFa(deviceInfo.getDeviceId());
            startRemoteFa(deviceInfo.getDeviceId());
        }).show();
    }

    private void startLocalFa(String deviceId) {
        LogUtil.info(TAG, "startLocalFa......");
        Intent intent = new Intent();
        intent.setParam(CommonData.KEY_REMOTE_DEVICEID, deviceId);
        intent.setParam(CommonData.KEY_IS_LOCAL, true);
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(getBundleName())
                .withAbilityName(CommonData.ABILITY_MAIN)
                .withAction(CommonData.DRAW_PAGE)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }

    private void startRemoteFa(String deviceId) {
        LogUtil.info(TAG, "startRemoteFa......");
        String localDeviceId =
                KvManagerFactory.getInstance().createKvManager(new KvManagerConfig(this)).getLocalDeviceInfo().getId();
        Intent intent = new Intent();
        intent.setParam(CommonData.KEY_REMOTE_DEVICEID, localDeviceId);
        intent.setParam(CommonData.KEY_IS_LOCAL, false);
        Operation operation = new Intent.OperationBuilder().withDeviceId(deviceId)
                .withBundleName(getBundleName())
                .withAbilityName(CommonData.ABILITY_MAIN)
                .withAction(CommonData.GUESS_PAGE)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }

    private class ButtonClick implements Component.ClickedListener {
        @Override
        public void onClick(Component component) {
            int btnId = component.getId();
            switch (btnId) {
                case ResourceTable.Id_rule:
                    //showRule();
                    showRuleDialog();

                    break;
                case ResourceTable.Id_start:
                    getDevices();
                    break;
                default:
                    LogUtil.info(TAG, "Click default");
                    break;
            }
        }
    }
//弹窗显示规则
    private void showRuleDialog(){
        LogUtil.info(TAG, "进入游戏规则");
        CommonDialog commonDialog = new CommonDialog(this);
        //水平布局
        DirectionalLayout directionalLayout = new DirectionalLayout(getContext());
        commonDialog.setCornerRadius(DIALOG_BOX_CORNER_RADIUS);
        commonDialog.setAlignment(TextAlignment.CENTER);
        commonDialog.setSize(DIALOG_BOX_WIDTH, MATCH_CONTENT);
        // 设置布局大小
        directionalLayout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        directionalLayout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        directionalLayout.setOrientation(Component.VERTICAL);
        directionalLayout.setPadding(30, 30, 30, 30);
        //第一个Text
        Text text1 = new Text(getContext());
        text1.setText("游戏规则");
        text1.setTextSize(60);
        text1.setMarginTop(60);
        Text text2 = new Text(getContext());
        text2.setText("一、游戏人数:2-6人");
        text2.setTextSize(60);
        text1.setMarginTop(60);
        Text text3 = new Text(getContext());
        text3.setText("二、游戏规则");
        text3.setTextSize(60);
        text3.setMarginTop(60);
        Text text4 = new TextField(getContext());
        text4.setText("1.描述玩家选择一个词语，总共绘画时间为100秒，通过画画的形式将词语描述出来。请勿直接将答案写在画板上~");
        text4.setTextSize(50);
        text4.setEnabled(false);
        text4.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);;
        text4.setPadding(8,8,8,8);
        Text text5 = new TextField(getContext());
        text5.setText("2.猜词玩家需要猜测描述玩家画的内容，并将答案输出聊天框中发送，优先猜对的玩家会获得更高的分数。");
        text5.setTextSize(50);
        text5.setEnabled(false);
        Text text6 = new Text(getContext());
        text6.setText("三、结算规则");
        text6.setMarginTop(60);
        text6.setTextSize(60);
        Text text7 = new TextField(getContext());
        text7.setText("2.猜词玩家需要猜测描述玩家画的内容，并将答案输出聊天框中发送，优先猜对的玩家会获得更高的分数。");
        text7.setTextSize(50);
        text7.setEnabled(false);

        Button button = new Button(getContext());
        button.setText("确定");
        button.setTextColor(Color.WHITE);
        button.setPadding(50,20,50,20);
        button.setTextSize(70);
        button.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        button.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        ShapeElement background = new ShapeElement();
        background.setRgbColor(new RgbColor(143,195,31));
        background.setCornerRadius(25);
        button.setBackground(background);

        // 为组件添加对应布局的布局属性
        DirectionalLayout.LayoutConfig layoutConfig = new DirectionalLayout.LayoutConfig(ComponentContainer.LayoutConfig.MATCH_CONTENT, ComponentContainer.LayoutConfig.MATCH_CONTENT);
        layoutConfig.alignment = LayoutAlignment.HORIZONTAL_CENTER;
        text1.setLayoutConfig(layoutConfig);
        button.setLayoutConfig(layoutConfig);

        directionalLayout.addComponent(text1);
        directionalLayout.addComponent(text2);
        directionalLayout.addComponent(text3);
        directionalLayout.addComponent(text4);
        directionalLayout.addComponent(text5);
        directionalLayout.addComponent(text6);
        directionalLayout.addComponent(text7);
        directionalLayout.addComponent(button);

        button.setClickedListener(new Component.ClickedListener() {
            @Override
            // 在组件中增加对点击事件的检测
            public void onClick(Component component) {
                commonDialog.destroy();
            }
        });
        commonDialog.setContentCustomComponent(directionalLayout);
        commonDialog.show();
    }

}
