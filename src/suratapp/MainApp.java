package suratapp;

import java.awt.print.*;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MainApp extends JFrame {
    private SuratController controller;
    private JList<Surat> listSurat;
    private JLabel lblStatus;
    private JComboBox<String> cbFilterJenis;
    private JComboBox<String> cbSort;
    private JTextField tfSearch;
    
    // Warna modern
    private final Color COLOR_BG = new Color(240, 242, 245);
    private final Color COLOR_WHITE = new Color(255, 255, 255);
    private final Color COLOR_PRIMARY = new Color(26, 115, 232);      // Biru untuk tombol utama
    private final Color COLOR_SUCCESS = new Color(52, 168, 83);      // Hijau untuk Download
    private final Color COLOR_WARNING = new Color(251, 140, 0);      // Orange untuk Edit
    private final Color COLOR_DANGER = new Color(234, 67, 53);       // Merah untuk Hapus
    private final Color COLOR_GRAY = new Color(128, 134, 139);       // Abu untuk Tutup
    private final Color COLOR_TEXT = new Color(32, 33, 36);
    private final Color COLOR_TEXT_SECONDARY = new Color(95, 99, 104);
    private final Color COLOR_BORDER = new Color(218, 220, 224);
    
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    
    public MainApp() {
        controller = new SuratController();
        initUI();
        updateStatus();
    }
    
    private void initUI() {
        setTitle("Aplikasi Arsip Surat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(COLOR_BG);
        
        JPanel toolbar = createToolbar();
        JPanel listPanel = createListPanel();
        
        lblStatus = new JLabel(" ");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblStatus.setFont(FONT_REGULAR);
        lblStatus.setForeground(COLOR_TEXT_SECONDARY);
        
        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(lblStatus, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        toolbar.setBackground(COLOR_WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        
        JButton btnTulis = createButton("Tulis Surat", Color.black);
        btnTulis.addActionListener(e -> bukaFormTambah());
        toolbar.add(btnTulis);
        
        toolbar.add(new JLabel("|"));
        
        JLabel lblFilter = new JLabel("Filter:");
        lblFilter.setFont(FONT_REGULAR);
        lblFilter.setForeground(COLOR_TEXT_SECONDARY);
        toolbar.add(lblFilter);
        
        cbFilterJenis = new JComboBox<>(new String[]{"Semua", "Masuk", "Keluar"});
        cbFilterJenis.setFont(FONT_REGULAR);
        cbFilterJenis.setBackground(COLOR_WHITE);
        cbFilterJenis.addActionListener(e -> {
            controller.setFilterJenis((String) cbFilterJenis.getSelectedItem());
            refreshList();
        });
        toolbar.add(cbFilterJenis);
        
        JLabel lblSort = new JLabel("Sortir:");
        lblSort.setFont(FONT_REGULAR);
        lblSort.setForeground(COLOR_TEXT_SECONDARY);
        toolbar.add(lblSort);
        
        cbSort = new JComboBox<>(new String[]{"Terbaru", "Terlama"});
        cbSort.setFont(FONT_REGULAR);
        cbSort.setBackground(COLOR_WHITE);
        cbSort.addActionListener(e -> {
            controller.setSortMode((String) cbSort.getSelectedItem());
            refreshList();
        });
        toolbar.add(cbSort);
        
        toolbar.add(Box.createHorizontalStrut(15));
        
        JLabel lblSearch = new JLabel("Cari:");
        lblSearch.setFont(FONT_REGULAR);
        lblSearch.setForeground(COLOR_TEXT_SECONDARY);
        toolbar.add(lblSearch);
        
        tfSearch = new JTextField(20);
        tfSearch.setFont(FONT_REGULAR);
        tfSearch.putClientProperty("JTextField.placeholderText", "Cari surat...");
        tfSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                controller.setSearchKeyword(tfSearch.getText());
                refreshList();
            }
        });
        toolbar.add(tfSearch);
        
        return toolbar;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        
        listSurat = new JList<>(controller.getListModel());
        listSurat.setCellRenderer(new SuratListRenderer());
        listSurat.setFixedCellHeight(90);
        listSurat.setBackground(COLOR_BG);
        listSurat.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        listSurat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Surat selected = listSurat.getSelectedValue();
                    if (selected != null) {
                        DetailSuratDialog detailDialog = new DetailSuratDialog(MainApp.this, selected);
                        detailDialog.setVisible(true);
                    }
                }
            }
        });
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit Surat");
        editItem.setFont(FONT_REGULAR);
        JMenuItem hapusItem = new JMenuItem("Hapus Surat");
        hapusItem.setFont(FONT_REGULAR);
        JMenuItem pdfItem = new JMenuItem("Download PDF");
        pdfItem.setFont(FONT_REGULAR);
        
        editItem.addActionListener(e -> {
            Surat selected = listSurat.getSelectedValue();
            if (selected != null) bukaFormEdit(selected);
        });
        hapusItem.addActionListener(e -> {
            Surat selected = listSurat.getSelectedValue();
            if (selected != null) hapusSurat(selected);
        });
        pdfItem.addActionListener(e -> {
            Surat selected = listSurat.getSelectedValue();
            if (selected != null) exportToFile(selected);
        });
        
        popupMenu.add(editItem);
        popupMenu.add(hapusItem);
        popupMenu.add(pdfItem);
        
        listSurat.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = listSurat.locationToIndex(e.getPoint());
                    if (index != -1) {
                        listSurat.setSelectedIndex(index);
                        popupMenu.show(listSurat, e.getX(), e.getY());
                    }
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = listSurat.locationToIndex(e.getPoint());
                    if (index != -1) {
                        listSurat.setSelectedIndex(index);
                        popupMenu.show(listSurat, e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scrollList = new JScrollPane(listSurat);
        scrollList.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), 
            "DAFTAR SURAT",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), 
            COLOR_TEXT
        ));
        scrollList.setBackground(COLOR_BG);
        scrollList.getViewport().setBackground(COLOR_BG);
        
        panel.add(scrollList, BorderLayout.CENTER);
        return panel;
    }
    
    private void bukaFormTambah() {
        FormSurat form = new FormSurat(this, controller, null, false);
        form.setVisible(true);
        refreshList();
    }
    
    private void bukaFormEdit(Surat surat) {
        FormSurat form = new FormSurat(this, controller, surat, true);
        form.setVisible(true);
        refreshList();
    }
    
    private void hapusSurat(Surat surat) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Hapus surat \"" + surat.getPerihal() + "\"?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.hapusSurat(surat);
            refreshList();
        }
    }
    
    private void exportToFile(Surat surat) {
        try {
            String filename = surat.getNomorSurat().replace("/", "_") + ".txt";
            FileWriter writer = new FileWriter(filename);
            PrintWriter pw = new PrintWriter(writer);
            
            pw.println("========================================");
            pw.println("           ARSIP SURAT");
            pw.println("========================================");
            pw.println();
            pw.println("Jenis Surat     : " + surat.getJenis());
            pw.println("Nomor Surat     : " + surat.getNomorSurat());
            pw.println("Tanggal         : " + surat.getTanggalFormatted());
            pw.println(surat.getJenis().equals("Masuk") ? "Pengirim        : " : "Penerima        : " + surat.getPengirimPenerima());
            pw.println("Perihal         : " + surat.getPerihal());
            pw.println();
            pw.println("Isi Surat:");
            pw.println(surat.getIsi());
            pw.println();
            pw.println("========================================");
            pw.close();
            
            JOptionPane.showMessageDialog(this, 
                "Surat berhasil diekspor ke:\n" + filename, 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal ekspor: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshList() {
        listSurat.repaint();
        updateStatus();
    }
    
    private void updateStatus() {
        int total = controller.getJumlahTampil();
        lblStatus.setText("Menampilkan " + total + " surat");
    }
    
    // ==================== CUSTOM CELL RENDERER ====================
    private class SuratListRenderer extends JPanel implements ListCellRenderer<Surat> {
        
        public SuratListRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends Surat> list, Surat surat, 
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();
            
            JPanel card = new JPanel(new BorderLayout(8, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            card.setBackground(COLOR_WHITE);
            
            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            
            JLabel lblPengirim = new JLabel(surat.getPengirimPenerima());
            lblPengirim.setFont(FONT_BOLD);
            lblPengirim.setForeground(COLOR_TEXT);
            
            JLabel lblTanggal = new JLabel(surat.getTanggalFormatted());
            lblTanggal.setFont(FONT_SMALL);
            lblTanggal.setForeground(COLOR_TEXT_SECONDARY);
            
            header.add(lblPengirim, BorderLayout.WEST);
            header.add(lblTanggal, BorderLayout.EAST);
            
            // Body
            JPanel body = new JPanel(new BorderLayout(0, 3));
            body.setOpaque(false);
            
            JLabel lblJudul = new JLabel(surat.getPerihal());
            lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblJudul.setForeground(COLOR_PRIMARY);
            
            JLabel lblPreview = new JLabel(surat.getPreview());
            lblPreview.setFont(FONT_SMALL);
            lblPreview.setForeground(COLOR_TEXT_SECONDARY);
            
            body.add(lblJudul, BorderLayout.NORTH);
            body.add(lblPreview, BorderLayout.SOUTH);
            
            card.add(header, BorderLayout.NORTH);
            card.add(body, BorderLayout.CENTER);
            
            if (isSelected) {
                card.setBackground(new Color(232, 240, 254));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
            card.setOpaque(true);
            
            add(card);
            return this;
        }
    }
    
    // ==================== DETAIL SURAT DIALOG ====================
    private class DetailSuratDialog extends JDialog {
        private Surat surat;
        
        public DetailSuratDialog(JFrame parent, Surat surat) {
            super(parent, "Detail Surat", true);
            this.surat = surat;
            setSize(550, 600);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initUI();
        }
        
        private void initUI() {
            getContentPane().setBackground(COLOR_WHITE);
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(COLOR_WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(COLOR_WHITE);
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                headerPanel.getBorder(),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
            ));
            
            JLabel lblHeader = new JLabel(surat.getJenis().equals("Masuk") ? "Surat Masuk" : "Surat Keluar");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblHeader.setForeground(COLOR_PRIMARY);
            headerPanel.add(lblHeader, BorderLayout.WEST);
            
            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(COLOR_WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 5, 8, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            addField(formPanel, gbc, "Nomor Surat:", surat.getNomorSurat(), row++);
            addField(formPanel, gbc, "Tanggal:", surat.getTanggalFormatted(), row++);
            String labelPengirim = surat.getJenis().equals("Masuk") ? "Pengirim:" : "Penerima:";
            addField(formPanel, gbc, labelPengirim, surat.getPengirimPenerima(), row++);
            addField(formPanel, gbc, "Perihal:", surat.getPerihal(), row++);
            
            // Isi Surat
            gbc.gridx = 0; gbc.gridy = row;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            formPanel.add(new JLabel("Isi Surat:"), gbc);
            
            JTextArea taIsi = new JTextArea(10, 30);
            taIsi.setText(surat.getIsi());
            taIsi.setLineWrap(true);
            taIsi.setWrapStyleWord(true);
            taIsi.setEditable(false);
            taIsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            taIsi.setBackground(new Color(250, 250, 250));
            JScrollPane scrollIsi = new JScrollPane(taIsi);
            gbc.gridx = 1;
            formPanel.add(scrollIsi, gbc);
            
            // Tombol dengan warna solid dan teks putih
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            buttonPanel.setBackground(COLOR_WHITE);
            buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));
            buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                buttonPanel.getBorder(),
                BorderFactory.createEmptyBorder(20, 0, 0, 0)
            ));
            
            JButton btnDownload = new JButton("Download PDF");
            btnDownload.setFont(FONT_BUTTON);
            btnDownload.setBackground(COLOR_SUCCESS);
            btnDownload.setForeground(Color.GREEN);
            btnDownload.setFocusPainted(false);
            btnDownload.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnDownload.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDownload.setOpaque(true);
            // Cari baris ini di dalam DetailSuratDialog:
            // Hapus action listener bawaanmu yang lama, ganti dengan ini:
            btnDownload.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Simpan Surat ke PDF");
                
                // Set default nama file
                String defaultName = "Surat_" + surat.getNomorSurat().replace("/", "-") + ".pdf";
                fileChooser.setSelectedFile(new File(defaultName));

                // Buka dialog "Save As..."
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    
                    // Pastikan akhirannya pakai .pdf
                    if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                        fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".pdf");
                    }
                    
                    // Eksekusi fungsi simpan PDF tanpa dialog print
                    simpanPDFLangsung(surat, fileToSave);
                }
            });
            
            JButton btnEdit = new JButton("Edit Surat");
            btnEdit.setFont(FONT_BUTTON);
            btnEdit.setBackground(COLOR_WARNING);
            btnEdit.setForeground(Color.YELLOW);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEdit.setOpaque(true);
            btnEdit.addActionListener(e -> {
                dispose();
                bukaFormEdit(surat);
            });
            
            JButton btnHapus = new JButton("Hapus Surat");
            btnHapus.setFont(FONT_BUTTON);
            btnHapus.setBackground(COLOR_DANGER);
            btnHapus.setForeground(Color.RED);
            btnHapus.setFocusPainted(false);
            btnHapus.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnHapus.setOpaque(true);
            btnHapus.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Hapus surat \"" + surat.getPerihal() + "\"?", 
                    "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.hapusSurat(surat);
                    dispose();
                    refreshList();
                }
            });

            JButton btnTutup = new JButton("Tutup");
            btnTutup.setFont(FONT_BUTTON);
            btnTutup.setBackground(COLOR_GRAY);
            btnTutup.setForeground(Color.GRAY);
            btnTutup.setFocusPainted(false);
            btnTutup.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnTutup.setOpaque(true);
            btnTutup.addActionListener(e -> dispose());
            
            buttonPanel.add(btnDownload);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnHapus);
            buttonPanel.add(btnTutup);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private void addField(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
            gbc.gridx = 0; gbc.gridy = row;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel lbl = new JLabel(label);
            lbl.setFont(FONT_BOLD);
            panel.add(lbl, gbc);
            
            gbc.gridx = 1;
            JLabel val = new JLabel(value);
            val.setFont(FONT_REGULAR);
            panel.add(val, gbc);
        }

        private void simpanPDFLangsung(Surat surat, File fileToSave) {
            PrinterJob job = PrinterJob.getPrinterJob();
            
            job.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                    if (pageIndex > 0) return NO_SUCH_PAGE;

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    g2d.setColor(Color.BLACK);

                    int y = 60;
                    int x = 50;

                    // --- HEADER SURAT ---
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    g2d.drawString("ARSIP SURAT ELEKTRONIK", x, y);
                    y += 10;
                    g2d.drawLine(x, y, (int) pageFormat.getImageableWidth() - x, y);
                    y += 30;

                    // --- DETAIL SURAT ---
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString("Jenis Surat", x, y);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(":  " + surat.getJenis(), x + 120, y);
                    y += 20;

                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString("Nomor Surat", x, y);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(":  " + surat.getNomorSurat(), x + 120, y);
                    y += 20;

                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString("Tanggal", x, y);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(":  " + surat.getTanggalFormatted(), x + 120, y);
                    y += 20;

                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString("Pengirim/Penerima", x, y);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(":  " + surat.getPengirimPenerima(), x + 120, y);
                    y += 20;

                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString("Perihal", x, y);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(":  " + surat.getPerihal(), x + 120, y);
                    y += 35;

                    // --- ISI SURAT ---
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString("Isi Surat :", x, y);
                    y += 20;

                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    String[] lines = surat.getIsi().split("\n");
                    for (String line : lines) {
                        g2d.drawString(line, x, y);
                        y += 18;
                    }
                    return PAGE_EXISTS;
                }
            });

            try {
                // Trik: Cari printer PDF virtual otomatis bawaan komputer (Microsoft Print to PDF)
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                PrintService pdfPrinter = null;
                for (PrintService service : services) {
                    if (service.getName().toLowerCase().contains("pdf")) {
                        pdfPrinter = service;
                        break;
                    }
                }

                if (pdfPrinter != null) {
                    job.setPrintService(pdfPrinter);
                    
                    // Beri instruksi ke layanan print untuk langsung lempar datanya ke file, BUKAN ke printer fisik/jendela pop-up
                    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                    attributes.add(new Destination(fileToSave.toURI()));
                    
                    job.print(attributes); // Mengeksekusi secara rahasia di background
                    JOptionPane.showMessageDialog(this, "Surat berhasil disimpan ke PDF di:\n" + fileToSave.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Tidak ada layanan Virtual PDF di komputermu.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Waduh, gagal membuat PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainApp().setVisible(true);
        });
    }
}