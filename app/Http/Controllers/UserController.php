<?php

namespace App\Http\Controllers;

use App\Models\Admin;
use App\Models\User;
use Illuminate\Http\Request;
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
        if(User::where("username", "=", $req->username)->exists())
        return $this->res->failed("Username sudah digunakan!");
        if($req->password != $req->vpassword) return $this->res->failed("Verifikasi password salah!");

        $uuid = Str::uuid()->toString();
        User::insert([
            "user_id" => $uuid,
            "username" => $req->username,
            "password" => $req->password,
            "saldo" => 0
        ]);
        return $this->res->success($uuid);
    }
    public function details(Request $req){
        $data = User::where("user_id", "=", $req->user_id)->first(["username", "saldo"]);
        return $this->res->success($data);
    }
    // username, password
    public function login(Request $req){
        $user = User::where("username", "=", $req->username)->where("password", "=", $req->password);
        if($user->exists()){
            return $this->res->success($user->get(["user_id"])->first()->user_id);
        } else {
            return $this->res->failed();
        }
    }
    public function login_admin(Request $req){
        $admin = Admin::where("username", "=", $req->username)->where("password", "=", $req->password);
        if($admin->exists()){
            return $this->res->success((string) $admin->get(["admin_id"])->first()->admin_id);
        } else {
            return $this->res->failed();
        }
    }
}
