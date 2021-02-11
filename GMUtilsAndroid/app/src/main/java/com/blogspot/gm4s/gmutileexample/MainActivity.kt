package com.blogspot.gm4s.gmutileexample

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.blogspot.gm4s1.gmutils.MyToast
import com.blogspot.gm4s1.gmutils.database.BaseDatabase
import com.blogspot.gm4s1.gmutils.database.annotations.Default
import com.blogspot.gm4s1.gmutils.database.annotations.Ignore
import com.blogspot.gm4s1.gmutils.database.annotations.PrimaryKey
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.text = "Show contact editor"
        btn1.setOnClickListener {
            val intent = Intent(this, ContactEditorActivity::class.java)
            startActivity(intent)
        }

        btn2.text = "read log file"
        btn2.setOnClickListener {
            val intent = Intent(this, ReadLogFileActivity::class.java)
            startActivity(intent)
        }

        btn3.text = "show my toast"
        btn3.setOnClickListener {
            MyToast.show(this, "test my toast")
        }

        btn4.text = "show original toast"
        btn4.setOnClickListener {
            Toast.makeText(this, "test original toast", Toast.LENGTH_LONG).show()
        }

        btn5.text = "test BaseDatabase class"
        btn5.setOnClickListener {
            val db = db(this)

            db.insert(
                Entity2::class.java,
                ContentValues().apply {
                    this.put("intField2", 2212122)
                }
            )

            db.insert(
                Entity2::class.java,
                ContentValues().apply {
                    this.put("intField2", null as? Int)
                }
            )

            db.insert(listOf(
                Entity1(
                    "123", 110, 120, 130f, 140.0, "150", true, null, null, null, null, null, null
                ).apply {
                    longField0 = 7654356786543567
                }
            ))

            db.insert(listOf(
                Entity1(
                    "124", 111, 121, 131f, 141.0, "151", true, 171, null, 191f, null, "201", null
                )
            ))

            db.insert(listOf(
                Entity1(
                    "124", 111, 121, 131f, 141.0, "151", true, 171, null, 191f, null, "201", null
                )
            ), true)

            var query1 = db.select(
                Entity1::class.java,
                object : TypeToken<List<Entity1>>() {},
                null,
                null
            )

            val query2 = db.select(
                Entity2::class.java,
                object : TypeToken<List<Entity2>>() {},
                null,
                null
            )

            Log.e("****", query1.toString())
            Log.e("****", query1.last().intField0.toString())
            Log.e("****", query2.toString())
            Log.e("****", query2.last().intField2?.toString() ?: "")

            db.update(
                Entity1(
                    "124", 111, 565665, 988998f, 123456.0, "mncbnmmnbv bvbv", true, 17231, null, 12391f, null, "20231", null
                )
            )

            query1 = db.select(
                Entity1::class.java,
                object : TypeToken<List<Entity1>>() {},
                null,
                null
            )

            Log.e("****", query1.toString())
            Log.e("****", query1.last().intField0.toString())

            db.delete(
                Entity1(
                    "124", 111, 1232231, 13231f, 14231.0, "15231", true, 17231, null, 12391f, null, null, null
                )
            )

            db.delete(Entity1::class.java, "${Entity1::id.name}=123")

            db.insert(listOf(
                Entity1(
                    "1240421", 111, 121, 131f, 141.0, "151", true, 171, null, 191f, null, "201", null
                )
            ))

            query1 = db.select(
                Entity1::class.java,
                object : TypeToken<List<Entity1>>() {},
                null,
                null
            )

            Log.e("****", query1.toString())
            Log.e("****", query1.last().intField0.toString())

            val selectSpecial =
                db.selectSpecial(Entity1::class.java, arrayOf("count(*) as count"), null, null)

            val entityCount = db.getEntityCount(Entity1::class.java, null)

            val sqlQuery = db.sqlQuery("select count(*) as count from ${Entity1::class.java.simpleName}")

            Log.e("****", selectSpecial.toString())
            Log.e("****", entityCount.toString())
            Log.e("****", sqlQuery.toString())
        }

    }

}

open class Entity0(
    val intField0: Int,
) {
    var longField0: Long? = null
}

data class Entity1(
    @PrimaryKey
    val id: String,

    val intField1: Int,
    val longField: Long,

    val floatField: Float,
    val doubleField: Double,

    val stringField: String,

    val booleanField: Boolean,

    val intField1Nullable: Int?,
    val longFieldNullable: Long?,

    val floatFieldNullable: Float?,
    val doubleFieldNullable: Double?,

    @Default("default of stringFieldNullable")
    val stringFieldNullable: String?,

    val booleanFieldNullable: Boolean?,

) : Entity0(intField1) {

    @Ignore
    var longField1: Long? = null
}

data class Entity2(
    @PrimaryKey
    val id: String,
    val intField2: Int?
)

class db(context: Context) : BaseDatabase(context) {
    override fun databaseName(): String {
        return "db";
    }

    override fun databaseVersion(): Int {
        return 16;
    }

    override fun databaseEntities(): Array<Class<*>> {
        return arrayOf(
            Entity1::class.java,
            Entity2::class.java
        )
    }

}