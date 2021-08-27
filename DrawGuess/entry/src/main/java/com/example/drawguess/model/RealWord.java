package com.example.drawguess.model;

public class RealWord {
    private volatile static RealWord singleton;
    private static String CHOOSEDWORD;
    private static String CHOOSEDHINT;
    private RealWord (){}
    public static RealWord getSingleton() {
        if (singleton == null) {
            synchronized (RealWord.class) {
                if (singleton == null) {
                    singleton = new RealWord();
                }
            }
        }
        return singleton;
    }

    public String getChoosedWord(){
        return CHOOSEDWORD;
    }

    public String getChoosedHint(){
        return CHOOSEDHINT;
    }

    public void setChoosedWord(String CHOOSEDWORD) {
        RealWord.CHOOSEDWORD = CHOOSEDWORD;
    }

    public void setChoosedHint(String CHOOSEDHINT) {
        RealWord.CHOOSEDHINT = CHOOSEDHINT;
    }
}