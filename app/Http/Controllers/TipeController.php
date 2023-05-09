<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;

class TipeController extends Controller
{
    protected $res;
    public function __construct(ResponseController $rescon)
    {
        $this->res = $rescon;
    }
    public function isTipeExist($id){
        return DB::table("tipe")->where("id", "=", $id)->exists();
    }
    public function get(Request $req){
        $data = DB::table("tipe");
        if($req->has("id_tipe")) $data = $data->where("id", "=", $req->id_tipe)->get()->first();
        else $data = $data->get();
        return $this->res->success($data);
    }
    // admin_id, nama, deskripsi
    public function add(Request $req){
        $req->validate(["image" => "mimes:jpg,jpeg,png|max:3000"]);

        if(!$this->res->isAdmin($req->admin_id))
        return $this->res->failed("Admin not found!");
        $id = DB::table("tipe")->insertGetId([
            "id" => null,
            "nama" => $req->nama,
            "deskripsi" => $req->deskripsi,
            "kolom" => $req->kolom  
        ]);
        $req->file("image")->move(public_path()."/images", $id);
        return $this->res->success();
    }
    // admin_id, id, nama, deskripsi, image
    public function edit(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        if(!$this->isTipeExist($req->id)) return $this->res->failed("Type not found!");
        
        if($req->hasFile("image")){
            $req->validate(["image" => "mimes:jpg,jpeg,png|max:3000"]);
            $req->file("image")->move(public_path()."/images", $req->id);
        }
        DB::table("tipe")->where("id", "=", $req->id)->update([
            "nama" => $req->nama,
            "deskripsi" => $req->deskripsi,
            "kolom" => $req->kolom
        ]);
        return $this->res->success();
    }
    // admin_id, id_tipe
    public function delete(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        if(!$this->isTipeExist($req->id_tipe)) return $this->res->failed("Type not found!");
        
        if(!DB::table("produk")->where("id_tipe", "=", $req->id_tipe)->exists())
        DB::table("tipe")->where("id", "=", $req->id_tipe)->delete();
        else return $this->res->failed("Type are used!");

        unlink(public_path()."/images/".$req->id_tipe);
        return $this->res->success();
    }
}
