package com.blogspot.gm4s.gmutileexample

import android.content.ContentValues
import android.content.Context
import android.util.Log
import gmutils.database.BaseDatabase
import gmutils.database.annotations.Default
import gmutils.database.annotations.Ignore
import gmutils.database.annotations.PrimaryKey
import gmutils.database.sqlitecommands.WhereClause
import com.google.gson.reflect.TypeToken


class DB(context: Context) : BaseDatabase(context) {
    override fun databaseName(): String {
        return "db"
    }

    override fun databaseVersion(): Int {
        return 18
    }

    override fun databaseEntities(): Array<Class<*>> {
        return arrayOf(
            Entity1::class.java,
            Entity2::class.java
        )
    }

    fun test() {
        insert(
            Entity2::class.java,
            ContentValues().apply {
                this.put("intField2", 2212122)
            }
        )

        insert(
            Entity2::class.java,
            ContentValues().apply {
                this.put("intField2", null as? Int)
            }
        )

        insert(listOf(
            Entity1(
                "123",
                110,
                120,
                130f,
                140.0,
                "150",
                true,
                null,
                null,
                null,
                null,
                null,
                null
            ).apply {
                longField0 = 7654356786543567
            }
        ))

        insert(
            listOf(
                Entity1(
                    "124",
                    111,
                    121,
                    131f,
                    141.0,
                    "151",
                    true,
                    171,
                    null,
                    191f,
                    null,
                    "201",
                    null
                )
            )
        )

        insert(
            listOf(
                Entity1(
                    "124",
                    111,
                    121,
                    131f,
                    141.0,
                    "151",
                    true,
                    171,
                    null,
                    191f,
                    null,
                    "201",
                    null
                )
            ), true
        )

        var query1 = select(
            Entity1::class.java,
            object : TypeToken<List<Entity1>>() {}
        )

        val query2 = select(
            Entity2::class.java,
            object : TypeToken<List<Entity2>>() {}
        )

        val query3 = select(
            Entity1::class.java,
            WhereClause().append(WhereClause.Clause(Entity2::id.name, WhereClause.CompareOperator.Equal, "124")),
            null
        )

        val query4 = select(
            Entity1::class.java,
            WhereClause().append(WhereClause.Clause(Entity2::id.name, WhereClause.CompareOperator.Equal, "12555")),
            null
        )

        Log.e("****", query1.toString())
        Log.e("****", query1.last().intField0.toString())
        Log.e("****", query2.toString())
        Log.e("****", query2.last().intField2?.toString() ?: "")
        Log.e("****", (query3 ?: "").toString())
        Log.e("****", (query4 ?: "").toString())

        update(
            Entity1(
                "124",
                111,
                565665,
                988998f,
                123456.0,
                "mncbnmmnbv bvbv",
                true,
                17231,
                null,
                12391f,
                null,
                "20231",
                null
            )
        )

        query1 = select(
            Entity1::class.java,
            object : TypeToken<List<Entity1>>() {}
        )

        Log.e("****", query1.toString())
        Log.e("****", query1.last().intField0.toString())

        delete(
            Entity1(
                "124",
                111,
                1232231,
                13231f,
                14231.0,
                "15231",
                true,
                17231,
                null,
                12391f,
                null,
                null,
                null
            )
        )

        delete(Entity1::class.java, "${Entity1::id.name}=123")

        insert(
            listOf(
                Entity1(
                    "1240421",
                    111,
                    121,
                    131f,
                    141.0,
                    "151",
                    true,
                    171,
                    null,
                    191f,
                    null,
                    "201",
                    null
                )
            )
        )

        query1 = select(
            Entity1::class.java,
            object : TypeToken<List<Entity1>>() {}
        )

        Log.e("****", query1.toString())
        Log.e("****", query1.last().intField0.toString())

        val selectSpecial = selectSpecial(Entity1::class.java, arrayOf("count(*) as count"))

        val entityCount = getEntityCount(Entity1::class.java)

        val sqlQuery =
            sqlQuery("select count(*) as count from ${Entity1::class.java.simpleName}")

        Log.e("****", selectSpecial.toString())
        Log.e("****", entityCount.toString())
        Log.e("****", sqlQuery.toString())
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
) {
    companion object {
        const val vvvvvvvvvvvvvvv: String = "ll"
    }
}

