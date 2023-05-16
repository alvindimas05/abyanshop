<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Admin extends Model
{
    use HasFactory, HasUuids;
    protected $table = "admins",
        $primaryKey = "admin_id",
        $fillable = ["admin_id", "username", "password"];
    public $timestamps = false;
}
