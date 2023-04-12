<?php

use App\Http\Controllers\PembelianController;
use App\Http\Controllers\ProdukController;
use App\Http\Controllers\TipeController;
use App\Http\Controllers\UserController;
use Illuminate\Support\Facades\Route;

$user = UserController::class;
$tipe = TipeController::class;
$produk = ProdukController::class;
$beli = PembelianController::class;

Route::get("/api/user", [$user, "details"]);
Route::post("/api/user", [$user, "create"]);
Route::post("/api/user/login", [$user, "login"]);
Route::post("/api/user/beli", [$beli, "beli"]);
Route::get("/api/user/riwayat", [$beli, "riwayat"]);
Route::post("/api/user/top_up", [$beli, "top_up"]);

Route::get("/api/tipe", [$tipe, "get"]);
Route::post("/api/tipe", [$tipe, "add"]);
Route::put("/api/tipe", [$tipe, "edit"]);
Route::delete("/api/tipe", [$tipe, "delete"]);

Route::get("/api/produk", [$produk, "get"]);
Route::post("/api/produk", [$produk, "add"]);
Route::put("/api/produk", [$produk, "edit"]);
Route::delete("/api/produk", [$produk, "delete"]);