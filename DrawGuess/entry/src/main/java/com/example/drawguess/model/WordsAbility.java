package com.example.drawguess.model;

import com.example.drawguess.ResourceTable;
import com.example.drawguess.slice.MainAbilitySlice;
import com.example.drawguess.utils.CommonData;
import com.example.drawguess.utils.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.app.AbilityContext;
import ohos.data.resultset.ResultSet;
import ohos.rpc.IRemoteObject;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;
import java.util.ArrayList;

public class WordsAbility extends Ability {
    private static final String TAG = CommonData.TAG + WordsAbility.class.getSimpleName();
    private ArrayList<sqliteData> result = new ArrayList<sqliteData>();

    private readSqliteFile readsqlite;
    private AbilityContext context;
    private sqliteData sqldata;

    public WordsAbility(AbilityContext context) {
        this.context = context;
        System.out.println("WordsAbility 构造函数======================================================");
        System.out.println("WordsAbility context ======================================================" + context.toString());
        getRadomWord();
    }

    private void getRadomWord(){
        System.out.println("getRadomWord 正确调用======================================================");
        readsqlite = new readSqliteFile(context);
        try{
            readsqlite.init();
        }catch (IOException e){
            terminateAbility();
        }
        ResultSet resultSet = readsqlite.search();


        while(resultSet.goToNextRow()){
            sqldata = new sqliteData();
            sqldata.no = resultSet.getInt(0);
            sqldata.wordName = resultSet.getString(1);
            sqldata.wordHint = resultSet.getString(2);
            result.add(sqldata);
        }
        resultSet.close();
    }

    public String getFirstWord() {
        return result.get(0).wordName;
    }
    public String getSecendWord() {
        return result.get(1).wordName;
    }
    public String getThirdWord() {
        return result.get(2).wordName;
    }
    public String getForthWord() {
        return result.get(3).wordName;
    }
    public String getFirstHint() {
        LogUtil.info(TAG, "WordsAbility::getFirstHint");
        return result.get(0).wordHint;
    }
    public String getSecendHint() {
        return result.get(1).wordHint;
    }
    public String getThirdHint() {
        return result.get(2).wordHint;
    }
    public String getForthHint() {
        return result.get(3).wordHint;
    }
}