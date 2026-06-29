package suratapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;

public class SuratController {
    private List<Surat> semuaSurat;
    private DefaultListModel<Surat> listModel;
    
    private String filterJenis = "Semua";
    private String sortMode = "Terbaru";
    private String searchKeyword = "";
    
    public SuratController() {
        semuaSurat = new ArrayList<>();
        listModel = new DefaultListModel<>();
        loadDataDummy();
        applyFilterSortSearch();
    }
    
    private void loadDataDummy() {
        semuaSurat.add(new Surat("001/SM/VI/26", LocalDate.of(2026, 2, 11), "Masuk", 
            "Dosen TYUN", "UNDANGAN RAPAT", "Undangan kepada yang bersangkutan untuk menghadiri rapat koordinasi di ruang sidang."));
        semuaSurat.add(new Surat("002/SK/VI/26", LocalDate.of(2026, 1, 10), "Keluar", 
            "Prodi Tubatu Wari Wari", "TUGAS", "Ditugaskan membuat project kelompok 5 anggota, membangun aplikasi TXT MARKETING. Deadline satu bulan."));
        semuaSurat.add(new Surat("003/SM/VI/26", LocalDate.of(2026, 6, 5), "Masuk", 
            "WEVERSE", "PESANAN", "Pesanan dengan nomor resi TXT123456789 telah dikonfirmasi."));
        semuaSurat.add(new Surat("004/SK/VI/26", LocalDate.of(2026, 5, 2), "Keluar", 
            "Leader Soobin", "PEMBERITAHUAN", "Pemberitahuan perubahan jadwal latihan."));
        semuaSurat.add(new Surat("005/SM/VI/26", LocalDate.of(2026, 4, 1), "Masuk", 
            "Hyuka's School", "JADWAL", "Masuknya semester baru dimulai tanggal 1 Agustus 2025."));
        semuaSurat.add(new Surat("006/SK/VI/26", LocalDate.of(2026, 3, 28), "Keluar", 
            "Rektorat Yeonjun", "UNDANGAN WISUDA", "Undangan upacara wisuda periode ke-67."));
        semuaSurat.add(new Surat("007/SM/VI/26", LocalDate.of(2026, 2, 25), "Masuk", 
            "Perusahaan GYU", "LOWONGAN MAGANG", "Informasi lowongan magang untuk mahasiswa semester akhir."));
        semuaSurat.add(new Surat("008/SM/VI/26", LocalDate.of(2025, 8, 22), "Keluar", 
            "Organisasi MOA", "PENDAFTARAN", "Informasi pendaftaran. Menerima berbagai kalangan, latar belakang, as long as you love TOMORROW X TOGETHER, then you will be accepted."));
    }
    
    public DefaultListModel<Surat> getListModel() {
        return listModel;
    }
    
    public void setFilterJenis(String filterJenis) {
        this.filterJenis = filterJenis;
        applyFilterSortSearch();
    }
    
    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
        applyFilterSortSearch();
    }
    
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword.toLowerCase();
        applyFilterSortSearch();
    }
    
    private void applyFilterSortSearch() {
        List<Surat> filtered = new ArrayList<>(semuaSurat);
        
        // Filter jenis surat
        if (!filterJenis.equals("Semua")) {
            filtered = filtered.stream()
                .filter(s -> s.getJenis().equals(filterJenis))
                .collect(Collectors.toList());
        }
        
        // Filter search
        if (!searchKeyword.isEmpty()) {
            filtered = filtered.stream()
                .filter(s -> s.getPerihal().toLowerCase().contains(searchKeyword) ||
                             s.getPengirimPenerima().toLowerCase().contains(searchKeyword) ||
                             s.getNomorSurat().toLowerCase().contains(searchKeyword))
                .collect(Collectors.toList());
        }
        
        // Sorting
        if (sortMode.equals("Terbaru")) {
            filtered.sort(Comparator.comparing(Surat::getTanggal).reversed());
        } else {
            filtered.sort(Comparator.comparing(Surat::getTanggal));
        }
        
        // Update list model
        listModel.clear();
        for (Surat s : filtered) {
            listModel.addElement(s);
        }
    }
    
    public void tambahSurat(Surat surat) {
        semuaSurat.add(surat);
        applyFilterSortSearch();
    }
    
    public void editSurat(Surat suratLama, Surat suratBaru) {
        int index = semuaSurat.indexOf(suratLama);
        if (index != -1) {
            semuaSurat.set(index, suratBaru);
            applyFilterSortSearch();
        }
    }
    
    public void hapusSurat(Surat surat) {
        semuaSurat.remove(surat);
        applyFilterSortSearch();
    }
    
    public String getFilterJenis() { return filterJenis; }
    public String getSortMode() { return sortMode; }
    public int getJumlahTampil() { return listModel.size(); }
}