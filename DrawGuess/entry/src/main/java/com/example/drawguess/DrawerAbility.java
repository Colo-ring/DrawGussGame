package com.example.drawguess;

import com.example.drawguess.slice.DrawRemSlice;
import com.example.drawguess.slice.GuesserAbilitySlice;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class DrawerAbility extends Ability {
    private static final String TAG = CommonData.TAG + DrawerAbility.class.getSimpleName();

    @Override
    public void onStart(Intent intent) {
        LogUtil.info(TAG, "DrawerAbility::onStart");
        super.onStart(intent);
        super.setMainRoute(DrawRemSlice.class.getName());
    }

    @Override
    public void onBackground() {
        super.onBackground();
        LogUtil.info(TAG, "DrawerAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.info(TAG, "DrawerAbility::onStop");
    }
}
