package ani.saikou.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou.R
import ani.saikou.databinding.ActivityFaqBinding
import ani.saikou.initActivity

class FAQActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqBinding

    private val faqs = listOf(
        Triple(R.drawable.ic_round_help_24,"Apa itu Anime Stream?\nMengapa Anda harus menggunakan Anime Stream?", "Anime Stream dibuat berdasarkan keanggunan yang sederhana namun canggih. Ini adalah satu-satunya klien Anilist, yang juga memungkinkan Anda streaming-unduh Anime & Manga. Kami berencana untuk segera menambahkan dukungan MyAnimeList.\nYah, kami ingin mengatakan ini adalah aplikasi sumber terbuka terbaik untuk anime dan manga di Android, tapi hei, Cobalah sendiri & nilai! "),
        Triple(R.drawable.ic_round_auto_awesome_24,"Apa saja fitur Anime Stream?","Beberapa fitur Anime Stream yang dapat disebutkan adalah\n\n- Cara mudah dan fungsional untuk keduanya, menonton anime dan membaca manga, Bebas Iklan.\n- Aplikasi sumber yang sepenuhnya terbuka dengan UI & Animasi yang bagus\n- Pengikisan paling efisien untuk anime dan manga dari berbagai sumber. (Peringatan Spoiler: Tidak ada tampilan web yang dirugikan)\n- Sinkronkan anime dan manga secara real-time dengan AniList. Mengkategorikan anime dan manga dengan mudah berdasarkan status Anda saat ini. (Didukung oleh AniList)\n- Temukan semua acara menggunakan daftar semua anime yang sedang tren, populer, dan sedang berlangsung secara menyeluruh dan sering diperbarui berdasarkan skor.\n- Lihat detail ekstensif tentang acara anime, film, dan judul manga. Ini juga menampilkan kemampuan untuk menghitung mundur ke episode berikutnya dari anime yang ditayangkan. (Didukung oleh AniList & MyAnimeList)"),
        Triple(R.drawable.ic_round_dns_24,"Apakah Anime Stream tersedia untuk PC?","Saat ini tidak ada (untuk Windows dan Linux). Belum ada perkiraan kapan akan tersedia. Tetapi Anda dapat mengunduh emulator Android apa pun dan menjalankan Anime Stream di atasnya. Untuk pengguna Windows 11, mereka dapat menggunakan Subsistem Windows untuk Android (a.k.a WSA) untuk menjalankan Anime Stream di Windows."),
        Triple(R.drawable.ic_baseline_screen_lock_portrait_24,"Apakah Anime Stream tersedia untuk iOS?","Anime Stream untuk iOS kini sedang dikembangkan"),
        Triple(R.drawable.ic_anilist,"Mengapa statistik saya tidak diperbarui?","Ini karena pembaruan setiap 48 jam secara otomatis (oleh anilist), Jika Anda benar-benar perlu memperbarui statistik Anda, Anda dapat memperbarui statistik Anda secara paksa setelah pergi ke ini [link](https://anilist.co/settings/lists)"),
        Triple(R.drawable.ic_round_movie_filter_24,"Bagaimana cara mengunduh Episode?","Unduh 1DM atau ADM dari Google Play Store.\n• Masuk ke aplikasi, berikan akses penyimpanan dan atur preferensi Anda (kecepatan pengunduhan, jalur pengunduhan dll (opsional))\n• Sekarang buka `Anime Stream > Pengaturan > Umum > Pengelola Unduhan` dan pilih pengelola unduhan yang baru saja Anda atur.\n• Buka episode yang Anda inginkan dan tekan ikon unduh dari server mana pun. Mungkin ada munculan untuk mengatur preferensi Anda lagi, cukup tekan \"Unduhan\" dan itu akan disimpan di jalur yang diarahkan.\n\nCatatan: Pengunduhan langsung juga dimungkinkan tanpa pengelola, tetapi tidak disarankan."),
        Triple(R.drawable.ic_round_menu_book_24,"Bagaimana cara mengunduh Chapter Manga?","Belum memungkinkan untuk mengunduh Chapter di Anime Stream tetapi fitur ini akan segera diterapkan."),
        Triple(R.drawable.ic_round_lock_open_24,"Bagaimana cara mengaktifkan konten NSFW?","Anda dapat mengaktifkan konten nsfw dengan mengaktifkan konten 18+ dari ini [link](https://anilist.co/settings/media)"),
        Triple(R.drawable.ic_round_smart_button_24,"Bagaimana cara mengimpor daftar MAL/Kitsu saya ke Anilist?","Inilah cara Anda melakukannya,\n\nEkspor:\nPergi ke Sini [link](https://malscraper.azurewebsites.net/). Kemudian berikan nama pengguna Kitsu/MAL Anda dan unduh daftar anime dan manga. (mereka akan dalam format xml)\nCatatan: Anda harus menulis nama pengguna pelacak yang Anda pilih\n\nImpor:\nSetelah mengekspor daftar anime dan manga Anda dari kitsu/MAL, sekarang lanjutkan [here](https://anilist.co/settings/import)\nPilih/jatuhkan file xml anime pada kotak di atas.\nPilih/jatuhkan file manga xml pada kotak di bawah ini."),
        Triple(R.drawable.ic_round_smart_button_24,"Bagaimana cara mengimpor daftar Anilist/Kitsu saya ke MAL?","Inilah cara Anda melakukannya,\n\nEkspor:\nPergi ke Sini [link](https://malscraper.azurewebsites.net/). Kemudian berikan nama pengguna Anilist/ID Kitsu Anda di \"Username/Kitsu User ID\" kotak. Kemudian pilih jenis daftar dan Aktifkan 'update_on_import'. Kemudian unduh file tersebut, itu akan dalam format .xml. Pastikan untuk mengunduh daftar Anime dan Manga.\n\nImpor:\nUntuk mengimpornya di akun MAL Anda, buka ini [link](https://myanimelist.net/import.php) dan pilih \"MyAnimeList Import\" sebagai jenis impor. Sekarang tekan \"Pilih File\" dan pilih file xml daftar anime/manga yang diunduh. Lalu tekan\"Impor Data\". Selamat, Anda baru saja mengimpor daftar yang dipilih ke akun MAL Anda."),
        Triple(R.drawable.ic_round_info_24,"Mengapa saya tidak dapat menemukan judul Anime/Manga tertentu?","Katakanlah Anda sedang mencari Castlevania di Anime Stream. Tapi Anilist tidak memilikinya, jadi Anime Stream juga tidak.\nSolusi untuk masalah di atas adalah sebagai berikut-\n1) Kunjungi anime mana pun yang tidak ada dalam daftar Anda.\n2) Pergi ke bagian menonton.\n3) Pilih sumber apa pun dan tekan judul yang salah.\n4) Sekarang cari Castlevania (Anime yang Anda cari) dan pilih.\n5) ENJOY!\n\nJika Anda tidak dapat menemukan anime bahkan melalui langkah-langkah ini, maka itu adalah nasib buruk bagi Anda. Bahkan sumber itu tidak memilikinya. Coba sumber lain."),
        Triple(R.drawable.ic_round_help_24,"Bagaimana cara memperbaiki sumber yang memilih judul yang benar-benar salah?","Jika aplikasi Anda memilih judul yang salah, Anda dapat memperbaikinya dengan menekan\"Salah Judul?\" di atas tata letak dan memilih judul yang benar"),
        Triple(R.drawable.ic_round_art_track_24,"Bagaimana cara membaca Manga berwarna?","Apakah Anda mencari manga berwarna? Maaf untuk menyampaikannya kepada Anda tetapi sejumlah kecil manga memiliki versi berwarna. Yang memiliki versi berwarna juga tersedia di Anime Stream. Katakanlah Anda ingin membaca versi berwarna dari Chainsaw Man. Kemudian ikuti langkah-langkah di bawah ini ↓\n\n1) Pergi ke Chainsaw Man\n2) Tekan di 'Baca'\n3) Pilih sumber yang berfungsi\n4) Tekan di 'Judul Salah'\n5) Pilih Chainsaw Man versi berwarna\n6) Enjoy\n\nCatatan: Banyak sumber tidak menyediakan versi berwarna meskipun tersedia di suatu tempat di internet. Jadi coba sumber lain. Jika tidak ada sumber yang memilikinya, maka versi berwarna dari manga yang Anda inginkan tidak ada. Jika Anda dapat menemukannya di situs manga mana pun di internet, Anda dapat menyarankan situs tersebut melalui Discord Server."),
        Triple(R.drawable.ic_round_video_settings_24,"Handshake gagal? Mengapa tidak ada stempel waktu yang tidak dimuat?","Anda dapat memperbaiki masalah ini dengan mengaktifkan `Proxy` dari \n`pengaturan> anime> pengaturan player> timestamp> proxy`\nJika timestamps masih tidak dimuat tetapi handshake gagal muncul sudah diperbaiki, maka episode yang Anda tonton belum memiliki timestamps untuk itu."),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity(this)

        binding.devsTitle2.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.devsRecyclerView.adapter = FAQAdapter(faqs, supportFragmentManager)
        binding.devsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}
