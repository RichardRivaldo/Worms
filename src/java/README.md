# Strategi Algoritma: Algoritma Greedy
**Tugas Besar 1 IF2211 Strategi Algoritma** 

**Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan *Worms***

## Spesifikasi dan Permasalahan
* Memanfaatkan Algoritma Greedy dalam Permainan *Worms Entelect Challenge 2019* untuk memenangkan permainan.
* Algoritma dibuat dengan melanjutkan dan melengkapi implementasi Bot permainan dalam Bahasa Java dengan menggunakan Intellij IDEA.
* Worms adalah permainan Battle Royale yang dimainkan pada sebuah map 33x33 oleh 3 buah worms dalam satu tim. Setiap worm mendapatkan gilirannya masing-masing dan hanya ada satu worm yang bisa diberikan Command tertentu pada setiap round.
* Ada tiga tipe worm yang memiliki karakteristik masing-masing, yaitu Commando, Agent, dan Technologist.
* Peta akan semakin mengecil ketika mencapai ronde 100, ditandai dengan munculnya lava dari pinggiran peta hingga menyisakan bagian tertentu pada tengah peta.

## Implementasi Strategi-Strategi Greedy
* Strategi Following, yaitu strategi yang digunakan untuk mengikuti Commando Worms dengan tujuan meminimumkan bahaya yang mungkin didapat jika worm kita bergerak sendiri-sendiri.
* Strategi Power Up, yaitu strategi yang digunakan sebagai navigasi supaya worm pemain bisa bergerak menuju power up yang paling dekat terhadap dirinya dengan harapan worm ini bisa mendapatkan power up sehingga memaksimalkan poin yang didapat serta bisa merestore health worm kita.
* Strategi Self Defense dengan Select Command, yaitu strategi yang digunakan supaya worm-worm yang sendirian bisa memiliki mekanisme Self-Defense ketika ada worm musuh yang terdeteksi oleh worm kita sehingga bisa meminimasi damage dari musuh serta memaksimasi kesempatan attack untuk worm tersebut.
* Strategi Attack, yaitu strategi yang digunakan untuk mengontrol serangan yang dilakukan oleh setiap worm. Serangan akan dilakukan dengan menggunakan Special Attack terlebih dahulu untuk memaksimalkan damage dari worm dan poin yang didapat tim. Untuk Snowball, di setiap rentang penggunaan Snowball juga dimungkinkan untuk melakukan serangan biasa sampai worm musuh bisa bergerak lagi. Hal ini digunakan untuk memaksimalkan durasi Frozen yang dimiliki oleh musuh yang terdampak Snowball sehingga damage output yang didapat lebih maksimal.
* Strategi One v One, yaitu strategi yang digunakan ketika worm kita memiliki health yang lebih besar dibandingkan dengan worm tim lawan dalam kondisi 1v1. Dalam hal ini, worm kita akan berusaha untuk mematikan worm lawan sehingga kita bisa memenangkan permainan.
* Strategi Lonewolf, yaitu strategi yang digunakan ketika worm kita hanya tersisa satu, sedangkan lawan masih memiliki worm yang banyak atau memiliki health yang lebih tinggi daripada worm kita. Worm kita akan berusaha untuk kabur dan menghindar dari serangan worm lawan sampai jumlah ronde maksimum tercapai, dengan harapan untuk mendapatkan poin yang maksimum dari pergerakan dan digging yang mungkin dilakukan sehingga memungkinkan kita untuk tetap menang.
* Dalam kasus perburuan yang dilakukan oleh musuh kepada worm kita, worm kita bisa kabur sekaligus berusaha mematikan musuh tersebut apabila health yang dimiliki musuh ini sudah tinggal sedikit sehingga memaksimalkan poin yang didapat dari membunuh worm lawan.

## Requirement
* Java (Minimal Java 8), dapat diunduh di https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html.
* Intellij IDEA, dapat diunduh di https://www.jetbrains.com/idea/.
* NodeJS, dapat diunduh di https://nodejs.org/en/download/.
* Official Worms Game Engine Entelect, dapat diunduh di https://github.com/EntelectChallenge/2019-Worms/releases/tag/2019.3.2.
* (Optional) Worms 2019 Entelect Challenge Visualizer, dapat diunduh di https://github.com/dlweatherhead/entelect-challenge-2019-visualiser/releases/tag/v1.0f1.

## Setup
* Pastikan semua requirement di atas sudah terinstall pada perangkat keras yang akan digunakan.
* Perhatikan bahwa Game Engine yang diunduh dari link di atas merupakan `starter pack` yang digunakan oleh pemain untuk memulai membuat bot.
* Struktur folder starter pack tersebut dapat dilihat di https://github.com/EntelectChallenge/2019-Worms.
* Lakukan pengimplementasian kode program menggunakan Intellij IDEA (dapat dilakukan dengan menjalankan file `pom.xml`).
* Setelah diimplementasikan, lakukan instalasi program dengan menggunakan `Maven Toolbox` pada bagian `Lifecycle` yang terletak di bagian kanan Intellij IDEA.
* Instalasi ini menghasilkan sebuah folder bernama target yang akan berisi sebuah file bernama `java-sample-bot-jar-with-dependencies.jar`.
* Pindahkan file ini ke dalam folder `starter-pack`. Jika sudah ada, file yang lama bisa digantikan dengan file yang baru ini.
* Pastikan konfigurasi program yang ada di `game-runner-config.json` sudah benar, meliputi direktori bot yang digunakan.
* Jika menggunakan file yang terdapat dalam repositori ini, maka yang perlu dilakukan adalah menggantikan file jar dengan file yang ada di folder `bin`.
* Selain itu, jangan lupa untuk tetap mengubah source code program dengan mengganti folder `starter-bots` dengan folder `src` yang ada di repositori ini.
* Visualizer bisa diletakkan di direktori manapun dan tidak mengganggu program bot yang dibuat.

## Run Permainan
* Setelah semua konfigurasi dilakukan dengan benar, maka kita bisa langsung menjalankan `run.bat` atau `make run` di Linux yang ada di starter-pack.
* Akan ditampilkan permainan dalam Terminal yang digunakan dalam perangkat lunak. Arsip dari permainan ini akan dimasukkan ke dalam folder `match-logs`.
* Untuk menampilkan permainan di dalam visualizer, pindahkan folder permainan (biasanya dengan nama timestamp waktu dilakukan run) ke dalam folder `Matches` di Visualizernya.
* Setelah itu, jalankan `start-visualizer` yang akan menampilkan program visualizer.
* Pilih dan klik arsip permainan yang ingin divisualisasikan.
* Gunakan `Alt + Enter` jika visualisasi permainan terpotong.
* Petunjuk-petunjuk konfigurasi permainan sudah ada sebagai `README` di dalam folder-folder yang ada di dalam `starter-pack` untuk dieksplorasi lebih lanjut.

## Sources
* Official Entelect Challenge 2019 Worms Starter Pack: https://github.com/EntelectChallenge/2019-Worms.
* Entelect Forum: https://forum.entelect.co.za/.
* Entelect 2019 Worms Visualizer: https://github.com/dlweatherhead/entelect-challenge-2019-visualiser/releases/tag/v1.0f1.

## Author
* Faris Aziz / 13519065
* Maximillian Lukman / 13519153
* Richard Rivaldo / 13519185