package db

import java.sql.Connection
import java.sql.DriverManager

object Database {

    private const val DB_URL = "jdbc:sqlite:mahasiswa.db"

    fun connect(): Connection {
        return DriverManager.getConnection(DB_URL)
    }

    fun createTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS mahasiswa (
                nim TEXT PRIMARY KEY,
                nama TEXT,
                nilai TEXT
            );
        """
        connect().use { conn ->
            conn.createStatement().use { it.execute(sql) }
        }
    }
}