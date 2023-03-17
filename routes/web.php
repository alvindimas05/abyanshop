<?php

use App\Http\Controllers\TipeController;
use App\Http\Controllers\UserController;
use Illuminate\Support\Facades\Route;

$user = UserController::class;
$tipe = TipeController::class;

Route::post("/api/user", [$user, "create"]);
Route::post("/api/user/login", [$user, "login"]);

Route::get("/api/tipe", [$tipe, "get"]);
Route::post("/api/tipe", [$tipe, "add"]);
Route::put("/api/tipe", [$tipe, "edit"]);
Route::delete("/api/tipe", [$tipe, "delete"]);