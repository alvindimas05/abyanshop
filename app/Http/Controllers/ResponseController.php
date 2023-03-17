<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\DB;

class ResponseController extends Controller
{
    public function failed(string $msg = null){
        return response()->json([ "status" => false, "message" => $msg ]);
    }
    public function success($data = null){
        return response()->json([ "status" => true, "data" => $data ]);
    }
    public function isAdmin(string $id){
        return DB::table("admins")->where("admin_id", "=", $id)->exists();
    }
}
