package com.example.timer2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Runnable, View.OnClickListener  {

    private long startTime;

    private TextView timerText;
    private Button startButton;

    private final Handler handler = new Handler();
    private volatile boolean stopRun = false;

    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss:SS", Locale.JAPAN);

    // データベース用
    public DatabaseHelper helper = null; // SQLiteOpenHelper
    SQLiteDatabase db = null; // データベース操作のためのSQLLiteDatebase
    private TextView hyouji;
    long diffTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        timerText.setText(dataFormat.format(0));

        Button stopButton = findViewById(R.id.button);
        stopButton.setOnClickListener((View.OnClickListener) this);

        hyouji = (TextView) findViewById(R.id.hyouji);

        // 画面を開いたと同時にタイマーを始める
        startTime = System.currentTimeMillis();
        Thread thread;
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void onClick(View v) {
        timerText.setText(dataFormat.format(diffTime));
        stopRun = true;

        // ストップボタンを押したら時間データを入力、表示させる
        // 入力
        if (helper == null) {
            helper = new DatabaseHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getWritableDatabase(); // dbに書き込みをするメソッド
            db = helper.getReadableDatabase(); // dbに読み込みをするメソッド
        }

        // insert用のメソッドを作成
        insertData(db, diffTime);

        // 読み込み
        // 読み込む内容用のメソッドを作成

        readData();
    }

    @Override
    public void run() {
        // 10 msec order
        int period = 10;

        while (!stopRun) {
            // sleep: period msec
            try {
                Thread.sleep(period);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                stopRun = true;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long endTime = System.currentTimeMillis();
                    // カウント時間 = 経過時間 - 開始時間
                    diffTime = (endTime - startTime);
                    timerText.setText(dataFormat.format(diffTime));
                }
            });
        }
    }

    // insert用メソッド
    private void insertData(SQLiteDatabase db, long timerstop){
        // execSQL() を使うことも可能だが、ContentValueを使ってマップデータ型のkeyとvalueとして扱い書き込みすることも可能
        ContentValues values = new ContentValues();
        values.put("timerstop", timerstop);

        // db.insert("追加するデータベース名", null(データを挿入する際Null値が許されない列に値が指定されていない場合代わりに利用される値を指定), "追加するデータ")
        db.insert("timerDB", null, values);
    }

    // 読み込み用内容記載メソッド
    private void readData(){

        // 読み込みにはqueryメソッドを使用する。rawQueryメソッドもあるが、これはSQL文をベタがきすればOK
        // 検索結果はCursorというインスタンスで返ってくるため、Cursor型の変数に検索結果値をいれる
        Cursor c = db.query(
                "timerDB", new String[] {"timerstop" },
                null,
                null,
                null,
                null,
                null
        );

        // table: テーブル名, columns: 検索結果に含める列名を指定。nullを指定すると全列の値が含まれる。
        // selection: 検索条件を指定, selectionArgs: 検索条件のパラメータ（？で指定）に置き換わる値を指定
        // groupBy: groupBy句を指定, having: having句を指定, orderBy: orderBy句を指定

        // 検索したデータの参照先を一番始めにする
        c.moveToFirst();

        // 検索結果の表示用にテキストデータを作成
        Long readTextSearch = Long.valueOf(0);
        for (int i = 0; i < c.getCount(); i++) {
            readTextSearch = c.getLong(0);
            c.moveToNext(); // 検索結果の次の行へ移動
        }

        // 検索が終了したらcloseメソッドを使ってcursorを閉じる
        c.close();

        hyouji.setText(dataFormat.format(readTextSearch));
    }
}
