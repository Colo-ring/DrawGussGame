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

package com.example.drawguess.utils;

import com.example.drawguess.DrawerAbility;
import com.example.drawguess.DrawerServiceAbility;
import com.example.drawguess.GuesserAbility;
import com.example.drawguess.MainAbility;
import ohos.distributedschedule.interwork.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * CommonData Util
 *
 * @since 2021-01-11
 */
public class CommonData {

    public static List<DeviceInfo> devices_list = new ArrayList<>();
    /**
     * MainAbility name
     */
    public static final String ABILITY_MAIN = MainAbility.class.getName();

    public static final String ABILITY_GUESSER = GuesserAbility.class.getName();

    public static final String ABILITY_DRAWER = DrawerAbility.class.getName();

    /**
     * DrawerService name
     */
    public static final String DRAWER_SERVICE_NAME = DrawerServiceAbility.class.getName(); //old MathGameService

    /**
     * LOG TAG
     */
    public static final String TAG = "[DrawGuess System] ";

    /**
     * math page action flag
     */
    public static final String DRAWER_PAGE = "action.system.drawer"; //MATH_PAGE

    /**
     * draw page action flag
     */
    public static final String DRAW_PAGE = "action.system.draw.rem";


    public static final String GUESS_PAGE= "action.system.guess";
    /**
     * math draw event action flag
     */
    public static final String DRAW_EVENT = "com.huawei.math.draw"; // MATH_DRAW_EVENT


    /**
     * key remote device id
     */
    public static final String KEY_REMOTE_DEVICEID = "remoteDeviceId";

    /**
     * isLocal
     */
    public static final String KEY_IS_LOCAL = "isLocal";

    /**
     * pointXs
     */
    public static final String KEY_POINT_X = "pointXs";

    /**
     * pointYs
     */
    public static final String KEY_POINT_Y = "pointYs";

    /**
     * isLastPoint
     */
    public static final String KEY_IS_LAST_POINT = "isLastPoint";

    /**
     * imageIndex
     */
    public static final String KEY_IMAGE_INDEX = "imageIndex";

    /**
     * moveImageId
     */
    public static final String KEY_MOVE_IMAGE_ID = "moveImageId";

    /**
     * movePosition
     */
    public static final String KEY_MOVE_POSITION = "movePosition";

    private CommonData() {
    }
}
