=========================================
      CYAYOSELL - PREMIUM ITEM SELLER
=========================================
"Solusi cerdas untuk sistem ekonomi servermu. Jual item dengan mudah melalui GUI yang modern dan terintegrasi penuh dengan ExcellentEconomy."

=========================================
             QUICK TUTORIAL
=========================================

1. CARA SETTING HARGA ITEM:
   - Pergi ke folder `items/`. Kamu akan melihat file seperti `material_prices.yml`.
   - Tambahkan item baru di dalam list `items:`.
   - Format Vanilla:
     - type: vanilla
       material: DIAMOND
       price: 100
       allowed-menus: ["material"]
   - Format MMOItems:
     - type: mmoitems
       mmo-type: SWORD
       mmo-id: STAR_SLAYER
       price: 500
       quality-prices:
         perfect: 1000
         broken: 100
       allowed-menus: ["weapon"]
   
   > [!IMPORTANT]
   > Fitur `quality-prices` bersifat **OPSIONAL** dan khusus untuk item yang memiliki sistem Quality dari plugin **CyayoForge**. Jika item tidak memiliki quality atau tidak ingin diatur per-kualitas, cukup gunakan `price` saja.

2. CARA MEMBUAT MENU BARU:
   - Buat file `.yml` baru di folder `menus/` (contoh: `fish.yml`).
   - Atur `title`, `rows`, dan `open-permission`.
   - Pastikan `item-slots` sudah diatur agar player tahu di mana harus menaruh item.

3. CARA MENGGUNAKAN DI SERVER:
   - Gunakan command `/itemsell <menu> <player>` (biasanya dipasang di NPC Citizens atau CommandSign).
   - Player menaruh item yang ingin dijual ke dalam slot kosong di GUI.
   - Klik tombol **JUAL** (Emerald/Diamond) untuk memproses transaksi.
   - Uang akan otomatis masuk ke akun ExcellentEconomy player.

=========================================
           COMMANDS & PERMS
=========================================

USER COMMAND:
/itemsell <menu> <player>              > Membuka menu jual tertentu untuk player.

ADMIN COMMAND:
/itemsell reload                       : Reload konfigurasi, menu, dan harga item.

PERMISSIONS:
itemsell.use                           > Izin dasar untuk menekan tombol jual di GUI.
itemsell.open.material                 > Izin untuk mengakses menu kategori Material.
itemsell.open.weapon                   > Izin untuk mengakses menu kategori Weapon.
itemsell.admin                         > Akses penuh (Reload & Semua Menu).

=========================================
             PLACEHOLDERS
=========================================

Pesan dalam plugin mendukung placeholder berikut:
{menu}                                 > Nama menu yang sedang aktif.
{player}                               > Nama player yang bersangkutan.
{estimated}                            > Total harga sementara (tampil di lore tombol).
{total}                                > Total uang yang didapat setelah berhasil jual.
{amount}                               > Jumlah item yang berhasil terjual.
{currency}                             > Nama mata uang (contoh: Gins).

=========================================
            PLUGIN FEATURES
=========================================
✨ GUI Modern - Tampilan bersih dengan estimasi harga real-time.
💎 MMOItems Support - Deteksi otomatis tipe, ID, dan Quality item.
💰 Multi-Quality Pricing - Dukungan harga opsional untuk item dengan Quality dari CyayoForge.
🎵 Immersive Sounds - Feedback suara saat membuka menu, sukses jual, atau error.
🚀 High Performance - Load konfigurasi cepat dan ringan untuk server besar.
