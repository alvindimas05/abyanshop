<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('history', function (Blueprint $table) {
            $table->foreign(['id_produk'], 'history_ibfk_2')->references(['id'])->on('produk')->onUpdate('CASCADE');
            $table->foreign(['user_id'], 'history_ibfk_1')->references(['user_id'])->on('users')->onUpdate('CASCADE');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::table('history', function (Blueprint $table) {
            $table->dropForeign('history_ibfk_2');
            $table->dropForeign('history_ibfk_1');
        });
    }
};
