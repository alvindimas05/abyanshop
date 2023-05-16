<?php

namespace App\Http\Controllers;

use App\Models\Produk;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ProdukController extends Controller
{
    protected $res;
    public function __construct(ResponseController $rescon)
    {
        $this->res = $rescon;
    }
    public function isProdukExist($id){
        return Produk::where("id", "=", $id)->exists();
    }
    public function get(Request $req){
        $data = new Produk;
        if($req->has("id_tipe")) $data = $data->where("id_tipe", "=", $req->id_tipe);
        $data = $data->get();
        return $this->res->success($data);
    }
    // admin_id, nama, harga, id_tipe
    public function add(Request $req){
        if(!$this->res->isAdmin($req->admin_id))
        return $this->res->failed("Produk not found!");
        Produk::insert([
            "id" => null,
            "nama" => $req->nama,
            "harga" => $req->harga,
            "id_tipe" => $req->id_tipe,
            "total_penjualan" => 0
        ]);
        return $this->res->success();
    }
    // admin_id, id, nama, harga, id_tipe
    public function edit(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        if(!$this->isProdukExist($req->id)) return $this->res->failed("Produk not found!");
        Produk::where("id", "=", $req->id)->update([
            "nama" => $req->nama,
            "harga" => $req->harga,
            "id_tipe" => $req->id_tipe,
            "total_penjualan" => 0
        ]);
        return $this->res->success();
    }
    // admin_id, id_produk
    public function delete(Request $req){
        if(!$this->res->isAdmin($req->admin_id)) return $this->res->failed();
        if(!$this->isProdukExist($req->id)) return $this->res->failed("Produk not found!");
        Produk::where("id", "=", $req->id_produk)->delete();
        return $this->res->success();
    }
}
