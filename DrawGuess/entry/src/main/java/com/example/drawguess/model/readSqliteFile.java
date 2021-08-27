package com.example.drawguess.model;

import com.example.drawguess.utils.CommonUtil;
import ohos.app.AbilityContext;
import ohos.data.DatabaseHelper;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;
import ohos.data.resultset.ResultSet;
import ohos.global.resource.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class readSqliteFile {
    private AbilityContext context;
    private File dirPath;
    private File dbPath;
    private RdbStore store;
    private StoreConfig config = StoreConfig.newDefaultConfig("PremierLeague.sqlite");
    private static final RdbOpenCallback callback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {}

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {}
    };
    public readSqliteFile(AbilityContext context)
    {
        System.out.println("readSqliteFile 正确调用======================================================");
        this.context = context;
        System.out.println("readSqliteFile context ======================================================" + context.toString());
        dirPath = new File(context.getDataDir().toString() + "/MainAbility/databases/db");
        System.out.println("dirPath++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + dirPath);
        if(!dirPath.exists()){
            dirPath.mkdirs();
        }
        dbPath = new File(Paths.get(dirPath.toString(),"PremierLeague.sqlite").toString());
        System.out.println("dbPath++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + dbPath);
    }
    private void extractDB() throws IOException{
        Resource resource = context.getResourceManager().getRawFileEntry("resources/rawfile/PremierLeague.sqlite").openRawFile();
        if(dbPath.exists()){
            dbPath.delete();
        }
        FileOutputStream fos = new FileOutputStream(dbPath);
        byte[] buffer = new byte[4096];
        int count = 0;
        while((count = resource.read(buffer)) >= 0){
            fos.write(buffer,0,count);
        }
        resource.close();
        fos.close();
    }
    public void init() throws IOException{
        extractDB();
        DatabaseHelper helper = new DatabaseHelper(context);
        store = helper.getRdbStore(config,1,callback,null);
    }
//    public ArrayList<sqliteData> search(){
//        int randomIndex = CommonUtil.getRandomInt(5);
//        String sql = "SELECT * FROM word ORDER BY RANDOM() limit 4";
//        ResultSet resultSet = store.querySql(sql,null);
//
//        ArrayList<sqliteData> result = new ArrayList<sqliteData>();
//        while(resultSet.goToNextRow()){
//            sqliteData sqldata = new sqliteData();
//            sqldata.no = resultSet.getInt(0);
//            sqldata.wordName = resultSet.getString(1);
//            sqldata.wordHint = resultSet.getString(2);
//            result.add(sqldata);
//        }
//        resultSet.close();
//        return result;
//    }
    public ResultSet search(){
        String sql = "SELECT * FROM word ORDER BY RANDOM() limit 4";
        return store.querySql(sql,null);
    }

}