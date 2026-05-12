# 📦 CyayoSell - Premium Item Seller

"Solusi cerdas untuk sistem ekonomi servermu. Jual item dengan mudah melalui GUI yang modern dan terintegrasi penuh dengan ExcellentEconomy."

```text
=========================================
             QUICK TUTORIAL
=========================================
```

### 1. Cara Setting Harga Item
Buka folder `items/` dan edit file `.yml`. Tambahkan item dengan format berikut:

**Format Vanilla:**
```yaml
- type: vanilla
  material: DIAMOND
  price: 100
  allowed-menus: ["material"]
```

**Format MMOItems:**
```yaml
- type: mmoitems
  mmo-type: SWORD
  mmo-id: STAR_SLAYER
  price: 500
  quality-prices: # Opsional (Khusus CyayoForge)
    perfect: 1000
    broken: 100
  allowed-menus: ["weapon"]
```

### 2. Cara Membuat Menu Baru
- Buat file `.yml` baru di folder `menus/`.
- Atur `open-permission` sesuai keinginanmu.
- Gunakan `item-slots` untuk menentukan area input player.

### 3. Cara Penggunaan
- Jalankan `/itemsell <menu> <player>` via NPC/Console/Command.
- Player menaruh item -> Klik tombol **JUAL**.
- Notifikasi rincian harga akan muncul di chat & uang masuk otomatis.

```text
=========================================
           COMMANDS & PERMS
=========================================

USER COMMAND:
/itemsell <menu> <player>    > Buka menu jual untuk player.

ADMIN COMMAND:
/itemsell reload             : Reload config, menu, & harga.

PERMISSIONS:
itemsell.use                 > Izin dasar menekan tombol jual.
itemsell.admin               > Akses master (Semua Menu & Reload).
itemsell.open.material       > Izin akses menu Material (Bawaan).
itemsell.open.weapon         > Izin akses menu Weapon (Bawaan).

[!] CUSTOM MENU PERMISSION:
Kamu bisa menentukan sendiri nama permission untuk setiap menu baru.

CARA MENGATUR:
Edit file menu di folder 'menus/*.yml', lalu ubah bagian 
'open-permission' sesuai keinginan (Contoh: 'itemsell.open.vip').

FUNGSINYA:
Membatasi akses menu tertentu agar hanya bisa dibuka oleh player
dengan rank atau izin khusus (misal: menu khusus Donatur).
```

```text
=========================================
             PLACEHOLDERS
=========================================

{menu}      > Nama menu aktif.
{player}    > Nama player.
{estimated} > Estimasi harga (tampil di lore).
{total}     > Total uang yang didapat.
{amount}    > Jumlah item terjual.
{currency}  > Nama mata uang (Gins).
```

### 🚀 Advanced Features
- ✨ **Real-time Estimation** - Lore tombol jual berubah otomatis saat item dimasukkan.
- 💎 **MMOItems Support** - Sinkronisasi otomatis dengan Type, ID, dan Quality.
- 💰 **ExcellentEconomy Integration** - Mendukung berbagai mata uang ExcellentEconomy.
- 🎵 **Custom Sounds** - Feedback suara saat Open, Sell Success, Fail, dan Error.
- 🎨 **Quality Strings** - Menampilkan nama kualitas dengan gradient warna di chat.
- 📝 **Detailed Chat Summary** - Rincian penjualan yang rapi dan mudah dibaca.
