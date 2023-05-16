<?php

namespace App\Http\Controllers;

use App\Models\Produk;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class PembelianController extends Controller
{
    protected $res, $pro;
    public function __construct(ResponseController $rescon, ProdukController $procon)
    {
        $this->res = $rescon;
        $this->pro = $procon;
    }
    public function isUserExist(string $id){
        return User::where("user_id", "=", $id)->exists();
    }
    // user_id, id_produk
    public function beli(Request $req){
        if(!$this->isUserExist($req->user_id)) return $this->res->failed("User not found!");
        if(!$this->pro->isProdukExist($req->id_produk)) return $this->res->failed("Produk not found!");

        $user = User::where("user_id", "=", $req->user_id)->get(["saldo"])->first();
        $produk = Produk::where("id", "=", $req->id_produk)->get(["harga", "total_penjualan"])->first();

        if($user->saldo < $produk->harga) return $this->res->failed("Saldo tidak cukup!");
        User::where("user_id", "=", $req->user_id)->update([ "saldo" => $user->saldo - $produk->harga ]);
        Produk::where("id", "=", $req->id_produk)->update([ "total_penjualan" => $produk->total_penjualan ]);

        DB::table("history")->insert([
            "id" => null,
            "user_id" => $req->user_id,
            "id_produk" => $req->id_produk
        ]);
        return $this->res->success();
    }
    // user_id
    public function riwayat(Request $req){
        $data = DB::table("history", "h")->select(["p.nama", "p.harga", "t.nama as tipe"])
        ->where("user_id", "=", $req->user_id)->join("produk as p", "h.id_produk", "=", "p.id")
        ->join("tipe as t", "t.id", "=", "p.id_tipe")->get();
        return $this->res->success($data);
    }
    // user_id, jumlah
    public function top_up(Request $req){
        if(!$this->isUserExist($req->user_id)) return $this->res->success("User not found!");
        User::where("user_id", "=", $req->user_id)->update([ "saldo" => DB::raw("saldo + ".$req->jumlah) ]);
        return $this->res->success();
    }
}
