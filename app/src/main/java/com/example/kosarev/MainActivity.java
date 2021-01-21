package com.example.kosarev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    final List<User> users = new ArrayList<>();
    DataAdapter adapter;
    Spinner spinner;
    User currentUser;
    Button btnAdd, btnRead, btnClear, btnDel, btnUp;
    EditText etName, etEmail;

    DBHelper dbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        adapter = new DataAdapter(this, users);

        adapter.setSelectElementListener(new DataAdapter.SelectElementListener(){

            @Override
            public void selectElement(User user) {
                currentUser = user;
                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
            }
        });
        RecyclerView recyler = findViewById(R.id.listItem);
        recyler.setAdapter(adapter);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
    }


    public void onClick(View v) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                // подготовим данные для вставки в виде пар: наименование столбца - значение

                cv.put("name", name);
                cv.put("email", email);
                // вставляем запись и получаем ее ID
                long rowID = db.insert("mytable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                readBd(db);
                break;
            case R.id.btnRead:
                readBd(db);
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // удаляем все записи
                int clearCount = db.delete("mytable", null, null);
//                db.endTransaction();
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                users.clear();
                adapter.notifyDataSetChanged();

//                dbHelper = new DBHelper(this);
//                   readBd(dbHelper.getWritableDatabase());
               break;
            case R.id.btnDel:
                if (currentUser == null) {
                    break;
                }
                Log.d(LOG_TAG, "--- Delete from mytable: ---");
                // удаляем по id
                int delCount = db.delete("mytable", "id = " + currentUser.getId(), null);
                Log.d(LOG_TAG, "deleted rows count = " + delCount);
                readBd(db);
                break;

            case R.id.btnUp:
                if (currentUser == null) {
                    break;
                }
                Log.d(LOG_TAG, "--- Update mytable: ---");
                // подготовим значения для обновления
                cv.put("name", name);
                cv.put("email", email);
                // обновляем по id
                int updCount = db.update("mytable", cv, "id = ?",
                        new String[] {String.valueOf(currentUser.getId())});
                Log.d(LOG_TAG, "updated rows count = " + updCount);
                readBd(db);
                break;

        }
        // закрываем подключение к БД
        dbHelper.close();
    }

    private void readBd(SQLiteDatabase db) {
        users.clear();
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        String orderBy ="";
        String NAME_COLUMN = "name";
        String EMAIL_COLUMN = "email";
        String DESC = " DESC";
        switch ((int) spinner.getSelectedItemId())
        {
            case 0:
                orderBy = NAME_COLUMN;
                break;
            case 1:
                orderBy = NAME_COLUMN+DESC;
                break;
            case 2:
                orderBy = EMAIL_COLUMN;
                break;
            case 3:
                orderBy = EMAIL_COLUMN+DESC;
                break;
            default:
                break;
        }

// делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, orderBy);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int emailColIndex = c.getColumnIndex("email");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", email = " + c.getString(emailColIndex));
                users.add(new User(c.getString(emailColIndex), c.getString(nameColIndex), c.getInt(idColIndex)));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
            adapter.notifyDataSetChanged();
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
    }

    class DBHelper extends SQLiteOpenHelper {
        final int DBVersion = 2;
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            for (int i = 1; i < DBVersion; i++) {
                onUpgrade(db,i-1,i);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //если был совершен переход от первой версии ко второй
            if(oldVersion == 1 && newVersion == 2)
                //добавляем столбец "возраст"
                db.execSQL("alter table mytable add column age integer;");
        }
    }
}
