<?php

use App\Http\Controllers\PembelianController;
use App\Http\Controllers\ProdukController;
use App\Http\Controllers\TipeController;
use App\Http\Controllers\UserController;
use Illuminate\Support\Facades\Route;


Route::prefix("api")->group(function(){
    Route::controller(UserController::class)->prefix("user")->group(function (){
        Route::get("", "details");
        Route::post("", "create");
        Route::post("login", "login");
        Route::post("beli", "beli");
        Route::get("riwayat", "riwayat");
        Route::post("top_up", "top_up");
    });

    Route::controller(TipeController::class)->prefix("tipe")->group(function (){
        Route::get("", "get");
        Route::post("", "add");
        Route::put("", "edit");
        Route::delete("", "delete");
    });

    Route::controller(ProdukController::class)->prefix("produk")->group(function (){
        Route::get("", "get");
        Route::post("", "add");
        Route::put("", "edit");
        Route::delete("", "delete");
    });
    Route::post("/admin/login", [UserController::class, "login_admin"]);
});
