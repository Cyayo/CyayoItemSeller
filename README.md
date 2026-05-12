# 📦 CyayoSell - Premium Item Seller

"Solusi cerdas untuk sistem ekonomi servermu. Jual item dengan mudah melalui GUI yang modern dan terintegrasi penuh dengan ExcellentEconomy."

```text
=========================================
             QUICK TUTORIAL
=========================================
```

### 1. Cara Setting Harga Item
Buka folder `items/` dan edit file `.yml` yang ada. Kamu bisa menambahkan item dengan format berikut:

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
  quality-prices:
    perfect: 1000
    broken: 100
  allowed-menus: ["weapon"]
```

> [!IMPORTANT]
> Fitur `quality-prices` bersifat **OPSIONAL** dan khusus untuk item yang memiliki sistem Quality dari plugin **CyayoForge**. Jika item tidak memiliki quality, cukup gunakan `price` saja.

### 2. Cara Membuat Menu Baru
- Buat file `.yml` baru di folder `menus/` (contoh: `fish.yml`).
- Atur `title`, `rows`, dan `open-permission`.
- Pastikan `item-slots` sudah diatur sebagai tempat player menaruh item.

### 3. Cara Penggunaan
- Gunakan command `/itemsell <menu> <player>` (bisa dipasang di NPC atau CommandSign).
- Player menaruh item ke slot kosong -> Klik tombol **JUAL**.
- Uang otomatis masuk ke ExcellentEconomy.

```text
=========================================
           COMMANDS & PERMS
=========================================

USER COMMAND:
/itemsell <menu> <player>    > Buka menu jual untuk player.

ADMIN COMMAND:
/itemsell reload             : Reload config, menu, & harga.

PERMISSIONS:
itemsell.use                 > Izin menggunakan tombol jual.
itemsell.open.material       > Izin akses menu Material.
itemsell.open.weapon         > Izin akses menu Weapon.
itemsell.admin               > Akses penuh (Reload & Semua Menu).
```

```text
=========================================
             PLACEHOLDERS
=========================================

{menu}      > Nama menu aktif.
{player}    > Nama player.
{estimated} > Estimasi harga di lore.
{total}     > Total uang yang didapat.
{amount}    > Jumlah item terjual.
{currency}  > Nama mata uang (Gins).
```

### 🚀 Plugin Features
- ✨ **GUI Modern** - Tampilan bersih & real-time estimation.
- 💎 **MMOItems Support** - Integrasi otomatis Type & ID.
- 💰 **Quality Pricing** - Dukungan opsional untuk CyayoForge.
- 🎵 **Immersive Sounds** - Feedback suara di setiap aksi.
- ⚡ **High Performance** - Ringan dan optimal.
