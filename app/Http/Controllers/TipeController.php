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
    public function isTipeExist($id){
        return DB::table("tipe")->where("user_id", "=", $id)->exists();
    }
    public function get(){
        $data = DB::table("tipe")->get();
        return $this->res->success($data);
    }
    // admin_id, nama, deskripsi
    public function add(Request $req){
        if(!$this->res->isAdmin($req->admin_id))
        return $this->res->failed("Admin not found!");
        DB::table("tipe")->insertOrIgnore([
            "id" => null,
            "nama" => $req->nama,
            "deskripsi" => $req->deskripsi,
            "kolom" => $req->kolom  
        ]);
        return $this->res->success();
    }
    // admin_id, id, nama, deskripsi
    public function edit(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        if(!$this->isTipeExist($req->id)) return $this->res->failed("Type not found!");
        DB::table("tipe")->update([
            "nama" => $req->nama,
            "deskripsi" => $req->deskripsi,
            "kolom" => $req->kolom
        ]);
        return $this->res->success();
    }
    // admin_id, id_tipe
    public function delete(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        if(!$this->isTipeExist($req->id)) return $this->res->failed("Type not found!");
        
        if(DB::table("produk")->where("id_tipe", "=", $req->id_tipe))
        DB::table("tipe")->where("id", "=", $req->id_tipe)->delete();
        else return $this->res->failed("Type are used!");
        return $this->res->success();
    }
}
