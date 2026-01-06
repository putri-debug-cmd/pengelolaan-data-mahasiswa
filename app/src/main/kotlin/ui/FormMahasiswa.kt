package ui

import dao.MahasiswaDao
import db.Database
import model.Mahasiswa
import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*

class FormMahasiswa : JFrame() {

    private val tableModel = DefaultTableModel(arrayOf("NIM", "Nama", "Nilai"), 0)
    private val dao = MahasiswaDao()

    init {
        title = "Pengelolaan Data Mahasiswa"
        setSize(1000, 600)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE

        Database.createTable()

        val fontLabel = Font("Arial", Font.BOLD, 16)
        val fontInput = Font("Arial", Font.PLAIN, 16)
        val fontTable = Font("Arial", Font.PLAIN, 14)
        val fontTitle = Font("Arial", Font.BOLD, 24)

        // ===== Judul =====
        val titlePanel = JPanel(BorderLayout())
        titlePanel.background = Color(245,245,245)
        val titleLabel = JLabel("Aplikasi Pengelolaan Data Mahasiswa", SwingConstants.CENTER)
        titleLabel.font = fontTitle
        titlePanel.add(titleLabel, BorderLayout.CENTER)
        titlePanel.border = BorderFactory.createEmptyBorder(20,0,20,0)

        // ===== Form input + tombol Simpan =====
        val formPanel = JPanel(GridBagLayout())
        formPanel.background = Color(245, 245, 245)
        val gbc = GridBagConstraints()
        gbc.insets = Insets(10,10,10,10)
        gbc.fill = GridBagConstraints.HORIZONTAL

        val txtNim = JTextField(25)
        txtNim.font = fontInput
        val txtNama = JTextField(25)
        txtNama.font = fontInput
        val txtNilai = JTextField(25)
        txtNilai.font = fontInput
        val txtSearch = JTextField(15) // lebih pendek, proporsional
        txtSearch.font = fontInput

        // NIM
        gbc.gridx = 0
        gbc.gridy = 0
        formPanel.add(JLabel("NIM:"), gbc)
        gbc.gridx = 1
        formPanel.add(txtNim, gbc)

        // Nama
        gbc.gridx = 0
        gbc.gridy = 1
        formPanel.add(JLabel("Nama:"), gbc)
        gbc.gridx = 1
        formPanel.add(txtNama, gbc)

        // Nilai
        gbc.gridx = 0
        gbc.gridy = 2
        formPanel.add(JLabel("Nilai:"), gbc)
        gbc.gridx = 1
        formPanel.add(txtNilai, gbc)

        // Tombol Simpan
        val btnSimpan = JButton("Simpan")
        btnSimpan.background = Color(60,179,113)
        btnSimpan.foreground = Color.WHITE
        btnSimpan.font = Font("Arial", Font.BOLD, 16)
        gbc.gridx = 1
        gbc.gridy = 3
        gbc.anchor = GridBagConstraints.LINE_START
        formPanel.add(btnSimpan, gbc)

        // ===== Search panel =====
        val searchPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0))
        searchPanel.background = Color(245, 245, 245)
        val btnSearch = JButton("Cari")
        btnSearch.background = Color(123,104,238)
        btnSearch.foreground = Color.WHITE
        btnSearch.font = Font("Arial", Font.BOLD, 16)
        searchPanel.add(JLabel("Search:"))
        searchPanel.add(txtSearch)
        searchPanel.add(btnSearch)

        // ===== Tabel =====
        val table = JTable(tableModel)
        table.rowHeight = 28
        table.font = fontTable
        table.selectionBackground = Color(100,149,237)
        table.background = Color(245,245,245)
        table.tableHeader.font = Font("Arial", Font.BOLD, 14)
        table.tableHeader.background = Color(100,149,237)
        table.tableHeader.foreground = Color.WHITE
        table.selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        val scrollPane = JScrollPane(table)
        scrollPane.border = BorderFactory.createEmptyBorder(10,20,10,20)

        // ===== Tombol Ubah, Hapus, Reset =====
        val btnUbah = JButton("Ubah")
        val btnHapus = JButton("Hapus")
        val btnReset = JButton("Reset")
        val buttonColors = listOf(Color(255,165,0), Color(220,20,60), Color(70,130,180))
        val buttons = listOf(btnUbah, btnHapus, btnReset)
        for(i in buttons.indices){
            buttons[i].background = buttonColors[i]
            buttons[i].foreground = Color.WHITE
            buttons[i].font = Font("Arial", Font.BOLD, 16)
        }
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 15,5))
        buttons.forEach { buttonPanel.add(it) }

        // ===== Main panel =====
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        mainPanel.add(titlePanel)
        mainPanel.add(formPanel)
        mainPanel.add(searchPanel)
        mainPanel.add(scrollPane)
        mainPanel.add(buttonPanel)
        add(mainPanel)

        // ===== Event Simpan =====
        btnSimpan.addActionListener {
            val nim = txtNim.text
            val nama = txtNama.text
            val nilai = txtNilai.text
            if(nim.isEmpty() || nama.isEmpty() || nilai.isEmpty()){
                JOptionPane.showMessageDialog(this,"Isi semua form dulu")
            } else {
                val mhs = Mahasiswa(nim,nama,nilai)
                dao.insert(mhs)
                tableModel.addRow(arrayOf(nim,nama,nilai))
                JOptionPane.showMessageDialog(this,"Data berhasil disimpan")
                resetForm(txtNim, txtNama, txtNilai)
            }
        }

        // ===== Table selection =====
        table.selectionModel.addListSelectionListener {
            val row = table.selectedRow
            if(row>=0){
                txtNim.text = tableModel.getValueAt(row,0).toString()
                txtNama.text = tableModel.getValueAt(row,1).toString()
                txtNilai.text = tableModel.getValueAt(row,2).toString()
            }
        }

        // Klik area kosong tabel batal seleksi
        scrollPane.viewport.addMouseListener(object : java.awt.event.MouseAdapter(){
            override fun mouseClicked(e: java.awt.event.MouseEvent){
                val row = table.rowAtPoint(e.point)
                if(row==-1){
                    table.clearSelection()
                    resetForm(txtNim, txtNama, txtNilai)
                }
            }
        })

        // Event Ubah
        btnUbah.addActionListener {
            val rows = table.selectedRows
            if(rows.size==1){
                val row = rows[0]
                val nim = txtNim.text
                val nama = txtNama.text
                val nilai = txtNilai.text
                if(nim.isEmpty() || nama.isEmpty() || nilai.isEmpty()){
                    JOptionPane.showMessageDialog(this,"Isi semua form dulu")
                } else {
                    val mhs = Mahasiswa(nim,nama,nilai)
                    dao.update(mhs)
                    tableModel.setValueAt(nim,row,0)
                    tableModel.setValueAt(nama,row,1)
                    tableModel.setValueAt(nilai,row,2)
                    JOptionPane.showMessageDialog(this,"Data berhasil diubah")
                }
            } else {
                JOptionPane.showMessageDialog(this,"Pilih satu baris untuk diubah")
            }
        }

        // Event Hapus (multi-delete)
        btnHapus.addActionListener {
            val rows = table.selectedRows
            if(rows.isNotEmpty()){
                val confirm = JOptionPane.showConfirmDialog(this,"Yakin ingin menghapus ${rows.size} data?","Konfirmasi",JOptionPane.YES_NO_OPTION)
                if(confirm==JOptionPane.YES_OPTION){
                    for(i in rows.size-1 downTo 0){
                        val nim = tableModel.getValueAt(rows[i],0).toString()
                        dao.delete(nim)
                        tableModel.removeRow(rows[i])
                    }
                    resetForm(txtNim,txtNama,txtNilai)
                    JOptionPane.showMessageDialog(this,"Data berhasil dihapus")
                }
            } else {
                JOptionPane.showMessageDialog(this,"Pilih data di tabel dulu")
            }
        }

        // Event Reset
        btnReset.addActionListener {
            resetForm(txtNim,txtNama,txtNilai)
            table.clearSelection()
        }

        // Event Search
        btnSearch.addActionListener {
            val keyword = txtSearch.text
            val result = dao.search(keyword)
            tableModel.rowCount=0
            for(m in result){
                tableModel.addRow(arrayOf(m.nim,m.nama,m.nilai))
            }
        }

        isVisible = true
    }

    private fun resetForm(txtNim:JTextField, txtNama:JTextField, txtNilai:JTextField){
        txtNim.text = ""
        txtNama.text = ""
        txtNilai.text = ""
    }
}