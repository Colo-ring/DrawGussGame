package com.example.drawguess;

import com.example.drawguess.slice.DrawRemSlice;
import com.example.drawguess.slice.GuesserAbilitySlice;
import com.example.drawguess.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import com.example.drawguess.utils.CommonData;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        addActionRoute(CommonData.DRAW_PAGE, DrawRemSlice.class.getName());
        addActionRoute(CommonData.GUESS_PAGE, GuesserAbilitySlice.class.getName());
    }
}
