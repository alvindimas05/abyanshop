<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Produk extends Model
{
    use HasFactory;
    protected $table = "produk",
        $primaryKey = "id",
        $fillable = ["id", "nama", "harga", "id_tipe"];
    public $timestamps = false;
}
