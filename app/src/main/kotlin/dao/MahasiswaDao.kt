package dao

import db.Database
import model.Mahasiswa

class MahasiswaDao {

    fun insert(m: Mahasiswa) {
        val sql = "INSERT INTO mahasiswa (nim, nama, nilai) VALUES (?, ?, ?)"
        Database.connect().prepareStatement(sql).use {
            it.setString(1, m.nim)
            it.setString(2, m.nama)
            it.setString(3, m.nilai)
            it.executeUpdate()
        }
    }

    fun update(m: Mahasiswa) {
        val sql = "UPDATE mahasiswa SET nama=?, nilai=? WHERE nim=?"
        Database.connect().prepareStatement(sql).use {
            it.setString(1, m.nama)
            it.setString(2, m.nilai)
            it.setString(3, m.nim)
            it.executeUpdate()
        }
    }

    fun delete(nim: String) {
        val sql = "DELETE FROM mahasiswa WHERE nim=?"
        Database.connect().prepareStatement(sql).use {
            it.setString(1, nim)
            it.executeUpdate()
        }
    }

    fun getAll(): List<Mahasiswa> {
        val list = mutableListOf<Mahasiswa>()
        val sql = "SELECT * FROM mahasiswa"
        Database.connect().createStatement().use { st ->
            val rs = st.executeQuery(sql)
            while (rs.next()) {
                list.add(
                    Mahasiswa(rs.getString("nim"), rs.getString("nama"), rs.getString("nilai"))
                )
            }
        }
        return list
    }

    fun search(keyword: String): List<Mahasiswa> {
        val list = mutableListOf<Mahasiswa>()
        val sql = "SELECT * FROM mahasiswa WHERE nim LIKE ? OR nama LIKE ?"
        Database.connect().prepareStatement(sql).use {
            val key = "%$keyword%"
            it.setString(1, key)
            it.setString(2, key)
            val rs = it.executeQuery()
            while (rs.next()) {
                list.add(
                    Mahasiswa(rs.getString("nim"), rs.getString("nama"), rs.getString("nilai"))
                )
            }
        }
        return list
    }
}