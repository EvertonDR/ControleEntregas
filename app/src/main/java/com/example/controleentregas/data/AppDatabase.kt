package com.example.controleentregas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClienteEntity::class,
        BairroEntity::class,
        EntregaEntity::class
    ],
    version = 7, // Forçar a recriação do banco de dados com os nomes corrigidos
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun bairroDao(): BairroDao
    abstract fun entregaDao(): EntregaDao

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
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
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
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Alto do Céu', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Alto do Mateus', 12.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Anatólia', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Bairro das Indústrias', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Bairro dos Estados', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Bairro dos Ipês', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Bairro dos Novais', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Bancários', 16.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Bessa', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Brisamar', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Cabo Branco', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Castelo Branco', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Centro', 12.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Cidade dos Colibris', 18.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Conj. Aspol', 30.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Costa do Sol', 30.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Costa e Silva', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Cristo Redentor', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Cruz das Armas', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Cuiá', 18.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Distrito Industrial', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Ernani Satiro', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Ernesto Geisel', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Esplanada', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Expedicionários', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Funcionários', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Gramame', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Grotão', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Ilha do Bispo', 10.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Intermares', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jaguaribe', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim 13 de Maio', 16.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jd Cid. Universitária', 16.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim Esther', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim Luna', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim Oceania', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim Planalto', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim São Paulo', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardim Veneza', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('João Agripino', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('João Paulo II', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('José Américo', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Manaíra', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Mandacaru', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Mangabeira', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Mangabeira 8', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Miramar', 18.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Muçumagro', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Oitizeiro', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Padre Zé', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Paratibe', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Pedro Gondim', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Penha', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Plan. da Boa Esperança', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Ponta dos Seixas', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Portal do Sol', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Renascer', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Róger', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Rangel', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('São José', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Tambaú', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Tambauzinho', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Tambiá', 15.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Torre', 20.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Trincheiras', 12.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Valentina Figueiredo', 25.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Varadouro', 12.0, 'João Pessoa')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Varjão', 15.0, 'João Pessoa')")

                // Santa Rita
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('7 Batalhão', 7.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Açude', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Alto das Populares', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Boa Vista', 10.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Centro', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Heitel Santiago', 15.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jardins', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jd. Carolina', 20.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Jd. Europa', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Lot. Nice', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Santo Amaro', 7.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Marcos Moura', 20.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Plano de Vida', 15.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Tibiri', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Tibiri Fábrica', 12.0, 'Santa Rita')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Várzea Nova', 10.0, 'Santa Rita')")

                // Bayeux
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Alto da Boa Vista', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Baralho', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Brasília', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Centro', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Comercial Norte', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Imaculada', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Aeroporto', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('São Severino', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('São Vicente', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Mário Andreazza', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Rio do Meio', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('São Bento', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Sesi', 6.0, 'Bayeux')")
                db.execSQL("INSERT INTO bairros (nome, valorEntrega, cidade) VALUES ('Tambay', 6.0, 'Bayeux')")
            }
        }
    }
}
