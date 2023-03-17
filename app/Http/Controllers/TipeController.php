<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TipeController extends Controller
{
    protected $res;
    public function __construct(ResponseController $rescon)
    {
        $this->res = $rescon;
    }
    public function get(){
        $data = DB::table("tipe")->get();
        return $this->res->success($data);
    }
    public function add(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        DB::table("tipe")->insertOrIgnore([
            "id" => null,
            "nama" => $req->nama,
            "deskripsi" => $req->deskripsi
        ]);
        return $this->res->success();
    }
    public function edit(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        DB::table("tipe")->update([
            "id" => $req->id,
            "nama" => $req->nama,
            "deskripsi" => $req->deskripsi
        ]);
        return $this->res->success();
    }
    public function delete(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        DB::table("tipe")->where("id", "=", $req->id)->delete();
        return $this->res->success();
    }
}
