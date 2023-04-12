<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;

class UserController extends Controller
{
    protected $res;
    public function __construct(ResponseController $rescon)
    {
        $this->res = $rescon;
    }
    // username, password, vpassword
    public function create(Request $req){
        if(DB::table("users")->where("username", "=", $req->username)->exists())
        return $this->res->failed("Username sudah digunakan!");
        if($req->password != $req->vpassword) return $this->res->failed("Verifikasi password salah!");

        $uuid = Str::uuid()->toString();
        DB::table("users")->insert([
            "user_id" => $uuid,
            "username" => $req->username,
            "password" => $req->password, 
            "saldo" => 0
        ]);
        return $this->res->success($uuid);
    }
    public function details(Request $req){
        $data = DB::table("users")->where("user_id", "=", $req->user_id)->first(["username", "saldo"]);
        return $this->res->success($data);
    }
    // username, password
    public function login(Request $req){
        if(DB::table("users")->where("username", "=", $req->username)->where("password", "=", $req->password)->exists()){
            return $this->res->success();
        } else {
            return $this->res->failed();
        }
    }
}
