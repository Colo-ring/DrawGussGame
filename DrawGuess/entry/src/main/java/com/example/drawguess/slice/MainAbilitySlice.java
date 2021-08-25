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

import static ohos.security.SystemPermission.DISTRIBUTED_DATASYNC;

import com.example.drawguess.ResourceTable;
import com.example.drawguess.devices.SelectDeviceDialog;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.bundle.IBundleManager;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;

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

    private static final int PERMISSION_CODE = 10000000;

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
                    showRule();
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

}
