<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class History extends Model
{
    use HasFactory;
    protected $table = "history",
        $primaryKey = "id",
        $fillable = ["id", "user_id", "id_produk"];
    public $timestamps = false;
}
