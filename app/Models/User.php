<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    use HasFactory, HasUuids;
    protected $table = "users",
        $primaryKey = "user_id",
        $hidden = ["password"],
        $fillable = ["user_id", "username", "password", "saldo"];
    public $timestamps = false;
}
