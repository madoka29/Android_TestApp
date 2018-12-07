package com.example.timer2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelperを継承したクラスを作成し、データベースを作成する

public class DatabaseHelper extends SQLiteOpenHelper {

    // SQLiteOpenHelperには以下の4つの値のコンストラクタが必要
    // context, name(データベースの名前), カーソルのファクトリー, バージョン番号
    // カーソルのファクトリー(null): あんまり利用しないのでnullでOK。時間あれば調べる。
    // バージョン番号(はじめは1): ここのバージョンはデータベースのバージョン。例えばアプリをアップデートするときテーブル追加もすることがあるかもしれないが、そのときにイチからデータベースを構成するのではなく、onUpgradeにてアップグレードする
    public DatabaseHelper(Context context) {
        // インスタンスを生成するときに4つの値の引数を与えても良いが、今回はcontext以外は設定しておく
        super(context, "testDB", null, 1);
    }

    // データベースを作成する際に呼び出されるメソッド
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        db.execSQL("CREATE TABLE timerDB (id INTEGER PRIMARY KEY, timerstop LONG)"); // execSQLに実行するSQL文を書く
    }

    // データベースをアップグレードする際に呼び出されるメソッド
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
