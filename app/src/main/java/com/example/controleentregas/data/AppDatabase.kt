package com.example.controleentregas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClienteEntity::class,
        BairroEntity::class,
        EntregaEntity::class,
        CustoEntity::class
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun bairroDao(): BairroDao
    abstract fun entregaDao(): EntregaDao
    abstract fun custoDao(): CustoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "controle_entregas_db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_8)
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }\n
        // Migrações
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE entregas ADD COLUMN realizada INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE entregas ADD COLUMN cidade TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE bairros ADD COLUMN cidade TEXT NOT NULL DEFAULT ''")
            }
        }
        
        val MIGRATION_4_8 = object : Migration(4, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `custos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nome` TEXT NOT NULL, `valor` REAL NOT NULL)")
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                 CoroutineScope(Dispatchers.IO).launch {
                    // Clientes
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Makeme')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Maison')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Karol')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Sensualize')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('N1')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Racco')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Ana Leao')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Carolis')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Mar Lima')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Ligia')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Maricota')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Glow')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Ana Praia')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Larocca')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Donna Bruna')")
                    db.execSQL("INSERT INTO clientes (nome) VALUES ('Dona Moça')")

                    // Bairros - João Pessoa
                    db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Aeroclube', 20.0, 'João Pessoa')")
                    db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Água Fria', 18.0, 'João Pessoa')")
                    db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Altiplano', 20.0, 'João Pessoa')")
                    // ... (restante do código de pré-cadastro)
                 }
            }
        }
    }
}
