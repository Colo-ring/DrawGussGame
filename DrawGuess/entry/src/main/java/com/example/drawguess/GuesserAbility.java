package com.example.drawguess;

import com.example.drawguess.slice.GuesserAbilitySlice;
import com.example.drawguess.slice.MainAbilitySlice;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class GuesserAbility extends Ability {
    private static final String TAG = CommonData.TAG + GuesserAbility.class.getSimpleName();

    @Override
    public void onStart(Intent intent) {
        LogUtil.info(TAG, "GuesserAbility::onStart");
        super.onStart(intent);
        super.setMainRoute(GuesserAbilitySlice.class.getName());
    }

    @Override
    public void onBackground() {
        super.onBackground();
        LogUtil.info(TAG, "GuesserAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.info(TAG, "GuesserAbility::onStop");
    }
}
