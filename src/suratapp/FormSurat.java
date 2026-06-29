package suratapp;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class FormSurat extends JDialog {
    private SuratController controller;
    private Surat suratLama;
    private boolean isEdit;
    
    private JComboBox<String> cbJenis;
    private JTextField tfNomorSurat;
    private JSpinner spinnerTanggal;
    private JTextField tfPengirimPenerima;
    private JTextField tfPerihal;
    private JTextArea taIsi;
    private JButton btnSimpan;
    private JButton btnBatal;
    
    public FormSurat(JFrame parent, SuratController controller, Surat surat, boolean isEdit) {
        super(parent, isEdit ? "✏️ Edit Surat" : "✏️ Tulis Surat Baru", true);
        this.controller = controller;
        this.suratLama = surat;
        this.isEdit = isEdit;
        
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        
        if (isEdit && surat != null) {
            loadDataToForm();
        }
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Jenis Surat
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Jenis Surat:"), gbc);
        cbJenis = new JComboBox<>(new String[]{"Masuk", "Keluar"});
        gbc.gridx = 1;
        formPanel.add(cbJenis, gbc);
        row++;
        
        // Nomor Surat
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nomor Surat:"), gbc);
        tfNomorSurat = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(tfNomorSurat, gbc);
        row++;
        
        // Tanggal
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Tanggal:"), gbc);
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        spinnerTanggal = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerTanggal, "dd/MM/yyyy");
        spinnerTanggal.setEditor(dateEditor);
        gbc.gridx = 1;
        formPanel.add(spinnerTanggal, gbc);
        row++;
        
        // Pengirim/Penerima
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Pengirim/Penerima:"), gbc);
        tfPengirimPenerima = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(tfPengirimPenerima, gbc);
        row++;
        
        // Perihal
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Perihal:"), gbc);
        tfPerihal = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(tfPerihal, gbc);
        row++;
        
        // Isi Surat
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Isi Surat:"), gbc);
        taIsi = new JTextArea(5, 20);
        taIsi.setLineWrap(true);
        taIsi.setWrapStyleWord(true);
        JScrollPane scrollIsi = new JScrollPane(taIsi);
        gbc.gridx = 1;
        formPanel.add(scrollIsi, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSimpan = new JButton("💾 Simpan");
        btnBatal = new JButton("❌ Batal");
        
        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadDataToForm() {
        cbJenis.setSelectedItem(suratLama.getJenis());
        tfNomorSurat.setText(suratLama.getNomorSurat());
        
        Date date = Date.from(suratLama.getTanggal().atStartOfDay(ZoneId.systemDefault()).toInstant());
        spinnerTanggal.setValue(date);
        
        tfPengirimPenerima.setText(suratLama.getPengirimPenerima());
        tfPerihal.setText(suratLama.getPerihal());
        taIsi.setText(suratLama.getIsi());
    }
    
    private void simpan() {
        if (cbJenis.getSelectedItem() == null || tfNomorSurat.getText().trim().isEmpty() ||
            tfPengirimPenerima.getText().trim().isEmpty() || tfPerihal.getText().trim().isEmpty() ||
            taIsi.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Date selectedDate = (Date) spinnerTanggal.getValue();
        LocalDate tanggal = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        Surat suratBaru = new Surat(
            tfNomorSurat.getText().trim(),
            tanggal,
            (String) cbJenis.getSelectedItem(),
            tfPengirimPenerima.getText().trim(),
            tfPerihal.getText().trim(),
            taIsi.getText().trim()
        );
        
        if (isEdit && suratLama != null) {
            controller.editSurat(suratLama, suratBaru);
        } else {
            controller.tambahSurat(suratBaru);
        }
        
        JOptionPane.showMessageDialog(this, "Surat berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}