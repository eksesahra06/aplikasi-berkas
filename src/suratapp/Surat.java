package suratapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Surat {
    private String nomorSurat;
    private LocalDate tanggal;
    private String jenis; // "Masuk" atau "Keluar"
    private String pengirimPenerima;
    private String perihal;
    private String isi;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    
    public Surat(String nomorSurat, LocalDate tanggal, String jenis, 
                 String pengirimPenerima, String perihal, String isi) {
        this.nomorSurat = nomorSurat;
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.pengirimPenerima = pengirimPenerima;
        this.perihal = perihal;
        this.isi = isi;
    }
    
    // Getters
    public String getNomorSurat() { return nomorSurat; }
    public LocalDate getTanggal() { return tanggal; }
    public String getJenis() { return jenis; }
    public String getPengirimPenerima() { return pengirimPenerima; }
    public String getPerihal() { return perihal; }
    public String getIsi() { return isi; }
    
    // Setters
    public void setNomorSurat(String nomorSurat) { this.nomorSurat = nomorSurat; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public void setPengirimPenerima(String pengirimPenerima) { this.pengirimPenerima = pengirimPenerima; }
    public void setPerihal(String perihal) { this.perihal = perihal; }
    public void setIsi(String isi) { this.isi = isi; }
    
    public String getTanggalFormatted() {
        return tanggal.format(formatter);
    }
    
    public String getPreview() {
        String preview = isi.length() > 60 ? isi.substring(0, 57) + "..." : isi;
        return perihal + " - " + preview;
    }
    
    @Override
    public String toString() {
        return "[" + (jenis.equals("Masuk") ? "📥" : "📤") + "] " + 
               pengirimPenerima + " - " + perihal + " (" + getTanggalFormatted() + ")";
    }
}